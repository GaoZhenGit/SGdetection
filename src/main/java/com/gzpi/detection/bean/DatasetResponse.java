package com.gzpi.detection.bean;

import java.util.ArrayList;
import java.util.List;

public class DatasetResponse<D> extends BaseResponse{
    public List<D> list = new ArrayList<>();
    public D item;
}
