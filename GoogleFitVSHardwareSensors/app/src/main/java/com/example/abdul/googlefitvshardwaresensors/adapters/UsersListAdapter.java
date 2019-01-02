package com.example.abdul.googlefitvshardwaresensors.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.abdul.googlefitvshardwaresensors.Constants;
import com.example.abdul.googlefitvshardwaresensors.FeedbackActivity;
import com.example.abdul.googlefitvshardwaresensors.R;
import com.example.abdul.googlefitvshardwaresensors.models.UserModel;

import java.util.ArrayList;

/**
 * Created by Minhaj on 20/12/2018.
 */
public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<UserModel> userModeList;


    public UsersListAdapter(Context context,ArrayList<UserModel> userModeList) {
        this.context = context;
        this.userModeList = userModeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_layout_users,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        UserModel userModel = userModeList.get(position);
        holder.tvUserName.setText(userModel.getUsername());
    }

    @Override
    public int getItemCount() {
        return userModeList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvUserName;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context,FeedbackActivity.class);
            intent.putExtra(Constants.EXTRA_USER,userModeList.get(getAdapterPosition()));
            context.startActivity(intent);
        }
    }
}
