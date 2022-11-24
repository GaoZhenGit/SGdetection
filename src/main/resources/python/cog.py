from osgeo import gdal
from osgeo import osr
import time
import numpy as np
import sys

def translateToCOG(in_ds, out_path):
    """
    将dataset转为cog文件
    :param in_ds: 输入dataset
    :param out_path: 输出路径
    :return:
    """
    im_bands = in_ds.RasterCount
    for i in range(im_bands):
        # 获取nodata和波段统计值
        nodataVal = in_ds.GetRasterBand(i + 1).GetNoDataValue()
        maxBandValue = in_ds.GetRasterBand(i + 1).GetMaximum()
        # 缺啥设置啥
        if maxBandValue is None:
            in_ds.GetRasterBand(i + 1).ComputeStatistics(0)
        if nodataVal is None:
            in_ds.GetRasterBand(i + 1).SetNoDataValue(0.0)
    in_ds.BuildOverviews("NEAREST", [1, 2, 4, 8, 16, 32, 64, 128])
    driver = gdal.GetDriverByName('GTiff')
    driver.CreateCopy(out_path, in_ds,
                      options=["COPY_SRC_OVERVIEWS=YES",
                               "TILED=YES",
                               "COMPRESS=DEFLATE",
                               "INTERLEAVE=BAND"])


def getTifDataset(fileDir, srid=None):
    """
    返回tif文件dataset
    :param fileDir:文件路径
    :param srid:epsg_srid，若指定且不同于dataset，就将dataset转为该空间参考
    :return:dataset
    """
    dataset = gdal.Open(fileDir, gdal.GA_ReadOnly)
    if dataset is None:
        print(fileDir + "文件无法打开")
        return
    fileSrs = osr.SpatialReference()
    fileSrs.ImportFromWkt(dataset.GetProjection())

    if srid is None:
        return dataset
    else:
        outSrs = osr.SpatialReference()
        outSrs.ImportFromEPSG(srid)

        if fileSrs.IsSame(outSrs):
            return dataset
        else:
            return warpDataset(dataset, outSrs.ExportToWkt())


def warpDataset(in_ds, proj, resampling=1):
    """
    转换空间参考
    :param in_ds:输入dataset
    :param proj: 目标空间参考wkt
    :param resampling: 重采样方法
    :return: 转换后的dataset
    """
    RESAMPLING_MODEL = ['', gdal.GRA_NearestNeighbour,
                        gdal.GRA_Bilinear, gdal.GRA_Cubic]

    resampleAlg = RESAMPLING_MODEL[resampling]

    return gdal.AutoCreateWarpedVRT(in_ds, None, proj, resampleAlg)

def cog(inPath, outputPath):
    start = time.process_time()
    originDataset = getTifDataset(inPath, 4326)
    translateToCOG(originDataset, outputPath)
    originDataset = None
    end = time.process_time()
    print('Running time: %s Seconds' % (end - start))

if __name__ == '__main__':
    cog(sys.argv[1], sys.argv[2])
