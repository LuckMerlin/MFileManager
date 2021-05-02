package luckmerlin.core.dialog;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.luckmerlin.core.debug.Debug;

import java.lang.ref.WeakReference;

/**
 * Create LuckMerlin
 * Date 20:03 2021/4/13
 * TODO
 */
public class SoftInputDialog {
    private final Dialog mLMDialog;
    private boolean mShownSoftInput=false;
    private WeakReference<EditText> mInputText;
    private WeakReference<EditText> mBindText;
    private final Runnable mInputRunnable=new Runnable() {
        @Override
        public void run() {
            EditText editText=isAttachedToWindow(mLMDialog.getDecorView())?getInputEditText():null;
            if (null!=editText&&!mShownSoftInput){
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean succeed=inputManager.showSoftInput(editText, 0);
                Debug.D("Open softInput."+succeed);
            }
        }
    };
    private final TextView.OnEditorActionListener mOnEditorActionListener=new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (SoftInputDialog.this.onEditorAction(v,actionId,event)){
                return true;
            }
            Debug.D("Need dismiss softInput while editor action unHanded.");
            Dialog dialog=mLMDialog;
            return null!=dialog&&(null!=dialog.dismiss()||true);
        }
    };
    private final TextWatcher mTextWatcher=new TextWatcher(){
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            updateInputToBindText(false);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
    };

    public SoftInputDialog(Context context){
        this(context,true);
    }

    public SoftInputDialog(Context context, boolean cutoutMode) {
        final Dialog dialog=mLMDialog=new Dialog(context,cutoutMode);
        dialog.setGravity(Gravity.BOTTOM);
        dialog.setCanceledOnTouchOutside(false).setCancelable(false);
        dialog.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        View decoreView= dialog.getDecorView();
        if (null!=decoreView){
            decoreView.setOnTouchListener((View v, MotionEvent event)-> {
                if (null!=event&&event.getAction()== MotionEvent.ACTION_DOWN){
                    Debug.D("To dismiss while softInput root touch.");
                    decoreView.setAlpha(0);//Alpha to 0 before dismiss
                    closeSoftInput("While softInput root touch.");
                    dialog.dismiss();
                }
                return false;
            });
            final int uiOptions =( View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            decoreView.setSystemUiVisibility(uiOptions);
            decoreView.setOnSystemUiVisibilityChangeListener((int visibility)-> {
                    if (Build.VERSION.SDK_INT >= 19) {
                        decoreView.setSystemUiVisibility(uiOptions| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    } else {
                        decoreView.setSystemUiVisibility(uiOptions| View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
            });
            mShownSoftInput=false;//Reset
            final ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener=()->{
                Object winObject=context.getSystemService(Context.WINDOW_SERVICE);
                WindowManager manager=null!=winObject&&winObject instanceof WindowManager ?(WindowManager)winObject:null;
                Display display=null!=manager?manager.getDefaultDisplay():null;
                View rootView=dialog.getDecorView();
                if (null==display||null==rootView){
                    return;
                }
                Rect rect=new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);
                Point screenSize = new Point();
                display.getSize(screenSize);
                boolean showingSoftInput=rect.height()!=screenSize.y;
                if (!showingSoftInput&&mShownSoftInput){//SoftInput closed
                    Debug.D("To dismiss softInput dialog while softInput closed.");
                    dialog.dismiss();
                }
                mShownSoftInput=mShownSoftInput?mShownSoftInput:showingSoftInput;
                alpha(dialog.getDecorView(),mShownSoftInput?1:0);
            };
            decoreView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
            decoreView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    if (null!=v&&v==decoreView) {
                        Debug.D("SoftInput root attach window.");
                        enableSoftInput(-1,"While root attached window.");
                    }
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    if (null!=v&&v==decoreView){
                        Debug.D("SoftInput root detached window.");
                        updateInputToBindText(false);//Update latest text
                        decoreView.removeOnAttachStateChangeListener(this);
                        ViewTreeObserver observer=decoreView.getViewTreeObserver();
                        if (null!=observer){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                observer.removeOnGlobalLayoutListener(globalLayoutListener);
                            }else{
                                observer.removeGlobalOnLayoutListener(globalLayoutListener);
                            }
                        }
                        enableSoftInput(null,"While root detached window.");
                        EditText editText=getInputEditText();
                        if (null!=editText){
                            editText.removeTextChangedListener(mTextWatcher);
                            editText.setOnEditorActionListener(null);
                        }
                    }
                }
            });
            enableSoftInput(-1,"While create.");
        }
    }

    protected boolean onEditorAction(TextView v, int actionId, KeyEvent event){
        //Do nothing
        return false;
    }

    protected CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend){
        //Do nothing
        return null;
    }

    public final SoftInputDialog setContentView(int layoutId){
        return setContentView(layoutId,null);
    }

    public final SoftInputDialog setContentView(int layoutId, Integer editTextId){
        Context context=mLMDialog.getContext();
        LayoutInflater inflater=null!=context? LayoutInflater.from(context):null;
        View view=null!=inflater?inflater.inflate(layoutId, null, false):null;
        return setContentView(view,editTextId);
    }

    public final SoftInputDialog setContentView(View view){
        return setContentView(view,null);
    }

    public final SoftInputDialog setContentView(View view, Integer editTextId){
        if (null==view) {
            Debug.W("Can't set softInput content view while view NULL.");
        }else if (null!=getInputEditText()){
            Debug.W("Can't set softInput content view while exist view.");
        }else if (isAttachedToWindow(view)){
            Debug.W("Can't set softInput content view while view already attached to window.");
        }else {
            final EditText editText=findEditTextChild(view, editTextId);
            if (null==editText){
                Debug.W("Can't set softInput content view while None editText found within view.");
            }else{
                mInputText=new WeakReference<>(editText);
                Dialog lmDialog=mLMDialog;
                View root=lmDialog.getDecorView();
                if (null!=root){//We make alpha animation to keep from UI dither
                    root.setAlpha(0);
                    ObjectAnimator animator= ObjectAnimator.ofFloat(root, "alpha", 0,1);
                    animator.setStartDelay(300);
                    animator.setInterpolator(new AccelerateInterpolator());
                    animator.setDuration(500).start();
                }
                lmDialog.setContentView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        return this;
    }

    public final SoftInputDialog dismiss(){
        WeakReference<EditText> reference=mInputText;
        if (null!=reference&&reference.get()==null){
            mInputText=null;
        }
        mLMDialog.dismiss();
        return this;
    }

    public final SoftInputDialog bindText(EditText editText){
        removeBindText();
        if (null!=editText){
            mBindText=new WeakReference<>(editText);
            updateInputToBindText(true);
        }
        return this;
    }

    public final EditText getBindText() {
        WeakReference<EditText> reference=mBindText;
        return null!=reference?reference.get():null;
    }

    public final SoftInputDialog appendImeOptions(int imeOptions){
        return setImeOptions(getImeOptions()|imeOptions);
    }

    public final SoftInputDialog setImeOptions(int imeOptions){
        EditText editText=getInputEditText();
        if (null!=editText){
            editText.setImeOptions(imeOptions);
        }
        return this;
    }

    public final SoftInputDialog removeBindText(){
        WeakReference<EditText> reference=mBindText;
        mBindText=null;
        if (null!=reference){
            reference.clear();
        }
        return this;
    }


    public final SoftInputDialog cleanInput(){
        EditText editText=getInputEditText();
        Editable editable=null!=editText?editText.getText():null;
        if (null!=editable){
            editable.clear();
        }
        return this;
    }


    public final int getImeOptions(){
        EditText editText=getInputEditText();
        return null!=editText?editText.getImeOptions(): EditorInfo.IME_NULL;
    }

    public final SoftInputDialog show() {
        EditText editText=getInputEditText();
        if (editText!=null){
            Dialog dialog=mLMDialog;
            if (null!=dialog){
                dialog.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialog.show();
                dialog.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                View decorView=mLMDialog.getDecorView();
                if (null!=decorView&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN);
                }
                enableSoftInput(-1,"While show call.");
                editText.addTextChangedListener(mTextWatcher);
                editText.setOnEditorActionListener(mOnEditorActionListener);
            }
        }
        return this;
    }

    final boolean alpha(View view, float alpha){
//        if (null!=view){
//            view.setAlpha(alpha);
//            view.setFocusable(alpha>0);
//            if (view instanceof ViewGroup){
//                ViewGroup vg=(ViewGroup)view;
//                int count=vg.getChildCount();
//                for (int i = 0; i < count; i++) {
//                    if (null!=(view=vg.getChildAt(i))){
//                        alpha(view,alpha);
//                    }
//                }
//            }
//            return true;
//        }
        return false;
    }

    final boolean updateInputToBindText(boolean fill){
        WeakReference<EditText> mBindReference=mBindText;
        WeakReference<EditText> mInputReference=mInputText;
        EditText bindText=null!=mBindReference?mBindReference.get():null;
        EditText inputText=null!=mInputReference?mInputReference.get():null;
        return null!=bindText&&null!=inputText&&(fill?updateEditText(bindText, inputText):updateEditText(inputText, bindText));
    }

    final boolean updateEditText(EditText from, EditText to){
        if (null!=from&&null!=to){
            Editable editable=from.getText();
            to.setText(null!=editable?editable:"");
            int fromStart=from.getSelectionStart();
            int fromEnd=from.getSelectionEnd();
            int length=to.length();
            to.setSelection(Math.min(fromStart, length), Math.min(fromEnd, length));
            return true;
        }
        return false;
    }

    final boolean enableSoftInput(Integer dither, String debug){
        View root=mLMDialog.getDecorView();
        if (null!=root){
            Debug.D("Enable softInput "+(null!=debug?debug:"")+" "+dither);
            root.removeCallbacks(mInputRunnable);
            if (null!=dither){
                root.postDelayed(mInputRunnable,dither<0?200:dither);
            }
            return true;
        }
        return false;
    }

    final boolean isAttachedToWindow(View view){
        return null!=view&&(Build.VERSION.SDK_INT >= 19?view.isAttachedToWindow():null!=view.getWindowToken());
    }

    final EditText findEditTextChild(View view, Integer editTextId){
        if (null!=view){
            if (view instanceof EditText &&(null==editTextId||editTextId==view.getId())){
                return (EditText)view;
            }else if (null!=editTextId){
                view=view.findViewById(editTextId);
                return null!=view&&view instanceof EditText ?(EditText)view:null;
            }else if (view instanceof ViewGroup){
                ViewGroup group=(ViewGroup)view;
                int count=group.getChildCount();
                View child=null;
                for (int i = 0; i < count; i++) {
                    if (null!=(child=group.getChildAt(i))&&
                            null!=(child=findEditTextChild(child,editTextId))&&child instanceof EditText){
                        return (EditText)child;
                    }
                }
            }
        }
        return null;
    }

    final boolean closeSoftInput(String debug){
        View decoreView=mLMDialog.getCurrentFocus();
        InputMethodManager inputManager = null!=decoreView?(InputMethodManager) decoreView.
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE):null;
        if (null!=inputManager) {
            boolean succeed=inputManager.hideSoftInputFromWindow(decoreView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            Debug.D("Close softInput "+(null!=debug?debug:".")+" "+succeed);
            return succeed;
        }
        return false;
    }

    final EditText getInputEditText(){
        WeakReference<EditText> reference=mInputText;
        return null!=reference?reference.get():null;
    }

    private int getScreenOrientation() {
        Context context=mLMDialog.getContext();
        Resources resources=null!=context?context.getResources():null;
        return null!=resources?resources.getConfiguration().orientation: Configuration.ORIENTATION_UNDEFINED;
    }

}
