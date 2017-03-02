package com.example.sandeepkumar.shareidea;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sign_Up extends AppCompatActivity {
    private EditText mNameField,mEmailField,mPasswordField;
    private Button mRegisterBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mProgress=new ProgressDialog(this);
        mNameField=(EditText)findViewById(R.id.nameField);
        mEmailField=(EditText)findViewById(R.id.emailField);
        mPasswordField=(EditText)findViewById(R.id.passwordField);
        mRegisterBtn=(Button)findViewById(R.id.registerBtn);


        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name=mNameField.getText().toString().trim();
                String email=mEmailField.getText().toString().trim();
                String password=mPasswordField.getText().toString().trim();

                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    mProgress.setMessage("Signing...");
                    mProgress.show();

                    mAuth.createUserWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String user_id=mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db=mDatabase.child(user_id);
                                current_user_db.child("name").setValue(name);
                                current_user_db.child("image").setValue("default");
                                mProgress.dismiss();
                                Intent mainIntent=new Intent(Sign_Up.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainIntent);
                                finish();
                            }else {
                                mProgress.dismiss();
                                AlertDialogCreate();
                            }
                        }
                    });
                }
            }
        });
    }

    public void AlertDialogCreate(){

        new AlertDialog.Builder(Sign_Up.this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("ShareIdea")
                .setMessage("Please enter password at least 8 character")
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(Sign_Up.this, "", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(Sign_Up.this, "", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }


}
