package com.espressif.blemesh.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class EspMeshNode {
    @Id
    public long id;

    public int address;

    public int elementCount;

    public String bleId;

    public String meshUUID;
}
