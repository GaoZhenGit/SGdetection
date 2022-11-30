package com.gzpi.detection.bean;

import java.util.List;

public class DatasetSample {
    public String id;
    public String name;
    public Type type;
    public List<DatasetItem> items;

    public enum Type {
        detection,
        change
    }
}
