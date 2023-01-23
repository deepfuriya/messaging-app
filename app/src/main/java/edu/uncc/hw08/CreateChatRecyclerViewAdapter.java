package edu.uncc.hw08;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CreateChatRecyclerViewAdapter extends RecyclerView.Adapter<CreateChatRecyclerViewAdapter.CreateChatViewHolder> {

    ArrayList<ChatUsers> usersArrayList = new ArrayList<>();
    CreateChatRecyclerInterface mListener;

    public CreateChatRecyclerViewAdapter(ArrayList<ChatUsers> data,CreateChatRecyclerInterface mListener){
        this.usersArrayList = data;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public CreateChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_row_item,parent,false);
        CreateChatViewHolder createChatViewHolder = new CreateChatViewHolder(view,mListener);
        return createChatViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CreateChatViewHolder holder, int position) {

        ChatUsers chatUsers = usersArrayList.get(position);
        holder.textViewName.setText(chatUsers.getUser_name());
        if (chatUsers.is_online == true){
            holder.imageViewOnline.setVisibility(View.VISIBLE);
        }else{
            holder.imageViewOnline.setVisibility(View.GONE);
        }

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.sendUserData(chatUsers.getUser_name(),chatUsers.getUser_id());
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.usersArrayList.size();
    }


    public static class CreateChatViewHolder extends RecyclerView.ViewHolder{
        TextView textViewName;
        ImageView imageViewOnline;
        View rootView;
        CreateChatRecyclerInterface mListener;

        public CreateChatViewHolder(@NonNull View itemView, CreateChatRecyclerInterface mListener) {
            super(itemView);
            this.mListener = mListener;
            rootView = itemView;
            textViewName = itemView.findViewById(R.id.textViewName);
            imageViewOnline = itemView.findViewById(R.id.imageViewOnline);
        }
    }

    interface CreateChatRecyclerInterface{
        void sendUserData(String userName,String userId);
    }
}
