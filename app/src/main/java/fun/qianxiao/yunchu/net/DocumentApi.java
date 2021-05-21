package fun.qianxiao.yunchu.net;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DocumentApi {

    /**
     * 获取文档
     * @param username
     * @param token
     * @param page
     * @param perpage
     * @param sortname
     * @param sort
     * @return
     */
    @POST("api/v1/text/textlist/")
    Observable<ResponseBody> listDocument(
            @Query("username") String username,
            @Query("token") String token,
            @Query("page") long page,
            @Query("perpage") int perpage,
            @Query("sortname") String sortname,
            @Query("sort") String sort
    );

    /**
     * 获取回收站文档
     * @param username
     * @param token
     * @param page
     * @param perpage
     * @param sortname
     * @param sort
     * @return
     */
    @POST("api/v1/text/textlist/")
    Observable<ResponseBody> listRecycleDocument(
            @Query("username") String username,
            @Query("token") String token,
            @Query("page") int page,
            @Query("perpage") int perpage,
            @Query("sortname") String sortname,
            @Query("sort") String sort,
            @Query("recycle") String recycle //recycle
    );

    /**
     * 新建
     * @param username
     * @param token
     * @param title
     * @param content
     * @param html
     * @param hide
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/text/new/")
    Observable<ResponseBody> create(
            @Query("username") String username,
            @Query("token") String token,
            @Field("title") String title,
            @Field("content") String content,
            @Field("html") int html,//0
            @Field("hide") int hide,//0
            @Field("password") String password//""
    );

    /**
     * 获取文档
     * @param username
     * @param token
     * @param id
     * @return
     */
    @POST("api/v1/text/update/")
    Observable<ResponseBody> get(
            @Query("username") String username,
            @Query("token") String token,
            @Query("id") long id
    );

    /**
     * 更新文档
     * @param username
     * @param token
     * @param id
     * @param title
     * @param content
     * @param html
     * @param hide
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/text/update/")
    Observable<ResponseBody> update(
            @Query("username") String username,
            @Query("token") String token,
            @Query("id") long id,
            @Field("title") String title,
            @Field("content") String content,
            @Field("html") int html,//0
            @Field("hide") int hide,//0
            @Field("password") String password//""
    );

    /**
     * 删除文档
     * @param username
     * @param token
     * @param id
     * @return
     */
    @POST("api/v1/text/delete/")
    Observable<ResponseBody> delete(
            @Query("username") String username,
            @Query("token") String token,
            @Query("id") long id
    );

    /**
     * 恢复文档
     * @param username
     * @param token
     * @param id
     * @param recycle
     * @return
     */
    @POST("api/v1/text/delete/")
    Observable<ResponseBody> restore(
            @Query("username") String username,
            @Query("token") String token,
            @Query("id") long id,
            @Query("recycle") String recycle //restore
    );

    /**
     * 生成/更新文档密钥
     * @param username
     * @param token
     * @param id
     * @param key
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/text/updatekey/")
    Observable<ResponseBody> updateKey(
            @Query("username") String username,
            @Query("token") String token,
            @Query("id") long id,
            @Field("key") String key //key
    );
}
