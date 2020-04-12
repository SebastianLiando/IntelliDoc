package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * This class is the main class of the application.
 * It is responsible in launching the user interface.
 */
public class Main extends Application {
    private static final int WINDOW_HEIGHT = 500;
    private static final int WINDOW_WIDTH = 900;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Setup the user interface
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../res/layout/main_layout.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("IntelliDoc");
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add("res/style/styles.css");
        primaryStage.setScene(scene);
        primaryStage.show();

        //Positions to the center of the screen
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setMinHeight(bounds.getHeight());
        primaryStage.setX((bounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((bounds.getHeight() - primaryStage.getHeight()) / 2);

        //Set app icon
        primaryStage.getIcons().add(new Image("res/img/doctor/doctor_female.png"));

        //Starts the application logic
        Controller controller = loader.getController();
        controller.beginConversation();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
