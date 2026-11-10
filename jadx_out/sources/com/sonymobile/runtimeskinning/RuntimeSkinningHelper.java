package com.sonymobile.runtimeskinning;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.text.TextUtils;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class RuntimeSkinningHelper {
    private static final SkinGlue[] IMPLEMENTATIONS = {new SkinGlueV18(), new SkinGlueV17(), new SkinGlueV16()};
    private SkinGlue mSkinGlue;

    public RuntimeSkinningHelper() {
    }

    RuntimeSkinningHelper(SkinGlue glue) {
        this.mSkinGlue = glue;
    }

    public boolean isSkin(Context context, PackageInfo skinPackage) throws RemoteException, NullPointerException {
        if (context == null || skinPackage == null) {
            throw new NullPointerException();
        }
        SkinGlue glue = getSkinGlue(context);
        return glue.isSkin(context, skinPackage, glue.myUserId(context));
    }

    public PackageInfo getCurrentSkin(Context context) throws RemoteException, NullPointerException {
        if (context == null) {
            throw new NullPointerException();
        }
        SkinGlue glue = getSkinGlue(context);
        String packageName = glue.getSkin(context, glue.myUserId(context));
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo skinPackage = pm.getPackageInfo(packageName, 0);
            return skinPackage;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public boolean isSkinConfigurationChanged(Context context, int diff) throws RemoteException {
        if (context == null) {
            throw new NullPointerException();
        }
        SkinGlue glue = getSkinGlue(context);
        return glue.isSkinConfigurationChanged(context, diff);
    }

    final synchronized SkinGlue getSkinGlue(Context context) {
        if (this.mSkinGlue == null) {
            SkinGlue[] arr$ = IMPLEMENTATIONS;
            int len$ = arr$.length;
            int i$ = 0;
            while (true) {
                if (i$ >= len$) {
                    break;
                }
                SkinGlue glue = arr$[i$];
                if (!glue.isApplicable(context)) {
                    i$++;
                } else {
                    this.mSkinGlue = glue;
                    break;
                }
            }
        }
        return this.mSkinGlue != null ? this.mSkinGlue : SkinGlue.DISABLED;
    }
}
