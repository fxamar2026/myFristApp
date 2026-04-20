package com.example.centers.ui;

import com.example.centers.config.AppContext;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class MainView {
    private final AppContext context;

    public MainView(AppContext context) {
        this.context = context;
    }

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        Label title = new Label("Central Centers Database Manager");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        root.setTop(title);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(new Tab("Dashboard", new DashboardView(context).build()));
        tabPane.getTabs().add(new Tab("Centers", new CentersView(context).build()));
        tabPane.getTabs().add(new Tab("Import", new ImportView(context).build()));

        tabPane.getTabs().forEach(tab -> tab.setClosable(false));
        root.setCenter(tabPane);

        return root;
    }
}
