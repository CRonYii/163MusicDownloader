package ui;

import com.jfoenix.controls.JFXDecorator;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

// TODO: new Theme, customize css
public class Main extends Application {

    public static final double WIDTH = 1200;
    public static final double HEIGHT = 900;
    public static ReadOnlyDoubleProperty WIDTH_PROPERTY;
    public static ReadOnlyDoubleProperty HEIGHT_PROPERTY;

    public static Set<Runnable> onShowEvents = new HashSet<>();

    public static void main(String[] args) {
        launch(args);
    }

    public static void addOnShowEvent(Runnable runnable) {
        onShowEvents.add(runnable);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));

        JFXDecorator decorator = new JFXDecorator(primaryStage, root);
        decorator.setCustomMaximize(true);

        Scene scene = new Scene(decorator, WIDTH, HEIGHT);
        Main.WIDTH_PROPERTY = scene.widthProperty();
        Main.HEIGHT_PROPERTY = scene.heightProperty();
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(
                getClass().getResource("/css/jfoenix-fonts.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-design.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-main-demo.css").toExternalForm());

        primaryStage.setOnCloseRequest(Center.CLOSE_EVENT);

        Center.setRootScene(scene);

        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);
        primaryStage.setResizable(false);

        primaryStage.setScene(scene);
        primaryStage.setTitle("163Music");
        primaryStage.show();

        onShowEvents.forEach(Runnable::run);
    }

}
