package luckmerlin.core.binding;

import android.widget.TextView;
import androidx.databinding.BindingAdapter;
/**
 * Create LuckMerlin
 * Date 18:51 2021/4/30
 * TODO
 */
public final class Text {
    private Object mTextObj;

    public boolean setText(TextView textView, Object textObj){
        if (null!=textView){
            if (null!=textObj){
                if(textObj instanceof CharSequence){
                    textView.setText((CharSequence)textObj);
                }else if (textObj instanceof Integer){
                    textView.setText((Integer)textObj);
                }
            }
            textView.setText("");
            return false;
        }
        return false;
    }

    public Text setText(Object textObj){
        mTextObj=textObj;
        return this;
    }


    public static Text text(Object text){
        return new Text().setText(text);
    }

    @BindingAdapter("texts")
    public static void setText(TextView textView, Text textObj) {
        if (null!=textView){
            if (null!=textObj&&textObj instanceof Text){
                Text text=(Text)textObj;
                textObj.setText(textView, text.mTextObj);
            }else{
                new Text().setText(textView,textObj);
            }
        }
    }

}
