package com.gzpi.detection.controller;

import com.gzpi.detection.bean.*;
import com.gzpi.detection.operation.PathSelector;
import com.gzpi.detection.service.ITrainingService;
import com.gzpi.detection.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/train/")
public class TrainingController {
    @Autowired
    private ITrainingService trainingService;
    @Autowired
    private PathSelector pathSelector;
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();

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
    public DatasetResponse<TrainingModel> getAllSamples(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String version,
            @RequestParam(required = false) String status) {
        DatasetResponse<TrainingModel> response = new DatasetResponse<>();
        response.list = trainingService.getAllModels(name, version, status);
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

    @RequestMapping(value = "model/publish/{id}", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse publish(@PathVariable("id") String modelId) {
        try {
            trainingService.publishModel(modelId);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("startTraining fail", e);
            return BaseResponse.fail(e.getMessage());
        }
    }

    @RequestMapping(value = "model/record/{id}", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> result(@PathVariable(name = "id") String modelId) {
        try {
            String path = pathSelector.getModelRecordPath(modelId);
            if (!FileUtil.isFileExist(path)) {
                return ResponseEntity.notFound().build();
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment;filename=log.csv");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resourceLoader.getResource("file:" + path));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
