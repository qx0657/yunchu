package fun.qianxiao.yunchu.model;

import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fun.qianxiao.yunchu.MyApplication;
import fun.qianxiao.yunchu.bean.DocumentBean;
import fun.qianxiao.yunchu.eventbus.EBRequestLogin;
import fun.qianxiao.yunchu.eventbus.EBSubTitleRefresh;
import fun.qianxiao.yunchu.net.ApiServiceManager;
import fun.qianxiao.yunchu.net.DocumentApi;
import fun.qianxiao.yunchu.net.ResponseBodyObserver;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.observers.ResourceObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class SearchModel {
    private Context context;
    private DocumentModel documentModel;
    int curPage = 1;
    boolean hasMore = true;
    List<DocumentBean> result = new ArrayList<>();

    public SearchModel(Context context) {
        this.context = context;
    }

    /**
     * 搜索
     * @param key
     * @param callback
     */
    public void search(String key, DocumentModel.GetDocumentListCallback callback){
        if(MyApplication.user == null){
            callback.getDocumentListError("请登录后使用");
            ThreadUtils.runOnUiThreadDelayed(()-> EventBus.getDefault().post(new EBRequestLogin(true)),750);
            return;
        }
        if(documentModel == null){
            documentModel = new DocumentModel(context);
        }
        result.clear();
        curPage = 1;
        hasMore = true;
        searchFuckYou(key, callback);

    }

    private void searchFuckYou(String key, DocumentModel.GetDocumentListCallback callback){
        ApiServiceManager.getInstance().create(DocumentApi.class)
                .listDocument(MyApplication.user.getUsername(), MyApplication.user.getToken(), curPage, 100, DocumentModel.SortName.UPDATE.value, DocumentModel.SortMethod.DESC.toString())
                .subscribeOn(Schedulers.io())               // 切换到IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  // 切换回到主线程 处理请求结果
                .subscribe(new ResponseBodyObserver() {
                    @Override
                    protected void onNext(JSONObject jsonObject) throws Exception {
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray items = data.getJSONArray("items");
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            String title = item.getString("title");
                            if(title.contains(key)){
                                DocumentBean bean = new DocumentBean();
                                bean.setId(item.getInt("id"));
                                bean.setTitle(title);
                                bean.setDesc(item.getString("desc"));
                                bean.setPassword(item.getString("password"));
                                bean.setCreate(TimeUtils.string2Date(item.getString("create")));
                                bean.setModify_time(item.getLong("modify_time"));
                                bean.setHtml(item.getInt("html")==1);
                                bean.setHide(item.getInt("hide")==1);
                                bean.setRead(item.getInt("read"));
                                bean.setModify(item.getInt("modify"));
                                bean.setHost(item.getString("url-text"));
                                result.add(bean);
                            }
                        }
                        LogUtils.i("轮询"+curPage,items.length()==100);
                        hasMore = items.length()==100;
                        if(hasMore){
                            curPage ++;
                            searchFuckYou(key, callback);
                        }else{
                            callback.getDocumentListSuccess(result, result.size());
                        }
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
}
