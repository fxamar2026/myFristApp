package com.example.centers;

import com.example.centers.config.AppContext;
import com.example.centers.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CenterDbManagerApp extends Application {
    @Override
    public void start(Stage stage) {
        AppContext context = AppContext.bootstrap();
        MainView mainView = new MainView(context);

        Scene scene = new Scene(mainView.build(), 1200, 760);
        stage.setTitle("Center DB Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
