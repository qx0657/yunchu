package fun.qianxiao.yunchu.net;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VerifyCodeApi {

    /**
     * 获取验证码图形
     * @return
     */
    @POST("api/v1/user/imagecode/")
    Observable<ResponseBody> getCodeImage();

    /**
     * 验证验证码
     * @param token
     * @param code
     * @return
     */
    @POST("api/v1/user/imagecode/")
    Observable<ResponseBody> verifyCode(
            @Query("token") String token,
            @Query("code") String code
    );

    /**
     * 发送邮箱验证码(注册)
     * @param inviteCode
     * @param email
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/user/code/")
    Observable<ResponseBody> sendRegisterMailCode(
            @Field("invitationcode") String inviteCode,
            @Field("email") String email
    );

    /**
     * 发送用户名到邮箱
     * @param type
     * @param email
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/user/code/")
    Observable<ResponseBody> sendUsername2Mail(
            @Field("type") String type,//retrieve
            @Field("email") String email
    );

    /**
     * 发送找回密码验证码
     * @param type
     * @param email
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/user/code/")
    Observable<ResponseBody> sendForgetpwdMailCode(
            @Field("type") String type,//reset
            @Field("username") String username,
            @Field("email") String email
    );
}
