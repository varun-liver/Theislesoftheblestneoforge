package com.isles.worldgen;

import terrablender.api.Regions;

public final class BlestTerrablender {
    private static boolean registered = false;

    private BlestTerrablender() {
    }

    public static void registerRegions() {
        if (registered) {
            return;
        }
        Regions.register(new SkyForestRegion(6));
        registered = true;
    }
}
