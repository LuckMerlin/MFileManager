package luckmerlin.core.dialog;

import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;

/**
 * Create LuckMerlin
 * Date 14:31 2021/4/23
 * TODO
 */
public class PopupWindow {
    private final android.widget.PopupWindow mPopupWindow;

    public PopupWindow(){
        this(0,0);
    }

    public PopupWindow(View contentView) {
        mPopupWindow=new android.widget.PopupWindow(contentView);
    }

    public PopupWindow(int width, int height) {
        mPopupWindow=new android.widget.PopupWindow(width,height);
    }

    public PopupWindow(View contentView, int width, int height) {
        mPopupWindow=new android.widget.PopupWindow(contentView,width,height);
    }

    public PopupWindow(View contentView, int width, int height, boolean focusable) {
        mPopupWindow=new android.widget.PopupWindow(contentView,width,height,focusable);
    }

    final void fixDropDownHeight(View anchor){
        if (null!=anchor&&Build.VERSION.SDK_INT >= 24){
            if (getHeight()==WindowManager.LayoutParams.MATCH_PARENT){
                Rect ew = new Rect();
                anchor.getGlobalVisibleRect(ew);
                Resources resources=anchor.getResources();
                DisplayMetrics metrics=null!=resources?resources.getDisplayMetrics():null;
                int height=null!=metrics?metrics.heightPixels-ew.top:-1;
                if (height>0){
                    setHeight(height);
                }
            }
        }
    }

    public final PopupWindow showAsDropDown(View anchor){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            popupWindow.showAsDropDown(anchor);
            fixDropDownHeight(anchor);
        }
        return this;
    }

    public final PopupWindow update(View anchor, int width, int height){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            popupWindow.update(anchor,width,height);
        }
        return this;
    }

    public final PopupWindow update(View anchor, int xoff, int yoff, int width, int height){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            popupWindow.update(anchor,xoff,yoff,width,height);
        }
        return this;
    }

    public final PopupWindow showAtLocation(View parent, int gravity, int x, int y){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            popupWindow.showAtLocation(parent,gravity,x,y);
        }
        return this;
    }

    public final PopupWindow setOutsideTouchable(boolean enable){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            popupWindow.setOutsideTouchable(enable);
        }
        return this;
    }

    public final PopupWindow setClippingEnabled(boolean enable){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            popupWindow.setClippingEnabled(enable);
        }
        return this;
    }

    public final PopupWindow setWidth(int width){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            popupWindow.setWidth(width);
        }
        return this;
    }

    public final PopupWindow setHeight(int height){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            popupWindow.setHeight(height);
        }
        return this;
    }

    public final PopupWindow setContentView(View contentView){
        return setContentView(contentView, null, null);
    }

    public final PopupWindow setContentView(View contentView,Integer width,Integer height){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow&&null!=contentView&&contentView.getParent()==null){
            popupWindow.setContentView(contentView);
            setWidth(null!=width?width:WindowManager.LayoutParams.WRAP_CONTENT).
                    setHeight(null!=height?height:WindowManager.LayoutParams.WRAP_CONTENT);
        }
        return this;
    }

    public final PopupWindow setOverlapAnchor(boolean enable){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popupWindow.setOverlapAnchor(enable);
            }
        }
        return this;
    }

    public final PopupWindow setBackgroundDrawable(Drawable drawable){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            popupWindow.setBackgroundDrawable(drawable);
        }
        return this;
    }

    public final PopupWindow dismiss(){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            popupWindow.dismiss();
        }
        return this;
    }

    public final PopupWindow showAsDropDown(View anchor, int xoff, int yoff){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            popupWindow.showAsDropDown(anchor,xoff,yoff);
            fixDropDownHeight(anchor);
        }
        return this;
    }

    public final PopupWindow showAsDropDown(View anchor, int xoff, int yoff, int gravity){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                popupWindow.showAsDropDown(anchor, xoff, yoff, gravity);

            }else{
                popupWindow.showAsDropDown(anchor,xoff,yoff);
            }
            fixDropDownHeight(anchor);
        }
        return this;
    }

    public final PopupWindow setTouchable(boolean touchable){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            popupWindow.setTouchable(touchable);
        }
        return this;
    }

    public final PopupWindow setTouchModal(boolean touchModal){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupWindow.setTouchModal(touchModal);
            }
        }
        return this;
    }

    public final PopupWindow setSoftInputMode(int mode){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        if (null!=popupWindow){
            popupWindow.setSoftInputMode(mode);
        }
        return this;
    }

    public final View getContentView(){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        return null!=popupWindow?popupWindow.getContentView():null;
    }

    public final int getWidth(){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        return null!=popupWindow?popupWindow.getWidth():0;
    }

    public final int getHeight(){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        return null!=popupWindow?popupWindow.getHeight():0;
    }


    public final boolean isShowing(){
        android.widget.PopupWindow popupWindow=mPopupWindow;
        return null!=popupWindow&&popupWindow.isShowing();
    }
}
