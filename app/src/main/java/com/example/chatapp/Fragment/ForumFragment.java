package com.example.chatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Activity.MainActivity;
import com.example.chatapp.Activity.MessageActivity;
import com.example.chatapp.Adapter.ForumAdapter;
import com.example.chatapp.Adapter.MessageAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.Forum;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.otwebrtc.ContextUtils.getApplicationContext;

public class ForumFragment extends Fragment {

    ImageButton btn_send;
    EditText et_send;

    FirebaseUser mUser;
    DatabaseReference userRef;
    DatabaseReference reference;

    Intent intent;

    String userId;

    String sender, receiver, message;

    ForumAdapter forumAdapter;
    List<Forum> mForum;
    RecyclerView mRecycler;

    ValueEventListener seenListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum,container,false);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("User").child(mUser.getUid());

        btn_send = view.findViewById(R.id.forum_btn_send);
        et_send = view.findViewById(R.id.forum_et_send);

        mForum = new ArrayList<>();

        mRecycler = view.findViewById(R.id.forum_recycler_container);
        mRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(linearLayoutManager);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                ReadMessage(mUser.getUid(),user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = et_send.getText().toString();
                if (!message.equals("")){
                    SendMessage();
                }else{
                    Toast.makeText(getActivity(), "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                et_send.setText("");
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void SendMessage(){
        reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", mUser.getUid());
        hashMap.put("message", message);

        reference.child("Forum").push().setValue(hashMap);

    }

    private void ReadMessage(final String myid, final String imageurl){

        reference = FirebaseDatabase.getInstance().getReference("Forum");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mForum.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Forum forum = snapshot.getValue(Forum.class);

                    if (forum.getUserId().equals(myid)){
                        mForum.add(forum);
                    }

                    forumAdapter = new ForumAdapter(getActivity(), mForum, imageurl);
                    mRecycler.setAdapter(forumAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}