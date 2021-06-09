package com.espressif.blemesh.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class EspFastNode {
    @Id
    public long id;

    public String meshUUID;

    public long address;

    public String bleId;
}
