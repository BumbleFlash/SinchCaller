package com.example.oneuser.caller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;

public class ForegroundService extends Service {
    public SinchClient sinchClient;
    public String callerId,recipientid;
    public ForegroundService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
