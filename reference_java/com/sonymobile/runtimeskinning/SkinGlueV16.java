package com.sonymobile.runtimeskinning;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.TypedValue;
import java.lang.reflect.Field;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
class SkinGlueV16 implements SkinGlue {
    private Field mFieldConfigSkinPackage;
    private Field mFieldIsSkin;
    private Field mFieldOverlayTarget;
    private Field mFieldSkinPackage;

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public int myUserId(Context context) {
        return 0;
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public boolean isApplicable(Context context) {
        return Build.VERSION.SDK_INT > 15 && init();
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public boolean isSkin(Context context, PackageInfo pinf, int userId) {
        if (!init()) {
            return false;
        }
        boolean isSkin = ((Boolean) ReflectionUtils.readField(this.mFieldIsSkin, pinf, Boolean.class)).booleanValue();
        String overlayTarget = (String) ReflectionUtils.readField(this.mFieldOverlayTarget, pinf, String.class);
        return isSkin && Constants.UXP_PACKAGE_NAME.equals(overlayTarget) && !TextUtils.isEmpty(pinf.packageName);
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public boolean isVerified(Context context, PackageInfo pinf, int userId) {
        return isSkin(context, pinf, userId);
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public boolean isTrusted(Context context, PackageInfo pinf, int userId) {
        return isSkin(context, pinf, userId);
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public boolean isResourceOverlaid(Context context, Resources res, int referenceResId, int resId) {
        try {
            TypedValue tv = new TypedValue();
            res.getValue(referenceResId, tv, true);
            int cookie = tv.assetCookie;
            res.getValue(resId, tv, true);
            return tv.assetCookie != cookie;
        } catch (Resources.NotFoundException e) {
            return false;
        }
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public boolean isSkinConfigurationChanged(Context context, int diff) throws RemoteException {
        Integer flag = (Integer) ReflectionUtils.readField(this.mFieldConfigSkinPackage, null, Integer.class);
        if (flag == null || flag.intValue() == 0) {
            throw new RemoteException();
        }
        return (flag.intValue() & diff) == flag.intValue();
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public boolean setSkin(Context context, PackageInfo pinf, int userId) throws RemoteException {
        IActivityManager am;
        Configuration cfg;
        if (!init() || (am = ActivityManagerNative.getDefault()) == null || (cfg = am.getConfiguration()) == null) {
            return false;
        }
        if (!ReflectionUtils.setField(this.mFieldSkinPackage, cfg, pinf == null ? null : pinf.packageName)) {
            return false;
        }
        am.updateConfiguration(cfg);
        return true;
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public String getSkin(Context context, int userId) throws RemoteException {
        if (!init()) {
            return null;
        }
        IActivityManager am = ActivityManagerNative.getDefault();
        Configuration cfg = am.getConfiguration();
        if (cfg != null) {
            return (String) ReflectionUtils.readField(this.mFieldSkinPackage, cfg, String.class);
        }
        return null;
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public boolean supportsInstallableSkins(Context context) {
        return false;
    }

    private synchronized boolean init() {
        boolean z;
        if (this.mFieldIsSkin == null) {
            this.mFieldIsSkin = ReflectionUtils.getField(PackageInfo.class, "isSkin", Boolean.TYPE);
        }
        if (this.mFieldOverlayTarget == null) {
            this.mFieldOverlayTarget = ReflectionUtils.getField(PackageInfo.class, "overlayTarget", String.class);
        }
        if (this.mFieldSkinPackage == null) {
            this.mFieldSkinPackage = ReflectionUtils.getField(Configuration.class, "skinPackage", String.class);
        }
        if (this.mFieldConfigSkinPackage == null) {
            this.mFieldConfigSkinPackage = ReflectionUtils.getField(ActivityInfo.class, "CONFIG_SKIN_PACKAGE", Integer.TYPE);
        }
        if (this.mFieldIsSkin != null && this.mFieldOverlayTarget != null && this.mFieldSkinPackage != null) {
            z = this.mFieldConfigSkinPackage != null;
        }
        return z;
    }
}
