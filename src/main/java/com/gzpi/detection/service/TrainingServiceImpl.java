package com.gzpi.detection.service;

import com.gzpi.detection.bean.TrainingModel;
import com.gzpi.detection.mapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class TrainingServiceImpl implements ITrainingService{
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public TrainingModel addModel(TrainingModel model) {
        modelMapper.save(model);
        return modelMapper.getModelById(model.id);
    }

    @Override
    public List<TrainingModel> getAllModels(String name, String version) {
        List<TrainingModel> models = modelMapper.getAllModels();
        if (name != null && !name.isEmpty()) {
            models = models.stream().filter(model -> Objects.equals(model.name, name)).collect(Collectors.toList());
        }
        if (version != null && !version.isEmpty()) {
            models = models.stream().filter(model -> Objects.equals(model.version, version)).collect(Collectors.toList());
        }
        return models;
    }

    @Override
    public void deleteModel(String id) {
        modelMapper.delete(id);
    }
}
