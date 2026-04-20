package com.example.centers.ui;

import com.example.centers.config.AppContext;
import com.example.centers.model.Center;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CentersView {
    private final AppContext context;
    private final TableView<Center> tableView = new TableView<>();

    public CentersView(AppContext context) {
        this.context = context;
    }

    public Parent build() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(12));

        GridPane form = buildForm();
        configureTable();
        refreshData();

        VBox.setVgrow(tableView, Priority.ALWAYS);
        root.getChildren().addAll(form, tableView);
        return root;
    }

    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);

        TextField code = new TextField();
        TextField arName = new TextField();
        TextField enName = new TextField();
        Button create = new Button("Add Center");

        create.setOnAction(e -> {
            try {
                context.centerService().createCenter(code.getText(), arName.getText(), enName.getText());
                code.clear();
                arName.clear();
                enName.clear();
                refreshData();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
            }
        });

        grid.addRow(0, new Label("Code"), code, new Label("Arabic Name"), arName, new Label("English Name"), enName, create);
        return grid;
    }

    private void configureTable() {
        TableColumn<Center, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().code()));

        TableColumn<Center, String> arCol = new TableColumn<>("Arabic Name");
        arCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().nameAr()));

        TableColumn<Center, String> enCol = new TableColumn<>("English Name");
        enCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().nameEn()));

        tableView.getColumns().setAll(codeCol, arCol, enCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void refreshData() {
        tableView.setItems(FXCollections.observableArrayList(context.centerService().listCenters()));
    }
}
