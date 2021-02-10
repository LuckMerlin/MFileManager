package com.csdk.server.http;

import com.csdk.api.bean.BaiduGpsBean;
import com.csdk.debug.Logger;
import com.csdk.server.data.Address;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Create LuckMerlin
 * Date 17:21 2020/9/8
 * TODO
 */
public final class LocationRequestor {

    public interface OnAddressDataUpdate{
        void onAddressDataUpdated(boolean succeed, String note, Address address);
    }

    public boolean requestLocation(double longitude,double latitude,OnAddressDataUpdate callback,String debug){
        HttpRequest httpRequest=new HttpRequest();
        Request request=httpRequest.buildGet("http://api.map.baidu.com/reverse_geocoding/v3/?output=json&coordtype=wgs84ll&location="+latitude+","+longitude+"&ak=8qlchRG4xUbw7iLPemfydGIRzNu066zN");
        if (null==request&&null!=callback){
            callback.onAddressDataUpdated(false,"Request invalid.",null);
            return false;
        }
        Logger.M("Request location data.","Request location data "+longitude+" "+latitude+(null!=debug?debug:"."));
        return null!=request&&httpRequest.callHttpRequest(request, new OnHttpFinish<BaiduGpsBean>() {
            @Override
            protected void onSyncFinish(boolean succeed, Call call, String note, BaiduGpsBean data) {
                super.onSyncFinish(succeed, call, note, data);
                if(null!=callback){
                    BaiduGpsBean.ResultBean resultBean=null!=data?data.getResult():null;
                    BaiduGpsBean.ResultBean.AddressComponentBean componentBean=null!=resultBean?resultBean.getAddressComponent():null;
                    String cityCode=null!=componentBean?componentBean.getAdcode():null;
                    if (null!=cityCode){
                        if (cityCode.length()==6){
                            cityCode=cityCode.substring(0,4)+"00";
                        }
                    }
                    Address address=new Address(longitude, latitude,cityCode,
                            null!=componentBean?componentBean.getCity():null,null!=componentBean?componentBean.getDistrict():null);
                    callback.onAddressDataUpdated(succeed,note,address);
                }
            }
        }, debug);
    }
}
