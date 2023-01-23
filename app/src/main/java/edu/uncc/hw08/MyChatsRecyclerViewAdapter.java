package edu.uncc.hw08;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MyChatsRecyclerViewAdapter extends RecyclerView.Adapter<MyChatsRecyclerViewAdapter.MyChatsViewHolder> {

    ArrayList<Message> messageArrayList = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String currentUserId = mAuth.getUid();

    MyChatsRecyclerViewInterface mListener;

    public MyChatsRecyclerViewAdapter(ArrayList<Message> data,MyChatsRecyclerViewInterface mListener){
        this.messageArrayList = data;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public MyChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_chats_list_item,parent,false);
        MyChatsViewHolder myChatsViewHolder = new MyChatsViewHolder(view,mListener);
        return myChatsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyChatsViewHolder holder, int position) {
        Message msg = messageArrayList.get(position);
        String name;
        if (msg.getUser_one().equalsIgnoreCase(currentUserId)){
            name = msg.getUser_two_name();
        }else{
            name = msg.getUser_one_name();
        }

        holder.textViewMsgBy.setText(name);
        holder.textViewMsgOn.setText(msg.getLast_msg_timestamp());
        holder.textViewMsgText.setText(msg.getLast_msg());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToViewChat(msg.getMessage_id(),name);
            }
        });



    }

    @Override
    public int getItemCount() {
        return this.messageArrayList.size();
    }

    public static class MyChatsViewHolder extends RecyclerView.ViewHolder{
        TextView textViewMsgBy;
        TextView textViewMsgText;
        TextView textViewMsgOn;
        MyChatsRecyclerViewInterface mListener;

        public MyChatsViewHolder(@NonNull View itemView, MyChatsRecyclerViewInterface mListener) {
            super(itemView);
            this.mListener = mListener;
            textViewMsgBy = itemView.findViewById(R.id.textViewMsgBy);
            textViewMsgText = itemView.findViewById(R.id.textViewMsgText);
            textViewMsgOn = itemView.findViewById(R.id.textViewMsgOn);

        }
    }

    interface MyChatsRecyclerViewInterface{
        void goToViewChat(String messageId,String name);
    }
}
