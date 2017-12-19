package com.example.oneuser.caller;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.calling.Call;


public class PlacaCallActivity extends BaseActivity implements android.view.View.OnClickListener {
    private Button mCallButton;
    private EditText mCallName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mCallName = findViewById(R.id.callName);
        mCallButton = findViewById(R.id.callButton);
        mCallButton.setEnabled(false);
        mCallButton.setOnClickListener(this);

        Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(this);


    }

    private void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }

    private void callButtonClicked() {
        String userName = mCallName.getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }

        Call call = getSinchServiceInterface().callUser(userName);
        String callId = call.getCallId();

        Intent callScreen = new Intent(getApplicationContext(), CallActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        startActivity(callScreen);
    }

    @Override
    protected void onServiceConnected(IBinder iBinder) {
        super.onServiceConnected(iBinder);
        TextView userName = findViewById(R.id.loggedInName);
        userName.setText(getSinchServiceInterface().getUserName());
        mCallButton.setEnabled(true);
    }

    @Override
    public void onClick(android.view.View view) {
        switch (view.getId()) {
            case R.id.callButton:
                callButtonClicked();
                break;

            case R.id.stopButton:
                stopButtonClicked();
                break;

        }
    }
}
