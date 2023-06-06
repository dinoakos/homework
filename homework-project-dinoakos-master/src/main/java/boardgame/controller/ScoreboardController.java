package boardgame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import boardgame.model.BoardGameModel;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ScoreboardController {

    @FXML
    private Button exitButton;
    @FXML
    private ListView<String> listView;
    private BoardGameModel model = new BoardGameModel();
    private Map<String,Integer> myMap = model.jsonToMap("ScoreBoard.json");

    @FXML
    private void initialize(){
        listView.getItems().addAll(convertMapToList(myMap));
    }

    private List<String> convertMapToList(Map<String, Integer> map) {
        List<String> keyValuePairs = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String keyValuePair = entry.getKey() + ": " + entry.getValue();
            keyValuePairs.add(keyValuePair);
        }

        keyValuePairs.sort(Comparator.comparingInt(s -> -1 * Integer.parseInt(s.split(": ")[1])));
        return keyValuePairs;
    }

    public void exitButtonAction(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
        Logger.info("Exiting the game.");
    }

    public void newGameAction(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui.fxml"));
        Parent root = fxmlLoader.load();
        currentStage.setScene(new Scene(root));
        Logger.info("Changing scene to OpeningScreen.");
    }
}

