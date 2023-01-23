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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import edu.uncc.hw08.databinding.FragmentCreateChatBinding;


public class CreateChatFragment extends Fragment implements CreateChatRecyclerViewAdapter.CreateChatRecyclerInterface{

    FragmentCreateChatBinding binding;
    ArrayList<ChatUsers> chatUsersArrayList = new ArrayList<>();
    LinearLayoutManager layoutManager;
    CreateChatRecyclerViewAdapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    String TAG = "deep";
    String user_id_for_chat = null;
    String user_name_for_chat = null;

    public CreateChatFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("New Chat");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateChatBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goBackToMyChats();
            }
        });

        db.collection("chat_users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        chatUsersArrayList.clear();
                        for (QueryDocumentSnapshot docuemnt : value) {
                            ChatUsers chatUsers = new ChatUsers();
                            chatUsers = (ChatUsers) docuemnt.toObject(ChatUsers.class);
                            if (!chatUsers.getUser_id().equalsIgnoreCase(mAuth.getUid())){
                                chatUsersArrayList.add(chatUsers);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });

        binding.UsersRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        binding.UsersRecyclerView.setLayoutManager(layoutManager);

        adapter = new CreateChatRecyclerViewAdapter(chatUsersArrayList,this);
        binding.UsersRecyclerView.setAdapter(adapter);

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msg = binding.editTextMessage.getText().toString();

                if (user_id_for_chat == null){
                    Toast.makeText(getActivity(), "Select User to send Message", Toast.LENGTH_SHORT).show();
                }else if (msg.isEmpty()){
                    Toast.makeText(getActivity(), "Please Enter Message", Toast.LENGTH_SHORT).show();
                }else{
//                    HashMap<String, Object> data = new HashMap<>();

                    HashMap<String,Object> userData = new HashMap<>();
                    userData.put("user_one",mAuth.getUid());
                    userData.put("user_one_name",mAuth.getCurrentUser().getDisplayName());
                    userData.put("user_two",user_id_for_chat);
                    userData.put("user_two_name",user_name_for_chat);
                    userData.put("last_msg_timestamp", new Timestamp(new Date()));
                    userData.put("last_msg",msg);

                    ArrayList<String> val = new ArrayList<>();
                    val.add(mAuth.getUid());
                    val.add(user_id_for_chat);

                    userData.put("user_list",val);
//                    data.put("users",userData);

                    HashMap<String,Object> messageData = new HashMap<>();
                    messageData.put("msg",msg);
                    messageData.put("timestamp", FieldValue.serverTimestamp());
                    messageData.put("user_id",mAuth.getUid());
                    messageData.put("user_name",mAuth.getCurrentUser().getDisplayName());

//                    data.put("messages",messageData);

                    DocumentReference docRef = db.collection("chats").document();

                    docRef.set(userData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                docRef.collection("messages")
                                        .add(messageData)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "onComplete: saved");
                                                binding.editTextMessage.setText(null);
                                                mListener.goBackToMyChats();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "Error : Message not sent", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onComplete: "+e.getMessage());
                                Toast.makeText(getActivity(), "Error : Message not sent", Toast.LENGTH_SHORT).show();

                            }
                        });


                }
            }
        });

    }

    CreateChatInterface mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CreateChatInterface) context;
    }

    @Override
    public void sendUserData(String userName, String userId) {
        this.user_id_for_chat = userId;
        this.user_name_for_chat = userName;

        binding.textViewSelectedUser.setText(userName);
    }

    interface CreateChatInterface{
        void goBackToMyChats();
    }


}