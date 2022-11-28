package com.gzpi.detection.controller;

import com.gzpi.detection.bean.*;
import com.gzpi.detection.service.IDatasetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/dataset/")
public class DatasetController {
    @Autowired
    private IDatasetService datasetService;


    @RequestMapping(value = "project/add", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse addProject(@RequestBody DatasetProject project) {
        try {
            datasetService.addProject(project);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("add dataset project fail", e);
            return BaseResponse.fail(e.getMessage());
        }
    }

    @RequestMapping(value = "project/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse deleteProject(@PathVariable("id") String id) {
        try {
            datasetService.deleteProject(id);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("delete dataset project fail", e);
            return BaseResponse.fail(e.getMessage());
        }
    }

    @RequestMapping(value = "project/all", method = RequestMethod.GET)
    @ResponseBody
    public DatasetResponse<DatasetProject> getAllProject() {
        List<DatasetProject> datasetProjects = datasetService.getAllProject();
        DatasetResponse<DatasetProject> response = new DatasetResponse<>();
        response.list = datasetProjects;
        response.msg = "success";
        return response;
    }

    @RequestMapping(value = "mission/all", method = RequestMethod.GET)
    @ResponseBody
    public DatasetResponse<DatasetMission> getMissionsByProjectId(@RequestParam(value = "projectId",required = false) String pid) {
        List<DatasetMission> datasetMissions = datasetService.getMissionsByProjectId(pid);
        DatasetResponse<DatasetMission> response = new DatasetResponse<>();
        response.list = datasetMissions;
        response.msg = "success";
        return response;
    }

    @RequestMapping(value = "mission/add", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse addProject(@RequestBody DatasetMission mission) {
        try {
            datasetService.addMission(mission);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("add dataset mission fail", e);
            return BaseResponse.fail(e.getMessage());
        }
    }

    @RequestMapping(value = "mission/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse deleteMission(@PathVariable("id") String id) {
        try {
            datasetService.deleteMission(id);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("delete dataset Mission fail", e);
            return BaseResponse.fail(e.getMessage());
        }
    }

    @RequestMapping(value = "sample/all", method = RequestMethod.GET)
    @ResponseBody
    public DatasetResponse<DatasetSample> getAllSamples() {
        DatasetResponse<DatasetSample> response = new DatasetResponse<>();
        response.list = datasetService.getAllSamples();
        response.msg = "success";
        return response;
    }

    @RequestMapping(value = "sample/add", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse save(@RequestBody DatasetSample sample) {
        try {
            datasetService.saveSample(sample);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("save dataset sample fail", e);
            return BaseResponse.fail(e.getMessage());
        }
    }
    @RequestMapping(value = "sample/addFromMissions", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse createFromMissions(@RequestBody DatasetRequest<List<String>> request) {
        try {
            datasetService.addSampleFromMissions(request);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("save dataset sample fail", e);
            return BaseResponse.fail(e.getMessage());
        }
    }

    @RequestMapping(value = "sample/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse deleteSample(@PathVariable("id") String id) {
        try {
            datasetService.deleteSample(id);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("delete dataset Mission fail", e);
            return BaseResponse.fail(e.getMessage());
        }
    }
}
