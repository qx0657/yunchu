package fun.qianxiao.yunchu.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.hjq.base.BaseDialog;
import com.hjq.widget.action.StatusAction;
import com.hjq.widget.layout.StatusLayout;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseActivity;
import fun.qianxiao.yunchu.databinding.ActivityBrowserBinding;
import fun.qianxiao.yunchu.lunzige.aop.CheckNet;
import fun.qianxiao.yunchu.lunzige.dialog.MessageDialog;
import fun.qianxiao.yunchu.lunzige.widget.BrowserView;
import fun.qianxiao.yunchu.utils.ToastTool;

public class BrowserActivity extends BaseActivity<ActivityBrowserBinding>
        implements StatusAction, OnRefreshListener {

    private StatusLayout mStatusLayout;
    private ProgressBar mProgressBar;
    private SmartRefreshLayout mRefreshLayout;
    private BrowserView mBrowserView;

    @Override
    protected ActivityBrowserBinding getBinding() {
        return ActivityBrowserBinding.inflate(getLayoutInflater());
    }

    public static void start(Context context, String title, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void initListener() {
        mStatusLayout = findViewById(R.id.hl_browser_hint);
        mProgressBar = findViewById(R.id.pb_browser_progress);
        mRefreshLayout = findViewById(R.id.sl_browser_refresh);
        mBrowserView = findViewById(R.id.wv_browser_view);
        // 设置 WebView 生命管控
        mBrowserView.setLifecycleOwner(this);
        // 设置网页刷新监听
        mRefreshLayout.setOnRefreshListener(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initData() {
        setTitle("网页浏览");
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        if(!TextUtils.isEmpty(title)){
            setTitle(title);
        }

        showLoading();
        String url = intent.getStringExtra("url");

        mBrowserView.setBrowserViewClient(new MyBrowserViewClient());
        mBrowserView.setBrowserChromeClient(new MyBrowserChromeClient(mBrowserView));
        mBrowserView.loadUrl(url);
    }


    /**
     * 重新加载当前页
     */
    @CheckNet
    private void reload() {
        mBrowserView.reload();
    }

    /**
     * {@link OnRefreshListener}
     */

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        reload();
    }

    @Override
    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mBrowserView.canGoBack()) {
            // 后退网页并且拦截该事件
            mBrowserView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyBrowserViewClient extends BrowserView.BrowserViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 处理自定义scheme
            if (!url.startsWith("http")) {
                new MessageDialog.Builder(context)
                        .setMessage("网页请求打开"+url.split(":")[0]+"协议对应应用，是否继续？")
                        .setListener(new MessageDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog) {
                                try {
                                    // 以下固定写法
                                    final Intent intent = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(url));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    context.startActivity(intent);
                                } catch (Exception e) {
                                    // 防止没有安装的情况
                                    e.printStackTrace();
                                    ToastTool.error("没有安装对应应用");
                                }
                            }
                        })
                        .create()
                .show();

                return true;
            }
            return false;
        }

        /**
         * 网页加载错误时回调，这个方法会在 onPageFinished 之前调用
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // 这里为什么要用延迟呢？因为加载出错之后会先调用 onReceivedError 再调用 onPageFinished
            post(() -> showError(v -> reload()));
        }

        /**
         * 开始加载网页
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        /**
         * 完成加载网页
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.setVisibility(View.GONE);
            mRefreshLayout.finishRefresh();
            showComplete();
        }
    }

    private class MyBrowserChromeClient extends BrowserView.BrowserChromeClient {

        private MyBrowserChromeClient(BrowserView view) {
            super(view);
        }

        /**
         * 收到网页标题
         */
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (title != null) {
                //setTitle(title);
            }
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            if (icon != null) {
                //setRightIcon(new BitmapDrawable(getResources(), icon));
            }
        }

        /**
         * 收到加载进度变化
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_browser, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, mBrowserView.getTitle());
                intent.putExtra(Intent.EXTRA_TEXT, mBrowserView.getUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, "分享"));
                break;
            case R.id.menu_item_open_in_system_browser:
                Intent intent1 = new Intent();
                intent1.setAction("android.intent.action.VIEW");
                intent1.addCategory("android.intent.category.BROWSABLE");
                intent1.addCategory("android.intent.category.DEFAULT");
                Uri content_url = Uri.parse(mBrowserView.getUrl());
                intent1.setData(content_url);
                context.startActivity(intent1);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
