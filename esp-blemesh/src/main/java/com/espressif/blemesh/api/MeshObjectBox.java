package com.espressif.blemesh.api;

import android.content.Context;

import com.espressif.blemesh.model.EspFastNode_;
import com.espressif.blemesh.model.EspMeshGroup;
import com.espressif.blemesh.model.EspFastNode;
import com.espressif.blemesh.model.EspMeshGroup_;
import com.espressif.blemesh.model.EspMeshNode;
import com.espressif.blemesh.model.EspMeshNode_;
import com.espressif.blemesh.model.MyObjectBox;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.objectbox.Box;
import io.objectbox.BoxStore;

public class MeshObjectBox {
    private static MeshObjectBox sInstance;

    private BoxStore mBoxStore;

    public static MeshObjectBox getInstance() {
        if (sInstance == null) {
            synchronized (MeshObjectBox.class) {
                if (sInstance == null) {
                    sInstance = new MeshObjectBox();
                }
            }
        }

        return sInstance;
    }

    private MeshObjectBox() {
    }

    public synchronized void init(Context context) {
        if (mBoxStore != null) {
            throw new IllegalStateException("MeshObjectBox has initialized");
        }

        mBoxStore = MyObjectBox.builder().androidContext(context).build();
    }

    public BoxStore boxStore() {
        return mBoxStore;
    }

    public EspFastNode getFastNodeForBleId(String bleId, String meshUUID) {
        Box<EspFastNode> box = mBoxStore.boxFor(EspFastNode.class);
        return box.query()
                .equal(EspFastNode_.meshUUID, meshUUID)
                .and()
                .equal(EspFastNode_.bleId, bleId)
                .build()
                .findFirst();
    }

    public List<EspFastNode> getFastNodes(String meshUUID) {
        Box<EspFastNode> box = mBoxStore.boxFor(EspFastNode.class);
        return box.query()
                .equal(EspFastNode_.meshUUID, meshUUID)
                .build()
                .find();
    }

    public void updateFastNodes(Collection<EspFastNode> fastNodes) {
        Box<EspFastNode> box = mBoxStore.boxFor(EspFastNode.class);
        Set<String> networkIds = new HashSet<>();
        for (EspFastNode node : fastNodes) {
            networkIds.add(node.meshUUID);
        }
        if (!networkIds.isEmpty()) {
            for (String uuid : networkIds) {
                box.query().equal(EspFastNode_.meshUUID, uuid).build().remove();
            }
        }

        box.put(fastNodes);
    }

    public void deleteFastNode(EspFastNode fastNode) {
        Box<EspFastNode> box = mBoxStore.boxFor(EspFastNode.class);
        box.remove(fastNode);
    }

    public void deleteFastNodes(Collection<EspFastNode> fastNodes) {
        Box<EspFastNode> box = mBoxStore.boxFor(EspFastNode.class);
        box.remove(fastNodes);
    }

    public List<EspMeshGroup> getMeshGroups(String meshUUID) {
        Box<EspMeshGroup> box = mBoxStore.boxFor(EspMeshGroup.class);
        return box.query()
                .equal(EspMeshGroup_.meshUUID, meshUUID)
                .build()
                .find();
    }

    public void updateMeshGroups(Collection<EspMeshGroup> groups) {
        Box<EspMeshGroup> box = mBoxStore.boxFor(EspMeshGroup.class);
        box.put(groups);
    }

    public void deleteMeshGroups(Collection<EspMeshGroup> groups) {
        Box<EspMeshGroup> box = mBoxStore.boxFor(EspMeshGroup.class);
        box.remove(groups);
    }

    public List<EspMeshNode> getMeshNodes(String meshUUID) {
        Box<EspMeshNode> box = mBoxStore.boxFor(EspMeshNode.class);
        return box.query()
                .equal(EspMeshNode_.meshUUID, meshUUID)
                .build()
                .find();
    }

    public void updateMeshNode(EspMeshNode node) {
        Box<EspMeshNode> box = mBoxStore.boxFor(EspMeshNode.class);
        box.put(node);
    }

    public void deleteMeshNode(EspMeshNode node) {
        Box<EspMeshNode> box = mBoxStore.boxFor(EspMeshNode.class);
        box.remove(node);
    }
}
