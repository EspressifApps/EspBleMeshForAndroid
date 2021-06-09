package com.espressif.blemesh.utils;

import android.content.Context;
import android.util.Log;

import com.espressif.blemesh.ota.EspOtaBin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EspUtils {
    public static boolean isTrue(Boolean b) {
        return b != null && b;
    }

    public static File getOtaBinDir(Context context) {
        // OTA bin dir = phone storage/Android/data/"application id"/files/bin/
        // Our application id is com.espressif.espblemesh
        File root = context.getExternalFilesDir(null);
        if (root == null) {
            return null;
        }
        File binDir = new File(root, "bin");
        if (!binDir.exists() && !binDir.mkdirs()) {
            return null;
        }
        return binDir;
    }

    public static List<EspOtaBin> getOtaBins(Context context) {
        File binDir = getOtaBinDir(context);
        if (binDir == null) {
            return Collections.emptyList();
        }
        File[] files = binDir.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        List<EspOtaBin> result = new ArrayList<>();
        for (File file : files) {
            try {
                EspOtaBin bin = new EspOtaBin(file);
                result.add(bin);
            } catch (IllegalArgumentException e) {
                Log.d("getOtaBins", "getOtaBins: invalid file bin: " + file.getPath());
            }
        }
        return result;
    }
}
