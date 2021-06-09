package com.espressif.blemesh.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
public class EspMeshGroup {
    @Id
    public long id;

    public int address;

    public String description;

    @Index
    public String meshUUID;
}
