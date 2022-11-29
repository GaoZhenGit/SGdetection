package com.gzpi.detection.bean;

public class TrainingModel {
    public String id;
    public String name;
    public String version;
    public String description;
    public String sampleId;
    public Parameter parameter;
    public static class Parameter {
        public int chipSize;
        public int epoch;
        public double learningRate;
        public int batchSize;

    }
}
