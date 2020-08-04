package com.example.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.Forum;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Forum> mForum;

    private String imageurl;

    FirebaseUser mUser;

    public ForumAdapter(Context mContext, List<Forum> mForum,String imageurl){
        this.mContext = mContext;
        this.mForum = mForum;
        this.imageurl = imageurl;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.message_show);
            profile_image = itemView.findViewById(R.id.message_image);
            txt_seen = itemView.findViewById(R.id.message_txt_seen);

        }
    }

    @NonNull
    @Override
    public ForumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_chat_right,parent,false);
            return new ForumAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_chat_left,parent,false);
            return new ForumAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ForumAdapter.ViewHolder holder, int position) {

        Forum forum = mForum.get(position);

        holder.show_message.setText(forum.getMessage());

        if (imageurl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Picasso.get().load(imageurl).into(holder.profile_image);
        }

    }

    @Override
    public int getItemCount() {
        return mForum.size();
    }

    @Override
    public int getItemViewType(int position) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mForum.get(position).getUserId().equals(mUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }

    }

}
