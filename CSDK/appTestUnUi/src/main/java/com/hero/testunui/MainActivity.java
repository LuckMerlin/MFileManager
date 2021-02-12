package com.hero.testunui;
import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.csdk.api.bean.ChatBaseInfo;
import com.csdk.api.bean.ChatConfig;
import com.csdk.api.bean.Gender;
import com.csdk.api.common.Api;
import com.csdk.api.common.CSDK;
import com.csdk.api.common.CommonApi;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ActivityUtil().applyFullscreen(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CAMERA
            }, 34242);
        }

        connect("200001", "绿色无界");
    }

    private boolean connect(String roleId, String roleName) {
        String serverId="1";
        String level="1";
        //
        ChatConfig newConfig = new ChatConfig();
//        newConfig.setProductId("10000").setProductKey("1234567812345678")
        newConfig.setProductId("10008").setProductKey("1234567812345678")
//        newConfig.setProductId("10006").setProductKey("WaSdnmRec3VR9Y3q")
//        newConfig.setProductId("10005").setProductKey("FnIA6z95jjPhupRh")
//        newConfig.setProductId("10000").setProductKey("1234567812345678")//星球
//              .setSocketHost("10.0.6.200",3101).setHttpHost("http://10.0.6.200",3111);
              .setSocketHost("123.57.210.235",3101).setHttpHost("http://123.57.210.235",3111);
//              .setSocketHost("123.57.210.235",3101).setHttpHost("http://123.57.210.235",3111);
        roleName = null != roleName && roleName.length() > 0 ? roleName : ""+System.currentTimeMillis();
        CSDK.getInstance().setChatBaseInfo(new ChatBaseInfo().setRoleId(roleId).setRoleName(roleName).
                setServerId("1").setFriendshipVersion(1));
        CSDK.getInstance().init(this, newConfig);
        //
        ChatBaseInfo baseInfo=new ChatBaseInfo();
        Map<String, Object> maps=new HashMap<>();
        baseInfo.setExtra(maps);
        baseInfo.setRoleId(roleId).setRoleName(roleName).setServerId(serverId).setFriendshipVersion(1);
        baseInfo.setAvatarUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1779340462,2367921606&fm=26&gp=0.jpg");
        baseInfo.setRoleLevel(level);
        baseInfo.setGender(Integer.toString(System.currentTimeMillis()%2==0?Gender.FEMALE: Gender.MAN));
        baseInfo.setVipLevel(System.currentTimeMillis()%2==0?"vipa":"vipb");
        //
        CSDK.getInstance().setChatBaseInfo(baseInfo);
        //
        Handler handler= new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CSDK.getInstance().openChatUi(System.currentTimeMillis()%2==0);
            }
        }, 3000);
        return false;
    }
}
