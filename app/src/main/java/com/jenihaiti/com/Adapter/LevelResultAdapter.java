package com.jenihaiti.com.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jenihaiti.com.Model.TodayLeaderBoardModel.Result;
import com.jenihaiti.com.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class LevelResultAdapter extends RecyclerView.Adapter<LevelResultAdapter.MyViewHolder> {

    private Context context;
    private List<Result> todayList;

    public LevelResultAdapter(Context context, List<Result> todayList, String current_User) {
        this.context = context;
        this.todayList = todayList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtContestantRank, txtName, txtPoints;
        public RoundedImageView rivContestant;
        public LinearLayout lyContestant;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lyContestant = itemView.findViewById(R.id.lyContestant);
            txtName = itemView.findViewById(R.id.txtName);
            txtPoints = itemView.findViewById(R.id.txtPoints);
            txtContestantRank = itemView.findViewById(R.id.txtContestantRank);
            rivContestant = itemView.findViewById(R.id.rivContestant);
        }
    }

    @NonNull
    @Override
    public LevelResultAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rankwise_contestant_items, parent, false);

        LevelResultAdapter.MyViewHolder viewHolder = new LevelResultAdapter.MyViewHolder(itemLayoutView);
        return viewHolder;
    }

    @SuppressLint({"LongLogTag", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull LevelResultAdapter.MyViewHolder holder, int position) {
        holder.txtContestantRank.setText("" + todayList.get(position).getRank());
        holder.txtName.setText("" + todayList.get(position).getName());
        holder.txtPoints.setText("" + String.format("%.0f", Double.parseDouble(todayList.get(position).getScore())));

        if (!todayList.get(position).getProfileImg().isEmpty()) {
            Picasso.get().load(todayList.get(position).getProfileImg())
                    .placeholder(context.getDrawable(R.drawable.ic_username))
                    .into(holder.rivContestant);
        }
    }

    @Override
    public int getItemCount() {
        return todayList.size();
    }

}
