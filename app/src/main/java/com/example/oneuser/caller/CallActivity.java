package com.example.oneuser.caller;

import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class CallActivity extends BaseActivity {

    static final String TAG = CallActivity.class.getSimpleName();
    private String mCallId;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;
    Button endCallButton;
    String type;
    private TextView mCallDuration;
    private TextView mCallState;
    private TextView mCallerName;

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    public void updateCallDuration() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            mCallDuration.setText(formatTimespan(call.getDetails().getDuration()));
        }
    }

    private String formatTimespan(int totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callscreen);
        mCallDuration = findViewById(R.id.callDuration);
        mCallerName = findViewById(R.id.remoteUser);
        mCallState = findViewById(R.id.callState);
        endCallButton = findViewById(R.id.hangupButton);
        endCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (endCallButton.getText().toString().equals("End Call"))
                    endCall();
                else
                    acceptCall();
            }
        });
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);


    }

    private void endCall() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private void acceptCall() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        call.answer();
        endCallButton.setText("End Call");

    }

    @Override
    public void onServiceConnected(IBinder iBinder) {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            mCallerName.setText(call.getRemoteUserId());
            mCallState.setText(call.getState().toString());
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallProgressing(Call call) {

            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onCallEstablished(Call call) {
            mCallState.setText(call.getState().toString());
        }

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            String endMsg = "Call ended: " + call.getDetails().toString();
            Toast.makeText(CallActivity.this, endMsg, Toast.LENGTH_LONG).show();
            endCall();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }
}




