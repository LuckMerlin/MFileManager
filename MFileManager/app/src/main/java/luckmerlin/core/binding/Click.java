//package luckmerlin.core.binding;
//
//import android.view.View;
//import androidx.databinding.BindingAdapter;
//
//import com.luckmerlin.databinding.touch.OnViewClick;
//
///**
// * Create LuckMerlin
// * Date 18:50 2021/2/1
// * TODO
// */
//public final class Click {
//    public final static int NONE=0;
//    public final static int CLICK=1;
//    public final static int LONG_CLICK=2;
//    private boolean mClickAnimEnable=true;
//    private final int mClick;
//    private Integer mId;
//    private final ClickListener mListener;
//    private Object mTag;
//
//    private Click(int click, ClickListener listener) {
//        mClick=click;
//        mListener=listener;
//    }
//
//    public static Click click(int click){
//        return new Click(click,null);
//    }
//
//    public Click tag(Object tag){
//        mTag=tag;
//        return this;
//    }
//
//    public Click id(Object id){
//        mId=null!=id?id instanceof Integer?(Integer)id:mId:null;
//        return this;
//    }
//
//    public Click animation(boolean clickAnim){
//        mClickAnimEnable=clickAnim;
//        return this;
//    }
//
//    public static Click click(){
//        return click(null);
//    }
//
//    public static Click click(ClickListener listener){
//        return click(CLICK,listener);
//    }
//
//    public static Click click(int click, ClickListener listener){
//        return new Click(click,listener);
//    }
//
//    @BindingAdapter("clicker")
//    public static void setClickEnable(View view, Click click){
//        if (null!=view&&null!=click) {
//            final int clickValue=click.mClick;
//            final Object tag=click.mTag;
//            final ClickListener listener=click.mListener;
//            final Integer id=click.mId;
//            final boolean clickAnimEnable=click.mClickAnimEnable;
//            if ((clickValue&CLICK)>0){
//                view.setOnClickListener((v)->{
//                    if (null!=v&&clickAnimEnable){
////                        new ViewClickAnimator().startClickAnim(v);
//                    }
//                    final int viewId=null!=id?id:null!=v?v.getId():0;
////                    if (null!=listener&&listener instanceof OnViewClick &&((OnViewClick)listener).onViewClick(viewId, v,tag)){
////                        return;
////                    }
////                    new DataBinding().dispatch(v, (arg)-> null!=arg&&arg instanceof OnViewClick&&((OnViewClick)arg).onClicked(viewId, v,tag));
//                });
//            }
//            if ((clickValue&LONG_CLICK)>0) {
//                view.setOnLongClickListener((v)-> {
//                    final int viewId =null!=id?id: null != v ? v.getId() : 0;
//                    if (null != listener && listener instanceof OnViewLongClick && ((OnViewLongClick) listener).
//                            onLongClicked(viewId, v, tag)) {
//                        return true;
//                    }
//                   return new DataBinding().dispatch(v, (arg) -> null != arg && arg instanceof OnViewLongClick
//                            && ((OnViewLongClick) arg).onLongClicked(viewId, v, tag));
//                });
//            }
//        }
//    }
//}
