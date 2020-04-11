package core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static final int WINDOW_HEIGHT = 500;
    private static final int WINDOW_WIDTH = 1000;
    private static Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main_layout.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("IntelliDoc");
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add("core/styles.css");
        primaryStage.setScene(scene);
        primaryStage.show();

        controller = loader.getController();
        controller.beginConversation();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
