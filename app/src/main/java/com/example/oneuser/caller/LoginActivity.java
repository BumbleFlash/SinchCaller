package com.example.oneuser.caller;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.SinchError;

public class LoginActivity extends BaseActivity implements SinchService.StartFailedListener {
    ProgressBar progressBar;
    EditText sender;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        progressBar = new ProgressBar(this);
        sender = findViewById(R.id.loginName);
        login = findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginClicked();
            }
        });


    }

    @Override
    protected void onServiceConnected(IBinder iBinder) {
        super.onServiceConnected(iBinder);
        login.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
    }

    private void loginClicked() {
        String userName = sender.getText().toString();

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        if (!userName.equals(getSinchServiceInterface().getUserName())) {
            getSinchServiceInterface().stopClient();
        }

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
        } else {
            startActivity(new Intent(this, PlacaCallActivity.class));
        }
    }


    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStarted() {

    }
}
