package fun.qianxiao.yunchu.ui.setting;

import android.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.CompoundButton;

import com.blankj.utilcode.util.ThreadUtils;
import com.hjq.base.BaseDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseActivity;
import fun.qianxiao.yunchu.config.AppConfig;
import fun.qianxiao.yunchu.databinding.ActivitySettingBinding;
import fun.qianxiao.yunchu.eventbus.EBTheme;
import fun.qianxiao.yunchu.lunzige.dialog.InputDialog;
import fun.qianxiao.yunchu.utils.CacheDataManager;
import fun.qianxiao.yunchu.utils.MySpUtils;
import fun.qianxiao.yunchu.utils.ToastTool;

public class SettingActivity extends BaseActivity<ActivitySettingBinding> {
    @Override
    protected ActivitySettingBinding getBinding() {
        return ActivitySettingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initListener() {
        //主题
        int themeIndex = MySpUtils.getInt("theme");
        AtomicInteger selectThemeIndex = new AtomicInteger(themeIndex);
        binding.sbChangeTheme.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("选择主题")
                    .setSingleChoiceItems(getResources().getStringArray(R.array.themes), themeIndex, (dialog, which) -> {
                        selectThemeIndex.set(which);
                    })
                    .setPositiveButton("确定", (dialog, which) -> {
                        if (selectThemeIndex.get() != themeIndex) {
                            MySpUtils.save("theme", selectThemeIndex.get());
                            recreate();
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {////优化一下，防止卡顿
                                    EventBus.getDefault().post(new EBTheme(true));
                                }
                            }, 500);
                        }

                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
        //清除缓存
        binding.sbCleanCache.setOnClickListener(v -> {
            ThreadUtils.executeBySingle(new ThreadUtils.Task<Object>() {
                @Override
                public Object doInBackground() throws Throwable {
                    CacheDataManager.clearAllCache(context);
                    return null;
                }

                @Override
                public void onSuccess(Object result) {
                    ToastTool.success("缓存已清空");
                    // 重新获取应用缓存大小
                    binding.sbCleanCache.setRightText(CacheDataManager.getTotalCacheSize(context));
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onFail(Throwable t) {

                }
            });
        });
        //每页数据
        binding.sbEachPageSizeSetting.setOnClickListener(v -> {
            int each_page_size = 50;
            if(MySpUtils.contain("each_page_size")){
                each_page_size = MySpUtils.getInt("each_page_size");
            }
            new InputDialog.Builder(context)
                    .setContent(String.valueOf(each_page_size))
                    .setHint("每页显示数据条数")
                    .setAutoDismiss(false)
                    .setInputType(InputType.TYPE_CLASS_NUMBER)
                    .setListener(new InputDialog.OnListener() {
                        @Override
                        public void onConfirm(BaseDialog dialog, String content) {
                            if(!TextUtils.isEmpty(content)){
                                if(Integer.parseInt(content)<1){
                                    ToastTool.warning("最小每页1条");
                                    return;
                                }
                                if(Integer.parseInt(content)>100){
                                    ToastTool.warning("最大每页100条");
                                    return;
                                }
                                dialog.dismiss();
                                MySpUtils.save("each_page_size",Integer.parseInt(content));
                                binding.sbEachPageSizeSetting.setRightText(content+" 条");
                                ToastTool.show("设置成功，下次启动软件生效");
                            }else {
                                ToastTool.warning("请输入每页显示数据条数");
                            }
                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        });
        //显示一言
        binding.scDisplayYiyanSetting.setOnCheckedChangeListener((buttonView, isChecked) -> MySpUtils.save("display_yiyan", isChecked));
    }

    @Override
    protected void initData() {
        setTitle("设置");
        int themeIndex = MySpUtils.getInt("theme");
        binding.actvThemeTextSetting.setText(getResources().getStringArray(R.array.themes)[themeIndex]);
        binding.sbEachPageSizeSetting.setRightText((MySpUtils.getInt("each_page_size")==0?50:MySpUtils.getInt("each_page_size"))+
                " 条");
        if(MySpUtils.contain("display_yiyan")){
            binding.scDisplayYiyanSetting.setChecked(MySpUtils.getBoolean("display_yiyan"));
        }else{
            binding.scDisplayYiyanSetting.setChecked(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.sbCleanCache.setRightText(CacheDataManager.getTotalCacheSize(context));
    }
}
