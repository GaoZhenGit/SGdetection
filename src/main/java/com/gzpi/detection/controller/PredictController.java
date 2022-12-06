package com.gzpi.detection.controller;

import com.gzpi.detection.bean.BaseResponse;
import com.gzpi.detection.bean.PredictRequest;
import com.gzpi.detection.bean.PredictResponse;
import com.gzpi.detection.mission.CopyGeoJsonMission;
import com.gzpi.detection.operation.CommandExecutor;
import com.gzpi.detection.operation.PathSelector;
import com.gzpi.detection.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Controller
@CrossOrigin
@RequestMapping("/model/")
public class PredictController {
    Logger logger = LoggerFactory.getLogger(PredictController.class);
    @Autowired
    private PathSelector pathSelector;
    @Autowired
    private FileController fileController;

    private final Executor mThreadPool = Executors.newCachedThreadPool();
    private final Map<String, CommandExecutor> mTaskList = new ConcurrentHashMap<>();

    @Value("${band.python.path}")
    private String expandBandCommand;
    @Value("${change.predict.command}")
    private String changePredictCommand;
    @Value("${building.predict.command}")
    private String buildingPredictCommand;

    private String changeBundleName = "change-model-bundle.zip";
    private String buildingBundleName = "building-model-bundle.zip";

    @RequestMapping(value = "change/predict", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse changePredict(@RequestBody PredictRequest predictRequest) {
        List<String> files = fileController.listFiles().files;
        if (!files.contains(predictRequest.img1) || !files.contains(predictRequest.img2)) {
            return BaseResponse.fail("server has not these images");
        }
        CommandExecutor e = mTaskList.get(predictRequest.id);
        if (e != null && !e.hasFinished()) {
            return BaseResponse.fail(predictRequest.id + " task has executed!");
        }
        String bundlePath = null;
        if (predictRequest.modelId != null && !predictRequest.modelId.isEmpty()) {
            bundlePath = pathSelector.getPublishedModelDir(predictRequest.modelId);
            if (!FileUtil.isFileExist(bundlePath)) {
                return BaseResponse.fail("model " + predictRequest.modelId + " not published");
            }
            logger.info("use custom model:" + bundlePath);
        } else {
            bundlePath = pathSelector.getModelBundleDir() + changeBundleName;
            logger.info("use default model:" + bundlePath);
        }
        String cmd = changePredictCommand
                .replace("$model", bundlePath)
                .replace("$img1", pathSelector.getPredictImagePath(predictRequest.img1))
                .replace("$img2", pathSelector.getPredictImagePath(predictRequest.img2))
                .replace("$output", pathSelector.getPredictTaskOutputPath(predictRequest.id));
        e = new CommandExecutor(cmd, pathSelector.getPredictTaskOutputPath(predictRequest.id));
        e.usingFiles = Arrays.asList(predictRequest.img1,predictRequest.img2);
        if (isFilesUsing(predictRequest.img1, predictRequest.img2)) {
            return BaseResponse.fail("selecting files is using by other task");
        }
        e.addPostMission(new CopyGeoJsonMission(predictRequest, pathSelector, logger));
        mTaskList.put(predictRequest.id, e);
        mThreadPool.execute(e);
        logger.info("start predict:" + predictRequest.id);
        return BaseResponse.success();
    }

    @RequestMapping(value = "building/predict", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse buildingPredict(@RequestBody PredictRequest predictRequest) {
        List<String> files = fileController.listFiles().files;
        if (!files.contains(predictRequest.img1)) {
            return BaseResponse.fail("server has not these images");
        }
        CommandExecutor e = mTaskList.get(predictRequest.id);
        if (e != null && !e.hasFinished()) {
            return BaseResponse.fail(predictRequest.id + " task has executed!");
        }
        String bundlePath = null;
        if (predictRequest.modelId != null && !predictRequest.modelId.isEmpty()) {
            bundlePath = pathSelector.getPublishedModelDir(predictRequest.modelId);
            if (!FileUtil.isFileExist(bundlePath)) {
                return BaseResponse.fail("model " + predictRequest.modelId + " not published");
            }
            logger.info("use custom model:" + bundlePath);
        } else {
            bundlePath = pathSelector.getModelBundleDir() + buildingBundleName;
            logger.info("use default model:" + bundlePath);
        }
        String cmd = buildingPredictCommand
                .replace("$model", bundlePath)
                .replace("$img1", pathSelector.getPredictImagePath(predictRequest.img1))
                .replace("$output", pathSelector.getPredictTaskOutputPath(predictRequest.id));
        e = new CommandExecutor(cmd, pathSelector.getPredictTaskOutputPath(predictRequest.id));
        e.usingFiles = Arrays.asList(predictRequest.img1);
        if (isFilesUsing(predictRequest.img1)) {
            return BaseResponse.fail("selecting files is using by other task");
        }
        e.addPostMission(new CopyGeoJsonMission(predictRequest, pathSelector, logger));
        mTaskList.put(predictRequest.id, e);
        mThreadPool.execute(e);
        logger.info("start predict:" + predictRequest.id);
        return BaseResponse.success();
    }

    @RequestMapping(value = "tasks", method = RequestMethod.GET)
    @ResponseBody
    public PredictResponse queryTask() {
        PredictResponse response = new PredictResponse();
        for (Map.Entry<String, CommandExecutor> em: mTaskList.entrySet()) {
            String id = em.getKey();
            CommandExecutor value = em.getValue();
            PredictResponse.PredictItem item = new PredictResponse.PredictItem();
            item.id = id;
            if (value == null) {
                item.status = "none";
            } else if (value.hasFinished()) {
                item.status = "finished";
                item.resultCode = value.getResultCode();
            } else {
                item.status = "loading";
            }
            response.list.add(item);
        }
        return response;
    }

    private boolean isFilesUsing(String img1, String img2) {
        List<String> usingFiles = new ArrayList<>();
        for (CommandExecutor executor : mTaskList.values()) {
            if (!executor.hasFinished()) {
                usingFiles.addAll(executor.usingFiles);
            }
        }
        return usingFiles.contains(img1) || usingFiles.contains(img2);
    }

    private boolean isFilesUsing(String img) {
        List<String> usingFiles = new ArrayList<>();
        for (CommandExecutor executor : mTaskList.values()) {
            if (!executor.hasFinished()) {
                usingFiles.addAll(executor.usingFiles);
            }
        }
        return usingFiles.contains(img);
    }
}
