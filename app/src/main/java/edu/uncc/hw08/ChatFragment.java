package edu.uncc.hw08;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import edu.uncc.hw08.databinding.FragmentChatBinding;

public class ChatFragment extends Fragment {


    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    ChatFragmentRecyclerView adapter;

    ArrayList<MessageList> messageListArrayList = new ArrayList<>();
    AlertDialog.Builder builder;

    private static final String MSG_ID = "msg_id";
    private static final String MSG_NAME = "msg_name";

    private String message_id;
    private String name;

    public ChatFragment() {
        // Required empty public constructor
    }


    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(MSG_ID, param1);
        args.putString(MSG_NAME, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message_id = getArguments().getString(MSG_ID);
            name = getArguments().getString(MSG_NAME);
        }
    }

    FragmentChatBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Chat - "+name.toUpperCase());

        binding.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goBackToChatsFrag();
            }
        });

        recyclerView = binding.recyclerView;

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ChatFragmentRecyclerView(messageListArrayList,message_id);
        recyclerView.setAdapter(adapter);

        db.collection("chats")
                .document(message_id)
                .collection("messages")
                .orderBy("timestamp",Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        messageListArrayList.clear();
                        if (!value.isEmpty()){
                            for (QueryDocumentSnapshot document : value) {
                                MessageList msg = document.toObject(MessageList.class);
                                msg.single_message_id = document.getId();
                                messageListArrayList.add(msg);
                            }
                            recyclerView.scrollToPosition(messageListArrayList.size() - 1);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });


        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = binding.editTextMessage.getText().toString().trim();
                if (msg.isEmpty()){
                    Toast.makeText(getActivity(), "Please Enter message", Toast.LENGTH_SHORT).show();
                }else{
                    HashMap<String,Object> data = new HashMap<>();
                    data.put("msg",msg);
                    data.put("timestamp", new Timestamp(new Date()));
                    data.put("user_id",mAuth.getUid());
                    data.put("user_name",mAuth.getCurrentUser().getDisplayName());

                    HashMap<String,Object> updateData = new HashMap<>();
                    updateData.put("last_msg",msg);
                    updateData.put("last_msg_timestamp",new Timestamp(new Date()));

                    db.collection("chats")
                            .document(message_id)
                            .collection("messages")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    binding.editTextMessage.setText(null);

                                    db.collection("chats")
                                                    .document(message_id)
                                                            .update(updateData)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isComplete()){
                                                                            }else{
                                                                                Toast.makeText(getActivity(), "Message not sent", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Message not sent", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        builder = new AlertDialog.Builder(getContext());

        binding.buttonDeleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Alert")
                        .setMessage("Are you sure you want to Delete ?")
                        .setCancelable(true)
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.collection("chats")
                                        .document(message_id)
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {


                                                if (task.isSuccessful()){

                                                    db.collection("chats/"+message_id+"/messages").get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        db.collection("chats/"+message_id+"/messages")
                                                                                .document(document.getId()).delete();
                                                                    }
                                                                }
                                                            });

                                                    mListener.goBackToChatsFrag();
                                                }else{
                                                    Toast.makeText(getActivity(), "Error Deleting Chat", Toast.LENGTH_SHORT).show();
                                                }

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

    ChatFragmentInterface mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ChatFragmentInterface) context;
    }

    interface ChatFragmentInterface{
        void goBackToChatsFrag();
    }
}