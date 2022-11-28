package com.gzpi.detection.service;

import com.gzpi.detection.bean.DatasetMission;
import com.gzpi.detection.bean.DatasetProject;
import com.gzpi.detection.bean.DatasetSample;

import java.util.List;

public interface IDatasetService {
    void addProject(DatasetProject project);
    List<DatasetProject> getAllProject();
    void deleteProject(String id);

    void addMission(DatasetMission mission) throws Exception;
    List<DatasetMission> getMissionsByProjectId(String projectId);
    void deleteMission(String id);

    DatasetSample getSampleById(String sampleId);
    List<DatasetSample> getAllSamples();
    void saveSample(DatasetSample sample);
}
