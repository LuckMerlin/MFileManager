package com.csdk.ui.model;

import android.content.Context;

import com.csdk.api.common.Api;
import com.csdk.api.core.Label;
import com.csdk.api.core.OnEventChange;
import com.csdk.api.data.Json;
import com.csdk.api.struct.SimpleStruct;
import com.csdk.api.struct.Struct;
import com.csdk.api.struct.StructArray;
import com.csdk.api.ui.Model;
import com.csdk.ui.R;
import com.csdk.ui.adapter.SystemMessageListAdapter;
import com.csdk.ui.data.SystemMessage;

/**
 * Create LuckMerlin
 * Date 13:18 2021/2/2
 * TODO
 */
public class HomeSystemModel extends Model implements OnEventChange {
    private final SystemMessageListAdapter mAdapter=new SystemMessageListAdapter();

    public HomeSystemModel(Api api) {
        super(api);
        mAdapter.add(new SystemMessage("系统", new StructArray().add(new SimpleStruct(Struct.TYPE_TEXT,"获得 ",null)).
                add(new SimpleStruct(Struct.TYPE_TEXT,"金币  ",new Json().putSafe(Label.LABEL_COLOR,"#FF4AE725"))).
                add(new SimpleStruct(Struct.TYPE_TEXT," x5",null)).getStructSpannableStringBuilder(null)));
        mAdapter.add(new SystemMessage("系统", new StructArray().add(new SimpleStruct(Struct.TYPE_LINK,
                "彭博社26日报道的数据显示，全球56个国家和地区接种了6800多万剂疫苗，接种率最高的国家是以色列—— 每100人接种了42.9剂；美国完成了2350万剂疫苗接种。 但即便如此，供不应求仍然几乎是各国的现状。在美国，新泽西州和纽约市26日均表示，目前接收到的新冠肺 炎疫苗根本无法满足当地居民的接种需求。新泽西州不得不关闭接种站点，而纽约市长表示目前纽约市甚至“几 乎没有供应”来制定新的新冠疫苗接种预约。  ",
                new Json().putSafe(Label.LABEL_COLOR,"#FFFF8E66"))).getStructSpannableStringBuilder(null)));
        mAdapter.add(new SystemMessage("系统", new StructArray().add(new SimpleStruct(Struct.TYPE_TEXT,"获得 ",null)).
                add(new SimpleStruct(Struct.TYPE_TEXT,"金币  ",new Json().putSafe(Label.LABEL_COLOR,"#FF4AE725"))).
                add(new SimpleStruct(Struct.TYPE_TEXT," x25",null)).getStructSpannableStringBuilder(null)));
        mAdapter.add(new SystemMessage("系统", new StructArray().add(new SimpleStruct(Struct.TYPE_TEXT,"获得 ",null)).
                add(new SimpleStruct(Struct.TYPE_TEXT,"金币  ",new Json().putSafe(Label.LABEL_COLOR,"#FF4AE725"))).
                add(new SimpleStruct(Struct.TYPE_TEXT," x35",null)).getStructSpannableStringBuilder(null)));
        mAdapter.add(new SystemMessage("系统", new StructArray().add(new SimpleStruct(Struct.TYPE_TEXT,"获得 ",null)).
                add(new SimpleStruct(Struct.TYPE_TEXT,"金币  ",new Json().putSafe(Label.LABEL_COLOR,"#FF4AE725"))).
                add(new SimpleStruct(Struct.TYPE_TEXT," x45",null)).getStructSpannableStringBuilder(null)));
    }

    @Override
    public void onEventChanged(int event, Object arg) {

    }

    @Override
    public Object onResolveModelView(Context context) {
        return R.layout.csdk_home_system_model;
    }

    public SystemMessageListAdapter getListAdapter() {
        return mAdapter;
    }
}
