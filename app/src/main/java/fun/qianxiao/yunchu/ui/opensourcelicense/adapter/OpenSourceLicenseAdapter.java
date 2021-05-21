package fun.qianxiao.yunchu.ui.opensourcelicense.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseRecycleViewHolder;
import fun.qianxiao.yunchu.databinding.ItemOpensourcelicenseBinding;
import fun.qianxiao.yunchu.ui.opensourcelicense.bean.OpenSourceLicense;

public class OpenSourceLicenseAdapter extends RecyclerView.Adapter<OpenSourceLicenseAdapter.ViewHolder> {
    private Context context;
    private List<OpenSourceLicense> openSourceLicenses;

    public OpenSourceLicenseAdapter(Context context, List<OpenSourceLicense> openSourceLicenses) {
        this.context = context;
        this.openSourceLicenses = openSourceLicenses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_opensourcelicense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OpenSourceLicense openSourceLicense = openSourceLicenses.get(position);
        holder.tv_name_item_osl.setText(openSourceLicense.getName());
        holder.tv_author_item_osl.setText(openSourceLicense.getAnthor());
        holder.tv_describe_item_osl.setText(openSourceLicense.getDescribe());
        holder.tv_license_item_osl.setText(openSourceLicense.getLicense());
        holder.iv_go_item_osl.setOnClickListener(view -> context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(openSourceLicense.getUrl()))));
        holder.itemView.setOnClickListener(view -> context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(openSourceLicense.getUrl()))));
    }

    @Override
    public int getItemCount() {
        return openSourceLicenses.size();
    }

    class ViewHolder extends BaseRecycleViewHolder<ItemOpensourcelicenseBinding> {
        TextView tv_name_item_osl,tv_author_item_osl,tv_describe_item_osl,tv_license_item_osl;
        ImageView iv_go_item_osl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_item_osl = binding.tvNameItemOsl;
            tv_author_item_osl = binding.tvAuthorItemOsl;
            tv_describe_item_osl = binding.tvDescribeItemOsl;
            tv_license_item_osl = binding.tvLicenseItemOsl;
            iv_go_item_osl = binding.tvGoItemOsl;
        }

        @Override
        protected ItemOpensourcelicenseBinding getBinding() {
            return ItemOpensourcelicenseBinding.bind(view);
        }
    }
}
