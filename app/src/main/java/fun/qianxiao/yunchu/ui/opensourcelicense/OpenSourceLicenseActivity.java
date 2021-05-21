package fun.qianxiao.yunchu.ui.opensourcelicense;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;

import java.util.Arrays;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseActivity;
import fun.qianxiao.yunchu.databinding.ActivityOpensourcelicenseBinding;
import fun.qianxiao.yunchu.ui.opensourcelicense.adapter.OpenSourceLicenseAdapter;
import fun.qianxiao.yunchu.ui.opensourcelicense.bean.OpenSourceLicense;


public class OpenSourceLicenseActivity extends BaseActivity<ActivityOpensourcelicenseBinding> {

    @Override
    protected ActivityOpensourcelicenseBinding getBinding() {
        return ActivityOpensourcelicenseBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        setTitle("开源许可");
        binding.rvOslActivity.setLayoutManager(new LinearLayoutManager(context));
        binding.rvOslActivity.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                //super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 0, 0, ConvertUtils.dp2px(4));
            }
        });
        binding.rvOslActivity.setAdapter(new OpenSourceLicenseAdapter(context, Arrays.asList(
                new OpenSourceLicense("OkHttp","square","An HTTP & HTTP/2 client for Android and java applications.","Apache 2.0","https://github.com/square/okhttp"),
                new OpenSourceLicense("Retrofit","square","A type-safe HTTP client for Android and Java.","Apache 2.0","https://github.com/square/retrofit"),
                new OpenSourceLicense("RxJava","ReactiveX","RxJava is a Java VM implementation of Reactive Extensions: a library for composing asynchronous and event-based programs by using observable sequences.","Apache 2.0","https://github.com/ReactiveX/RxJava"),
                new OpenSourceLicense("Glide","bumptech","Glide is a fast and efficient open source media management and image loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface.","BSD, part MIT and Apache 2.0","https://github.com/bumptech/glide"),
                new OpenSourceLicense("AndroidUtilCode","Blankj","AndroidUtilCode is a powerful & easy to use library for Android.","Apache 2.0","https://github.com/Blankj/AndroidUtilCode"),
                //new OpenSourceLicense("DKVideoPlayer","Doikki","A video player for Android.","Apache 2.0","https://github.com/Doikki/DKVideoPlayer"),
                new OpenSourceLicense("GSON","google","Gson is a Java library that can be used to convert Java Objects into their JSON representation. ","Apache 2.0","https://github.com/google/gson"),
                //new OpenSourceLicense("AgentWeb","Justson","AgentWeb 是一个基于的 Android WebView ，极度容易使用以及功能强大的库，提供了 Android WebView 一系列的问题解决方案 ，并且轻量和极度灵活","Apache 2.0","https://github.com/Justson/AgentWeb"),
                new OpenSourceLicense("EventBus","greenrobot","EventBus is a publish/subscribe event bus for Android and Java.","Apache 2.0","https://github.com/greenrobot/EventBus"),
                new OpenSourceLicense("EasyHttp","轮子哥","简单易用的网络框架","Apache 2.0","https://github.com/getActivity/EasyHttp")
        )));
    }
}
