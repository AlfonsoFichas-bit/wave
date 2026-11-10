package com.sonymobile.runtimeskinning;

import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.TypedValue;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
class SkinGlueV17 implements SkinGlue {
    private Field mFieldConfigSkinPackage;
    private Field mFieldIsSkin;
    private Field mFieldOverlayTarget;
    private Field mFieldSkinPackageSeq;
    private Method mMethodGetRuntimeSkin;
    private Method mMethodSetRuntimeSkin;

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public int myUserId(Context context) {
        return UserHandle.myUserId();
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public boolean isApplicable(Context context) {
        return Build.VERSION.SDK_INT > 16 && init();
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
        IActivityManager iam;
        Configuration cfg;
        if (!init()) {
            return false;
        }
        IPackageManager ipm = AppGlobals.getPackageManager();
        Method method = this.mMethodSetRuntimeSkin;
        Object[] objArr = new Object[3];
        objArr[0] = Constants.UXP_PACKAGE_NAME;
        objArr[1] = pinf == null ? null : pinf.packageName;
        objArr[2] = Integer.valueOf(userId);
        if (!((Boolean) ReflectionUtils.invokeMethod(method, ipm, Boolean.class, objArr)).booleanValue() || (cfg = (iam = ActivityManagerNative.getDefault()).getConfiguration()) == null) {
            return false;
        }
        int seq = ((Integer) ReflectionUtils.readField(this.mFieldSkinPackageSeq, cfg, Integer.class)).intValue() + 1;
        if (!ReflectionUtils.setField(this.mFieldSkinPackageSeq, cfg, Integer.valueOf(seq))) {
            return false;
        }
        iam.updateConfiguration(cfg);
        return true;
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public String getSkin(Context context, int userId) {
        if (!init()) {
            return null;
        }
        IPackageManager ipm = AppGlobals.getPackageManager();
        return (String) ReflectionUtils.invokeMethod(this.mMethodGetRuntimeSkin, ipm, String.class, Constants.UXP_PACKAGE_NAME, Integer.valueOf(userId));
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public boolean supportsInstallableSkins(Context context) {
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:33:0x0098, code lost:
    
        if (r8.mMethodSetRuntimeSkin != null) goto L34;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private synchronized boolean init() {
        /*
            r8 = this;
            r0 = 1
            r1 = 0
            monitor-enter(r8)
            java.lang.reflect.Field r2 = r8.mFieldIsSkin     // Catch: java.lang.Throwable -> L9e
            if (r2 != 0) goto L13
            java.lang.Class<android.content.pm.PackageInfo> r2 = android.content.pm.PackageInfo.class
            java.lang.String r3 = "isSkin"
            java.lang.Class r4 = java.lang.Boolean.TYPE     // Catch: java.lang.Throwable -> L9e
            java.lang.reflect.Field r2 = com.sonymobile.runtimeskinning.ReflectionUtils.getField(r2, r3, r4)     // Catch: java.lang.Throwable -> L9e
            r8.mFieldIsSkin = r2     // Catch: java.lang.Throwable -> L9e
        L13:
            java.lang.reflect.Field r2 = r8.mFieldOverlayTarget     // Catch: java.lang.Throwable -> L9e
            if (r2 != 0) goto L23
            java.lang.Class<android.content.pm.PackageInfo> r2 = android.content.pm.PackageInfo.class
            java.lang.String r3 = "overlayTarget"
            java.lang.Class<java.lang.String> r4 = java.lang.String.class
            java.lang.reflect.Field r2 = com.sonymobile.runtimeskinning.ReflectionUtils.getField(r2, r3, r4)     // Catch: java.lang.Throwable -> L9e
            r8.mFieldOverlayTarget = r2     // Catch: java.lang.Throwable -> L9e
        L23:
            java.lang.reflect.Field r2 = r8.mFieldSkinPackageSeq     // Catch: java.lang.Throwable -> L9e
            if (r2 != 0) goto L33
            java.lang.Class<android.content.res.Configuration> r2 = android.content.res.Configuration.class
            java.lang.String r3 = "skinPackageSeq"
            java.lang.Class r4 = java.lang.Integer.TYPE     // Catch: java.lang.Throwable -> L9e
            java.lang.reflect.Field r2 = com.sonymobile.runtimeskinning.ReflectionUtils.getField(r2, r3, r4)     // Catch: java.lang.Throwable -> L9e
            r8.mFieldSkinPackageSeq = r2     // Catch: java.lang.Throwable -> L9e
        L33:
            java.lang.reflect.Field r2 = r8.mFieldConfigSkinPackage     // Catch: java.lang.Throwable -> L9e
            if (r2 != 0) goto L43
            java.lang.Class<android.content.pm.ActivityInfo> r2 = android.content.pm.ActivityInfo.class
            java.lang.String r3 = "CONFIG_SKIN_PACKAGE"
            java.lang.Class r4 = java.lang.Integer.TYPE     // Catch: java.lang.Throwable -> L9e
            java.lang.reflect.Field r2 = com.sonymobile.runtimeskinning.ReflectionUtils.getField(r2, r3, r4)     // Catch: java.lang.Throwable -> L9e
            r8.mFieldConfigSkinPackage = r2     // Catch: java.lang.Throwable -> L9e
        L43:
            java.lang.reflect.Method r2 = r8.mMethodGetRuntimeSkin     // Catch: java.lang.Throwable -> L9e
            if (r2 != 0) goto L60
            java.lang.Class<android.content.pm.IPackageManager> r2 = android.content.pm.IPackageManager.class
            java.lang.String r3 = "getRuntimeSkin"
            java.lang.Class<java.lang.String> r4 = java.lang.String.class
            r5 = 2
            java.lang.Class[] r5 = new java.lang.Class[r5]     // Catch: java.lang.Throwable -> L9e
            r6 = 0
            java.lang.Class<java.lang.String> r7 = java.lang.String.class
            r5[r6] = r7     // Catch: java.lang.Throwable -> L9e
            r6 = 1
            java.lang.Class r7 = java.lang.Integer.TYPE     // Catch: java.lang.Throwable -> L9e
            r5[r6] = r7     // Catch: java.lang.Throwable -> L9e
            java.lang.reflect.Method r2 = com.sonymobile.runtimeskinning.ReflectionUtils.getMethod(r2, r3, r4, r5)     // Catch: java.lang.Throwable -> L9e
            r8.mMethodGetRuntimeSkin = r2     // Catch: java.lang.Throwable -> L9e
        L60:
            java.lang.reflect.Method r2 = r8.mMethodSetRuntimeSkin     // Catch: java.lang.Throwable -> L9e
            if (r2 != 0) goto L82
            java.lang.Class<android.content.pm.IPackageManager> r2 = android.content.pm.IPackageManager.class
            java.lang.String r3 = "setRuntimeSkin"
            java.lang.Class r4 = java.lang.Boolean.TYPE     // Catch: java.lang.Throwable -> L9e
            r5 = 3
            java.lang.Class[] r5 = new java.lang.Class[r5]     // Catch: java.lang.Throwable -> L9e
            r6 = 0
            java.lang.Class<java.lang.String> r7 = java.lang.String.class
            r5[r6] = r7     // Catch: java.lang.Throwable -> L9e
            r6 = 1
            java.lang.Class<java.lang.String> r7 = java.lang.String.class
            r5[r6] = r7     // Catch: java.lang.Throwable -> L9e
            r6 = 2
            java.lang.Class r7 = java.lang.Integer.TYPE     // Catch: java.lang.Throwable -> L9e
            r5[r6] = r7     // Catch: java.lang.Throwable -> L9e
            java.lang.reflect.Method r2 = com.sonymobile.runtimeskinning.ReflectionUtils.getMethod(r2, r3, r4, r5)     // Catch: java.lang.Throwable -> L9e
            r8.mMethodSetRuntimeSkin = r2     // Catch: java.lang.Throwable -> L9e
        L82:
            java.lang.reflect.Field r2 = r8.mFieldIsSkin     // Catch: java.lang.Throwable -> L9e
            if (r2 == 0) goto L9c
            java.lang.reflect.Field r2 = r8.mFieldOverlayTarget     // Catch: java.lang.Throwable -> L9e
            if (r2 == 0) goto L9c
            java.lang.reflect.Field r2 = r8.mFieldSkinPackageSeq     // Catch: java.lang.Throwable -> L9e
            if (r2 == 0) goto L9c
            java.lang.reflect.Field r2 = r8.mFieldConfigSkinPackage     // Catch: java.lang.Throwable -> L9e
            if (r2 == 0) goto L9c
            java.lang.reflect.Method r2 = r8.mMethodGetRuntimeSkin     // Catch: java.lang.Throwable -> L9e
            if (r2 == 0) goto L9c
            java.lang.reflect.Method r2 = r8.mMethodSetRuntimeSkin     // Catch: java.lang.Throwable -> L9e
            if (r2 == 0) goto L9c
        L9a:
            monitor-exit(r8)
            return r0
        L9c:
            r0 = r1
            goto L9a
        L9e:
            r0 = move-exception
            monitor-exit(r8)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sonymobile.runtimeskinning.SkinGlueV17.init():boolean");
    }
}
