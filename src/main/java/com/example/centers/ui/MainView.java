package com.example.centers.ui;

import com.example.centers.config.AppContext;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainView {
    private final AppContext context;
    private Locale locale = Locale.ENGLISH;
    private ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);

    public MainView(AppContext context) {
        this.context = context;
    }

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));
        root.getStyleClass().add("app-root");

        Label title = new Label(bundle.getString("app.title"));
        title.getStyleClass().add("app-title");

        ComboBox<String> language = new ComboBox<>();
        language.getItems().addAll("English", "العربية");
        language.setValue("English");

        ToggleButton mode = new ToggleButton("🌙");

        HBox topBar = new HBox(10, title, new Separator(), new Label("Lang"), language, mode);
        topBar.setPadding(new Insets(0, 0, 12, 0));
        root.setTop(topBar);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().add(new Tab(bundle.getString("nav.dashboard"), new DashboardView(context).build()));
        tabPane.getTabs().add(new Tab(bundle.getString("nav.centers"), new CentersView(context).build()));
        tabPane.getTabs().add(new Tab(bundle.getString("nav.import"), new ImportView(context).build()));
        tabPane.getTabs().add(new Tab(bundle.getString("nav.review"), new ReviewMergeView(context).build()));
        tabPane.getTabs().forEach(tab -> tab.setClosable(false));
        root.setCenter(tabPane);

        language.setOnAction(e -> {
            boolean arabic = "العربية".equals(language.getValue());
            locale = arabic ? Locale.of("ar") : Locale.ENGLISH;
            bundle = ResourceBundle.getBundle("i18n.messages", locale);
            root.setNodeOrientation(arabic ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
            title.setText(bundle.getString("app.title"));
            tabPane.getTabs().get(0).setText(bundle.getString("nav.dashboard"));
            tabPane.getTabs().get(1).setText(bundle.getString("nav.centers"));
            tabPane.getTabs().get(2).setText(bundle.getString("nav.import"));
            tabPane.getTabs().get(3).setText(bundle.getString("nav.review"));
        });

        mode.setOnAction(e -> {
            if (mode.isSelected()) {
                root.getStyleClass().add("dark");
                mode.setText("☀️");
            } else {
                root.getStyleClass().remove("dark");
                mode.setText("🌙");
            }
        });

        return root;
    }
}
