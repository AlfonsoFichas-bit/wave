package com.sonymobile.experienceflow.wallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.sonymobile.experienceflow2.R;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class ColorPicker {
    private static final boolean CLASS_DEBUG = false;
    private static ColorInfo[] sAllColors;
    private static ColorInfo[] sColors;
    private final Context mContext;
    private int mCurrentColorIndex;
    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceListener;
    private SharedPreferences mPreferences;

    public ColorPicker(Context context) {
        this.mContext = context.getApplicationContext();
        this.mPreferences = context.getSharedPreferences(ExperienceFlowPreferencesActivity.SHARED_PREFS_NAME, 0);
        this.mCurrentColorIndex = getColorIndexFromPreferences(this.mPreferences);
        registerPreferencesListener(this.mPreferences);
    }

    private void registerPreferencesListener(SharedPreferences preferences) {
        this.mPreferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: com.sonymobile.experienceflow.wallpaper.ColorPicker.1
            @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (ExperienceFlowPreferencesActivity.PREFS_KEY_COLOR.equals(key)) {
                    ColorPicker.this.mCurrentColorIndex = ColorPicker.this.getColorIndexFromPreferences(ColorPicker.this.mPreferences);
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(this.mPreferenceListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getColorIndexFromPreferences(SharedPreferences sp) {
        if (sp.contains(ExperienceFlowPreferencesActivity.PREFS_KEY_COLOR)) {
            try {
                return sp.getInt(ExperienceFlowPreferencesActivity.PREFS_KEY_COLOR, getDefaultLiveWallpaperIndex(this.mContext));
            } catch (ClassCastException e) {
                Log.w(ExperienceFlowPreferencesActivity.LOG_TAG, "Conflict detected in prefs, drop old one.");
                sp.edit().clear().apply();
            }
        }
        return ThemeChangeReceiver.getThemeWallpaperColor(this.mContext);
    }

    public int getCurrentColorIndex() {
        return this.mCurrentColorIndex;
    }

    public void destroy() {
        this.mPreferences.unregisterOnSharedPreferenceChangeListener(this.mPreferenceListener);
    }

    public static synchronized ColorInfo[] getAllColors() {
        ColorInfo[] colorInfoArr;
        synchronized (ColorPicker.class) {
            if (sAllColors == null) {
                ColorInfo[] semcColors = importAllSemcColors();
                sAllColors = new ColorInfo[semcColors.length];
                System.arraycopy(semcColors, 0, sAllColors, 0, semcColors.length);
            }
            colorInfoArr = (ColorInfo[]) sAllColors.clone();
        }
        return colorInfoArr;
    }

    public static synchronized ColorInfo[] getSemcColors(Context context) {
        ColorInfo[] colorInfoArr;
        ColorInfo[] semcColors;
        synchronized (ColorPicker.class) {
            if (sColors == null) {
                int colorGroupID = context.getResources().getInteger(R.integer.config_colorGroupID);
                switch (colorGroupID) {
                    case 0:
                        semcColors = importAllSemcColors();
                        break;
                    case 1:
                        semcColors = importSemcColorGroup1();
                        break;
                    case 2:
                        semcColors = importSemcColorGroup2();
                        break;
                    case 3:
                        semcColors = importSemcColorGroup3();
                        break;
                    case 4:
                        semcColors = importSemcColorGroup4();
                        break;
                    case 5:
                        semcColors = importVerizonRedColorGroup();
                        break;
                    default:
                        semcColors = importSemcColors();
                        break;
                }
                sColors = new ColorInfo[semcColors.length];
                System.arraycopy(semcColors, 0, sColors, 0, semcColors.length);
            }
            colorInfoArr = (ColorInfo[]) sColors.clone();
        }
        return colorInfoArr;
    }

    public static int getDefaultLiveWallpaperIndex(Context context) {
        ColorInfo[] semcColors = getSemcColors(context);
        return semcColors[0].index;
    }

    private static ColorInfo[] importAllSemcColors() {
        return new ColorInfo[]{ColorInfo.Xperia, ColorInfo.Blue, ColorInfo.Purple, ColorInfo.Red, ColorInfo.Orange, ColorInfo.Green, ColorInfo.Gray, ColorInfo.Black, ColorInfo.Copper, ColorInfo.AquaBlue, ColorInfo.SeaGreen, ColorInfo.WarmSilver, ColorInfo.Rose};
    }

    private static ColorInfo[] importSemcColorGroup1() {
        return new ColorInfo[]{ColorInfo.Xperia, ColorInfo.Blue, ColorInfo.Purple, ColorInfo.Red, ColorInfo.Orange, ColorInfo.Green, ColorInfo.Gray, ColorInfo.Black, ColorInfo.Copper, ColorInfo.AquaBlue};
    }

    private static ColorInfo[] importSemcColorGroup2() {
        return new ColorInfo[]{ColorInfo.Xperia, ColorInfo.Blue, ColorInfo.Purple, ColorInfo.Red, ColorInfo.Orange, ColorInfo.Green, ColorInfo.Gray, ColorInfo.Black, ColorInfo.SeaGreen};
    }

    private static ColorInfo[] importSemcColorGroup3() {
        return new ColorInfo[]{ColorInfo.Xperia, ColorInfo.Blue, ColorInfo.Purple, ColorInfo.Red, ColorInfo.Orange, ColorInfo.Green, ColorInfo.Gray, ColorInfo.Black};
    }

    private static ColorInfo[] importSemcColorGroup4() {
        return new ColorInfo[]{ColorInfo.Xperia, ColorInfo.Blue, ColorInfo.Purple, ColorInfo.Red, ColorInfo.Orange, ColorInfo.Green, ColorInfo.Gray, ColorInfo.Black, ColorInfo.SeaGreen, ColorInfo.Rose, ColorInfo.WarmSilver};
    }

    private static ColorInfo[] importSemcColors() {
        return new ColorInfo[]{ColorInfo.Xperia, ColorInfo.Blue, ColorInfo.Purple, ColorInfo.Red, ColorInfo.Orange, ColorInfo.Green, ColorInfo.Gray};
    }

    private static ColorInfo[] importVerizonRedColorGroup() {
        return new ColorInfo[]{ColorInfo.XperiaVerizonRed, ColorInfo.Blue, ColorInfo.Purple, ColorInfo.Red, ColorInfo.Orange, ColorInfo.Green, ColorInfo.Gray};
    }
}
