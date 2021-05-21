package fun.qianxiao.yunchu.ui.about;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.TimeUtils;

import fun.qianxiao.yunchu.BuildConfig;
import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseActivity;
import fun.qianxiao.yunchu.checkupdate.CheckUpdateManager;
import fun.qianxiao.yunchu.config.AppConfig;
import fun.qianxiao.yunchu.databinding.ActivityAboutBinding;
import fun.qianxiao.yunchu.ui.activity.BrowserActivity;
import fun.qianxiao.yunchu.ui.opensourcelicense.OpenSourceLicenseActivity;
import fun.qianxiao.yunchu.utils.ToastTool;

public class AboutActivity extends BaseActivity<ActivityAboutBinding> {

    @Override
    protected ActivityAboutBinding getBinding() {
        return ActivityAboutBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initListener() {
        binding.tvDeveloper.setOnClickListener(v -> {
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=1540223760";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        });

        //当前版本
        binding.llVersionAbout.setOnClickListener(v -> {
            ToastTool.show(AppUtils.getAppName()+".v."+AppUtils.getAppVersionName()+"_"+AppUtils.getAppVersionCode()+"\n" +
                    "Build Time: "+ BuildConfig.BUILD_TIME);
        });

        //检查更新
        binding.llCheckUpdateAbout.setOnClickListener(v -> new CheckUpdateManager(context).check(false));
        binding.llShareAbout.setOnClickListener(v -> {

        });
        binding.llJoinQqGroupAbout.setOnClickListener(v -> {
            Intent intent = new Intent();
            String key = "ZEFjBny5hkNaTiY7da1eWirqyvknZpbW";
            intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
            // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                startActivity(intent);
            } catch (Exception e) {
                // 未安装手Q或安装的版本不支持
                ToastTool.warning("未安装手Q或安装的版本不支持");
            }
        });
        binding.llPrivateAgreementAbout.setOnClickListener(v -> {
            Intent intent = new Intent(context, BrowserActivity.class);
            intent.putExtra("title", "隐私协议");
            intent.putExtra("url", AppConfig.PRIVACYAGREEMENT_URL);
            startActivity(intent);
        });
        binding.llUserAgreementAbout.setOnClickListener(v -> {
            Intent intent = new Intent(context, BrowserActivity.class);
            intent.putExtra("title", "用户协议");
            intent.putExtra("url", AppConfig.USERAGREEMENT_URL);
            startActivity(intent);
        });
        binding.llOpenSourceListenerAbout.setOnClickListener(v -> {
            //开源许可
            startActivity(new Intent(context, OpenSourceLicenseActivity.class));
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        setTitle("关于");
        //binding.tvVersion.setText("V."+ AppUtils.getAppVersionName());
        String year = TimeUtils.getNowString(TimeUtils.getSafeDateFormat("yyyy"));
        if(year.equals("2021")){
            binding.tvAboutCopyrightAboutActivity.setText("Copyright © 2021");
        }else{
            binding.tvAboutCopyrightAboutActivity.setText("Copyright © 2021 - "+year);
        }

        binding.tvNowVersionAbout.setText("V."+ AppUtils.getAppVersionName()+" ("+AppUtils.getAppVersionCode()+")");
    }
}
