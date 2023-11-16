package com.example.foodmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.foodmanager.databinding.ActivityLoginBinding;
import com.example.foodmanager.model.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(getColor(R.color.brown_120));
        setBindingAnimation();

        setBindingAnimation();
        binding.tvForgotPass.setOnClickListener(v -> {

        });

        binding.tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });

        binding.btnLogin.setOnClickListener(v -> {
            if (validate()) {
                onClickSignIn();
            }
        });

    }

    private void onClickSignIn() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String strEmail = binding.email.getText().toString().trim();
        String strPass = binding.password.getText().toString().trim();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutLogin.setAlpha(0.2f);

        mAuth.signInWithEmailAndPassword(strEmail, strPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            getToken();
                            Toast.makeText(LoginActivity.this, getString(R.string.notifi_login_success), Toast.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.VISIBLE);
                            finishAffinity();
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.notifi_login_fail), Toast.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.GONE);
                            binding.layoutLogin.setAlpha(1f);
                        }
                    }
                });

    }
    private void getToken() {
        String firebaseAuth = FirebaseAuth.getInstance().getUid();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                //If task is failed then
                if (!task.isSuccessful()) {
                    Log.d("TAG", "onComplete: Failed to get the Token");
                }
                //Token
                String token = task.getResult();
                Log.d("TAG", "onComplete: " + token);
                Token token1 = new Token(token);
                Log.d("TAG", "onComplete: "+firebaseAuth);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("Tokens")
                        .child(firebaseAuth)
                        .setValue(token1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });


            }
        });
    }
    private boolean validate() {
        String strEmail = binding.email.getText().toString().trim();
        String strPass = binding.password.getText().toString().trim();
        if (TextUtils.isEmpty(strEmail)) {
            binding.email.setError(getString(R.string.error_email_1), null);
            binding.email.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            binding.email.setError(getString(R.string.error_email_2), null);
            binding.email.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(strPass)) {
            binding.password.setError(getString(R.string.error_pass_1), null);
            binding.password.requestFocus();
            return false;
        } else {
            return true;
        }
    }
    public void setBindingAnimation() {
        viewAnimation(binding.icLogo, "translationY", -400f, 0f);
        viewAnimation(binding.tvIcon, "translationY", -400f, 0f);
        viewAnimation(binding.email, "translationX", -300f, 0f);
        viewAnimation(binding.tilPassword, "translationX", 300f, 0f);
        viewAnimation(binding.tvForgotPass, "translationY", -400f, 0f);
        viewAnimation(binding.cavButton, "translationY", 400f, 0f);
        viewAnimation(binding.tvContent, "translationX", -200f, 0f);
        viewAnimation(binding.tvSignUp, "translationX", 200f, 0f);
        viewAnimation(binding.line1, "translationX", -200f, 0f);
        viewAnimation(binding.line2, "translationX", 200f, 0f);
//        viewAnimation(binding.tvContent2, "translationY", 300f, 0f);
        viewAnimation(binding.cavGoogle, "translationY", 300f, 0f);

    }
    public void viewAnimation(View view, String ani, float... values) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, ani, values);
        animator.setDuration(1800);
        animator.start();
    }


}