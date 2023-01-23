package edu.uncc.hw08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import edu.uncc.hw08.databinding.FragmentMyChatsBinding;

public class MyChatsFragment extends Fragment implements MyChatsRecyclerViewAdapter.MyChatsRecyclerViewInterface{

    FragmentMyChatsBinding binding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String currentUserId = mAuth.getCurrentUser().getUid();

    String TAG = "deep";

    ArrayList<Message> messageArrayList = new ArrayList<>();
    LinearLayoutManager layoutManager;
    MyChatsRecyclerViewAdapter adapter;
    RecyclerView recyclerView;

    public MyChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyChatsBinding.inflate(inflater,container,false);
        return  binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("My Chats");

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.logout();
            }
        });

        binding.buttonNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToNewChat();
            }
        });

        CollectionReference colRef = db.collection("chats");

        Query query = colRef.whereArrayContains("user_list",currentUserId);


        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        messageArrayList.clear();
                        for (QueryDocumentSnapshot document : value) {
                            Log.d("deep", "onEvent: "+document.getData());
                            Message msg = new Message();
                            msg = document.toObject(Message.class);
                            msg.message_id = document.getId();
                            messageArrayList.add(msg);
                        }

                        Collections.sort(messageArrayList, new Comparator<Message>() {
                            @Override
                            public int compare(Message message, Message t1) {
                                return -1 * message.getLast_msg_timestamp_real().compareTo(t1.getLast_msg_timestamp_real());
                            }
                        });

                        adapter.notifyDataSetChanged();
                    }
                });

        recyclerView = binding.UsersRecyclerView;
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MyChatsRecyclerViewAdapter(messageArrayList,this);
        recyclerView.setAdapter(adapter);


    }

    MyChatsFragInterface mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (MyChatsFragInterface) context;
    }

    @Override
    public void goToViewChat(String messageId, String name) {
        mListener.openViewChat(messageId,name);
    }

    interface MyChatsFragInterface{
        void logout();
        void goToNewChat();
        void openViewChat(String message_id,String name);
    }

    public void setOnlineStatus(String uId){
        if (!uId.isEmpty()){

            HashMap<String,Object> val = new HashMap<>();
            val.put("is_online",true);

            db.collection("chat_users")
                    .document(currentUserId)
                    .update(val)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(getActivity(), "Online Status not updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }
}