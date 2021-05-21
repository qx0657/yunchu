package fun.qianxiao.yunchu.model;

import android.graphics.Bitmap;
import android.util.Base64;

import com.blankj.utilcode.util.ImageUtils;

import org.json.JSONObject;

import fun.qianxiao.yunchu.config.AppConfig;
import fun.qianxiao.yunchu.net.ApiServiceManager;
import fun.qianxiao.yunchu.net.ResponseBodyObserver;
import fun.qianxiao.yunchu.net.VerifyCodeApi;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class VerifyCodeModel {
    public interface GetCodeImageListener{
        void getCodeImageSuccess(Bitmap bitmap, String token);
        void getCodeImageError(String e);
    }
    /*
    获取图片验证码图片
     */
    public void getCodeImage(GetCodeImageListener listener){
        ApiServiceManager.getInstance().create(VerifyCodeApi.class)
                .getCodeImage()
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        JSONObject data = jsonObject.getJSONObject("data");
                        listener.getCodeImageSuccess(
                                ImageUtils.bytes2Bitmap(Base64.decode(data.getString("image"), Base64.DEFAULT)),
                                data.getString("token"));
                    }

                    @Override
                    protected void onError(String e) {
                        listener.getCodeImageError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*
    验证图片验证码
     */
    public void veriftImageCode(String token, String code, OperateListener listener){
        ApiServiceManager.getInstance().create(VerifyCodeApi.class)
                .verifyCode(token, code)
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
    发送注册邮箱验证码（使用软件内设定邀请码）
     */
    public void sendRegisterMailCode(String email, OperateListener listener){
        ApiServiceManager.getInstance().create(VerifyCodeApi.class)
                .sendRegisterMailCode(AppConfig.INVITE_CODE, email)
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        listener.operateSuccess(jsonObject.getString("token"));
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
    发送用户名到用户邮箱
     */
    public void sendUsername2mail(String email, OperateListener listener){
        ApiServiceManager.getInstance().create(VerifyCodeApi.class)
                .sendUsername2Mail("retrieve", email)
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
    忘记密码时邮箱验证码
     */
    public void sendForgetPwdMailCode(String username, String email, OperateListener listener){
        ApiServiceManager.getInstance().create(VerifyCodeApi.class)
                .sendForgetpwdMailCode("reset", username, email)
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        listener.operateSuccess(jsonObject.getString("token"));
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
