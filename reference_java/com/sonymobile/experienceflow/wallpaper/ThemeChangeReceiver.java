package com.sonymobile.experienceflow.wallpaper;

import android.app.IntentService;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import com.sonymobile.experienceflow2.R;
import com.sonymobile.runtimeskinning.RuntimeSkinningUtil;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class ThemeChangeReceiver extends BroadcastReceiver {
    public static final boolean DEBUG = false;
    public static final String LOG_TAG = "ThemeChangeReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        WallpaperManager wallpaperMgr;
        WallpaperInfo wallpaperInfo;
        try {
            if (RuntimeSkinningUtil.isLiveWallpaperUsed(intent) && !RuntimeSkinningUtil.isInitialSetup(intent) && (wallpaperMgr = WallpaperManager.getInstance(context)) != null && (wallpaperInfo = wallpaperMgr.getWallpaperInfo()) != null) {
                String name = wallpaperInfo.getPackageName();
                if (name.contentEquals(context.getPackageName())) {
                    context.startService(new Intent(context, (Class<?>) ApplyAccentColorService.class));
                }
            }
        } catch (IllegalArgumentException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int getThemeWallpaperColor(Context context) {
        Resources res = context.getResources();
        int preferencesColorIndex = res.getInteger(R.integer.config_wallpaperPaletteID);
        if (preferencesColorIndex >= ColorPicker.getDefaultLiveWallpaperIndex(context)) {
            context.getSharedPreferences(ExperienceFlowPreferencesActivity.SHARED_PREFS_NAME, 0).edit().putInt(ExperienceFlowPreferencesActivity.PREFS_KEY_COLOR, preferencesColorIndex).apply();
        } else {
            Log.w(LOG_TAG, "Theme wallpaper color is NOT valid: " + preferencesColorIndex);
        }
        return preferencesColorIndex;
    }

    /* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
    public static final class ApplyAccentColorService extends IntentService {
        public ApplyAccentColorService() {
            super("applyAccentColorService");
        }

        @Override // android.app.IntentService
        protected void onHandleIntent(Intent intent) {
            ThemeChangeReceiver.getThemeWallpaperColor(getApplicationContext());
        }
    }
}
