package com.common.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;

import com.csdk.api.audio.AudioManager;
import com.csdk.api.bean.Group;
import com.csdk.api.bean.Menu;
import com.csdk.api.bean.Session;
import com.csdk.api.common.Api;
import com.csdk.api.config.Config;
import com.csdk.api.core.Debug;
import com.csdk.api.core.GroupType;
import com.csdk.api.ui.Model;
import com.csdk.api.ui.ModelBinder;
import com.csdk.api.ui.OnViewClick;
import com.csdk.server.Configure;
import com.csdk.ui.R;
import com.csdk.ui.adapter.OutlineMessageAdapter;
import com.csdk.ui.binding.Click;
import com.csdk.ui.databinding.CsdkHomeFriendsModelBinding;
import com.csdk.ui.databinding.CsdkHomeGroupModelBinding;
import com.csdk.ui.databinding.CsdkHomeItemMenuBinding;
import com.csdk.ui.databinding.CsdkHomeSystemModelBinding;
import com.csdk.ui.model.HomeContentModel;
import com.csdk.ui.model.HomeFriendsModel;
import com.csdk.ui.model.HomeGroupModel;
import com.csdk.ui.model.HomeSessionModel;
import com.csdk.ui.model.HomeSystemModel;
import com.csdk.ui.ue4.NativityActivityTouchHocker;
import com.csdk.ui.ue4.SoftInputCloser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CommonHomeModel extends Model implements OnViewClick {
    private final OutlineMessageAdapter mOutlineAdapter = new OutlineMessageAdapter();
    private final ObservableField<Boolean> mShowOutline=new ObservableField<>(true);
    private final ObservableField<Boolean> mInputEmoji = new ObservableField<>(false);
    private final ObservableField<Menu> mShowingChannel = new ObservableField<>();
    private final ObservableField<Boolean> mVoiceMessageSendEnable=new ObservableField<>(true);
    private final ObservableField<Boolean> mInputEnable=new ObservableField<>(false);
    private final ObservableField<Menu> mSelectMenu=new ObservableField<>();
    private final ObservableField<Session> mCurrentSession=new ObservableField<>();
    private final ObservableField<Boolean> mRoomAudioEnable=new ObservableField<>(true);
    private final NativityActivityTouchHocker mNativityActivityTouchHocker=new NativityActivityTouchHocker();
    private final ObservableField<Boolean> mOutChatEnable=new ObservableField<>(true);//Default must  as true
    private final View.OnTouchListener mEmptyViewTouchListener=(View v, MotionEvent event)-> {
        if (null!=event&&event.getAction()==MotionEvent.ACTION_DOWN){
            View root=null!=v?v.getRootView():null;
            if (null!=root){
                new SoftInputCloser().close(root,"While home empty view touch down.");
            }
            showOutline(true, "After home empty view touch down.");
        }
        return true;
    };

    public CommonHomeModel(Api api) {
        super(api);
    }

    @Override
    protected void onRootAttached(String debug) {
        super.onRootAttached(debug);
        applyHomeMenus(getMenus(null, -1), "While model root attached.");
        Boolean outline=mShowOutline.get();
        showOutline(null!=outline&&outline,"While instance.");//Must reset set outline here
    }

    private boolean showOutline(boolean show,String debug){
        mShowOutline.set(show);
        enableTouchHocker(!show,debug);
        if (show){//Stop playing audio while
            stopPlayingAudio("While leave home.");
        }
        return true;
    }

    public boolean enableTouchHocker(boolean enable,String debug){
        return mNativityActivityTouchHocker.enableHocker(getActivity(),enable,debug);
    }

    private boolean stopPlayingAudio(String debug){
        AudioManager manager=AudioManager.instance();
        return null!=manager&&manager.stopPlayVoiceFile(debug);
    }

    @Override
    public boolean onClicked(int i, View view, Object o) {
        return false;
    }

    @Override
    protected void onRootDetached(String debug) {
        super.onRootDetached(debug);
        mNativityActivityTouchHocker.closeHocker("While home model root detached.");
    }

    private boolean applyHomeMenus(List<Menu<Group>> channels, String debug){
        if (null != channels) {
            Debug.D("Apply home menus "+(null!=channels?channels.size():-1)+" "+(null!=debug?debug:"."));
            final ViewGroup vg = getHomeMenuItemViewContent();
            if (null != vg) {
                final List<Menu> menus=new ArrayList<>();
                for (Menu channel:channels) {
                    if (null==channel||!channel.isVisible()){
                        continue;//Skip invisible channel
                    }
                    String channelKey=channel.getMenuKey();
                    if (null==channelKey||channelKey.length()<=0){
                        continue;//Skip channel
                    }
                    menus.add(channel);
                }
                final int currentTotal=vg.getChildCount();
                final int menuTotal=menus.size();
                if (currentTotal>menuTotal){
                    vg.removeViews(menuTotal,currentTotal-menuTotal);
                }
                final List<View> notNeedRemove=new ArrayList<>();
                LayoutInflater inflater = LayoutInflater.from(vg.getContext());
                final Menu currentSelected=mSelectMenu.get();
                Menu selectNewMenu=null;
                for (int i = 0; i < menuTotal; i++) {
                    View childView=i<currentTotal?vg.getChildAt(i):null;
                    ViewDataBinding dataBinding=null!=childView? DataBindingUtil.getBinding(childView):null;
                    if ((null==dataBinding||!(dataBinding instanceof CsdkHomeItemMenuBinding))&&null!=childView){
                        dataBinding=null;
                    }
                    dataBinding=null!=dataBinding?dataBinding:DataBindingUtil.inflate(inflater, R.layout.csdk_home_item_menu, vg, true);
                    if (null==dataBinding||!(dataBinding instanceof CsdkHomeItemMenuBinding)){
                        Debug.W("Can't apply home menu while create item view invalid.");
                        continue;
                    }
                    if (null!=(childView=dataBinding.getRoot())){
                        notNeedRemove.add(childView);
                    }
                    CsdkHomeItemMenuBinding menuBinding=(CsdkHomeItemMenuBinding)dataBinding;
                    Menu menu=menus.get(i);
                    menuBinding.setClick(Click.click(this).tag(menu));
                    menuBinding.setMenu(menu);
                    selectNewMenu=null!=selectNewMenu?selectNewMenu:menu;
                    selectNewMenu=null!=currentSelected&&currentSelected.isChannelIdMatch(menu.getId())?menu:selectNewMenu;
                }
                return selectMenu(selectNewMenu,"After home menus apply "+(null!=debug?debug:"."));
            }
        }
        return false;
    }

    private boolean selectMenu(Menu menu,String debug){
        return selectMenu(menu,null,debug);
    }

    private boolean selectMenu(Menu menu,Object argObj,String debug){
        final String menuId=null!=menu?menu.getId():null;
        if (null==menuId||menuId.length()<=0){
            Debug.W("Can't select home menu while menu id invalid "+(null!=debug?debug:"."));
            return false;
        }
        Menu current=mSelectMenu.get();
        mSelectMenu.set(menu);
        if (null==current||!current.isChannelIdMatch(menuId)){//Check if changed
            ViewGroup vg = getHomeMenuItemViewContent();
            int count=null!=vg?vg.getChildCount():-1;
            for (int i = 0; i < count; i++) {
                View child=vg.getChildAt(i);
                ViewDataBinding binding=null!=child?DataBindingUtil.getBinding(child):null;
                if (null!=binding&&binding instanceof CsdkHomeItemMenuBinding){
                    CsdkHomeItemMenuBinding menuBinding=(CsdkHomeItemMenuBinding)binding;
                    Menu childMenu=menuBinding.getMenu();
                    menuBinding.setSelected(null!=childMenu&&childMenu.isChannelIdMatch(menuId));
                }
            }
        }
        //Set select content model
        final ViewGroup contentRoot=getHomeContentRoot();
        if (null==contentRoot){
            Debug.W("Can't set home menu content model while content view invalid "+(null!=debug?debug:"."));
            return false;
        }
        final int contentChildCount=contentRoot.getChildCount();
        View currentView=contentChildCount>0?contentRoot.getChildAt(contentChildCount-1):null;
        ViewDataBinding currentBinding=null!=currentView?DataBindingUtil.getBinding(currentView):null;
        String menuType=null!=menu?menu.getMenuType():null;
        Model contentModel=null;
        if (null!=menuType&&menuType.length()>0){
            String menuKey=menu.getMenuKey();
            if (menuType.equals(Menu.MENU_TYPE_CHANNEL)){
                contentModel= null!=currentBinding&&currentBinding instanceof CsdkHomeGroupModelBinding ?((CsdkHomeGroupModelBinding)currentBinding).getVm():null;
                contentModel=null!=contentModel?contentModel:new HomeGroupModel(getApi());
            }else if (menuType.equals(Menu.MENU_TYPE_MENU)){
                if (menuKey.equals(GroupType.GROUP_TYPE_SYSTEM)){
                    contentModel= null!=currentBinding&&currentBinding instanceof CsdkHomeSystemModelBinding ?((CsdkHomeSystemModelBinding)currentBinding).getVm():null;
                    contentModel=null!=contentModel&&contentModel instanceof HomeSystemModel ?contentModel:new HomeSystemModel(getApi());
                }else if (menuKey.equals(GroupType.GROUP_TYPE_FRIEND_LIST)){
                    contentModel= null!=currentBinding&&currentBinding instanceof CsdkHomeFriendsModelBinding ?((CsdkHomeFriendsModelBinding)currentBinding).getVm():null;
                    contentModel=null!=contentModel&&contentModel instanceof HomeFriendsModel ?contentModel:new HomeFriendsModel(getApi());
                }
            }
        }
        if (null!=contentModel&&contentModel instanceof HomeContentModel){
            ((HomeContentModel)contentModel).onMenuSelect(menu,argObj);
        }
        final View contentModelView=null!=contentModel?new ModelBinder().bind(contentRoot,contentModel,debug):null;
        if (null==contentModelView){
            Debug.W("Not generate home menu content model or content model invalid.");
        }
        final List<View> needRemoveList=new ArrayList<>();
        int childCount=contentRoot.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child=contentRoot.getChildAt(i);
            if (null!=child&&(null==contentModelView||child!=contentModelView)){
                needRemoveList.add(child);
            }
        }
        for (View child:needRemoveList) {
            contentRoot.removeView(child);
        }
        updateCurrentSession("");
        return true;
    }

    private Session updateCurrentSession(String debug){
        final ViewGroup contentRoot=getHomeContentRoot();
        final int contentChildCount=null!=contentRoot?contentRoot.getChildCount():null;
        View currentView=contentChildCount>0?contentRoot.getChildAt(contentChildCount-1):null;
        ViewDataBinding binding=null!=currentView?DataBindingUtil.getBinding(currentView):null;
        Class cls=null!=binding?binding.getClass():null;
        Field[] fields=null!=cls?cls.getSuperclass().getDeclaredFields():null;
        Session session=null;
        if (null!=fields&&fields.length>0){
            for (Field child:fields) {
                if (null!=child){
                    try {
                        child.setAccessible(true);
                        Object object=child.get(binding);
                        if (null!=object&&object instanceof HomeSessionModel){
                            session= ((HomeSessionModel)object).getSessionObject();
                            break;
                        }
                    }catch (Exception e){
                        //Do nothing
                    }
                }
            }
        }
        mCurrentSession.set(session);
        return session;
    }

    @Override
    public Object onResolveModelView(Context context) {
        return R.layout.csdk_home_model;
    }

    public OutlineMessageAdapter getOutlineAdapter() {
        return mOutlineAdapter;
    }

    public ObservableField<Boolean> getVoiceMessageSendEnable() {
        return mVoiceMessageSendEnable;
    }

    public ObservableField<Boolean> getInputEnable() {
        return mInputEnable;
    }

    public ObservableField<Menu> getShowingChannel() {
        return mShowingChannel;
    }

    public ObservableField<Boolean> getShowOutline() {
        return mShowOutline;
    }

    public View.OnTouchListener getEmptyViewTouchListener() {
        return mEmptyViewTouchListener;
    }

    public ObservableField<Boolean> getOutChatEnable() {
        return mOutChatEnable;
    }

    public ObservableField<Boolean> getRoomAudioEnable() {
        return mRoomAudioEnable;
    }

    public ObservableField<Boolean> getInputEmoji() {
        return mInputEmoji;
    }

    public ObservableField<Session> getCurrentSession() {
        return mCurrentSession;
    }

    private ViewGroup getHomeContentRoot(){
        View view= findViewById(R.id.csdk_homeModel_contentFL);
        return null!=view&&view instanceof ViewGroup?((ViewGroup)view):null;
    }

    private ViewGroup getHomeMenuItemViewContent(){
        View view= findViewById(R.id.csdk_homeModel_menuLL);
        return null!=view&&view instanceof ViewGroup?((ViewGroup)view):null;
    }

}
