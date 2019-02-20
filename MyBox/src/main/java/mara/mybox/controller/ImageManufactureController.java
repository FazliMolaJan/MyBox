package mara.mybox.controller;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.util.Callback;
import javax.imageio.ImageIO;
import mara.mybox.db.TableImageHistory;
import mara.mybox.db.TableImageInit;
import mara.mybox.image.file.ImageFileReaders;
import mara.mybox.image.file.ImageFileWriters;
import mara.mybox.value.AppVaribles;
import static mara.mybox.value.AppVaribles.getMessage;
import mara.mybox.value.CommonValues;
import mara.mybox.data.ImageHistory;
import mara.mybox.data.ImageManufactureValues;
import mara.mybox.image.ImageScope;
import mara.mybox.tools.DateTools;
import mara.mybox.tools.FileTools;
import mara.mybox.fxml.FxmlTools;
import mara.mybox.fxml.image.ImageTools;
import mara.mybox.fxml.image.FxmlScopeTools;
import static mara.mybox.fxml.FxmlTools.badStyle;
import mara.mybox.fxml.image.PixelsOperation;
import mara.mybox.image.ImageColor;
import mara.mybox.image.ImageScope.ColorScopeType;
import mara.mybox.image.ImageScope.ScopeType;
import mara.mybox.image.PixelsOperation.OperationType;
import mara.mybox.data.IntRectangle;
import static mara.mybox.value.AppVaribles.logger;

/**
 * @Author Mara
 * @CreateDate 2018-6-20
 * @Description
 * @License Apache License Version 2.0
 */
public abstract class ImageManufactureController extends ImageViewerController {

    protected ScrollPane refPane, scopePane;
    protected ImageView refView, scopeView;
    protected Label refLabel;
    protected VBox refBox, scopeBox;
    protected ImageManufactureValues values;
    protected boolean isSwitchingTab, isSettingColor;
    protected String initTab;
    protected int stageWidth, stageHeight;
    protected String imageHistoriesPath;
    protected List<String> imageHistories;
    protected ImageScope scope;

    protected TextField scopeLeftXInput, scopeLeftYInput, scopeRightXInput, scopeRightYInput;
    protected TextField scopeRadiusInput, scopeCenterXInput, scopeCenterYInput, scopeDistanceInput;
    protected ComboBox scopeMatchBox, scopePointsBox;
    protected ComboBox<Color> scopeColorsBox;
    protected HBox scopeColorSettingBox, scopeAreaSettingBox, imageLabelBox2;
    protected CheckBox scopeColorExcludedCheck, scopeAreaExcludedCheck;
    protected Button scopeClearButton, scopeDeleteButton;
    protected Label scopePromptLabel;

    public static class ImageOperationType {

        public static int Load = 0;
        public static int Arc = 1;
        public static int Color = 2;
        public static int Crop = 3;
        public static int Text = 4;
        public static int Effects = 5;
        public static int Shadow = 8;
        public static int Size = 9;
        public static int Transform = 10;
        public static int Cut_Margins = 11;
        public static int Add_Margins = 12;
        public static int Cover = 13;
        public static int Convolution = 14;
    }

    protected class ImageManufactureParameters {

    }

    @FXML
    protected ToolBar fileBar, hotBar;
    @FXML
    protected Tab fileTab, viewTab, colorTab, textTab, coverTab, cropTab,
            arcTab, shadowTab, effectsTab, sizeTab, refTab,
            browseTab, transformTab, marginsTab;
    @FXML
    protected Label promptLabel, imageLabel;
    @FXML
    protected Button selectRefButton, recoverButton, undoButton, redoButton, popButton, refButton;
    @FXML
    protected CheckBox showRefCheck, showScopeCheck, saveCheck;
    @FXML
    protected TabPane tabPane;
    @FXML
    protected HBox hotBox, imageLabelBox;
    @FXML
    protected VBox imageBox;
    @FXML
    protected ComboBox<String> hisBox, scopeListBox;
    @FXML
    protected ColorPicker colorPicker;

    public ImageManufactureController() {
        TipsLabelKey = "ImageManufactureTips";

    }

    protected void initCommon() {
        try {
            values = new ImageManufactureValues();
            values.setRefSync(true);
            isSettingColor = false;

            browseTab.setDisable(true);
            viewTab.setDisable(true);
            colorTab.setDisable(true);
            effectsTab.setDisable(true);
            sizeTab.setDisable(true);
            refTab.setDisable(true);
            transformTab.setDisable(true);
            textTab.setDisable(true);
            coverTab.setDisable(true);
            arcTab.setDisable(true);
            shadowTab.setDisable(true);
            marginsTab.setDisable(true);
            cropTab.setDisable(true);

            hotBar.setDisable(true);

            tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
                @Override
                public void changed(ObservableValue<? extends Tab> observable,
                        Tab oldTab, Tab newTab) {
                    if (isSettingValues) {
                        return;
                    }
                    hidePopup();
                    switchTab(newTab);
                }
            });

            Tooltip tips = new Tooltip(getMessage("ImageRefTips"));
            tips.setFont(new Font(16));
            FxmlTools.setComments(showRefCheck, tips);
            showRefCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                    checkReferenceImage();
                }
            });

//            tips = new Tooltip(getMessage("ImageHisComments"));
//            tips.setFont(new Font(16));
//            FxmlTools.setComments(hisBox, tips);
            hisBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue ov, Number oldValue, Number newValue) {
                    int index = newValue.intValue();
                    if (index < 0 || hisBox.getItems() == null) {
                        return;
                    }
                    if (getMessage("SettingsDot").equals(hisBox.getItems().get(index))) {
                        BaseController c = openStage(CommonValues.SettingsFxml, false);
                        c.setParentController(getMyController());
                        c.setParentFxml(getMyFxml());
                        return;
                    } else if (getMessage("OpenPathDot").equals(hisBox.getItems().get(index))) {
                        try {
                            Desktop.getDesktop().browse(new File(AppVaribles.getImageHisPath()).toURI());
                        } catch (Exception e) {
                            logger.error(e.toString());
                        }
                    }
                    if (imageHistories == null || imageHistories.size() <= index) {
                        return;
                    }
//                    logger.debug(index + " " + imageHistories.get(index) + "  " + hisBox.getSelectionModel().getSelectedItem());
                    loadImageHistory(index);
                }
            });
            hisBox.setVisibleRowCount(15);
            hisBox.setDisable(!AppVaribles.getUserConfigBoolean("ImageHis"));

            tips = new Tooltip("CTRL+s");
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(saveButton, tips);

            tips = new Tooltip("CTRL+r");
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(recoverButton, tips);

            tips = new Tooltip("CTRL+y");
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(redoButton, tips);

            tips = new Tooltip("CTRL+z");
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(undoButton, tips);

            tips = new Tooltip("CTRL+h");
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(hisBox, tips);

            tips = new Tooltip("CTRL+f");
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(refButton, tips);

            tips = new Tooltip("CTRL+p");
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(popButton, tips);

            if (showScopeCheck != null && scopeListBox != null) {
                tips = new Tooltip(getMessage("ShowScopeComments"));
                tips.setFont(new Font(16));
                FxmlTools.setComments(showScopeCheck, tips);
                showScopeCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                        if (new_val) {
                            showScopePane();
                        } else {
                            hideScopePane();
                        }
                    }
                });
                initScopeBar();
            }

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    protected void initInterface() {
        try {
            if (values == null || values.getImage() == null) {
                return;
            }
            isSettingValues = true;

            sourceFile = values.getSourceFile();
            image = values.getImage();
            imageInformation = values.getImageInfo();

            cropTab.setDisable(false);
            colorTab.setDisable(false);
            effectsTab.setDisable(false);
            arcTab.setDisable(false);
            shadowTab.setDisable(false);
            sizeTab.setDisable(false);
            refTab.setDisable(false);
            hotBar.setDisable(false);
            transformTab.setDisable(false);
            textTab.setDisable(false);
            coverTab.setDisable(false);
            marginsTab.setDisable(false);
            viewTab.setDisable(false);
            browseTab.setDisable(false);

            undoButton.setDisable(true);
            redoButton.setDisable(true);

            imageView.setPreserveRatio(true);
            imageView.setImage(values.getCurrentImage());
            imageView.setCursor(Cursor.OPEN_HAND);
            setBottomLabel();
            setImageChanged(values.isImageChanged());
            updateHisBox();

            showRefCheck.setSelected(values.isShowRef());

            getMyStage().widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue,
                        Number oldWidth, Number newWidth) {
                    stageWidth = newWidth.intValue();
                }
            });
            getMyStage().heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue,
                        Number oldHeight, Number newHeight) {
                    stageHeight = newHeight.intValue();
                }
            });
            if (values.getStageWidth() > getMyStage().getWidth()) {
                getMyStage().setWidth(values.getStageWidth());
            }
            if (values.getStageHeight() > getMyStage().getHeight()) {
                getMyStage().setHeight(values.getStageHeight());
            }
            if (values.getImageViewHeight() > 0) {
                imageView.setFitWidth(values.getImageViewWidth());
                imageView.setFitHeight(values.getImageViewHeight());
            } else {
                fitSize();
            }

            values.setScope(new ImageScope(image));
            scope = values.getScope();
            checkScopeType();

            imageView.requestFocus();

            isSettingValues = false;

            imageData = values.getImageData();

        } catch (Exception e) {
            logger.debug(e.toString());
        }

    }

    protected void initScopeBar() {
        try {

            List<String> scopeList = Arrays.asList(getMessage("All"),
                    getMessage("Rectangle"), getMessage("Circle"), getMessage("Matting"), getMessage("ColorMatching"),
                    getMessage("RectangleColor"), getMessage("CircleColor"));
            scopeListBox.getItems().addAll(scopeList);
            scopeListBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue ov, Number oldValue, Number newValue) {
                    checkScopeType();
                }
            });
            scopeListBox.getSelectionModel().select(0);

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    protected void checkScopeType() {
        try {
            if (showScopeCheck == null || values == null || scope == null) {
                return;
            }
            scope.setImage(values.getCurrentImage());
            imageView.setImage(values.getCurrentImage());
            scope.clearColors();
            scope.clearPoints();

            String selected = scopeListBox.getSelectionModel().getSelectedItem();
            if (AppVaribles.getMessage("All").equals(selected)) {
                scope.setScopeType(ImageScope.ScopeType.All);
                showScopeCheck.setDisable(true);
                hideScopePane();

            } else {

                if (AppVaribles.getMessage("Matting").equals(selected)) {
                    scope.setScopeType(ImageScope.ScopeType.Matting);

                } else if (getMessage("Rectangle").equals(selected)) {
                    scope.setScopeType(ImageScope.ScopeType.Rectangle);

                } else if (getMessage("Circle").equals(selected)) {
                    scope.setScopeType(ImageScope.ScopeType.Circle);

                } else if (getMessage("ColorMatching").equals(selected)) {
                    scope.setScopeType(ImageScope.ScopeType.Color);

                } else if (getMessage("RectangleColor").equals(selected)) {
                    scope.setScopeType(ImageScope.ScopeType.RectangleColor);

                } else if (getMessage("CircleColor").equals(selected)) {
                    scope.setScopeType(ImageScope.ScopeType.CircleColor);

                }

                showScopeCheck.setDisable(false);
                showScopeCheck.setSelected(true);
                showScopePane();
            }

            showLabels();

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    protected void checkMatchType() {
        String matchType = (String) scopeMatchBox.getSelectionModel().getSelectedItem();
        Tooltip tips;
        if (getMessage("Color").equals(matchType)) {
            scope.setColorScopeType(ColorScopeType.Color);
            tips = new Tooltip("0~255");
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(scopeDistanceInput, tips);
        } else if (getMessage("Hue").equals(matchType)) {
            scope.setColorScopeType(ColorScopeType.Hue);
            tips = new Tooltip("0~360");
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(scopeDistanceInput, tips);
        } else if (getMessage("Red").equals(matchType)) {
            scope.setColorScopeType(ColorScopeType.Red);
            tips = new Tooltip("0~255");
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(scopeDistanceInput, tips);
        } else if (getMessage("Green").equals(matchType)) {
            scope.setColorScopeType(ColorScopeType.Green);
            tips = new Tooltip("0~255");
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(scopeDistanceInput, tips);
        } else if (getMessage("Blue").equals(matchType)) {
            scope.setColorScopeType(ColorScopeType.Blue);
            tips = new Tooltip("0~255");
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(scopeDistanceInput, tips);
        } else if (getMessage("Brightness").equals(matchType)) {
            scope.setColorScopeType(ColorScopeType.Brightness);
            tips = new Tooltip("0~100");
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(scopeDistanceInput, tips);
        } else if (getMessage("Saturation").equals(matchType)) {
            scope.setColorScopeType(ColorScopeType.Saturation);
            tips = new Tooltip("0~100");
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(scopeDistanceInput, tips);
        }
        checkDistanceValue();
    }

    protected boolean checkDistanceValue() {
        try {
            if (scope.getColorScopeType() == null) {
                return false;
            }
            int distance = Integer.valueOf(scopeDistanceInput.getText());
            boolean valid = true;
            switch (scope.getColorScopeType()) {
                case Hue:
                    if (distance >= 0 && distance <= 360) {
                        scopeDistanceInput.setStyle(null);
                        scope.setHsbDistance(distance / 360.0f);
                    } else {
                        scopeDistanceInput.setStyle(badStyle);
                        valid = false;
                    }
                    break;
                case Brightness:
                case Saturation:
                    if (distance >= 0 && distance <= 100) {
                        scopeDistanceInput.setStyle(null);
                        scope.setHsbDistance(distance / 100.0f);
                    } else {
                        scopeDistanceInput.setStyle(badStyle);
                        valid = false;
                    }
                    break;
                default:
                    if (distance >= 0 && distance <= 255) {
                        scopeDistanceInput.setStyle(null);
                        scope.setColorDistance(distance);
                    } else {
                        scopeDistanceInput.setStyle(badStyle);
                        valid = false;
                    }
            }
            return valid;
        } catch (Exception e) {
            scopeDistanceInput.setStyle(badStyle);
            return false;
        }
    }

    protected void checkRectangle() {
        if (isSettingValues) {
            return;
        }
        boolean areaValid = true;
        int leftX = -1, leftY = -1, rightX = -1, rightY = -1;
        try {
            leftX = Integer.valueOf(scopeLeftXInput.getText());
            if (leftX >= 0 && leftX <= values.getCurrentImage().getWidth()) {
                scopeLeftXInput.setStyle(null);
            } else {
                areaValid = false;
                scopeLeftXInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            areaValid = false;
            scopeLeftXInput.setStyle(badStyle);
        }
        try {
            leftY = Integer.valueOf(scopeLeftYInput.getText());
            if (leftY >= 0 && leftY <= values.getCurrentImage().getHeight()) {
                scopeLeftYInput.setStyle(null);
            } else {
                areaValid = false;
                scopeLeftYInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            areaValid = false;
            scopeLeftYInput.setStyle(badStyle);
        }

        try {
            rightX = Integer.valueOf(scopeRightXInput.getText());
            if (rightX >= 0 && rightX <= values.getCurrentImage().getWidth()) {
                scopeRightXInput.setStyle(null);
            } else {
                areaValid = false;
                scopeRightXInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            areaValid = false;
            scopeRightXInput.setStyle(badStyle);
        }
        try {
            rightY = Integer.valueOf(scopeRightYInput.getText());
            if (rightY >= 0 && rightY <= values.getCurrentImage().getHeight()) {
                scopeRightYInput.setStyle(null);
            } else {
                areaValid = false;
                scopeRightYInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            areaValid = false;
            scopeRightYInput.setStyle(badStyle);
        }

        if (leftX >= rightX) {
            scopeRightXInput.setStyle(badStyle);
            areaValid = false;
        }

        if (leftY >= rightY) {
            scopeRightYInput.setStyle(badStyle);
            areaValid = false;
        }

        if (areaValid) {
            scope.setRectangle(new IntRectangle(leftX, leftY, rightX, rightY));
            indicateRectangle();
        } else {
            popError(getMessage("InvalidRectangle"));
        }

    }

    protected void checkCircle() {
        if (isSettingValues) {
            return;
        }
        boolean areaValid = true;
        int x = -1, y = -1, r;

        try {
            x = Integer.valueOf(scopeCenterXInput.getText());
            if (x >= 0 && x <= values.getCurrentImage().getWidth()) {
                scopeCenterXInput.setStyle(null);
            } else {
                areaValid = false;
                scopeCenterXInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            areaValid = false;
            scopeCenterXInput.setStyle(badStyle);
        }
        try {
            y = Integer.valueOf(scopeCenterYInput.getText());
            if (y >= 0 && y <= values.getCurrentImage().getHeight()) {
                scopeCenterYInput.setStyle(null);
            } else {
                areaValid = false;
                scopeCenterYInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            areaValid = false;
            scopeCenterYInput.setStyle(badStyle);
        }

        if (areaValid) {
            scope.setCircleCenter(x, y);
        }

        try {
            r = Integer.valueOf(scopeRadiusInput.getText());
            if (r > 0) {
                scopeRadiusInput.setStyle(null);
                scope.setCircleRadius(r);
            } else {
                areaValid = false;
                scopeRadiusInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            areaValid = false;
            scopeRadiusInput.setStyle(badStyle);
        }

        if (areaValid) {
            indicateCircle();
        } else {
            popError(getMessage("InvalidCircle"));
        }
    }

    protected void indicateScope() {
        if (null != scope.getScopeType()) {
            switch (scope.getScopeType()) {
                case All:
                    break;
                case Matting:
                    indicateMatting();
                    break;
                case Rectangle:
                    indicateRectangle();
                    break;
                case Circle:
                    indicateCircle();
                    break;
                case Color:
                    indicateColor();
                    break;
                case RectangleColor:
                    indicateRectangle();
                    break;
                case CircleColor:
                    indicateCircle();
                    break;
                default:
                    break;
            }
        }
    }

    protected void indicateRectangle() {
        if (scope.getScopeType() != ScopeType.Rectangle
                && scope.getScopeType() != ScopeType.RectangleColor) {
            return;
        }
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    int lineWidth = 1;
                    if (values.getCurrentImage().getWidth() >= 150) {
                        lineWidth = (int) values.getCurrentImage().getWidth() / 150;
                    }
                    final Image newImage = FxmlScopeTools.indicateRectangle(values.getCurrentImage(),
                            Color.RED, lineWidth, scope.getRectangle());
                    if (task.isCancelled()) {
                        return null;
                    }
                    PixelsOperation pixelsOperation = new PixelsOperation(values.getCurrentImage(), scope, OperationType.Scope);
                    final Image scopedImage = pixelsOperation.operateFxImage();
                    if (task.isCancelled()) {
                        return null;
                    }
                    final Image rectImage = FxmlScopeTools.indicateRectangle(scopedImage,
                            Color.RED, lineWidth, scope.getRectangle());
                    if (task.isCancelled()) {
                        return null;
                    }
                    scope.setImage(rectImage);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImage(newImage);
                            if (scopeView != null) {
                                scopeView.setImage(rectImage);
                            }
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

    protected void indicateCircle() {
        if (scope.getScopeType() != ScopeType.Circle
                && scope.getScopeType() != ScopeType.CircleColor) {
            return;
        }
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    int lineWidth = 1;
                    if (values.getCurrentImage().getWidth() >= 150) {
                        lineWidth = (int) values.getCurrentImage().getWidth() / 150;
                    }
                    final Image newImage = FxmlScopeTools.indicateCircle(values.getCurrentImage(),
                            Color.RED, lineWidth, scope.getCircle());
                    if (task.isCancelled()) {
                        return null;
                    }
                    PixelsOperation pixelsOperation = new PixelsOperation(values.getCurrentImage(), scope, OperationType.Scope);
                    final Image scopedImage = pixelsOperation.operateFxImage();
                    if (task.isCancelled()) {
                        return null;
                    }
                    final Image circledImage = FxmlScopeTools.indicateCircle(scopedImage,
                            Color.RED, lineWidth, scope.getCircle());
                    if (task.isCancelled()) {
                        return null;
                    }
                    scope.setImage(circledImage);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImage(newImage);
                            if (scopeView != null) {
                                scopeView.setImage(circledImage);
                            }
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

    protected void indicateMatting() {
        if (scope.getScopeType() != ScopeType.Matting) {
            return;
        }
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    PixelsOperation pixelsOperation = new PixelsOperation(values.getCurrentImage(), scope, OperationType.Scope);
                    final Image scopeImage = pixelsOperation.operateFxImage();
                    if (task.isCancelled()) {
                        return null;
                    }
                    scope.setImage(scopeImage);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (scopeView != null) {
                                scopeView.setImage(scopeImage);
                            }
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

    protected void indicateColor() {
        if (scope.getScopeType() != ScopeType.Color) {
            return;
        }
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    PixelsOperation pixelsOperation = new PixelsOperation(values.getCurrentImage(), scope, OperationType.Scope);
                    final Image scopeImage = pixelsOperation.operateFxImage();
                    if (task.isCancelled()) {
                        return null;
                    }
                    scope.setImage(scopeImage);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (scopeView != null) {
                                scopeView.setImage(scopeImage);
                            }
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
    @Override
    protected void selectSourceFile(ActionEvent event) {
        if (values == null || values.getCurrentImage() != null && values.isImageChanged()) {
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

            if (imageInformation != null && imageInformation.isIsSampled()) {
                hotBar.setDisable(false);
                showRefCheck.setDisable(true);
                hisBox.setDisable(true);
                undoButton.setDisable(true);
                redoButton.setDisable(true);
                recoverButton.setDisable(true);
                saveButton.setDisable(true);

                browseTab.setDisable(true);
                viewTab.setDisable(true);
                colorTab.setDisable(true);
                effectsTab.setDisable(true);
                sizeTab.setDisable(true);
                refTab.setDisable(true);
                transformTab.setDisable(true);
                textTab.setDisable(true);
                coverTab.setDisable(true);
                arcTab.setDisable(true);
                shadowTab.setDisable(true);
                marginsTab.setDisable(true);
                cropTab.setDisable(true);

            }

            isSettingValues = true;
            values.setSourceFile(sourceFile);
            values.setImage(image);
            values.setImageInfo(imageInformation);
            values.setCurrentImage(image);
            isSettingValues = false;

            if (image == null
                    || (imageInformation != null && imageInformation.isIsSampled())) {
                return;
            }

            isSettingValues = true;
            values.setRefImage(image);
            values.setRefInfo(imageInformation);
            setImageChanged(false);
            values.setScope(new ImageScope(image));
            scope = values.getScope();

            recordImageHistory(ImageOperationType.Load, image);

            imageData = values.getImageData();
            if (initTab != null) {
                switchTab(initTab);
            } else {
                initInterface();
            }
            isSettingValues = false;

        } catch (Exception e) {
            logger.debug(e.toString());
        }
    }

    public void setImage(final File file) {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                BufferedImage bufferImage = ImageIO.read(file);
                if (task.isCancelled()) {
                    return null;
                }
                final Image newImage = SwingFXUtils.toFXImage(bufferImage, null);
                if (task.isCancelled()) {
                    return null;
                }
//                recordImageHistory(ImageOperationType.Load, newImage);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        values.setUndoImage(values.getCurrentImage());
                        values.setCurrentImage(newImage);
                        imageView.setImage(newImage);
                        setImageChanged(true);
                        setBottomLabel();
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

    public void showRef() {
        showRefCheck.setSelected(true);
    }

    protected void checkReferenceImage() {
        try {
            values.setShowRef(showRefCheck.isSelected());

            if (values.isShowRef()) {

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
                            if (values.getRefInfo() == null) {
                                return;
                            }
                            String str = AppVaribles.getMessage("Format") + ":" + values.getRefInfo().getImageFormat() + "  "
                                    + AppVaribles.getMessage("Pixels") + ":" + values.getRefInfo().getWidth() + "x" + values.getRefInfo().getHeight();
                            if (values.getRefFile() != null) {
                                str += "  " + AppVaribles.getMessage("Size") + ":" + FileTools.showFileSize(values.getRefFile().length()) + "  "
                                        + AppVaribles.getMessage("ModifyTime") + ":" + DateTools.datetimeToString(values.getRefFile().lastModified());
                            }
                            bottomLabel.setText(str);
                        }
                    });
                    refPane.setContent(refView);
                }

                if (refBox == null) {
                    refBox = new VBox();
                    VBox.setVgrow(refBox, Priority.ALWAYS);
                    HBox.setHgrow(refBox, Priority.ALWAYS);
                    refLabel = new Label();
                    refLabel.setText(getMessage("Reference"));
                    refLabel.setAlignment(Pos.CENTER);
                    VBox.setVgrow(refLabel, Priority.NEVER);
                    HBox.setHgrow(refLabel, Priority.ALWAYS);
                    refBox.getChildren().add(0, refLabel);
                    refBox.getChildren().add(1, refPane);
                }

                if (values.getRefFile() == null) {
                    values.setRefFile(values.getSourceFile());
                }
                if (values.getRefImage() == null) {
                    loadReferenceImage();
                } else {
                    refView.setImage(values.getRefImage());
                    if (values.getRefInfo() != null) {
//                            logger.debug(scrollPane.getHeight() + " " + refInfo.getyPixels());
                        if (scrollPane.getHeight() < values.getRefInfo().getHeight()) {
                            refView.setFitWidth(scrollPane.getWidth() - 1);
                            refView.setFitHeight(scrollPane.getHeight() - 5); // use attributes of scrollPane but not refPane
                        } else {
                            refView.setFitWidth(values.getRefInfo().getWidth());
                            refView.setFitHeight(values.getRefInfo().getHeight());
                        }
                    }
                }

                if (!splitPane.getItems().contains(refBox)) {
                    splitPane.getItems().add(0, refBox);
                }

            } else {

                if (refBox != null && splitPane.getItems().contains(refBox)) {
                    splitPane.getItems().remove(refBox);
                }

            }

            adjustSplitPane();

        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    protected void loadReferenceImage() {
        if (values.getRefFile() == null || values.getSourceFile() == null) {
            return;
        }
        if (values.getRefFile().getAbsolutePath().equals(values.getSourceFile().getAbsolutePath())) {
            values.setRefImage(image);
            values.setRefInfo(values.getImageInfo());
            refView.setImage(image);
            if (scrollPane.getHeight() < (int) values.getImage().getWidth()) {
                refView.setFitWidth(scrollPane.getWidth() - 1);
                refView.setFitHeight(scrollPane.getHeight() - 5); // use attributes of scrollPane but not refPane
            } else {
                refView.setFitWidth((int) values.getImage().getWidth());
                refView.setFitHeight((int) values.getImage().getHeight());
            }
            return;
        }
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                values.setRefInfo(ImageFileReaders.readImageFileMetaData(values.getRefFile().getAbsolutePath()).getImageInformation());
                if (task.isCancelled()) {
                    return null;
                }
                values.setRefImage(SwingFXUtils.toFXImage(ImageIO.read(values.getRefFile()), null));
                if (task.isCancelled()) {
                    return null;
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        refView.setImage(values.getRefImage());
                        if (refPane.getHeight() < values.getRefInfo().getHeight()) {
                            refView.setFitWidth(refPane.getWidth() - 1);
                            refView.setFitHeight(refPane.getHeight() - 5);
                        } else {
                            refView.setFitWidth(values.getRefInfo().getWidth());
                            refView.setFitHeight(values.getRefInfo().getHeight());
                        }
                        setBottomLabel();
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

    protected void setImageChanged(boolean imageChanged) {
        values.setImageChanged(imageChanged);
        if (imageChanged) {
            if (values.getSourceFile() != null) {
                getMyStage().setTitle(getBaseTitle() + "  " + values.getSourceFile().getAbsolutePath() + "*");
            }
            saveButton.setDisable(false);
            recoverButton.setDisable(false);
            undoButton.setDisable(false);
            redoButton.setDisable(true);

            loadData();

        } else {
            saveButton.setDisable(true);
            recoverButton.setDisable(true);
            if (values.getSourceFile() != null) {
                getMyStage().setTitle(getBaseTitle() + "  " + values.getSourceFile().getAbsolutePath());
            }
        }
        setBottomLabel();
        if (scopeView != null) {
            scope.setImage(imageView.getImage());
            switch (scope.getScopeType()) {
                case Matting:
                    indicateMatting();
                    break;
                case Rectangle:
                    indicateRectangle();
                    break;
                case Circle:
                    indicateCircle();
                    break;
                case Color:
                    indicateColor();
                    break;
                default:
                    break;
            }
        }

    }

    protected void updateHisBox() {
        if (!AppVaribles.getUserConfigBoolean("ImageHis") || values.getSourceFile() == null) {
            hisBox.setDisable(true);
            return;
        }
        hisBox.setDisable(false);
        hisBox.getItems().clear();
        String fname = values.getSourceFile().getAbsolutePath();
        List<ImageHistory> his = TableImageHistory.read(fname);
        List<String> hisStrings = new ArrayList<>();
        imageHistories = new ArrayList<>();
        for (ImageHistory r : his) {
            String s;
            if (r.getUpdate_type() == ImageOperationType.Load) {
                s = AppVaribles.getMessage("Load");
            } else if (r.getUpdate_type() == ImageOperationType.Add_Margins) {
                s = AppVaribles.getMessage("AddMargins");
            } else if (r.getUpdate_type() == ImageOperationType.Arc) {
                s = AppVaribles.getMessage("Arc");
            } else if (r.getUpdate_type() == ImageOperationType.Color) {
                s = AppVaribles.getMessage("Color");
            } else if (r.getUpdate_type() == ImageOperationType.Crop) {
                s = AppVaribles.getMessage("Crop");
            } else if (r.getUpdate_type() == ImageOperationType.Cut_Margins) {
                s = AppVaribles.getMessage("CutMargins");
            } else if (r.getUpdate_type() == ImageOperationType.Effects) {
                s = AppVaribles.getMessage("Effects");
            } else if (r.getUpdate_type() == ImageOperationType.Convolution) {
                s = AppVaribles.getMessage("Convolution");
            } else if (r.getUpdate_type() == ImageOperationType.Shadow) {
                s = AppVaribles.getMessage("Shadow");
            } else if (r.getUpdate_type() == ImageOperationType.Size) {
                s = AppVaribles.getMessage("Size");
            } else if (r.getUpdate_type() == ImageOperationType.Transform) {
                s = AppVaribles.getMessage("Transform");
            } else if (r.getUpdate_type() == ImageOperationType.Text) {
                s = AppVaribles.getMessage("Text");
            } else if (r.getUpdate_type() == ImageOperationType.Cover) {
                s = AppVaribles.getMessage("Cover");
            } else {
                continue;
            }
            s = DateTools.datetimeToString(r.getOperation_time()) + " " + s;
            hisStrings.add(s);
            imageHistories.add(r.getHistory_location());
        }
        ImageHistory init = TableImageInit.read(fname);
        if (init != null) {
            String s = DateTools.datetimeToString(init.getOperation_time()) + " " + AppVaribles.getMessage("Load");
            hisStrings.add(s);
            imageHistories.add(init.getHistory_location());
        }
        hisStrings.add(AppVaribles.getMessage("OpenPathDot"));
        hisStrings.add(AppVaribles.getMessage("SettingsDot"));
        hisBox.getItems().addAll(hisStrings);
    }

    protected void recordImageHistory(final int updateType, final Image newImage) {
        if (values == null || values.getSourceFile() == null
                || !AppVaribles.getUserConfigBoolean("ImageHis")
                || updateType < 0 || newImage == null) {
            return;
        }
        if (imageHistoriesPath == null) {
            imageHistoriesPath = AppVaribles.getImageHisPath();
        }
        Task saveTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    final BufferedImage bufferedImage = ImageTools.getBufferedImage(newImage);
                    String filename = imageHistoriesPath + File.separator
                            + FileTools.getFilePrefix(values.getSourceFile().getName())
                            + "_" + (new Date().getTime()) + "_" + updateType
                            + "_" + new Random().nextInt(1000) + ".png";
                    while (new File(filename).exists()) {
                        filename = imageHistoriesPath + File.separator
                                + FileTools.getFilePrefix(values.getSourceFile().getName())
                                + "_" + (new Date().getTime()) + "_" + updateType
                                + "_" + new Random().nextInt(1000) + ".png";
                    }
                    filename = new File(filename).getAbsolutePath();
                    ImageFileWriters.writeImageFile(bufferedImage, "png", filename);
                    if (updateType == ImageOperationType.Load) {
                        TableImageInit.write(values.getSourceFile().getAbsolutePath(), filename);
                    } else {
                        TableImageHistory.add(values.getSourceFile().getAbsolutePath(), updateType, filename);
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            updateHisBox();
                        }
                    });
                } catch (Exception e) {
                    logger.debug(e.toString());
                }
                return null;
            }
        };
        Thread thread = new Thread(saveTask);
        thread.setDaemon(true);
        thread.start();
    }

    protected boolean loadImageHistory(final int index) {
        if (values == null || values.getSourceFile() == null
                || imageHistories == null
                || index < 0 || index > imageHistories.size() - 1) {
            return false;
        }
        String filename = imageHistories.get(index);
        try {
            File file = new File(filename);
            if (file.exists()) {
                setImage(file);
                return true;
            }
        } catch (Exception e) {
        }
        imageHistories.remove(index);
        TableImageHistory.clearHistory(values.getSourceFile().getAbsolutePath(), filename);
        return false;
    }

    protected void switchTab(Tab newTab) {

        String tabName = findTabName(newTab);
        switchTab(tabName);
    }

    public void switchTab(String tabName) {
        try {
            isSwitchingTab = true;
            String fxml = null;
            switch (tabName) {
                case "file":
                    fxml = CommonValues.ImageManufactureFileFxml;
                    break;
                case "size":
                    fxml = CommonValues.ImageManufactureSizeFxml;
                    break;
                case "crop":
                    fxml = CommonValues.ImageManufactureCropFxml;
                    break;
                case "color":
                    fxml = CommonValues.ImageManufactureColorFxml;
                    break;
                case "effects":
                    fxml = CommonValues.ImageManufactureEffectsFxml;
                    break;
                case "text":
                    fxml = CommonValues.ImageManufactureTextFxml;
                    break;
                case "cover":
                    fxml = CommonValues.ImageManufactureCoverFxml;
                    break;
                case "arc":
                    fxml = CommonValues.ImageManufactureArcFxml;
                    break;
                case "shadow":
                    fxml = CommonValues.ImageManufactureShadowFxml;
                    break;
                case "transform":
                    fxml = CommonValues.ImageManufactureTransformFxml;
                    break;
                case "margins":
                    fxml = CommonValues.ImageManufactureMarginsFxml;
                    break;
                case "view":
                    fxml = CommonValues.ImageManufactureViewFxml;
                    break;
                case "ref":
                    fxml = CommonValues.ImageManufactureRefFxml;
                    break;
                case "browse":
                    fxml = CommonValues.ImageManufactureBrowseFxml;
                    break;
            }

            if (fxml != null) {
//                values.setCurrentImage(imageView.getImage());
                values.setStageWidth(stageWidth);
                values.setStageHeight(stageHeight);
                values.setImageViewWidth((int) imageView.getFitWidth());
                values.setImageViewHeight((int) imageView.getFitHeight());
                values.setImageData(imageData);
                ImageManufactureController controller
                        = (ImageManufactureController) reloadStage(fxml, AppVaribles.getMessage("ImageManufacture"));
                controller.setValues(values);
                controller.setTab(tabName);
                controller.xZoomStep = xZoomStep;
                controller.yZoomStep = yZoomStep;
                controller.initInterface();
                controller.loadData(imageData);

            }
            isSwitchingTab = false;
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    public void setTab(String tabName) {
        try {
            Tab tab = findTab(tabName);
            if (tab == null) {
                return;
            }
            isSettingValues = true;
            tabPane.getSelectionModel().select(tab);
            isSettingValues = false;
        } catch (Exception e) {
            logger.debug(e.toString());
        }
    }

    protected String findTabName(Tab tab) {
        String tabName = null;
        if (fileTab.equals(tab)) {
            tabName = "file";
        } else if (sizeTab.equals(tab)) {
            tabName = "size";
        } else if (cropTab.equals(tab)) {
            tabName = "crop";
        } else if (effectsTab.equals(tab)) {
            tabName = "effects";
        } else if (colorTab.equals(tab)) {
            tabName = "color";
        } else if (textTab.equals(tab)) {
            tabName = "text";
        } else if (coverTab.equals(tab)) {
            tabName = "cover";
        } else if (arcTab.equals(tab)) {
            tabName = "arc";
        } else if (shadowTab.equals(tab)) {
            tabName = "shadow";
        } else if (transformTab.equals(tab)) {
            tabName = "transform";
        } else if (marginsTab.equals(tab)) {
            tabName = "margins";
        } else if (viewTab.equals(tab)) {
            tabName = "view";
        } else if (refTab.equals(tab)) {
            tabName = "ref";
        } else if (browseTab.equals(tab)) {
            tabName = "browse";
        }
        return tabName;
    }

    public Tab findTab(String tabName) {
        switch (tabName) {
            case "file":
                return fileTab;
            case "size":
                return sizeTab;
            case "crop":
                return cropTab;
            case "color":
                return colorTab;
            case "effects":
                return effectsTab;
            case "text":
                return textTab;
            case "cover":
                return coverTab;
            case "arc":
                return arcTab;
            case "shadow":
                return shadowTab;
            case "transform":
                return transformTab;
            case "margins":
                return marginsTab;
            case "view":
                return viewTab;
            case "ref":
                return refTab;
            case "browse":
                return browseTab;
        }
        return null;
    }

    //  Hotbar Methods
    @FXML
    @Override
    public void saveAction() {
        if (saveButton.isDisabled()) {
            return;
        }
        if (values.getSourceFile() == null) {
            saveAsAction();
            return;
        }
        if (values.isIsConfirmBeforeSave()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(getMyStage().getTitle());
            alert.setContentText(AppVaribles.getMessage("SureOverrideFile"));
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            ButtonType buttonSave = new ButtonType(AppVaribles.getMessage("Save"));
            ButtonType buttonSaveAs = new ButtonType(AppVaribles.getMessage("SaveAs"));
            ButtonType buttonCancel = new ButtonType(AppVaribles.getMessage("Cancel"));
            alert.getButtonTypes().setAll(buttonSave, buttonSaveAs, buttonCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonCancel) {
                return;
            } else if (result.get() == buttonSaveAs) {
                saveAsAction();
                return;
            }

        }

        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String format = "png";
                if (values.getImageInfo() != null) {
                    format = values.getImageInfo().getImageFormat();
                }
                final BufferedImage bufferedImage = ImageTools.getBufferedImage(values.getCurrentImage());
                if (task.isCancelled()) {
                    return null;
                }
                ImageFileWriters.writeImageFile(bufferedImage, format, values.getSourceFile().getAbsolutePath());
                if (task.isCancelled()) {
                    return null;
                }
                imageInformation = ImageFileReaders.readImageFileMetaData(values.getSourceFile().getAbsolutePath()).getImageInformation();
                image = values.getCurrentImage();
                values.setImage(image);
                values.setImageInfo(imageInformation);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        setImageChanged(false);
                        setBottomLabel();
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
    public void saveAsAction() {
        try {
            final FileChooser fileChooser = new FileChooser();
            File path = AppVaribles.getUserConfigPath(targetPathKey);
            if (path.exists()) {
                fileChooser.setInitialDirectory(path);
            }
            fileChooser.getExtensionFilters().addAll(fileExtensionFilter);
            final File file = fileChooser.showSaveDialog(getMyStage());
            if (file == null) {
                return;
            }
            AppVaribles.setUserConfigValue(targetPathKey, file.getParent());

            task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    String format = FileTools.getFileSuffix(file.getName());
                    final BufferedImage bufferedImage = ImageTools.getBufferedImage(values.getCurrentImage());
                    if (task.isCancelled()) {
                        return null;
                    }
                    ImageFileWriters.writeImageFile(bufferedImage, format, file.getAbsolutePath());
                    if (task.isCancelled()) {
                        return null;
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (values.getSourceFile() == null
                                    || values.getSaveAsType() == ImageManufactureFileController.SaveAsType.Load) {
                                sourceFileChanged(file);

                            } else if (values.getSaveAsType() == ImageManufactureFileController.SaveAsType.Open) {
                                openImageManufacture(file.getAbsolutePath());
                            }
                            popInformation(AppVaribles.getMessage("Successful"));
                        }
                    });
                    return null;
                }
            };
            openHandlingStage(task, Modality.WINDOW_MODAL);
            Thread thread = new Thread(task);
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
            super.zoomIn();

            if (values.isRefSync() && refView != null) {
                refView.setFitWidth(imageView.getFitWidth());
                refView.setFitHeight(imageView.getFitHeight());
            }
            if (scopeView != null) {
                scopeView.setFitWidth(imageView.getFitWidth());
                scopeView.setFitHeight(imageView.getFitHeight());
            }
        } catch (Exception e) {
            logger.debug(e.toString());
        }
    }

    @FXML
    @Override
    public void zoomOut() {
        super.zoomOut();
        if (values.isRefSync() && refView != null) {
            refView.setFitWidth(imageView.getFitWidth());
            refView.setFitHeight(imageView.getFitHeight());
        }
        if (scopeView != null) {
            scopeView.setFitWidth(imageView.getFitWidth());
            scopeView.setFitHeight(imageView.getFitHeight());
        }
    }

    @FXML
    @Override
    public void imageSize() {
        super.imageSize();
        if (values.isRefSync() && refView != null) {
            refView.setFitWidth(refView.getImage().getWidth());
            refView.setFitHeight(refView.getImage().getHeight());
        }
        if (scopeView != null) {
            scopeView.setFitWidth(scopeView.getImage().getWidth());
            scopeView.setFitHeight(scopeView.getImage().getHeight());
        }
    }

    @FXML
    @Override
    public void paneSize() {
        super.paneSize();
        if (values.isRefSync() && refView != null) {
            refView.setFitWidth(scrollPane.getWidth() - 5);
            refView.setFitHeight(scrollPane.getHeight() - 5);
        }
        if (scopeView != null) {
            scopeView.setFitWidth(scrollPane.getWidth() - 5);
            scopeView.setFitHeight(scrollPane.getHeight() - 5);
        }
    }

    // Common Methods
    @FXML
    public void setBottomLabel() {
        if (values == null || values.getCurrentImage() == null) {
            return;
        }
        String str;
        if (values.getImageInfo() == null) {
            str = AppVaribles.getMessage("CurrentPixels") + ":" + (int) values.getCurrentImage().getWidth() + "x" + (int) values.getCurrentImage().getHeight();
        } else {
            str = AppVaribles.getMessage("Format") + ":" + values.getImageInfo().getImageFormat() + "  "
                    + AppVaribles.getMessage("Pixels") + ":" + (int) values.getImage().getWidth() + "x" + (int) values.getImage().getHeight() + "  "
                    + AppVaribles.getMessage("Size") + ":" + FileTools.showFileSize(values.getSourceFile().length()) + "  "
                    + AppVaribles.getMessage("ModifyTime") + ":" + DateTools.datetimeToString(values.getSourceFile().lastModified()) + "  "
                    + AppVaribles.getMessage("CurrentPixels") + ":" + (int) values.getCurrentImage().getWidth() + "x" + (int) values.getCurrentImage().getHeight();
        }
        bottomLabel.setText(str);
    }

    @FXML
    @Override
    public void clickImage(MouseEvent event) {
        if (values == null || values.getCurrentImage() == null || scope == null) {
            imageView.setCursor(Cursor.OPEN_HAND);
            return;
        }
        imageView.setCursor(Cursor.HAND);

        int x = (int) Math.round(event.getX() * values.getCurrentImage().getWidth() / imageView.getBoundsInLocal().getWidth());
        int y = (int) Math.round(event.getY() * values.getCurrentImage().getHeight() / imageView.getBoundsInLocal().getHeight());
        PixelReader pixelReader = values.getCurrentImage().getPixelReader();
        Color color = pixelReader.getColor(x, y);

        switch (scope.getScopeType()) {
            case All:
                if (isSettingColor) {
                    colorPicker.setValue(color);
                }
                break;

            case Color:
                if (isSettingColor
                        && event.getButton() == MouseButton.SECONDARY) {
                    colorPicker.setValue(color);
                } else {
                    scope.addColor(ImageColor.converColor(color));
                    scopeColorsBox.getItems().add(color);
                    scopeColorsBox.getSelectionModel().select(scopeColorsBox.getItems().size() - 1);
                    scopeColorsBox.setVisibleRowCount(15);
                    indicateColor();
                }
                break;

            case Matting:
                if (isSettingColor
                        && event.getButton() == MouseButton.SECONDARY) {
                    colorPicker.setValue(color);
                } else {
                    scope.addPoints(x, y);
                    scopePointsBox.getItems().add(x + "," + y);
                    scopePointsBox.getSelectionModel().select(scopePointsBox.getItems().size() - 1);
                    scopePointsBox.setVisibleRowCount(15);
                    indicateMatting();
                }
                break;

            case Rectangle:
                if (isSettingColor) {
                    colorPicker.setValue(color);
                } else {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        isSettingValues = true;
                        scopeLeftXInput.setText(x + "");
                        scopeLeftYInput.setText(y + "");
                        isSettingValues = false;

                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        isSettingValues = true;
                        scopeRightXInput.setText(x + "");
                        scopeRightYInput.setText(y + "");
                        isSettingValues = false;
                    }
                    checkRectangle();
                }
                break;

            case Circle:
                if (isSettingColor) {
                    colorPicker.setValue(color);
                } else {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        isSettingValues = true;
                        scopeCenterXInput.setText(x + "");
                        scopeCenterYInput.setText(y + "");
                        isSettingValues = false;

                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        isSettingValues = true;
                        int cx = scope.getCircle().getCenterX();
                        int cy = scope.getCircle().getCenterY();
                        long r = Math.round(Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy)));
                        scopeRadiusInput.setText(r + "");
                        isSettingValues = false;

                    }
                    checkCircle();
                }
                break;

            case RectangleColor:
                if (isSettingColor
                        && event.getButton() == MouseButton.SECONDARY) {
                    colorPicker.setValue(color);
                } else {
                    scope.addColor(ImageColor.converColor(color));
                    scopeColorsBox.getItems().add(color);
                    scopeColorsBox.getSelectionModel().select(scopeColorsBox.getItems().size() - 1);
                    scopeColorsBox.setVisibleRowCount(15);

                    checkRectangle();
                }
                break;

            case CircleColor:
                if (isSettingColor
                        && event.getButton() == MouseButton.SECONDARY) {
                    colorPicker.setValue(color);
                } else {
                    scope.addColor(ImageColor.converColor(color));
                    scopeColorsBox.getItems().add(color);
                    scopeColorsBox.getSelectionModel().select(scopeColorsBox.getItems().size() - 1);
                    scopeColorsBox.setVisibleRowCount(15);

                    checkCircle();
                }
                break;

            default:
                break;

        }
    }

    public void scopeViewClicked(MouseEvent event) {
        if (scopeView == null || values == null
                || values.getCurrentImage() == null || scope == null) {
            scopeView.setCursor(Cursor.OPEN_HAND);
            return;
        }
        scopeView.setCursor(Cursor.HAND);

        int x = (int) Math.round(event.getX() * values.getCurrentImage().getWidth() / scopeView.getBoundsInLocal().getWidth());
        int y = (int) Math.round(event.getY() * values.getCurrentImage().getHeight() / scopeView.getBoundsInLocal().getHeight());
        PixelReader pixelReader = values.getCurrentImage().getPixelReader();
        Color color = pixelReader.getColor(x, y);

        switch (scope.getScopeType()) {
            case Color:
                scope.addColor(ImageColor.converColor(color));
                scopeColorsBox.getItems().add(color);
                scopeColorsBox.getSelectionModel().select(scopeColorsBox.getItems().size() - 1);
                scopeColorsBox.setVisibleRowCount(15);
                indicateColor();
                break;

            case Matting:
                scope.addPoints(x, y);
                scopePointsBox.getItems().add(x + "," + y);
                scopePointsBox.getSelectionModel().select(scopePointsBox.getItems().size() - 1);
                scopePointsBox.setVisibleRowCount(15);
                indicateMatting();
                break;

            case Rectangle:
                if (event.getButton() == MouseButton.PRIMARY) {
                    isSettingValues = true;
                    scopeLeftXInput.setText(x + "");
                    scopeLeftYInput.setText(y + "");
                    isSettingValues = false;

                } else if (event.getButton() == MouseButton.SECONDARY) {
                    isSettingValues = true;
                    scopeRightXInput.setText(x + "");
                    scopeRightYInput.setText(y + "");
                    isSettingValues = false;
                }
                checkRectangle();
                break;

            case Circle:
                if (event.getButton() == MouseButton.PRIMARY) {
                    isSettingValues = true;
                    scopeCenterXInput.setText(x + "");
                    scopeCenterYInput.setText(y + "");
                    isSettingValues = false;

                } else if (event.getButton() == MouseButton.SECONDARY) {
                    isSettingValues = true;
                    int cx = scope.getCircle().getCenterX();
                    int cy = scope.getCircle().getCenterY();
                    long r = Math.round(Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy)));
                    scopeRadiusInput.setText(r + "");
                    isSettingValues = false;

                }
                checkCircle();
                break;

            case RectangleColor:
                if (event.getButton() == MouseButton.PRIMARY) {
                    isSettingValues = true;
                    scopeLeftXInput.setText(x + "");
                    scopeLeftYInput.setText(y + "");
                    isSettingValues = false;

                } else if (event.getButton() == MouseButton.SECONDARY) {
                    isSettingValues = true;
                    scopeRightXInput.setText(x + "");
                    scopeRightYInput.setText(y + "");
                    isSettingValues = false;
                }

                checkRectangle();
                break;

            case CircleColor:
                if (event.getButton() == MouseButton.PRIMARY) {
                    isSettingValues = true;
                    scopeCenterXInput.setText(x + "");
                    scopeCenterYInput.setText(y + "");
                    isSettingValues = false;

                } else if (event.getButton() == MouseButton.SECONDARY) {
                    isSettingValues = true;
                    int cx = scope.getCircle().getCenterX();
                    int cy = scope.getCircle().getCenterY();
                    long r = Math.round(Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy)));
                    scopeRadiusInput.setText(r + "");
                    isSettingValues = false;

                }
                checkCircle();
                break;

            default:
                break;
        }
    }

    @FXML
    public void recovery() {
        imageView.setImage(values.getImage());
        values.setUndoImage(values.getCurrentImage());
        values.setCurrentImage(values.getImage());
        setImageChanged(false);
        undoButton.setDisable(false);
        redoButton.setDisable(true);
        loadData();
    }

    @FXML
    public void undoAction() {
        if (values.getUndoImage() == null) {
            undoButton.setDisable(true);
        }
        values.setRedoImage(values.getCurrentImage());
        values.setCurrentImage(values.getUndoImage());
        imageView.setImage(values.getUndoImage());
        setImageChanged(true);
        undoButton.setDisable(true);
        redoButton.setDisable(false);
    }

    @FXML
    public void redoAction() {
        if (values.getRedoImage() == null) {
            redoButton.setDisable(true);
        }
        values.setUndoImage(values.getCurrentImage());
        values.setCurrentImage(values.getRedoImage());
        imageView.setImage(values.getRedoImage());
        setImageChanged(true);
        undoButton.setDisable(false);
        redoButton.setDisable(true);
    }

    @FXML
    public void refAction() {
        values.setRefImage(imageView.getImage());
        values.setRefInfo(null);
        if (!showRefCheck.isSelected()) {
            showRefCheck.setSelected(true);
        } else if (refView != null) {
            refView.setImage(imageView.getImage());
        }
    }

    @FXML
    public void popAction() {
        ImageViewerController controller
                = (ImageViewerController) openStage(CommonValues.ImageViewerFxml, false);
        controller.loadImage(sourceFile, imageView.getImage(), imageInformation, imageData);
    }

    @FXML
    public void clearScope() {
        scope.clearColors();
        scope.clearPoints();
        switch (scope.getScopeType()) {
            case All:
                break;

            case Color:
                indicateColor();
                break;

            case Matting:
                indicateMatting();
                break;

            case Rectangle:

                indicateRectangle();

                break;
            case Circle:

                indicateCircle();

                break;

            default:
                break;
        }
    }

    @Override
    protected void keyEventsHandler(KeyEvent event) {
        super.keyEventsHandler(event);
        if (event.isControlDown()) {
            String key = event.getText();
            if (key == null || key.isEmpty()) {
                return;
            }
            switch (key) {
                case "r":
                case "R":
                    if (!recoverButton.isDisabled()) {
                        recovery();
                    }
                    break;
                case "z":
                case "Z":
                    if (!undoButton.isDisabled()) {
                        undoAction();
                    }
                    break;
                case "y":
                case "Y":
                    if (!redoButton.isDisabled()) {
                        redoAction();
                    }
                    break;
                case "h":
                case "H":
                    if (!hisBox.isDisabled()) {
                        hisBox.show();
                    }
                    break;
                case "x":
                    if (!scopeClearButton.isDisabled()) {
                        clearScope();
                    }
                    break;
                case "f":
                    if (!refButton.isDisabled()) {
                        refAction();
                    }
                    break;
                case "p":
                    if (!popButton.isDisabled()) {
                        popAction();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    protected void showScopePane() {
        try {
            if (showScopeCheck == null || !showScopeCheck.isSelected()
                    || values == null || scope == null) {
                hideScopePane();
                return;
            }

            if (scopePane == null) {
                scopePane = new ScrollPane();
                scopePane.setPannable(true);
                scopePane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                VBox.setVgrow(scopePane, Priority.ALWAYS);
                HBox.setHgrow(scopePane, Priority.ALWAYS);
            }
            if (scopeView == null) {
                scopeView = new ImageView();
                scopeView.setPreserveRatio(true);
                scopeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        scopeViewClicked(event);
                    }
                });
                scopePane.setContent(scopeView);
                Tooltip stips = new Tooltip(getMessage("ScopeImageComments"));
                stips.setFont(new Font(16));
                FxmlTools.quickTooltip(scopeView, stips);
            }
            scopeView.setImage(scope.getImage());

            if (scopeColorSettingBox == null) {
                scopeColorSettingBox = new HBox();
                VBox.setVgrow(scopeColorSettingBox, Priority.NEVER);
                HBox.setHgrow(scopeColorSettingBox, Priority.ALWAYS);
                scopeColorSettingBox.setSpacing(5);
                scopeColorSettingBox.setAlignment(Pos.CENTER_LEFT);
            }

            if (scopeAreaSettingBox == null) {
                scopeAreaSettingBox = new HBox();
                VBox.setVgrow(scopeAreaSettingBox, Priority.NEVER);
                HBox.setHgrow(scopeAreaSettingBox, Priority.ALWAYS);
                scopeAreaSettingBox.setSpacing(5);
                scopeAreaSettingBox.setAlignment(Pos.CENTER_LEFT);
            }

            if (scopeBox == null) {
                scopeBox = new VBox();
                VBox.setVgrow(scopeBox, Priority.ALWAYS);
                HBox.setHgrow(scopeBox, Priority.ALWAYS);
                scopeBox.setSpacing(5);
            }
            scopeBox.getChildren().clear();
            scopeBox.getChildren().add(scopePane);

            if (!splitPane.getItems().contains(scopeBox)) {
                splitPane.getItems().add(0, scopeBox);
            }

            adjustSplitPane();

            if (scopeClearButton == null) {
                scopeClearButton = new Button(getMessage("Clear"));
            }
            scopeClearButton.setOnAction(null);
            if (scopeDeleteButton == null) {
                scopeDeleteButton = new Button(getMessage("Del"));
            }
            scopeDeleteButton.setOnAction(null);
            if (scopeColorExcludedCheck == null) {
                scopeColorExcludedCheck = new CheckBox(getMessage("Excluded"));
                scopeColorExcludedCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable,
                            Boolean oldValue, Boolean newValue) {
                        scope.setColorExcluded(newValue);
                        indicateScope();
                    }
                });
            }
            if (scopeAreaExcludedCheck == null) {
                scopeAreaExcludedCheck = new CheckBox(getMessage("Excluded"));
                scopeAreaExcludedCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable,
                            Boolean oldValue, Boolean newValue) {
                        scope.setAreaExcluded(newValue);
                        indicateScope();
                    }
                });
            }
            if (scopeMatchBox == null) {
                scopeMatchBox = new ComboBox();
                scopeMatchBox.setPrefWidth(100);
                scopeMatchBox.getItems().addAll(Arrays.asList(
                        getMessage("Color"), getMessage("Hue"), getMessage("Red"), getMessage("Green"),
                        getMessage("Blue"), getMessage("Brightness"), getMessage("Saturation")
                ));
                scopeMatchBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {
                        checkMatchType();
                        checkDistanceValue();
                        if (!isSettingValues) {
                            indicateScope();
                        }
                    }
                });

                scopeDistanceInput = new TextField();
                scopeDistanceInput.setPrefWidth(45);
                scopeDistanceInput.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {
                        checkDistanceValue();
                        if (!isSettingValues) {
                            indicateScope();
                        }
                    }
                });

                Tooltip tips = new Tooltip(getMessage("ColorMatchComments"));
                tips.setFont(new Font(16));
                FxmlTools.setComments(scopeMatchBox, tips);
            }

            if (scopeLeftXInput == null) {
                scopeLeftXInput = new TextField();
                scopeLeftXInput.setPrefWidth(70);
                scopeLeftXInput.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {
                        checkRectangle();
                    }
                });
            }
            if (scopeLeftYInput == null) {
                scopeLeftYInput = new TextField();
                scopeLeftYInput.setPrefWidth(70);
                scopeLeftYInput.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {
                        checkRectangle();
                    }
                });
            }
            if (scopeRightXInput == null) {
                scopeRightXInput = new TextField();
                scopeRightXInput.setPrefWidth(70);
                scopeRightXInput.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {
                        checkRectangle();
                    }
                });
            }
            if (scopeRightYInput == null) {
                scopeRightYInput = new TextField();
                scopeRightYInput.setPrefWidth(70);
                scopeRightYInput.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {
                        checkRectangle();
                    }
                });
            }
            if (scopeCenterXInput == null) {
                scopeCenterXInput = new TextField();
                scopeCenterXInput.setPrefWidth(70);
                scopeCenterXInput.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {
                        checkCircle();
                    }
                });
            }
            if (scopeCenterYInput == null) {
                scopeCenterYInput = new TextField();
                scopeCenterYInput.setPrefWidth(70);
                scopeCenterYInput.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {
                        checkCircle();
                    }
                });
            }
            if (scopeRadiusInput == null) {
                scopeRadiusInput = new TextField();
                scopeRadiusInput.setPrefWidth(70);
                scopeRadiusInput.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {
                        checkCircle();
                    }
                });
            }
            if (scopeColorsBox == null) {
                scopeColorsBox = new ComboBox();
                scopeColorsBox.setPrefWidth(80);
                scopeColorsBox.setButtonCell(new ListCell<Color>() {
                    private final Rectangle rectangle;

                    {
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        rectangle = new Rectangle(30, 20);
                    }

                    @Override
                    protected void updateItem(Color item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            rectangle.setFill(item);
                            setGraphic(rectangle);
                        }
                    }
                });
                scopeColorsBox.setCellFactory(new Callback<ListView<Color>, ListCell<Color>>() {
                    @Override
                    public ListCell<Color> call(ListView<Color> p) {
                        ListCell<Color> cell = new ListCell<Color>() {
                            private final Rectangle rectangle;

                            {
                                setContentDisplay(ContentDisplay.LEFT);
                                rectangle = new Rectangle(30, 20);
                            }

                            @Override
                            protected void updateItem(Color item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item == null || empty) {
                                    setGraphic(null);
                                } else {
                                    rectangle.setFill(item);
                                    setGraphic(rectangle);
                                    setText(item.toString());
                                }
                            }
                        };
                        return cell;
                    }
                });
            }

            if (imageLabelBox2 == null) {
                imageLabelBox2 = new HBox();
                VBox.setVgrow(imageLabelBox2, Priority.NEVER);
                HBox.setHgrow(imageLabelBox2, Priority.ALWAYS);
                imageLabelBox2.setSpacing(5);
                imageLabelBox2.setAlignment(Pos.CENTER_LEFT);
                scopePromptLabel = new Label();
                scopePromptLabel.setStyle("-fx-text-fill: #2e598a;-fx-font-weight: bolder;");
                imageLabelBox2.getChildren().add(scopePromptLabel);
                Button b = new Button("");
                b.setVisible(false);
                imageLabelBox2.getChildren().add(b);
            }
            if (imageBox.getChildren().contains(imageLabelBox2)) {
                imageBox.getChildren().remove(imageLabelBox2);
            }
            scopeColorSettingBox.getChildren().clear();
            scopeAreaSettingBox.getChildren().clear();
            switch (scope.getScopeType()) {
                case Matting:
                    scopeBox.getChildren().add(0, scopeColorSettingBox);
                    if (scopePointsBox == null) {
                        scopePointsBox = new ComboBox();
                        scopePointsBox.setPrefWidth(80);
                    }
                    scopePointsBox.getItems().clear();
                    scopeColorSettingBox.getChildren().add(new Label(getMessage("Points")));
                    scopeColorSettingBox.getChildren().add(scopePointsBox);
                    scopeColorSettingBox.getChildren().add(scopeDeleteButton);
                    scopeDeleteButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            int index = scopePointsBox.getSelectionModel().getSelectedIndex();
                            if (index >= 0) {
                                isSettingValues = true;
                                scope.getPoints().remove(index);
                                scopePointsBox.getItems().remove(index);
                                int size = scopePointsBox.getItems().size();
                                if (size > 0) {
                                    if (index > size - 1) {
                                        scopePointsBox.getSelectionModel().select(index - 1);
                                    } else {
                                        scopePointsBox.getSelectionModel().select(index);
                                    }
                                }
                                isSettingValues = false;
                                indicateMatting();
                            }
                        }
                    });
                    scopeColorSettingBox.getChildren().add(scopeClearButton);
                    scopeClearButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            isSettingValues = true;
                            scope.getPoints().clear();
                            scopePointsBox.getItems().clear();
                            isSettingValues = false;
                            indicateMatting();
                        }
                    });
                    scopeColorSettingBox.getChildren().add(new Label(getMessage("Match")));
                    scopeColorSettingBox.getChildren().add(scopeMatchBox);
                    scopeColorSettingBox.getChildren().add(new Label(getMessage("Distance")));
                    scopeColorSettingBox.getChildren().add(scopeDistanceInput);
                    scopeColorSettingBox.getChildren().add(scopeColorExcludedCheck);
                    isSettingValues = true;
                    scopeMatchBox.getSelectionModel().select(0);
                    scopeDistanceInput.setText("50");
                    isSettingValues = false;
                    indicateMatting();
                    break;

                case Rectangle:
                    scopeBox.getChildren().add(0, scopeAreaSettingBox);
                    scopeAreaSettingBox.getChildren().add(new Label(getMessage("LeftTop")));
                    scopeAreaSettingBox.getChildren().add(scopeLeftXInput);
                    scopeAreaSettingBox.getChildren().add(scopeLeftYInput);
                    scopeAreaSettingBox.getChildren().add(new Label(getMessage("RightBottom")));
                    scopeAreaSettingBox.getChildren().add(scopeRightXInput);
                    scopeAreaSettingBox.getChildren().add(scopeRightYInput);
                    scopeAreaSettingBox.getChildren().add(scopeAreaExcludedCheck);
                    isSettingValues = true;
                    scopeLeftXInput.setText((int) (values.getCurrentImage().getWidth() / 4) + "");
                    scopeLeftYInput.setText((int) (values.getCurrentImage().getHeight() / 4) + "");
                    scopeRightXInput.setText((int) (values.getCurrentImage().getWidth() * 3 / 4) + "");
                    scopeRightYInput.setText((int) (values.getCurrentImage().getHeight() * 3 / 4) + "");
                    isSettingValues = false;
                    checkRectangle();
                    break;

                case Circle:
                    scopeBox.getChildren().add(0, scopeAreaSettingBox);
                    scopeAreaSettingBox.getChildren().add(new Label(getMessage("Center")));
                    scopeAreaSettingBox.getChildren().add(scopeCenterXInput);
                    scopeAreaSettingBox.getChildren().add(scopeCenterYInput);
                    scopeAreaSettingBox.getChildren().add(new Label(getMessage("Radius")));
                    scopeAreaSettingBox.getChildren().add(scopeRadiusInput);
                    scopeAreaSettingBox.getChildren().add(scopeAreaExcludedCheck);
                    isSettingValues = true;
                    scopeCenterXInput.setText((int) (values.getCurrentImage().getWidth() / 2) + "");
                    scopeCenterYInput.setText((int) (values.getCurrentImage().getHeight() / 2) + "");
                    scopeRadiusInput.setText((int) (values.getCurrentImage().getWidth() / 4) + "");
                    isSettingValues = false;
                    checkCircle();
                    break;

                case Color:
                    scopeBox.getChildren().add(0, scopeColorSettingBox);
                    scopeColorsBox.getItems().clear();
                    scopeColorSettingBox.getChildren().add(new Label(getMessage("Colors")));
                    scopeColorSettingBox.getChildren().add(scopeColorsBox);
                    scopeColorSettingBox.getChildren().add(scopeDeleteButton);
                    scopeDeleteButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            int index = scopeColorsBox.getSelectionModel().getSelectedIndex();
                            if (index >= 0) {
                                isSettingValues = true;
                                scope.getColors().remove(index);
                                scopeColorsBox.getItems().remove(index);
                                int size = scopeColorsBox.getItems().size();
                                if (size > 0) {
                                    if (index > size - 1) {
                                        scopeColorsBox.getSelectionModel().select(index - 1);
                                    } else {
                                        scopeColorsBox.getSelectionModel().select(index);
                                    }
                                }
                                isSettingValues = false;
                                indicateColor();
                            }
                        }
                    });
                    scopeColorSettingBox.getChildren().add(scopeClearButton);
                    scopeClearButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            isSettingValues = true;
                            scope.getColors().clear();
                            scopeColorsBox.getItems().clear();
                            isSettingValues = false;
                            indicateColor();
                        }
                    });
                    scopeColorSettingBox.getChildren().add(new Label(getMessage("Match")));
                    scopeColorSettingBox.getChildren().add(scopeMatchBox);
                    scopeColorSettingBox.getChildren().add(new Label(getMessage("Distance")));
                    scopeColorSettingBox.getChildren().add(scopeDistanceInput);
                    scopeColorSettingBox.getChildren().add(scopeColorExcludedCheck);
                    isSettingValues = true;
                    scopeMatchBox.getSelectionModel().select(0);
                    scopeDistanceInput.setText("50");
                    isSettingValues = false;
                    indicateColor();
                    break;

                case RectangleColor:
                    scopeBox.getChildren().add(0, scopeColorSettingBox);
                    scopeColorsBox.getItems().clear();
                    scopeColorSettingBox.getChildren().add(new Label(getMessage("Colors")));
                    scopeColorSettingBox.getChildren().add(scopeColorsBox);
                    scopeColorSettingBox.getChildren().add(scopeDeleteButton);
                    scopeDeleteButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            int index = scopeColorsBox.getSelectionModel().getSelectedIndex();
                            if (index >= 0) {
                                isSettingValues = true;
                                scope.getColors().remove(index);
                                scopeColorsBox.getItems().remove(index);
                                int size = scopeColorsBox.getItems().size();
                                if (size > 0) {
                                    if (index > size - 1) {
                                        scopeColorsBox.getSelectionModel().select(index - 1);
                                    } else {
                                        scopeColorsBox.getSelectionModel().select(index);
                                    }
                                }
                                isSettingValues = false;
                                indicateRectangle();
                            }
                        }
                    });
                    scopeColorSettingBox.getChildren().add(scopeClearButton);
                    scopeClearButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            isSettingValues = true;
                            scope.getColors().clear();
                            scopeColorsBox.getItems().clear();
                            isSettingValues = false;
                            indicateRectangle();
                        }
                    });
                    scopeColorSettingBox.getChildren().add(new Label(getMessage("Match")));
                    scopeColorSettingBox.getChildren().add(scopeMatchBox);
                    scopeColorSettingBox.getChildren().add(new Label(getMessage("Distance")));
                    scopeColorSettingBox.getChildren().add(scopeDistanceInput);
                    scopeColorSettingBox.getChildren().add(scopeColorExcludedCheck);

                    scopeBox.getChildren().add(0, scopeAreaSettingBox);
                    scopeAreaSettingBox.getChildren().add(new Label(getMessage("LeftTop")));
                    scopeAreaSettingBox.getChildren().add(scopeLeftXInput);
                    scopeAreaSettingBox.getChildren().add(scopeLeftYInput);
                    scopeAreaSettingBox.getChildren().add(new Label(getMessage("RightBottom")));
                    scopeAreaSettingBox.getChildren().add(scopeRightXInput);
                    scopeAreaSettingBox.getChildren().add(scopeRightYInput);
                    scopeAreaSettingBox.getChildren().add(scopeAreaExcludedCheck);

                    imageBox.getChildren().add(1, imageLabelBox2);

                    isSettingValues = true;
                    scopeMatchBox.getSelectionModel().select(0);
                    scopeDistanceInput.setText("50");
                    scopeLeftXInput.setText((int) (values.getCurrentImage().getWidth() / 4) + "");
                    scopeLeftYInput.setText((int) (values.getCurrentImage().getHeight() / 4) + "");
                    scopeRightXInput.setText((int) (values.getCurrentImage().getWidth() * 3 / 4) + "");
                    scopeRightYInput.setText((int) (values.getCurrentImage().getHeight() * 3 / 4) + "");
                    isSettingValues = false;
                    checkRectangle();
                    break;

                case CircleColor:
                    scopeBox.getChildren().add(0, scopeColorSettingBox);
                    scopeColorsBox.getItems().clear();
                    scopeColorSettingBox.getChildren().add(new Label(getMessage("Colors")));
                    scopeColorSettingBox.getChildren().add(scopeColorsBox);
                    scopeColorSettingBox.getChildren().add(scopeDeleteButton);
                    scopeDeleteButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            int index = scopeColorsBox.getSelectionModel().getSelectedIndex();
                            if (index >= 0) {
                                isSettingValues = true;
                                scope.getColors().remove(index);
                                scopeColorsBox.getItems().remove(index);
                                int size = scopeColorsBox.getItems().size();
                                if (size > 0) {
                                    if (index > size - 1) {
                                        scopeColorsBox.getSelectionModel().select(index - 1);
                                    } else {
                                        scopeColorsBox.getSelectionModel().select(index);
                                    }
                                }
                                isSettingValues = false;
                                indicateCircle();
                            }
                        }
                    });
                    scopeColorSettingBox.getChildren().add(scopeClearButton);
                    scopeClearButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            isSettingValues = true;
                            scope.getColors().clear();
                            scopeColorsBox.getItems().clear();
                            isSettingValues = false;
                            indicateCircle();
                        }
                    });
                    scopeColorSettingBox.getChildren().add(new Label(getMessage("Match")));
                    scopeColorSettingBox.getChildren().add(scopeMatchBox);
                    scopeColorSettingBox.getChildren().add(new Label(getMessage("Distance")));
                    scopeColorSettingBox.getChildren().add(scopeDistanceInput);
                    scopeColorSettingBox.getChildren().add(scopeColorExcludedCheck);

                    scopeBox.getChildren().add(0, scopeAreaSettingBox);
                    scopeAreaSettingBox.getChildren().add(new Label(getMessage("Center")));
                    scopeAreaSettingBox.getChildren().add(scopeCenterXInput);
                    scopeAreaSettingBox.getChildren().add(scopeCenterYInput);
                    scopeAreaSettingBox.getChildren().add(new Label(getMessage("Radius")));
                    scopeAreaSettingBox.getChildren().add(scopeRadiusInput);
                    scopeAreaSettingBox.getChildren().add(scopeAreaExcludedCheck);

                    imageBox.getChildren().add(1, imageLabelBox2);

                    isSettingValues = true;
                    scopeMatchBox.getSelectionModel().select(0);
                    scopeDistanceInput.setText("50");
                    scopeCenterXInput.setText((int) (values.getCurrentImage().getWidth() / 2) + "");
                    scopeCenterYInput.setText((int) (values.getCurrentImage().getHeight() / 2) + "");
                    scopeRadiusInput.setText((int) (values.getCurrentImage().getWidth() / 4) + "");
                    isSettingValues = false;
                    checkCircle();
                    break;

                default:
                    hideScopePane();
            }

        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    protected void showLabels() {
        try {
            if (values == null || scope == null) {
                return;
            }

            imageLabelBox.setAlignment(Pos.CENTER_LEFT);
            imageLabel.setStyle("-fx-text-fill: #2e598a;-fx-font-weight: bolder;");

            switch (scope.getScopeType()) {
                case All:
                    if (isSettingColor) {
                        imageLabel.setText(getMessage("ClickCurrentForNewColor"));
                    } else {
                        imageLabelBox.setAlignment(Pos.CENTER);
                        imageLabel.setStyle("-fx-text-fill: #2e598a;");
                        imageLabel.setText(getMessage("CurrentImage"));
                    }
                    promptLabel.setText("");
                    break;
                case Matting:
                    if (isSettingColor) {
                        promptLabel.setText(getMessage("MattingComments2"));
                        imageLabel.setText(getMessage("ClickCurrentForColors"));
                    } else {
                        promptLabel.setText(getMessage("MattingComments"));
                        imageLabel.setText(getMessage("BothImagesCanClicked"));
                    }
                    break;

                case Rectangle:
                    if (isSettingColor) {
                        promptLabel.setText(getMessage("RectangleLabel"));
                        imageLabel.setText(getMessage("ClickCurrentForNewColor"));
                    } else {
                        promptLabel.setText(getMessage("RectangleLabel"));
                        imageLabel.setText(getMessage("BothImagesCanClicked"));
                    }
                    break;

                case Circle:
                    if (isSettingColor) {
                        promptLabel.setText(getMessage("CircleLabel"));
                        imageLabel.setText(getMessage("ClickCurrentForNewColor"));
                    } else {
                        promptLabel.setText(getMessage("CircleLabel"));
                        imageLabel.setText(getMessage("BothImagesCanClicked"));
                    }
                    break;

                case Color:
                    if (isSettingColor) {
                        promptLabel.setText(getMessage("ClickScopeForColorMatch"));
                        imageLabel.setText(getMessage("ClickCurrentForColors"));
                    } else {
                        promptLabel.setText(getMessage("ClickImagesPickColors"));
                        imageLabel.setText(getMessage("BothImagesCanClicked"));
                    }
                    break;

                case RectangleColor:
                    if (isSettingColor) {
                        promptLabel.setText("");
                        imageLabel.setText(getMessage("ClickScopeForRectangle"));
                        scopePromptLabel.setText(getMessage("ClickCurrentForColors"));
                    } else {
                        promptLabel.setText("");
                        imageLabel.setText(getMessage("ClickScopeForRectangle"));
                        scopePromptLabel.setText(getMessage("ClickCurrentForColorMatch"));
                    }
                    break;

                case CircleColor:
                    if (isSettingColor) {
                        promptLabel.setText("");
                        imageLabel.setText(getMessage("ClickScopeForCircle"));
                        scopePromptLabel.setText(getMessage("ClickCurrentForColors"));
                    } else {
                        promptLabel.setText("");
                        imageLabel.setText(getMessage("ClickScopeForCircle"));
                        scopePromptLabel.setText(getMessage("ClickCurrentForColorMatch"));
                    }
                    break;
            }

        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    protected void hideScopePane() {
        try {
            if (showScopeCheck != null) {
                showScopeCheck.setSelected(false);
            }
            if (scopeBox != null && splitPane.getItems().contains(scopeBox)) {
                splitPane.getItems().remove(scopeBox);
            }

            if (imageLabelBox2 != null && imageBox.getChildren().contains(imageLabelBox2)) {
                imageBox.getChildren().remove(imageLabelBox2);
            }
            adjustSplitPane();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    public boolean stageReloading() {
        if (isSwitchingTab) {
            return true;
        }
        return checkSavingBeforeExit();
    }

    @Override
    public boolean stageClosing() {
        if (!checkSavingBeforeExit()) {
            return false;
        }
        return super.stageClosing();
    }

    public boolean checkSavingBeforeExit() {
        if (values != null && values.isImageChanged()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(getMyStage().getTitle());
            alert.setContentText(AppVaribles.getMessage("ImageChanged"));
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            ButtonType buttonSave = new ButtonType(AppVaribles.getMessage("Save"));
            ButtonType buttonSaveAs = new ButtonType(AppVaribles.getMessage("SaveAs"));
            ButtonType buttonNotSave = new ButtonType(AppVaribles.getMessage("NotSave"));
            ButtonType buttonCancel = new ButtonType(AppVaribles.getMessage("Cancel"));
            alert.getButtonTypes().setAll(buttonSave, buttonSaveAs, buttonNotSave, buttonCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonSave) {
                saveAction();
                return true;
            } else if (result.get() == buttonNotSave) {
                return true;
            } else if (result.get() == buttonSaveAs) {
                saveAsAction();
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public String getInitTab() {
        return initTab;
    }

    public void setInitTab(String initTab) {
        this.initTab = initTab;
    }

    public ImageManufactureValues getValues() {
        return values;
    }

    public void setValues(final ImageManufactureValues values) {
        this.values = values;
    }

}
