package fun.qianxiao.yunchu.fragment.doc.view;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ThreadUtils;

import java.util.Timer;
import java.util.TimerTask;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseDialogFragment;
import fun.qianxiao.yunchu.databinding.DialogfragmentJubaoBinding;
import fun.qianxiao.yunchu.model.JuBaoModel;
import fun.qianxiao.yunchu.utils.ToastTool;

public class JuBaoDialogFragment extends BaseDialogFragment<DialogfragmentJubaoBinding> {

    @Override
    protected DialogfragmentJubaoBinding getBinging() {
        return DialogfragmentJubaoBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        binding.etJubaoContentDf.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                binding.rbJubaoCategory0Df.setChecked(true);
            }
        });
        binding.rbJubaoCategory0Df.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.rgJubaoCategoryDf.clearCheck();
                    binding.etJubaoContentDf.requestFocus();
                }
            }
        });
        binding.rgJubaoCategoryDf.setOnCheckedChangeListener((group, checkedId) -> {
            binding.rbJubaoCategory0Df.setChecked(false);
            binding.etJubaoContentDf.setText("");
            binding.etJubaoContentDf.clearFocus();
        });
        binding.brnJubaoSubmitDf.setOnClickListener(v -> {
            String id = binding.tieJubaoIdDf.getText().toString().trim();
            if(TextUtils.isEmpty(id)){
                ToastTool.warning("请输入要举报的文档ID");
                binding.tieJubaoIdDf.setText("");
                binding.tilJubaoIdDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.brnJubaoSubmitDf.showError(2000);
                binding.tieJubaoIdDf.requestFocus();
                return;
            }
            JuBaoModel.JuBaoType juBaoType = JuBaoModel.JuBaoType.UNSELECT;
            switch (binding.rgJubaoCategoryDf.getCheckedRadioButtonId()){
                case R.id.rb_jubao_category1_df:
                    juBaoType = JuBaoModel.JuBaoType.ZHENGZHIMINGAN;
                    break;
                case R.id.rb_jubao_category2_df:
                    juBaoType = JuBaoModel.JuBaoType.BAOLIXUEXING;
                    break;
                case R.id.rb_jubao_category3_df:
                    juBaoType = JuBaoModel.JuBaoType.SEQINGDUBO;
                    break;
                case R.id.rb_jubao_category4_df:
                    juBaoType = JuBaoModel.JuBaoType.XUJIAZHAPIAN;
                    break;
                default:
                    break;
            }
            if(binding.rbJubaoCategory0Df.isChecked()){
                juBaoType = JuBaoModel.JuBaoType.OTHER;
            }
            String content = "";
            switch (juBaoType){
                case OTHER:
                    content = binding.etJubaoContentDf.getText().toString().trim();
                    if(TextUtils.isEmpty(content)){
                        ToastTool.warning("请输入违规内容");
                        binding.etJubaoContentDf.setText("");
                        binding.etJubaoContentDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                        binding.brnJubaoSubmitDf.showError(2000);
                        binding.etJubaoContentDf.requestFocus();
                        return;
                    }
                    break;
                case ZHENGZHIMINGAN:
                    break;
                case BAOLIXUEXING:
                    break;
                case SEQINGDUBO:
                    break;
                case XUJIAZHAPIAN:
                    break;
                default:
                    ToastTool.warning("请选择举报类型");
                    binding.rgJubaoCategoryDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                    binding.brnJubaoSubmitDf.showError(2000);
                    return;
            }
            String email = binding.tieEmailJubaoerDf.getText().toString();
            if(TextUtils.isEmpty(email)){
                ToastTool.warning("请输入您的邮箱");
                binding.tieEmailJubaoerDf.setText("");
                binding.tilEmailJubaoerDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.brnJubaoSubmitDf.showError(2000);
                binding.tieEmailJubaoerDf.requestFocus();
                return;
            }
            if(!RegexUtils.isEmail(email)){
                ToastTool.warning("请输入正确的邮箱");
                binding.tilEmailJubaoerDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.brnJubaoSubmitDf.showError(2000);
                binding.tieEmailJubaoerDf.requestFocus();
                return;
            }
            binding.tieEmailJubaoerDf.requestFocus();
            KeyboardUtils.hideSoftInput(binding.tieEmailJubaoerDf);
            binding.brnJubaoSubmitDf.showProgress();
            new JuBaoModel().juBao(Long.parseLong(id), juBaoType, content, email, new JuBaoModel.JuBaoCallBack() {
                @Override
                public void juBaoFinish() {
                    binding.brnJubaoSubmitDf.showSucceed();
                    ThreadUtils.runOnUiThreadDelayed(()->{
                        binding.brnJubaoSubmitDf.reset();
                    },1500);
                }

                @Override
                public void juBaoError() {
                    binding.brnJubaoSubmitDf.reset();
                }
            });
        });
    }

    @Override
    protected void initData() {
        binding.tilJubaoIdDf.requestFocus();
        //延迟打开软键盘
        new Timer().schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }

        }, 200);//n秒后弹出
    }
}
