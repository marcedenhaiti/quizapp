package com.jenihaiti.com.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jenihaiti.com.Model.RecentQuizModel.Result;
import com.jenihaiti.com.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class RecentQuizAdapter extends RecyclerView.Adapter<RecentQuizAdapter.MyViewHolder> {

    Context context;
    List<Result> recentQuizList;

    public RecentQuizAdapter(Context context, List<Result> recentQuizList) {
        this.context = context;
        this.recentQuizList = recentQuizList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtWinningStatus, txtTopicName, txtPoints;
        public RoundedImageView rivContestant;
        public LinearLayout lyContestant;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWinningStatus = itemView.findViewById(R.id.txtWinningStatus);
            txtTopicName = itemView.findViewById(R.id.txtTopicName);
            txtPoints = itemView.findViewById(R.id.txtPoints);
            rivContestant = itemView.findViewById(R.id.rivContestant);
            lyContestant = itemView.findViewById(R.id.lyContestant);
        }
    }

    @NonNull
    @Override
    public RecentQuizAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_quiz_items, parent, false);

        RecentQuizAdapter.MyViewHolder viewHolder = new RecentQuizAdapter.MyViewHolder(itemLayoutView);
        return viewHolder;
    }

    @SuppressLint({"LongLogTag", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecentQuizAdapter.MyViewHolder holder, int position) {
        if (recentQuizList.get(position).getWinStatus().equalsIgnoreCase("win")) {
            holder.txtPoints.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
            holder.txtPoints.setText("+" + String.format("%.0f",
                    Double.parseDouble(recentQuizList.get(position).getScore())) + " P");
            holder.txtWinningStatus.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            holder.txtPoints.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.Red)));
            holder.txtPoints.setText("-" + String.format("%.0f",
                    Double.parseDouble(recentQuizList.get(position).getScore())) + " P");
            holder.txtWinningStatus.setTextColor(context.getResources().getColor(R.color.Red));
        }

        holder.txtWinningStatus.setText("" + recentQuizList.get(position).getWinStatus());
        holder.txtTopicName.setText("" + recentQuizList.get(position).getLevelName());

        if (!recentQuizList.get(position).getProfileImg().equalsIgnoreCase("")) {
            Picasso.get().load(recentQuizList.get(position).getProfileImg())
                    .placeholder(context.getResources().getDrawable(R.drawable.ic_username))
                    .into(holder.rivContestant);
        }

    }

    @Override
    public int getItemCount() {
        return recentQuizList.size();
    }

}
