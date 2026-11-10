package com.sonymobile.runtimeskinning;

import android.os.RemoteException;
import java.lang.Thread;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Throwable mCause;

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable cause) {
        this.mCause = cause;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reThrow() throws RemoteException {
        if (this.mCause != null) {
            if (!(this.mCause instanceof RemoteException)) {
                RemoteException re = new RemoteException();
                re.initCause(this.mCause);
                throw re;
            }
            throw ((RemoteException) this.mCause);
        }
    }
}
