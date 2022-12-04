from rastervision.core.rv_pipeline import *
from rastervision.core.backend import *
from rastervision.core.data import *
from rastervision.pytorch_backend import *
from rastervision.pytorch_learner import *
import os

def findAllFile(base):
    for root, ds, fs in os.walk(base):
        for f in fs:
            yield f

def get_sense(workspace):
    image_dir = workspace + "/images/"
    label_dir = workspace + "/labels/"
    for f in findAllFile(label_dir):
        img_path = image_dir + f.replace('.geojson', '.tif')
        label_path = label_dir + f
        yield img_path, label_path

def get_sense_test(train_set, rate = 0.1):
    import random
    count = int(len(train_set)*rate+0.5)
    return random.choices(train_set, k=count)

def get_config(runner) -> SemanticSegmentationConfig:
    external_model = True
    chip_sz = REPLACE_chip_size
    workspace = 'REPLACE_workspace'

    class_config = ClassConfig(
        names=['target'],
        colors=['red'],
    )

    def make_scene(scene_id: str, image_uri: str, label_uri: str) -> SceneConfig:
        raster_source = RasterioSourceConfig(
            channel_order=[0, 1, 2],
            uris=[image_uri],
        )

        vector_source = GeoJSONVectorSourceConfig(
            default_class_id=0,
            uri=label_uri,
            ignore_crs_field=True,
        )
        rasterizer = RasterizerConfig(
            background_class_id=1,
        )
        rasterizedsource = RasterizedSourceConfig(
            vector_source=vector_source,
            rasterizer_config=rasterizer,
        )

        label_source = SemanticSegmentationLabelSourceConfig(
            raster_source=rasterizedsource,
        )

        scene = SceneConfig(
            id=scene_id,
            raster_source=raster_source,
            label_source=label_source,
            label_store = SemanticSegmentationLabelStoreConfig(vector_output=[PolygonVectorOutputConfig(class_id=0)])
        )
        return scene

    train_set = [make_scene(i, img, label) for i, (img, label) in get_sense(workspace)]
    test_set = get_sense_test(train_set)
    scene_dataset = DatasetConfig(
        class_config=class_config,
        #   -ClassConfig
        train_scenes = train_set,
        validation_scenes = test_set,
        test_scenes=[
        ],
        img_channels=3,
    )
    if external_model:
        model = SemanticSegmentationModelConfig(
            external_def=ExternalModuleConfig(
                uri="/opt/src/code/input/SemanticSegmentation/vision-0.8.1",
                entrypoint=REPLACE_model,
                force_reload=False,
            )
        )
    else:
        model = SemanticSegmentationModelConfig(
            backbone=REPLACE_backbone,
        )

    solver = SolverConfig(
        lr=REPLACE_learning_rate,
        num_epochs=REPLACE_epoch,
        batch_sz=REPLACE_batch_size,
        ignore_last_class=False,
    )

    window_opts = GeoDataWindowConfig(
        method=GeoDataWindowMethod.random,
        size=chip_sz,
        size_lims=(chip_sz, chip_sz + 1),
        max_windows=20,
    )

    data = SemanticSegmentationGeoDataConfig(
        scene_dataset=scene_dataset,
        window_opts=window_opts,
        img_channels=3,
    )

    backend = PyTorchSemanticSegmentationConfig(
        model=model,
        solver=solver,
        data=data,
    )

    semanticsegmentation = SemanticSegmentationConfig(
        root_uri=workspace+'/output',
        dataset=scene_dataset,
        backend=backend,
        train_chip_sz=chip_sz,
        predict_chip_sz=chip_sz,
        chip_nodata_threshold=1,
        source_bundle_uri=None,
        label_format='geojson',
    )

    return semanticsegmentation