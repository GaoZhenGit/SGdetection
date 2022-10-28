package com.gzpi.detection.controller;

import com.gzpi.detection.bean.BaseResponse;
import com.gzpi.detection.bean.PredictRequest;
import com.gzpi.detection.operation.CommandExecutor;
import com.gzpi.detection.operation.PathSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class PredictController {
    Logger logger = LoggerFactory.getLogger(PredictController.class);
    @Autowired
    private PathSelector pathSelector;
    @Autowired
    private FileController fileController;

    private Executor mThreadPool = Executors.newCachedThreadPool();
    private Map<String, CommandExecutor> mTaskList = new ConcurrentHashMap<>();

    @Value("${predict.command}")
    private String command;

    @RequestMapping(value = "predict", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse predict(@RequestBody PredictRequest predictRequest) {
        List<String> files = fileController.listFiles().files;
        if (!files.contains(predictRequest.img1) || !files.contains(predictRequest.img2)) {
            return BaseResponse.fail("server has not these images");
        }
        String dockerOutputPath = pathSelector.getDockerBasePath("resultset") + "/" + predictRequest.id + "/";
        String realOutPath = pathSelector.getRealPath("resultset") + "/" + predictRequest.id + "/";
        CommandExecutor e = mTaskList.get(predictRequest.id);
        if (e != null && !e.hasFinished()) {
            return BaseResponse.fail(predictRequest.id+" task has executed!");
        }
        String cmd = command
                .replace("$model", pathSelector.getDockerBasePath("bundle/model-bundle.zip"))
                .replace("$img1", pathSelector.getDockerBasePath(predictRequest.img1))
                .replace("$img2", pathSelector.getDockerBasePath(predictRequest.img2))
                .replace("$output", dockerOutputPath);
        e = new CommandExecutor(cmd, realOutPath);
        mTaskList.put(predictRequest.id, e);
        mThreadPool.execute(e);
        logger.info("start predict:" + predictRequest.id);
        return BaseResponse.success();
    }

//    @RequestMapping(value = "queryTask", method = RequestMethod.GET)
//    @ResponseBody
//    public BaseResponse queryTask(@RequestBody PredictRequest predictRequest) {
//
//    }
}
