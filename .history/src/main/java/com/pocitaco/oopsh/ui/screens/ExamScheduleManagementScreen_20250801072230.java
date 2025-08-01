package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.ExamScheduleDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.enums.ScheduleStatus;
import com.pocitaco.oopsh.enums.TimeSlot;
import com.pocitaco.oopsh.models.ExamSchedule;
import com.pocitaco.oopsh.models.ExamType;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Colors;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Typography;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Icons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Buttons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Cards;
import com.pocitaco.oopsh.utils.DialogUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exam Schedule Management Screen
 * Quản lý lịch thi cử cho admin với sidebar navigation
 * 
 * @author OOPSH Team
 * @version 1.0
 */
public class ExamScheduleManagementScreen extends BorderPane {

    private User currentUser;
    private ExamScheduleDAO examScheduleDAO;
    private ExamTypeDAO examTypeDAO;
    private ExamSchedule selectedSchedule;
    private Stage primaryStage;

    // UI Components
    private TableView<ExamSchedule> tableView;
    private ComboBox<ExamType> cbExamType;
    private DatePicker dpExamDate;
    private ComboBox<TimeSlot> cbTimeSlot;
    private ComboBox<ScheduleStatus> cbStatus;
    private TextField txtMaxCandidates;
    private TextField txtLocation;
    private Button btnAdd;
    private Button btnUpdate;
    private Button btnClear;
    private Button btnDelete;

    // Search controls
    private TextField txtSearch;
    private DatePicker dpSearchFrom;
    private DatePicker dpSearchTo;
    private ComboBox<ScheduleStatus> cbSearchStatus;
    private Button btnSearch;
    private Button btnReset;

    // Statistics labels
    private Label totalSchedulesLabel;
    private Label openSchedulesLabel;
    private Label upcomingSchedulesLabel;
    private Label completedSchedulesLabel;

    // Navigation components
    private VBox navigationRail;
    private StackPane contentArea;

    public ExamScheduleManagementScreen(User currentUser) {
        this.currentUser = currentUser;
        this.examScheduleDAO = new ExamScheduleDAO();
        this.examTypeDAO = new ExamTypeDAO();

        initializeComponents();
        setupLayout();
        loadData();
    }

    public ExamScheduleManagementScreen(User currentUser, Stage primaryStage) {
        this.currentUser = currentUser;
        this.primaryStage = primaryStage;
        this.examScheduleDAO = new ExamScheduleDAO();
        this.examTypeDAO = new ExamTypeDAO();

        initializeComponents();
        setupLayout();
        loadData();
    }

    private void initializeComponents() {
        // Initialize table
        tableView = new TableView<>();
        setupTableColumns();

        // Initialize form controls
        cbExamType = new ComboBox<>();
        dpExamDate = new DatePicker();
        cbTimeSlot = new ComboBox<>();
        cbStatus = new ComboBox<>();
        txtMaxCandidates = new TextField();
        txtLocation = new TextField();

        // Initialize buttons
        btnAdd = new Button("Thêm");
        btnUpdate = new Button("Cập nhật");
        btnClear = new Button("Xóa");
        btnDelete = new Button("Xóa");

        // Initialize search controls
        txtSearch = new TextField();
        dpSearchFrom = new DatePicker();
        dpSearchTo = new DatePicker();
        cbSearchStatus = new ComboBox<>();
        btnSearch = new Button("Tìm kiếm");
        btnReset = new Button("Làm mới");

        setupFormControls();
        setupEventHandlers();
    }

    private void setupTableColumns() {
        TableColumn<ExamSchedule, Integer> colId = new TableColumn<>("ID");
        TableColumn<ExamSchedule, String> colExamType = new TableColumn<>("Loại thi");
        TableColumn<ExamSchedule, String> colDate = new TableColumn<>("Ngày thi");
        TableColumn<ExamSchedule, String> colTimeSlot = new TableColumn<>("Khung giờ");
        TableColumn<ExamSchedule, String> colStatus = new TableColumn<>("Trạng thái");
        TableColumn<ExamSchedule, String> colMaxCandidates = new TableColumn<>("Số lượng tối đa");
        TableColumn<ExamSchedule, String> colRegistered = new TableColumn<>("Đã đăng ký");
        TableColumn<ExamSchedule, String> colLocation = new TableColumn<>("Địa điểm");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colExamType.setCellValueFactory(new PropertyValueFactory<>("examTypeName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("examDate"));
        colTimeSlot.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colMaxCandidates.setCellValueFactory(new PropertyValueFactory<>("maxCandidates"));
        colRegistered.setCellValueFactory(new PropertyValueFactory<>("registeredCandidates"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));

        tableView.getColumns().addAll(colId, colExamType, colDate, colTimeSlot, colStatus, colMaxCandidates,
                colRegistered, colLocation);

        // Setup selection listener
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedSchedule = newSelection;
                        loadScheduleToForm(newSelection);
                    }
                });
    }

    private void setupFormControls() {
        // Load exam types
        List<ExamType> examTypes = examTypeDAO.getAll();
        cbExamType.setItems(FXCollections.observableArrayList(examTypes));
        cbExamType.setCellFactory(param -> new ListCell<ExamType>() {
            @Override
            protected void updateItem(ExamType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getCode() + ")");
                }
            }
        });
        cbExamType.setButtonCell(cbExamType.getCellFactory().call(null));

        // Load time slots
        ObservableList<TimeSlot> timeSlots = FXCollections.observableArrayList(TimeSlot.values());
        cbTimeSlot.setItems(timeSlots);
        cbTimeSlot.setCellFactory(param -> new ListCell<TimeSlot>() {
            @Override
            protected void updateItem(TimeSlot item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        cbTimeSlot.setButtonCell(cbTimeSlot.getCellFactory().call(null));

        // Load status options
        ObservableList<ScheduleStatus> statuses = FXCollections.observableArrayList(ScheduleStatus.values());
        cbStatus.setItems(statuses);
        cbStatus.setCellFactory(param -> new ListCell<ScheduleStatus>() {
            @Override
            protected void updateItem(ScheduleStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        cbStatus.setButtonCell(cbStatus.getCellFactory().call(null));

        // Setup search status
        cbSearchStatus.setItems(statuses);
        cbSearchStatus.setCellFactory(param -> new ListCell<ScheduleStatus>() {
            @Override
            protected void updateItem(ScheduleStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        cbSearchStatus.setButtonCell(cbSearchStatus.getCellFactory().call(null));

        // Set default values
        dpExamDate.setValue(LocalDate.now());
        cbStatus.setValue(ScheduleStatus.OPEN);
        txtLocation.setPromptText("Nhập địa điểm thi");
        txtMaxCandidates.setPromptText("Số lượng thí sinh tối đa");
    }

    private void setupEventHandlers() {
        btnAdd.setOnAction(event -> handleAdd());
        btnUpdate.setOnAction(event -> handleUpdate());
        btnDelete.setOnAction(event -> handleDelete());
        btnClear.setOnAction(event -> clearForm());
        btnSearch.setOnAction(event -> handleSearch());
        btnReset.setOnAction(event -> resetSearch());
    }

    private void setupLayout() {
        // Main layout with sidebar
        setBackground(new Background(new BackgroundFill(
                Colors.BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        // Create components
        HBox appBar = createAppBar();
        navigationRail = createNavigationRail();
        contentArea = createContentArea();

        // Layout setup
        setTop(appBar);
        setLeft(navigationRail);
        setCenter(contentArea);

        // Load exam schedule management content
        loadExamScheduleManagementContent();
    }

    private HBox createAppBar() {
        HBox appBar = new HBox();
        appBar.setAlignment(Pos.CENTER_LEFT);
        appBar.setSpacing(16);
        appBar.setPadding(new Insets(0, 24, 0, 24));
        appBar.setPrefHeight(64);
        appBar.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Title
        Label title = new Label("Quản lý lịch thi - OOPSH");
        title.setFont(Typography.TITLE_LARGE);
        title.setTextFill(Colors.PRIMARY);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Back to Dashboard button
        Button backButton = Buttons.createIconButton(Icons.createDashboardIcon());
        backButton.setOnAction(e -> backToDashboard());

        appBar.getChildren().addAll(title, spacer, backButton);

        // Add elevation
        appBar.setEffect(createElevation(2));

        return appBar;
    }

    private VBox createNavigationRail() {
        VBox navRail = new VBox();
        navRail.setAlignment(Pos.TOP_CENTER);
        navRail.setSpacing(8);
        navRail.setPadding(new Insets(16, 8, 16, 8));
        navRail.setPrefWidth(80);
        navRail.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Navigation items
        navRail.getChildren().addAll(
                createNavItem("Dashboard", Icons.createDashboardIcon(), false),
                createNavItem("Người dùng", Icons.createAccountGroupIcon(), false),
                createNavItem("Loại thi", Icons.createFileDocumentIcon(), false),
                createNavItem("Lịch thi", Icons.createCalendarIcon(), true),
                createNavItem("Báo cáo", Icons.createChartBarIcon(), false));

        return navRail;
    }

    private VBox createNavItem(String text, FontIcon icon, boolean selected) {
        VBox navItem = new VBox();
        navItem.setAlignment(Pos.CENTER);
        navItem.setSpacing(4);
        navItem.setPadding(new Insets(8));
        navItem.setPrefWidth(64);
        navItem.setPrefHeight(64);

        // Set background and styling based on selection
        if (selected) {
            navItem.setBackground(new Background(new BackgroundFill(
                    Colors.PRIMARY_CONTAINER, new CornerRadii(16), Insets.EMPTY)));
            icon.setIconColor(Colors.ON_PRIMARY_CONTAINER);
        } else {
            navItem.setBackground(Background.EMPTY);
            icon.setIconColor(Colors.ON_SURFACE_VARIANT);
        }

        icon.setIconSize(24);

        Label label = new Label(text);
        label.setFont(Typography.LABEL_SMALL);
        label.setTextFill(selected ? Colors.ON_PRIMARY_CONTAINER : Colors.ON_SURFACE_VARIANT);

        navItem.getChildren().addAll(icon, label);

        // Add click handler
        navItem.setOnMouseClicked(e -> handleNavItemClick(text));
        navItem.setStyle("-fx-cursor: hand;");

        return navItem;
    }

    private StackPane createContentArea() {
        StackPane content = new StackPane();
        content.setPadding(new Insets(24));
        content.setBackground(new Background(new BackgroundFill(
                Colors.BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        return content;
    }

    private void loadExamScheduleManagementContent() {
        VBox managementContent = new VBox(24);
        managementContent.setAlignment(Pos.TOP_LEFT);

        // Header Section
        VBox headerSection = createHeaderSection();

        // Statistics Cards
        HBox statsSection = createStatsSection();

        // Search Section
        VBox searchSection = createSearchSection();

        // Main content
        VBox mainContent = new VBox(20);

        // Table
        VBox tableSection = new VBox(10);
        Label tableLabel = new Label("Danh sách lịch thi");
        tableLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        tableSection.getChildren().addAll(tableLabel, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        // Form section
        VBox formSection = createFormSection();

        mainContent.getChildren().addAll(tableSection, formSection);

        managementContent.getChildren().addAll(headerSection, statsSection, searchSection, mainContent);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(managementContent);
    }

    private VBox createHeaderSection() {
        VBox headerSection = new VBox(8);

        Label titleLabel = new Label("Quản lý lịch thi sát hạch");
        titleLabel.setFont(Typography.DISPLAY_SMALL);
        titleLabel.setTextFill(Colors.PRIMARY);

        Label subtitleLabel = new Label("Quản lý lịch thi và lập kế hoạch cho các kỳ thi sát hạch");
        subtitleLabel.setFont(Typography.BODY_LARGE);
        subtitleLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        headerSection.getChildren().addAll(titleLabel, subtitleLabel);
        return headerSection;
    }

    private HBox createStatsSection() {
        HBox statsSection = new HBox(16);
        statsSection.setAlignment(Pos.CENTER_LEFT);

        // Total Schedules Card
        VBox totalCard = createStatCard(
                "Tổng lịch thi",
                "0",
                Icons.createCalendarIcon(),
                Colors.PRIMARY);
        totalSchedulesLabel = (Label) ((VBox) ((HBox) totalCard.getChildren().get(0)).getChildren().get(1))
                .getChildren().get(1);

        // Open Schedules Card
        VBox openCard = createStatCard(
                "Mở đăng ký",
                "0",
                Icons.createCalendarIcon(),
                Colors.SUCCESS);
        openSchedulesLabel = (Label) ((VBox) ((HBox) openCard.getChildren().get(0)).getChildren().get(1))
                .getChildren().get(1);

        // Upcoming Schedules Card
        VBox upcomingCard = createStatCard(
                "Sắp tới",
                "0",
                Icons.createCalendarIcon(),
                Colors.WARNING);
        upcomingSchedulesLabel = (Label) ((VBox) ((HBox) upcomingCard.getChildren().get(0)).getChildren().get(1))
                .getChildren().get(1);

        // Completed Schedules Card
        VBox completedCard = createStatCard(
                "Hoàn thành",
                "0",
                Icons.createCalendarIcon(),
                Colors.SECONDARY);
        completedSchedulesLabel = (Label) ((VBox) ((HBox) completedCard.getChildren().get(0)).getChildren().get(1))
                .getChildren().get(1);

        statsSection.getChildren().addAll(totalCard, openCard, upcomingCard, completedCard);
        return statsSection;
    }

    private VBox createStatCard(String title, String value, FontIcon icon, Color accentColor) {
        VBox card = Cards.createCard();
        card.setPrefWidth(200);
        card.setSpacing(16);

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(16);

        icon.setIconSize(32);
        icon.setIconColor(accentColor);

        VBox textInfo = new VBox();
        textInfo.setSpacing(4);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Typography.BODY_MEDIUM);
        titleLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Typography.HEADLINE_SMALL);
        valueLabel.setTextFill(Colors.ON_SURFACE);

        textInfo.getChildren().addAll(titleLabel, valueLabel);
        header.getChildren().addAll(icon, textInfo);

        card.getChildren().add(header);
        return card;
    }

    private VBox createSearchSection() {
        VBox searchSection = Cards.createCard();
        searchSection.setSpacing(16);
        searchSection.setPadding(new Insets(20));

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(12);

        FontIcon searchIcon = Icons.createFileDocumentIcon();
        searchIcon.setIconSize(24);
        searchIcon.setIconColor(Colors.PRIMARY);

        Label searchLabel = new Label("Tìm kiếm lịch thi");
        searchLabel.setFont(Typography.TITLE_MEDIUM);
        searchLabel.setTextFill(Colors.ON_SURFACE);

        header.getChildren().addAll(searchIcon, searchLabel);

        // Search Grid
        GridPane searchGrid = new GridPane();
        searchGrid.setHgap(16);
        searchGrid.setVgap(16);

        // Keyword search
        VBox keywordContainer = new VBox(8);
        Label keywordLabel = new Label("Từ khóa");
        keywordLabel.setFont(Typography.LABEL_MEDIUM);
        keywordLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        txtSearch.setPromptText("Tìm theo loại thi, địa điểm...");
        txtSearch.setStyle("-fx-background-color: " + Colors.SURFACE + "; -fx-background-radius: 8; -fx-border-color: "
                + Colors.OUTLINE_VARIANT
                + "; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12 16; -fx-font-size: 14;");
        keywordContainer.getChildren().addAll(keywordLabel, txtSearch);

        // Date range
        VBox dateContainer = new VBox(8);
        Label dateLabel = new Label("Khoảng thời gian");
        dateLabel.setFont(Typography.LABEL_MEDIUM);
        dateLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        HBox dateRange = new HBox(12);
        dateRange.setAlignment(Pos.CENTER_LEFT);

        dpSearchFrom.setPromptText("Từ ngày");
        dpSearchFrom
                .setStyle("-fx-background-color: " + Colors.SURFACE + "; -fx-background-radius: 8; -fx-border-color: "
                        + Colors.OUTLINE_VARIANT + "; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12 16;");

        dpSearchTo.setPromptText("Đến ngày");
        dpSearchTo.setStyle("-fx-background-color: " + Colors.SURFACE + "; -fx-background-radius: 8; -fx-border-color: "
                + Colors.OUTLINE_VARIANT + "; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12 16;");

        dateRange.getChildren().addAll(dpSearchFrom, dpSearchTo);
        dateContainer.getChildren().addAll(dateLabel, dateRange);

        // Status filter
        VBox statusContainer = new VBox(8);
        Label statusLabel = new Label("Trạng thái");
        statusLabel.setFont(Typography.LABEL_MEDIUM);
        statusLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        cbSearchStatus.setPromptText("Chọn trạng thái");
        cbSearchStatus
                .setStyle("-fx-background-color: " + Colors.SURFACE + "; -fx-background-radius: 8; -fx-border-color: "
                        + Colors.OUTLINE_VARIANT + "; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12 16;");
        statusContainer.getChildren().addAll(statusLabel, cbSearchStatus);

        // Add to grid
        searchGrid.add(keywordContainer, 0, 0);
        searchGrid.add(dateContainer, 1, 0);
        searchGrid.add(statusContainer, 2, 0);

        // Action buttons
        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        btnSearch.setText("Tìm kiếm");
        btnSearch.setStyle("-fx-background-color: " + Colors.PRIMARY + "; -fx-background-radius: 8; -fx-text-fill: "
                + Colors.ON_PRIMARY + "; -fx-font-weight: 600; -fx-padding: 12 24;");
        btnSearch.setOnAction(e -> handleSearch());

        btnReset.setText("Làm mới");
        btnReset.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-text-fill: "
                + Colors.PRIMARY + "; -fx-border-color: " + Colors.PRIMARY
                + "; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12 24;");
        btnReset.setOnAction(e -> resetSearch());

        actionButtons.getChildren().addAll(btnSearch, btnReset);
        searchGrid.add(actionButtons, 3, 0);

        searchSection.getChildren().addAll(header, searchGrid);
        return searchSection;
    }

    private VBox createFormSection() {
        VBox formSection = Cards.createCard();
        formSection.setSpacing(16);
        formSection.setPadding(new Insets(20));

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(12);

        FontIcon formIcon = Icons.createEditIcon();
        formIcon.setIconSize(24);
        formIcon.setIconColor(Colors.PRIMARY);

        Label formLabel = new Label("Thông tin lịch thi");
        formLabel.setFont(Typography.TITLE_MEDIUM);
        formLabel.setTextFill(Colors.ON_SURFACE);

        header.getChildren().addAll(formIcon, formLabel);

        // Form Grid
        GridPane formGrid = new GridPane();
        formGrid.setHgap(16);
        formGrid.setVgap(16);

        // Exam Type
        VBox examTypeContainer = new VBox(8);
        Label examTypeLabel = new Label("Loại thi");
        examTypeLabel.setFont(Typography.LABEL_MEDIUM);
        examTypeLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        cbExamType.setPromptText("Chọn loại thi");
        cbExamType.setStyle("-fx-background-color: " + Colors.SURFACE + "; -fx-background-radius: 8; -fx-border-color: "
                + Colors.OUTLINE_VARIANT + "; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12 16;");
        examTypeContainer.getChildren().addAll(examTypeLabel, cbExamType);

        // Exam Date
        VBox examDateContainer = new VBox(8);
        Label examDateLabel = new Label("Ngày thi");
        examDateLabel.setFont(Typography.LABEL_MEDIUM);
        examDateLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        dpExamDate.setPromptText("Chọn ngày thi");
        dpExamDate.setStyle("-fx-background-color: " + Colors.SURFACE + "; -fx-background-radius: 8; -fx-border-color: "
                + Colors.OUTLINE_VARIANT + "; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12 16;");
        examDateContainer.getChildren().addAll(examDateLabel, dpExamDate);

        // Time Slot
        VBox timeSlotContainer = new VBox(8);
        Label timeSlotLabel = new Label("Khung giờ");
        timeSlotLabel.setFont(Typography.LABEL_MEDIUM);
        timeSlotLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        cbTimeSlot.setPromptText("Chọn khung giờ");
        cbTimeSlot.setStyle("-fx-background-color: " + Colors.SURFACE + "; -fx-background-radius: 8; -fx-border-color: "
                + Colors.OUTLINE_VARIANT + "; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12 16;");
        timeSlotContainer.getChildren().addAll(timeSlotLabel, cbTimeSlot);

        // Status
        VBox statusContainer = new VBox(8);
        Label statusLabel = new Label("Trạng thái");
        statusLabel.setFont(Typography.LABEL_MEDIUM);
        statusLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        cbStatus.setPromptText("Chọn trạng thái");
        cbStatus.setStyle("-fx-background-color: " + Colors.SURFACE + "; -fx-background-radius: 8; -fx-border-color: "
                + Colors.OUTLINE_VARIANT + "; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12 16;");
        statusContainer.getChildren().addAll(statusLabel, cbStatus);

        // Max Candidates
        VBox maxCandidatesContainer = new VBox(8);
        Label maxCandidatesLabel = new Label("Số lượng tối đa");
        maxCandidatesLabel.setFont(Typography.LABEL_MEDIUM);
        maxCandidatesLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        txtMaxCandidates.setPromptText("Nhập số lượng thí sinh tối đa");
        txtMaxCandidates.setStyle("-fx-background-color: " + Colors.SURFACE
                + "; -fx-background-radius: 8; -fx-border-color: " + Colors.OUTLINE_VARIANT
                + "; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12 16; -fx-font-size: 14;");
        maxCandidatesContainer.getChildren().addAll(maxCandidatesLabel, txtMaxCandidates);

        // Location
        VBox locationContainer = new VBox(8);
        Label locationLabel = new Label("Địa điểm");
        locationLabel.setFont(Typography.LABEL_MEDIUM);
        locationLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        txtLocation.setPromptText("Nhập địa điểm thi");
        txtLocation.setStyle("-fx-background-color: " + Colors.SURFACE
                + "; -fx-background-radius: 8; -fx-border-color: " + Colors.OUTLINE_VARIANT
                + "; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12 16; -fx-font-size: 14;");
        locationContainer.getChildren().addAll(locationLabel, txtLocation);

        // Add to grid
        formGrid.add(examTypeContainer, 0, 0);
        formGrid.add(examDateContainer, 1, 0);
        formGrid.add(timeSlotContainer, 2, 0);
        formGrid.add(statusContainer, 0, 1);
        formGrid.add(maxCandidatesContainer, 1, 1);
        formGrid.add(locationContainer, 2, 1);

        // Action buttons
        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        btnAdd.setText("Thêm");
        btnAdd.setStyle("-fx-background-color: " + Colors.PRIMARY + "; -fx-background-radius: 8; -fx-text-fill: "
                + Colors.ON_PRIMARY + "; -fx-font-weight: 600; -fx-padding: 12 24;");
        btnAdd.setOnAction(e -> handleAdd());

        btnUpdate.setText("Cập nhật");
        btnUpdate.setStyle("-fx-background-color: " + Colors.PRIMARY + "; -fx-background-radius: 8; -fx-text-fill: "
                + Colors.ON_PRIMARY + "; -fx-font-weight: 600; -fx-padding: 12 24;");
        btnUpdate.setOnAction(e -> handleUpdate());

        btnDelete.setText("Xóa");
        btnDelete.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-text-fill: " + Colors.ERROR
                + "; -fx-border-color: " + Colors.ERROR
                + "; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12 24;");
        btnDelete.setOnAction(e -> handleDelete());

        btnClear.setText("Làm mới");
        btnClear.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-text-fill: "
                + Colors.PRIMARY + "; -fx-border-color: " + Colors.PRIMARY
                + "; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 12 24;");
        btnClear.setOnAction(e -> clearForm());

        actionButtons.getChildren().addAll(btnAdd, btnUpdate, btnDelete, btnClear);
        formGrid.add(actionButtons, 3, 1);

        formSection.getChildren().addAll(header, formGrid);
        return formSection;
    }

    private void loadData() {
        try {
            List<ExamSchedule> schedules = examScheduleDAO.getAllSchedules();

            // Add exam type names to schedules
            for (ExamSchedule schedule : schedules) {
                try {
                    examTypeDAO.get(schedule.getExamTypeId()).ifPresent(examType -> {
                        schedule.setExamTypeName(examType.getName());
                    });
                } catch (Exception e) {
                    schedule.setExamTypeName("Không xác định");
                }
            }

            tableView.getItems().clear();
            tableView.getItems().addAll(schedules);

            // Update statistics
            updateStats(schedules);
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Lỗi", "Không thể tải danh sách lịch thi: " + e.getMessage());
        }
    }

    private void updateStats(List<ExamSchedule> schedules) {
        int totalSchedules = schedules.size();
        int openSchedules = (int) schedules.stream()
                .filter(s -> s.getStatus() == ScheduleStatus.OPEN)
                .count();
        int upcomingSchedules = (int) schedules.stream()
                .filter(s -> s.getExamDate().isAfter(LocalDate.now()) || s.getExamDate().equals(LocalDate.now()))
                .count();
        int completedSchedules = (int) schedules.stream()
                .filter(s -> s.getStatus() == ScheduleStatus.COMPLETED)
                .count();

        totalSchedulesLabel.setText(String.valueOf(totalSchedules));
        openSchedulesLabel.setText(String.valueOf(openSchedules));
        upcomingSchedulesLabel.setText(String.valueOf(upcomingSchedules));
        completedSchedulesLabel.setText(String.valueOf(completedSchedules));
    }

    private void handleAdd() {
        if (validateForm()) {
            try {
                ExamSchedule schedule = new ExamSchedule();
                schedule.setExamTypeId(cbExamType.getValue().getId());
                schedule.setExamDate(dpExamDate.getValue());
                schedule.setTimeSlot(cbTimeSlot.getValue());
                schedule.setStatus(cbStatus.getValue());
                schedule.setMaxCandidates(Integer.parseInt(txtMaxCandidates.getText()));
                schedule.setLocation(txtLocation.getText());
                schedule.setExaminerId(currentUser.getId());

                examScheduleDAO.addSchedule(schedule);
                loadData();
                clearForm();
                DialogUtils.showInfo("Thành công", "Đã thêm lịch thi mới!");
            } catch (Exception e) {
                e.printStackTrace();
                DialogUtils.showError("Lỗi", "Không thể thêm lịch thi: " + e.getMessage());
            }
        }
    }

    private void handleUpdate() {
        if (selectedSchedule != null && validateForm()) {
            try {
                selectedSchedule.setExamTypeId(cbExamType.getValue().getId());
                selectedSchedule.setExamDate(dpExamDate.getValue());
                selectedSchedule.setTimeSlot(cbTimeSlot.getValue());
                selectedSchedule.setStatus(cbStatus.getValue());
                selectedSchedule.setMaxCandidates(Integer.parseInt(txtMaxCandidates.getText()));
                selectedSchedule.setLocation(txtLocation.getText());

                examScheduleDAO.updateSchedule(selectedSchedule);
                loadData();
                clearForm();
                DialogUtils.showInfo("Thành công", "Đã cập nhật lịch thi!");
            } catch (Exception e) {
                e.printStackTrace();
                DialogUtils.showError("Lỗi", "Không thể cập nhật lịch thi: " + e.getMessage());
            }
        } else {
            DialogUtils.showError("Lỗi", "Vui lòng chọn lịch thi để cập nhật!");
        }
    }

    private void handleDelete() {
        if (selectedSchedule != null) {
            if (DialogUtils.showConfirmation("Xác nhận xóa", "Bạn có chắc muốn xóa lịch thi này?")) {
                try {
                    examScheduleDAO.deleteSchedule(selectedSchedule.getId());
                    loadData();
                    clearForm();
                    DialogUtils.showInfo("Thành công", "Đã xóa lịch thi!");
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogUtils.showError("Lỗi", "Không thể xóa lịch thi: " + e.getMessage());
                }
            }
        } else {
            DialogUtils.showError("Lỗi", "Vui lòng chọn lịch thi để xóa!");
        }
    }

    private void loadScheduleToForm(ExamSchedule schedule) {
        try {
            examTypeDAO.get(schedule.getExamTypeId()).ifPresent(cbExamType::setValue);
            dpExamDate.setValue(schedule.getExamDate());
            cbTimeSlot.setValue(schedule.getTimeSlot());
            cbStatus.setValue(schedule.getStatus());
            txtMaxCandidates.setText(String.valueOf(schedule.getMaxCandidates()));
            txtLocation.setText(schedule.getLocation());
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Lỗi", "Không thể tải thông tin lịch thi: " + e.getMessage());
        }
    }

    private void handleSearch() {
        try {
            String searchText = txtSearch.getText().toLowerCase();
            LocalDate fromDate = dpSearchFrom.getValue();
            LocalDate toDate = dpSearchTo.getValue();
            ScheduleStatus status = cbSearchStatus.getValue();

            List<ExamSchedule> allSchedules = examScheduleDAO.getAllSchedules();

            List<ExamSchedule> filteredSchedules = allSchedules.stream()
                    .filter(schedule -> {
                        // Text search
                        boolean textMatch = searchText.isEmpty() ||
                                (schedule.getExamTypeName() != null
                                        && schedule.getExamTypeName().toLowerCase().contains(searchText))
                                ||
                                schedule.getTimeSlot().getDisplayName().toLowerCase().contains(searchText) ||
                                (schedule.getLocation() != null
                                        && schedule.getLocation().toLowerCase().contains(searchText));

                        // Date range search
                        boolean dateMatch = true;
                        if (fromDate != null && toDate != null) {
                            dateMatch = !schedule.getExamDate().isBefore(fromDate) &&
                                    !schedule.getExamDate().isAfter(toDate);
                        } else if (fromDate != null) {
                            dateMatch = !schedule.getExamDate().isBefore(fromDate);
                        } else if (toDate != null) {
                            dateMatch = !schedule.getExamDate().isAfter(toDate);
                        }

                        // Status search
                        boolean statusMatch = status == null || schedule.getStatus() == status;

                        return textMatch && dateMatch && statusMatch;
                    })
                    .collect(Collectors.toList());

            // Add exam type names
            for (ExamSchedule schedule : filteredSchedules) {
                try {
                    examTypeDAO.get(schedule.getExamTypeId()).ifPresent(examType -> {
                        schedule.setExamTypeName(examType.getName());
                    });
                } catch (Exception e) {
                    schedule.setExamTypeName("Không xác định");
                }
            }

            tableView.getItems().clear();
            tableView.getItems().addAll(filteredSchedules);
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Lỗi", "Không thể tìm kiếm: " + e.getMessage());
        }
    }

    private void resetSearch() {
        txtSearch.clear();
        dpSearchFrom.setValue(null);
        dpSearchTo.setValue(null);
        cbSearchStatus.setValue(null);
        loadData();
    }

    private void clearForm() {
        cbExamType.setValue(null);
        dpExamDate.setValue(LocalDate.now());
        cbTimeSlot.setValue(null);
        cbStatus.setValue(ScheduleStatus.OPEN);
        txtMaxCandidates.clear();
        txtLocation.clear();
        selectedSchedule = null;
    }

    private boolean validateForm() {
        if (cbExamType.getValue() == null) {
            DialogUtils.showError("Lỗi", "Vui lòng chọn loại thi!");
            return false;
        }

        if (dpExamDate.getValue() == null) {
            DialogUtils.showError("Lỗi", "Vui lòng chọn ngày thi!");
            return false;
        }

        if (dpExamDate.getValue().isBefore(LocalDate.now())) {
            DialogUtils.showError("Lỗi", "Ngày thi không thể là ngày trong quá khứ!");
            return false;
        }

        if (cbTimeSlot.getValue() == null) {
            DialogUtils.showError("Lỗi", "Vui lòng chọn khung giờ thi!");
            return false;
        }

        if (cbStatus.getValue() == null) {
            DialogUtils.showError("Lỗi", "Vui lòng chọn trạng thái!");
            return false;
        }

        try {
            int maxCandidates = Integer.parseInt(txtMaxCandidates.getText());
            if (maxCandidates <= 0) {
                DialogUtils.showError("Lỗi", "Số lượng thí sinh tối đa phải lớn hơn 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            DialogUtils.showError("Lỗi", "Số lượng thí sinh tối đa phải là số nguyên!");
            return false;
        }

        if (txtLocation.getText().trim().isEmpty()) {
            DialogUtils.showError("Lỗi", "Vui lòng nhập địa điểm thi!");
            return false;
        }

        return true;
    }

    private void handleNavItemClick(String itemName) {
        System.out.println("🔄 Navigating to: " + itemName);

        switch (itemName) {
            case "Dashboard":
                backToDashboard();
                break;
            case "Người dùng":
                openUserManagement();
                break;
            case "Loại thi":
                openExamTypeManagement();
                break;
            case "Lịch thi":
                // Stay on current screen
                break;
            case "Báo cáo":
                openReports();
                break;
            default:
                System.out.println("🔍 Unknown navigation item: " + itemName);
                break;
        }
    }

    private void backToDashboard() {
        if (primaryStage != null) {
            AdminDashboardScreen adminDashboard = new AdminDashboardScreen(primaryStage, currentUser);
            adminDashboard.show();
        }
    }

    private void openUserManagement() {
        if (primaryStage != null) {
            UserManagementScreen userManagement = new UserManagementScreen(primaryStage);
            Scene scene = userManagement.createScene();
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    private void openExamTypeManagement() {
        if (primaryStage != null) {
            ExamTypeManagementScreen examTypeManagement = new ExamTypeManagementScreen(primaryStage);
            Scene scene = examTypeManagement.createScene();
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    private void openReports() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Báo cáo");
        alert.setHeaderText(null);
        alert.setContentText("Chức năng báo cáo đang được phát triển");
        alert.showAndWait();
    }

    private javafx.scene.effect.DropShadow createElevation(int level) {
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.2));
        shadow.setOffsetY(level * 2);
        shadow.setRadius(level * 4);
        return shadow;
    }

    public Scene createScene() {
        return new Scene(this, 1200, 800);
    }
}