package fun.qianxiao.yunchu.net;

import android.app.Application;
import android.content.Intent;

import com.blankj.utilcode.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import fun.qianxiao.yunchu.eventbus.EBLoginExpire;
import fun.qianxiao.yunchu.lunzige.manager.ActivityManager;
import fun.qianxiao.yunchu.utils.ToastTool;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import okhttp3.ResponseBody;

public abstract class ResponseBodyObserver implements Observer<ResponseBody> {
    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull ResponseBody responseBody) {
        try{
            String res = responseBody.string();
            //LogUtils.i(res);
            JSONObject jsonObject = new JSONObject(res);
            if(jsonObject.getInt("state")==200){
                onNext(jsonObject);
            }else if(jsonObject.getInt("state")==229||jsonObject.getInt("state")==228){
                // 登录过期
                onError("登录已过期，正在重新登录");
                // ……
                EventBus.getDefault().post(new EBLoginExpire(true));
            }else{
                onError(new Throwable(jsonObject.getString("msg")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(e);
        }
    }

    protected abstract void onNext(JSONObject jsonObject) throws Exception;

    @Override
    public void onError(@NonNull Throwable e) {
        onComplete();
        LogUtils.e(e.toString());
        onError(e.getMessage()==null?e.toString():e.getMessage());
    }

    protected abstract void onError(String e);
}
