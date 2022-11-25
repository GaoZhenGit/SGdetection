package com.gzpi.detection.bean;

public class DatasetProject {
    public String id;
    public String name;
    public Type type;
    public int clipSize;

    public enum Type {
        detection,
        change
    }
}
