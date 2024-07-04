package com.example.retrievam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ProfilePostsAdaptor extends RecyclerView.Adapter<ProfilePostsAdaptor.viewholder> {
    ArrayList<String> postsImages;
    Context context;

    ProfilePostsAdaptor(ArrayList<String> postsImages,Context context)
    {
        this.postsImages=postsImages;
        this.context=context;
    }
    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_profile_totalpost,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        String img = postsImages.get(position);

        Glide.with(context.getApplicationContext())
                .load(img)
                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                .into(holder.ivSingle_profile_totalpost);


        //soon

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                userId=users.getId();
//                Intent intent = new Intent(activity, chatBoxActivity.class);
//                intent.putExtra("userId",userId);
//                activity.startActivity(intent);
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return postsImages.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        ImageView ivSingle_profile_totalpost;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            ivSingle_profile_totalpost=itemView.findViewById(R.id.ivSingle_profile_totalpost);
        }
    }
}
