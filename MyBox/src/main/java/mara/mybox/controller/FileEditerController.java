package mara.mybox.controller;

import java.io.File;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mara.mybox.data.FileEditInformation;
import mara.mybox.data.FileEditInformation.Edit_Type;
import mara.mybox.data.FileEditInformation.Filter_Type;
import mara.mybox.data.FileEditInformation.Line_Break;
import mara.mybox.data.VisitHistory;
import mara.mybox.fxml.FxmlControl;
import static mara.mybox.fxml.FxmlControl.badStyle;
import mara.mybox.tools.FileTools;
import mara.mybox.tools.StringTools;
import mara.mybox.tools.TextTools;
import mara.mybox.value.AppVariables;
import static mara.mybox.value.AppVariables.logger;
import static mara.mybox.value.AppVariables.message;
import mara.mybox.value.CommonImageValues;
import mara.mybox.value.CommonValues;

/**
 * @Author Mara
 * @CreateDate 2018-12-09
 * @Description
 * @License Apache License Version 2.0
 */
public abstract class FileEditerController extends BaseController {

    protected Edit_Type editType;
    protected String DisplayKey, PageSizeKey;
    protected String BytesLineBreakKey, LineBreakWidthKey, LineBreakValueKey, BytesCharsetKey;
    protected int currentFound, currentPageTmp, lineLocation, objectLocation;
    protected long pageSize, currentPage;
    protected SimpleBooleanProperty fileChanged;
    protected boolean findWhole, charsetByUser;
    protected FileEditInformation sourceInformation, targetInformation;
    protected TextArea displayArea;
    protected String filterConditions = "";
    protected Filter_Type filterType;
    protected Line_Break lineBreak;
    protected int lineBreakWidth;
    protected String lineBreakValue;
    protected String[] filterStrings;

    protected enum Action {
        None, FindFirst, FindNext, FindPrevious, FindLast, Replace, ReplaceAll, Filter,
        SetPageSize, NextPage, PreviousPage, FirstPage, LastPage, GoPage
    }

    @FXML
    protected AnchorPane mainPane;
    @FXML
    protected TextArea mainArea, lineArea;
    @FXML
    protected ComboBox<String> currentBox, targetBox;
    @FXML
    protected ToggleGroup saveAsGroup, filterGroup, lineBreakGroup, findGroup;
    @FXML
    protected CheckBox displayCheck, targetBomCheck, confirmCheck, scrollCheck, filterLineNumberCheck;
    @FXML
    protected SplitPane splitPane;
    @FXML
    protected Label editLabel, bomLabel, pageLabel, charsetLabel, selectionLabel;
    @FXML
    protected Button charactersButton, linesButton,
            findFirstButton, findPreviousButton, findNextButton, findLastButton, countButton,
            replaceButton, replaceAllButton, filterButton,
            locateObjectButton, locateLineButton;
    @FXML
    protected TextField fromInput, toInput, pageSizeInput, pageInput, filterInput, findInput, replaceInput,
            currentLineBreak, objectNumberInput, lineInput;
    @FXML
    protected ToolBar pageBar;
    @FXML
    protected RadioButton crlfRadio, lfRadio, crRadio, wholeFileRadio, currentPageRadio;
    @FXML
    protected HBox saveAsBox, findBox, filterBox, filterTypesBox;
    @FXML
    protected Tab inputTab, lbTab;
    @FXML
    protected TabPane tabPane;

    public FileEditerController() {
        baseTitle = AppVariables.message("FileEditer");
//        setTextType();
//        logger.debug(editType);
    }

    public FileEditerController(Edit_Type editType) {
        baseTitle = AppVariables.message("FileEditer");
        if (editType == Edit_Type.Text) {
            setTextType();
        } else if (editType == Edit_Type.Bytes) {
            setBytesType();
        }
    }

    public final void setTextType() {
        editType = Edit_Type.Text;

        SourceFileType = VisitHistory.FileType.Text;
        SourcePathType = VisitHistory.FileType.Text;
        TargetPathType = VisitHistory.FileType.Text;
        TargetFileType = VisitHistory.FileType.Text;
        AddFileType = VisitHistory.FileType.Text;
        AddPathType = VisitHistory.FileType.Text;

        sourcePathKey = "TextFilePath";
        DisplayKey = "TextEditerDisplayHex";
        PageSizeKey = "TextPageSize";

        sourceExtensionFilter = CommonImageValues.TextExtensionFilter;
        targetExtensionFilter = sourceExtensionFilter;

    }

    public final void setBytesType() {
        editType = Edit_Type.Bytes;

        SourceFileType = VisitHistory.FileType.Bytes;
        SourcePathType = VisitHistory.FileType.Bytes;
        TargetPathType = VisitHistory.FileType.Bytes;
        TargetFileType = VisitHistory.FileType.Bytes;
        AddFileType = VisitHistory.FileType.Bytes;
        AddPathType = VisitHistory.FileType.Bytes;

        sourcePathKey = "ByteFilePath";
        DisplayKey = "BytesEditerDisplayText";
        PageSizeKey = "BytesPageSize";

        BytesLineBreakKey = "BytesLineBreakKey";
        LineBreakWidthKey = "LineBreakWidthKey";
        LineBreakValueKey = "LineBreakValueKey";
        BytesCharsetKey = "BytesCharsetKey";

        sourceExtensionFilter = CommonImageValues.AllExtensionFilter;
        targetExtensionFilter = sourceExtensionFilter;
    }

    @Override
    public void initializeNext() {
        try {
            initPage(null);

            initFileTab();
            initEditBar();
            initLineBreakTab();
            initLocateTab();
            initDisplayTab();
            initFilterTab();
            initReplaceTab();
            initPageinateTab();
            initMainArea();
            initPageBar();

        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    @Override
    public void afterSceneLoaded() {
        super.afterSceneLoaded();
        if (okButton != null) {
            FxmlControl.setTooltip(okButton, new Tooltip(message("OK") + "\nF1 / CTRL+g"));
        }

    }

    protected void initEditBar() {
        try {

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void keyEventsHandler(KeyEvent event) {
        super.keyEventsHandler(event);
        switch (event.getCode()) {
            case PAGE_UP:
                if (!findPreviousButton.isDisabled()) {
                    findPreviousAction();
                }
                return;
            case PAGE_DOWN:
                if (!findNextButton.isDisabled()) {
                    findNextAction();
                }
                return;
        }

        if (event.isControlDown()) {
            switch (event.getCode()) {
                case HOME:
                    if (!findFirstButton.isDisabled()) {
                        findFirstAction();
                    }
                    return;
                case END:
                    if (!findLastButton.isDisabled()) {
                        findLastAction();
                    }
                    return;
            }
            String key = event.getText();
            if (key == null || key.isEmpty()) {
                return;
            }
            switch (key) {
                case "r":
                case "R":
                    if (mainArea.isFocused() && !recoverButton.isDisabled()) {
                        recoverAction();
                    }
                    break;
//                case "e":
//                case "E":
//                    if (!replaceButton.isDisabled()) {
//                        replaceAction();
//                    }
//                    break;
                default:
                    break;
            }
        }
    }

    protected void initPage(File file) {
        try {
            if (task != null && task.isRunning()) {
                task.cancel();
                task = null;
            }
            if (backgroundTask != null && backgroundTask.isRunning()) {
                backgroundTask.cancel();
                backgroundTask = null;
            }

            isSettingValues = true;
            fileChanged = new SimpleBooleanProperty(false);
            long fileCurrentFound = -1;
            String findString = null;
            if (sourceFile != null && sourceFile == file) {
                fileCurrentFound = sourceInformation.getCurrentFound();
                findString = sourceInformation.getFindString();
                currentPage = sourceInformation.getCurrentPage();
            } else {
                currentPage = 1;
                currentFound = -1;
            }

            sourceFile = file;
            sourceInformation = FileEditInformation.newEditInformation(editType, file);
            sourceInformation.setPageSize(AppVariables.getUserConfigInt(PageSizeKey, 100000));
            sourceInformation.setCurrentPage(currentPage);
            if (fileCurrentFound >= 0) {
                sourceInformation.setCurrentFound(fileCurrentFound);
            }

            sourceInformation.setFindString(findString);
            targetInformation = FileEditInformation.newEditInformation(editType);
            mainArea.clear();
            if (displayArea != null) {
                displayArea.clear();
            }

            lineArea.clear();
            bottomLabel.setText("");
            selectionLabel.setText("");
            pageBar.setDisable(true);
            pageInput.setText("");
            pageInput.setStyle(null);
            pageLabel.setText("");

            if (bomLabel != null) {
                bomLabel.setText("");
            }
            if (infoButton != null) {
                infoButton.setDisable(true);
            }
            if (findBox != null) {
                findBox.setDisable(false);
            }
            if (filterBox != null) {
                filterBox.setDisable(false);
            }
            if (recoverButton != null) {
                recoverButton.setDisable(file == null);
            }

            if (currentLineBreak != null) {
                switch (System.lineSeparator()) {
                    case "\r\n":
                        crlfRadio.fire();
                        currentLineBreak.setText("CRLF");
                        sourceInformation.setLineBreak(Line_Break.CRLF);
                        break;
                    case "\r":
                        crRadio.fire();
                        currentLineBreak.setText("CR");
                        sourceInformation.setLineBreak(Line_Break.CR);
                        break;
                    default:
                        lfRadio.fire();
                        currentLineBreak.setText("LF");
                        sourceInformation.setLineBreak(Line_Break.LF);
                        break;
                }
                sourceInformation.setLineBreakValue(TextTools.lineBreakValue(sourceInformation.getLineBreak()));
                targetInformation.setLineBreak(sourceInformation.getLineBreak());
                targetInformation.setLineBreakValue(sourceInformation.getLineBreakValue());
            }

            if (currentBox != null) {
                if (charsetByUser) {
                    sourceInformation.setCharset(Charset.forName(currentBox.getSelectionModel().getSelectedItem()));
                    if (targetBox == null) {
                        targetInformation.setCharset(sourceInformation.getCharset());
                    }
                } else {
                    currentBox.getSelectionModel().select(Charset.defaultCharset().name());
                    currentBox.setDisable(false);
                    if (targetBox != null) {
                        targetBox.getSelectionModel().select(Charset.defaultCharset().name());
                    } else {
                        targetInformation.setCharset(Charset.defaultCharset());
                    }
                }
            }
            if (wholeFileRadio != null) {
                if (file == null) {
                    wholeFileRadio.setDisable(true);
                    currentPageRadio.fire();
                } else {
                    wholeFileRadio.setDisable(false);
                }
            }
            isSettingValues = false;

            mainArea.requestFocus();

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    protected void initFileTab() {
        try {
            confirmCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov,
                        Boolean oldValue, Boolean newValue) {
                    AppVariables.setUserConfigValue("TextEditerConfirmSave", newValue);
                }
            });
            confirmCheck.setSelected(AppVariables.getUserConfigBoolean("TextEditerConfirmSave", true));

            saveAsGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> ov,
                        Toggle old_toggle, Toggle new_toggle) {
                    checkSaveAsType();
                }
            });

            checkSaveAsType();

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    protected void checkSaveAsType() {
        try {
            RadioButton selected = (RadioButton) saveAsGroup.getSelectedToggle();
            if (AppVariables.message("LoadAfterSaveAs").equals(selected.getText())) {
                saveAsType = SaveAsType.Load;
            } else if (AppVariables.message("OpenAfterSaveAs").equals(selected.getText())) {
                saveAsType = SaveAsType.Open;
            } else if (AppVariables.message("JustSaveAs").equals(selected.getText())) {
                saveAsType = SaveAsType.None;
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    protected void initLineBreakTab() {
        try {

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    protected void initDisplayTab() {
        try {
            displayCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue ov, Boolean oldValue, Boolean newValue) {
                    checkDisplay();
                }
            });
            isSettingValues = true;
            displayCheck.setSelected(AppVariables.getUserConfigBoolean(DisplayKey, true));
            isSettingValues = false;
            checkDisplay();

            Tooltip tips = new Tooltip(AppVariables.message("EncodeComments"));
            tips.setFont(new Font(16));
            FxmlControl.setTooltip(currentBox, tips);

            List<String> setNames = TextTools.getCharsetNames();
            currentBox.getItems().addAll(setNames);
            currentBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    changeCurrentCharset();
                }
            });

            scrollCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue ov, Boolean oldValue, Boolean newValue) {
                    if (displayArea != null && newValue) {
                        displayArea.setScrollLeft(mainArea.getScrollLeft());
                        displayArea.setScrollTop(mainArea.getScrollTop());
                    }
                }
            });

        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    protected void changeCurrentCharset() {

    }

    protected void checkDisplay() {
        setDisplayPane();
//        scrollCheck.setDisable(!displayCheck.isSelected());
        AppVariables.setUserConfigValue(DisplayKey, displayCheck.isSelected());
    }

    protected void initFilterTab() {
        try {

            Tooltip tips = new Tooltip(AppVariables.message("SeparateByCommaBlanksInvolved"));
            tips.setFont(new Font(16));
            FxmlControl.setTooltip(filterInput, tips);

            tips = new Tooltip(AppVariables.message("FilterTypesComments"));
            tips.setFont(new Font(16));
            FxmlControl.setTooltip(filterTypesBox, tips);

            filterGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> ov,
                        Toggle old_toggle, Toggle new_toggle) {
                    checkFilterType();
                }
            });
            checkFilterType();

            filterInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    if (!isSettingValues) {
                        checkFilterStrings();
                    }
                }
            });
            checkFilterStrings();

            filterLineNumberCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue ov, Boolean oldValue, Boolean newValue) {
                    AppVariables.setUserConfigValue("FilterRecordLineNumber", filterLineNumberCheck.isSelected());
                }
            });
            filterLineNumberCheck.setSelected(AppVariables.getUserConfigBoolean("FilterRecordLineNumber", true));

        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    protected void initLocateTab() {
        try {

            lineInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    try {
                        int v = Integer.valueOf(lineInput.getText());
                        if (v > 0 && v <= sourceInformation.getLinesNumber()) {
                            lineLocation = v;
                            lineInput.setStyle(null);
                            locateLineButton.setDisable(false);
                        } else {
                            lineInput.setStyle(badStyle);
                            locateLineButton.setDisable(true);
                        }
                    } catch (Exception e) {
                        lineInput.setStyle(badStyle);
                        locateLineButton.setDisable(true);
                    }
                }
            });

            objectNumberInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    try {
                        int v = Integer.valueOf(objectNumberInput.getText());
                        if (v > 0 && v <= sourceInformation.getObjectsNumber()) {
                            objectLocation = v;
                            objectNumberInput.setStyle(null);
                            locateObjectButton.setDisable(false);
                        } else {
                            objectNumberInput.setStyle(badStyle);
                            locateObjectButton.setDisable(true);
                        }
                    } catch (Exception e) {
                        objectNumberInput.setStyle(badStyle);
                        locateObjectButton.setDisable(true);
                    }
                }
            });

        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    protected void checkFilterType() {
        RadioButton selected = (RadioButton) filterGroup.getSelectedToggle();
        if (AppVariables.message("IncludeOne").equals(selected.getText())) {
            filterType = Filter_Type.IncludeOne;
        } else if (AppVariables.message("IncludeAll").equals(selected.getText())) {
            filterType = Filter_Type.IncludeAll;
        } else if (AppVariables.message("NotIncludeAll").equals(selected.getText())) {
            filterType = Filter_Type.NotIncludeAll;
        } else if (AppVariables.message("NotIncludeAny").equals(selected.getText())) {
            filterType = Filter_Type.NotIncludeAny;
        }
    }

    protected void checkFilterStrings() {
        if (editType == Edit_Type.Text) {
            checkTextFilterStrings();
        } else if (editType == Edit_Type.Bytes) {
            checkBytesFilterStrings();
        }
    }

    protected void checkTextFilterStrings() {
        String f = filterInput.getText();
        boolean invalid = f.isEmpty() || sourceFile == null || mainArea.getText().isEmpty();
        if (!invalid) {
            if (f.length() >= sourceInformation.getPageSize()) {
                popError(AppVariables.message("FindStringLimitation"));
                invalid = true;
            } else {
                filterStrings = StringTools.splitByComma(filterInput.getText());
                invalid = filterStrings.length == 0;
            }
        }
        filterButton.setDisable(invalid);
    }

    protected void checkBytesFilterStrings() {

    }

    protected void initReplaceTab() {
        try {

            findGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> ov,
                        Toggle old_toggle, Toggle new_toggle) {
                    checkFindType();
                }
            });
            checkFindType();

            findInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    checkFindInput();
                }
            });

            replaceInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    boolean invalid = !checkReplaceString(newValue);
                    replaceButton.setDisable(invalid);
                    replaceAllButton.setDisable(invalid);
                    if (!invalid) {
                        sourceInformation.setReplaceString(newValue);
                    }
                }
            });

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    protected void checkFindType() {
        if (findGroup == null) {
            return;
        }
        RadioButton selected = (RadioButton) findGroup.getSelectedToggle();
        findWhole = AppVariables.message("WholeFile").equals(selected.getText());
        checkFindInput();
    }

    protected void checkFindInput() {
        String string = findInput.getText().trim();
        boolean invalid = string.isEmpty() || !validateFindString(string);
        findFirstButton.setDisable(invalid);
        findLastButton.setDisable(invalid);
        countButton.setDisable(invalid);
        findPreviousButton.setDisable(true);
        findNextButton.setDisable(true);
        replaceButton.setDisable(badStyle.equals(findInput.getStyle())
                || badStyle.equals(replaceInput.getStyle()));
        replaceAllButton.setDisable(invalid
                || badStyle.equals(findInput.getStyle())
                || badStyle.equals(replaceInput.getStyle()));
        if (!invalid) {
            sourceInformation.setFindString(string);
        }
        currentFound = -1;
        sourceInformation.setCurrentFound(-1);
    }

    protected boolean validateFindString(String string) {
        return true;
    }

    protected boolean checkReplaceString(String string) {
        return true;
    }

    protected void initPageinateTab() {
        try {
            pageSizeInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    checkPageSize();
                }
            });
            pageSize = AppVariables.getUserConfigInt(PageSizeKey, 100000);
            if (pageSize <= 0) {
                pageSize = 100000;
            }
            pageSizeInput.setText(pageSize + "");

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    protected void initMainArea() {
        try {
            mainArea.setStyle("-fx-highlight-fill: dodgerblue; -fx-highlight-text-fill: white;");

            mainArea.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    if (!isSettingValues) {
                        updateInterface(true);
                    }
                }
            });
            mainArea.scrollTopProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue ov, Number oldValue, Number newValue) {
                    if (isSettingValues) {
                        return;
                    }
                    if (displayArea != null && scrollCheck.isSelected()) {
                        isSettingValues = true;
                        displayArea.setScrollTop(newValue.doubleValue());
                        isSettingValues = false;
                    }
                    isSettingValues = true;
                    lineArea.setScrollTop(newValue.doubleValue());
                    isSettingValues = false;
                }
            });
            mainArea.scrollLeftProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue ov, Number oldValue, Number newValue) {
                    if (!isSettingValues && displayArea != null && scrollCheck.isSelected()) {
                        isSettingValues = true;
                        displayArea.setScrollLeft(newValue.doubleValue());
                        isSettingValues = false;
                    }
                }
            });
            mainArea.selectionProperty().addListener(new ChangeListener<IndexRange>() {
                @Override
                public void changed(ObservableValue ov, IndexRange oldValue, IndexRange newValue) {
                    if (!isSettingValues) {
                        setSecondAreaSelection();
                        boolean none = (newValue.getLength() == 0);
                        IndexRange range = mainArea.getSelection();
                        int start, len;
                        if (editType == Edit_Type.Text) {
                            start = range.getStart() + 1;
                            len = range.getLength();
                        } else {
                            start = range.getStart() / 3 + 1;
                            len = range.getLength() / 3;
                        }
                        if (sourceInformation != null && sourceInformation.getCurrentPage() > 1) {
                            start += (sourceInformation.getCurrentPage() - 1) * sourceInformation.getPageSize();
                        }
                        selectionLabel.setText(AppVariables.message("Selection") + ": " + start + "-" + (start + len));

                    }
                }
            });

        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    protected void initPageBar() {
        try {

            pageInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    checkCurrentPage();
                }
            });
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void checkPageSize() {
        try {
            int v = Integer.valueOf(pageSizeInput.getText());
            if (v > 0) {
                pageSize = v;
                pageSizeInput.setStyle(null);
            } else {
                pageSizeInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            pageSizeInput.setStyle(badStyle);
        }
    }

    protected void checkCurrentPage() {
        if (pageBar.isDisabled()) {
            currentPageTmp = 0;
            pageInput.setStyle(null);
            return;
        }
        try {
            int v = Integer.valueOf(pageInput.getText());
            if (v > 0 && v <= sourceInformation.getPagesNumber()) {
                currentPageTmp = v;
                pageInput.setStyle(null);
                goButton.setDisable(false);
            } else {
                pageInput.setStyle(badStyle);
                goButton.setDisable(true);
            }
        } catch (Exception e) {
            pageInput.setStyle(badStyle);
            goButton.setDisable(true);
        }
    }

    protected void setDisplayPane() {
        if (displayCheck.isSelected()) {
            if (displayArea == null) {
                displayArea = new TextArea();
                displayArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                VBox.setVgrow(displayArea, Priority.ALWAYS);
                HBox.setHgrow(displayArea, Priority.ALWAYS);
                displayArea.setStyle("-fx-highlight-fill: black; -fx-highlight-text-fill: palegreen;");
                displayArea.setEditable(false);
                displayArea.scrollTopProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue ov, Number oldValue, Number newValue) {
                        if (!isSettingValues && scrollCheck.isSelected()) {
                            isSettingValues = true;
                            mainArea.setScrollTop(newValue.doubleValue());
                            isSettingValues = false;
                        }
                    }
                });
            }
            if (!splitPane.getItems().contains(displayArea)) {
                splitPane.getItems().add(displayArea);
            }

            setSecondArea(mainArea.getText());

        } else {
            if (displayArea != null && splitPane.getItems().contains(displayArea)) {
                splitPane.getItems().remove(displayArea);
            }
        }

        switch (splitPane.getItems().size()) {
            case 3:
                splitPane.getDividers().get(0).setPosition(0.33333);
                splitPane.getDividers().get(1).setPosition(0.66666);
//                splitPane.setDividerPositions(0.33, 0.33, 0.33); // This way not work!
                break;
            case 2:
                splitPane.getDividers().get(0).setPosition(0.5);
//               splitPane.setDividerPositions(0.5, 0.5); // This way not work!
                break;
            default:
                splitPane.setDividerPositions(1);
                break;
        }
        splitPane.layout();
    }

    protected void setLines(long from, long to) {
        if (isSettingValues) {
            return;
        }
        isSettingValues = true;
        if (from < 0 || to <= 0 || from > to) {
            lineArea.clear();
        } else {
            StringBuilder lines = new StringBuilder();
            for (long i = from; i <= to; i++) {
                lines.append(i).append("\n");
            }
            lineArea.setText(lines.toString());
        }
        isSettingValues = false;
    }

    protected void setSecondArea(String contents) {

    }

    protected void setSecondAreaSelection() {

    }

    @FXML
    @Override
    public void recoverAction() {
        if (!mainArea.isFocused() && !recoverButton.isFocused()) {
            return;
        }
        try {
            if (!recoverButton.isDisabled() && sourceInformation.getFile() != null) {
                loadPage();
            }
        } catch (Exception e) {

        }
    }

    @FXML
    @Override
    public void okAction() {
        if (!checkBeforeNextAction()) {
            return;
        }
        AppVariables.setUserConfigInt(PageSizeKey, (int) pageSize);
        popInformation(AppVariables.message("Saved"), 3000);
        sourceInformation.setPageSize(pageSize);
        sourceInformation.setCurrentPage(1);
        if (sourceInformation.getLineBreak() == Line_Break.Width) {
            sourceInformation.setTotalNumberRead(false);
            openFile(sourceFile);
        } else {
            loadPage();
        }
    }

    @FXML
    protected void findFirstAction() {
        final String areaText = mainArea.getText();
        if (findFirstButton.isDisabled() || areaText.isEmpty()) {
            return;
        }
        final String findString = findInput.getText();
        if (findString.isEmpty()) {
            return;
        }
        final boolean whole = findWhole && (sourceInformation.getPagesNumber() > 1);
        if (whole) {
            if (!checkBeforeNextAction()) {
                return;
            }
        }
        sourceInformation.setFindString(findString);
        currentFound = -1;
        synchronized (this) {
            if (task != null) {
                return;
            }
            task = new SingletonTask<Void>() {

                private String text;

                @Override
                protected boolean handle() {
                    if (whole) {
                        text = sourceInformation.findFirst();
                    } else if (areaText != null) {
                        text = areaText;
                        currentFound = text.indexOf(findString);
                    }
                    return true;
                }

                @Override
                protected void whenSucceeded() {
                    if (text == null || text.isEmpty()) {
                        findFirstButton.setDisable(true);
                    } else {
                        if (whole) {
                            currentPage = sourceInformation.getCurrentPage();
                            isSettingValues = true;
                            mainArea.setText(text);
                            isSettingValues = false;
                            updateInterface(false);
                        }
                    }
                    if (currentFound >= 0) {
                        mainArea.requestFocus();
                        mainArea.deselect();
                        mainArea.selectRange(currentFound, Math.min(mainArea.getText().length(), currentFound + findString.length()));
                        findPreviousButton.setDisable(true);
                        findNextButton.setDisable(false);
                        findLastButton.setDisable(false);
                        countButton.setDisable(false);
                        replaceButton.setDisable(false);
                        replaceAllButton.setDisable(false);
                    } else {
                        popInformation(AppVariables.message("NotFound"));
                        findPreviousButton.setDisable(true);
                        findNextButton.setDisable(true);
                        findLastButton.setDisable(true);
                        countButton.setDisable(true);
                        replaceButton.setDisable(true);
                        replaceAllButton.setDisable(true);
                    }

                }

            };
            openHandlingStage(task, Modality.WINDOW_MODAL);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    protected void findNextAction() {
        final String areaText = mainArea.getText();
        if (findNextButton.isDisabled() || areaText.isEmpty()) {
            return;
        }
        final String findString = findInput.getText();
        if (findString.isEmpty()) {
            return;
        }
        sourceInformation.setFindString(findString);
        final boolean whole = findWhole && (sourceInformation.getPagesNumber() > 1);
        if (whole) {
            if (!checkBeforeNextAction()) {
                return;
            }
            if (sourceInformation.getCurrentFound() < 0) {
                findFirstAction();
                return;
            }
        } else {
            if (currentFound < 0) {
                findFirstAction();
                return;
            }
        }
        synchronized (this) {
            if (task != null) {
                return;
            }
            task = new SingletonTask<Void>() {

                private String text;

                @Override
                protected boolean handle() {
                    if (whole) {
                        text = sourceInformation.findNext();
                    } else if (areaText != null) {
                        text = areaText;
                        currentFound = text.indexOf(findString, currentFound + 1);
                    }
                    return true;
                }

                @Override
                protected void whenSucceeded() {
                    if (text == null || text.isEmpty()) {
                        findNextButton.setDisable(true);
                    } else {
                        if (whole) {
                            isSettingValues = true;
                            mainArea.setText(text);
                            isSettingValues = false;
                            currentPage = sourceInformation.getCurrentPage();
                            updateInterface(false);
                        }
                    }
                    if (currentFound >= 0) {
                        findPreviousButton.setDisable(false);
                        mainArea.deselect();
                        mainArea.selectRange(currentFound, Math.min(mainArea.getText().length(), currentFound + findString.length()));
                        replaceButton.setDisable(false);
                        replaceAllButton.setDisable(false);
                    } else {
                        findNextButton.setDisable(true);
                    }
                }
            };
            openHandlingStage(task, Modality.WINDOW_MODAL);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    protected void findPreviousAction() {
        final String areaText = mainArea.getText();
        if (findPreviousButton.isDisabled() || areaText.isEmpty()) {
            return;
        }
        final String findString = findInput.getText();
        if (findString.isEmpty()) {
            return;
        }
        final boolean whole = findWhole && (sourceInformation.getPagesNumber() > 1);
        if (whole) {
            if (!checkBeforeNextAction()) {
                return;
            }
            if (sourceInformation.getCurrentFound() < 0) {
                findFirstAction();
                return;
            }
        } else {
            if (currentFound < 0) {
                findFirstAction();
                return;
            }
        }
        sourceInformation.setFindString(findString);
        synchronized (this) {
            if (task != null) {
                return;
            }
            task = new SingletonTask<Void>() {
                private String text;

                @Override
                protected boolean handle() {
                    if (whole) {
                        text = sourceInformation.findPrevious();
                    } else if (areaText != null) {
                        text = areaText;
                        text = text.substring(0, currentFound + findString.length() - 1);
                        currentFound = text.lastIndexOf(findString);
                    }

                    return true;
                }

                @Override
                protected void whenSucceeded() {
                    if (text == null || text.isEmpty()) {
                        findPreviousButton.setDisable(true);
                    } else {
                        if (whole) {
                            isSettingValues = true;
                            mainArea.setText(text);
                            isSettingValues = false;
                            currentPage = sourceInformation.getCurrentPage();
                            updateInterface(false);
                        }
                    }
                    if (currentFound >= 0) {
                        findNextButton.setDisable(false);
                        mainArea.deselect();
                        mainArea.selectRange(currentFound, Math.min(mainArea.getText().length(), currentFound + findString.length()));
                        replaceButton.setDisable(false);
                        replaceAllButton.setDisable(false);
                    } else {
                        findPreviousButton.setDisable(true);
                    }

                }
            };
            openHandlingStage(task, Modality.WINDOW_MODAL);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    protected void findLastAction() {
        final String areaText = mainArea.getText();
        if (findLastButton.isDisabled() || areaText.isEmpty()) {
            return;
        }
        final String findString = findInput.getText();
        if (findString.isEmpty()) {
            return;
        }
        final boolean whole = findWhole && (sourceInformation.getPagesNumber() > 1);
        if (whole) {
            if (!checkBeforeNextAction()) {
                return;
            }
        }
        sourceInformation.setFindString(findString);
        synchronized (this) {
            if (task != null) {
                return;
            }
            task = new SingletonTask<Void>() {

                private String text;

                @Override
                protected boolean handle() {
                    if (whole) {
                        text = sourceInformation.findLast();
                    } else if (areaText != null) {
                        text = areaText;
                        currentFound = text.lastIndexOf(findString);
                    }

                    return true;
                }

                @Override
                protected void whenSucceeded() {
                    if (text == null || text.isEmpty()) {
                        findLastButton.setDisable(true);
                    } else {
                        if (whole) {
                            isSettingValues = true;
                            mainArea.setText(text);
                            isSettingValues = false;
                            currentPage = sourceInformation.getCurrentPage();
                            updateInterface(false);
                        }
                    }
                    if (currentFound >= 0) {
                        findPreviousButton.setDisable(false);
                        findNextButton.setDisable(true);
                        findFirstButton.setDisable(false);
                        countButton.setDisable(false);
                        mainArea.deselect();
                        mainArea.selectRange(currentFound, Math.min(mainArea.getText().length(), currentFound + findString.length()));
                        replaceButton.setDisable(false);
                        replaceAllButton.setDisable(false);
                    } else {
                        popInformation(AppVariables.message("NotFound"));
                        findPreviousButton.setDisable(true);
                        findNextButton.setDisable(true);
                        findFirstButton.setDisable(true);
                        countButton.setDisable(true);
                        findLastButton.setDisable(true);
                        replaceButton.setDisable(true);
                        replaceAllButton.setDisable(true);
                    }

                }
            };
            openHandlingStage(task, Modality.WINDOW_MODAL);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    protected void countAction() {
        final String areaText = mainArea.getText();
        if (countButton.isDisabled() || areaText.isEmpty()) {
            return;
        }
        final String findString = findInput.getText();
        if (findString.isEmpty()) {
            return;
        }
        final boolean whole = findWhole && (sourceInformation.getPagesNumber() > 1);
        if (whole) {
            if (!checkBeforeNextAction()) {
                return;
            }
        }
        sourceInformation.setFindString(findString);
        synchronized (this) {
            if (task != null) {
                return;
            }
            task = new SingletonTask<Void>() {

                private int count = 0;

                @Override
                protected boolean handle() {
                    if (whole) {
                        count = sourceInformation.count();
                    } else if (areaText != null) {
                        count = TextTools.countNumber(areaText, findString);
                    }

                    return true;
                }

                @Override
                protected void whenSucceeded() {
                    if (count > 0) {
                        popInformation(MessageFormat.format(AppVariables.message("CountNumber"), count));
                        findFirstButton.setDisable(false);
                        replaceButton.setDisable(false);
                        replaceAllButton.setDisable(false);
                    } else {
                        popInformation(AppVariables.message("NotFound"));
                        findFirstButton.setDisable(true);
                        findPreviousButton.setDisable(true);
                        findNextButton.setDisable(true);
                        findLastButton.setDisable(true);
                        countButton.setDisable(true);
                        replaceButton.setDisable(true);
                        replaceAllButton.setDisable(true);
                    }

                }
            };
            openHandlingStage(task, Modality.WINDOW_MODAL);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    protected void replaceAction() {
        final String text = mainArea.getText();
        if (replaceButton.isDisabled() || text.isEmpty()) {
            return;
        }
        final String findString = findInput.getText();
        if (findString.isEmpty() || currentFound < 0) {
            replaceButton.setDisable(true);
            return;
        }
        final String replaceString = replaceInput.getText();
        sourceInformation.setFindString(findString);
        sourceInformation.setReplaceString(replaceString);
        synchronized (this) {
            if (task != null) {
                return;
            }
            task = new SingletonTask<Void>() {

                private String replaced;

                @Override
                protected boolean handle() {
                    String bstr = text.substring(0, currentFound);
                    String astr = text.substring(currentFound + findString.length(), text.length());
                    replaced = bstr + replaceString + astr;

                    return true;
                }

                @Override
                protected void whenSucceeded() {
                    isSettingValues = true;
                    mainArea.setText(replaced);
                    isSettingValues = false;
                    updateInterface(true);
                    mainArea.deselect();
                    mainArea.selectRange(currentFound, currentFound + replaceString.length());
                    replaceButton.setDisable(true);
                }
            };
            openHandlingStage(task, Modality.WINDOW_MODAL);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    protected void replaceAllAction() {
        final String text = mainArea.getText();
        if (replaceAllButton.isDisabled() || text.isEmpty()) {
            return;
        }
        final String findString = findInput.getText();
        if (findString.isEmpty()) {
            return;
        }
        final String replaceString = replaceInput.getText();
        sourceInformation.setFindString(findString);
        sourceInformation.setReplaceString(replaceString);
        final boolean whole = findWhole && (sourceInformation.getPagesNumber() > 1);
        if (whole) {
            if (!checkBeforeNextAction()) {
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(getMyStage().getTitle());
            alert.setContentText(AppVariables.message("SureReplaceAll"));
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            ButtonType buttonSure = new ButtonType(AppVariables.message("Sure"));
            ButtonType buttonCancel = new ButtonType(AppVariables.message("Cancel"));
            alert.getButtonTypes().setAll(buttonSure, buttonCancel);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            stage.toFront();

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonCancel) {
                return;
            }
        }
        synchronized (this) {
            if (task != null) {
                return;
            }
            task = new SingletonTask<Void>() {

                private String replaced;
                private int num;

                @Override
                protected boolean handle() {
                    if (whole) {
                        num = sourceInformation.replaceAll();
                    } else {
                        num = TextTools.countNumber(text, findString);
                        if (num > 0) {
                            replaced = text.replaceAll(findString, replaceString);
                        }
                    }

                    return true;
                }

                @Override
                protected void whenSucceeded() {
                    if (num > 0) {
                        sourceInformation.setCurrentFound(-1);
                        currentFound = -1;
                        if (whole) {
                            openFile(sourceFile);
                        } else {
                            isSettingValues = true;
                            mainArea.setText(replaced);
                            isSettingValues = false;
                            updateInterface(true);
                        }
                        popInformation(MessageFormat.format(AppVariables.message("ReplaceAllOk"), num));
                    } else {
                        popInformation(AppVariables.message("NotFound"));
                        findPreviousButton.setDisable(true);
                        findNextButton.setDisable(true);
                        findLastButton.setDisable(true);
                        replaceButton.setDisable(true);
                        replaceAllButton.setDisable(true);
                        currentFound = -1;
                    }
                }
            };
            openHandlingStage(task, Modality.WINDOW_MODAL);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    protected void filterAction() {
        if (isSettingValues || filterButton.isDisabled()) {
            return;
        }
        if (!checkBeforeNextAction() || sourceFile == null
                || filterStrings == null || filterStrings.length == 0) {
            return;
        }
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < filterStrings.length; i++) {
//            if (i == 0) {
//                sb.append(filterStrings[i]);
//            } else {
//                sb.append(",").append(filterStrings[i]);
//            }
//        }
//        isSettingValues = true;
//        filterInput.setText(sb.toString());
//        isSettingValues = false;

        sourceInformation.setFilterStrings(filterStrings);
        sourceInformation.setFilterType(filterType);
        final FileFilterController controller = (FileFilterController) openStage(CommonValues.FileFilterFxml);
        if (controller != null) {
            controller.filterFile(sourceInformation, filterConditions, filterLineNumberCheck.isSelected());
        }

    }

    @FXML
    @Override
    public void nextAction() {
        if (!checkBeforeNextAction()) {
            return;
        }
        if (sourceInformation.getObjectsNumber() <= sourceInformation.getCurrentPageObjectEnd()) {
            nextButton.setDisable(true);
        } else {
            sourceInformation.setCurrentPage(sourceInformation.getCurrentPage() + 1);
            currentFound = -1;
            loadPage();
        }
    }

    @FXML
    @Override
    public void previousAction() {
        if (!checkBeforeNextAction()) {
            return;
        }
        if (sourceInformation.getCurrentPage() <= 1) {
            previousButton.setDisable(true);
        } else {
            sourceInformation.setCurrentPage(sourceInformation.getCurrentPage() - 1);
            currentFound = -1;
            loadPage();
        }
    }

    @FXML
    @Override
    public void firstAction() {
        if (!checkBeforeNextAction()) {
            return;
        }
        sourceInformation.setCurrentPage(1);
        currentFound = -1;
        loadPage();
    }

    @FXML
    @Override
    public void lastAction() {
        if (!checkBeforeNextAction()) {
            return;
        }
        sourceInformation.setCurrentPage(sourceInformation.getPagesNumber());
        currentFound = -1;
        loadPage();
    }

    @FXML
    protected void goPageAction() {
        if (!checkBeforeNextAction()) {
            return;
        }
        if (currentPageTmp > sourceInformation.getPagesNumber()) {
            sourceInformation.setCurrentPage(sourceInformation.getPagesNumber());
        } else {
            sourceInformation.setCurrentPage(currentPageTmp);
        }
        loadPage();
    }

    @FXML
    @Override
    public void infoAction() {

    }

    @FXML
    protected void locateLine() {

        sourceInformation.setCurrentLine(-1);
        if (sourceFile == null || sourceInformation.getPageSize() <= 1) {
            String[] lines = mainArea.getText().split("\n");
            if (lineLocation > lines.length) {
                return;
            }
            mainArea.requestFocus();
            mainArea.deselect();
            int index = 0;
            for (int i = 0; i < lineLocation - 1; i++) {
                index += lines[i].length() + 1;
            }
            if (editType == Edit_Type.Bytes) {
                mainArea.selectRange(index, index + 2);
            } else {
                mainArea.selectRange(index, index + 1);
            }

        } else {

            if (lineLocation > sourceInformation.getLinesNumber()) {
                return;
            }
            if (sourceInformation.getCurrentPageLineStart() <= lineLocation
                    && sourceInformation.getCurrentPageLineEnd() >= lineLocation) {
                String[] lines = mainArea.getText().split("\n");
                mainArea.requestFocus();
                mainArea.deselect();
                int index = 0, end = (int) (lineLocation - sourceInformation.getCurrentPageLineStart());
                for (int i = 0; i < end; i++) {
                    index += lines[i].length() + 1;
                }
                if (editType == Edit_Type.Bytes) {
                    mainArea.selectRange(index, index + 2);
                } else {
                    mainArea.selectRange(index, index + 1);
                }

            } else {
                sourceInformation.setCurrentLine(lineLocation);
                checkFindType();
                synchronized (this) {
                    if (task != null) {
                        return;
                    }
                    task = new SingletonTask<Void>() {

                        private String text;

                        @Override
                        protected boolean handle() {
                            text = sourceInformation.locateLine();
                            if (this == null || isCancelled()) {
                                return false;
                            }
                            currentPage = sourceInformation.getCurrentPage();

                            return true;
                        }

                        @Override
                        protected void whenSucceeded() {
                            if (text != null) {
                                isSettingValues = true;
                                mainArea.setText(text);
                                isSettingValues = false;

                                sourceInformation.setCurrentLine(lineLocation);
                                updateInterface(false);
                            } else {
                                popFailed();
                            }
                        }
                    };
                    openHandlingStage(task, Modality.WINDOW_MODAL);
                    Thread thread = new Thread(task);
                    thread.setDaemon(true);
                    thread.start();
                }
            }

        }
    }

    @FXML
    protected void locateObject() {

        if (sourceFile == null || sourceInformation.getPageSize() <= 1) {
            mainArea.requestFocus();
            mainArea.deselect();
            if (editType == Edit_Type.Bytes) {
                int start = (objectLocation - 1) * 3;
                mainArea.selectRange(start, start + 2);
            } else {
                int start = objectLocation - 1;
                mainArea.selectRange(start, start + 1);
            }

        } else {

            if (sourceInformation.getCurrentPageObjectStart() <= objectLocation - 1
                    && sourceInformation.getCurrentPageObjectEnd() >= objectLocation) {
                mainArea.requestFocus();
                mainArea.deselect();
                if (editType == Edit_Type.Bytes) {
                    int pLocation = (int) ((objectLocation - 1) % pageSize) * 3;
                    mainArea.selectRange(pLocation, pLocation + 2);
                } else {
                    int pLocation = (int) ((objectLocation - 1) % pageSize);
                    mainArea.selectRange(pLocation, pLocation + 1);
                }

            } else {
                int page = (int) ((objectLocation - 1) / pageSize) + 1;
                int pLocation = (int) ((objectLocation - 1) % pageSize);
                sourceInformation.setCurrentPage(page);
                if (editType == Edit_Type.Bytes) {
                    sourceInformation.setCurrentPosition(pLocation * 3);
                } else {
                    sourceInformation.setCurrentPosition(pLocation);
                }
                loadPage();
            }

        }
    }

    @Override
    public void sourceFileChanged(final File file) {
        super.sourceFileChanged(file);
        sourceFile = null;
        openFile(file);
    }

    public void openFile(File file) {
        if (editType == Edit_Type.Text) {
            openTextFile(file);
        } else if (editType == Edit_Type.Bytes) {
            openBytesFile(file);
        }
    }

    public void openTextFile(File file) {
        if (file == null) {
            return;
        }

        initPage(file);
        synchronized (this) {
            if (task != null) {
                return;
            }
            bottomLabel.setText(AppVariables.message("CheckingEncoding"));
            task = new SingletonTask<Void>() {

                @Override
                protected boolean handle() {
                    if (sourceInformation == null) {
                        return false;
                    }
                    sourceInformation.setLineBreak(TextTools.checkLineBreak(sourceFile));
                    sourceInformation.setLineBreakValue(TextTools.lineBreakValue(sourceInformation.getLineBreak()));
                    if (charsetByUser) {
                        return true;
                    } else {
                        return sourceInformation.checkCharset();
                    }
                }

                @Override
                protected void whenSucceeded() {
                    bottomLabel.setText("");
                    isSettingValues = true;
                    if (currentLineBreak != null) {
                        currentLineBreak.setText(sourceInformation.getLineBreak().toString());
                        switch (sourceInformation.getLineBreak()) {
                            case CRLF:
                                crlfRadio.fire();
                                break;
                            case CR:
                                crRadio.fire();
                                break;
                            default:
                                lfRadio.fire();
                                break;
                        }
                    }
                    if (currentBox != null) {
                        currentBox.getSelectionModel().select(sourceInformation.getCharset().name());
                        if (targetBox != null) {
                            targetBox.getSelectionModel().select(sourceInformation.getCharset().name());
                        } else {
                            targetInformation.setCharset(sourceInformation.getCharset());
                        }
                        if (sourceInformation.isWithBom()) {
                            currentBox.setDisable(true);
                            bomLabel.setText(AppVariables.message("WithBom"));
                            if (targetBomCheck != null) {
                                targetBomCheck.setSelected(true);
                            }
                        } else {
                            currentBox.setDisable(false);
                            bomLabel.setText("");
                            if (targetBomCheck != null) {
                                targetBomCheck.setSelected(false);
                            }
                        }
                    }
                    if (infoButton != null) {
                        infoButton.setDisable(false);
                    }
                    isSettingValues = false;

                    loadPage();

                }

                @Override
                protected void whenFailed() {
                    bottomLabel.setText("");
                    super.whenFailed();
                }

            };
            openHandlingStage(task, Modality.WINDOW_MODAL);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void openBytesFile(File file) {
        if (file == null) {
            return;
        }
        if (lineBreak == Line_Break.Value && lineBreakValue == null
                || lineBreak == Line_Break.Width && lineBreakWidth <= 0) {
            popError(AppVariables.message("WrongLineBreak"));
            tabPane.getSelectionModel().select(lbTab);
            return;
        }
        initPage(file);
        sourceInformation.setLineBreak(lineBreak);
        sourceInformation.setLineBreakValue(lineBreakValue);
        sourceInformation.setLineBreakWidth(lineBreakWidth);
        loadPage();

    }

    protected void loadTotalNumbers() {
        if (sourceInformation == null || sourceFile == null
                || sourceInformation.isTotalNumberRead()) {
            return;
        }

        synchronized (this) {
            if (backgroundTask != null) {
                backgroundTask.cancel();
            }
            backgroundTask = new Task<Void>() {
                private boolean ok;

                @Override
                protected Void call() {
                    ok = sourceInformation.readTotalNumbers();
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    if (ok) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                updateInterface(false);
                            }
                        });
                    }
                }
            };
            Thread thread = new Thread(backgroundTask);
            thread.setDaemon(true);
            thread.start();
        }
    }

    protected void loadPage() {
        if (sourceInformation == null || sourceFile == null) {
            return;
        }
        synchronized (this) {
            if (task != null) {
                return;
            }
            bottomLabel.setText(AppVariables.message("ReadingFile"));
            checkFindType();
            task = new SingletonTask<Void>() {

                private String text;

                @Override
                protected boolean handle() {
                    text = sourceInformation.readPage();

                    return true;
                }

                @Override
                protected void whenSucceeded() {
                    if (text != null) {
                        isSettingValues = true;
                        mainArea.setText(text);
                        isSettingValues = false;

                        updateInterface(false);
                        if (!sourceInformation.isTotalNumberRead()) {
                            loadTotalNumbers();
                        }
                    } else {
                        popFailed();
                    }

                }

                @Override
                protected void cancelled() {
                    super.cancelled();
                    taskCanceled();
                }

            };
            openHandlingStage(task, Modality.WINDOW_MODAL);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    protected void updateInterface(boolean changed) {
        fileChanged.set(changed);
        if (getMyStage() == null) {
            return;
        }
        String t = getBaseTitle();
        if (sourceFile != null) {
            t += "  " + sourceFile.getAbsolutePath();
        }
        if (fileChanged.getValue()) {
            getMyStage().setTitle(t + "*");
        } else {
            getMyStage().setTitle(t);
        }
        if (!formatMainArea()) {
            if (editLabel != null) {
                editLabel.setText(AppVariables.message("InvalidData"));
            }
            mainArea.setStyle(badStyle);
            return;
        } else if (editLabel != null) {
            editLabel.setText("");
        }
        mainArea.setStyle(null);
        String text = mainArea.getText();
        int objectsNumber = text.length();
        int linesNumber = TextTools.countNumber(text, "\n") + 1;
        String objectNumberName = "", objectName = "";
        if (editType == Edit_Type.Text) {
            if (sourceInformation.getLineBreak().equals(Line_Break.CRLF)) {
                objectsNumber += linesNumber - 1;
            }
            objectName = AppVariables.message("Characters");
            objectNumberName = AppVariables.message("CharactersNumber");
        } else if (editType == Edit_Type.Bytes) {
            objectsNumber = text.length() / 3;
            objectName = AppVariables.message("Bytes");
            objectNumberName = AppVariables.message("BytesNumber");
        }
        saveButton.setDisable(false);
        if (saveAsBox != null) {
            saveAsBox.setDisable(false);
        }
        if (sourceFile == null) {
            pageBar.setDisable(true);
            setLines(1, linesNumber);
            pageLabel.setText("");
            sourceInformation.setObjectsNumber(objectsNumber);
            sourceInformation.setLinesNumber(linesNumber);
            bottomLabel.setText(objectNumberName + ": " + sourceInformation.getObjectsNumber() + "  "
                    + AppVariables.message("LinesNumber") + ": " + sourceInformation.getLinesNumber());
        } else {
            if (!sourceInformation.isTotalNumberRead()) {
                pageBar.setDisable(true);
                saveButton.setDisable(true);
                if (saveAsBox != null) {
                    saveAsBox.setDisable(true);
                }
                setLines(sourceInformation.getCurrentPageLineStart(), sourceInformation.getCurrentPageLineEnd());
                bottomLabel.setText(objectName + ": "
                        + (sourceInformation.getCurrentPageObjectStart() + 1) + "-" + sourceInformation.getCurrentPageObjectEnd() + " "
                        + AppVariables.message("CountingTotalNumber"));

                locateObjectButton.setDisable(true);
                locateLineButton.setDisable(true);
            } else {
                if (sourceInformation.getObjectsNumber() <= sourceInformation.getPageSize()) {
                    pageBar.setDisable(true);
                    sourceInformation.setPagesNumber(1);
                } else {
                    pageBar.setDisable(changed);
                    if (findBox != null) {
                        findBox.setDisable(changed && findWhole);
                    }
                    if (filterBox != null) {
                        filterBox.setDisable(changed && findWhole);
                    }
                    if (currentBox != null) {
                        currentBox.setDisable(changed || sourceInformation.isWithBom());
                    }
                    if (locateObjectButton != null) {
                        locateObjectButton.setDisable(changed);
                        locateLineButton.setDisable(changed);
                    }
                    previousButton.setDisable(sourceInformation.getCurrentPage() <= 1);
                    nextButton.setDisable(sourceInformation.getObjectsNumber() <= sourceInformation.getCurrentPageObjectEnd());
                    if (sourceInformation.getObjectsNumber() % sourceInformation.getPageSize() == 0) {
                        sourceInformation.setPagesNumber(sourceInformation.getObjectsNumber() / sourceInformation.getPageSize());
                    } else {
                        sourceInformation.setPagesNumber(sourceInformation.getObjectsNumber() / sourceInformation.getPageSize() + 1);
                    }
                    pageInput.setText(sourceInformation.getCurrentPage() + "");
                    currentPage = sourceInformation.getCurrentPage();
                    countCurrentFound();
                }
                isSettingValues = false;

                pageLabel.setText("/" + sourceInformation.getPagesNumber());
                if (!changed) {
                    setLines(sourceInformation.getCurrentPageLineStart(), sourceInformation.getCurrentPageLineEnd());
                    bottomLabel.setText(objectName + ": "
                            + (sourceInformation.getCurrentPageObjectStart() + 1) + "-" + sourceInformation.getCurrentPageObjectEnd() + "/"
                            + sourceInformation.getObjectsNumber() + " "
                            + AppVariables.message("Lines") + ": "
                            + sourceInformation.getCurrentPageLineStart() + "-" + sourceInformation.getCurrentPageLineEnd() + "/"
                            + sourceInformation.getLinesNumber());
                } else {
                    long charsTo = sourceInformation.getCurrentPageObjectStart() + objectsNumber;
                    long charsTotal = sourceInformation.getObjectsNumber()
                            + (objectsNumber - (sourceInformation.getCurrentPageObjectEnd() - sourceInformation.getCurrentPageObjectStart()));
                    long linesTo = sourceInformation.getCurrentPageLineStart() + linesNumber - 1;
                    long linesTotal = sourceInformation.getLinesNumber()
                            + (linesNumber - (sourceInformation.getCurrentPageLineEnd() - sourceInformation.getCurrentPageLineStart() + 1));
                    setLines(sourceInformation.getCurrentPageLineStart(), linesTo);
                    bottomLabel.setText(objectName + ": "
                            + (sourceInformation.getCurrentPageObjectStart() + 1) + "-" + charsTo + "/"
                            + charsTotal + " "
                            + AppVariables.message("Lines") + ": "
                            + sourceInformation.getCurrentPageLineStart() + "-" + linesTo + "/"
                            + linesTotal);
                }
            }
        }
        if (okButton != null) {
            okButton.setDisable(changed);
        }

        setSecondArea(text);
        if (sourceInformation.getCurrentPosition() >= 0) {
            mainArea.requestFocus();
            mainArea.deselect();
            int pLocation = sourceInformation.getCurrentPosition();
            if (editType == Edit_Type.Bytes) {
                mainArea.selectRange(pLocation, sourceInformation.getCurrentPosition() + 2);
            } else {
                mainArea.selectRange(pLocation, sourceInformation.getCurrentPosition() + 1);
            }
            sourceInformation.setCurrentPosition(-1);

        } else if (sourceInformation.getCurrentLine() >= 1) {
            if (sourceInformation.getCurrentPageLineStart() <= sourceInformation.getCurrentLine()
                    && sourceInformation.getCurrentPageLineEnd() >= sourceInformation.getCurrentLine()) {
                String[] lines = text.split("\n");
                int index = 0, end = (int) (sourceInformation.getCurrentLine() - sourceInformation.getCurrentPageLineStart());
                for (int i = 0; i < end; i++) {
                    index += lines[i].length() + 1;
                }
                if (editType == Edit_Type.Bytes) {
                    mainArea.selectRange(index, index + 2);
                } else {
                    mainArea.selectRange(index, index + 1);
                }
            }
            sourceInformation.setCurrentLine(-1);

        } else if (currentFound >= 0) {
            mainArea.requestFocus();
            mainArea.deselect();
            mainArea.selectRange(currentFound, Math.min(text.length(), currentFound + sourceInformation.getFindString().length()));
            if (findNextButton != null) {
                findNextButton.setDisable(false);
                findPreviousButton.setDisable(false);
                replaceButton.setDisable(false);
            }
        }

    }

    protected boolean formatMainArea() {
        return true;
    }

    protected void countCurrentFound() {

    }

    @FXML
    @Override
    public void saveAction() {
        if (sourceFile == null) {
            saveNew();
        } else {
            saveExisted();
        }
    }

    protected void saveNew() {
        final File file = chooseSaveFile(AppVariables.getUserConfigPath(targetPathKey),
                null, targetExtensionFilter, true);
        if (file == null) {
            return;
        }
        recordFileWritten(file);
        targetInformation.setFile(file);
        if (targetBomCheck != null) {
            targetInformation.setWithBom(targetBomCheck.isSelected());
        }
        synchronized (this) {
            if (task != null) {
                return;
            }
            task = new SingletonTask<Void>() {

                @Override
                protected boolean handle() {
                    ok = targetInformation.writeObject(mainArea.getText());

                    return true;
                }

                @Override
                protected void whenSucceeded() {
                    popSuccessul();
                    charsetByUser = false;
                    openFile(file);
                }

            };
            openHandlingStage(task, Modality.WINDOW_MODAL);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    protected void saveExisted() {
        if (confirmCheck.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(getMyStage().getTitle());
            alert.setContentText(AppVariables.message("SureOverrideFile"));
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            ButtonType buttonSave = new ButtonType(AppVariables.message("Save"));
            ButtonType buttonSaveAs = new ButtonType(AppVariables.message("SaveAs"));
            ButtonType buttonCancel = new ButtonType(AppVariables.message("Cancel"));
            alert.getButtonTypes().setAll(buttonSave, buttonSaveAs, buttonCancel);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            stage.toFront();

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonCancel) {
                return;
            } else if (result.get() == buttonSaveAs) {
                saveAsAction();
                return;
            }
        }
        targetInformation.setFile(sourceFile);
        if (targetBomCheck != null) {
            targetInformation.setWithBom(targetBomCheck.isSelected());
        }
        synchronized (this) {
            if (task != null) {
                return;
            }
            task = new SingletonTask<Void>() {

                @Override
                protected boolean handle() {
                    ok = targetInformation.writePage(sourceInformation, mainArea.getText());

                    return true;
                }

                @Override
                protected void whenSucceeded() {
                    popSuccessul();
                    openFile(sourceFile);
                    updateInterface(false);
                }

                @Override
                protected void whenFailed() {
                    updateInterface(false);
                    super.whenFailed();
                }

            };
            openHandlingStage(task, Modality.WINDOW_MODAL);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    @Override
    public void saveAsAction() {
        String name = null;
        if (sourceFile != null) {
            name = FileTools.getFilePrefix(sourceFile.getName());
        }
        final File file = chooseSaveFile(AppVariables.getUserConfigPath(targetPathKey),
                name, targetExtensionFilter, true);
        if (file == null) {
            return;
        }
        recordFileWritten(file);

        targetInformation.setFile(file);
        if (targetBomCheck != null) {
            targetInformation.setWithBom(targetBomCheck.isSelected());
        } else {
            targetInformation.setWithBom(sourceInformation.isWithBom());
        }
        synchronized (this) {
            if (task != null) {
                return;
            }
            task = new SingletonTask<Void>() {

                @Override
                protected boolean handle() {
                    ok = targetInformation.writePage(sourceInformation, mainArea.getText());
                    return true;
                }

                @Override
                protected void whenSucceeded() {
                    if (saveAsType == SaveAsType.Load) {
                        openFile(file);
                    } else if (saveAsType == SaveAsType.Open) {
                        FileEditerController controller = openNewStage();
                        controller.openFile(file);
                    }
                    popSuccessul();
                }

            };
            openHandlingStage(task, Modality.WINDOW_MODAL);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    public FileEditerController openNewStage() {
        if (null == editType) {
            return null;
        } else {
            switch (editType) {
                case Text:
                    return (FileEditerController) openStage(CommonValues.TextEditerFxml);
                case Bytes:
                    return (FileEditerController) openStage(CommonValues.BytesEditerFxml);
                default:
                    return null;
            }
        }
    }

    @FXML
    @Override
    public void createAction() {
        try {
            if (!checkBeforeNextAction()) {
                return;
            }
            initPage(null);
            updateInterface(false);
        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    @Override
    public boolean checkBeforeNextAction() {
        if (fileChanged == null || !fileChanged.getValue()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(getMyStage().getTitle());
            alert.setContentText(AppVariables.message("NeedSaveBeforeAction"));
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            ButtonType buttonSave = new ButtonType(AppVariables.message("Save"));
            ButtonType buttonNotSave = new ButtonType(AppVariables.message("NotSave"));
            ButtonType buttonCancel = new ButtonType(AppVariables.message("Cancel"));
            alert.getButtonTypes().setAll(buttonSave, buttonNotSave, buttonCancel);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            stage.toFront();

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonSave) {
                saveAction();
                return false;
            } else {
                return result.get() == buttonNotSave;
            }
        }
    }

    @Override
    public void taskCanceled(Task task) {
        taskCanceled();
    }

    public void taskCanceled() {
        bottomLabel.setText("");
        if (backgroundTask != null && backgroundTask.isRunning()) {
            backgroundTask.cancel();
            backgroundTask = null;
        }
    }

}
