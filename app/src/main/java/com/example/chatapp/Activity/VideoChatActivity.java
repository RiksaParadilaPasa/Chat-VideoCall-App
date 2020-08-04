package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoChatActivity extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener {

    private static String API_KEY = "46856484";
    private static String SESSION_ID = "2_MX40Njg1NjQ4NH5-MTU5NTU5MDAxNzgxMX5QcHoxQUkvSnpaem9xUUl6bndpTzQ3a0h-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00Njg1NjQ4NCZzaWc9ZDY1MTdkNGY0NTZhY2M0MGNmZDVhY2ZkOWJlMzJhOTMxMDE0NTA5NDpzZXNzaW9uX2lkPTJfTVg0ME5qZzFOalE0Tkg1LU1UVTVOVFU1TURBeE56Z3hNWDVRY0hveFFVa3ZTbnBhZW05eFVVbDZibmRwVHpRM2EwaC1mZyZjcmVhdGVfdGltZT0xNTk1NTkwMDg2Jm5vbmNlPTAuMzY1MTM1NTYwMDI4Mzc1MiZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTk4MTgyMDg2JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static final String LOG_TAG = VideoChatActivity.class.getSimpleName();
    private static final int RC_VIDEO_APP_PERM = 124;

    private ImageView btnCloseVideoChat;

    private DatabaseReference userRef;
    private String userID ="";

    private FrameLayout mPublisherView;
    private FrameLayout mSubscriberView;

    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubcriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        userRef = FirebaseDatabase.getInstance().getReference().child("User");

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mPublisherView = findViewById(R.id.videochat_publisher_container);
        mSubscriberView = findViewById(R.id.videochat_subscriber_container);

        requestpermission();

        btnCloseVideoChat = findViewById(R.id.videochat_btn_close_video_chat);
        btnCloseVideoChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(userID).hasChild("Ringing")){
                            userRef.child(userID).child("Ringing").removeValue();

                            if (mPublisher != null){
                                mPublisher.destroy();
                            }

                            if (mSubcriber != null){
                                mSubcriber.destroy();
                            }

                            startActivity(new Intent(VideoChatActivity.this, MainActivity.class));
                            finish();
                        }

                        if (dataSnapshot.child(userID).hasChild("Calling")){
                            userRef.child(userID).child("Calling").removeValue();

                            if (mPublisher != null){
                                mPublisher.destroy();
                            }

                            if (mSubcriber != null){
                                mSubcriber.destroy();
                            }

                            startActivity(new Intent(VideoChatActivity.this, MainActivity.class));
                            finish();
                        }else{
                            if (mPublisher != null){
                                mPublisher.destroy();
                            }

                            if (mSubcriber != null){
                                mSubcriber.destroy();
                            }

                            startActivity(new Intent(VideoChatActivity.this, MainActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        requestpermission();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, VideoChatActivity.this);

    }


    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    public void requestpermission(){
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        mPublisherView = findViewById(R.id.videochat_publisher_container);
        mSubscriberView = findViewById(R.id.videochat_subscriber_container);

        if (EasyPermissions.hasPermissions(this,perms)){

            //1.initialize and connect to the session

            mSession = new Session.Builder(this, API_KEY, SESSION_ID).build();
            mSession.setSessionListener(VideoChatActivity.this);
            mSession.connect(TOKEN);
        }else{
            EasyPermissions.requestPermissions(this,"Hey this app needs mic and camera, please allow",RC_VIDEO_APP_PERM);
        }

    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    //2. Publishing a stream to the session
    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG, "Session Connected");

        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(VideoChatActivity.this);

        mPublisherView.addView(mPublisher.getView());

        if (mPublisher.getView() instanceof GLSurfaceView){
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "Stream Disconnected  ");
    }

    //3. subscribing to the streams
    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Received  ");

        if (mSubcriber == null){
            mSubcriber = new Subscriber.Builder(this,stream).build();
            mSession.subscribe(mSubcriber);
            mSubscriberView.addView(mSubcriber.getView());
        }

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped  ");

        if (mSubcriber != null){
            mSubcriber = null;
            mSubscriberView.removeAllViews();
        }

    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.i(LOG_TAG, "Stream Error  ");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }




}
