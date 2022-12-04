package com.gzpi.detection.service;

import com.gzpi.detection.bean.DatasetItem;
import com.gzpi.detection.bean.LabelType;
import com.gzpi.detection.bean.TrainingModel;
import com.gzpi.detection.mapper.ModelMapper;
import com.gzpi.detection.operation.CommandExecutor;
import com.gzpi.detection.operation.PathSelector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TrainingServiceImpl implements ITrainingService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PathSelector pathSelector;
    @Value("${train.config.python.path}")
    private String configPythonPath;
    @Value("${common.train.command}")
    private String trainCommand;

    private final Executor mThreadPool = Executors.newCachedThreadPool();

    @Override
    public TrainingModel addModel(TrainingModel model) {
        modelMapper.save(model);
        return modelMapper.getModelById(model.id);
    }

    @Override
    public List<TrainingModel> getAllModels(String name, String version) {
        List<TrainingModel> models = modelMapper.getAllModels();
        if (name != null && !name.isEmpty()) {
            models = models.stream().filter(model -> Objects.equals(model.name, name)).collect(Collectors.toList());
        }
        if (version != null && !version.isEmpty()) {
            models = models.stream().filter(model -> Objects.equals(model.version, version)).collect(Collectors.toList());
        }
        return models;
    }

    @Override
    public void deleteModel(String id) {
        modelMapper.delete(id);
    }

    @Override
    public void startTraining(String modelId) throws Exception {
        TrainingModel model = modelMapper.getModelById(modelId);
        String workspacePath = pathSelector.getTrainingWorkspaceDir(modelId);
        trainingConfig(model, workspacePath);
        train(model, workspacePath);
    }

    private void trainingConfig(TrainingModel model, String workspacePath) throws Exception {
        String cmd = "python " + configPythonPath;
        cmd = cmd.replace("$learning_rate", String.valueOf(model.learningRate))
                .replace("$id", model.id)
                .replace("$workspace", workspacePath)
                .replace("$epoch", String.valueOf(model.epoch))
                .replace("$backbone", model.backbone.toString())
                .replace("$model", model.model.toString())
                .replace("$batch_size", String.valueOf(model.batchSize))
                .replace("$chip_size", String.valueOf(model.chipSize))
                .replace("$type", model.sample.type.toString());
        StringBuilder labels = new StringBuilder();
        StringBuilder images = new StringBuilder();
        StringBuilder images2 = new StringBuilder();
        for (int i = 0; i < model.sample.items.size(); i++) {
            DatasetItem item = model.sample.items.get(i);
            labels.append(pathSelector.getUploadImageDir()).append(item.labelName);
            images.append(pathSelector.getUploadImageDir()).append(item.imageName);
            images2.append(pathSelector.getUploadImageDir()).append(item.image2Name);
            if (i != model.sample.items.size() - 1) {
                labels.append(" ");
                images.append(" ");
                images2.append(" ");
            }
        }
        cmd = cmd.replace("$images1", images.toString());
        cmd = cmd.replace("$labels", labels.toString());
        if (model.sample.type == LabelType.change) {
            cmd = cmd.replace("$images2", images2.toString());
        } else {
            cmd = cmd.replace("$images2 ", "");
        }
        log.info("training config command:" + cmd);
        CommandExecutor configExec = new CommandExecutor(cmd, workspacePath);
        configExec.run();
        int code = configExec.getResultCode();
        if (code != 0) {
            throw new Exception("training config error" + configExec.getErrMsg());
        }
    }

    private void train(TrainingModel model, String workspacePath) throws Exception {
        String cmd = trainCommand;
        cmd = cmd.replace("$src", workspacePath + "/config.py");
        CommandExecutor trainExec = new CommandExecutor(cmd, workspacePath);
        mThreadPool.execute(trainExec);
    }
}
