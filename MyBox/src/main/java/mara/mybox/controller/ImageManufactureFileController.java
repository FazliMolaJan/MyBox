package mara.mybox.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import static mara.mybox.value.AppVaribles.logger;
import mara.mybox.value.AppVaribles;
import mara.mybox.image.ImageScope;

/**
 * @Author Mara
 * @CreateDate 2018-10-12
 * @Description
 * @License Apache License Version 2.0
 */
public class ImageManufactureFileController extends ImageManufactureController {

    final protected String ImageSaveAsKey, ImageSaveConfirmKey;

    @FXML
    protected ToolBar saveAsBar;
    @FXML
    protected ToggleGroup saveAsGroup;
    @FXML
    protected RadioButton loadRadio, openRadio, justRadio;

    public static class SaveAsType {

        public static int Load = 0;
        public static int Open = 1;
        public static int None = 2;
    }

    public ImageManufactureFileController() {
        ImageSaveAsKey = "ImageSaveAsKey";
        ImageSaveConfirmKey = "ImageSaveConfirmKey";
    }

    @Override
    protected void initializeNext2() {
        try {
            initCommon();
            initFileTab();
            values.setScope(new ImageScope());
            imageView.requestFocus();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    protected void initFileTab() {
        try {
            fileBar.setDisable(true);
            saveAsBar.setDisable(true);

            saveAsGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> ov,
                        Toggle old_toggle, Toggle new_toggle) {
                    checkSaveAsType();
                }
            });
            try {
                int vv = AppVaribles.getUserConfigInt(ImageSaveAsKey, SaveAsType.Load);
                values.setSaveAsType(vv);
                if (vv == SaveAsType.Load) {
                    loadRadio.setSelected(true);
                } else if (vv == SaveAsType.Open) {
                    openRadio.setSelected(true);
                } else if (vv == SaveAsType.None) {
                    justRadio.setSelected(true);
                }
            } catch (Exception e) {
                logger.error(e.toString());
            }

            saveCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                    AppVaribles.setUserConfigValue(ImageSaveConfirmKey, saveCheck.isSelected());
                    values.setIsConfirmBeforeSave(saveCheck.isSelected());
                }
            });
            saveCheck.setSelected(AppVaribles.getUserConfigBoolean(ImageSaveConfirmKey));
            values.setIsConfirmBeforeSave(saveCheck.isSelected());

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void checkSaveAsType() {
        try {
            RadioButton selected = (RadioButton) saveAsGroup.getSelectedToggle();
            if (AppVaribles.getMessage("LoadAfterSaveAs").equals(selected.getText())) {
                AppVaribles.setUserConfigValue(ImageSaveAsKey, SaveAsType.Load + "");
                values.setSaveAsType(SaveAsType.Load);

            } else if (AppVaribles.getMessage("OpenAfterSaveAs").equals(selected.getText())) {
                AppVaribles.setUserConfigValue(ImageSaveAsKey, SaveAsType.Open + "");
                values.setSaveAsType(SaveAsType.Open);

            } else if (AppVaribles.getMessage("JustSaveAs").equals(selected.getText())) {
                AppVaribles.setUserConfigValue(ImageSaveAsKey, SaveAsType.None + "");
                values.setSaveAsType(SaveAsType.None);
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    protected void initInterface() {
        try {
            if (values == null || values.getImage() == null) {
                return;
            }
            super.initInterface();

            isSettingValues = true;
            fileBar.setDisable(false);
            saveAsBar.setDisable(false);
            infoButton.setDisable(imageInformation == null);
            metaButton.setDisable(imageInformation == null);
            isSettingValues = false;
        } catch (Exception e) {
            logger.debug(e.toString());
        }

    }

    @Override
    protected void afterInfoLoaded() {
        super.afterInfoLoaded();
        fileBar.setDisable(false);
        saveCheck.setDisable(true);
    }

    @Override
    public void afterImageLoaded() {
        try {
            super.afterImageLoaded();
            saveCheck.setDisable(false);

        } catch (Exception e) {
            logger.debug(e.toString());
        }
    }

}
