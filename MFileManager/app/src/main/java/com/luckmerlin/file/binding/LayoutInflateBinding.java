package com.luckmerlin.file.binding;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.CreatedModel;
import com.luckmerlin.databinding.CustomBinding;
import com.luckmerlin.databinding.DataBindingUtil;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.ModelBinder;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.databinding.ViewBindingBinder;

import java.util.Collection;

public final class LayoutInflateBinding implements CustomBinding {
    private final Object mLayout;

    private LayoutInflateBinding(Object layout){
        mLayout=layout;
    }

    public static LayoutInflateBinding layout(Object layout){
        return new LayoutInflateBinding(layout);
    }

    @Override
    public boolean onBind(View view) {
        return null!=inflateLayout(view,mLayout);
    }

    private View inflateLayout(View view,Object layout){
        if (null!=view&&view instanceof ViewGroup &&null!=layout){
            if (layout instanceof Integer){
                if (!layout.equals(Resources.ID_NULL)){
                    ViewDataBinding binding=DataBindingUtil.inflate(LayoutInflater.from(view.getContext()),(Integer)layout,(ViewGroup)view,true);
                    return null!=binding?binding.getRoot():null;
                }
            }else if (layout instanceof View&&null==((View)layout).getParent()){
                ViewGroup.LayoutParams params=view.getLayoutParams();
                int width=null!=params?params.width:ViewGroup.LayoutParams.WRAP_CONTENT;
                int height=null!=params?params.height:ViewGroup.LayoutParams.WRAP_CONTENT;
                ((ViewGroup)view).addView((View)layout,new ViewGroup.LayoutParams(width,height));
                return (View)layout;
            }else if (layout instanceof ViewDataBinding){
                View root=((ViewDataBinding)layout).getRoot();
                if (null!=root&&null==root.getParent()){
                    return inflateLayout(view,root);
                }
            }else if (layout instanceof Collection &&((Collection)layout).size()>0){
                View childView=null;
                for (Object child:(Collection)layout) {
                    View view1=inflateLayout(view,child);
                    childView=null!=view1?view1:childView;
                }
                return childView;
            }else if (layout instanceof Model){
                Model model=(Model)layout;
                View root=model.getRoot();
                if (null!=root&&null!=(inflateLayout(view,root))){
                    new ModelBinder().bindModelForObject(view.getContext(),layout,null);
                }else if (model instanceof OnModelResolve&&null!=(inflateLayout
                        (view,((OnModelResolve)model).onResolveModel()))){
                    new ModelBinder().bindModelForObject(view.getContext(),layout,null);
                }
            }
        }
        return null;
    }
}
