package com.jenihaiti.com.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jenihaiti.com.Activity.LevelSelection;
import com.jenihaiti.com.Model.CategoryModel.Result;
import com.jenihaiti.com.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<Result> categoryList;
    Context context;

    public CategoryAdapter(Context context, List<Result> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtCategory;
        public ImageView ivCategory;
        public LinearLayout lyCategory;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            lyCategory = itemView.findViewById(R.id.lyCategory);
        }
    }

    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);

        CategoryAdapter.MyViewHolder viewHolder = new CategoryAdapter.MyViewHolder(itemLayoutView);
        return viewHolder;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.MyViewHolder holder, int position) {
        holder.txtCategory.setText("" + categoryList.get(position).getName());
        Picasso.get().load(categoryList.get(position).getImage()).into(holder.ivCategory);

        holder.lyCategory.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                Log.e("Click", "position : " + position);
                Intent intent = new Intent(context, LevelSelection.class);
                intent.putExtra("catId", "" + categoryList.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

}
