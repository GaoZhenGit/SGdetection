package com.gzpi.detection.service;

import com.gzpi.detection.bean.DatasetMission;
import com.gzpi.detection.bean.DatasetProject;
import com.gzpi.detection.mapper.DatasetMissionMapper;
import com.gzpi.detection.mapper.DatasetProjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DatasetServiceImpl implements IDatasetService {
    @Autowired
    private DatasetProjectMapper projectMapper;
    @Autowired
    private DatasetMissionMapper missionMapper;

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
    public void addMission(DatasetMission mission) {
        missionMapper.save(mission);
    }

    @Override
    public void deleteMission(String id) {
        missionMapper.delete(id);
    }
}
