package boardgame.controller;

import boardgame.model.BoardGameModel;
import boardgame.model.Position;
import boardgame.model.Square;
import boardgame.util.BoardGameMoveSelector;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.io.IOException;

import static boardgame.util.BoardGameMoveSelector.Phase;

public class BoardGameController {
    @FXML
    private void loadScoreBoard(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scoreBoard.fxml"));
        Parent root = fxmlLoader.load();
        currentStage.setScene(new Scene(root));
        Logger.info("Changing scene to ScoreBoard.");
    }

    @FXML
    private GridPane board;
    @FXML
    private Button loadScoreButton;
    

    private BoardGameModel model = new BoardGameModel();

    private BoardGameMoveSelector selector = new BoardGameMoveSelector(model);

    @FXML
    private void initialize() {
        for (var i = 0; i < board.getRowCount(); i++) {
            for (var j = 0; j < board.getColumnCount(); j++) {
                var square = createSquare(i, j);
                board.add(square, j, i);
            }
        }
        selector.phaseProperty().addListener(this::showSelectionPhaseChange);
        loadScoreButton.setDisable(true);
        BoardGameModel.winner=Square.NONE;
    }

    private StackPane createSquare(int i, int j) {
        var square = new StackPane();
        square.getStyleClass().add("square");
        //TODO adj hozzá bábukat
        var piece=switch (model.squareProperty(i,j).get()) {
            case NONE -> new Circle(50);
            case RED -> new Circle(50);
            case BLUE -> new Circle(50);
            case GRAY -> new Circle(50);
            case BLOCKED -> new Rectangle(100,100);
        };
        piece.fillProperty().bind(createSquareBinding(model.squareProperty(i, j)));
        square.getChildren().add(piece);
        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        var square = (StackPane) event.getSource();
        var row = GridPane.getRowIndex(square);
        var col = GridPane.getColumnIndex(square);
        Logger.info("Click on square ({},{})", row, col);
        Position p =new Position(row,col);
        if(BoardGameModel.winner==Square.NONE) {
            if (selector.isInvalidSelection(p)) {
                Logger.error("invalid selection");

            } else {

                selector.select(p);
                if (selector.isReadyToMove()) {
                    selector.makeMove();
                    getWinner();
                }
            }
        }

    }



    private ObjectBinding<Paint> createSquareBinding(ReadOnlyObjectProperty<Square> squareProperty) {
        return new ObjectBinding<Paint>() {
            {
                super.bind(squareProperty);
            }
            @Override
            protected Paint computeValue() {
                return switch (squareProperty.get()) {
                    case NONE -> Color.TRANSPARENT;
                    case RED -> Color.RED;
                    case BLUE -> Color.BLUE;
                    case GRAY -> Color.GRAY;
                    case BLOCKED -> Color.BLACK;
                };
            }
        };
    }

    private void showSelectionPhaseChange(ObservableValue<? extends Phase> value, Phase oldPhase, Phase newPhase) {
        switch (newPhase) {
            case SELECT_FROM -> {}
            case SELECT_TO -> showSelection(selector.getFrom());
            case READY_TO_MOVE -> hideSelection(selector.getFrom());
        }
    }

    private void showSelection(Position position) {
        var square = getSquare(position);
        square.getStyleClass().add("selected");
    }

    private void hideSelection(Position position) {
        var square = getSquare(position);
        square.getStyleClass().remove("selected");
    }

    private StackPane getSquare(Position position) {
        for (var child : board.getChildren()) {
            if (GridPane.getRowIndex(child) == position.row() && GridPane.getColumnIndex(child) == position.col()) {
                return (StackPane) child;
            }
        }
        throw new AssertionError();
    }
    private void getWinner(){

        Square winner = BoardGameModel.winner;

        if (winner.toString()=="BLUE"){
            loadScoreButton.setDisable(false);
            loadScoreButton.setVisible(true);
            loadScoreButton.setText("BLUE WINS");
            model.updatePlayerData("BLUE");
            //itt kéne fájlba írni
        }
        if (winner.toString()=="RED"){
            loadScoreButton.setDisable(false);
            loadScoreButton.setVisible(true);
            loadScoreButton.setText("RED WINS");
            model.updatePlayerData("RED");
        }

    }


}
