package com.gzpi.detection.service;

import com.gzpi.detection.bean.TrainingModel;

import java.util.List;

public interface ITrainingService {
    TrainingModel addModel(TrainingModel model);
    List<TrainingModel> getAllModels(String name, String version);
    void deleteModel(String id);
}
