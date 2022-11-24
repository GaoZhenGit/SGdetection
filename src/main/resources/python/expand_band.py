from osgeo import gdal
from osgeo import osr
import time
import numpy as np
import sys

def expand_band(src, des):
    in_ds = gdal.Open(src)
    width = in_ds.RasterXSize  # 获取数据宽度
    height = in_ds.RasterYSize  # 获取数据高度
    im_geotrans = in_ds.GetGeoTransform()  # 获取仿射矩阵信息
    im_proj = in_ds.GetProjection()  # 获取投影信息
    datatype = in_ds.GetRasterBand(1).DataType
    in_band1 = in_ds.GetRasterBand(1)

    out_band1 = (1 - in_band1.ReadAsArray(0,0,width,height)) * 255 / 2
    gtif_driver = gdal.GetDriverByName("GTiff")
    out_ds = gtif_driver.Create(des, width, height, 4, datatype)
    out_ds.SetGeoTransform(im_geotrans)
    out_ds.SetProjection(im_proj)
    out_ds.GetRasterBand(1).WriteArray(out_band1)
    out_band1[out_band1>0] = 1

    zeroBand = np.zeros((height, width))
    out_ds.GetRasterBand(2).WriteArray(zeroBand)
    out_ds.GetRasterBand(3).WriteArray(zeroBand)
    oneBand = np.ones((height, width)) * 255
    oneBand[out_band1==0] = 0
    out_ds.GetRasterBand(4).WriteArray(oneBand)
    out_ds.FlushCache()
    pass

if __name__ == '__main__':
    expand_band(sys.argv[1], sys.argv[2])
    # expand_band('D:\\download\\change_1.tif', 'D:\\download\\_change_1.tif')
    pass