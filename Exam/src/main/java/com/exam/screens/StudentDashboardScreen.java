package com.exam.screens;

import com.exam.dao.AttemptDAO;
import com.exam.dao.ExamDAO;
import com.exam.models.Attempt;
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

public class StudentDashboardScreen {

    private final ExamDAO    examDAO    = new ExamDAO();
    private final AttemptDAO attemptDAO = new AttemptDAO();

    // Stat card labels — updated after loading data
    private Label completedLabel = new Label("0");
    private Label avgLabel       = new Label("0%");
    private Label upcomingLabel  = new Label("0");

    public Scene buildScene() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F4F0;");

        root.setLeft(buildSidebar());
        root.setCenter(buildCenter());

        return new Scene(root, 980, 700);
    }

    // ── LEFT SIDEBAR ──────────────────────────────────────────────
    private VBox buildSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(16));
        sidebar.setStyle("-fx-background-color: white;");

        // Avatar circle with user initials
        String initials = SessionManager.getInstance().getCurrentUser()
                .getFirstName().substring(0, 1).toUpperCase()
                + SessionManager.getInstance().getCurrentUser()
                .getLastName().substring(0, 1).toUpperCase();

        Circle avatar = new Circle(20);
        avatar.setFill(Color.web("#534AB7"));

        Label initialsLabel = new Label(initials);
        initialsLabel.setTextFill(Color.WHITE);
        initialsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        StackPane avatarStack = new StackPane(avatar, initialsLabel);

        // User name and department
        String fullName = SessionManager.getInstance().getCurrentUser().getFirstName()
                + " "
                + SessionManager.getInstance().getCurrentUser().getLastName();
        String dept = SessionManager.getInstance().getCurrentUser().getDepartment();

        Label nameLabel = new Label(fullName);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        Label deptLabel = new Label(dept != null ? dept : "");
        deptLabel.setFont(Font.font("System", 11));
        deptLabel.setTextFill(Color.web("#888780"));

        VBox userInfo = new VBox(2, nameLabel, deptLabel);
        HBox userBox  = new HBox(10, avatarStack, userInfo);
        userBox.setAlignment(Pos.CENTER_LEFT);

        Separator sep = new Separator();
        sep.setPadding(new Insets(6, 0, 6, 0));

        // Nav buttons
        Button dashBtn   = navBtn("Dashboard");
        Button examsBtn  = navBtn("My Exams");
        Button resultsBtn= navBtn("Results");

        // Spacer pushes logout to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Logout button
        Button logoutBtn = navBtn("Log Out");
        logoutBtn.setStyle(navBtnStyle() +
                "-fx-text-fill: #A32D2D;");
        logoutBtn.setOnAction(e -> {
            SessionManager.getInstance().logout();
            SceneManager.switchTo("login");
        });

        sidebar.getChildren().addAll(
                userBox, sep,
                dashBtn, examsBtn, resultsBtn,
                spacer, logoutBtn
        );

        return sidebar;
    }

    // ── CENTER CONTENT ────────────────────────────────────────────
    private VBox buildCenter() {
        VBox center = new VBox(16);
        center.setPadding(new Insets(24));

        // Greeting
        String firstName = SessionManager.getInstance()
                .getCurrentUser().getFirstName();
        Label greeting = new Label("Good morning, " + firstName + "!");
        greeting.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Stat cards row
        HBox statsRow = buildStatsRow();

        // Section label
        Label availableLabel = new Label("Available Exams");
        availableLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        // Exams table
        TableView<Exam> tableView = buildExamTable();
        VBox.setVgrow(tableView, Priority.ALWAYS);

        center.getChildren().addAll(
                greeting, statsRow, availableLabel, tableView
        );

        // Load data from database
        loadData(tableView);

        return center;
    }

    // ── STAT CARDS ────────────────────────────────────────────────
    private HBox buildStatsRow() {
        VBox card1 = statCard("Exams Completed", completedLabel);
        VBox card2 = statCard("Avg. Score",      avgLabel);
        VBox card3 = statCard("Upcoming",        upcomingLabel);

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

        // Title column
        TableColumn<Exam, String> titleCol = new TableColumn<>("Exam Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(240);

        // Scheduled column
        TableColumn<Exam, String> schedCol = new TableColumn<>("Scheduled");
        schedCol.setCellValueFactory(new PropertyValueFactory<>("scheduledAt"));
        schedCol.setPrefWidth(150);

        // Duration column
        TableColumn<Exam, Integer> durCol = new TableColumn<>("Duration");
        durCol.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        durCol.setPrefWidth(100);
        durCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : val + " min");
            }
        });

        // Marks column
        TableColumn<Exam, Integer> marksCol = new TableColumn<>("Marks");
        marksCol.setCellValueFactory(new PropertyValueFactory<>("totalMarks"));
        marksCol.setPrefWidth(80);

        // Action column — Take Exam button per row
        TableColumn<Exam, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(120);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Take Exam");
            {
                btn.setStyle(
                        "-fx-background-color: #534AB7;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 11;" +
                                "-fx-background-radius: 6;" +
                                "-fx-cursor: hand;"
                );
                btn.setOnAction(e -> {
                    Exam exam = getTableView().getItems().get(getIndex());
                    ExamScreen examScreen = new ExamScreen(exam);
                    SceneManager.getStage().setScene(examScreen.buildScene());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.getColumns().addAll(
                titleCol, schedCol, durCol, marksCol, actionCol
        );

        return table;
    }

    // ── LOAD DATA ─────────────────────────────────────────────────
    private void loadData(TableView<Exam> table) {
        // Load active exams into table
        List<Exam> exams = examDAO.getActiveExams();
        table.getItems().addAll(exams);
        upcomingLabel.setText(String.valueOf(exams.size()));

        // Load attempts to calculate stats
        int userId = SessionManager.getInstance().getCurrentUser().getId();
        List<Attempt> attempts = attemptDAO.getAttemptsByUser(userId);

        completedLabel.setText(String.valueOf(attempts.size()));

        // Calculate average score percentage
        if (!attempts.isEmpty()) {
            double totalScore = attempts.stream()
                    .mapToInt(Attempt::getScore)
                    .sum();
            double avg = totalScore / attempts.size();
            avgLabel.setText(String.format("%.1f", avg));
        }
    }

    // ── STYLE HELPERS ─────────────────────────────────────────────
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
