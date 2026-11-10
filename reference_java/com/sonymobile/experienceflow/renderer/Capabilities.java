package com.sonymobile.experienceflow.renderer;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class Capabilities {
    public static final String CAP_VERTEX_HALF_FLOAT = "GL_OES_vertex_half_float";
    private static final boolean DEBUG = false;
    private String[] extensions;
    private int mMaxTextureSize;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExtensions(String[] extensions) {
        this.extensions = extensions;
    }

    void setMaxTextureSize(int maxTexSize) {
        this.mMaxTextureSize = maxTexSize;
    }

    public boolean checkExtension(String extension) {
        for (int i = 0; i < this.extensions.length; i++) {
            if (this.extensions[i].equals(extension)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVertextHalfFloat() {
        return checkExtension(CAP_VERTEX_HALF_FLOAT);
    }

    public int getMaxTextureSize() {
        return this.mMaxTextureSize;
    }
}
