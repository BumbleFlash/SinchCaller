package com.example.oneuser.caller;

import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class CallActivity extends BaseActivity {
    private Call call;
    private TextView callState;
    private SinchClient sinchClient;
    private Button button;
    private String callerId;
    private String recipientId;
    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    Ringtone r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        Intent intent = getIntent();
        callerId = intent.getStringExtra("callerId");
        recipientId = intent.getStringExtra("recipientId");
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(callerId)
                .applicationKey("32948f49-92b4-4b3f-9cbc-666f01cf1d0d")
                .applicationSecret("ng74/3ajWUagSpGfShyk4Q==")
                .environmentHost("clientapi.sinch.com")
                .build();
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

        button = (Button) findViewById(R.id.button);
        callState = (TextView) findViewById(R.id.callState);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (call==null) {
                    Log.d("Call",button.getText()+"");
                    call = sinchClient.getCallClient().callUser(recipientId);
                    call.addCallListener(new SinchCallListener());
                    button.setText("Hang Up");
                }
                else
                    if(button.getText().toString().equals("Answer")){
                           call.answer();
                           button.setText("Hang Up");
                }
                        else {
                    call.hangup();
                }
            }
        });
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            button.setText("Call");
            callState.setText("");
            r.stop();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            callState.setText("connected");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            callState.setText("ringing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {}
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;

            r.play();
            button.setText("Answer");
            call.addCallListener(new SinchCallListener());
            //button.setText("Hang Up");


        }
    }
}

