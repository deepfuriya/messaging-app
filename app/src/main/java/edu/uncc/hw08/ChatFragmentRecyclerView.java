package edu.uncc.hw08;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatFragmentRecyclerView extends RecyclerView.Adapter<ChatFragmentRecyclerView.ChatFragmentViewHolder> {

    ArrayList<MessageList> messageList = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String message_id;
    AlertDialog.Builder builder;

    public ChatFragmentRecyclerView(ArrayList<MessageList> data,String msg_id){
        this.messageList = data;
        this.message_id = msg_id;
    }

    @NonNull
    @Override
    public ChatFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item,parent,false);
        ChatFragmentViewHolder chatFragmentViewHolder = new ChatFragmentViewHolder(view);
        return chatFragmentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatFragmentViewHolder holder, int position) {
        MessageList msgList = messageList.get(position);
        String name;
        if (mAuth.getCurrentUser().getUid().equalsIgnoreCase(msgList.getUser_id())){
            name = "Me";
            holder.imageViewDelete.setVisibility(View.VISIBLE);
        }else{
            holder.imageViewDelete.setVisibility(View.GONE);
            name = msgList.getUser_name();
        }

        holder.textViewMsgBy.setText(name);
        holder.textViewMsgText.setText(msgList.getMsg());
        holder.textViewMsgOn.setText(msgList.getTimestamp());

        builder = new AlertDialog.Builder(holder.rootView.getContext());

        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder.setTitle("Alert")
                    .setMessage("Do you want to Delete ?")
                            .setCancelable(true)
                            .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db.collection("chats")
                                            .document(message_id)
                                            .collection("messages")
                                            .document(msgList.getSingle_message_id())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    db.collection("chats")
                                                            .document(message_id)
                                                            .collection("messages")
                                                            .orderBy("timestamp", Query.Direction.DESCENDING).limit(1)
                                                            .get()
                                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                    String lastMsg = null;
                                                                    Timestamp time = null;
                                                                    if (queryDocumentSnapshots.size() > 0){
                                                                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                                                            lastMsg = doc.get("msg").toString();
                                                                            time = (Timestamp) doc.get("timestamp");
                                                                        }
                                                                    }

                                                                    HashMap<String,Object> updateData = new HashMap<>();
                                                                    updateData.put("last_msg",lastMsg);

                                                                    if (queryDocumentSnapshots.size() > 0){
                                                                        updateData.put("last_msg_timestamp",time);
                                                                    }

                                                                    db.collection("chats")
                                                                            .document(message_id)
                                                                            .update(updateData)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isComplete()){
                                                                                    }else{
                                                                                        Toast.makeText(view.getContext(), "Message not updated", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("deep", "onFailure: "+e.getMessage());
                                                }
                                            });
                                }
                            })
                                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    })
                        .show();





            }
        });
    }

    @Override
    public int getItemCount() {
        return this.messageList.size();
    }

    public static class ChatFragmentViewHolder extends RecyclerView.ViewHolder{
        TextView textViewMsgBy;
        TextView textViewMsgText;
        TextView textViewMsgOn;
        ImageView imageViewDelete;
        View rootView;

        public ChatFragmentViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            textViewMsgBy = itemView.findViewById(R.id.textViewMsgBy);
            textViewMsgText = itemView.findViewById(R.id.textViewMsgText);
            textViewMsgOn = itemView.findViewById(R.id.textViewMsgOn);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
        }
    }
}
