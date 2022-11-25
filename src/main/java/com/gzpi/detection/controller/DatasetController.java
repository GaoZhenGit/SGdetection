package com.gzpi.detection.controller;

import com.gzpi.detection.bean.BaseResponse;
import com.gzpi.detection.bean.DatasetProject;
import com.gzpi.detection.bean.DatasetProjectResponse;
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
    public DatasetProjectResponse getAllProject() {
        List<DatasetProject> datasetProjects = datasetService.getAllProject();
        DatasetProjectResponse response = new DatasetProjectResponse();
        response.list = datasetProjects;
        response.msg = "success";
        return response;
    }
}
