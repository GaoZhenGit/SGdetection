package com.gzpi.detection.mapper;

import com.gzpi.detection.bean.DatasetProject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DatasetProjectMapper {
    DatasetProject getProjectById(String id);
    List<DatasetProject> getAllProjects();
    void save(DatasetProject project);
    void delete(String id);
}
