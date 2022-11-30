package com.gzpi.detection.mapper;

import com.gzpi.detection.bean.TrainingModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ModelMapper {
    void save(TrainingModel model);
    void delete(String id);
    List<TrainingModel> getAllModels();
    TrainingModel getModelById(String id);
}
