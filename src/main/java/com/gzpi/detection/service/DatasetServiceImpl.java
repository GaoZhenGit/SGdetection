package com.gzpi.detection.service;

import com.gzpi.detection.bean.*;
import com.gzpi.detection.mapper.DatasetMissionMapper;
import com.gzpi.detection.mapper.DatasetProjectMapper;
import com.gzpi.detection.mapper.DatasetSampleMapper;
import com.gzpi.detection.operation.PathSelector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DatasetServiceImpl implements IDatasetService {
    @Autowired
    private DatasetProjectMapper projectMapper;
    @Autowired
    private DatasetMissionMapper missionMapper;
    @Autowired
    private DatasetSampleMapper sampleMapper;
    @Autowired
    private PathSelector pathSelector;

    @Override
    public void addProject(DatasetProject project) {
        projectMapper.save(project);
    }

    @Override
    public List<DatasetProject> getAllProject() {
        return projectMapper.getAllProjects();
    }

    @Override
    public void deleteProject(String id) {
        projectMapper.delete(id);
    }

    @Override
    public List<DatasetMission> getMissionsByProjectId(String projectId) {
        if (projectId == null || projectId.isEmpty()) {
            return missionMapper.getAllMission();
        } else {
            return missionMapper.getMissionByProjectId(projectId);
        }
    }

    @Override
    public void addMission(DatasetMission mission) throws Exception {
        String imagePath = pathSelector.getPredictImagePath(mission.imageName);
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            missionMapper.save(mission);
        } else {
            throw new Exception("image " + mission.imageName + " not exist");
        }
    }

    @Override
    public void deleteMission(String id) {
        missionMapper.delete(id);
    }

    @Override
    public DatasetSample getSampleById(String sampleId) {
        return sampleMapper.getSampleById(sampleId);
    }

    @Override
    public List<DatasetSample> getAllSamples() {
        return sampleMapper.getAllSamples();
    }

    public void saveSample(DatasetSample datasetSample) {
        for (DatasetItem item : datasetSample.items) {
            item.sampleId = datasetSample.id;
        }
        sampleMapper.save(datasetSample);
        sampleMapper.saveItem(datasetSample.items);
    }

    @Override
    public void addSampleFromMissions(DatasetRequest<List<String>> request) {
        DatasetSample sample = new DatasetSample();
        sample.id = request.id;
        sample.name = request.name;
        sample.items = new ArrayList<>();

        for (String missionId : request.data) {
            DatasetMission mission = missionMapper.getMissionById(missionId);
            DatasetItem item = new DatasetItem();
            item.id = mission.id;
            item.sampleId = sample.id;
            item.imageName = mission.imageName;
            item.labelName = mission.imageName.replace(".tiff", ".geojson").replace(".tif", ".geojson");
            sample.items.add(item);
        }
        sampleMapper.save(sample);
        sampleMapper.saveItem(sample.items);
    }

    @Override
    public void deleteSample(String id) {
        sampleMapper.delete(id);
    }
}
