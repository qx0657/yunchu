package fun.qianxiao.yunchu.ui.addoredit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseActivity;
import fun.qianxiao.yunchu.bean.DocumentBean;
import fun.qianxiao.yunchu.databinding.ActivityAddBinding;
import fun.qianxiao.yunchu.eventbus.EBCreateDocumentSuccess;
import fun.qianxiao.yunchu.model.DocumentModel;
import fun.qianxiao.yunchu.model.OperateListener;
import fun.qianxiao.yunchu.utils.PerformEdit;
import fun.qianxiao.yunchu.utils.ToastTool;
import fun.qianxiao.yunchu.view.loading.ILoadingView;
import fun.qianxiao.yunchu.view.loading.MyLoadingDialog;

public class AddEditActivity extends BaseActivity<ActivityAddBinding> implements KeyboardUtils.OnSoftInputChangedListener, ILoadingView {
    private PerformEdit performEdit;
    private MyLoadingDialog loadingDialog;
    private FromType fromType = FromType.ADD;
    private long editId = -1;

    public enum FromType{
        ADD,EDIT
    }

    @Override
    protected ActivityAddBinding getBinding() {
        return ActivityAddBinding.inflate(getLayoutInflater());
    }

    boolean beforeClickPwdIsKeyBoardShow;

    @Override
    protected void initListener() {
        KeyboardUtils.registerSoftInputChangedListener(this, this);
        binding.etTitleAdd.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                binding.ivUndoAdd.setVisibility(View.GONE);
                binding.ivRedoAdd.setVisibility(View.GONE);
            }else{
                binding.ivUndoAdd.setVisibility(View.VISIBLE);
                binding.ivRedoAdd.setVisibility(View.VISIBLE);
            }
        });
        binding.cbPasswordAdd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                if(!TextUtils.isEmpty(((String)binding.cbPasswordAdd.getTag()))){
                    return;
                }
                binding.cbPasswordAdd.setChecked(false);
                if(KeyboardUtils.isSoftInputVisible(AddEditActivity.this)){
                    beforeClickPwdIsKeyBoardShow = true;
                    KeyboardUtils.hideSoftInput(getWindow());
                }else{
                    beforeClickPwdIsKeyBoardShow = false;
                }
                EditText editText = new EditText(context);
                editText.setHint("请输入访问密码");
                LinearLayout l1 = new LinearLayout(context);
                l1.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lp1.setMargins(0,ConvertUtils.dp2px(20),0,0);
                l1.setLayoutParams(lp1);
                l1.setPadding(ConvertUtils.dp2px(10),0,ConvertUtils.dp2px(10),0);
                l1.addView(editText);
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("访问密码")
                        .setView(l1)
                        .setPositiveButton("确定", null)
                        .setNegativeButton("取消", (dialog, which) -> {
                            if(!beforeClickPwdIsKeyBoardShow){
                                ThreadUtils.runOnUiThreadDelayed(()-> KeyboardUtils.hideSoftInput(getWindow()),200);
                            }
                        })
                        .setCancelable(false)
                        .show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String password = editText.getText().toString().trim();
                    if(TextUtils.isEmpty(password)){
                        editText.setText("");
                        editText.requestFocus();
                        ToastTool.warning("请输入访问密码");
                    }else{
                        binding.cbPasswordAdd.setTag(password);
                        alertDialog.dismiss();
                        binding.cbPasswordAdd.setChecked(true);
                        binding.sbPasswordAdd.setRightText(password);
                        binding.sbPasswordAdd.setVisibility(View.VISIBLE);
                        if(!beforeClickPwdIsKeyBoardShow){
                            ThreadUtils.runOnUiThreadDelayed(()-> KeyboardUtils.hideSoftInput(getWindow()),200);
                        }
                    }
                });
                editText.requestFocus();
                //延迟打开软键盘
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                }, 200);//n秒后弹出
            }else{
                binding.cbPasswordAdd.setTag("");
                binding.sbPasswordAdd.setVisibility(View.GONE);
            }
        });

        View.OnLongClickListener onLongClickListener = v -> {
            new AlertDialog.Builder(context)
                    .setTitle("温馨提示")
                    .setMessage("是否清空编辑历史？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        if(performEdit!=null){
                            performEdit.clearHistory();
                            ToastTool.success("编辑历史已清空");
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
            return true;
        };
        binding.ivUndoAdd.setOnLongClickListener(onLongClickListener);
        binding.ivRedoAdd.setOnLongClickListener(onLongClickListener);
        binding.ivUndoAdd.setOnClickListener(v -> performEdit.undo());
        binding.ivRedoAdd.setOnClickListener(v -> performEdit.redo());
        binding.ivSaveAdd.setOnClickListener(v -> Save());

        binding.sbPasswordAdd.setOnClickListener(v -> {
            String pwd = binding.sbPasswordAdd.getRightText().toString();
            if(!TextUtils.isEmpty(pwd)){
                new AlertDialog.Builder(context)
                        .setTitle("访问密码")
                        .setMessage(pwd)
                        .setNeutralButton("重置", (dialog, which) -> {
                            EditText editText = new EditText(context);
                            editText.setHint("请输入访问密码");
                            editText.setText(pwd);
                            LinearLayout l1 = new LinearLayout(context);
                            l1.setOrientation(LinearLayout.VERTICAL);
                            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp1.setMargins(0,ConvertUtils.dp2px(20),0,0);
                            l1.setLayoutParams(lp1);
                            l1.setPadding(ConvertUtils.dp2px(10),0,ConvertUtils.dp2px(10),0);
                            l1.addView(editText);
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
                                    .setTitle("访问密码")
                                    .setView(l1)
                                    .setPositiveButton("确定", null)
                                    .setNegativeButton("取消", (dialog2, which2) -> {
                                        if(!beforeClickPwdIsKeyBoardShow){
                                            ThreadUtils.runOnUiThreadDelayed(()-> KeyboardUtils.hideSoftInput(getWindow()),200);
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v2-> {
                                String password = editText.getText().toString().trim();
                                if(TextUtils.isEmpty(password)){
                                    editText.setText("");
                                    editText.requestFocus();
                                    ToastTool.warning("请输入访问密码");
                                }else{
                                    binding.cbPasswordAdd.setTag(password);
                                    alertDialog.dismiss();
                                    binding.cbPasswordAdd.setChecked(true);
                                    binding.sbPasswordAdd.setRightText(password);
                                    binding.sbPasswordAdd.setVisibility(View.VISIBLE);
                                    if(!beforeClickPwdIsKeyBoardShow){
                                        ThreadUtils.runOnUiThreadDelayed(()-> KeyboardUtils.hideSoftInput(getWindow()),200);
                                    }
                                }
                            });
                            editText.requestFocus();
                            //延迟打开软键盘
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    assert imm != null;
                                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                                }

                            }, 200);//n秒后弹出
                        })
                        .setPositiveButton("复制", (dialog, which) -> {
                            ClipboardUtils.copyText(pwd);
                            ToastTool.success("访问密码已复制至剪贴板");
                        })
                        .show();
            }
        });
        binding.sbKeyAdd.setOnClickListener(v -> {
            String key = (String) binding.sbKeyAdd.getTag();
            if(!TextUtils.isEmpty(key)){
                //复制、重置
                new AlertDialog.Builder(context)
                        .setTitle("文档密钥")
                        .setMessage(key)
                        .setNeutralButton("重置", (dialog, which) -> {
                            openLoadingDialog("正在重置");
                            new DocumentModel(context).updateKey(editId, new OperateListener() {
                                @Override
                                public void operateSuccess(String key) {
                                    closeLoadingDialog();
                                    ToastTool.success("重置成功");
                                    binding.sbKeyAdd.setTag(key);
                                    binding.sbKeyAdd.setRightText(key.substring(0,12)+"********"+key.substring(20,32));
                                }

                                @Override
                                public void operateError(String e) {
                                    closeLoadingDialog();
                                    ToastTool.error(e);
                                }
                            });
                        })
                        .setPositiveButton("复制", (dialog, which) -> {
                            ClipboardUtils.copyText(key);
                            ToastTool.success("文档密钥已复制至剪贴板");
                        })
                        .show();
            }else{
                openLoadingDialog("正在生成");
                new DocumentModel(context).updateKey(editId, new OperateListener() {
                    @Override
                    public void operateSuccess(String key) {
                        closeLoadingDialog();
                        ToastTool.success("生成成功");
                        binding.sbKeyAdd.setTag(key);
                        binding.sbKeyAdd.setRightText(key.substring(0,12)+"********"+key.substring(20,32));
                    }

                    @Override
                    public void operateError(String e) {
                        closeLoadingDialog();
                        ToastTool.error(e);
                    }
                });
            }
        });
        binding.tieContentAddA.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    JSONObject jsonObject = new JSONObject(s.toString());
                    binding.tvJsonAddEdit.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    try{
                        JSONArray jsonObject = new JSONArray(s.toString());
                        binding.tvJsonAddEdit.setVisibility(View.VISIBLE);
                    } catch (JSONException e2) {
                        binding.tvJsonAddEdit.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        setTitle("");
        binding.tieContentAddA.requestFocus();
        binding.cbPasswordAdd.setTag("");

        Intent intent = getIntent();
        editId = intent.getLongExtra("id",-1);
        if(editId!=-1){
            fromType = FromType.EDIT;
            //获取
            binding.etTitleAdd.setText("云储文档");
            openLoadingDialog("加载中");
            new DocumentModel(context).getDocument(editId, new DocumentModel.GetDocumentListener() {
                @Override
                public void getDocumentSuccess(DocumentBean documentBean) {
                    closeLoadingDialog();
                    binding.etTitleAdd.setText(documentBean.getTitle());
                    binding.tieContentAddA.setText(documentBean.getContent());
                    binding.cbHtmlAdd.setChecked(documentBean.isHtml());
                    binding.cbPrivateAdd.setChecked(documentBean.isHide());
                    if(!TextUtils.isEmpty(documentBean.getPassword())){
                        binding.cbPasswordAdd.setTag(documentBean.getPassword());
                        binding.cbPasswordAdd.setChecked(true);
                        binding.sbPasswordAdd.setRightText(documentBean.getPassword());
                        binding.sbPasswordAdd.setVisibility(View.VISIBLE);
                    }else{
                        binding.sbPasswordAdd.setVisibility(View.GONE);
                    }
                    if(!TextUtils.isEmpty(documentBean.getKey())&&!documentBean.getKey().equals("null")){
                        binding.sbKeyAdd.setTag(documentBean.getKey());
                        binding.sbKeyAdd.setRightText(documentBean.getKey().substring(0,12)+"********"+documentBean.getKey().substring(20,32));
                    }
                    performEdit = new PerformEdit(binding.tieContentAddA, binding.ivUndoAdd, binding.ivRedoAdd);
                    performEdit.setDefaultText(documentBean.getContent());
                    binding.tieContentAddA.setSelection(binding.tieContentAddA.getText().length());
                }

                @Override
                public void getDocumentError(String e) {
                    closeLoadingDialog();
                    ToastTool.error(e);
                    finish();
                }
            });
        }else{
            binding.etTitleAdd.setText("云储文档"+ TimeUtils.getNowString());
            binding.sbKeyAdd.setVisibility(View.GONE);
            performEdit = new PerformEdit(binding.tieContentAddA, binding.ivUndoAdd, binding.ivRedoAdd);
            performEdit.setDefaultText("");
        }
    }

    private void Save() {
        String content = Objects.requireNonNull(binding.tieContentAddA.getText()).toString();
        if(TextUtils.isEmpty(content)){
            ToastTool.warning("文档内容不能为空");
            binding.tieContentAddA.requestFocus();
            binding.tilContentAddA.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
            return;
        }
        String title = binding.etTitleAdd.getText().toString();
        if(TextUtils.isEmpty(title)){
            ToastTool.warning("文档标题不能为空");
            binding.etTitleAdd.requestFocus();
            binding.etTitleAdd.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
            return;
        }
        if(fromType==FromType.ADD){
            //新增
            openLoadingDialog("正在保存");
            new DocumentModel(context).createDocument(title, content, binding.cbHtmlAdd.isChecked(), binding.cbPrivateAdd.isChecked(), (String) binding.cbPasswordAdd.getTag(),
                    new OperateListener() {
                        @Override
                        public void operateSuccess(String id) {
                            closeLoadingDialog();
                            ToastTool.success("保存成功");
                            DocumentBean documentBean = new DocumentBean();
                            documentBean.setId(Long.parseLong(id));
                            documentBean.setTitle(title);
                            documentBean.setDesc(content.length()<=50?content:content.substring(0,50));
                            documentBean.setCreate(TimeUtils.getNowDate());
                            documentBean.setModify_time(TimeUtils.getNowMills()/1000);
                            documentBean.setRead(0);
                            documentBean.setModify(0);
                            documentBean.setHtml(binding.cbHtmlAdd.isChecked());
                            documentBean.setHide(binding.cbPrivateAdd.isChecked());
                            documentBean.setPassword((String) binding.cbPasswordAdd.getTag());
                            EventBus.getDefault().post(new EBCreateDocumentSuccess(true, documentBean));
                            ThreadUtils.runOnUiThreadDelayed(()->finish(),200);
                        }

                        @Override
                        public void operateError(String e) {
                            closeLoadingDialog();
                            ToastTool.error(e);
                        }
                    });
        }else{
            //更新
            openLoadingDialog("正在更新");
            new DocumentModel(context).update(editId, title, content, binding.cbHtmlAdd.isChecked(), binding.cbPrivateAdd.isChecked(), (String) binding.cbPasswordAdd.getTag(),
                    new OperateListener() {
                        @Override
                        public void operateSuccess(String var1) {
                            closeLoadingDialog();
                            ToastTool.success("更新成功");
                            setResult(Activity.RESULT_OK);
                            ThreadUtils.runOnUiThreadDelayed(()->finish(),200);
                        }

                        @Override
                        public void operateError(String e) {
                            closeLoadingDialog();
                            ToastTool.error(e);
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KeyboardUtils.unregisterSoftInputChangedListener(this.getWindow());
    }

    @Override
    public void onSoftInputChanged(int height) {
        ViewGroup.LayoutParams lp = binding.tilContentAddA.getLayoutParams();
        LogUtils.i(ScreenUtils.getScreenHeight(),BarUtils.getStatusBarHeight(),BarUtils.getActionBarHeight());
        lp.height = ScreenUtils.getScreenHeight()-BarUtils.getStatusBarHeight()-BarUtils.getActionBarHeight()-ConvertUtils.dp2px(48+5+5)-height;
        binding.tilContentAddA.setLayoutParams(lp);
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
