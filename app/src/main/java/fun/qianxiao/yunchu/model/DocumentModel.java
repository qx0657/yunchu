package fun.qianxiao.yunchu.model;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import fun.qianxiao.yunchu.MyApplication;
import fun.qianxiao.yunchu.bean.DocumentBean;
import fun.qianxiao.yunchu.eventbus.EBRequestLogin;
import fun.qianxiao.yunchu.eventbus.EBSubTitleRefresh;
import fun.qianxiao.yunchu.net.ApiServiceManager;
import fun.qianxiao.yunchu.net.DocumentApi;
import fun.qianxiao.yunchu.net.ResponseBodyObserver;
import fun.qianxiao.yunchu.ui.main.MainActivity;
import fun.qianxiao.yunchu.utils.MySpUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DocumentModel {
    private Context context;

    public DocumentModel(Context context) {
        this.context = context;
    }


    public enum  SortName{
        CREATE("create"),UPDATE("update"),TITLE("title"),BEIZHU("customid");

        String value;

        SortName(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum SortMethod{
        ASC("rise"),DESC("down");
        String value;

        SortMethod(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public interface GetDocumentListCallback{
        void getDocumentListSuccess(List<DocumentBean> list, int total);
        void getDocumentListError(String e);
    }

    /**
     * 获取文档列表
     * @param page
     * @param per_page
     * @param sortname title/create/customid
     * @param sort rise/down
     * @param callback
     */
    public void getDocumentList(long page, int per_page, SortName sortname, SortMethod sort,
                                GetDocumentListCallback callback){
        if(MyApplication.user == null){
            callback.getDocumentListError("请登录后使用");
            ThreadUtils.runOnUiThreadDelayed(()->EventBus.getDefault().post(new EBRequestLogin(true)),750);
            return;
        }
        ApiServiceManager.getInstance().create(DocumentApi.class)
                .listDocument(MyApplication.user.getUsername(), MyApplication.user.getToken(), page, per_page, sortname.toString(), sort.toString())
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        JSONObject data = jsonObject.getJSONObject("data");
                        int total = 0;
                        if(!data.isNull("total")){
                            total = data.getInt("total");
                        }
                        String yiyan = data.optString("word");
                        if(!TextUtils.isEmpty(yiyan)){
                            EventBus.getDefault().post(new EBSubTitleRefresh(true, yiyan));
                        }
                        JSONArray items = data.getJSONArray("items");
                        List<DocumentBean> list = new ArrayList<>();
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            DocumentBean bean = new DocumentBean();
                            bean.setId(item.getInt("id"));
                            bean.setTitle(item.getString("title"));
                            bean.setDesc(item.getString("desc"));
                            bean.setPassword(item.getString("password"));
                            bean.setCreate(TimeUtils.string2Date(item.getString("create")));
                            bean.setModify_time(item.getLong("modify_time"));
                            bean.setHtml(item.getInt("html")==1);
                            bean.setHide(item.getInt("hide")==1);
                            bean.setRead(item.getInt("read"));
                            bean.setModify(item.getInt("modify"));
                            bean.setHost(item.getString("url-text"));
                            list.add(bean);
                        }
                        callback.getDocumentListSuccess(list, total);
                    }

                    @Override
                    protected void onError(String e) {
                        callback.getDocumentListError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*
    获取回收站文档
     */
    public void getRecycleDocumentList(int page, int per_page, SortName sortname, SortMethod sort,
                                       GetDocumentListCallback callback){
        if(MyApplication.user == null){
            callback.getDocumentListError("请登录后使用");
            ThreadUtils.runOnUiThreadDelayed(()->EventBus.getDefault().post(new EBRequestLogin(true)),750);
            return;
        }
        ApiServiceManager.getInstance().create(DocumentApi.class)
                .listRecycleDocument(MyApplication.user.getUsername(), MyApplication.user.getToken(), page, per_page, sortname.toString(), sort.toString(), "recycle")
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        JSONObject data = jsonObject.getJSONObject("data");
                        int total = 0;
                        if(!data.isNull("total")){
                            total = data.getInt("total");
                        }
                        JSONArray items = data.getJSONArray("items");
                        List<DocumentBean> list = new ArrayList<>();
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            DocumentBean bean = new DocumentBean();
                            bean.setId(item.getInt("id"));
                            bean.setTitle(item.getString("title"));
                            bean.setDesc(item.getString("desc"));
                            bean.setPassword(item.getString("password"));
                            bean.setCreate(TimeUtils.string2Date(item.getString("create")));
                            bean.setModify_time(item.getLong("modify_time"));
                            bean.setHtml(item.getInt("html")==1);
                            bean.setHide(item.getInt("hide")==1);
                            bean.setRead(item.getInt("read"));
                            bean.setModify(item.getInt("modify"));
                            bean.setHost(item.getString("url-text"));
                            list.add(bean);
                        }
                        //排序 根据删除时间（modify_time）
                        Collections.sort(list, (o1, o2) -> {
                            if(o1.getModify_time()>o2.getModify_time()){
                                return 1;
                            }
                            if(o1.getModify_time()<o2.getModify_time()){
                                return -1;
                            }
                            return 0;
                        });
                        callback.getDocumentListSuccess(list, total);
                    }

                    @Override
                    protected void onError(String e) {
                        callback.getDocumentListError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*
    创建文档
     */
    public void createDocument(String title, String content, boolean html, boolean hide, String password,
                               OperateListener listener){
        if(MyApplication.user == null){
            listener.operateError("请登录后使用");
            ((Activity)context).finish();
            ThreadUtils.runOnUiThreadDelayed(()->EventBus.getDefault().post(new EBRequestLogin(true)),750);
            return;
        }
        ApiServiceManager.getInstance().create(DocumentApi.class)
                .create(MyApplication.user.getUsername(), MyApplication.user.getToken(), title, content, html?1:0, hide?1:0, password)
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        listener.operateSuccess(jsonObject.getString("id"));
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

    public interface GetDocumentListener{
        void getDocumentSuccess(DocumentBean documentBean);
        void getDocumentError(String e);
    }

    /**
     * 获取文档
     * @param id
     * @param listener
     */
    public void getDocument(long id, GetDocumentListener listener){
        if(MyApplication.user == null){
            listener.getDocumentError("请登录后使用");
            ThreadUtils.runOnUiThreadDelayed(()->EventBus.getDefault().post(new EBRequestLogin(true)),750);
            return;
        }
        ApiServiceManager.getInstance().create(DocumentApi.class)
                .get(MyApplication.user.getUsername(), MyApplication.user.getToken(), id)
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        JSONObject item = jsonObject.getJSONObject("data");
                        DocumentBean bean = new DocumentBean();
                        bean.setId(item.getInt("id"));
                        bean.setTitle(item.getString("title"));
                        bean.setContent(item.getString("content"));
                        bean.setPassword(item.getString("password"));
                        bean.setKey(item.getString("key"));
                        bean.setCreate(TimeUtils.string2Date(item.getString("date")));
                        bean.setModify_time(item.getLong("updatetime"));
                        bean.setHtml(item.getInt("html")==1);
                        bean.setHide(item.getInt("hide")==1);
                        bean.setRead(item.getInt("read"));
                        bean.setModify(item.getInt("modify"));
                        listener.getDocumentSuccess(bean);
                    }

                    @Override
                    protected void onError(String e) {
                        listener.getDocumentError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*
    更新文档
     */
    public void update(long id, String title, String content, boolean html, boolean hide, String password,
                       OperateListener listener){
        if(MyApplication.user == null){
            listener.operateError("请登录后使用");
            ((Activity)context).finish();
            ThreadUtils.runOnUiThreadDelayed(()->EventBus.getDefault().post(new EBRequestLogin(true)),750);
            return;
        }
        ApiServiceManager.getInstance().create(DocumentApi.class)
                .update(MyApplication.user.getUsername(), MyApplication.user.getToken(), id, title, content, html?1:0, hide?1:0, password)
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
    删除文档
     */
    public void delete(long id, OperateListener listener){
        if(MyApplication.user == null){
            listener.operateError("请登录后使用");
            ThreadUtils.runOnUiThreadDelayed(()->EventBus.getDefault().post(new EBRequestLogin(true)),750);
            return;
        }
        ApiServiceManager.getInstance().create(DocumentApi.class)
                .delete(MyApplication.user.getUsername(), MyApplication.user.getToken(), id)
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
    恢复文档
     */
    public void restore(long id, OperateListener listener){
        if(MyApplication.user == null){
            listener.operateError("请登录后使用");
            ((Activity)context).finish();
            ThreadUtils.runOnUiThreadDelayed(()->EventBus.getDefault().post(new EBRequestLogin(true)),750);
            return;
        }
        ApiServiceManager.getInstance().create(DocumentApi.class)
                .restore(MyApplication.user.getUsername(), MyApplication.user.getToken(), id, "restore")
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
    生成、更新文档密钥
     */
    public void updateKey(long id, OperateListener listener){
        if(MyApplication.user == null){
            listener.operateError("请登录后使用");
            ((Activity)context).finish();
            ThreadUtils.runOnUiThreadDelayed(()->EventBus.getDefault().post(new EBRequestLogin(true)),750);
            return;
        }
        ApiServiceManager.getInstance().create(DocumentApi.class)
                .updateKey(MyApplication.user.getUsername(), MyApplication.user.getToken(), id, "true")
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
}
