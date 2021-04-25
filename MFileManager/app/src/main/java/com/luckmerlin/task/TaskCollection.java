package com.luckmerlin.task;

import com.luckmerlin.core.match.Matchable;
import com.luckmerlin.core.match.Matcher;

import java.util.Collection;
import java.util.List;

public class TaskCollection<T extends Task,M extends Collection<T>>  extends Task{
    private M mTasks;

    public TaskCollection(M tasks){
        mTasks=tasks;
    }

    @Override
    protected Result onExecute(Task task, OnTaskUpdate callback) {
        return null;
    }

    //    @Override
//    protected Response onExecute(Task task, OnTaskUpdate update) {
//        Task unFinishTask=null;Task firstFetched=null;
//        Response result;
//        MapResult<Task, Response> mapResult=new MapResult<>();
//        while ((null!=(unFinishTask=getFirstUnFinish()))){
//            if (null!=firstFetched&&firstFetched==unFinishTask){
//                break;
//            }
//            if (null!=unFinishTask&&!unFinishTask.isExecuting()){
//                mapResult.put(unFinishTask,unFinishTask.onExecute(this,update));
//            }
//            firstFetched=null!=firstFetched?firstFetched:unFinishTask;
//        }
//        return null!=mapResult&&mapResult.size()>0?mapResult:null;
//    }

    public final boolean isAllFinish(){
        return null==getFirstUnFinish();
    }

    public final Task getFirstUnFinish(){
        return indexFirst((Object object)-> null!=object&&object instanceof
                Task&&!((Task)object).isFinished()?Matchable.MATCHED:Matchable.CONTINUE);
    }

    protected final M getTasks(){
        return mTasks;
    }

    public final boolean remove(T obj){
        M tasks=null!=obj?mTasks:null;
        if(null!=tasks){
            synchronized (tasks){
                return tasks.remove(obj);
            }
        }
        return false;
    }

    public final boolean add(T obj,boolean skipEqualed){
        M tasks=null!=obj?mTasks:null;
        if(null!=tasks){
            synchronized (tasks){
                if (!skipEqualed||!tasks.contains(obj)){
                    return tasks.add(obj);
                }
            }
        }
        return false;
    }

    public final Task getExecuting(){
        return indexFirst((Object object)->null!=object&&object instanceof Task&&((Task)object).isExecuting()?Matchable.MATCHED:Matchable.CONTINUE);
    }

    public final Task indexFirst(Matchable matchable){
        List<T> list=new Matcher().match(mTasks,matchable,1);
        return null!=list&&list.size()>0?list.get(0):null;
    }

    public final T index(Object obj){
        return index(obj,false);
    }

    public final T index(Object obj,boolean fullMatch){
        M tasks=null!=obj?mTasks:null;
        if(null!=tasks){
            synchronized (tasks){
                for (T child:tasks) {
                    if (null!=child&&(child==obj||(!fullMatch&&child.equals(obj)))){
                        return child;
                    }
                }
            }
        }
        return null;
    }

    public final int size(){
        Collection<T> tasks=mTasks;
        return null!=tasks?tasks.size():0;
    }

}
