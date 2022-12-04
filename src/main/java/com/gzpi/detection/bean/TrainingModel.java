package com.gzpi.detection.bean;

public class TrainingModel {
    public String id;
    public String name;
    public String version;
    public String description;
    public DatasetSample sample;
    public int chipSize = 300;
    public int epoch = 50;
    public double learningRate = 1e-5;
    public int batchSize = 8;
    public Model model = Model.fcn_resnet50;
    public Backbone backbone = Backbone.resnet50;

    public enum Model {
        fcn_resnet50,
        inception_v3,
        deeplabv3_resnet50
    }

    public enum Backbone {
        resnet50,
        resnet101
    }
}
