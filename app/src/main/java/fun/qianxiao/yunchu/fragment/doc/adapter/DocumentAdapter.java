package fun.qianxiao.yunchu.fragment.doc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.TimeUtils;

import org.jetbrains.annotations.NotNull;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.AppAdapter;
import fun.qianxiao.yunchu.bean.DocumentBean;
import fun.qianxiao.yunchu.databinding.ItemDocumentBinding;

public class DocumentAdapter extends AppAdapter<DocumentBean> {
    public DocumentAdapter(@NonNull @NotNull Context context) {
        super(context);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    class ViewHolder extends SimpleHolder{
        ItemDocumentBinding binding;

        public ViewHolder() {
            super(R.layout.item_document);
            binding = ItemDocumentBinding.bind(getItemView());
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
            binding.tvReadDocumentItem.setText(String.valueOf(bean.getRead()));
            binding.tvModifyDocumentItem.setText(String.valueOf(bean.getModify()));
            binding.ivPasswordDocumentItem.setVisibility(TextUtils.isEmpty(bean.getPassword())? View.GONE:View.VISIBLE);
            binding.ivHideDocumentItem.setVisibility(bean.isHide()?View.VISIBLE:View.GONE);
            if(bean.isHtml()){
                binding.tvHtmlDocumentItem.setVisibility(View.VISIBLE);
            }else{
                binding.tvHtmlDocumentItem.setVisibility(View.GONE);
            }
        }
    }
}
