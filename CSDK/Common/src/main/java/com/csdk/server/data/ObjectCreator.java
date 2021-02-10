package com.csdk.server.data;
import java.lang.reflect.Constructor;

/**
 * Create LuckMerlin
 * Date 20:39 2021/1/19
 * TODO
 */
public class ObjectCreator {

    public <T> T generate(Class<T> cls,Object object){
        T instance=null!=cls?create(cls):null;
        if (null!=instance&&instance instanceof JsonObject&&((JsonObject)instance).apply(object)){
            return instance;
        }
        return null;
    }

    public <T> T create(Class<T> cls){
        if (null==cls){
            return null;
        }
        Constructor[] constructors=cls.getDeclaredConstructors();
        if (null!=constructors&&constructors.length>0){
            for (Constructor child:constructors) {
                if (null==child){
                    continue;
                }
                boolean accessible=child.isAccessible();
                T instance=null;
                try {
                    child.setAccessible(true);
                    Class[] types=child.getParameterTypes();
                    int length=null!=types?types.length:-1;
                    if (length<=0){//None parameter
                        instance= (T)child.newInstance();
                    }else{
                        Object[] args=new Object[length];
                        Object value=null;
                        for (int i = 0; i < length; i++) {
                            Class type=types[i];
                            if (null==(value=(null!=type?generateTypeDefault(type):null))){
                                args=null;
                                break;
                            }
                            args[i]=value;
                        }
                        instance= null!=args&&args.length==types.length?(T)child.newInstance(args):null;
                    }
                }catch (Exception e){
                    //Do nothing
                }finally {
                    child.setAccessible(accessible);
                }
                if (null!=instance){
                    return instance;
                }
            }
        }
        return null;
    }

    private Object generateTypeDefault(Class type){
        if (null==type){
            return null;
        }else if (type.equals(int.class)){
            return 0;
        }else if (type.equals(float.class)){
            return 0f;
        }else  if (type.equals(double.class)){
            return 0.0;
        }else if (type.equals(char.class)){
            return " ".charAt(0);
        }else if (type.equals(long.class)){
            return 0L;
        }else if (type.equals(boolean.class)){
            return false;
        }else if (type.equals(short.class)){
            return (short)0;
        }else if (type.equals(byte.class)){
            return (byte)0;
        }
        return null;
    }
}
