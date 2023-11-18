package com.example.inboxmsg;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class SmsListenerService extends Service {

    private SmsReceiver smsReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        registerSmsReceiver();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterSmsReceiver();
    }

    private void registerSmsReceiver() {
        smsReceiver = new SmsReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.Sms.Intents.SMS_RECEIVED");
        intentFilter.setPriority(1000); // Set a higher priority to receive broadcasts before the system
        Log.i("LOVE","onStartCommand");
        registerReceiver(smsReceiver, intentFilter);
    }

    private void unregisterSmsReceiver() {
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
    }
}
