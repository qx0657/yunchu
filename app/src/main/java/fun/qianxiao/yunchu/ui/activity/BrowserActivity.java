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
        // ?????? WebView ????????????
        mBrowserView.setLifecycleOwner(this);
        // ????????????????????????
        mRefreshLayout.setOnRefreshListener(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initData() {
        setTitle("????????????");
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
     * ?????????????????????
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
            // ?????????????????????????????????
            mBrowserView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyBrowserViewClient extends BrowserView.BrowserViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // ???????????????scheme
            if (!url.startsWith("http")) {
                new MessageDialog.Builder(context)
                        .setMessage("??????????????????"+url.split(":")[0]+"????????????????????????????????????")
                        .setListener(new MessageDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog) {
                                try {
                                    // ??????????????????
                                    final Intent intent = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(url));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    context.startActivity(intent);
                                } catch (Exception e) {
                                    // ???????????????????????????
                                    e.printStackTrace();
                                    ToastTool.error("????????????????????????");
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
         * ???????????????????????????????????????????????? onPageFinished ????????????
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // ????????????????????????????????????????????????????????????????????? onReceivedError ????????? onPageFinished
            post(() -> showError(v -> reload()));
        }

        /**
         * ??????????????????
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        /**
         * ??????????????????
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
         * ??????????????????
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
         * ????????????????????????
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
                startActivity(Intent.createChooser(intent, "??????"));
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
