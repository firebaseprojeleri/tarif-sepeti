package com.gelecegiyazanlar.tarifsepeti.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gelecegiyazanlar.tarifsepeti.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Button btnLogin, btnReset, sign_up_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.i("TAG", "test1446 loginAct");

        //Get Firebase mAuth instance
        mAuth = FirebaseAuth.getInstance();

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        sign_up_button = (Button)findViewById(R.id.sign_up_button);

        btnReset.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        sign_up_button.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_login:

                login();

                break;
            case R.id.btn_reset_password:

                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));

                break;
            case R.id.sign_up_button:

                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();

                break;

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void login() {

        String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {

            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();

            return;
        }

        if (TextUtils.isEmpty(password)) {

            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();

            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //authenticate user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressBar.setVisibility(View.INVISIBLE);
                        if (!task.isSuccessful()) {

                            // there was an error
                            if (password.length() < 6) {

                                inputPassword.setError(getString(R.string.minimum_password));

                            } else {

                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();

                            }

                        } else {

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }

                    }

                });

    }
}
