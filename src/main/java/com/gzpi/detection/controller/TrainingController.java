package com.gzpi.detection.controller;

import com.gzpi.detection.bean.*;
import com.gzpi.detection.service.ITrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/train/")
public class TrainingController {
    @Autowired
    private ITrainingService trainingService;

    @RequestMapping(value = "model/add", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse addModel(@RequestBody TrainingModel model) {
        try {
            model = trainingService.addModel(model);
            DatasetResponse<TrainingModel> response = new DatasetResponse<>();
            response.msg = "success";
            response.item = model;
            return response;
        } catch (Exception e) {
            log.error("add dataset mission fail", e);
            return BaseResponse.fail(e.getMessage());
        }
    }

    @RequestMapping(value = "model/all", method = RequestMethod.GET)
    @ResponseBody
    public DatasetResponse<TrainingModel> getAllSamples(@RequestParam(required = false) String name, @RequestParam(required = false) String version) {
        DatasetResponse<TrainingModel> response = new DatasetResponse<>();
        response.list = trainingService.getAllModels(name, version);
        response.msg = "success";
        return response;
    }

    @RequestMapping(value = "model/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse deleteModel(@PathVariable("id") String id) {
        try {
            trainingService.deleteModel(id);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("add dataset mission fail", e);
            return BaseResponse.fail(e.getMessage());
        }
    }

    @RequestMapping(value = "model/startTraining/{id}", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse startTraining(@PathVariable("id") String modelId) {
        try {
            trainingService.startTraining(modelId);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("startTraining fail", e);
            return BaseResponse.fail(e.getMessage());
        }
    }
}
