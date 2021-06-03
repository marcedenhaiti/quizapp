package com.jenihaiti.com.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jenihaiti.com.Model.LeaderBoardModel.Result;
import com.jenihaiti.com.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class TopContestantAdapter extends RecyclerView.Adapter<TopContestantAdapter.MyViewHolder> {

    private Context context;
    private List<Result> topContenstantList;

    public TopContestantAdapter(Context context, List<Result> topContenstantList) {
        this.context = context;
        this.topContenstantList = topContenstantList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName, txtIndex, txtPoints;
        public RoundedImageView rivContestant;
        public LinearLayout lyContestant;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lyContestant = itemView.findViewById(R.id.lyContestant);
            txtName = itemView.findViewById(R.id.txtName);
            txtIndex = itemView.findViewById(R.id.txtIndex);
            txtPoints = itemView.findViewById(R.id.txtPoints);
            rivContestant = itemView.findViewById(R.id.rivContestant);
        }
    }

    @NonNull
    @Override
    public TopContestantAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.top_contestant_items, parent, false);

        TopContestantAdapter.MyViewHolder viewHolder = new TopContestantAdapter.MyViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TopContestantAdapter.MyViewHolder holder, int position) {
        if ((position + 3) <= topContenstantList.size()) {
            holder.txtIndex.setText("" + topContenstantList.get(position + 3).getRank());
            holder.txtName.setText("" + topContenstantList.get(position + 3).getName());

            holder.txtPoints.setText("" + String.format("%.0f",
                    Double.parseDouble(topContenstantList.get(position + 3).getScore())));

            if (!TextUtils.isEmpty(topContenstantList.get(position + 3).getProfileImg()))
                Picasso.get().load(topContenstantList.get(position + 3).getProfileImg())
                        .placeholder(context.getResources().getDrawable(R.drawable.ic_username))
                        .into(holder.rivContestant);
        }
    }

    @Override
    public int getItemCount() {
        return (topContenstantList.size()) - 3;
    }

}
