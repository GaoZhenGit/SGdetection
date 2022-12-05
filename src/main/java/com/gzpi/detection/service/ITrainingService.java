package com.gzpi.detection.service;

import com.gzpi.detection.bean.TrainingModel;

import java.util.List;

public interface ITrainingService {
    TrainingModel addModel(TrainingModel model);
    List<TrainingModel> getAllModels(String name, String version, String status);
    void deleteModel(String id);
    void startTraining(String modelId) throws Exception;
    void publishModel(String modelId) throws Exception;
}
