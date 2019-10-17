/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.infonet.flavio.AutScript.GUI;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 * FXML Controller class
 *
 * @author flaviorodolfo
 */
public class TelaFXMLController implements Initializable {

    @FXML
    private TextField path;
    @FXML
    private Button verifyButton;
    @FXML
    private TextArea info;
    @FXML
    private TableView<?> table;
    @FXML
    private TableColumn<?, ?> dataRec;
    @FXML
    private TableColumn<?, ?> valorDif;
    @FXML
    private TextArea criarText;
    @FXML
    private Button criarButton;
    @FXML
    private Button clearBt;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void enterPressed(KeyEvent event) {
    }

    @FXML
    private void verifyAction(ActionEvent event) {
    }

    @FXML
    private void criarScript(ActionEvent event) {
    }

    @FXML
    private void clearAction(ActionEvent event) {
    }
    
}
