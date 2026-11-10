package com.sonymobile.experienceflow.wallpaper;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class MasterResetReceiver extends BroadcastReceiver {
    private static final String ACTION_MASTERRESET = "com.sonyericsson.settings.MASTERRESET";
    public static final boolean DEBUG = false;
    public static final String LOG_TAG = "MasterResetReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (ACTION_MASTERRESET.equals(intent.getAction())) {
            Intent service = new Intent(context, (Class<?>) ResetPreferencesService.class);
            context.startService(service);
        }
    }

    /* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
    public static final class ResetPreferencesService extends IntentService {
        public ResetPreferencesService() {
            super("resetPrefsService");
        }

        @Override // android.app.IntentService
        protected void onHandleIntent(Intent intent) {
            getSharedPreferences(ExperienceFlowPreferencesActivity.SHARED_PREFS_NAME, 0).edit().clear().apply();
        }
    }
}
