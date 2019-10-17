package br.infonet.flavio.AutScript.Launcher;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author fillipe
 */
public class GeradorLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("br/infonet/flavio/AutScript/GUI/telaFXML.fxml"));
            Stage stage = new Stage();
            stage.setTitle("AutScript - Gerador de Script Automático");
            stage.setScene(new Scene(root));
            stage.getIcons().add(new Image("br/infonet/flavio/AutScript/images/automatic.png"));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        launch(args);

    }
}
