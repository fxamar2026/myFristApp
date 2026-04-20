package com.example.centers.ui;

import com.example.centers.config.AppContext;
import com.example.centers.model.StagingRecord;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ReviewMergeView {
    private final AppContext context;
    private final TableView<StagingRecord> table = new TableView<>();

    public ReviewMergeView(AppContext context) {
        this.context = context;
    }

    public Parent build() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(12));

        TextField centerCode = new TextField();
        centerCode.setPromptText("Center Code (e.g. C01)");

        Button load = new Button("Load Pending");
        Button approve = new Button("Approve + Merge");

        load.setOnAction(e -> {
            if (centerCode.getText().isBlank()) return;
            table.setItems(FXCollections.observableArrayList(context.importService().pendingForCenter(centerCode.getText())));
        });

        approve.setOnAction(e -> {
            if (centerCode.getText().isBlank()) return;
            int merged = context.mergeService().approvePendingRecords(centerCode.getText().trim().toUpperCase());
            new Alert(Alert.AlertType.INFORMATION, "Merged " + merged + " records", ButtonType.OK).showAndWait();
            table.setItems(FXCollections.observableArrayList(context.importService().pendingForCenter(centerCode.getText())));
        });

        TableColumn<StagingRecord, String> code = new TableColumn<>("Generated Code");
        code.setCellValueFactory(v -> new javafx.beans.property.SimpleStringProperty(v.getValue().generatedCode()));
        TableColumn<StagingRecord, String> status = new TableColumn<>("Status");
        status.setCellValueFactory(v -> new javafx.beans.property.SimpleStringProperty(v.getValue().status()));
        TableColumn<StagingRecord, String> payload = new TableColumn<>("Encrypted Payload");
        payload.setCellValueFactory(v -> new javafx.beans.property.SimpleStringProperty(v.getValue().payloadJson()));

        table.getColumns().setAll(code, status, payload);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        VBox.setVgrow(table, Priority.ALWAYS);
        root.getChildren().addAll(new HBox(8, new Label("Center"), centerCode, load, approve), table);
        return root;
    }
}
