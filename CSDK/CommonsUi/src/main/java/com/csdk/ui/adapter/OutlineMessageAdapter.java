package com.csdk.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.csdk.api.bean.Link;
import com.csdk.api.bean.Message;
import com.csdk.api.core.ContentType;
import com.csdk.api.core.GroupType;
import com.csdk.api.core.Label;
import com.csdk.ui.R;
import com.csdk.ui.databinding.CsdkItemMessageOutlineBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Create LuckMerlin
 * Date 16:35 2020/8/18
 * TODO
 */
public class OutlineMessageAdapter extends AutoScrollMessageAdapter {
    private ObservableField<Integer> mCount=new ObservableField<>();

    @Override
    protected Integer onResolveDataTypeLayout(ViewGroup parent) {
        return R.layout.csdk_item_message_outline;
    }

    public final boolean addOutlineMessage(final Message message, String debug){
        if (null!=message){
            clean("While add new outline message.");
            List<Message> messages=new ArrayList<>(1);
            messages.add(message);
            return set(messages, true);
        }
        return false;
    }

    @Override
    protected void onDataSizeChanged(int lastSize, int currentSize, int position, Message data) {
        super.onDataSizeChanged(lastSize, currentSize, position, data);
        mCount.set(getDataCount());
    }

    private int getMessageTitleColor(Message message){
        String groupType=null!=message?message.getGroupType():null;
        if (null!=groupType){
            if (groupType.equals(GroupType.GROUP_TYPE_WORLD)){
                return Color.parseColor("#43ce53");
            }else if (groupType.equals(GroupType.GROUP_TYPE_AREA)){
                return Color.parseColor("#43ce53");
            }else if (groupType.equals(GroupType.GROUP_TYPE_TEAM)){
                return Color.parseColor("#437bce");
            }
        }
      return Color.parseColor("#de4343");
    }


    public ObservableField<Integer> getOutlineMessageCount() {
        return mCount;
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, final Message data, ViewDataBinding binding, List<Object> payloads) {
        super.onBindViewHolder(holder, position, data, binding, payloads);
        if (null!=binding&&binding instanceof CsdkItemMessageOutlineBinding){
            CsdkItemMessageOutlineBinding outlineBinding=(CsdkItemMessageOutlineBinding)binding;
                GradientDrawable drawable=new GradientDrawable();drawable.setCornerRadius(0);
                drawable.setColor(getMessageTitleColor(data));
            outlineBinding.setTitleDrawable(drawable);
            String fromType=null;
            String subType=null;
            String content=null;
            if (null!=data){
                subType=data.getExtraString(Label.LABEL_USER_NAME);
                content=data.getContent();
                View root=binding.getRoot();
                Context context=null!=root?root.getContext():null;
                Resources resources=null!=context?context.getResources():null;
                if (null!=resources){
                    String groupType=data.getGroupType();
                    if (null!=groupType&&groupType.length()>0&&null!=resources){
                        if (groupType.equals(GroupType.GROUP_TYPE_SYSTEM)){
                            fromType=resources.getString(R.string.csdk_system);
                        }else if (groupType.equals(GroupType.GROUP_TYPE_GUILD)){
                            fromType=resources.getString(R.string.csdk_labourUnion);
                        }else if (groupType.equals(GroupType.GROUP_TYPE_TEAM)){
                            fromType=resources.getString(R.string.csdk_team);
                        }else if (groupType.equals(GroupType.GROUP_TYPE_HALL)){
                            fromType=resources.getString(R.string.csdk_hall);
                        }else if (groupType.equals(GroupType.GROUP_TYPE_WORLD)){
                            fromType=resources.getString(R.string.csdk_world);
                        }else if (groupType.equals(GroupType.GROUP_TYPE_AREA)){
                            fromType=resources.getString(R.string.csdk_area);
                        }
                    }else{
                        fromType=resources.getString(R.string.csdk_friend);
                    }
                    String contentType=data.getContentType();
                    if (null!=contentType){
                        if (contentType.equals(ContentType.CONTENTTYPE_VOICE)){
                            String translation=data.getVoiceTranslation();
                            content="【"+resources.getString(R.string.csdk_voiceMessage)+"】"+(null!=translation?translation:"");
                        }else if (contentType.equals(ContentType.CONTENTTYPE_CUSTOM)){
                            Link link=null!=data?data.getLink():null;
                            CharSequence linkTitle=null!=link?link.getTitle():null;
                            content="【"+(null!=linkTitle&&linkTitle.length()>0?linkTitle:
                                    resources.getString(R.string.csdk_linkMessage))+"】";
                        }
                    }
                }
            }
            outlineBinding.setTitle(fromType);
            outlineBinding.setSubTitle(subType);
            outlineBinding.setMessage(content);
            //Post delay to remove
            View root=null!=outlineBinding?outlineBinding.getRoot():null;
            if (null!=root){
                root.postDelayed(()->remove(data,"While time counter down."),5000);
            }
        }
    }
}
