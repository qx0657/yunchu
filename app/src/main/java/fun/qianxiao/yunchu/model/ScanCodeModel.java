package fun.qianxiao.yunchu.model;

import android.util.Base64;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fun.qianxiao.yunchu.MyApplication;
import fun.qianxiao.yunchu.net.ApiServiceManager;
import fun.qianxiao.yunchu.net.ResponseBodyObserver;
import fun.qianxiao.yunchu.net.ScanCodeApi;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanCodeModel {

    public interface YunchuScanCallback{
        void scanSuccess(String data);
        void scanError(String e);
    }

    public void scan(String url, YunchuScanCallback callback){
        first(url, callback);
    }

    private void first(String url, YunchuScanCallback callback){
        ApiServiceManager.getInstance().create(ScanCodeApi.class)
                .first(url)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try{
                            assert response.body() != null;
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if(jsonObject.getInt("state")==200){
                                String base64 = jsonObject.getJSONObject("data").getString("data");
                                String text = ConvertUtils.bytes2String(Base64.decode(base64, Base64.DEFAULT));
                                JSONObject res = new JSONObject(text);
                                String state = res.getString("state");
                                String data = res.getString("data");
                                second(state, data, callback);
                            }else{
                                onFailure(call, new Throwable(jsonObject.getString("msg")));
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            onFailure(call, e);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        callback.scanError(t.toString());
                    }
                });
    }

    private void second(String state, String data, YunchuScanCallback callback){
        ApiServiceManager.getInstance().create(ScanCodeApi.class)
                .qrState(MyApplication.user.getUsername(), MyApplication.user.getToken(), state)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        if(jsonObject.getInt("state")==200){
                            callback.scanSuccess(data);
                        }else{
                            callback.scanError(jsonObject.getString("msg"));
                        }
                    }

                    @Override
                    protected void onError(String e) {
                        callback.scanError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void confirm(String data, YunchuScanCallback callback){
        ApiServiceManager.getInstance().create(ScanCodeApi.class)
                .qrData(MyApplication.user.getUsername(), MyApplication.user.getToken(), data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        if(jsonObject.getInt("state")==200){
                            callback.scanSuccess(data);
                        }else{
                            callback.scanError(jsonObject.getString("msg"));
                        }
                    }

                    @Override
                    protected void onError(String e) {
                        callback.scanError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
