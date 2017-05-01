package com.gelecegiyazanlar.tarifsepeti.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gelecegiyazanlar.tarifsepeti.R;
import com.gelecegiyazanlar.tarifsepeti.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText inputEmail, inputPassword;
    private Button btnLogin, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase mAuth instance
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnLogin = (Button) findViewById(R.id.login_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnResetPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.sign_up_button:

                signUp();

                break;
            case R.id.login_button:

                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();

                break;
            case  R.id.btn_reset_password:

                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
                finish();

                break;

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void signUp() {

        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {

            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();

            return;
        }

        if (TextUtils.isEmpty(password)) {

            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();

            return;
        }

        if (password.length() < 6) {

            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();

            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //create user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);

                        if (task.isSuccessful()) {

                            onAuthSuccess(task.getResult().getUser());

                        } else {

                            Toast.makeText(SignupActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }

                });


    }

    @Override
    protected void onResume() {
        super.onResume();

        progressBar.setVisibility(View.INVISIBLE);

    }

    private void onAuthSuccess(FirebaseUser user) {

        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        startActivity(new Intent(SignupActivity.this, MainActivity.class));
        finish();

    }

    private void writeNewUser(String userId, String name, String email){

        User user = new User(name, email);
        mDatabase.child("users").child(userId).setValue(user);

    }

    private String usernameFromEmail(String email) {

        if (email.contains("@")) {

            return email.split("@")[0];

        } else {

            return email;
        }

    }

}