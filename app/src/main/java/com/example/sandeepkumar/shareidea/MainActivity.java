package com.example.sandeepkumar.shareidea;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mBlog_list;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseLike,mDatabaseShowTime;
    private FirebaseAuth mAuth;
    TextView likeNo;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean mProcessLike=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth=FirebaseAuth.getInstance();
        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null){
                    Intent loginIntent=new Intent(MainActivity.this,Login_Activity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };
        mBlog_list=(RecyclerView)findViewById(R.id.blog_list);
        mBlog_list.setHasFixedSize(true);
        mBlog_list.setLayoutManager(new LinearLayoutManager(this));
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blogs");
        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
        //mDatabaseShowTime=FirebaseDatabase.getInstance().getReference().child("Posted_Time");

    }
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, Blog model, int position) {
                final String post_key= getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                //viewHolder.setPost(model.getPost());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setLikeBtn(post_key);
                viewHolder.setAllLikes(post_key);
                viewHolder.setAllUserViews(post_key);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singleBlogIntent=new Intent(MainActivity.this,ViewPostBlogs.class);
                        singleBlogIntent.putExtra("blog_id",post_key);
                        startActivity(singleBlogIntent);
                        //Toast.makeText(MainActivity.this, "Clicked on page", Toast.LENGTH_SHORT).show();
                    }
                });
                viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessLike=true;

                        //mDatabaseLike.child(post_key).child("likes").setValue("0");
                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            //TextView likeNo=(TextView)findViewById(R.id.likeNo);
                            TextView likeNo=(TextView)viewHolder.mView.findViewById(R.id.likeNo);
                            @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (mProcessLike) {
                                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                            String like=(String) dataSnapshot.child(post_key).child("likes").getValue();
                                            int noLike=Integer.valueOf(like);
                                            noLike=noLike-1;
                                            String s=String.valueOf(noLike);
                                            //TextView tv=(TextView)findViewById(R.id.likeNo);
                                            likeNo.setText(s);
                                            mDatabaseLike.child(post_key).child("likes").setValue(s);
                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                            mProcessLike = false;
                                        } else {
                                            String like=(String) dataSnapshot.child(post_key).child("likes").getValue();
                                            if(like==null){
                                                like="1";
                                                likeNo.setText(like);
                                                mDatabaseLike.child(post_key).child("likes").setValue(like);
                                                mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("true");
                                                mProcessLike = false;
                                            }else {
                                                int noLike = Integer.valueOf(like);
                                                noLike = noLike + 1;
                                                String s = String.valueOf(noLike);
                                                likeNo.setText(s);
                                                mDatabaseLike.child(post_key).child("likes").setValue(s);
                                                mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("true");
                                                mProcessLike = false;
                                            }
                                        }
                                    }

                                }   @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                });
            }
        };
        mBlog_list.setAdapter(firebaseRecyclerAdapter);
    }
    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton mLikeBtn;
        TextView textLike,userViews;
        TextView tv;
        DatabaseReference mDatabaseLike1,mDatabaseViews;
        FirebaseAuth mAuth;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            userViews=(TextView)mView.findViewById(R.id.userViews);
            tv=(TextView)mView.findViewById(R.id.likeNo);
            textLike=(TextView)mView.findViewById(R.id.textLike);
            mLikeBtn=(ImageButton)mView.findViewById(R.id.likeBtn);
            mDatabaseLike1=FirebaseDatabase.getInstance().getReference().child("Likes");
            mDatabaseViews=FirebaseDatabase.getInstance().getReference().child("No_Blog_Views");
            mAuth=FirebaseAuth.getInstance();
        }

        public void setLikeBtn(final String post_key){
            //mDatabaseLike1.child(post_key).child("likes").setValue("0");
            mDatabaseLike1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        mLikeBtn.setImageResource(R.mipmap.liked);
                        textLike.setTextColor(Color.parseColor("#26ae90"));
                    }else{
                        mLikeBtn.setImageResource(R.mipmap.like);
                        textLike.setTextColor(Color.parseColor("#AAAAAA"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void setAllLikes(final String post_key){
            mDatabaseLike1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String s= (String) dataSnapshot.child(post_key).child("likes").getValue();
                    if(s==null){
                        tv.setText("0");
                    }else{
                        tv.setText(s);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        public void setAllUserViews( final String post_key){
            mDatabaseViews.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String s=(String) dataSnapshot.child(post_key).child("views").getValue();
                    if(s == null){
                        userViews.setText("0"+" "+ "views");
                    }else{
                        userViews.setText(s+" "+"views");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void setTitle(String title){
            TextView postTilte=(TextView)mView.findViewById(R.id.post_title);
            postTilte.setText(title);
        }

        /*public void setPost(String post){
            TextView postBlog=(TextView)mView.findViewById(R.id.post_blog);
            postBlog.setText(post);
        }*/
        public void setUsername(String username){
            TextView mPostUserName=(TextView)mView.findViewById(R.id.postUsername);
            mPostUserName.setText(username);
        }
        public void setImage(Context ctx , String image){
            ImageView post_image=(ImageView)mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }
        public void setLike(String like){
            TextView likes=(TextView)mView.findViewById(R.id.likeNo);
            likes.setText(like);
        }
    }


    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override



    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_add){
            startActivity(new Intent(MainActivity.this,Post_Activity.class));
        }
        if(id == R.id.action_logout){
            logout();
        }

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure you want to exit?")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                }).setNegativeButton("No", null).show();
    }
}
