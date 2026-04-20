package com.example.centers.ui;

import com.example.centers.config.AppContext;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardView {
    private final AppContext context;

    public DashboardView(AppContext context) {
        this.context = context;
    }

    public Parent build() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(12));

        int centersCount = context.centerService().listCenters().size();
        Label centers = new Label("Registered centers: " + centersCount);
        Label note = new Label("Phase 1 scaffold: database, center management, and import placeholder are ready.");

        centers.setStyle("-fx-font-size: 16px;");
        note.setStyle("-fx-text-fill: #666;");

        box.getChildren().addAll(centers, note);
        return box;
    }
}
