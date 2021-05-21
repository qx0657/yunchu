package fun.qianxiao.yunchu.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import fun.qianxiao.yunchu.MyApplication;
import fun.qianxiao.yunchu.bean.UserInfo;
import fun.qianxiao.yunchu.net.ApiServiceManager;
import fun.qianxiao.yunchu.net.ResponseBodyObserver;
import fun.qianxiao.yunchu.net.UserInfoApi;
import fun.qianxiao.yunchu.utils.MySpUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserInfoModel {
    public interface GetUserInfoListener{
        void getUserInfoSuccess(UserInfo userInfo);
        void getUserInfoError(String e);
    }
    /*
    获取用户信息
     */
    public void getUserInfo(GetUserInfoListener listener){
        ApiServiceManager.getInstance().create(UserInfoApi.class)
                .getUserInfo(MyApplication.user.getUsername(), MyApplication.user.getToken())
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        JSONObject data = jsonObject.getJSONObject("data");
                        UserInfo userInfo = new GsonBuilder()
                                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                                .create().fromJson(data.toString(),new TypeToken<UserInfo>(){}.getType());
                        listener.getUserInfoSuccess(userInfo);
                    }

                    @Override
                    protected void onError(String e) {
                        listener.getUserInfoError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*
    重置用户key
     */
    public void resetUserAppKey(OperateListener listener){
        ApiServiceManager.getInstance().create(UserInfoApi.class)
                .updateUserKey(MyApplication.user.getUsername(), MyApplication.user.getToken(), "true")
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        listener.operateSuccess(jsonObject.getString("key"));
                    }

                    @Override
                    protected void onError(String e) {
                        listener.operateError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*
    修改密码
     */
    public void resetUserPwd(String newPwd, OperateListener listener){
        ApiServiceManager.getInstance().create(UserInfoApi.class)
                .resetPassword(MyApplication.user.getUsername(), MyApplication.user.getToken(), MySpUtils.getString("login_pwd"), newPwd)
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        listener.operateSuccess("");
                    }

                    @Override
                    protected void onError(String e) {
                        listener.operateError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*
    注销用户
     */
    public void destory(OperateListener listener){
        ApiServiceManager.getInstance().create(UserInfoApi.class)
                .destory(MyApplication.user.getUsername(), MyApplication.user.getToken(), "true")
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        listener.operateSuccess("");
                    }

                    @Override
                    protected void onError(String e) {
                        listener.operateError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
