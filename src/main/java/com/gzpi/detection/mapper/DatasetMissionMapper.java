package com.gzpi.detection.mapper;

import com.gzpi.detection.bean.DatasetMission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DatasetMissionMapper {
    DatasetMission getMissionById(String id);
    List<DatasetMission> getMissionByProjectId(String id);
    List<DatasetMission> getAllMission();
    void save(DatasetMission mission);
    void update(DatasetMission mission);
    void delete(String id);
}
