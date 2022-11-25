package com.gzpi.detection.service;

import com.gzpi.detection.bean.BaseResponse;
import com.gzpi.detection.bean.DatasetProject;

import java.util.List;

public interface IDatasetService {
    void addProject(DatasetProject project);
    List<DatasetProject> getAllProject();
    void deleteProject(String id);
}
