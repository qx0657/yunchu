package fun.qianxiao.yunchu.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fun.qianxiao.yunchu.MyApplication;
import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseActivity;
import fun.qianxiao.yunchu.bean.User;
import fun.qianxiao.yunchu.checkupdate.CheckUpdateManager;
import fun.qianxiao.yunchu.config.AppConfig;
import fun.qianxiao.yunchu.databinding.ActivityMainBinding;
import fun.qianxiao.yunchu.databinding.ActivityMainRealBinding;
import fun.qianxiao.yunchu.eventbus.EBLoginExpire;
import fun.qianxiao.yunchu.eventbus.EBRequestLogin;
import fun.qianxiao.yunchu.eventbus.EBTheme;
import fun.qianxiao.yunchu.fragment.app.ApplicationFragment;
import fun.qianxiao.yunchu.fragment.doc.view.LoginDialogFragment;
import fun.qianxiao.yunchu.fragment.pic.PictureFragment;
import fun.qianxiao.yunchu.lunzige.manager.ActivityManager;
import fun.qianxiao.yunchu.lunzige.widget.BrowserView;
import fun.qianxiao.yunchu.lunzige.widget.SmartBallPulseFooter;
import fun.qianxiao.yunchu.model.OperateListener;
import fun.qianxiao.yunchu.model.UserManagerModel;
import fun.qianxiao.yunchu.ui.about.AboutActivity;
import fun.qianxiao.yunchu.ui.main.adapter.Viewpager2Adapter;
import fun.qianxiao.yunchu.fragment.doc.DocumentFragment;
import fun.qianxiao.yunchu.fragment.file.FileFragment;
import fun.qianxiao.yunchu.ui.recyclebin.RecycleBinActivity;
import fun.qianxiao.yunchu.ui.setting.SettingActivity;
import fun.qianxiao.yunchu.ui.userinfo.UserInfoActivity;
import fun.qianxiao.yunchu.utils.MySpUtils;
import fun.qianxiao.yunchu.utils.ToastTool;
import fun.qianxiao.yunchu.view.loading.ILoadingView;
import fun.qianxiao.yunchu.view.loading.MyLoadingDialog;
import fun.qianxiao.yunchu.widget.ScrollListenerWebView;

public class MainActivity extends BaseActivity<ActivityMainRealBinding> implements ILoadingView {
    private List<Fragment> mFragments;
    private Viewpager2Adapter mAdapter;
    private ActivityMainBinding mainBinding;
    private MyLoadingDialog loadingDialog;
    private final int RECYCLE_BIN_REQUEST_CODE = 201;

    @Override
    protected ActivityMainRealBinding getBinding() {
        ActivityMainRealBinding activityMainRealBinding = ActivityMainRealBinding.inflate(getLayoutInflater());
        mainBinding = activityMainRealBinding.layoutAppbarmain;
        return activityMainRealBinding;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void initListener() {
        mainBinding.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_doc:
                    mainBinding.viewPager2.setCurrentItem(0, false);
                    break;
                case R.id.navigation_file:
                    mainBinding.viewPager2.setCurrentItem(1, false);
                    break;
                case R.id.navigation_pic:
                    mainBinding.viewPager2.setCurrentItem(2, false);
                    break;
                case R.id.navigation_app:
                    mainBinding.viewPager2.setCurrentItem(3, false);
                    break;
                default:
                    break;
            }
            return true;
        });
        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.drawer_menu_userinfo:
                    if(MyApplication.user==null){
                        Login(false);
                    }else{
                        //ToastTool.show("已登录"+MyApplication.user.getUsername());
                        startActivity(new Intent(context, UserInfoActivity.class));
                    }
                    return true;
                case R.id.drawer_menu_recycle_bin:
                    if(MyApplication.user==null){
                        ToastTool.error("请登录后使用");
                        Login(false);
                    }else{
                        startActivityForResult(new Intent(context, RecycleBinActivity.class), RECYCLE_BIN_REQUEST_CODE);
                    }
                    return true;
                case R.id.drawer_menu_loginout:
                    new androidx.appcompat.app.AlertDialog.Builder(context)
                            .setTitle("提示")
                            .setMessage("确认退出登录？")
                            .setPositiveButton("确定", (dialog, which) -> {
                                LoginOut();
                                binding.drawerLayout.closeDrawers();
                            })
                            .setNegativeButton("取消",null)
                            .show();
                    return true;
                case R.id.drawer_menu_setting:
                    startActivity(new Intent(context, SettingActivity.class));
                    return true;
                case R.id.drawer_menu_about:
                    startActivity(new Intent(context, AboutActivity.class));
                    return true;
                default:
                    return false;
            }
        });
    }

    public void LoginSuccess(User user, boolean flag){
        MyApplication.user = user;
        MenuItem infoMenuItem = binding.navView.getMenu().findItem(R.id.drawer_menu_userinfo);
        binding.navView.getMenu().setGroupVisible(R.id.group_menu3,true);
        binding.navView.getMenu().findItem(R.id.drawer_menu_recycle_bin).setVisible(true);
        infoMenuItem.setTitle(user.getUsername());
        if(flag){
            ((DocumentFragment)mFragments.get(0)).ifNotRefreshThenRefresh();
        }
    }

    public void Login(boolean autologin) {
        new LoginDialogFragment(autologin)
                .setOnLoginClickListener(new UserManagerModel.OnLoginListener() {
                    @Override
                    public void loginSuccess(User user) {
                        //登录成功
                        ToastTool.success("欢迎 "+user.getUsername());
                        MySpUtils.save("username", user.getUsername());
                        MySpUtils.save("token", user.getToken());
                        LoginSuccess(user, true);
                    }

                    @Override
                    public void loginError(String e) {

                    }
                }).show(getSupportFragmentManager(),"LoginDialogFragment");
    }

    private void loginOutSuccess(){
        MyApplication.user = null;
        MySpUtils.remove("username");
        MySpUtils.remove("token");
        binding.navView.getMenu().findItem(R.id.drawer_menu_userinfo).setTitle("立即登录");
        binding.navView.getMenu().setGroupVisible(R.id.group_menu3,false);
        binding.navView.getMenu().findItem(R.id.drawer_menu_recycle_bin).setVisible(false);

        //通知HomeFragment清空列表
        ((DocumentFragment)mFragments.get(0)).clearDataList();
    }

    private void LoginOut(){
        openLoadingDialog("正在退出");
        new UserManagerModel().logout(MyApplication.user, new OperateListener() {
            @Override
            public void operateSuccess(String var1) {
                closeLoadingDialog();
                loginOutSuccess();
            }

            @Override
            public void operateError(String e) {
                closeLoadingDialog();
                loginOutSuccess();
                ToastTool.error(e);
            }
        });
    }

    boolean isReadComplete1,isReadComplete2;

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        //状态栏透明
        BarUtils.transparentStatusBar(this);
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, getColorPrimaryId()));
        //左侧侧滑栏下移状态栏距离
        BarUtils.addMarginTopEqualStatusBarHeight(binding.navView);

        View headerView = binding.navView.getHeaderView(0);
        mainBinding.ivMenuMain.setOnClickListener(v -> {
            if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawers();
            }else {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mFragments = new ArrayList<>(Arrays.asList(
                new DocumentFragment(),
                new FileFragment(),
                new PictureFragment(),
                new ApplicationFragment()
        ));
        mAdapter = new Viewpager2Adapter(this, mFragments);
        mainBinding.viewPager2.setAdapter(mAdapter);
        mainBinding.viewPager2.setOffscreenPageLimit(mFragments.size());
        mainBinding.viewPager2.setUserInputEnabled(false);
        mainBinding.viewPager2.setCurrentItem(0,false);


        //检查更新
        new CheckUpdateManager(context).check(true);

        if(!MySpUtils.getBoolean("tipagreeysxy")){
            ScrollListenerWebView webView = new ScrollListenerWebView(context);
            webView.setLayoutParams(new ViewGroup.LayoutParams(-1,-2));
            webView.loadUrl(AppConfig.PRIVACYAGREEMENT_URL);
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setView(webView)
                    .setCancelable(false)
                    .setPositiveButton("同意", null)
                    .setNegativeButton("拒绝", (dialog, which) -> AppUtils.exitApp())
                    .show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                if(isReadComplete1){
                    if(!isReadComplete2){
                        if(!webView.getUrl().equals(AppConfig.USERAGREEMENT_URL)){
                            webView.loadUrl(AppConfig.USERAGREEMENT_URL);
                            webView.scrollTo(0,0);
                        }
                        ToastTool.warning("请阅读完成用户协议");
                    }else{
                        MySpUtils.save("tipagreeysxy", true);
                        alertDialog.dismiss();
                        Login(false);
                    }
                }else{
                    ToastTool.warning("请阅读完成隐私政策");
                }
            });
            webView.setBrowserViewClient(new BrowserView.BrowserViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    ThreadUtils.runOnUiThreadDelayed(()->{

                        LogUtils.i(webView.getContentHeight() * webView.getScale(), webView.getHeight());
                        if(Math.abs(webView.getContentHeight() * webView.getScale()-webView.getHeight())<100){
                            LogUtils.i(url);
                            if(url.equals(AppConfig.PRIVACYAGREEMENT_URL)){
                                isReadComplete1 = true;
                            }else if(url.equals(AppConfig.USERAGREEMENT_URL)){
                                isReadComplete2 = true;
                            }
                        }
                    },500);
                }
            });

            webView.setOnCustomScroolChangeListener((l, t, oldl, oldt) -> {
                //WebView的总高度
                float webViewContentHeight=webView.getContentHeight() * webView.getScale();
                //WebView的现高度
                float webViewCurrentHeight=(webView.getHeight() + webView.getScrollY());
                if (Math.abs(webViewContentHeight-webViewCurrentHeight) < 100) {
                    if(webView.getUrl().equals(AppConfig.PRIVACYAGREEMENT_URL)){
                        isReadComplete1 = true;
                    }else if(webView.getUrl().equals(AppConfig.USERAGREEMENT_URL)){
                        isReadComplete2 = true;
                    }
                    ToastTool.success("已阅读完成");
                }else{
                    LogUtils.i(webViewContentHeight,webViewCurrentHeight);
                }
            });
        }

        // 设置全局的 Header 构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) ->
                new MaterialHeader(context).setColorSchemeColors(ContextCompat.getColor(context, getColorPrimaryId())));
        // 设置全局的 Footer 构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new SmartBallPulseFooter(context));
        // 设置全局初始化器
        SmartRefreshLayout.setDefaultRefreshInitializer((context, layout) -> {
            // 刷新头部是否跟随内容偏移
            layout.setEnableHeaderTranslationContent(true)
                    // 刷新尾部是否跟随内容偏移
                    .setEnableFooterTranslationContent(true)
                    // 加载更多是否跟随内容偏移
                    .setEnableFooterFollowWhenNoMoreData(true)
                    // 内容不满一页时是否可以上拉加载更多
                    .setEnableLoadMoreWhenContentNotFull(false)
                    // 仿苹果越界效果开关
                    .setEnableOverScrollDrag(false);
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeTheme(EBTheme ebTheme) {
        if (ebTheme.isSuccess()) {
            recreate();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginExpire(EBLoginExpire ebLoginExpire) {
        if (ebLoginExpire.isSuccess()) {
            loginOutSuccess();
            //重新登录
            Login(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RequestLogin(EBRequestLogin ebRequestLogin){
        if(ebRequestLogin.isSuccess()){
            Login(false);
            ActivityManager.getInstance().finishAllActivities(MainActivity.class);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RECYCLE_BIN_REQUEST_CODE:
                if(resultCode == Activity.RESULT_OK){
                    ((DocumentFragment)mFragments.get(0)).ifNotRefreshThenRefresh();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void openLoadingDialog(String msg) {
        if(loadingDialog == null){
            loadingDialog = new MyLoadingDialog(context);
        }
        if(!loadingDialog.isShowing()){
            loadingDialog.setMessage(msg);
            loadingDialog.show();
        }else{
            loadingDialog.updateMessage(msg);
        }
    }

    @Override
    public void closeLoadingDialog() {
        if(loadingDialog!=null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }
}