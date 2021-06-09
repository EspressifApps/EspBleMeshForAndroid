package com.espressif.blemesh.ota;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

import no.nordicsemi.android.mesh.espressif.EspOtaUtils;

public class EspOtaBin {
    private String name;
    private int binId;
    private String versionName;
    private int versionCode;

    private File file;

    public EspOtaBin(@NonNull File file) throws IllegalArgumentException {
        this.file = file;
        String name = file.getName();
        this.name = name;

        if (name.toLowerCase(Locale.ENGLISH).endsWith(".bin")) {
            name = name.substring(0, name.length() - 4);
        }
        String[] splits = name.split("_");
        if (splits.length != 3) {
            throw new IllegalArgumentException("File name is invalid");
        }

        try {
            this.binId = Integer.parseInt(splits[1], 16);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("File bin id is invalid");
        }

        this.versionName = splits[2];
        String[] versionSplits = this.versionName.split("\\.");
        if (versionSplits.length != 3) {
            throw new IllegalArgumentException("File version is invalid");
        }
        try {
            this.versionCode = EspOtaUtils.binVersionString2Int(this.versionName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("File version is invalid");
        }
    }

    public String getName() {
        return name;
    }

    public int getBinId() {
        return binId;
    }

    public String getVersionName() {
        return versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public File getFile() {
        return file;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(file);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EspOtaBin)) {
            return false;
        }
        return file.equals(((EspOtaBin) obj).file);
    }
}
