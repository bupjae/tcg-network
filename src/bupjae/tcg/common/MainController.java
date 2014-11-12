/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bupjae.tcg.common;

import bupjae.tcg.common.proto.CardInfo;
import bupjae.tcg.common.data.DataManager;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Bupjae
 */
public class MainController implements Initializable {

    @FXML
    private BorderPane root;
    @FXML
    private TableView<CardInfo> masterTable;
    @FXML
    private TableView<DataManager.DeckWrapper.EntryWrapper> userTable;
    @FXML
    private ImageView cardView;

    private final ObjectProperty<DataManager> dataManager = new SimpleObjectProperty<>(this, "dataManager");
    private FileChooser userFileChooser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dataManager.addListener((o, oldValue, newValue) -> {
            if (newValue == null) {
                masterTable.setItems(FXCollections.emptyObservableList());
                userTable.setItems(FXCollections.emptyObservableList());
            } else {
                masterTable.setItems(newValue.getMaster());
                userTable.setItems(newValue.getDeck());
            }
        });
        dataManager.bind(Bindings.createObjectBinding(
                () -> (DataManager) root.getProperties().get(DataManager.CURRENT_DATA_MANAGER_KEY),
                root.getProperties()));
        userFileChooser = new FileChooser();
        userFileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("덱 파일 (*.dek)", "*.dek"),
                new FileChooser.ExtensionFilter("모든 파일 (*.*)", "*.*")
        );
        userFileChooser.setSelectedExtensionFilter(userFileChooser.getExtensionFilters().get(0));
        masterTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            cardView.setImage(ImageCache.getImage(newValue.getImageUrl()));
        });
        userTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            cardView.setImage(ImageCache.getImage(newValue.getImageUrl()));
        });
    }

    @FXML
    private void openDeck() throws IOException {
        File file = userFileChooser.showOpenDialog(root.getScene().getWindow());
        if (file == null) {
            return;
        }
        dataManager.get().readDeck(file);
    }

    @FXML
    private void boardTest() throws IOException {
        Stage boardStage = new Stage();
        boardStage.initOwner(root.getScene().getWindow());
        boardStage.initModality(Modality.WINDOW_MODAL);
        boardStage.initStyle(StageStyle.UTILITY);

        Parent boardRoot = FXMLLoader.load(getClass().getResource("/bupjae/tcg/chaostcg/Board.fxml"));
        boardRoot.getProperties().put(DataManager.CURRENT_DATA_MANAGER_KEY, dataManager.get());
        boardStage.setScene(new Scene(boardRoot));
        boardStage.setMaximized(true);
        boardStage.show();
    }
}
