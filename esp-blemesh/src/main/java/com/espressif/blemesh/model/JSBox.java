package com.espressif.blemesh.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;

@Entity
public class JSBox {
    @Id
    public long id;

    @Unique
    public String key;

    public String value;
}
