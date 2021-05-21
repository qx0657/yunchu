package fun.qianxiao.yunchu.fragment.doc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hjq.base.BaseAdapter;
import com.hjq.base.BaseDialog;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import fun.qianxiao.yunchu.MyApplication;
import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseFragment;
import fun.qianxiao.yunchu.bean.DocumentBean;
import fun.qianxiao.yunchu.bean.User;
import fun.qianxiao.yunchu.databinding.FragmentDocBinding;
import fun.qianxiao.yunchu.eventbus.EBCreateDocumentSuccess;
import fun.qianxiao.yunchu.eventbus.EBSubTitleRefresh;
import fun.qianxiao.yunchu.fragment.doc.adapter.DocumentAdapter;
import fun.qianxiao.yunchu.lunzige.dialog.InputDialog;
import fun.qianxiao.yunchu.model.DocumentModel;
import fun.qianxiao.yunchu.model.OperateListener;
import fun.qianxiao.yunchu.model.SearchModel;
import fun.qianxiao.yunchu.ui.addoredit.AddEditActivity;
import fun.qianxiao.yunchu.ui.main.MainActivity;
import fun.qianxiao.yunchu.utils.MySpUtils;
import fun.qianxiao.yunchu.utils.ToastTool;

public class DocumentFragment extends BaseFragment<FragmentDocBinding>
        implements OnRefreshLoadMoreListener,BaseAdapter.OnItemClickListener,BaseAdapter.OnItemLongClickListener {
    private DocumentAdapter adapter;
    private DocumentModel documentModel;

    private final int EDIT_ACTIVITY_REQUEST_CODE = 101;

    protected int curPage = 1;
    protected final int PER_PAGE = MySpUtils.getInt("each_page_size");
    private DocumentModel.SortName curSortName = DocumentModel.SortName.UPDATE;
    private DocumentModel.SortMethod curSortMethod = DocumentModel.SortMethod.DESC;

    @Override
    protected FragmentDocBinding getBinding() {
        return FragmentDocBinding.inflate(getLayoutInflater());
    }

    boolean hasSearchSuccess;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void initListener() {
        binding.smartRefreshLayoutHome.setOnRefreshLoadMoreListener(this);
        binding.fbAddDocumentHome.setOnClickListener(v -> {
            if(MyApplication.user == null){
                ToastTool.error("请登录后使用");
                ((MainActivity)context).Login(false);
            }else{
                startActivity(new Intent(context, AddEditActivity.class));
            }
        });
        binding.toolBar.inflateMenu(R.menu.menu_home_fragment);
        SearchView searchView = (SearchView) binding.toolBar.getMenu().findItem(R.id.app_bar_search).getActionView();
        searchView.setQueryHint("请输入标题关键字搜索");
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(hasSearchSuccess){
                    hasSearchSuccess = false;
                    ifNotRefreshThenRefresh();
                }
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(TextUtils.isEmpty(query.trim())){
                    searchView.setQuery("", false);
                    ToastTool.error("请输入内容后搜索");
                    return false;
                }
                ((MainActivity)context).openLoadingDialog("正在搜索");
                new SearchModel(context).search(query, new DocumentModel.GetDocumentListCallback() {
                    @Override
                    public void getDocumentListSuccess(List<DocumentBean> list, int total) {
                        ((MainActivity)context).closeLoadingDialog();
                        if(total != 0){
                            hasSearchSuccess = true;
                            ToastTool.success("搜索到"+total+"个结果");
                            assert adapter.getData() != null;
                            adapter.getData().clear();
                            adapter.setData(list);
                        }else{
                            ToastTool.warning("搜索结果为空");
                        }
                    }

                    @Override
                    public void getDocumentListError(String e) {
                        ((MainActivity)context).closeLoadingDialog();
                        ToastTool.error(e);
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setCursorVisible(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            searchAutoComplete.setTextCursorDrawable(R.drawable.cursor_white);
        }else{
            try {
                Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
                f.setAccessible(true);
                f.set(searchAutoComplete, R.drawable.cursor_white);
            } catch (Exception ignored) {
            }
        }
        binding.toolBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_item_edit_by_id:
                    new InputDialog.Builder(context)
                            .setInputType(InputType.TYPE_CLASS_NUMBER)
                            .setHint("请输入您的文档ID")
                            .setContent("")
                            .setAutoDismiss(false)
                            .setListener(new InputDialog.OnListener() {
                                @Override
                                public void onConfirm(BaseDialog dialog, String content) {
                                    if(!TextUtils.isEmpty(content)){
                                        long id = Integer.parseInt(content);
                                        dialog.dismiss();
                                        startActivityForResult(new Intent(context, AddEditActivity.class)
                                                .putExtra("id", id), EDIT_ACTIVITY_REQUEST_CODE);
                                    }else{
                                        ToastTool.warning("请输入您的文档ID");
                                    }
                                }

                                @Override
                                public void onCancel(BaseDialog dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    return true;
                case R.id.menu_item_sort_by_title_rise_home:
                    curSortName = DocumentModel.SortName.TITLE;
                    curSortMethod = DocumentModel.SortMethod.ASC;
                    binding.smartRefreshLayoutHome.autoRefresh();
                    break;
                case R.id.menu_item_sort_by_title_down_home:
                    curSortName = DocumentModel.SortName.TITLE;
                    curSortMethod = DocumentModel.SortMethod.DESC;
                    binding.smartRefreshLayoutHome.autoRefresh();
                    break;
                case R.id.menu_item_sort_by_update_rise_home:
                    curSortName = DocumentModel.SortName.UPDATE;
                    curSortMethod = DocumentModel.SortMethod.ASC;
                    binding.smartRefreshLayoutHome.autoRefresh();
                    break;
                case R.id.menu_item_sort_by_update_down_home:
                    curSortName = DocumentModel.SortName.UPDATE;
                    curSortMethod = DocumentModel.SortMethod.DESC;
                    binding.smartRefreshLayoutHome.autoRefresh();
                case R.id.menu_item_sort_by_create_rise_home:
                    curSortName = DocumentModel.SortName.CREATE;
                    curSortMethod = DocumentModel.SortMethod.ASC;
                    binding.smartRefreshLayoutHome.autoRefresh();
                    break;
                case R.id.menu_item_sort_by_create_down_home:
                    curSortName = DocumentModel.SortName.CREATE;
                    curSortMethod = DocumentModel.SortMethod.DESC;
                    binding.smartRefreshLayoutHome.autoRefresh();
                    break;
            }
            return true;
        });
    }

    private void adapterDataObserverOnChanged(){
        if(MyApplication.user == null){
            return;
        }
        if(adapter.getItemCount()==0){
            binding.rvDocumentHome.setVisibility(View.GONE);
            binding.getRoot().findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        }else{
            binding.rvDocumentHome.setVisibility(View.VISIBLE);
            binding.getRoot().findViewById(R.id.empty_view).setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        binding.toolBar.setTitle("云储文档");
        binding.toolBar.setTitleMarginStart(ConvertUtils.dp2px(64));

        EventBus.getDefault().register(this);
        binding.rvDocumentHome.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.inset_recyclerview_divider)));
        binding.rvDocumentHome.addItemDecoration(dividerItemDecoration);
        adapter = new DocumentAdapter(context);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                //super.onChanged();
                adapterDataObserverOnChanged();
            }
        });

        adapter.setOnItemClickListener(this::onItemClick);
        adapter.setOnItemLongClickListener(this::onItemLongClick);
        binding.rvDocumentHome.setAdapter(adapter);
        binding.rvDocumentHome.setItemAnimator(new DefaultItemAnimator());

        if(MySpUtils.getBoolean("tipagreeysxy")){
            if(MySpUtils.contain("username")&&MySpUtils.contain("token")){
                User user = new User(MySpUtils.getString("username"), MySpUtils.getString("token"));
                MyApplication.user = user;
                binding.smartRefreshLayoutHome.autoRefresh();
                ((MainActivity) Objects.requireNonNull(getActivity())).LoginSuccess(user,false);
            }else{
                ((MainActivity) Objects.requireNonNull(getActivity())).Login(false);
            }
        }

        binding.smartRefreshLayoutHome.setEnableLoadMoreWhenContentNotFull(true);

    }

    public void ifNotRefreshThenRefresh(){
        if(MyApplication.user!=null&&binding!=null&&binding.smartRefreshLayoutHome!=null&&!binding.smartRefreshLayoutHome.isRefreshing()){
            binding.smartRefreshLayoutHome.autoRefresh();
        }
    }

    public void clearDataList(){
        adapter.clearData();
    }

    private boolean isFirstLoadData;

    public void LoadData(){
        if(documentModel==null){
            documentModel = new DocumentModel(context);
        }
        documentModel.getDocumentList(curPage, PER_PAGE, curSortName, curSortMethod, new DocumentModel.GetDocumentListCallback() {
            @Override
            public void getDocumentListSuccess(List<DocumentBean> list, int total) {
                binding.smartRefreshLayoutHome.finishLoadMore();
                binding.smartRefreshLayoutHome.finishRefresh();
                if(curPage == 1){
                    if(adapter.getData()!=null){
                        adapter.getData().clear();
                    }
                    adapter.setData(list);
                }else{
                    adapter.addData(list);
                }
                adapter.setLastPage(list.size() < PER_PAGE);
                if(!isFirstLoadData){
                    isFirstLoadData = true;
                    binding.smartRefreshLayoutHome.setNoMoreData(false);
                }else{
                    binding.smartRefreshLayoutHome.setNoMoreData(adapter.isLastPage());
                }
            }

            @Override
            public void getDocumentListError(String e) {
                binding.smartRefreshLayoutHome.finishLoadMore();
                binding.smartRefreshLayoutHome.finishRefresh();
                ToastTool.error(e);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void createDocumentSuccess(EBCreateDocumentSuccess ebCreateDocumentSuccess){
        if(ebCreateDocumentSuccess.isSuccess()){
            adapter.addItem(ebCreateDocumentSuccess.getDocumentBean());
        }
    }

    private boolean isInitSubtitle;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setSubTitle(EBSubTitleRefresh ebSubTitleRefresh){
        if (ebSubTitleRefresh.isSuccess()){
            if(MySpUtils.contain("display_yiyan")&&!MySpUtils.getBoolean("display_yiyan")){
                return;
            }
            binding.toolBar.setSubtitle(ebSubTitleRefresh.getText());
            if(!isInitSubtitle){
                isInitSubtitle = true;
                try {
                    Field f = Toolbar.class.getDeclaredField("mSubtitleTextView");
                    f.setAccessible(true);
                    TextView mSubtitleTextView = (TextView) f.get(binding.toolBar);
                    assert mSubtitleTextView != null;
                    mSubtitleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    mSubtitleTextView.setSelected(true);
                    f.set(binding.toolBar, mSubtitleTextView);
                } catch (Exception ignored) {
                }
            }
        }
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
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        DocumentBean documentBean = adapter.getItem(position);
        startActivityForResult(new Intent(context, AddEditActivity.class).putExtra("id", documentBean.getId()), EDIT_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case EDIT_ACTIVITY_REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK){
                    binding.smartRefreshLayoutHome.autoRefresh();
                }
                break;
            default:
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onItemLongClick(RecyclerView recyclerView, View itemView, int position) {
        DocumentBean documentBean = adapter.getItem(position);
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(documentBean.getId()+" "+documentBean.getTitle())
                .setNeutralButton("删除", (dialog, which) -> {
                    ((MainActivity) Objects.requireNonNull(getActivity())).openLoadingDialog("正在删除");
                    new DocumentModel(context).delete(documentBean.getId(), new OperateListener() {
                        @Override
                        public void operateSuccess(String var1) {
                            ((MainActivity) Objects.requireNonNull(getActivity())).closeLoadingDialog();
                            ToastTool.success("删除成功");
                            adapter.removeItem(position);

                            ThreadUtils.runOnUiThreadDelayed(()-> adapterDataObserverOnChanged(),200);
                        }

                        @Override
                        public void operateError(String e) {
                            ((MainActivity) Objects.requireNonNull(getActivity())).closeLoadingDialog();
                            ToastTool.error(e);
                        }
                    });
                })
                .setPositiveButton("复制", null)
                .show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(context, alertDialog.getButton(AlertDialog.BUTTON_POSITIVE));
            popupMenu.inflate(R.menu.menu_copy);
            popupMenu.setOnMenuItemClickListener(item -> {
                String copy_text = documentBean.getHost() + documentBean.getId();
                switch (item.getItemId()){
                    case R.id.menu_item_copy_id:
                        copy_text = String.valueOf(documentBean.getId());
                        ToastTool.success("文档ID已复制到剪贴板");
                        break;
                    case R.id.menu_item_copy_html:
                        copy_text += ".html";
                        ToastTool.success("HTML链接已复制到剪贴板");
                        break;
                    case R.id.menu_item_copy_json:
                        copy_text += ".json";
                        ToastTool.success("JSON链接已复制到剪贴板");
                        break;
                    case R.id.menu_item_copy_md5:
                        copy_text += ".md5";
                        ToastTool.success("MD5链接已复制到剪贴板");
                        break;
                    case R.id.menu_item_copy_css:
                        copy_text += ".css";
                        ToastTool.success("CSS链接已复制到剪贴板");
                        break;
                    case R.id.menu_item_copy_js:
                        copy_text += ".js";
                        ToastTool.success("JS链接已复制到剪贴板");
                        break;
                    default:
                        break;
                }
                ClipboardUtils.copyText(copy_text);
                alertDialog.dismiss();
                return true;
            });
            popupMenu.show();
        });
        return true;
    }
}
