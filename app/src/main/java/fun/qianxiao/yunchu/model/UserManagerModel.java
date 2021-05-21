package fun.qianxiao.yunchu.model;

import com.blankj.utilcode.util.RegexUtils;

import org.json.JSONObject;

import fun.qianxiao.yunchu.bean.User;
import fun.qianxiao.yunchu.config.AppConfig;
import fun.qianxiao.yunchu.net.ApiServiceManager;
import fun.qianxiao.yunchu.net.ResponseBodyObserver;
import fun.qianxiao.yunchu.net.UserServiceApi;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class UserManagerModel {
    public interface OnLoginListener{
        void loginSuccess(User user);
        void loginError(String e);
    }
    /*
    账户登录
     */
    public void login(String uid, String pwd,
                      OnLoginListener listener){
        UserServiceApi userServiceApi = ApiServiceManager.getInstance().create(UserServiceApi.class);
        Observable<ResponseBody> observable;
        if(RegexUtils.isEmail(uid)){
            observable = userServiceApi.loginEmail(uid,pwd, AppConfig.LOGIN_EXPIRE_TIME);
        }else{
            observable = userServiceApi.login(uid,pwd, AppConfig.LOGIN_EXPIRE_TIME);
        }
        observable.subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        JSONObject data = jsonObject.getJSONObject("data");
                        User user = new User(data.getString("username"),data.getString("token"));
                        listener.loginSuccess(user);
                    }

                    @Override
                    protected void onError(String e) {
                        listener.loginError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public interface OnRegisterListener{
        void registerSuccess(String uid, String pwd);
        void registerError(String e);
    }
    /*
    账户注册
     */
    public void register(String token, String uid, String pwd, String email, String code, String mobile, String qq,
                         OnRegisterListener listener){
        ApiServiceManager.getInstance().create(UserServiceApi.class)
                .register(token, uid, pwd, AppConfig.INVITE_CODE, email, code, mobile, qq)
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        listener.registerSuccess(uid, pwd);
                    }

                    @Override
                    protected void onError(String e) {
                        listener.registerError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*
    退出登录
     */
    public void logout(User user, OperateListener listener){
        ApiServiceManager.getInstance().create(UserServiceApi.class)
                .logout(user.getUsername(), user.getToken())
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
    重置密码
     */
    public void resetPwd(String token, String username, String email, String code,
                         OperateListener listener){
        ApiServiceManager.getInstance().create(UserServiceApi.class)
                .resetPassword(token, username, email, code)
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
