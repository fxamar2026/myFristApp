package com.example.centers.ui;

import com.example.centers.config.AppContext;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

public class ImportView {
    private final AppContext context;

    public ImportView(AppContext context) {
        this.context = context;
    }

    public Parent build() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(12));

        TextField centerCode = new TextField();
        centerCode.setPromptText("Center Code");

        Label selectedFile = new Label("No file selected");
        Button select = new Button("Select import package");
        Button register = new Button("Register import");

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
            if (holder[0] == null || centerCode.getText().isBlank()) {
                new Alert(Alert.AlertType.WARNING, "Please enter center code and choose file", ButtonType.OK).showAndWait();
                return;
            }
            context.importService().registerImport(centerCode.getText().trim().toUpperCase(), holder[0].toPath());
            new Alert(Alert.AlertType.INFORMATION, "Import batch has been staged", ButtonType.OK).showAndWait();
        });

        root.getChildren().addAll(
                new Label("Import center package into staging"),
                new HBox(8, new Label("Center:"), centerCode),
                new HBox(8, select, selectedFile),
                register
        );

        return root;
    }
}
