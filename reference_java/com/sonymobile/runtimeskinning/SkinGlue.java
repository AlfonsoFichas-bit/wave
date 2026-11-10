package com.sonymobile.runtimeskinning;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.RemoteException;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
interface SkinGlue {
    public static final SkinGlue DISABLED = new GlueStub();

    String getSkin(Context context, int i) throws RemoteException;

    boolean isApplicable(Context context);

    boolean isResourceOverlaid(Context context, Resources resources, int i, int i2) throws RemoteException;

    boolean isSkin(Context context, PackageInfo packageInfo, int i) throws RemoteException;

    boolean isSkinConfigurationChanged(Context context, int i) throws RemoteException;

    boolean isTrusted(Context context, PackageInfo packageInfo, int i) throws RemoteException;

    boolean isVerified(Context context, PackageInfo packageInfo, int i) throws RemoteException;

    int myUserId(Context context);

    boolean setSkin(Context context, PackageInfo packageInfo, int i) throws RemoteException;

    boolean supportsInstallableSkins(Context context);

    /* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
    public static final class GlueStub implements SkinGlue {
        private GlueStub() {
        }

        @Override // com.sonymobile.runtimeskinning.SkinGlue
        public int myUserId(Context context) {
            return 0;
        }

        @Override // com.sonymobile.runtimeskinning.SkinGlue
        public boolean isApplicable(Context context) {
            return false;
        }

        @Override // com.sonymobile.runtimeskinning.SkinGlue
        public boolean isSkin(Context context, PackageInfo pinf, int userId) {
            return false;
        }

        @Override // com.sonymobile.runtimeskinning.SkinGlue
        public boolean isVerified(Context context, PackageInfo pinf, int userId) {
            return false;
        }

        @Override // com.sonymobile.runtimeskinning.SkinGlue
        public boolean isTrusted(Context context, PackageInfo pinf, int userId) throws RemoteException {
            return false;
        }

        @Override // com.sonymobile.runtimeskinning.SkinGlue
        public boolean isResourceOverlaid(Context context, Resources res, int referenceResId, int resId) {
            return false;
        }

        @Override // com.sonymobile.runtimeskinning.SkinGlue
        public boolean isSkinConfigurationChanged(Context context, int diff) {
            return false;
        }

        @Override // com.sonymobile.runtimeskinning.SkinGlue
        public boolean setSkin(Context context, PackageInfo pinf, int userId) {
            return false;
        }

        @Override // com.sonymobile.runtimeskinning.SkinGlue
        public String getSkin(Context context, int userId) {
            return null;
        }

        @Override // com.sonymobile.runtimeskinning.SkinGlue
        public boolean supportsInstallableSkins(Context context) {
            return false;
        }
    }
}
