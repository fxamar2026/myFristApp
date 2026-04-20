package com.example.centers.ui;

import com.example.centers.config.AppContext;
import com.example.centers.model.ImportBatch;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

public class ImportView {
    private final AppContext context;
    private final TableView<ImportBatch> batches = new TableView<>();

    public ImportView(AppContext context) {
        this.context = context;
    }

    public Parent build() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(12));

        TextField centerCode = new TextField();
        centerCode.setPromptText("Center Code");

        PasswordField centerPassword = new PasswordField();
        centerPassword.setPromptText("Center encryption password");

        Label selectedFile = new Label("No file selected");
        Button select = new Button("Select import package");
        Button register = new Button("Stage import");
        Button refresh = new Button("Refresh batches");

        final File[] holder = new File[1];

        select.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Center Export File");
            File file = chooser.showOpenDialog(null);
            if (file != null) {
                holder[0] = file;
                selectedFile.setText(file.getName());
            }
        });

        register.setOnAction(e -> {
            if (holder[0] == null || centerCode.getText().isBlank() || centerPassword.getText().isBlank()) {
                new Alert(Alert.AlertType.WARNING, "Please enter center code/password and choose file", ButtonType.OK).showAndWait();
                return;
            }
            long batchId = context.importService().registerImport(centerCode.getText(), holder[0].toPath(), centerPassword.getText());
            new Alert(Alert.AlertType.INFORMATION, "Import batch staged: #" + batchId, ButtonType.OK).showAndWait();
            refreshBatches();
        });

        refresh.setOnAction(e -> refreshBatches());

        TableColumn<ImportBatch, String> centerCol = new TableColumn<>("Center");
        centerCol.setCellValueFactory(v -> new javafx.beans.property.SimpleStringProperty(v.getValue().centerCode()));
        TableColumn<ImportBatch, String> fileCol = new TableColumn<>("File");
        fileCol.setCellValueFactory(v -> new javafx.beans.property.SimpleStringProperty(v.getValue().fileName()));
        TableColumn<ImportBatch, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(v -> new javafx.beans.property.SimpleStringProperty(v.getValue().status()));

        batches.getColumns().setAll(centerCol, fileCol, statusCol);
        batches.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        VBox.setVgrow(batches, Priority.ALWAYS);
        root.getChildren().addAll(
                new Label("Import center package into staging"),
                new HBox(8, new Label("Center:"), centerCode),
                new HBox(8, new Label("Password:"), centerPassword),
                new HBox(8, select, selectedFile, register, refresh),
                batches
        );

        refreshBatches();
        return root;
    }

    private void refreshBatches() {
        batches.setItems(FXCollections.observableArrayList(context.importService().recentBatches()));
    }
}
