package com.csdk.ui.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<T> mData;
    private final SparseArray<Object> mFixHolders=new SparseArray<>(0);
    public final static int VIEW_TYPE_DATA=0;
    public final static int VIEW_TYPE_HEADER=-1;
    public final static int VIEW_TYPE_TAIL=-2;
    public final static int VIEW_TYPE_EMPTY=-3;
    private final static float mInitialAlpha=0.001f;

    public interface OnLayoutManageResolver{
        RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView recyclerView);
    }

    public ListAdapter(){
        this(null);
    }

    public ListAdapter(List<T> list){
        int size=null!=list?list.size():0;
        List<T> data=size>0?(mData=new ArrayList<T>(size)):null;
        if (null!=data){
            data.addAll(list);
        }
    }

    public final boolean replace(List<T> data, boolean addIfNotExist){
        if (null!=data&&data.size()>0){
            for(T child:data){
                if (null!=child){
                    replace(child, addIfNotExist);
                }
            }
            return true;
        }
        return false;
    }

    protected RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView recyclerView){
        return null!=recyclerView?new DefaultLayoutManager().vertical(recyclerView):null;
    }

    public final boolean setEmpty(Object holder){
        return setFixHolder(VIEW_TYPE_EMPTY,holder);
    }

    public final boolean setTail(Object holder){
        return setFixHolder(VIEW_TYPE_TAIL,holder);
    }

    public final boolean setFixHolder(int type,Object holder){
        SparseArray<Object> holders=mFixHolders;
        if (null!=holders){
            holders.remove(type);
            if (null!=holder){
                holders.put(type,holder);
            }
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public final boolean replace(T data,boolean addIfNotExist){
        return replace(data,addIfNotExist,null);
    }

    public final boolean replace(T data,boolean addIfNotExist,String debug){
        if (null!=data){
            List<T> list=mData;
            if (null!=(list=null!=list?list:(mData=new ArrayList<>(1)))){
                int size=list.size();
                int index=list.indexOf(data);//Index already exist data position
                if (index<0){
                    if (!addIfNotExist){
                        return false;//Not need add
                    }
                    list.add(data);
                    int position=size+1;
                    notifyItemInserted(position);
                    onDataSizeChanged(size,position,position,data);
                    return true;
                }else if (null!=list.remove(index)){
                    list.add(index,data);
                    notifyItemChanged(index,"Payload");
                    onDataSizeChanged(size,size,index,data);
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean set(List<T> data, boolean smooth){
        return set(data,smooth,null);
    }

    public final boolean set(List<T> data, boolean smooth,String debug){
        List<T> list=mData;
        final int size=null!=data?data.size():-1;
        if (size<=0){
            if (null!=list){
                mData=null;
                notifyDataSetChanged();
            }
            onDataSizeChanged(size,0,-1,null);
            return true;
        }
        list=null!=list?list:(mData=new ArrayList<>(size));
        list.clear();
        list.addAll(data);
        onDataSizeChanged(0,size,-1,null);
        notifyDataSetChanged();
        return true;
    }

    public final boolean insert(int index,List<T> data,boolean smooth,String debug){
        if (index<0){
            return false;
        }
        final int size=null!=data?data.size():-1;
        if (size>0){
            List<T> list=mData;
            list=null!=list?list:(mData=new ArrayList<>(size));
            synchronized (list){
                if (index>list.size()){
                    return false;
                }
                list.addAll(index,data);
                notifyDataSetChanged();
            }
            return true;
        }
        return false;
    }

    public final boolean append(List<T> data,boolean smooth,String debug){
        final int size=null!=data?data.size():-1;
        if (size>0){
            List<T> list=mData;
            list=null!=list?list:(mData=new ArrayList<>(size));
            int currentSize=list.size();
            list.addAll(currentSize,data);
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public final boolean add(T data){
        return add(-1,data, true);
    }

    public final boolean add(int index, List<T> data){
        int dataSize=null!=data?data.size():-1;
        if (dataSize>0){
            List<T> list=mData;
            list=null!=list?list:(mData=new ArrayList<>(1));
            boolean succeed=false;
            int currentSize=0;
            synchronized (list){
                if (index>=0&&index<=(currentSize=list.size())){
                    list.addAll(index, data);
                    notifyItemRangeInserted(index, dataSize);
                    succeed= true;
                }
            }
            onDataSizeChanged(currentSize,list.size(),index,null);
            return succeed;
        }
        return false;
    }

    public final boolean add(int index,T data,boolean skipAlreadyExist){
        if (null!=data){
            List<T> list=mData;
            list=null!=list?list:(mData=new ArrayList<>(1));
            int size=list.size();
            if ((!skipAlreadyExist||!list.contains(data))){
                index=index<0||index>size?size:index;
                list.add(index,data);
                notifyItemInserted(index);
                onDataSizeChanged(size,size+1,size,data);
                return true;
            }
        }
        return false;
    }

    public final int indexDataPosition(Object obj){
        List<T> data=null!=obj?mData:null;
        if (null!=data){
            synchronized (data){
                int length=data.size();
                for (int i = 0; i < length; i++) {
                    T child=data.get(i);
                    if (null!=child&&child.equals(obj)){
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public final T indexData(Object obj){
        List<T> data=null!=obj?mData:null;
        if (null!=data){
            for (T child:data) {
                if (null!=child&&child.equals(obj)){
                    return child;
                }
            }
        }
        return null;
    }

    public final int removeAndIndex(Object obj,String debug){
        if (null!=obj){
            List<T> list=mData;
            int index=null!=list?list.indexOf(obj):-1;
            index=index<0&&obj instanceof Integer?(Integer)obj:index;
            T data=null;
            int size=0;
            if (index>=0&&index<(size=list.size())&&null!=(data=list.remove(index))){
                notifyItemRemoved(index);
                onDataSizeChanged(size,size-1,index,data);
                return index;
            }
        }
        return -1;
    }

    public final T remove(Object obj,String debug){
        if (null!=obj){
            List<T> list=mData;
            int index=null!=list?list.indexOf(obj):-1;
            index=index<0&&obj instanceof Integer?(Integer)obj:index;
            T data=null;
            int size=0;
            if (index>=0&&index<(size=list.size())&&null!=(data=list.remove(index))){
                notifyItemRemoved(index);
                onDataSizeChanged(size,size-1,index,data);
                return data;
            }
        }
        return null;
    }

    protected void  onDataSizeChanged(int lastSize,int currentSize,int position,T data){
        //Do nothing
    }

    protected Integer onResolveDataTypeLayout(ViewGroup parent){
        return null;
    }

    protected Integer onResolveViewTypeLayout(ViewGroup parent,int viewType){
        //Do nothing
        return null;
    }

    protected RecyclerView.ViewHolder onResolveViewTypeHolder(ViewGroup parent, int viewType){
        //Do nothing
        return null;
    }

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Integer layoutId=onResolveViewTypeLayout(parent,viewType);
        layoutId=null!=layoutId&&layoutId!= Resources.ID_NULL?layoutId:viewType==VIEW_TYPE_DATA?onResolveDataTypeLayout(parent):null;
        RecyclerView.ViewHolder holder=null;
        if (null!=layoutId&&layoutId!= Resources.ID_NULL){
            holder=createViewHolderWithLayoutId(parent,layoutId);
        }
        holder=null!=holder?holder:onResolveViewTypeHolder(parent,viewType);
        holder=null!=holder?holder:createHolderFromFixHolders(parent,viewType);
        holder= null!=holder?holder:new ViewHolder(new View(parent.getContext()));
        View itemView=null!=holder?holder.itemView:null;
        if (null!=itemView&&itemView.getAlpha()==1){
            itemView.setAlpha(mInitialAlpha);
        }
        return holder;
    }

    private RecyclerView.ViewHolder createHolderFromFixHolders(ViewGroup parent, int viewType){
        SparseArray<Object> fixHolders=mFixHolders;
        Object object=null!=fixHolders?fixHolders.get(viewType,null):null;
        if (null!=object){
            if (object instanceof Integer){
                return createViewHolderWithLayoutId(parent,(Integer)object);
            }else if (object instanceof RecyclerView.ViewHolder){
                return (RecyclerView.ViewHolder)object;
            }
        }
        return null;
    }

    private RecyclerView.ViewHolder createViewHolderWithLayoutId(ViewGroup parent, int layoutId){
        Context context=null!=parent?parent.getContext():null;
        ViewDataBinding binding=null!=context? DataBindingUtil.inflate(LayoutInflater.from(context),layoutId,parent,false):null;
        return null!=binding?new ViewHolder(binding):null;
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        List<T> list=mData;
        T data= null!=list&&position>=0&&position<list.size()?list.get(position):null;
        View itemView= null!=holder?holder.itemView:null;
        ViewDataBinding binding= null!=itemView? DataBindingUtil.findBinding(itemView):null;
        onBindViewHolder(holder,position, data,binding,payloads);
        if (null!=itemView&&itemView.getAlpha()==mInitialAlpha){
            itemView.setAlpha(1);
        }
    }

    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, T data, ViewDataBinding binding, List<Object> payloads) {

    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Do nothing
    }

    public final boolean clean(String debug){
        List<T> data=mData;
        if (null!=data){
            int size=data.size();
            mData=null;
            data.clear();
            notifyDataSetChanged();
            onDataSizeChanged(size,0,-1,null);
            return true;
        }
        return false;
    }

    @Override
    public final int getItemViewType(int position) {
        int dataCount=getDataCount();
        if (position==(dataCount<=0?0:dataCount)){
            SparseArray<Object> fixHolder=mFixHolders;
            return null!=fixHolder&&null!=fixHolder.get(VIEW_TYPE_TAIL)?VIEW_TYPE_TAIL:VIEW_TYPE_EMPTY;
        }
        return super.getItemViewType(position);
    }

    protected void onResolveFixViewHolder(RecyclerView recyclerView){
        //Do nothing
    }

    protected void onAttachedRecyclerView(RecyclerView recyclerView){
        //Do nothing
    }

    protected void onLayoutManagerSet(RecyclerView recyclerView, RecyclerView.LayoutManager manager){
        //Do nothing
    }

    @Override
    public final void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        float alpha=0f;
        recyclerView.setAlpha(alpha);
        if (null!=recyclerView){
            RecyclerView.LayoutManager manager=this instanceof OnLayoutManageResolver?
                    ((OnLayoutManageResolver)this).onResolveLayoutManager(recyclerView):null;
            manager=null!=manager?manager:onResolveLayoutManager(recyclerView);
            recyclerView.setLayoutManager(manager);
            onLayoutManagerSet(recyclerView,manager);
            onResolveFixViewHolder(recyclerView);
        }
        //
        ObjectAnimator animator=ObjectAnimator.ofFloat(recyclerView, "alpha",alpha,1).setDuration(50);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        //
        onAttachedRecyclerView(recyclerView);

    }

    protected void onDetachedRecyclerView(RecyclerView recyclerView){
        //Do nothing
    }

    @Override
    public final void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        onDetachedRecyclerView(recyclerView);
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (null!=holder&&holder instanceof ViewHolder){
            ((ViewHolder)holder).onViewRecycled();
        }
    }

    @Override
    public final int getItemCount(){
        int dataCount=getDataCount();
        SparseArray<Object> fixHolders=mFixHolders;
        if (dataCount<=0){
            return null==fixHolders.get(VIEW_TYPE_TAIL)&&null==fixHolders.get(VIEW_TYPE_EMPTY)?0:1;
        }
        return dataCount+(null!=fixHolders.get(VIEW_TYPE_TAIL)?1:0);
    }

    public final T getData(int index){
        List<T> list=mData;
        return index>=0&&null!=list&&index<list.size()?list.get(index):null;
    }

    public final int getDataCount(){
        List<T> list=mData;
        return null!=list?list.size():-1;
    }

    protected final List<T> getDataSrc(){
        return mData;
    }

    public final List<T> getData(){
        List<T> list=mData;
        int size=null!=list?list.size():0;
        if (size>0){
           List<T> result= new ArrayList<>(size);
            result.addAll(list);
            return result;
        }
        return null;
    }

    protected final String getText(ViewDataBinding binding, int textId, Object ...args){
        View root=null!=binding?binding.getRoot():null;
        return null!=root?getText(root.getContext(), textId, args):null;
    }

    protected final String getText(Context context,int textId,Object ...args){
        return null!=context?context.getString(textId, args):null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull ViewDataBinding itemView) {
            this(itemView.getRoot());
        }

        public void  onViewRecycled(){
            //Do nothing
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
