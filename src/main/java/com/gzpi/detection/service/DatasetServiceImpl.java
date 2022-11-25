package com.gzpi.detection.service;

import com.gzpi.detection.bean.DatasetProject;
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

    public void createLabelMission() {

    }

    public void getLabelMissions() {

    }

    public void deleteLabelMission() {

    }
}
