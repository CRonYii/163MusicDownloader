package ui.fxml;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXSnackbar;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import ui.Center;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private StackPane toastContainer;

    @FXML
    private Label statusLabel;

    @FXML
    private JFXHamburger titleBurger;

    @FXML
    private JFXRippler titleRippler;

    @FXML
    private JFXHamburger optionBurger;

    @FXML
    private JFXRippler optionRippler;

    @FXML
    private StackPane tabViewContainer;

    private JFXPopup downloadPopup;

    private JFXPopup optionPopup;

    public MainController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        loader.setRoot(this);
        loader.setController(this);
    }

    @PostConstruct
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JFXSnackbar toast = new JFXSnackbar(toastContainer);
        Center.setToast(toast);
        Center.setLabel(statusLabel);
        Center.printToStatus("Welcome to 163Music");

        try {
            tabViewContainer.getChildren().add(FXMLLoader.load(getClass().getResource("/fxml/ui/TabView.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            downloadPopup = new JFXPopup(FXMLLoader.load(getClass().getResource("/fxml/ui/DownloadPopup.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        titleBurger.setOnMouseClicked(event -> downloadPopup.show(titleRippler, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, 50.0, 10.0));

        try {
            optionPopup = new JFXPopup(FXMLLoader.load(getClass().getResource("/fxml/ui/OptionPopup.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        optionBurger.setOnMouseClicked(event -> optionPopup.show(optionRippler, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT, -50.0, 10.0));

    }


}
