package com.sonymobile.runtimeskinning;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.RemoteException;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class RuntimeSkinningUtil {
    public static final int FLAG_DO_NOT_SHOW_UI = 1;

    private RuntimeSkinningUtil() {
    }

    public static Intent newChangeCurrentSkinAndResourcesIntent(Context context, PackageInfo skinPackage, int flags) throws RemoteException, NullPointerException, IllegalArgumentException {
        if (context == null || skinPackage == null) {
            throw new NullPointerException("Parameters must not be null");
        }
        Intent intent = new Intent(Constants.ACTION_SET_SKIN_PACKAGE);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package://" + skinPackage.packageName));
        intent.putExtra(Constants.EXTRA_SILENT, (flags & 1) != 0);
        intent.putExtra(Constants.EXTRA_FLAGS, flags);
        return intent;
    }

    public static Intent newGetSkinForColorIntent(float h, float s, float v, float ht, float st, float vt, int flags) {
        Intent intent = new Intent(Constants.ACTION_PICK_SKIN_PACKAGE);
        intent.addCategory("android.intent.category.DEFAULT");
        float[] hsvColorAndTolerance = {h, s, v, ht, st, vt};
        intent.putExtra(Constants.EXTRA_HSV_COLOR_AND_TOLERANCE, hsvColorAndTolerance);
        intent.putExtra(Constants.EXTRA_SILENT, (flags & 1) != 0);
        intent.putExtra(Constants.EXTRA_FLAGS, flags);
        return intent;
    }

    public static boolean isLiveWallpaperUsed(Intent intent) {
        return getSkinPackageChangedFlag(intent, 4);
    }

    public static boolean isStaticWallpaperUsed(Intent intent) {
        return getSkinPackageChangedFlag(intent, 8);
    }

    public static boolean isInitialSetup(Intent intent) {
        return getSkinPackageChangedFlag(intent, Constants.EXTRA_FLAGS_INITIAL_SETUP);
    }

    private static boolean getSkinPackageChangedFlag(Intent intent, int flag) {
        if (intent == null) {
            throw new NullPointerException("Intent must not be null");
        }
        if (!Constants.ACTION_SKIN_PACKAGE_CHANGED.equals(intent.getAction())) {
            throw new IllegalArgumentException("Invalid intent action, " + intent.getAction());
        }
        int flags = intent.getIntExtra(Constants.EXTRA_FLAGS, 0);
        if (intent.getBooleanExtra(Constants.EXTRA_SILENT, false)) {
            flags |= 1;
        }
        return (flags & flag) == flag;
    }
}
