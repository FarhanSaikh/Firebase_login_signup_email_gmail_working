package com.a2zdaddy.dani.loginsignupfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.a2zdaddy.dani.loginsignupfirebase.Main2Activity;
import com.a2zdaddy.dani.loginsignupfirebase.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;


    //widgets
    private TextView mRegister,forgotpass;
    private EditText mEmail, mPassword;
    private Button mLogin;
    private ProgressBar mProgressBar;
    private ImageView mLogo;
    ImageButton googlebutton;
    View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mRegister = (TextView) findViewById(R.id.link_register);
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mLogin = (Button) findViewById(R.id.btn_login);
        mLogo = (ImageView) findViewById(R.id.logo);
        forgotpass=findViewById(R.id.forgotpass);
           googlebutton=findViewById(R.id.googlebutton);
         parentLayout= findViewById(android.R.id.content);

        initProgressBar();
        setupFirebaseAuth();
        init();




    }




    private void init(){
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if the fields are filled out
                if(!isEmpty(mEmail.getText().toString())
                        && !isEmpty(mPassword.getText().toString())){
                    Log.d(TAG, "onClick: attempting to authenticate.");

                    showProgressBar();

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail.getText().toString(),
                            mPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    hideProgressBar();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();

                            Snackbar snackbar = Snackbar
                                    .make(parentLayout, "Email id or Password is Incorrect", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            hideProgressBar();
                        }
                    });
                }else{

                    mEmail.setError("Enter Email");

                    mPassword.setError("Enter Password");

                    //Toast.makeText(LoginActivity.this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating to Register Screen");

                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });


        hideSoftKeyboard();
    }


    /**
     * Return true if the @param is null
     * @param string
     * @return
     */
    private boolean isEmpty(String string){
        return string.equals("");
    }


    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideProgressBar(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }




    private void initProgressBar(){
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
    }


    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
        ----------------------------- Firebase setup ---------------------------------
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    //check if email is verified
                    if(user.isEmailVerified()){
                        Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                        Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }else{
                        //Toast.makeText(LoginActivity.this, "Email is not Verified\nCheck your Inbox", Toast.LENGTH_SHORT).show();
                        Snackbar snackbar = Snackbar
                                .make(parentLayout, "Please verify your email id", Snackbar.LENGTH_LONG);
                        snackbar.show();

                        FirebaseAuth.getInstance().signOut();
                    }

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    public void forgotpassword(View view) {
        Intent intent=new Intent(this,ResetPasswordActivity.class);
        startActivity(intent);


    }

    public void googlelogin(View view) {

        Intent intent=new Intent(this,LoginActivityGgl.class);
        startActivity(intent);


    }
}
