package com.sonymobile.runtimeskinning;

import android.app.AppGlobals;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.TypedValue;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class SkinGlueV18 implements SkinGlue {
    private Field mFieldConfigSkinPackage;
    private Field mFieldIsSkin;
    private Field mFieldIsVerifiedSkin;
    private Method mMethodGetRuntimeSkin;
    private Method mMethodSetRuntimeSkin;

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public boolean isApplicable(Context context) {
        return Build.VERSION.SDK_INT > 17 && init();
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public boolean isSkin(Context context, PackageInfo pinf, int userId) {
        if (init()) {
            return ((Boolean) ReflectionUtils.readField(this.mFieldIsSkin, pinf, Boolean.class)).booleanValue() && !TextUtils.isEmpty(pinf.packageName);
        }
        return false;
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public boolean isVerified(Context context, PackageInfo pinf, int userId) {
        if (init()) {
            return isSkin(context, pinf, userId) && ((Boolean) ReflectionUtils.readField(this.mFieldIsVerifiedSkin, pinf, Boolean.class)).booleanValue();
        }
        return false;
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public boolean isTrusted(Context context, PackageInfo pinf, int userId) {
        if (!init() || !isVerified(context, pinf, userId)) {
            return false;
        }
        boolean systemApp = (pinf.applicationInfo.flags & 1) != 0;
        if (systemApp) {
            return true;
        }
        return context.getPackageManager().checkSignatures(pinf.packageName, "android") == 0;
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
        if (!init()) {
            return false;
        }
        ExceptionHandler eh = new ExceptionHandler();
        IPackageManager ipm = AppGlobals.getPackageManager();
        Method method = this.mMethodSetRuntimeSkin;
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(userId);
        objArr[1] = pinf == null ? null : pinf.packageName;
        Boolean result = (Boolean) ReflectionUtils.invokeMethod(method, ipm, eh, Boolean.class, objArr);
        eh.reThrow();
        return result == null ? false : result.booleanValue();
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public String getSkin(Context context, int userId) throws RemoteException {
        if (!init()) {
            return null;
        }
        ExceptionHandler eh = new ExceptionHandler();
        IPackageManager ipm = AppGlobals.getPackageManager();
        String str = (String) ReflectionUtils.invokeMethod(this.mMethodGetRuntimeSkin, ipm, eh, String.class, Integer.valueOf(userId));
        eh.reThrow();
        return str;
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public int myUserId(Context context) {
        return UserHandle.myUserId();
    }

    @Override // com.sonymobile.runtimeskinning.SkinGlue
    public boolean supportsInstallableSkins(Context context) {
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x007a, code lost:
    
        if (r8.mMethodSetRuntimeSkin != null) goto L29;
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
            java.lang.reflect.Field r2 = r8.mFieldIsSkin     // Catch: java.lang.Throwable -> L80
            if (r2 != 0) goto L13
            java.lang.Class<android.content.pm.PackageInfo> r2 = android.content.pm.PackageInfo.class
            java.lang.String r3 = "isSkin"
            java.lang.Class r4 = java.lang.Boolean.TYPE     // Catch: java.lang.Throwable -> L80
            java.lang.reflect.Field r2 = com.sonymobile.runtimeskinning.ReflectionUtils.getField(r2, r3, r4)     // Catch: java.lang.Throwable -> L80
            r8.mFieldIsSkin = r2     // Catch: java.lang.Throwable -> L80
        L13:
            java.lang.reflect.Field r2 = r8.mFieldIsVerifiedSkin     // Catch: java.lang.Throwable -> L80
            if (r2 != 0) goto L23
            java.lang.Class<android.content.pm.PackageInfo> r2 = android.content.pm.PackageInfo.class
            java.lang.String r3 = "isVerifiedSkin"
            java.lang.Class r4 = java.lang.Boolean.TYPE     // Catch: java.lang.Throwable -> L80
            java.lang.reflect.Field r2 = com.sonymobile.runtimeskinning.ReflectionUtils.getField(r2, r3, r4)     // Catch: java.lang.Throwable -> L80
            r8.mFieldIsVerifiedSkin = r2     // Catch: java.lang.Throwable -> L80
        L23:
            java.lang.reflect.Field r2 = r8.mFieldConfigSkinPackage     // Catch: java.lang.Throwable -> L80
            if (r2 != 0) goto L33
            java.lang.Class<android.content.pm.ActivityInfo> r2 = android.content.pm.ActivityInfo.class
            java.lang.String r3 = "CONFIG_SKIN_PACKAGE"
            java.lang.Class r4 = java.lang.Integer.TYPE     // Catch: java.lang.Throwable -> L80
            java.lang.reflect.Field r2 = com.sonymobile.runtimeskinning.ReflectionUtils.getField(r2, r3, r4)     // Catch: java.lang.Throwable -> L80
            r8.mFieldConfigSkinPackage = r2     // Catch: java.lang.Throwable -> L80
        L33:
            java.lang.reflect.Method r2 = r8.mMethodGetRuntimeSkin     // Catch: java.lang.Throwable -> L80
            if (r2 != 0) goto L4b
            java.lang.Class<android.content.pm.IPackageManager> r2 = android.content.pm.IPackageManager.class
            java.lang.String r3 = "getRuntimeSkin"
            java.lang.Class<java.lang.String> r4 = java.lang.String.class
            r5 = 1
            java.lang.Class[] r5 = new java.lang.Class[r5]     // Catch: java.lang.Throwable -> L80
            r6 = 0
            java.lang.Class r7 = java.lang.Integer.TYPE     // Catch: java.lang.Throwable -> L80
            r5[r6] = r7     // Catch: java.lang.Throwable -> L80
            java.lang.reflect.Method r2 = com.sonymobile.runtimeskinning.ReflectionUtils.getMethod(r2, r3, r4, r5)     // Catch: java.lang.Throwable -> L80
            r8.mMethodGetRuntimeSkin = r2     // Catch: java.lang.Throwable -> L80
        L4b:
            java.lang.reflect.Method r2 = r8.mMethodSetRuntimeSkin     // Catch: java.lang.Throwable -> L80
            if (r2 != 0) goto L68
            java.lang.Class<android.content.pm.IPackageManager> r2 = android.content.pm.IPackageManager.class
            java.lang.String r3 = "setRuntimeSkin"
            java.lang.Class r4 = java.lang.Boolean.TYPE     // Catch: java.lang.Throwable -> L80
            r5 = 2
            java.lang.Class[] r5 = new java.lang.Class[r5]     // Catch: java.lang.Throwable -> L80
            r6 = 0
            java.lang.Class r7 = java.lang.Integer.TYPE     // Catch: java.lang.Throwable -> L80
            r5[r6] = r7     // Catch: java.lang.Throwable -> L80
            r6 = 1
            java.lang.Class<java.lang.String> r7 = java.lang.String.class
            r5[r6] = r7     // Catch: java.lang.Throwable -> L80
            java.lang.reflect.Method r2 = com.sonymobile.runtimeskinning.ReflectionUtils.getMethod(r2, r3, r4, r5)     // Catch: java.lang.Throwable -> L80
            r8.mMethodSetRuntimeSkin = r2     // Catch: java.lang.Throwable -> L80
        L68:
            java.lang.reflect.Field r2 = r8.mFieldIsSkin     // Catch: java.lang.Throwable -> L80
            if (r2 == 0) goto L7e
            java.lang.reflect.Field r2 = r8.mFieldIsVerifiedSkin     // Catch: java.lang.Throwable -> L80
            if (r2 == 0) goto L7e
            java.lang.reflect.Field r2 = r8.mFieldConfigSkinPackage     // Catch: java.lang.Throwable -> L80
            if (r2 == 0) goto L7e
            java.lang.reflect.Method r2 = r8.mMethodGetRuntimeSkin     // Catch: java.lang.Throwable -> L80
            if (r2 == 0) goto L7e
            java.lang.reflect.Method r2 = r8.mMethodSetRuntimeSkin     // Catch: java.lang.Throwable -> L80
            if (r2 == 0) goto L7e
        L7c:
            monitor-exit(r8)
            return r0
        L7e:
            r0 = r1
            goto L7c
        L80:
            r0 = move-exception
            monitor-exit(r8)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sonymobile.runtimeskinning.SkinGlueV18.init():boolean");
    }
}
