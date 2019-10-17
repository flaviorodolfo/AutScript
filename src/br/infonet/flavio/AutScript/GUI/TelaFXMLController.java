package br.infonet.flavio.AutScript.GUI;

import br.infonet.flavio.AutScript.gerador.AutScript;
import br.infonet.flavio.AutScript.RecolhimentoObject.Recolhimento;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * FXML Controller class
 *
 * @author flaviorodolfo
 */
public class TelaFXMLController_old implements Initializable {

    /**
     * Initializes the controller class.
     */
    private AutScript aut;
    
    private String caminho, pathScript;
    @FXML
    private TextField path;
    @FXML
    private TextArea info;
    @FXML
    private TableView<Recolhimento> table;
    @FXML
    private TableColumn<Recolhimento, String> dataRec;
    @FXML
    private TableColumn<Recolhimento, String> valorDif;
    @FXML
    private TextArea criarText;
    @FXML
    private Button criarButton;
    @FXML
    private Button clearBt;
    @FXML
    private Button verifyButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        aut = new AutScript();
        info.setEditable(false);
        dataRec.setCellValueFactory(new PropertyValueFactory<>("DataString"));
        valorDif.setCellValueFactory(new PropertyValueFactory<>("ValorAlteracao"));
        criarText.setVisible(false);
        criarButton.setVisible(false);
        criarText.setEditable(false);
        clearBt.setVisible(false);
        // TODO
    }
    
    private void clear() {
        aut = new AutScript();
        info.clear();
        table.getItems().clear();
        criarText.clear();
        criarText.setVisible(false);
        clearBt.setVisible(false);
        criarButton.setVisible(false);
        criarButton.setText("Gerar");
        
    }
    
    private void obterBobinas() {
        
        clear();
        new Thread(){
            @Override
            public void run(){
                verifyButton.setDisable(true);
                 info.setText("Obtendo Bobinas...\n");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TelaFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
                caminho = path.getText();
                String resp = aut.lerBob(path.getText());
                info.setText(info.getText() + resp);
                mostrarTabela();
                criarButton.setText("Gerar");
                clearBt.setVisible(true);
                if (!resp.equals("Caminho Inválido! Favor verificar se existem bobinas no diretório informado.")
                        && !resp.equals("Não foi encontrar recolhimentos na bobina.\n"
                                + "Favor verificar se os recolhimentos estão no formato padrão.")) {
                    criarButton.setVisible(true);
                }
                verifyButton.setDisable(false);
            }
        }.start();
        
        
    }
    
    private void mostrarTabela() {
        if (!aut.getDiferencas().isEmpty()) {
            ObservableList<Recolhimento> linhas
                    = FXCollections.observableArrayList(aut.getDiferencas());
            table.setItems(linhas);
        }
        
    }
    
    @FXML
    private void verifyAction(ActionEvent event) {
        obterBobinas();
    }
    
    @FXML
    private void enterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            obterBobinas();
        }
        
    }
    
    @FXML
    private void criarScript(ActionEvent event) {
        if (criarButton.getText().equals("Abrir Script")) {
            try {
                System.out.println(pathScript);
                Desktop.getDesktop().open(new File(pathScript));
            } catch (IOException ex) {
                Logger.getLogger(TelaFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (criarButton.getText().equals("Gerar")) {
            String scriptName = aut.criarScript(caminho);
            if (scriptName != null) {
                criarText.setVisible(true);
                criarText.setText(scriptName + " criado com sucesso!\nScript salvo em: " + caminho);
                criarButton.setText("Abrir Script");
                pathScript = caminho + "\\" + scriptName;
            }
            
        }
    }
    
    @FXML
    private void clearAction(ActionEvent event) {
        clear();
    }
    
}
