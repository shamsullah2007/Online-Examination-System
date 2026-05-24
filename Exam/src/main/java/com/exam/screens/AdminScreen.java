package com.exam.screens;

import com.exam.dao.ExamDAO;
import com.exam.models.Exam;
import com.exam.utils.SceneManager;
import com.exam.utils.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class AdminScreen {

    private final ExamDAO examDAO = new ExamDAO();

    // Form fields
    private TextField titleField;
    private TextArea  descField;
    private TextField durationField;
    private TextField totalMarksField;
    private TextField passMarksField;
    private TextField scheduledField;
    private Label     formMessage;

    // Table refreshed after exam creation
    private TableView<Exam> examTable;

    // Stat labels
    private Label totalExamsLabel  = new Label("0");
    private Label activeExamsLabel = new Label("0");

    public Scene buildScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F4F0;");

        root.setLeft(buildSidebar());
        root.setCenter(buildCenter());

        return new Scene(root, 980, 700);
    }

    // ── SIDEBAR ───────────────────────────────────────────────────
    private VBox buildSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(16));
        sidebar.setStyle("-fx-background-color: white;");

        // Admin avatar
        Circle avatar = new Circle(20);
        avatar.setFill(Color.web("#534AB7"));

        Label initials = new Label("AD");
        initials.setTextFill(Color.WHITE);
        initials.setFont(Font.font("System", FontWeight.BOLD, 14));

        StackPane avatarStack = new StackPane(avatar, initials);

        String adminName = SessionManager.getInstance()
                .getCurrentUser().getFirstName()
                + " "
                + SessionManager.getInstance()
                .getCurrentUser().getLastName();

        Label nameLabel = new Label(adminName);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        Label roleLabel = new Label("Administrator");
        roleLabel.setFont(Font.font("System", 11));
        roleLabel.setTextFill(Color.web("#888780"));

        VBox userInfo = new VBox(2, nameLabel, roleLabel);
        HBox userBox  = new HBox(10, avatarStack, userInfo);
        userBox.setAlignment(Pos.CENTER_LEFT);

        Separator sep = new Separator();
        sep.setPadding(new Insets(6, 0, 6, 0));

        Button overviewBtn   = navBtn("Overview");
        Button createExamBtn = navBtn("Create Exam");
        Button studentsBtn   = navBtn("All Students");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = navBtn("Log Out");
        logoutBtn.setStyle(navBtnStyle() + "-fx-text-fill: #A32D2D;");
        logoutBtn.setOnAction(e -> {
            SessionManager.getInstance().logout();
            SceneManager.switchTo("login");
        });

        sidebar.getChildren().addAll(
                userBox, sep,
                overviewBtn, createExamBtn, studentsBtn,
                spacer, logoutBtn
        );

        return sidebar;
    }

    // ── CENTER ────────────────────────────────────────────────────
    private ScrollPane buildCenter() {
        VBox center = new VBox(16);
        center.setPadding(new Insets(24));

        Label pageTitle = new Label("Admin Dashboard");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Stat cards
        HBox statsRow = buildStatsRow();

        // Exams table section
        Label tableTitle = new Label("All Exams");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

        examTable = buildExamTable();

        // Create exam form section
        Label formTitle = new Label("Create New Exam");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

        GridPane form = buildExamForm();

        center.getChildren().addAll(
                pageTitle, statsRow,
                tableTitle, examTable,
                formTitle, form
        );

        // Load data
        loadStats();
        refreshTable();

        ScrollPane scroll = new ScrollPane(center);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #F5F4F0;");

        return scroll;
    }

    // ── STAT CARDS ────────────────────────────────────────────────
    private HBox buildStatsRow() {
        VBox card1 = statCard("Total Exams",   totalExamsLabel);
        VBox card2 = statCard("Active Exams",  activeExamsLabel);
        VBox card3 = statCard("Total Students", new Label("—"));

        HBox row = new HBox(12, card1, card2, card3);
        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);

        return row;
    }

    private VBox statCard(String title, Label valueLabel) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", 12));
        titleLabel.setTextFill(Color.web("#888780"));

        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 22));

        VBox card = new VBox(4, titleLabel, valueLabel);
        card.setPadding(new Insets(14));
        card.setStyle(
                "-fx-background-color: #EEECFC;" +
                        "-fx-background-radius: 10;"
        );

        return card;
    }

    // ── EXAMS TABLE ───────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private TableView<Exam> buildExamTable() {
        TableView<Exam> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(220);

        TableColumn<Exam, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(240);

        TableColumn<Exam, String> schedCol = new TableColumn<>("Scheduled");
        schedCol.setCellValueFactory(new PropertyValueFactory<>("scheduledAt"));
        schedCol.setPrefWidth(150);

        TableColumn<Exam, Integer> durCol = new TableColumn<>("Duration");
        durCol.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        durCol.setPrefWidth(90);
        durCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : val + " min");
            }
        });

        TableColumn<Exam, Integer> marksCol = new TableColumn<>("Marks");
        marksCol.setCellValueFactory(new PropertyValueFactory<>("totalMarks"));
        marksCol.setPrefWidth(80);

        // Status column — green Active or gray Draft
        TableColumn<Exam, Boolean> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        statusCol.setPrefWidth(90);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean active, boolean empty) {
                super.updateItem(active, empty);
                if (empty || active == null) {
                    setGraphic(null);
                } else {
                    Label lbl = new Label(active ? "Active" : "Draft");
                    lbl.setStyle(active
                            ? "-fx-text-fill: #0F6E56; -fx-font-weight: bold;"
                            : "-fx-text-fill: #888780;"
                    );
                    setGraphic(lbl);
                }
            }
        });

        table.getColumns().addAll(
                titleCol, schedCol, durCol, marksCol, statusCol
        );

        return table;
    }

    // ── EXAM CREATION FORM ────────────────────────────────────────
    private GridPane buildExamForm() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 20;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #E0DFF8;" +
                        "-fx-border-radius: 12;"
        );

        // Column constraints
        ColumnConstraints labelCol = new ColumnConstraints(120);
        ColumnConstraints fieldCol = new ColumnConstraints();
        fieldCol.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labelCol, fieldCol);

        // Form fields
        titleField      = styledField("Exam title");
        descField       = new TextArea();
        descField.setPromptText("Exam description");
        descField.setPrefRowCount(3);
        descField.setStyle(fieldStyle());

        durationField    = styledField("e.g. 60");
        totalMarksField  = styledField("e.g. 100");
        passMarksField   = styledField("e.g. 50");
        scheduledField   = styledField("2026-05-20 10:00");

        formMessage = new Label("");
        formMessage.setWrapText(true);

        Button createBtn = new Button("Create Exam");
        createBtn.setStyle(
                "-fx-background-color: #534AB7;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 9 20;" +
                        "-fx-cursor: hand;"
        );
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setOnAction(e -> handleCreateExam());

        // Add rows to grid
        addRow(grid, 0, "Title",           titleField);
        addRow(grid, 1, "Description",     descField);
        addRow(grid, 2, "Duration (min)",  durationField);
        addRow(grid, 3, "Total Marks",     totalMarksField);
        addRow(grid, 4, "Pass Marks",      passMarksField);
        addRow(grid, 5, "Scheduled",       scheduledField);

        grid.add(formMessage, 1, 6);
        grid.add(createBtn,   1, 7);

        return grid;
    }

    private void addRow(GridPane grid, int row,
                        String labelText, Control field) {
        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        grid.add(lbl,   0, row);
        grid.add(field, 1, row);
    }

    // ── CREATE EXAM HANDLER ───────────────────────────────────────
    private void handleCreateExam() {
        String title     = titleField.getText().trim();
        String desc      = descField.getText().trim();
        String durStr    = durationField.getText().trim();
        String totStr    = totalMarksField.getText().trim();
        String passStr   = passMarksField.getText().trim();
        String scheduled = scheduledField.getText().trim();

        // Validate required fields
        if (title.isEmpty() || durStr.isEmpty() ||
                totStr.isEmpty() || passStr.isEmpty()) {
            showMessage("Please fill in all required fields.", false);
            return;
        }

        // Parse integers
        int duration, totalMarks, passMarks;
        try {
            duration   = Integer.parseInt(durStr);
            totalMarks = Integer.parseInt(totStr);
            passMarks  = Integer.parseInt(passStr);
        } catch (NumberFormatException ex) {
            showMessage("Duration, Total Marks, Pass Marks must be numbers.", false);
            return;
        }

        // Pass marks cannot exceed total marks
        if (passMarks > totalMarks) {
            showMessage("Pass marks cannot exceed total marks.", false);
            return;
        }

        int adminId = SessionManager.getInstance().getCurrentUser().getId();

        Exam exam = new Exam(
                title, desc, duration, totalMarks,
                passMarks, scheduled, adminId, true
        );

        boolean success = examDAO.createExam(exam);

        if (success) {
            showMessage("Exam created successfully!", true);
            clearForm();
            refreshTable();
            loadStats();
        } else {
            showMessage("Failed to create exam. Try again.", false);
        }
    }

    // ── HELPERS ───────────────────────────────────────────────────
    private void loadStats() {
        List<Exam> all = examDAO.getAllExams();
        long active    = all.stream().filter(Exam::isActive).count();
        totalExamsLabel.setText(String.valueOf(all.size()));
        activeExamsLabel.setText(String.valueOf(active));
    }

    private void refreshTable() {
        examTable.getItems().clear();
        examTable.getItems().addAll(examDAO.getAllExams());
    }

    private void clearForm() {
        titleField.clear();
        descField.clear();
        durationField.clear();
        totalMarksField.clear();
        passMarksField.clear();
        scheduledField.clear();
    }

    private void showMessage(String msg, boolean success) {
        formMessage.setText(msg);
        formMessage.setStyle(success
                ? "-fx-text-fill: #0F6E56; -fx-font-size: 12;"
                : "-fx-text-fill: #A32D2D; -fx-font-size: 12;"
        );
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(fieldStyle());
        return tf;
    }

    private String fieldStyle() {
        return "-fx-background-color: #F5F4F0;" +
                "-fx-border-color: #D0CFF8;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 12;";
    }

    private Button navBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(navBtnStyle());
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private String navBtnStyle() {
        return "-fx-background-color: transparent;" +
                "-fx-text-fill: #888780;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-padding: 8 12;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";
    }
}