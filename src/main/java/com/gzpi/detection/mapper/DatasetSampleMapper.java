package com.gzpi.detection.mapper;

import com.gzpi.detection.bean.DatasetItem;
import com.gzpi.detection.bean.DatasetSample;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DatasetSampleMapper {
    DatasetSample getSampleById(String id);
    List<DatasetSample> getAllSamples();
    void save(DatasetSample project);
    void saveItem(List<DatasetItem> items);
    void delete(String id);
}
