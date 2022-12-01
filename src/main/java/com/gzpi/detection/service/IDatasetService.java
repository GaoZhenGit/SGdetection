package com.gzpi.detection.service;

import com.gzpi.detection.bean.DatasetMission;
import com.gzpi.detection.bean.DatasetProject;
import com.gzpi.detection.bean.DatasetRequest;
import com.gzpi.detection.bean.DatasetSample;

import java.io.IOException;
import java.util.List;

public interface IDatasetService {
    void addProject(DatasetProject project);
    List<DatasetProject> getAllProject();
    void deleteProject(String id);

    DatasetMission addMission(DatasetMission mission) throws Exception;
    List<DatasetMission> getMissionsByProjectId(String projectId, String type);
    void deleteMission(String id);

    DatasetSample getSampleById(String sampleId);
    List<DatasetSample> getAllSamples();
    void saveSample(DatasetSample sample);
    void addSampleFromMissions(DatasetRequest<List<String>> request) throws IOException;
    void deleteSample(String id);
}
