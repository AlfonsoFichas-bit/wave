package com.sonymobile.experienceflow.util;

import android.util.Log;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class FreeList<K> {
    private int mFreeSize;
    private int[] mIndexList;
    private int mMaxSize;
    private K[] mPool;
    private final Class<K> mProtoType;

    public FreeList(Class<K> clazz, int maxSize) {
        this.mProtoType = clazz;
        this.mMaxSize = maxSize;
        this.mFreeSize = this.mMaxSize;
        init();
    }

    private void init() {
        synchronized (this) {
            this.mIndexList = new int[this.mMaxSize];
        }
        for (int i = 0; i < this.mMaxSize; i++) {
            synchronized (this) {
                this.mIndexList[i] = i;
            }
        }
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.mMaxSize; i2++) {
            try {
                arrayList.add(createContents(this.mProtoType));
            } catch (IllegalAccessException e) {
                Log.w(Constants.TAG, "Failed to create object in FreeList.", e);
            } catch (InstantiationException e2) {
                Log.w(Constants.TAG, "Failed to create object in FreeList.", e2);
            }
        }
        synchronized (this) {
            this.mPool = (K[]) convertToArray(arrayList, this.mProtoType);
        }
    }

    private K createContents(Class<K> clazz) throws InstantiationException, IllegalAccessException {
        return clazz.newInstance();
    }

    private static <K> K[] convertToArray(List<K> list, Class<K> cls) {
        return (K[]) list.toArray((Object[]) Array.newInstance((Class<?>) cls, list.size()));
    }

    public synchronized K checkOut() {
        K k;
        if (this.mFreeSize <= 0) {
            k = null;
        } else {
            this.mFreeSize--;
            k = this.mPool[this.mIndexList[0]];
            int temp = this.mIndexList[0];
            this.mIndexList[0] = this.mIndexList[this.mFreeSize];
            this.mIndexList[this.mFreeSize] = temp;
        }
        return k;
    }

    public synchronized void checkIn(K k) {
        if (this.mFreeSize < this.mMaxSize) {
            K[] kArr = this.mPool;
            int[] iArr = this.mIndexList;
            int i = this.mFreeSize;
            this.mFreeSize = i + 1;
            kArr[iArr[i]] = k;
        }
    }

    public synchronized int getCapacity() {
        return this.mMaxSize;
    }

    public synchronized int getNumOfFreeObjects() {
        return this.mFreeSize;
    }

    public synchronized boolean isEmpty() {
        return this.mFreeSize == 0;
    }
}
