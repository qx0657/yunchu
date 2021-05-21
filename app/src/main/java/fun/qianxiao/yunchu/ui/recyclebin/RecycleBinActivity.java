package fun.qianxiao.yunchu.ui.recyclebin;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ThreadUtils;
import com.hjq.base.BaseAdapter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseActivity;
import fun.qianxiao.yunchu.bean.DocumentBean;
import fun.qianxiao.yunchu.databinding.ActivityRecycleBinBinding;
import fun.qianxiao.yunchu.model.DocumentModel;
import fun.qianxiao.yunchu.model.OperateListener;
import fun.qianxiao.yunchu.ui.recyclebin.adapter.RecycleDocumentAdapter;
import fun.qianxiao.yunchu.utils.MySpUtils;
import fun.qianxiao.yunchu.utils.ToastTool;
import fun.qianxiao.yunchu.view.loading.ILoadingView;
import fun.qianxiao.yunchu.view.loading.MyLoadingDialog;

public class RecycleBinActivity extends BaseActivity<ActivityRecycleBinBinding>
        implements OnRefreshLoadMoreListener, BaseAdapter.OnItemClickListener, ILoadingView{
    private RecycleDocumentAdapter adapter;
    private DocumentModel documentModel;
    protected int curPage = 1;
    protected final int PER_PAGE = MySpUtils.getInt("each_page_size");
    private DocumentModel.SortName curSortName = DocumentModel.SortName.UPDATE;
    private DocumentModel.SortMethod curSortMethod = DocumentModel.SortMethod.DESC;

    private MyLoadingDialog loadingDialog;

    @Override
    protected ActivityRecycleBinBinding getBinding() {
        return ActivityRecycleBinBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initListener() {
        binding.smartRefreshLayoutRecycle.setOnRefreshLoadMoreListener(this);
    }

    public void adapterDataObserverOnChanged(){
        if(adapter.getItemCount()==0){
            binding.rvRecycleBin.setVisibility(View.GONE);
            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        }else{
            binding.rvRecycleBin.setVisibility(View.VISIBLE);
            findViewById(R.id.empty_view).setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        setTitle("回收站");
        binding.rvRecycleBin.setLayoutManager(new LinearLayoutManager(context));
        adapter = new RecycleDocumentAdapter(context);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.inset_recyclerview_divider)));
        binding.rvRecycleBin.addItemDecoration(dividerItemDecoration);
        adapter.setOnItemClickListener(this::onItemClick);
        binding.rvRecycleBin.setAdapter(adapter);
        binding.rvRecycleBin.setItemAnimator(new DefaultItemAnimator());

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                //super.onChanged();
                adapterDataObserverOnChanged();
            }
        });

        binding.smartRefreshLayoutRecycle.autoRefresh();
    }

    private boolean isFirstLoadData;

    private void LoadData(){
        if(documentModel==null){
            documentModel = new DocumentModel(context);
        }
        new DocumentModel(context).getRecycleDocumentList(curPage, PER_PAGE, curSortName, curSortMethod,
                new DocumentModel.GetDocumentListCallback() {
                    @Override
                    public void getDocumentListSuccess(List<DocumentBean> list, int total) {
                        binding.smartRefreshLayoutRecycle.finishLoadMore();
                        binding.smartRefreshLayoutRecycle.finishRefresh();
                        if(curPage == 1){
                            if(adapter.getData()!=null){
                                adapter.getData().clear();
                            }
                            adapter.setData(list);
                            if(list.size()==0){
                                binding.smartRefreshLayoutRecycle.finishRefreshWithNoMoreData();
                            }
                        }else{
                            adapter.addData(list);
                            if(list.size() < PER_PAGE){
                                binding.smartRefreshLayoutRecycle.finishLoadMoreWithNoMoreData();
                            }
                        }
                        adapter.setLastPage(list.size() < PER_PAGE);
                        if(!isFirstLoadData){
                            isFirstLoadData = true;
                            binding.smartRefreshLayoutRecycle.setNoMoreData(false);
                        }else{
                            binding.smartRefreshLayoutRecycle.setNoMoreData(adapter.isLastPage());
                        }
                    }

                    @Override
                    public void getDocumentListError(String e) {
                        binding.smartRefreshLayoutRecycle.finishLoadMore();
                        binding.smartRefreshLayoutRecycle.finishRefresh();
                        ToastTool.error(e);
                    }
                });
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        DocumentBean documentBean = adapter.getItem(position);
        new AlertDialog.Builder(context)
                .setTitle(documentBean.getId()+" "+documentBean.getTitle())
                .setMessage("是否恢复此文档？")
                .setPositiveButton("恢复", (dialog, which) -> {
                    openLoadingDialog("正在恢复");
                    new DocumentModel(context).restore(documentBean.getId(),
                            new OperateListener() {
                                @Override
                                public void operateSuccess(String var1) {
                                    closeLoadingDialog();
                                    ToastTool.success("恢复成功");
                                    adapter.removeItem(position);
                                    setResult(Activity.RESULT_OK);
                                    ThreadUtils.runOnUiThreadDelayed(()-> adapterDataObserverOnChanged(),200);
                                }

                                @Override
                                public void operateError(String e) {
                                    closeLoadingDialog();
                                    ToastTool.error(e);

                                }
                            });
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
        curPage++;
        LoadData();
    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
        curPage = 1;
        LoadData();
    }

    @Override
    public void openLoadingDialog(String msg) {
        if(loadingDialog == null){
            loadingDialog = new MyLoadingDialog(context);
        }
        if(!loadingDialog.isShowing()){
            loadingDialog.setMessage(msg);
            loadingDialog.show();
        }
    }

    @Override
    public void closeLoadingDialog() {
        if(loadingDialog!=null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }
}
