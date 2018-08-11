package mara.mybox.controller;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;
import static mara.mybox.controller.BaseController.logger;
import mara.mybox.image.ImageConverter;
import mara.mybox.image.ImageGrayTools;
import mara.mybox.imagefile.ImageFileReaders;
import mara.mybox.imagefile.ImageFileWriters;
import mara.mybox.objects.AppVaribles;
import static mara.mybox.objects.AppVaribles.getMessage;
import mara.mybox.objects.CommonValues;
import mara.mybox.objects.ImageAttributes;
import mara.mybox.objects.ImageFileInformation;
import mara.mybox.objects.ImageScope;
import mara.mybox.objects.ImageScope.AreaScopeType;
import mara.mybox.objects.ImageScope.OperationType;
import mara.mybox.tools.DateTools;
import mara.mybox.tools.FileTools;
import mara.mybox.tools.FxmlTools;
import static mara.mybox.tools.FxmlTools.badStyle;

/**
 * @Author Mara
 * @CreateDate 2018-6-20
 * @Description
 * @License Apache License Version 2.0
 */
public class ImageManufactureController extends ImageViewerController {

    protected File nextFile, lastFile, refFile;
    protected String ImageSortTypeKey, ImageOpenAfterSaveAsKey, ImageReferenceDisplayKey, ImageSaveConfirmKey, FontSizeKey, FontFamilyKey;
    protected ScrollPane refPane, cropPane;
    protected ImageView refView, cropImageView;
    protected Label refLabel;
    protected VBox refBox;
    protected Image refImage, undoImage, redoImage, cropImage, currentImage, scopeImage;
    protected ImageFileInformation refInfo, cropInfo;
    private boolean noRatio, isScale, isSettingValues, areaValid;
    private float scale = 1.0f, shearX, waterTransparent = 0.5f;
    private int width, height, colorOperationType, filtersOperationType, waterX, waterY, waterSize;
    private int pixelPickingType, colorValue, cropLeftX, cropLeftY, cropRightX, cropRightY, binaryThreshold;
    private ImageScope colorScope, filtersScope, replaceColorScope, cropScope;
    protected SimpleBooleanProperty imageChanged;

    @FXML
    protected ToolBar fileBar, navBar, refBar, hotBar, scaleBar, replaceColorBar, watermarkBar, transformBar;
    @FXML
    protected Tab fileTab, zoomTab, colorTab, filtersTab, watermarkTab, cropTab;
    @FXML
    protected Tab replaceColorTab, pixelsTab, refTab, browseTab, transformTab, edgesTab;
    @FXML
    protected Slider zoomSlider, angleSlider, colorSlider, binarySlider;
    @FXML
    protected Label zoomValue, colorUnit, tipsLabel, promptLabel, binaryValue, thresholdLabel;
    @FXML
    protected ToggleGroup sortGroup, pixelsGroup, filtersGroup, colorGroup;
    @FXML
    protected Button nextButton, lastButton, origImageButton, selectRefButton, pixelsOkButton, saveButton, leftButton, rightButton;
    @FXML
    protected Button pickNewColorButton, transparentForNewButton, recoverButton, replaceColorOkButton, replaceColorScopeButton;
    @FXML
    protected Button waterPositionButton, waterAddButton, undoButton, redoButton, cropOkButton, filtersScopeButton, shearButton;
    @FXML
    protected Button colorScopeButton, binaryCalculateButton, binaryOkButton, colorDecreaseButton, colorIncreaseButton;
    @FXML
    protected CheckBox openCheck, saveCheck, displayRefCheck, refSyncCheck, keepRatioCheck;
    @FXML
    protected CheckBox edgesTopCheck, edgesBottomCheck, edgesLeftCheck, edgesRightCheck;
    @FXML
    protected SplitPane splitPane;
    @FXML
    protected TextField widthInput, heightInput, scaleInput, colorInput;
    @FXML
    protected TextField waterInput, waterXInput, waterYInput;
    @FXML
    protected TextField cropLeftXInput, cropLeftYInput, cropRightXInput, cropRightYInput;
    @FXML
    protected ChoiceBox ratioBox, waterStyleBox, waterFamilyBox;
    @FXML
    private TabPane tabPane;
    @FXML
    protected ColorPicker newColorPicker, waterColorPicker, edgesColorPicker;
    @FXML
    protected ComboBox angleBox, waterSizeBox, waterTransparentBox, colorBox, shearBox;
    @FXML
    protected RadioButton opacityRadio;

    public static class ColorOperationType {

        public static int Brightness = 0;
        public static int Sauration = 1;
        public static int Hue = 2;
        public static int Opacity = 3;

    }

    public static class FiltersOperationType {

        public static int Gray = 0;
        public static int Invert = 1;
        public static int BlackOrWhite = 2;

    }

    public static class PixelPickingType {

        public static int None = 0;
        public static int NewColor = 1;
        public static int Watermark = 2;
        public static int RectangleLeft = 3;
        public static int RectangleRight = 4;
        public static int OriginalColor = 5;
        public static int CircleCenter = 6;

    }

    public ImageManufactureController() {
        ImageSortTypeKey = "ImageSortType";
        ImageOpenAfterSaveAsKey = "ImageOpenAfterSaveAs";
        ImageSaveConfirmKey = "ImageSaveConfirmKey";
        ImageReferenceDisplayKey = "ImageReferenceDisplay";
        FontSizeKey = "FontSizeKey";
        FontFamilyKey = "FontFamilyKey";
    }

    @Override
    protected void initializeNext2() {
        try {
            initCommon();
            initFileTab();
            initViewTab();
            initBrowseTab();
            initPixelsTab();
            initColorTab();
            initFiltersTab();
            initTransformTab();
            initReplaceColorTab();
            initWatermarkTab();
            initReferenceTab();
            initEdgesTab();
            initCropTab();

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    protected void initCommon() {
        try {
            attributes = new ImageAttributes();

            fileBar.setDisable(true);
            browseTab.setDisable(true);
            navBar.setDisable(true);
            zoomTab.setDisable(true);
            colorTab.setDisable(true);
            filtersTab.setDisable(true);
            replaceColorTab.setDisable(true);
            pixelsTab.setDisable(true);
            refTab.setDisable(true);
            transformTab.setDisable(true);
            watermarkTab.setDisable(true);
            edgesTab.setDisable(true);
            cropTab.setDisable(true);
            hotBar.setDisable(true);

            tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
                @Override
                public void changed(ObservableValue<? extends Tab> observable,
                        Tab oldValue, Tab newValue) {
                    imageView.setImage(currentImage);
                    pixelPickingType = ImageManufactureController.PixelPickingType.None;
                    imageView.setCursor(Cursor.OPEN_HAND);
                    promptLabel.setText("");
                    showScope();
                }
            });

            Tooltip tips = new Tooltip(getMessage("ImageManufactureTips"));
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(tipsLabel, tips);

            if (AppVaribles.showComments) {
                tips = new Tooltip(getMessage("ImageRefTips"));
                tips.setFont(new Font(16));
                FxmlTools.setComments(displayRefCheck, tips);
            }

            imageChanged = new SimpleBooleanProperty(false);
            imageChanged.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                    if (imageChanged.getValue()) {
                        saveButton.setDisable(false);
                        recoverButton.setDisable(false);
                        undoButton.setDisable(false);
                        redoButton.setDisable(true);
                        getMyStage().setTitle(getBaseTitle() + "  " + sourceFile.getAbsolutePath() + "*");
                    } else {
                        saveButton.setDisable(true);
                        recoverButton.setDisable(true);
                        getMyStage().setTitle(getBaseTitle() + "  " + sourceFile.getAbsolutePath());
                    }
                    setBottomLabel();
                }
            });

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    // File Methods
    protected void initFileTab() {
        try {
            openCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                    AppVaribles.setConfigValue(ImageOpenAfterSaveAsKey, openCheck.isSelected());
                }
            });
            openCheck.setSelected(AppVaribles.getConfigBoolean(ImageOpenAfterSaveAsKey));

            saveCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                    AppVaribles.setConfigValue(ImageSaveConfirmKey, saveCheck.isSelected());
                }
            });
            saveCheck.setSelected(AppVaribles.getConfigBoolean(ImageSaveConfirmKey));

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @FXML
    @Override
    protected void selectSourceFile(ActionEvent event) {
        if (image != null && imageChanged.getValue()) {
            if (!checkSavingBeforeExit()) {
                return;
            }
        }
        super.selectSourceFile(event);
    }

    @Override
    public void afterImageLoaded() {
        try {
            super.afterImageLoaded();
            if (image == null) {
                return;
            }
            fileBar.setDisable(false);
            navBar.setDisable(false);
            zoomTab.setDisable(false);
            colorTab.setDisable(false);
            filtersTab.setDisable(false);
            replaceColorTab.setDisable(false);
            pixelsTab.setDisable(false);
            refTab.setDisable(false);
            hotBar.setDisable(false);
            browseTab.setDisable(false);
            if (CommonValues.NoAlphaImages.contains(imageInformation.getImageFormat())) {
                opacityRadio.setDisable(true);
                transparentForNewButton.setDisable(true);
            } else {
                opacityRadio.setDisable(false);
                transparentForNewButton.setDisable(false);
            }
            transformTab.setDisable(false);
            watermarkTab.setDisable(false);
            edgesTab.setDisable(false);
            cropTab.setDisable(false);

            isSettingValues = true;

            currentImage = image;
            imageChanged.set(false);

            widthInput.setText(imageInformation.getxPixels() + "");
            heightInput.setText(imageInformation.getyPixels() + "");
            attributes.setSourceWidth(imageInformation.getxPixels());
            attributes.setSourceHeight(imageInformation.getyPixels());

            waterXInput.setText(imageInformation.getxPixels() / 2 + "");
            waterYInput.setText(imageInformation.getyPixels() / 2 + "");

            colorScope = new ImageScope();
            colorScope.setOperationType(OperationType.Color);

            filtersScope = new ImageScope();
            filtersScope.setOperationType(OperationType.Filters);

            replaceColorScope = new ImageScope();
            replaceColorScope.setOperationType(OperationType.ReplaceColor);
//            replaceColorScope.setAllColors(false);
//            replaceColorOkButton.setDisable(true);

            cropRightXInput.setText(imageInformation.getxPixels() * 3 / 4 + "");
            cropRightYInput.setText(imageInformation.getyPixels() * 3 / 4 + "");
            cropLeftXInput.setText(imageInformation.getxPixels() / 4 + "");
            cropLeftYInput.setText(imageInformation.getyPixels() / 4 + "");

            cropScope = new ImageScope();
            cropScope.setOperationType(OperationType.Crop);
            cropScope.setAreaScopeType(AreaScopeType.Rectangle);
            cropScope.setIndicateScope(true);

            pixelPickingType = PixelPickingType.None;
            imageView.setCursor(Cursor.OPEN_HAND);
            promptLabel.setText("");

            isSettingValues = false;

            checkNevigator();
            straighten();
            setBottomLabel();

            undoButton.setDisable(true);
            redoButton.setDisable(true);
            getMyStage().setTitle(getBaseTitle() + "  " + sourceFile.getAbsolutePath());
        } catch (Exception e) {
            logger.debug(e.toString());
        }

    }

    // View Methods
    protected void initViewTab() {
        try {

            zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    zoomStep = newValue.intValue();
                    zoomValue.setText(zoomStep + "%");
                }
            });

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @FXML
    @Override
    public void moveRight() {
        FxmlTools.setScrollPane(scrollPane, -40, scrollPane.getVvalue());
        if (refSyncCheck.isSelected() && displayRefCheck.isSelected()) {
            FxmlTools.setScrollPane(refPane, -40, refPane.getVvalue());
        }
    }

    @FXML
    @Override
    public void moveLeft() {
        FxmlTools.setScrollPane(scrollPane, 40, scrollPane.getVvalue());
        if (refSyncCheck.isSelected() && displayRefCheck.isSelected()) {
            FxmlTools.setScrollPane(refPane, 40, refPane.getVvalue());
        }
    }

    @FXML
    @Override
    public void moveUp() {
        FxmlTools.setScrollPane(scrollPane, scrollPane.getHvalue(), 40);
        if (refSyncCheck.isSelected() && displayRefCheck.isSelected()) {
            FxmlTools.setScrollPane(refPane, refPane.getHvalue(), 40);
        }
    }

    @FXML
    @Override
    public void moveDown() {
        FxmlTools.setScrollPane(scrollPane, scrollPane.getHvalue(), -40);
        if (refSyncCheck.isSelected() && displayRefCheck.isSelected()) {
            FxmlTools.setScrollPane(refPane, refPane.getHvalue(), -40);
        }
    }

    // Browse Methods
    protected void initBrowseTab() {
        try {
            sortGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> ov,
                        Toggle old_toggle, Toggle new_toggle) {
                    checkNevigator();
                    RadioButton selected = (RadioButton) sortGroup.getSelectedToggle();
                    AppVaribles.setConfigValue(ImageSortTypeKey, selected.getText());
                }
            });
            FxmlTools.setRadioSelected(sortGroup, AppVaribles.getConfigValue(ImageSortTypeKey, getMessage("FileName")));

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @FXML
    public void next() {
        if (!checkSavingBeforeExit()) {
            return;
        }
        if (nextFile != null) {
            loadImage(nextFile.getAbsoluteFile(), false);
        }
    }

    @FXML
    public void last() {
        if (!checkSavingBeforeExit()) {
            return;
        }
        if (lastFile != null) {
            loadImage(lastFile.getAbsoluteFile(), false);
        }
    }

    @FXML
    public void browseAction() {
        try {
            if (!stageReloading()) {
                return;
            }
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(CommonValues.ImagesViewerFxml), AppVaribles.CurrentBundle);
            Pane pane = fxmlLoader.load();
            final ImagesViewerController controller = fxmlLoader.getController();
            controller.setMyStage(myStage);
            myStage.setScene(new Scene(pane));
            myStage.setTitle(getMessage("MultipleImagesViewer"));
            controller.loadImages(sourceFile.getParentFile(), 16);
        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    private void checkNevigator() {
        try {
            if (sourceFile == null) {
                lastFile = null;
                lastButton.setDisable(true);
                nextFile = null;
                nextButton.setDisable(true);
                return;
            }
            File path = sourceFile.getParentFile();
            List<File> sortedFiles = new ArrayList<>();
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isFile() && FileTools.isSupportedImage(file)) {
                    sortedFiles.add(file);
                }
            }
            RadioButton sort = (RadioButton) sortGroup.getSelectedToggle();
            if (getMessage("OriginalFileName").equals(sort.getText())) {
                FileTools.sortFiles(sortedFiles, FileTools.FileSortType.FileName);

            } else if (getMessage("CreateTime").equals(sort.getText())) {
                FileTools.sortFiles(sortedFiles, FileTools.FileSortType.CreateTime);

            } else if (getMessage("ModifyTime").equals(sort.getText())) {
                FileTools.sortFiles(sortedFiles, FileTools.FileSortType.ModifyTime);

            } else if (getMessage("Size").equals(sort.getText())) {
                FileTools.sortFiles(sortedFiles, FileTools.FileSortType.Size);
            }

            for (int i = 0; i < sortedFiles.size(); i++) {
                if (sortedFiles.get(i).getAbsoluteFile().equals(sourceFile.getAbsoluteFile())) {
                    if (i < sortedFiles.size() - 1) {
                        nextFile = sortedFiles.get(i + 1);
                        nextButton.setDisable(false);
                    } else {
                        nextFile = null;
                        nextButton.setDisable(true);
                    }
                    if (i > 0) {
                        lastFile = sortedFiles.get(i - 1);
                        lastButton.setDisable(false);
                    } else {
                        lastFile = null;
                        lastButton.setDisable(true);
                    }
                    return;
                }
            }
            lastFile = null;
            lastButton.setDisable(true);
            nextFile = null;
            nextButton.setDisable(true);
        } catch (Exception e) {
            logger.debug(e.toString());
        }
    }

    // Pixels methods
    protected void initPixelsTab() {
        try {

            widthInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkPixelsWidth();
                }
            });
            checkPixelsWidth();

            heightInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkPixelsHeight();
                }
            });
            checkPixelsHeight();

            keepRatioCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                    if (keepRatioCheck.isSelected()) {
                        checkRatio();
                    }
                }
            });

            pixelsOkButton.disableProperty().bind(
                    widthInput.styleProperty().isEqualTo(badStyle)
                            .or(heightInput.styleProperty().isEqualTo(badStyle))
                            .or(scaleInput.styleProperty().isEqualTo(badStyle))
            );

            pixelsGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> ov,
                        Toggle old_toggle, Toggle new_toggle) {
                    RadioButton selected = (RadioButton) pixelsGroup.getSelectedToggle();
                    if (getMessage("Pixels").equals(selected.getText())) {
                        scaleBar.setDisable(false);
                        isScale = false;
                        scaleInput.setStyle(null);
                    } else {
                        scaleBar.setDisable(true);
                        isScale = true;
                        widthInput.setStyle(null);
                        heightInput.setStyle(null);
                    }
                }
            });

            ratioBox.getItems().addAll(FXCollections.observableArrayList(getMessage("BaseOnLarger"),
                    getMessage("BaseOnWidth"), getMessage("BaseOnHeight"), getMessage("BaseOnSmaller")));
            ratioBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (newValue != null && !newValue.isEmpty()) {
                        checkRatioAdjustion(newValue);
                    }
                }
            });
            ratioBox.getSelectionModel().select(0);

            scaleInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkPixelsScale();
                }
            });
            checkPixelsScale();

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void checkPixelsWidth() {
        try {
            width = Integer.valueOf(widthInput.getText());
            if (width > 0) {
                widthInput.setStyle(null);
                checkRatio();
            } else {
                widthInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            widthInput.setStyle(badStyle);
        }
    }

    private void checkPixelsHeight() {
        try {
            height = Integer.valueOf(heightInput.getText());
            if (height > 0) {
                heightInput.setStyle(null);
                checkRatio();
            } else {
                heightInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            heightInput.setStyle(badStyle);
        }
    }

    private void checkPixelsScale() {
        try {
            scale = Float.valueOf(scaleInput.getText());
            if (scale > 0) {
                scaleInput.setStyle(null);
                if (currentImage != null) {
                    noRatio = true;
                    widthInput.setText(Math.round(currentImage.getWidth() * scale) + "");
                    heightInput.setText(Math.round(currentImage.getHeight() * scale) + "");
                    noRatio = false;
                }
            } else {
                scaleInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            scaleInput.setStyle(badStyle);
        }
    }

    protected void checkRatioAdjustion(String s) {
        try {
            if (getMessage("BaseOnWidth").equals(s)) {
                attributes.setRatioAdjustion(ImageConverter.KeepRatioType.BaseOnWidth);
            } else if (getMessage("BaseOnHeight").equals(s)) {
                attributes.setRatioAdjustion(ImageConverter.KeepRatioType.BaseOnHeight);
            } else if (getMessage("BaseOnLarger").equals(s)) {
                attributes.setRatioAdjustion(ImageConverter.KeepRatioType.BaseOnLarger);
            } else if (getMessage("BaseOnSmaller").equals(s)) {
                attributes.setRatioAdjustion(ImageConverter.KeepRatioType.BaseOnSmaller);
            } else {
                attributes.setRatioAdjustion(ImageConverter.KeepRatioType.None);
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    protected void checkRatio() {
        try {
            width = Integer.valueOf(widthInput.getText());
            height = Integer.valueOf(heightInput.getText());
            attributes.setTargetWidth(width);
            attributes.setTargetHeight(height);
            int sourceX = imageInformation.getxPixels();
            int sourceY = imageInformation.getyPixels();
            if (noRatio || !keepRatioCheck.isSelected() || sourceX <= 0 || sourceY <= 0) {
                return;
            }
            long ratioX = Math.round(width * 1000 / sourceX);
            long ratioY = Math.round(height * 1000 / sourceY);
            if (ratioX == ratioY) {
                return;
            }
            switch (attributes.getRatioAdjustion()) {
                case ImageConverter.KeepRatioType.BaseOnWidth:
                    heightInput.setText(Math.round(width * sourceY / sourceX) + "");
                    break;
                case ImageConverter.KeepRatioType.BaseOnHeight:
                    widthInput.setText(Math.round(height * sourceX / sourceY) + "");
                    break;
                case ImageConverter.KeepRatioType.BaseOnLarger:
                    if (ratioX > ratioY) {
                        heightInput.setText(Math.round(width * sourceY / sourceX) + "");
                    } else {
                        widthInput.setText(Math.round(height * sourceX / sourceY) + "");
                    }
                    break;
                case ImageConverter.KeepRatioType.BaseOnSmaller:
                    if (ratioX > ratioY) {
                        widthInput.setText(Math.round(height * sourceX / sourceY) + "");
                    } else {
                        heightInput.setText(Math.round(width * sourceY / sourceX) + "");
                    }
                    break;
                default:
                    break;
            }
            width = Integer.valueOf(widthInput.getText());
            height = Integer.valueOf(heightInput.getText());
        } catch (Exception e) {
//            logger.error(e.toString());
        }
    }

    @FXML
    public void pixelsCalculator() {
        try {
            attributes.setKeepRatio(keepRatioCheck.isSelected());
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(CommonValues.PixelsCalculatorFxml), AppVaribles.CurrentBundle);
            Pane pane = fxmlLoader.load();
            PixelsCalculationController controller = fxmlLoader.getController();
            Stage stage = new Stage();
            controller.setMyStage(stage);
            controller.setSource(attributes, widthInput, heightInput);

            Scene scene = new Scene(pane);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(getMyStage());
            stage.setTitle(AppVaribles.getMessage("PixelsCalculator"));
            stage.getIcons().add(CommonValues.AppIcon);
            stage.setScene(scene);
            stage.show();
            noRatio = true;
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    noRatio = false;
                }
            });

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @FXML
    protected void pixelsAction() {
        if (isScale) {
            setScale();
        } else {
            setPixels();
        }
    }

    protected void setScale() {
        if (scale == 1.0 || scale <= 0) {
            return;
        }
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.scaleImage(currentImage, imageInformation.getImageFormat(), scale);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    protected void setPixels() {
        if (width <= 0 || height <= 0) {
            return;
        }
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.scaleImage(currentImage, imageInformation.getImageFormat(), width, height);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // Color Methods
    protected void initColorTab() {
        try {
            colorGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> ov,
                        Toggle old_toggle, Toggle new_toggle) {
                    checkColorOperationType();
                }
            });
            checkColorOperationType();

            colorSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    colorValue = newValue.intValue();
                    colorInput.setText(colorValue + "");
                }
            });

            colorInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkColorInput();
                }
            });
            checkColorInput();

            if (AppVaribles.showComments) {
                Tooltip stips = new Tooltip(getMessage("ScopeComments"));
                stips.setFont(new Font(16));
                FxmlTools.setComments(colorScopeButton, stips);
            }

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void checkColorOperationType() {
        RadioButton selected = (RadioButton) colorGroup.getSelectedToggle();
        if (getMessage("Brightness").equals(selected.getText())) {
            colorOperationType = ColorOperationType.Brightness;
            colorSlider.setMax(100);
            colorSlider.setMin(0);
            colorSlider.setBlockIncrement(1);
            colorUnit.setText("%");
            colorInput.setText("5");
            colorDecreaseButton.setVisible(true);
            colorIncreaseButton.setText(getMessage("Increase"));
        } else if (getMessage("Saturation").equals(selected.getText())) {
            colorOperationType = ColorOperationType.Sauration;
            colorSlider.setMax(100);
            colorSlider.setMin(0);
            colorSlider.setBlockIncrement(1);
            colorUnit.setText("%");
            colorInput.setText("5");
            colorDecreaseButton.setVisible(true);
            colorIncreaseButton.setText(getMessage("Increase"));
        } else if (getMessage("Hue").equals(selected.getText())) {
            colorOperationType = ColorOperationType.Hue;
            colorSlider.setMax(360);
            colorSlider.setMin(0);
            colorSlider.setBlockIncrement(1);
            colorUnit.setText(getMessage("Degree"));
            colorInput.setText("5");
            colorDecreaseButton.setVisible(true);
            colorIncreaseButton.setText(getMessage("Increase"));
        } else if (getMessage("Opacity").equals(selected.getText())) {
            colorOperationType = ColorOperationType.Opacity;
            colorSlider.setMax(100);
            colorSlider.setMin(0);
            colorSlider.setBlockIncrement(1);
            colorUnit.setText("%");
            colorInput.setText("50");
            colorDecreaseButton.setVisible(false);
            colorIncreaseButton.setText(getMessage("OK"));
        }
    }

    private void checkColorInput() {
        try {
            colorValue = Integer.valueOf(colorInput.getText());
            if (colorValue >= 0 && colorValue <= colorSlider.getMax()) {
                colorInput.setStyle(null);
                colorSlider.setValue(colorValue);
            } else {
                colorInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            colorInput.setStyle(badStyle);
        }
    }

    @FXML
    public void increaseColor() {
        if (colorOperationType == ColorOperationType.Brightness) {
            increaseBrightness();
        } else if (colorOperationType == ColorOperationType.Sauration) {
            increaseSaturate();
        } else if (colorOperationType == ColorOperationType.Hue) {
            increaseHue();
        } else if (colorOperationType == ColorOperationType.Opacity) {
            setOpacity();
        }
    }

    @FXML
    public void decreaseColor() {
        if (colorOperationType == ColorOperationType.Brightness) {
            decreaseBrightness();
        } else if (colorOperationType == ColorOperationType.Sauration) {
            decreaseSaturate();
        } else if (colorOperationType == ColorOperationType.Hue) {
            decreaseHue();
        }
    }

    @FXML
    public void setColorScope() {
        setScope(colorScope);
    }

    @FXML
    public void clearColorScope() {
        colorScope = new ImageScope();
        colorScope.setOperationType(OperationType.Color);
        refImage = currentImage;
        refInfo = imageInformation;
        displayRefCheck.setSelected(false);
    }

    public void increaseHue() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.changeHue(currentImage, colorValue, colorScope);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void decreaseHue() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.changeHue(currentImage, 0 - colorValue, colorScope);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void increaseSaturate() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.changeSaturate(currentImage, colorValue / 100.0f, colorScope);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

    public void decreaseSaturate() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.changeSaturate(currentImage, 0.0f - colorValue / 100.0f, colorScope);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void increaseBrightness() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.changeBrightness(currentImage, colorValue / 100.0f, colorScope);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void decreaseBrightness() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.changeBrightness(currentImage, 0.0f - colorValue / 100.0f, colorScope);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void setOpacity() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.setOpacity(currentImage, colorValue, colorScope);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // Filters Methods
    protected void initFiltersTab() {
        try {
            filtersGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> ov,
                        Toggle old_toggle, Toggle new_toggle) {
                    checkFiltersOperationType();
                }
            });
            checkFiltersOperationType();

            binarySlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    binaryThreshold = newValue.intValue();
                    binaryValue.setText(binaryThreshold + "%");
                }
            });
            binarySlider.setValue(50);

            if (AppVaribles.showComments) {
                Tooltip stips = new Tooltip(getMessage("ScopeComments"));
                stips.setFont(new Font(16));
                FxmlTools.setComments(filtersScopeButton, stips);
            }

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void checkFiltersOperationType() {
        RadioButton selected = (RadioButton) filtersGroup.getSelectedToggle();
        if (getMessage("BlackOrWhite").equals(selected.getText())) {
            filtersOperationType = FiltersOperationType.BlackOrWhite;
            binarySlider.setDisable(false);
            binaryValue.setDisable(false);
            thresholdLabel.setDisable(false);
            binaryCalculateButton.setDisable(false);
        } else {
            if (getMessage("Gray").equals(selected.getText())) {
                filtersOperationType = FiltersOperationType.Gray;
            } else if (getMessage("Invert").equals(selected.getText())) {
                filtersOperationType = FiltersOperationType.Invert;
            }
            binarySlider.setDisable(true);
            binaryValue.setDisable(true);
            thresholdLabel.setDisable(true);
            binaryCalculateButton.setDisable(true);
        }
    }

    @FXML
    public void setFiltersScope() {
        setScope(filtersScope);
    }

    @FXML
    public void clearFiltersScope() {
        filtersScope = new ImageScope();
        filtersScope.setOperationType(OperationType.Filters);
        refImage = currentImage;
        refInfo = imageInformation;
        displayRefCheck.setSelected(false);
    }

    @FXML
    public void calculateThreshold() {
        int threshold = ImageGrayTools.calculateThreshold(sourceFile);
        binaryThreshold = threshold * 100 / 256;
        binarySlider.setValue(binaryThreshold);
    }

    @FXML
    public void filtersAction() {
        if (filtersOperationType == FiltersOperationType.Gray) {
            setGray();
        } else if (filtersOperationType == FiltersOperationType.Invert) {
            setInvert();
        } else if (filtersOperationType == FiltersOperationType.BlackOrWhite) {
            setBinary();
        }
    }

    public void setInvert() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.makeInvert(currentImage, filtersScope);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void setGray() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.makeGray(currentImage, filtersScope);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void setBinary() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.makeBinary(currentImage, binaryThreshold, filtersScope);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // Transform Methods
    protected void initTransformTab() {
        try {

            if (AppVaribles.showComments) {
                Tooltip tips = new Tooltip(getMessage("transformComments"));
                tips.setFont(new Font(16));
                FxmlTools.setComments(transformBar, tips);
            }

            ObservableList<String> shears = FXCollections.observableArrayList(
                    "0.5", "-0.5", "0.4", "-0.4", "0.2", "-0.2", "0.1", "-0.1",
                    "0.7", "-0.7", "0.9", "-0.9", "0.8", "-0.8", "1", "-1",
                    "1.5", "-1.5", "2", "-2");
            shearBox.getItems().addAll(shears);
            shearBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    try {
                        shearX = Float.valueOf(newValue);
                        shearBox.getEditor().setStyle(null);
                        shearButton.setDisable(false);
                    } catch (Exception e) {
                        shearX = 0;
                        shearBox.getEditor().setStyle(badStyle);
                        shearButton.setDisable(true);
                    }
                }
            });
            shearBox.getSelectionModel().select(0);

            angleSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    rotateAngle = newValue.intValue();
                    angleBox.getEditor().setText(rotateAngle + "");
                    leftButton.setDisable(false);
                    rightButton.setDisable(false);
                }
            });

            ObservableList<String> angles = FXCollections.observableArrayList(
                    "90", "180", "45", "30", "60", "15", "75", "120", "135");
            angleBox.getItems().addAll(angles);
            angleBox.setVisibleRowCount(angles.size());
            angleBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    try {
                        rotateAngle = Integer.valueOf(newValue);
                        angleSlider.setValue(rotateAngle);
                        angleBox.getEditor().setStyle(null);
                        leftButton.setDisable(false);
                        rightButton.setDisable(false);
                    } catch (Exception e) {
                        rotateAngle = 0;
                        angleBox.getEditor().setStyle(badStyle);
                        leftButton.setDisable(true);
                        rightButton.setDisable(true);
                    }
                }
            });
            angleBox.getSelectionModel().select(0);

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @FXML
    public void rightRotate() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.rotateImage(currentImage, rotateAngle);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void leftRotate() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.rotateImage(currentImage, 360 - rotateAngle);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void horizontalAction() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.horizontalImage(currentImage);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void verticalAction() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.verticalImage(currentImage);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void shearAction() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.shearImage(currentImage, shearX, 0);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    //  Replace Colors Methods
    protected void initReplaceColorTab() {
        try {

            if (AppVaribles.showComments) {
                Tooltip stips = new Tooltip(getMessage("ScopeComments"));
                stips.setFont(new Font(16));
                FxmlTools.setComments(replaceColorScopeButton, stips);
            }

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @FXML
    public void setReplaceColorScope() {
        setScope(replaceColorScope);

    }

    @FXML
    public void clearReplaceColorScope() {
        replaceColorScope = new ImageScope();
        replaceColorScope.setOperationType(OperationType.ReplaceColor);
        refImage = currentImage;
        refInfo = imageInformation;
        displayRefCheck.setSelected(false);
    }

    @FXML
    public void pickColorForNew() {
        pixelPickingType = PixelPickingType.NewColor;
        imageView.setCursor(Cursor.HAND);
        promptLabel.setText(getMessage("PickColorComments"));
    }

    @FXML
    public void transparentForNew() {
        newColorPicker.setValue(new Color(0, 0, 0, 0));
    }

    @FXML
    public void replaceColorAction() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlTools.replaceColors(currentImage, newColorPicker.getValue(), replaceColorScope);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // Watermark Methods
    protected void initWatermarkTab() {
        try {

            if (AppVaribles.showComments) {
                Tooltip tips = new Tooltip(getMessage("watermarkComments"));
                tips.setFont(new Font(16));
                FxmlTools.setComments(watermarkBar, tips);
            }

            waterXInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkWaterX();
                }
            });
            checkWaterX();

            waterYInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkWaterY();
                }
            });
            checkWaterY();

            ObservableList<String> sizes = FXCollections.observableArrayList(
                    "72", "18", "15", "9", "10", "12", "14", "17", "24", "36", "48", "64", "96");
            waterSizeBox.getItems().addAll(sizes);
            waterSizeBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    try {
                        waterSize = Integer.valueOf(newValue);
                        waterSizeBox.getEditor().setStyle(null);
                    } catch (Exception e) {
                        waterSize = 15;
                        waterSizeBox.getEditor().setStyle(badStyle);
                    }
                }
            });
            waterSizeBox.getSelectionModel().select(0);

            ObservableList<String> transparents = FXCollections.observableArrayList(
                    "0.5", "1.0", "0.3", "0.1", "0.8");
            waterTransparentBox.getItems().addAll(transparents);
            waterTransparentBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    try {
                        waterTransparent = Float.valueOf(newValue);
                        if (waterTransparent >= 0.0f && waterTransparent <= 1.0f) {
                            waterSizeBox.getEditor().setStyle(null);
                        } else {
                            waterTransparent = 0.5f;
                            waterSizeBox.getEditor().setStyle(badStyle);
                        }
                    } catch (Exception e) {
                        waterTransparent = 0.5f;
                        waterSizeBox.getEditor().setStyle(badStyle);
                    }
                }
            });
            waterTransparentBox.getSelectionModel().select(0);

            ObservableList<String> styles = FXCollections.observableArrayList(
                    getMessage("Regular"), getMessage("Bold"), getMessage("Italic"), getMessage("Bold Italic"));
            waterStyleBox.getItems().addAll(styles);
            waterStyleBox.getSelectionModel().select(0);

            GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontNames = e.getAvailableFontFamilyNames();
            waterFamilyBox.getItems().addAll(Arrays.asList(fontNames));
            waterFamilyBox.getSelectionModel().select(AppVaribles.getConfigValue(FontFamilyKey, fontNames[0]));
            waterFamilyBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    AppVaribles.setConfigValue(FontFamilyKey, newValue);
                }
            });

            waterAddButton.disableProperty().bind(
                    waterXInput.styleProperty().isEqualTo(badStyle)
                            .or(waterYInput.styleProperty().isEqualTo(badStyle))
                            .or(waterSizeBox.getEditor().styleProperty().isEqualTo(badStyle))
                            .or(waterTransparentBox.getEditor().styleProperty().isEqualTo(badStyle))
                            .or(Bindings.isEmpty(waterInput.textProperty()))
            );

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void checkWaterX() {
        try {
            waterX = Integer.valueOf(waterXInput.getText());
            waterXInput.setStyle(null);
            if (waterX >= 0 && waterX <= currentImage.getWidth()) {
                waterXInput.setStyle(null);
            } else {
                waterXInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            waterXInput.setStyle(badStyle);
        }
    }

    private void checkWaterY() {
        try {
            waterY = Integer.valueOf(waterYInput.getText());
            waterYInput.setStyle(null);
            if (waterY >= 0 && waterY <= currentImage.getHeight()) {
                waterYInput.setStyle(null);
            } else {
                waterYInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            waterYInput.setStyle(badStyle);
        }
    }

    @FXML
    public void waterPositionAction() {
        pixelPickingType = PixelPickingType.Watermark;
        imageView.setCursor(Cursor.HAND);
        promptLabel.setText(getMessage("PickPositionComments"));
    }

    @FXML
    public void waterAddAction() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String fontFamily = (String) waterFamilyBox.getSelectionModel().getSelectedItem();
                java.awt.Font font;
                String fontStyle = (String) waterStyleBox.getSelectionModel().getSelectedItem();
                if (AppVaribles.getMessage("Bold").equals(fontStyle)) {
                    font = new java.awt.Font(fontFamily, java.awt.Font.BOLD, waterSize);
                } else if (AppVaribles.getMessage("Italic").equals(fontStyle)) {
                    font = new java.awt.Font(fontFamily, java.awt.Font.ITALIC, waterSize);
                } else if (AppVaribles.getMessage("Bold Italic").equals(fontStyle)) {
                    font = new java.awt.Font(fontFamily, java.awt.Font.BOLD + java.awt.Font.ITALIC, waterSize);
                } else {
                    font = new java.awt.Font(fontFamily, java.awt.Font.PLAIN, waterSize);
                }
                final Image newImage = FxmlTools.addWatermark(currentImage, waterInput.getText(),
                        font, waterColorPicker.getValue(), waterX, waterY, waterTransparent);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        undoImage = currentImage;
                        currentImage = newImage;
                        imageView.setImage(newImage);
                        imageChanged.set(true);
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // Reference Methods
    protected void initReferenceTab() {
        try {
            displayRefCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                    checkReferenceImage();
                }
            });

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void checkReferenceImage() {
        try {
            if (displayRefCheck.isSelected()) {
                if (splitPane.getItems().size() == 1) {
                    if (refPane == null) {
                        refPane = new ScrollPane();
                        refPane.setPannable(true);
                        refPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        VBox.setVgrow(refPane, Priority.ALWAYS);
                        HBox.setHgrow(refPane, Priority.ALWAYS);
                    }
                    if (refView == null) {
                        refView = new ImageView();
                        refView.setPreserveRatio(true);
                        refView.setOnMouseEntered(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                if (refInfo == null) {
                                    return;
                                }
                                String str = AppVaribles.getMessage("Format") + ":" + refInfo.getImageFormat() + "  "
                                        + AppVaribles.getMessage("Pixels") + ":" + refInfo.getxPixels() + "x" + refInfo.getyPixels();
                                if (refInfo.getFile() != null) {
                                    str += "  " + AppVaribles.getMessage("Size") + ":" + FileTools.showFileSize(refInfo.getFile().length()) + "  "
                                            + AppVaribles.getMessage("ModifyTime") + ":" + DateTools.datetimeToString(refInfo.getFile().lastModified());
                                }
                                bottomLabel.setText(str);
                            }
                        });
                    }
                    refPane.setContent(refView);

                    if (refInfo != null && refInfo.isIsScope()) {
                        refBox = new VBox();
                        VBox.setVgrow(refBox, Priority.ALWAYS);
                        HBox.setHgrow(refBox, Priority.ALWAYS);
                        refLabel = new Label();
                        refLabel.setText(getMessage("Scope") + getMessage("opacityComments"));
                        refLabel.setAlignment(Pos.CENTER);
                        VBox.setVgrow(refLabel, Priority.NEVER);
                        HBox.setHgrow(refLabel, Priority.ALWAYS);
                        refBox.getChildren().add(0, refLabel);
                        refBox.getChildren().add(1, refPane);
                        splitPane.getItems().add(0, refBox);
                        logger.debug(refLabel.getText());
                    } else {
                        splitPane.getItems().add(0, refPane);
                    }
                    splitPane.setDividerPositions(0.5);
                    refBar.setDisable(false);

                    if (refFile == null) {
                        refFile = sourceFile;
                    }
                    if (refImage == null) {
                        loadReferenceImage();
                    } else {
                        refView.setImage(refImage);
                        if (refInfo != null) {
                            logger.debug(scrollPane.getHeight() + " " + refInfo.getyPixels());
                            if (scrollPane.getHeight() < refInfo.getyPixels()) {
                                refView.setFitHeight(scrollPane.getHeight() - 5); // use attributes of scrollPane but not refPane
                                refView.setFitWidth(scrollPane.getWidth() - 1);
                            } else {
                                refView.setFitHeight(refInfo.getyPixels());
                                refView.setFitWidth(refInfo.getxPixels());
                            }
                        }
                    }

                }
            } else {
                if (splitPane.getItems().size() == 2) {
                    splitPane.getItems().remove(0);
                    refBar.setDisable(true);
                }
            }

        } catch (Exception e) {
//            logger.error(e.toString());
        }
    }

    private void loadReferenceImage() {
        if (refFile == null || sourceFile == null) {
            return;
        }
        if (refFile.getAbsolutePath().equals(sourceFile.getAbsolutePath())) {
            refImage = image;
            refInfo = imageInformation;
            refView.setImage(refImage);
            if (scrollPane.getHeight() < refInfo.getyPixels()) {
                refView.setFitHeight(scrollPane.getHeight() - 5); // use attributes of scrollPane but not refPane
                refView.setFitWidth(scrollPane.getWidth() - 1);
            } else {
                refView.setFitHeight(refInfo.getyPixels());
                refView.setFitWidth(refInfo.getxPixels());
            }
            return;
        }
        Task refTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                refInfo = ImageFileReaders.readImageMetaData(refFile.getAbsolutePath());
                refImage = SwingFXUtils.toFXImage(ImageIO.read(refFile), null);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        refView.setImage(refImage);
                        if (refPane.getHeight() < refInfo.getyPixels()) {
                            refView.setFitHeight(refPane.getHeight() - 5);
                            refView.setFitWidth(refPane.getWidth() - 1);
                        } else {
                            refView.setFitHeight(refInfo.getyPixels());
                            refView.setFitWidth(refInfo.getxPixels());
                        }
                    }
                });
                return null;
            }
        };
        openHandlingStage(refTask, Modality.WINDOW_MODAL);
        Thread thread = new Thread(refTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void selectReference() {
        try {
            final FileChooser fileChooser = new FileChooser();
            File path = new File(AppVaribles.getConfigValue(sourcePathKey, System.getProperty("user.home")));
            if (!path.isDirectory()) {
                path = new File(System.getProperty("user.home"));
            }
            fileChooser.setInitialDirectory(path);
            fileChooser.getExtensionFilters().addAll(fileExtensionFilter);
            File file = fileChooser.showOpenDialog(getMyStage());
            if (file == null) {
                return;
            }
            refFile = file;
            AppVaribles.setConfigValue("LastPath", sourceFile.getParent());
            AppVaribles.setConfigValue(sourcePathKey, sourceFile.getParent());

            loadReferenceImage();

        } catch (Exception e) {
//            logger.error(e.toString());
        }
    }

    @FXML
    public void originalImage() {
        refFile = sourceFile;
        loadReferenceImage();
    }

    @FXML
    public void popRefInformation() {
        showImageInformation(refInfo);
    }

    @FXML
    public void popRefMeta() {
        showImageMetaData(refInfo);
    }

    // Cut Edges Methods
    protected void initEdgesTab() {
        try {
            displayRefCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                    checkReferenceImage();
                }
            });

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @FXML
    public void edgesTransparentAction() {
        edgesColorPicker.setValue(Color.TRANSPARENT);
    }

    @FXML
    public void edgesBlackAction() {
        edgesColorPicker.setValue(Color.BLACK);
    }

    @FXML
    public void edgesWhiteAction() {
        edgesColorPicker.setValue(Color.WHITE);
    }

    @FXML
    public void cutEdgesAction() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    final Image newImage = FxmlTools.cutEdges(currentImage, waterColorPicker.getValue(),
                            edgesTopCheck.isSelected(), edgesBottomCheck.isSelected(),
                            edgesLeftCheck.isSelected(), edgesRightCheck.isSelected());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            undoImage = currentImage;
                            currentImage = newImage;
                            imageView.setImage(newImage);
                            imageChanged.set(true);
                        }
                    });
                } catch (Exception e) {
                    logger.debug(e.toString());
                }
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // Crop Methods
    protected void initCropTab() {
        try {

            cropLeftXInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkCropValues();
                }
            });
            cropLeftYInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkCropValues();
                }
            });
            cropRightXInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkCropValues();
                }
            });
            cropRightYInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkCropValues();
                }
            });

            cropOkButton.disableProperty().bind(
                    cropLeftXInput.styleProperty().isEqualTo(badStyle)
                            .or(cropLeftYInput.styleProperty().isEqualTo(badStyle))
                            .or(cropRightXInput.styleProperty().isEqualTo(badStyle))
                            .or(cropRightYInput.styleProperty().isEqualTo(badStyle))
                            .or(Bindings.isEmpty(cropLeftXInput.textProperty()))
                            .or(Bindings.isEmpty(cropLeftYInput.textProperty()))
                            .or(Bindings.isEmpty(cropRightXInput.textProperty()))
                            .or(Bindings.isEmpty(cropRightYInput.textProperty()))
            );

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void checkCropValues() {
        areaValid = true;
        try {
            cropLeftX = Integer.valueOf(cropLeftXInput.getText());
            cropLeftXInput.setStyle(null);
            if (cropLeftX >= 0 && cropLeftX <= currentImage.getWidth()) {
                cropLeftXInput.setStyle(null);
            } else {
                cropLeftXInput.setStyle(badStyle);
                areaValid = false;
            }
        } catch (Exception e) {
            cropLeftXInput.setStyle(badStyle);
            areaValid = false;
        }

        try {
            cropLeftY = Integer.valueOf(cropLeftYInput.getText());
            cropLeftYInput.setStyle(null);
            if (cropLeftY >= 0 && cropLeftY <= currentImage.getHeight()) {
                cropLeftYInput.setStyle(null);
            } else {
                cropLeftYInput.setStyle(badStyle);
                areaValid = false;
            }
        } catch (Exception e) {
            cropLeftYInput.setStyle(badStyle);
            areaValid = false;
        }

        try {
            cropRightX = Integer.valueOf(cropRightXInput.getText());
            cropRightXInput.setStyle(null);
            if (cropRightX >= 0 && cropRightX <= currentImage.getWidth()) {
                cropRightXInput.setStyle(null);
            } else {
                cropRightXInput.setStyle(badStyle);
                areaValid = false;
            }
        } catch (Exception e) {
            cropRightXInput.setStyle(badStyle);
            areaValid = false;
        }

        try {
            cropRightY = Integer.valueOf(cropRightYInput.getText());
            cropRightYInput.setStyle(null);
            if (cropRightY >= 0 && cropRightY <= currentImage.getHeight()) {
                cropRightYInput.setStyle(null);
            } else {
                cropRightYInput.setStyle(badStyle);
                areaValid = false;
            }
        } catch (Exception e) {
            cropRightYInput.setStyle(badStyle);
            areaValid = false;
        }

        if (cropLeftX >= cropRightX) {
            cropLeftXInput.setStyle(badStyle);
            cropRightXInput.setStyle(badStyle);
            areaValid = false;
        }

        if (cropLeftY >= cropRightY) {
            cropLeftYInput.setStyle(badStyle);
            cropRightYInput.setStyle(badStyle);
            areaValid = false;
        }

        if (!areaValid) {
            promptLabel.setText(getMessage("InvalidRectangle"));
            return;
        }

        if (!isSettingValues) {
            showCropScope();
        }

    }

    private void showCropScope() {
        cropScope.setLeftX(cropLeftX);
        cropScope.setLeftY(cropLeftY);
        cropScope.setRightX(cropRightX);
        cropScope.setRightY(cropRightY);

        if (task != null && task.isRunning()) {
            return;
        }
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    final Image newImage = FxmlTools.indicateScope(currentImage, cropScope);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImage(newImage);
                        }
                    });
                } catch (Exception e) {
                    logger.debug(e.toString());
                }
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void cropPickLeft() {
        pixelPickingType = PixelPickingType.RectangleLeft;
        imageView.setCursor(Cursor.HAND);
        promptLabel.setText(getMessage("ContinueClickLeft"));
    }

    @FXML
    public void cropPickRight() {
        pixelPickingType = PixelPickingType.RectangleRight;
        imageView.setCursor(Cursor.HAND);
        promptLabel.setText(getMessage("ContinueClickRight"));
    }

    @FXML
    public void cropAction() {
        pixelPickingType = ImageManufactureController.PixelPickingType.None;
        imageView.setCursor(Cursor.OPEN_HAND);
        promptLabel.setText("");

        Task cropTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    final Image newImage = FxmlTools.cropImage(currentImage,
                            cropLeftX, cropLeftY, cropRightX, cropRightY);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            undoImage = currentImage;
                            currentImage = newImage;
                            imageView.setImage(newImage);
                            imageChanged.set(true);

                            isSettingValues = true;
                            cropRightXInput.setText("");
                            cropRightYInput.setText("");
                            cropLeftXInput.setText("");
                            cropLeftYInput.setText("");
                            isSettingValues = false;
                            promptLabel.setText("");

                        }
                    });
                } catch (Exception e) {
                    logger.debug(e.toString());
                }
                return null;
            }
        };
        openHandlingStage(cropTask, Modality.WINDOW_MODAL);
        Thread thread = new Thread(cropTask);
        thread.setDaemon(true);
        thread.start();
    }

    //  Hotbar Methods
    @FXML
    public void recovery() {
        imageView.setImage(image);
        undoImage = currentImage;
        currentImage = image;
        imageChanged.set(false);
        undoButton.setDisable(false);
        redoButton.setDisable(true);
    }

    @FXML
    protected void setOriginalSize() {
        noRatio = true;
        if (imageInformation.getxPixels() > 0) {
            widthInput.setText(imageInformation.getxPixels() + "");
        }
        if (imageInformation.getyPixels() > 0) {
            heightInput.setText(imageInformation.getyPixels() + "");
        }
        noRatio = false;
    }

    @FXML
    public void save() {
        if (saveCheck.isSelected()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle(getMyStage().getTitle());
            alert.setContentText(AppVaribles.getMessage("SureOverrideFile"));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != ButtonType.OK) {
                return;
            }
        }

        Task saveTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String format = imageInformation.getImageFormat();
                final BufferedImage bufferedImage = FxmlTools.getWritableData(currentImage, format);
                ImageFileWriters.writeImageFile(bufferedImage, format, sourceFile.getAbsolutePath());
                imageInformation = ImageFileReaders.readImageMetaData(sourceFile.getAbsolutePath());
                image = currentImage;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        imageChanged.set(false);
                    }
                });
                return null;
            }
        };
        openHandlingStage(saveTask, Modality.WINDOW_MODAL);
        Thread thread = new Thread(saveTask);
        thread.setDaemon(true);
        thread.start();

    }

    @FXML
    public void saveAs() {
        try {
            final FileChooser fileChooser = new FileChooser();
            File path = new File(AppVaribles.getConfigValue(targetPathKey, System.getProperty("user.home")));
            if (!path.isDirectory()) {
                path = new File(System.getProperty("user.home"));
            }
            fileChooser.setInitialDirectory(path);
            fileChooser.getExtensionFilters().addAll(fileExtensionFilter);
            final File file = fileChooser.showSaveDialog(getMyStage());
            AppVaribles.setConfigValue(targetPathKey, file.getParent());

            Task saveTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    String format = FileTools.getFileSuffix(file.getName());
                    final BufferedImage bufferedImage = FxmlTools.getWritableData(currentImage, imageInformation.getImageFormat());
                    ImageFileWriters.writeImageFile(bufferedImage, format, file.getAbsolutePath());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (openCheck.isSelected()) {
                                showImageManufacture(file.getAbsolutePath());
                            }
                        }
                    });
                    return null;
                }
            };
            openHandlingStage(saveTask, Modality.WINDOW_MODAL);
            Thread thread = new Thread(saveTask);
            thread.setDaemon(true);
            thread.start();

        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    @FXML
    @Override
    public void zoomIn() {
        try {
            imageView.setFitHeight(imageView.getFitHeight() * (1 + zoomStep / 100.0f));
            imageView.setFitWidth(imageView.getFitWidth() * (1 + zoomStep / 100.0f));
            if (refSyncCheck.isSelected() && displayRefCheck.isSelected()) {
                refView.setFitHeight(refView.getFitHeight() * (1 + zoomStep / 100.0f));
                refView.setFitWidth(refView.getFitWidth() * (1 + zoomStep / 100.0f));
            }
        } catch (Exception e) {
            logger.debug(e.toString());
        }
    }

    @FXML
    @Override
    public void zoomOut() {
        imageView.setFitHeight(imageView.getFitHeight() * (1 - zoomStep / 100.0f));
        imageView.setFitWidth(imageView.getFitWidth() * (1 - zoomStep / 100.0f));
        if (refSyncCheck.isSelected() && displayRefCheck.isSelected()) {
            refView.setFitHeight(refView.getFitHeight() * (1 - zoomStep / 100.0f));
            refView.setFitWidth(refView.getFitWidth() * (1 - zoomStep / 100.0f));
        }
    }

    @FXML
    @Override
    public void imageSize() {
        imageView.setFitWidth(currentImage.getWidth());
        imageView.setFitHeight(currentImage.getHeight());
        if (refSyncCheck.isSelected() && displayRefCheck.isSelected() && refInfo != null) {
            refView.setFitHeight(refInfo.getyPixels());
            refView.setFitWidth(refInfo.getxPixels());
        }
    }

    @FXML
    @Override
    public void paneSize() {
        imageView.setFitHeight(scrollPane.getHeight() - 5);
        imageView.setFitWidth(scrollPane.getWidth() - 1);
        if (refSyncCheck.isSelected() && displayRefCheck.isSelected()) {
            refView.setFitHeight(refPane.getHeight() - 5);
            refView.setFitWidth(refPane.getWidth() - 1);
        }
    }

    // Common Methods
    @FXML
    public void setBottomLabel() {
        if (imageInformation == null || currentImage == null) {
            return;
        }
        String str = AppVaribles.getMessage("Format") + ":" + imageInformation.getImageFormat() + "  "
                + AppVaribles.getMessage("Pixels") + ":" + imageInformation.getxPixels() + "x" + imageInformation.getyPixels() + "  "
                + AppVaribles.getMessage("Size") + ":" + FileTools.showFileSize(imageInformation.getFile().length()) + "  "
                + AppVaribles.getMessage("ModifyTime") + ":" + DateTools.datetimeToString(imageInformation.getFile().lastModified()) + "  "
                + AppVaribles.getMessage("CurrentPixels") + ":" + (int) currentImage.getWidth() + "x" + (int) currentImage.getHeight();
        bottomLabel.setText(str);
    }

    @FXML
    public void clickImage(MouseEvent event) {
        if (currentImage == null || event.getButton() == MouseButton.SECONDARY
                || pixelPickingType == ImageManufactureController.PixelPickingType.None) {

            pixelPickingType = ImageManufactureController.PixelPickingType.None;
            imageView.setCursor(Cursor.OPEN_HAND);
            promptLabel.setText("");
            return;
        }

        int x = (int) Math.round(event.getX() * currentImage.getWidth() / imageView.getBoundsInLocal().getWidth());
        int y = (int) Math.round(event.getY() * currentImage.getHeight() / imageView.getBoundsInLocal().getHeight());

        if (pixelPickingType == PixelPickingType.NewColor) {

            PixelReader pixelReader = currentImage.getPixelReader();
            Color color = pixelReader.getColor(x, y);
            newColorPicker.setValue(color);
            promptLabel.setText(getMessage("ContinueClickColor"));

        } else if (pixelPickingType == PixelPickingType.Watermark) {

            waterXInput.setText(x + "");
            waterYInput.setText(y + "");
            promptLabel.setText(getMessage("ContinueClickPosition"));

        } else if (pixelPickingType == PixelPickingType.RectangleLeft) {

            isSettingValues = true;
            cropLeftXInput.setText(x + "");
            cropLeftYInput.setText(y + "");
            isSettingValues = false;

            if (!areaValid) {
                promptLabel.setText(getMessage("InvalidRectangle"));
            } else if (task == null || !task.isRunning()) {
                showCropScope();
                promptLabel.setText(getMessage("ContinueClickLeft"));
            } else {
                promptLabel.setText(getMessage("PickPositionComments"));
            }

        } else if (pixelPickingType == PixelPickingType.RectangleRight) {

            isSettingValues = true;
            cropRightXInput.setText(x + "");
            cropRightYInput.setText(y + "");
            isSettingValues = false;

            if (!areaValid) {
                promptLabel.setText(getMessage("InvalidRectangle"));
            } else if (task == null || !task.isRunning()) {
                showCropScope();
                promptLabel.setText(getMessage("ContinueClickRight"));
            } else {
                promptLabel.setText(getMessage("PickPositionComments"));
            }
        }

    }

    @FXML
    public void undoAction() {
        if (undoImage == null) {
            undoButton.setDisable(true);
        }
        redoImage = currentImage;
        currentImage = undoImage;
        imageView.setImage(undoImage);
        imageChanged.set(true);
        undoButton.setDisable(true);
        redoButton.setDisable(false);
    }

    @FXML
    public void redoAction() {
        if (redoImage == null) {
            redoButton.setDisable(true);
        }
        undoImage = currentImage;
        currentImage = redoImage;
        imageView.setImage(redoImage);
        imageChanged.set(true);
        undoButton.setDisable(false);
        redoButton.setDisable(true);
    }

    @FXML
    public void keyPressed() {

    }

    public void setScope(ImageScope imageScope) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(CommonValues.ImageScopeFxml), AppVaribles.CurrentBundle);
            Pane pane = fxmlLoader.load();
            ImageScopeController controller = fxmlLoader.getController();
            Stage stage = new Stage();
            controller.setMyStage(stage);

            Scene scene = new Scene(pane);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(getMyStage());
            stage.getIcons().add(CommonValues.AppIcon);
            stage.setScene(scene);
            stage.show();

            imageScope.setImage(currentImage);
            String title = AppVaribles.getMessage("ImageManufactureScope");
            switch (imageScope.getOperationType()) {
                case OperationType.Color:
                    title += " - " + AppVaribles.getMessage("Color");
                    break;
                case OperationType.ReplaceColor:
                    title += " - " + AppVaribles.getMessage("ReplaceColor");
                    break;
                case OperationType.Filters:
                    title += " - " + AppVaribles.getMessage("Filters");
                    break;
                case OperationType.Crop:
                    title += " - " + AppVaribles.getMessage("Crop");
                    break;
                default:
                    break;
            }
            controller.loadImage(this, imageScope, title);

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private ImageScope getCurrentScope() {
        String tab = tabPane.getSelectionModel().getSelectedItem().getText();
        final ImageScope imageScope;
        if (getMessage("Color").equals(tab)) {
            imageScope = colorScope;
        } else if (getMessage("ReplaceColor").equals(tab)) {
            imageScope = replaceColorScope;
        } else if (getMessage("Filters").equals(tab)) {
            imageScope = filtersScope;
        } else if (getMessage("Crop").equals(tab)) {
            showCropScope();
            promptLabel.setText(AppVaribles.getMessage("CropComments"));
            imageScope = null;
        } else {
            imageScope = null;
        }
        return imageScope;
    }

    private void showScope() {
        showScope(getCurrentScope());
    }

    private void showScope(final ImageScope imageScope) {
        if (imageScope == null || imageScope.getImage() == null) {
            refImage = currentImage;
            refInfo = imageInformation;
            displayRefCheck.setSelected(false);
            return;
        }
        if (!imageScope.isAll()) {
            displayRefCheck.setSelected(false);
            refImage = imageScope.getImage();
            refInfo = new ImageFileInformation();
            refInfo.setImageFormat(imageInformation.getImageFormat());
            refInfo.setxPixels(imageInformation.getxPixels());
            refInfo.setyPixels(imageInformation.getyPixels());
            refInfo.setIsScope(true);
            logger.debug(refInfo.isIsScope());
            displayRefCheck.setSelected(true);
        }
    }

    private void recoveryScope() {
        final ImageScope imageScope = getCurrentScope();
        if (imageScope == null) {
            return;
        }
        refImage = currentImage;
        refInfo = imageInformation;
        displayRefCheck.setSelected(false);
    }

    public void scopeDetermined(ImageScope imageScope) {
        showScope(imageScope);
        switch (imageScope.getOperationType()) {
            case OperationType.Color:
                colorScope = imageScope;
                break;
            case OperationType.ReplaceColor:
                replaceColorScope = imageScope;
                break;
            case OperationType.Filters:
                filtersScope = imageScope;
                break;
            default:
                break;
        }
    }

    @Override
    public boolean stageReloading() {
//        logger.debug("stageReloading");
        return checkSavingBeforeExit();
    }

    @Override
    public boolean stageClosing() {
//        logger.debug("stageClosing");
        if (!checkSavingBeforeExit()) {
            return false;
        }
        return super.stageClosing();
    }

    public boolean checkSavingBeforeExit() {
        if (imageChanged.getValue()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(getMyStage().getTitle());
            alert.setContentText(AppVaribles.getMessage("ImageChanged"));
            ButtonType buttonSave = new ButtonType(AppVaribles.getMessage("Save"));
            ButtonType buttonNotSave = new ButtonType(AppVaribles.getMessage("NotSave"));
            ButtonType buttonCancel = new ButtonType(AppVaribles.getMessage("Cancel"));
            alert.getButtonTypes().setAll(buttonSave, buttonNotSave, buttonCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonSave) {
                save();
                return true;
            } else if (result.get() == buttonNotSave) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

}
