package mara.mybox.controller;

import java.text.MessageFormat;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import mara.mybox.data.AlarmClock;
import mara.mybox.value.AppVaribles;
import static mara.mybox.value.AppVaribles.scheduledTasks;
import mara.mybox.value.CommonValues;
import static mara.mybox.value.AppVaribles.logger;

/**
 * @Author Mara
 * @CreateDate 2018-6-4 17:48:15
 * @Description
 * @License Apache License Version 2.0
 */
public class MyBoxController extends BaseController {

    private ContextMenu pdfMenus, imageMenu, fileMenu, desktopMenu, languageMenu, networkMenu;

    @FXML
    private VBox imageBox, pdfBox, fileBox, desktopBox, languageBox, networkBox;

    @Override
    protected void initializeNext() {
        try {
            initPdfToolsMenu();
            initImageToolsMenu();
            initDesktopToolsMenu();
            initFileToolsMenu();
            initNetworkToolsMenu();
            initOtherMenu();

            List<AlarmClock> alarms = AlarmClock.readAlarmClocks();
            if (alarms != null) {
                for (AlarmClock alarm : alarms) {
                    if (alarm.isIsActive()) {
                        AlarmClock.scehduleAlarmClock(alarm);
                    }
                }
                if (scheduledTasks != null && scheduledTasks.size() > 0) {
                    bottomLabel.setText(MessageFormat.format(AppVaribles.getMessage("AlarmClocksRunning"), scheduledTasks.size()));
                }
            }

        } catch (Exception e) {
            logger.debug(e.toString());
        }

    }

    private void initPdfToolsMenu() {
        MenuItem pdfView = new MenuItem(AppVaribles.getMessage("PdfView"));
        pdfView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfViewFxml, AppVaribles.getMessage("PdfView"));
            }
        });

        MenuItem pdfExtractImages = new MenuItem(AppVaribles.getMessage("PdfExtractImages"));
        pdfExtractImages.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfExtractImagesFxml, AppVaribles.getMessage("PdfExtractImages"));
            }
        });

        MenuItem pdfExtractImagesBatch = new MenuItem(AppVaribles.getMessage("PdfExtractImagesBatch"));
        pdfExtractImagesBatch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfExtractImagesBatchFxml, AppVaribles.getMessage("PdfExtractImagesBatch"));
            }
        });

        MenuItem pdfExtractTexts = new MenuItem(AppVaribles.getMessage("PdfExtractTexts"));
        pdfExtractTexts.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfExtractTextsFxml, AppVaribles.getMessage("pdfExtractTexts"));
            }
        });

        MenuItem pdfExtractTextsBatch = new MenuItem(AppVaribles.getMessage("PdfExtractTextsBatch"));
        pdfExtractTextsBatch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfExtractTextsBatchFxml, AppVaribles.getMessage("PdfExtractTextsBatch"));
            }
        });

        MenuItem pdfConvertImages = new MenuItem(AppVaribles.getMessage("PdfConvertImages"));
        pdfConvertImages.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfConvertImagesFxml, AppVaribles.getMessage("PdfConvertImages"));
            }
        });

        MenuItem pdfConvertImagesBatch = new MenuItem(AppVaribles.getMessage("PdfConvertImagesBatch"));
        pdfConvertImagesBatch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfConvertImagesBatchFxml, AppVaribles.getMessage("PdfConvertImagesBatch"));
            }
        });

        MenuItem imagesCombinePdf = new MenuItem(AppVaribles.getMessage("ImagesCombinePdf"));
        imagesCombinePdf.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImagesCombinePdfFxml, AppVaribles.getMessage("ImagesCombinePdf"));
            }
        });

        MenuItem pdfCompressImages = new MenuItem(AppVaribles.getMessage("CompressPdfImages"));
        pdfCompressImages.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfCompressImagesFxml, AppVaribles.getMessage("CompressPdfImages"));
            }
        });

        MenuItem pdfCompressImagesBatch = new MenuItem(AppVaribles.getMessage("CompressPdfImagesBatch"));
        pdfCompressImagesBatch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfCompressImagesBatchFxml, AppVaribles.getMessage("CompressPdfImagesBatch"));
            }
        });

        MenuItem pdfMerge = new MenuItem(AppVaribles.getMessage("MergePdf"));
        pdfMerge.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfMergeFxml, AppVaribles.getMessage("MergePdf"));
            }
        });

        MenuItem pdfSplit = new MenuItem(AppVaribles.getMessage("SplitPdf"));
        pdfSplit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfSplitFxml, AppVaribles.getMessage("SplitPdf"));
            }
        });

        pdfMenus = new ContextMenu();
        pdfMenus.getItems().addAll(
                pdfView, new SeparatorMenuItem(),
                pdfConvertImages, pdfConvertImagesBatch, new SeparatorMenuItem(),
                pdfExtractImages, pdfExtractImagesBatch, pdfExtractTexts, pdfExtractTextsBatch, new SeparatorMenuItem(),
                imagesCombinePdf, new SeparatorMenuItem(),
                pdfMerge, pdfSplit, new SeparatorMenuItem(),
                pdfCompressImages, pdfCompressImagesBatch
        );

    }

    private void initImageToolsMenu() {
        MenuItem imageViewer = new MenuItem(AppVaribles.getMessage("ImageViewer"));
        imageViewer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageViewerFxml, AppVaribles.getMessage("ImageViewer"));
            }
        });

        MenuItem imagesBrowser = new MenuItem(AppVaribles.getMessage("ImagesBrowser"));
        imagesBrowser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImagesBrowserFxml, AppVaribles.getMessage("ImagesBrowser"));
            }
        });

        MenuItem ImageManufacture = new MenuItem(AppVaribles.getMessage("ImageManufacture"));
        ImageManufacture.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
            }
        });

        MenuItem imageConverter = new MenuItem(AppVaribles.getMessage("ImageConverter"));
        imageConverter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageConverterFxml, AppVaribles.getMessage("ImageConverter"));
            }
        });

        MenuItem imageConverterBatch = new MenuItem(AppVaribles.getMessage("ImageConverterBatch"));
        imageConverterBatch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageConverterBatchFxml, AppVaribles.getMessage("ImageConverterBatch"));
            }
        });

        MenuItem imageStatistic = new MenuItem(AppVaribles.getMessage("ImageStatistic"));
        imageStatistic.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageStatisticFxml, AppVaribles.getMessage("ImageStatistic"));
            }
        });

        MenuItem convolutionKernelManager = new MenuItem(AppVaribles.getMessage("ConvolutionKernelManager"));
        convolutionKernelManager.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ConvolutionKernelManagerFxml, AppVaribles.getMessage("ConvolutionKernelManager"));
            }
        });

        MenuItem pixelsCalculator = new MenuItem(AppVaribles.getMessage("PixelsCalculator"));
        pixelsCalculator.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openStage(CommonValues.PixelsCalculatorFxml, AppVaribles.getMessage("PixelsCalculator"), false, false);
            }
        });

        MenuItem colorPalette = new MenuItem(AppVaribles.getMessage("ColorPalette"));
        colorPalette.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openStage(CommonValues.ColorPaletteFxml, AppVaribles.getMessage("ColorPalette"), false, false);
            }
        });

        Menu manufactureSubMenu = initImageSubToolsMenu();
        Menu manufactureBatchMenu = initImageBatchToolsMenu();
        Menu framesMenu = initImageFramesMenu();
        Menu partMenu = initImagePartMenu();
        Menu mergeMenu = initImageMergeMenu();

        imageMenu = new ContextMenu();
        imageMenu.getItems().addAll(
                imageViewer, imagesBrowser, new SeparatorMenuItem(),
                ImageManufacture, manufactureSubMenu, manufactureBatchMenu, new SeparatorMenuItem(),
                framesMenu, new SeparatorMenuItem(), mergeMenu, new SeparatorMenuItem(), partMenu, new SeparatorMenuItem(),
                imageConverter, imageConverterBatch, new SeparatorMenuItem(),
                //                imageStatistic, new SeparatorMenuItem(),
                convolutionKernelManager, colorPalette, pixelsCalculator);
    }

    private Menu initImageSubToolsMenu() {

        MenuItem imageSizeMenu = new MenuItem(AppVaribles.getMessage("Size"));
        imageSizeMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ImageManufactureFileController controller
                        = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
                controller.setInitTab("size");
            }
        });

        MenuItem imageCropMenu = new MenuItem(AppVaribles.getMessage("Crop"));
        imageCropMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ImageManufactureFileController controller
                        = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
                controller.setInitTab("crop");
            }
        });

        MenuItem imageColorMenu = new MenuItem(AppVaribles.getMessage("Color"));
        imageColorMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ImageManufactureFileController controller
                        = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
                controller.setInitTab("color");
            }
        });

        MenuItem imageEffectsMenu = new MenuItem(AppVaribles.getMessage("Effects"));
        imageEffectsMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ImageManufactureFileController controller
                        = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
                controller.setInitTab("effects");
            }
        });

        MenuItem imageTextMenu = new MenuItem(AppVaribles.getMessage("Text"));
        imageTextMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ImageManufactureFileController controller
                        = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
                controller.setInitTab("text");
            }
        });

        MenuItem imageCoverMenu = new MenuItem(AppVaribles.getMessage("Cover"));
        imageCoverMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ImageManufactureFileController controller
                        = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
                controller.setInitTab("cover");
            }
        });

        MenuItem imageTransformMenu = new MenuItem(AppVaribles.getMessage("Transform"));
        imageTransformMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ImageManufactureFileController controller
                        = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
                controller.setInitTab("transform");
            }
        });

        MenuItem imageMarginsMenu = new MenuItem(AppVaribles.getMessage("Margins"));
        imageMarginsMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ImageManufactureFileController controller
                        = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
                controller.setInitTab("margins");
            }
        });

        MenuItem imageArcMenu = new MenuItem(AppVaribles.getMessage("Arc"));
        imageArcMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ImageManufactureFileController controller
                        = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
                controller.setInitTab("arc");
            }
        });

        MenuItem imageShadowMenu = new MenuItem(AppVaribles.getMessage("Shadow"));
        imageShadowMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ImageManufactureFileController controller
                        = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
                controller.setInitTab("shadow");
            }
        });

        Menu manufactureSubMenu = new Menu(AppVaribles.getMessage("ImageManufactureSub"));
        manufactureSubMenu.getItems().addAll(imageSizeMenu, imageCropMenu, imageColorMenu, imageEffectsMenu,
                imageTextMenu, imageCoverMenu, imageArcMenu, imageShadowMenu,
                imageTransformMenu, imageMarginsMenu);
        return manufactureSubMenu;

    }

    private Menu initImageBatchToolsMenu() {

        MenuItem imageSizeMenu = new MenuItem(AppVaribles.getMessage("Size"));
        imageSizeMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageManufactureBatchSizeFxml, AppVaribles.getMessage("ImageManufactureBatchSize"));
            }
        });

        MenuItem imageCropMenu = new MenuItem(AppVaribles.getMessage("Crop"));
        imageCropMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageManufactureBatchCropFxml, AppVaribles.getMessage("ImageManufactureBatchCrop"));
            }
        });

        MenuItem imageColorMenu = new MenuItem(AppVaribles.getMessage("Color"));
        imageColorMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageManufactureBatchColorFxml, AppVaribles.getMessage("ImageManufactureBatchColor"));
            }
        });

        MenuItem imageEffectsMenu = new MenuItem(AppVaribles.getMessage("Effects"));
        imageEffectsMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageManufactureBatchEffectsFxml, AppVaribles.getMessage("ImageManufactureBatchEffects"));
            }
        });

        MenuItem imageReplaceColorMenu = new MenuItem(AppVaribles.getMessage("ReplaceColor"));
        imageReplaceColorMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageManufactureBatchReplaceColorFxml, AppVaribles.getMessage("ImageManufactureBatchReplaceColor"));
            }
        });

        MenuItem imageTextMenu = new MenuItem(AppVaribles.getMessage("Text"));
        imageTextMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageManufactureBatchTextFxml, AppVaribles.getMessage("ImageManufactureBatchText"));
            }
        });

        MenuItem imageArcMenu = new MenuItem(AppVaribles.getMessage("Arc"));
        imageArcMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageManufactureBatchArcFxml, AppVaribles.getMessage("ImageManufactureBatchArc"));
            }
        });

        MenuItem imageShadowMenu = new MenuItem(AppVaribles.getMessage("Shadow"));
        imageShadowMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageManufactureBatchShadowFxml, AppVaribles.getMessage("ImageManufactureBatchShadow"));
            }
        });

        MenuItem imageTransformMenu = new MenuItem(AppVaribles.getMessage("Transform"));
        imageTransformMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageManufactureBatchTransformFxml, AppVaribles.getMessage("ImageManufactureBatchTransform"));
            }
        });

        MenuItem imageCutMarginsMenu = new MenuItem(AppVaribles.getMessage("CutMargins"));
        imageCutMarginsMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageManufactureBatchCutMarginsFxml, AppVaribles.getMessage("ImageManufactureBatchCutMargins"));
            }
        });

        MenuItem imageAddMarginsMenu = new MenuItem(AppVaribles.getMessage("AddMargins"));
        imageAddMarginsMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageManufactureBatchAddMarginsFxml, AppVaribles.getMessage("ImageManufactureBatchAddMargins"));
            }
        });

        Menu manufactureBatchMenu = new Menu(AppVaribles.getMessage("ImageManufactureBatch"));
        manufactureBatchMenu.getItems().addAll(imageSizeMenu, imageCropMenu, imageColorMenu, imageEffectsMenu,
                imageReplaceColorMenu, imageTextMenu, imageArcMenu, imageShadowMenu, imageTransformMenu,
                imageCutMarginsMenu, imageAddMarginsMenu);
        return manufactureBatchMenu;

    }

    private Menu initImageFramesMenu() {

        MenuItem imageGifViewer = new MenuItem(AppVaribles.getMessage("ImageGifViewer"));
        imageGifViewer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageGifViewerFxml, AppVaribles.getMessage("ImageGifViewer"));
            }
        });

        MenuItem imageGifEditer = new MenuItem(AppVaribles.getMessage("ImageGifEditer"));
        imageGifEditer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageGifEditerFxml, AppVaribles.getMessage("ImageGifEditer"));
            }
        });

        MenuItem imageTiffEditer = new MenuItem(AppVaribles.getMessage("ImageTiffEditer"));
        imageTiffEditer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageTiffEditerFxml, AppVaribles.getMessage("ImageTiffEditer"));
            }
        });

        MenuItem imageFramesViewer = new MenuItem(AppVaribles.getMessage("ImageFramesViewer"));
        imageFramesViewer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageFramesViewerFxml, AppVaribles.getMessage("ImageFramesViewer"));
            }
        });

        Menu manufactureSubMenu = new Menu(AppVaribles.getMessage("MultipleFramesImageFile"));
        manufactureSubMenu.getItems().addAll(imageFramesViewer, imageTiffEditer, imageGifViewer, imageGifEditer);
        return manufactureSubMenu;

    }

    private Menu initImagePartMenu() {

        MenuItem ImageSplit = new MenuItem(AppVaribles.getMessage("ImageSplit"));
        ImageSplit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageSplitFxml, AppVaribles.getMessage("ImageSplit"));
            }
        });

        MenuItem ImageSample = new MenuItem(AppVaribles.getMessage("ImageSubsample"));
        ImageSample.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageSampleFxml, AppVaribles.getMessage("ImageSubsample"));
            }
        });

        Menu manufactureSubMenu = new Menu(AppVaribles.getMessage("ImagePart"));
        manufactureSubMenu.getItems().addAll(ImageSplit, ImageSample);
        return manufactureSubMenu;

    }

    private Menu initImageMergeMenu() {

        MenuItem ImageCombine = new MenuItem(AppVaribles.getMessage("ImageCombine"));
        ImageCombine.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImagesCombineFxml, AppVaribles.getMessage("ImageCombine"));
            }
        });

        MenuItem ImagesBlend = new MenuItem(AppVaribles.getMessage("ImagesBlend"));
        ImagesBlend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImagesBlendFxml, AppVaribles.getMessage("ImagesBlend"));
            }
        });

        MenuItem imagesCombinePdf = new MenuItem(AppVaribles.getMessage("ImagesCombinePdf"));
        imagesCombinePdf.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImagesCombinePdfFxml, AppVaribles.getMessage("ImagesCombinePdf"));
            }
        });

        Menu manufactureSubMenu = new Menu(AppVaribles.getMessage("MergeImages"));
        manufactureSubMenu.getItems().addAll(ImagesBlend, ImageCombine, imagesCombinePdf);
        return manufactureSubMenu;

    }

    private void initDesktopToolsMenu() {
        MenuItem filesRename = new MenuItem(AppVaribles.getMessage("FilesRename"));
        filesRename.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.FilesRenameFxml, AppVaribles.getMessage("FilesRename"));
            }
        });

        MenuItem dirSynchronize = new MenuItem(AppVaribles.getMessage("DirectorySynchronize"));
        dirSynchronize.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.DirectorySynchronizeFxml, AppVaribles.getMessage("DirectorySynchronize"));
            }
        });

        MenuItem filesArrangement = new MenuItem(AppVaribles.getMessage("FilesArrangement"));
        filesArrangement.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.FilesArrangementFxml, AppVaribles.getMessage("FilesArrangement"));
            }
        });

        MenuItem textEditer = new MenuItem(AppVaribles.getMessage("TextEditer"));
        textEditer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.TextEditerFxml, AppVaribles.getMessage("TextEditer"));
            }
        });

        MenuItem textEncodingBatch = new MenuItem(AppVaribles.getMessage("TextEncodingBatch"));
        textEncodingBatch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.TextEncodingBatchFxml, AppVaribles.getMessage("TextEncodingBatch"));
            }
        });

        MenuItem textLineBreakBatch = new MenuItem(AppVaribles.getMessage("TextLineBreakBatch"));
        textLineBreakBatch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.TextLineBreakBatchFxml, AppVaribles.getMessage("TextLineBreakBatch"));
            }
        });

        MenuItem bytesEditer = new MenuItem(AppVaribles.getMessage("BytesEditer"));
        bytesEditer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.BytesEditerFxml, AppVaribles.getMessage("BytesEditer"));
            }
        });

        MenuItem fileCut = new MenuItem(AppVaribles.getMessage("FileCut"));
        fileCut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.FileCutFxml, AppVaribles.getMessage("FileCut"));
            }
        });

        MenuItem fileMerge = new MenuItem(AppVaribles.getMessage("FileMerge"));
        fileMerge.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.FileMergeFxml, AppVaribles.getMessage("FileMerge"));
            }
        });

        MenuItem recordImages = new MenuItem(AppVaribles.getMessage("RecordImagesInSystemClipBoard"));
        recordImages.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.RecordImagesInSystemClipboardFxml, AppVaribles.getMessage("RecordImagesInSystemClipBoard"));
            }
        });

        MenuItem alarmClock = new MenuItem(AppVaribles.getMessage("AlarmClock"));
        alarmClock.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.AlarmClockFxml, AppVaribles.getMessage("AlarmClock"));
            }
        });

        desktopMenu = new ContextMenu();
        desktopMenu.getItems().addAll(
                textEditer, textEncodingBatch, textLineBreakBatch, new SeparatorMenuItem(),
                bytesEditer, fileCut, fileMerge, new SeparatorMenuItem(),
                filesRename, filesArrangement, dirSynchronize, new SeparatorMenuItem(),
                recordImages, alarmClock);
    }

    private void initFileToolsMenu() {
        fileMenu = new ContextMenu();

    }

    private void initNetworkToolsMenu() {
        MenuItem htmlEditor = new MenuItem(AppVaribles.getMessage("HtmlEditor"));
        htmlEditor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                HtmlEditorController controller
                        = (HtmlEditorController) reloadStage(CommonValues.HtmlEditorFxml, AppVaribles.getMessage("HtmlEditor"));
//                controller.switchBroswerTab();
            }
        });

        MenuItem weiboSnap = new MenuItem(AppVaribles.getMessage("WeiboSnap"));
        weiboSnap.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WeiboSnapController controller
                        = (WeiboSnapController) reloadStage(CommonValues.WeiboSnapFxml, AppVaribles.getMessage("WeiboSnap"));
            }
        });
        networkMenu = new ContextMenu();
        networkMenu.getItems().addAll(weiboSnap, htmlEditor);

    }

    private void initOtherMenu() {
        MenuItem setEnglish = new MenuItem("English");
        setEnglish.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AppVaribles.setLanguage("en");
                reloadStage(myFxml, AppVaribles.getMessage("AppTitle"));
            }
        });
        MenuItem setChinese = new MenuItem("中文");
        setChinese.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AppVaribles.setLanguage("zh");
                reloadStage(myFxml, AppVaribles.getMessage("AppTitle"));
            }
        });
        languageMenu = new ContextMenu();
        languageMenu.getItems().addAll(setChinese, setEnglish);

    }

    @FXML
    void showPdfMenu(MouseEvent event) {
        if (pdfMenus.isShowing()) {
            return;
        }
        Bounds bounds = pdfBox.localToScreen(pdfBox.getBoundsInLocal());
        pdfMenus.show(pdfBox, bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2);
        imageMenu.hide();
        fileMenu.hide();
        desktopMenu.hide();
        languageMenu.hide();
        networkMenu.hide();
    }

    @FXML
    void showImageMenu(MouseEvent event) {
        if (imageMenu.isShowing()) {
            return;
        }
        Bounds bounds = imageBox.localToScreen(imageBox.getBoundsInLocal());
        imageMenu.show(imageBox, bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2);
        pdfMenus.hide();
        fileMenu.hide();
        desktopMenu.hide();
        languageMenu.hide();
        networkMenu.hide();

    }

    @FXML
    void showFileMenu(MouseEvent event) {
        if (fileMenu.isShowing()) {
            return;
        }
        Bounds bounds = fileBox.localToScreen(fileBox.getBoundsInLocal());
        fileMenu.show(fileBox, bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2);
        imageMenu.hide();
        pdfMenus.hide();
        desktopMenu.hide();
        languageMenu.hide();
        networkMenu.hide();
    }

    @FXML
    void showNetworkMenu(MouseEvent event) {
        if (networkMenu.isShowing()) {
            return;
        }
        Bounds bounds = networkBox.localToScreen(networkBox.getBoundsInLocal());
        networkMenu.show(networkBox, bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2);
        imageMenu.hide();
        fileMenu.hide();
        pdfMenus.hide();
        languageMenu.hide();
        desktopMenu.hide();
    }

    @FXML
    void showDesktopMenu(MouseEvent event) {
        if (desktopMenu.isShowing()) {
            return;
        }
        Bounds bounds = desktopBox.localToScreen(desktopBox.getBoundsInLocal());
        desktopMenu.show(desktopBox, bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2);
        imageMenu.hide();
        fileMenu.hide();
        pdfMenus.hide();
        languageMenu.hide();
        networkMenu.hide();
    }

    @FXML
    void showLanguageMenu(MouseEvent event) {
        if (languageMenu.isShowing()) {
            return;
        }
        Bounds bounds = languageBox.localToScreen(languageBox.getBoundsInLocal());
        languageMenu.show(languageBox, bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2);
        imageMenu.hide();
        fileMenu.hide();
        desktopMenu.hide();
        pdfMenus.hide();
        networkMenu.hide();
    }

    @FXML
    private void showAbout(MouseEvent event) {
        openStage(CommonValues.AboutFxml, false);
        imageMenu.hide();
        fileMenu.hide();
        desktopMenu.hide();
        languageMenu.hide();
        pdfMenus.hide();
        networkMenu.hide();
    }

}
