package com.example.sandeepkumar.shareidea;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.example.sandeepkumar.shareidea.R.color.colorPrimaryDark;

public class ViewPostBlogs extends AppCompatActivity {
    private String mPost_key=null;
    private DatabaseReference mDatabase,mDatabaseViews;
    private ImageView mSinglePost;
    private TextView mPostTitle,mPostBlog;
    private FirebaseAuth mAuth;
    public boolean textView=true;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post_blogs);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
       // collapsingToolbarLayout.setTitle(getResources().getString(R.string.user_name));
        mAuth=FirebaseAuth.getInstance();
        mPostTitle=(TextView)findViewById(R.id.post_title);
        mPostBlog=(TextView)findViewById(R.id.post_blog);
        mSinglePost=(ImageView)findViewById(R.id.profile_id);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blogs");
        mDatabaseViews=FirebaseDatabase.getInstance().getReference().child("No_Blog_Views");

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
                collapsingToolbarLayout.setTitle(post_title);

                Picasso.with(ViewPostBlogs.this).load(post_image).fit().centerCrop().into(mSinglePost);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseViews.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                if (textView) {
                    if (dataSnapshot.child(mPost_key).hasChild(mAuth.getCurrentUser().getUid())) {
                        textView=false;
                    } else {
                        String view = (String) dataSnapshot.child(mPost_key).child("views").getValue();
                        if (view == null) {
                            mDatabaseViews.child(mPost_key).child("views").setValue("1");
                        } else {
                            int noViews = Integer.valueOf(view);
                            noViews = noViews + 1;
                            String s = String.valueOf(noViews);
                            mDatabaseViews.child(mPost_key).child("views").setValue(s);
                        }
                        mDatabaseViews.child(mPost_key).child(mAuth.getCurrentUser().getUid()).setValue("true");
                        textView=false;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


}



