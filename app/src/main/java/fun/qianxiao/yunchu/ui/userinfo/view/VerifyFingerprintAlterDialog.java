package fun.qianxiao.yunchu.ui.userinfo.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.blankj.utilcode.util.ScreenUtils;

import fun.qianxiao.yunchu.base.BaseActivity;
import fun.qianxiao.yunchu.base.BaseAlertDialog;
import fun.qianxiao.yunchu.databinding.DialogVerifyfingerprintBinding;
import fun.qianxiao.yunchu.ui.userinfo.utils.OnVerifyResult;
import fun.qianxiao.yunchu.ui.userinfo.utils.fingerprint.FingerprintManagerUtil;


/**
 * 指纹验证
 * Create by QianXiao
 * On 2020/6/22
 */
public class VerifyFingerprintAlterDialog extends BaseAlertDialog<DialogVerifyfingerprintBinding> {
    private TextView tv_tip_vfd;
    private ImageView iv_verifyfingerpaint_dailog;
    private OnVerifyResult onVerifyResult;

    public VerifyFingerprintAlterDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected DialogVerifyfingerprintBinding getBinging() {
        return DialogVerifyfingerprintBinding.inflate(getLayoutInflater());
    }

    public OnVerifyResult getOnVerifyResult() {
        return onVerifyResult;
    }

    public VerifyFingerprintAlterDialog setOnVerifyResult(OnVerifyResult onVerifyResult) {
        this.onVerifyResult = onVerifyResult;
        return this;
    }

    @Override
    protected void initListener() {
        tv_tip_vfd = binding.tvTipVfd;
        iv_verifyfingerpaint_dailog = binding.ivVerifyfingerpaintDailog;
    }

    @Override
    protected void initData() {

    }

    @Override
    public void show(){
        super.show();
        FingerprintManagerUtil.startFingerprinterVerification(context, new FingerprintManagerUtil.FingerprintListener() {
            @Override
            public void onNonsupport() {
                onVerifyResult.verifyFail(VerifyFingerprintAlterDialog.this,"设备不支持指纹");
                tv_tip_vfd.setText("设备不支持指纹");
            }

            @Override
            public void onEnrollFailed() {
                onVerifyResult.verifyFail(VerifyFingerprintAlterDialog.this,"设备没有录入指纹");
                tv_tip_vfd.setText("设备没有录入指纹");
                iv_verifyfingerpaint_dailog.setColorFilter(Color.parseColor("#C0C0C0"));
            }

            @Override
            public void onAuthenticationStart() {
                tv_tip_vfd.setText("请放置指纹");
                //Color.parseColor("#90EE90")
                iv_verifyfingerpaint_dailog.setColorFilter(ContextCompat.getColor(context,((BaseActivity)context).getColorPrimaryId()));
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                onVerifyResult.verifySuccess(VerifyFingerprintAlterDialog.this);
                iv_verifyfingerpaint_dailog.setColorFilter(Color.parseColor("#7FFF00"));
                tv_tip_vfd.setText("验证成功");
            }

            @Override
            public void onAuthenticationFailed() {
                onVerifyResult.verifyFail(VerifyFingerprintAlterDialog.this,"指纹验证失败");
                iv_verifyfingerpaint_dailog.setColorFilter(Color.parseColor("#FF4500"));
                tv_tip_vfd.setText("指纹验证失败");
            }

            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                //onVerifyResult.verifyFail(VerifyFingerprintAlterDialog.this, "Error "+errString.toString());
                iv_verifyfingerpaint_dailog.setColorFilter(Color.parseColor("#FFA500"));
                tv_tip_vfd.setText(errString.toString());
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                //onVerifyResult.verifyFail(VerifyFingerprintAlterDialog.this, "Help "+helpString.toString());
                iv_verifyfingerpaint_dailog.setColorFilter(Color.parseColor("#FFA500"));
                tv_tip_vfd.setText(helpString.toString());
            }
        });
    }

    @Override
    public void dismiss(){
        FingerprintManagerUtil.cancel();
        onVerifyResult.verifyCancle(this);
        super.dismiss();
    }

    public void superdismiss(){
        super.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        assert window != null;
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (ScreenUtils.getAppScreenWidth() * 0.8);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }
}
