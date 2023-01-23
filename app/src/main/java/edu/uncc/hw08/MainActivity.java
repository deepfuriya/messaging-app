package edu.uncc.hw08;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements MyChatsFragment.MyChatsFragInterface, CreateChatFragment.CreateChatInterface,ChatFragment.ChatFragmentInterface {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    AuthActivity authActivity = new AuthActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView,new MyChatsFragment())
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth.getCurrentUser() != null){
            authActivity.setOnlineStatus(mAuth.getCurrentUser().getUid(),false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
            authActivity.setOnlineStatus(mAuth.getCurrentUser().getUid(),true);
        }
    }

    @Override
    public void logout() {

        AuthActivity a = new AuthActivity();
        a.setOnlineStatus(mAuth.getCurrentUser().getUid(),false);

        mAuth.signOut();

        setContentView(R.layout.activity_auth);

        Intent intent = new Intent(this,AuthActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goToNewChat() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView,new CreateChatFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void openViewChat(String message_id,String name) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView,new ChatFragment().newInstance(message_id,name))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goBackToMyChats() {
        getSupportFragmentManager().popBackStack();
    }


    @Override
    public void goBackToChatsFrag() {
        getSupportFragmentManager().popBackStack();
    }
}