package com.example.sandeepkumar.shareidea;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BlogSingleActivity extends AppCompatActivity {
    private String mPost_key=null;
    private DatabaseReference mDatabase;
    private ImageView mSinglePost;
    private TextView mPostTitle,mPostBlog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);
        mPostTitle=(TextView)findViewById(R.id.post_title);
        mPostBlog=(TextView)findViewById(R.id.post_blog);
        mSinglePost=(ImageView)findViewById(R.id.post_image);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blogs");
        mPost_key=getIntent().getExtras().getString("blog_id");
        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title=(String)dataSnapshot.child("title").getValue();
                String post_blog=(String)dataSnapshot.child("post").getValue();
                String post_image=(String)dataSnapshot.child("image").getValue();
                String user_id=(String)dataSnapshot.child("uid").getValue();
                mPostTitle.setText(post_title);
                mPostBlog.setText(post_blog);

                Picasso.with(BlogSingleActivity.this).load(post_image).into(mSinglePost);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
