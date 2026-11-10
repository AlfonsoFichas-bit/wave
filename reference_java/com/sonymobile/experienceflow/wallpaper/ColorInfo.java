package com.sonymobile.experienceflow.wallpaper;

import com.sonymobile.experienceflow2.R;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
public class ColorInfo {
    public final int index;
    public final String label;
    public final int nameID;
    public final int thumbnailID;
    public static final ColorInfo XperiaVerizonRed = new ColorInfo(R.string.xperia_title, -2, R.drawable.verizonredwallpaper_preview, "Xperia Verizon Red");
    public static final ColorInfo Xperia = new ColorInfo(R.string.xperia_title, -1, R.drawable.xperiawallpaper_preview, "Xperia");
    public static final ColorInfo Blue = new ColorInfo(R.string.air_title, 0, R.drawable.bluewallpaper_preview, "Blue");
    public static final ColorInfo Purple = new ColorInfo(R.string.silk_title, 1, R.drawable.purplewallpaper_preview, "Purple");
    public static final ColorInfo Red = new ColorInfo(R.string.heat_title, 2, R.drawable.redwallpaper_preview, "Red");
    public static final ColorInfo Orange = new ColorInfo(R.string.sunny_title, 3, R.drawable.orangewallpaper_preview, "Orange");
    public static final ColorInfo Green = new ColorInfo(R.string.earth_title, 4, R.drawable.greenwallpaper_preview, "Green");
    public static final ColorInfo Gray = new ColorInfo(R.string.day_title, 5, R.drawable.graywallpaper_preview, "Gray");
    public static final ColorInfo Black = new ColorInfo(R.string.black_title, 6, R.drawable.blackwallpaper_preview, "Black");
    public static final ColorInfo Copper = new ColorInfo(R.string.copper_title, 7, R.drawable.copperwallpaper_preview, "Copper");
    public static final ColorInfo SeaGreen = new ColorInfo(R.string.sea_green_title, 12, R.drawable.seagreenwallpaper_preview, "Sea Green");
    public static final ColorInfo AquaBlue = new ColorInfo(R.string.aqua_blue_title, 13, R.drawable.aquabluewallpaper_preview, "Aqua Blue");
    public static final ColorInfo WarmSilver = new ColorInfo(R.string.warm_silver_title, 14, R.drawable.warmsilverwallpaper_preview, "Warm Silver");
    public static final ColorInfo Rose = new ColorInfo(R.string.rose_title, 15, R.drawable.rosewallpaper_preview, "Rose");

    public ColorInfo(int nameID, int index, int thumbnailID, String label) {
        this.nameID = nameID;
        this.index = index;
        this.thumbnailID = thumbnailID;
        this.label = label;
    }
}
