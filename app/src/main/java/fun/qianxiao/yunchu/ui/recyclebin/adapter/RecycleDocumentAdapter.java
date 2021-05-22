package fun.qianxiao.yunchu.ui.recyclebin.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;

import org.jetbrains.annotations.NotNull;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.AppAdapter;
import fun.qianxiao.yunchu.bean.DocumentBean;
import fun.qianxiao.yunchu.databinding.ItemDocumentBinding;
import fun.qianxiao.yunchu.databinding.ItemDocumentRecycleBinding;
import fun.qianxiao.yunchu.model.DocumentModel;
import fun.qianxiao.yunchu.model.OperateListener;
import fun.qianxiao.yunchu.ui.recyclebin.RecycleBinActivity;
import fun.qianxiao.yunchu.utils.ToastTool;

public class RecycleDocumentAdapter extends AppAdapter<DocumentBean> {
    public RecycleDocumentAdapter(@NonNull @NotNull Context context) {
        super(context);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    class ViewHolder extends SimpleHolder{
        ItemDocumentRecycleBinding binding;

        public ViewHolder() {
            super(R.layout.item_document_recycle);
            binding = ItemDocumentRecycleBinding.bind(getItemView());
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindView(int position) {
            DocumentBean bean = getItem(position);
            binding.tvIdDocumentItem.setText(String.valueOf(bean.getId()));
            binding.tvTitleDocumentItem.setText(bean.getTitle());
            binding.tvDescDocumentItem.setText(bean.getDesc());
            binding.tvCreateDocumentItem.setText(TimeUtils.date2String(bean.getCreate(), TimeUtils.getSafeDateFormat("yyyy-MM-dd")));
            binding.tvUpdateDocumentItem.setText(TimeUtils.getFriendlyTimeSpanByNow(bean.getModify_time()*1000));
            binding.tvUpdateDocumentItem.setText(binding.tvUpdateDocumentItem.getText().toString()+"删除");
            binding.ivPasswordDocumentItem.setVisibility(TextUtils.isEmpty(bean.getPassword())? View.GONE:View.VISIBLE);
            binding.ivHideDocumentItem.setVisibility(bean.isHide()?View.VISIBLE:View.GONE);
            if(bean.isHtml()){
                binding.tvHtmlDocumentItem.setVisibility(View.VISIBLE);
            }else{
                binding.tvHtmlDocumentItem.setVisibility(View.GONE);
            }

            long surplusTime = (TimeConstants.DAY / 1000)*30 - (TimeUtils.getNowMills() / 1000 - bean.getModify_time());
            if(surplusTime>=TimeConstants.DAY / 1000){
                binding.tvSurplusTimeRecycle.setText("剩余"+(int) Math.ceil(surplusTime * 1.0 / (TimeConstants.DAY / 1000f)) +"天");
            }else if(surplusTime>=TimeConstants.HOUR / 1000){
                binding.tvSurplusTimeRecycle.setText("剩余"+(int) Math.ceil(surplusTime / (TimeConstants.HOUR / 1000f)) +"小时");
            }else{
                binding.tvSurplusTimeRecycle.setText("剩余"+(int) Math.ceil(surplusTime / (TimeConstants.MIN / 1000f)) +"分钟");
            }
            binding.tvRestoreRecycleBin.setOnClickListener(v -> {
                DocumentBean documentBean = getItem(position);
                ((RecycleBinActivity)getContext()).openLoadingDialog("正在恢复");
                new DocumentModel(getContext()).restore(documentBean.getId(),
                        new OperateListener() {
                            @Override
                            public void operateSuccess(String var1) {
                                ((RecycleBinActivity)getContext()).closeLoadingDialog();
                                ToastTool.success("恢复成功");
                                removeItem(position);
                                ((RecycleBinActivity)getContext()).setResult(Activity.RESULT_OK);
                                ThreadUtils.runOnUiThreadDelayed(()-> ((RecycleBinActivity)getContext()).adapterDataObserverOnChanged(),200);
                            }

                            @Override
                            public void operateError(String e) {
                                ((RecycleBinActivity)getContext()).closeLoadingDialog();
                                ToastTool.error(e);

                            }
                        });
            });
        }
    }
}
