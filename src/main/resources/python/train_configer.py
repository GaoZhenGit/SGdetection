import argparse
import os
import shutil
import numpy as np
from osgeo import gdal

def parse_opt():
    parser = argparse.ArgumentParser()
    parser.add_argument('--id')
    parser.add_argument('--chip_size')
    parser.add_argument('--epoch')
    parser.add_argument('--learning_rate')
    parser.add_argument('--batch_size')
    parser.add_argument('--model')
    parser.add_argument('--backbone')
    parser.add_argument('--workspace')
    parser.add_argument('--images',nargs='+')
    parser.add_argument('--images2',nargs='*')
    parser.add_argument('--labels',nargs='+')
    parser.add_argument('--type', choices=['detection', 'change'], required=True)
    opt = parser.parse_args()
    print(opt)
    return opt

def create_real_config(args):
    current_path = os.path.abspath(__file__)
    father_path = os.path.abspath(os.path.dirname(current_path) + os.path.sep + ".")
    template = open(father_path + '/train_template.py')
    real_config = open(args.workspace + '/config.py', 'w')
    for l in template.readlines():
        l = l.replace('REPLACE_workspace', args.workspace)
        l = l.replace('REPLACE_chip_size', args.chip_size)
        l = l.replace('REPLACE_epoch', args.epoch)
        l = l.replace('REPLACE_learning_rate', args.learning_rate)
        l = l.replace('REPLACE_batch_size', args.batch_size)
        l = l.replace('REPLACE_model', args.model)
        l = l.replace('REPLACE_backbone', args.backbone)
        real_config.write(l)
    real_config.flush()
    real_config.close()
    template.close()
    pass

def copy_label_data(args):
    label_dir = args.workspace + "/labels/"
    if os.path.exists(label_dir):
        shutil.rmtree(label_dir)
    os.makedirs(label_dir)
    for i, label in enumerate(args.labels):
        shutil.copyfile(label, label_dir + str(i) + ".geojson")

def copy_detection_data(args):
    if args.type != 'detection':
        return
    img_dir = args.workspace + "/images/"
    if os.path.exists(img_dir):
        shutil.rmtree(img_dir)
    os.makedirs(img_dir)
    for i, img in enumerate(args.images):
        shutil.copyfile(img, img_dir + str(i) + ".tif")

    if args.type == 'change':
        img_dir = args.workspace + "/images2/"
        if os.path.exists(img_dir):
            shutil.rmtree(img_dir)
        os.makedirs(img_dir)
        for i, img in enumerate(args.images2):
            shutil.copyfile(img, img_dir + str(i) + ".tif")

def diffBand(file1, file2, outPutFile, bandCount=3):
    f1 = gdal.Open(file1)
    f2 = gdal.Open(file2)

    width = f1.RasterXSize  # 获取数据宽度
    height = f1.RasterYSize  # 获取数据高度
    outbandsize = f1.RasterCount  # 获取数据波段数
    im_geotrans = f1.GetGeoTransform()  # 获取仿射矩阵信息
    im_proj = f1.GetProjection()  # 获取投影信息
    datatype = f1.GetRasterBand(1).DataType

    gtif_driver = gdal.GetDriverByName("GTiff")
    print('will output:' + outPutFile + ' band:' + str(bandCount))
    out_ds = gtif_driver.Create(outPutFile, width, height, bandCount, datatype)
    out_ds.SetGeoTransform(im_geotrans)
    # 设置SRS属性（投影信息）
    out_ds.SetProjection(im_proj)

    for i in range(bandCount):
        print('band:'+str(i+1))
        in_band1 = f1.GetRasterBand(i+1)
        in_band2 = f2.GetRasterBand(i+1)
        out_band1 = in_band1.ReadAsArray(0,0,width,height).astype(np.int16)
        out_band2 = in_band2.ReadAsArray(0,0,width,height).astype(np.int16)

        # out_band = out_band2 / base + np.abs(out_band2 - out_band1) * enhanse
        diff = np.abs(out_band1 - out_band2)
        avg = np.average(diff)
        std = np.std(diff)
        print('before:avg:{},std:{}'.format(avg,std))
        diff[diff>=avg] = np.sqrt(diff[diff>=avg]) * 16
        diff[diff<avg] = (diff[diff<avg] / 16) ** 2
        out_band = diff.astype(np.uint8)
        avg = np.average(out_band)
        std = np.std(out_band)
        print('after:avg:{},std:{}'.format(avg,std))
        out_ds.GetRasterBand(i+1).WriteArray(out_band)
        out_ds.FlushCache()
    pass

def prepare_change_data(args):
    if args.type != 'change':
        return
    img_dir = args.workspace + "/images/"
    if os.path.exists(img_dir):
        shutil.rmtree(img_dir)
    os.makedirs(img_dir)
    for i, (img1, img2) in enumerate(zip(args.images, args.images2)):
        print(i, img1, img2)
        diffBand(img1, img2, img_dir + str(i) + '.tif')

def findAllFile(base):
    for root, ds, fs in os.walk(base):
        for f in fs:
            yield f

if __name__ == "__main__":
    args = parse_opt()
    create_real_config(args)
    copy_label_data(args)
    copy_detection_data(args)
    prepare_change_data(args)
    pass