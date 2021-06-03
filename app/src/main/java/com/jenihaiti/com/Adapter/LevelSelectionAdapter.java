package com.jenihaiti.com.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jenihaiti.com.Activity.QuestionAnswer;
import com.jenihaiti.com.Model.LevelModel.Result;
import com.jenihaiti.com.R;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.List;

public class LevelSelectionAdapter extends RecyclerView.Adapter<LevelSelectionAdapter.MyViewHolder> {

    Context context;
    List<Result> levelList;

    public LevelSelectionAdapter(Context context, List<Result> levelList) {
        this.context = context;
        this.levelList = levelList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtLevel, txtQuestionCount;
        public LinearLayout lyLevel;
        public RoundedImageView rivLockUnlock;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rivLockUnlock = itemView.findViewById(R.id.rivLockUnlock);
            lyLevel = itemView.findViewById(R.id.lyLevel);
            txtLevel = itemView.findViewById(R.id.txtLevel);
            txtQuestionCount = itemView.findViewById(R.id.txtQuestionCount);
        }
    }

    @NonNull
    @Override
    public LevelSelectionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.level_selection_items, parent, false);

        LevelSelectionAdapter.MyViewHolder viewHolder = new LevelSelectionAdapter.MyViewHolder(itemLayoutView);
        return viewHolder;
    }

    @SuppressLint({"LongLogTag", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull LevelSelectionAdapter.MyViewHolder holder, int position) {
        holder.txtLevel.setText("" + levelList.get(position).getName());
        holder.txtQuestionCount.setText("Question " + levelList.get(position).getTotalQuestion());

        if (levelList.get(position).getIsUnlock() == 0) {
            holder.rivLockUnlock.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_level_lock));
        } else {
            holder.rivLockUnlock.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_level_unlock));
        }

        holder.lyLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click", "" + position);
                if (levelList.get(position).getIsUnlock() == 1) {
                    Intent intent = new Intent(context, QuestionAnswer.class);
                    intent.putExtra("catID", "" + levelList.get(position).getCategoryId());
                    intent.putExtra("levelID", "" + levelList.get(position).getId());
                    intent.putExtra("currentLevel", "" + (position + 1));
                    intent.putExtra("TotalLevel", "" + levelList.size());
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return levelList.size();
    }

}