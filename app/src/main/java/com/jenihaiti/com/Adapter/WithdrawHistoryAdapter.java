package com.jenihaiti.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jenihaiti.com.Model.WithdrawalModel.Result;
import com.jenihaiti.com.R;
import com.jenihaiti.com.Util.Utility;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class WithdrawHistoryAdapter extends RecyclerView.Adapter<WithdrawHistoryAdapter.MyViewHolder> {

    Context mcontext;
    List<Result> historyList;

    public WithdrawHistoryAdapter(Context context, List<Result> historyList) {
        this.mcontext = context;
        this.historyList = historyList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle, txtDate, txtStatus, txtAmount;
        RoundedImageView ivThumb;
        LinearLayout lyMain;

        public MyViewHolder(View view) {
            super(view);
            lyMain = view.findViewById(R.id.lyMain);
            txtTitle = view.findViewById(R.id.txtTitle);
            txtDate = view.findViewById(R.id.txtDate);
            txtStatus = view.findViewById(R.id.txtStatus);
            txtAmount = view.findViewById(R.id.txtAmount);
            ivThumb = view.findViewById(R.id.ivThumb);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.withdraw_history_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.txtTitle.setText("" + historyList.get(position).getPaymentType());
        holder.txtAmount.setText("" + historyList.get(position).getTotalAmount());
        holder.txtDate.setText("" + Utility.DateFormat2(historyList.get(position).getCreatedAt()));

        if (historyList.get(position).getStatus().equalsIgnoreCase("0")) {
            holder.txtStatus.setText("" + mcontext.getResources().getString(R.string.pending));
            holder.txtStatus.setBackground(mcontext.getResources().getDrawable(R.drawable.round_bg_lightgray));
            holder.txtStatus.setTextColor(mcontext.getResources().getColor(R.color.text_color_primary));
        } else {
            holder.txtStatus.setText("" + mcontext.getResources().getString(R.string.completed));
            holder.txtStatus.setBackground(mcontext.getResources().getDrawable(R.drawable.round_bg_yellow));
            holder.txtStatus.setTextColor(mcontext.getResources().getColor(R.color.white));
        }

    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

}
