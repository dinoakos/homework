package boardgame.model;

/*import boardgame.BoardGameApplication;
import boardgame.controller.BoardGameController;
import boardgame.controller.ScoreboardController;
import javafx.application.Application;

 */
import com.google.gson.*;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static boardgame.model.Square.*;

/**
 * Model of the boardgame
 */
public class BoardGameModel {
    /**
     * The number of rows on the board
     */
    public static final int BOARD_X = 6;
    /**
     * The number of columns on the board
     */
    public static final int BOARD_Y = 7;
    /**
     * Stores the value of the winning square,
     */

    public static Square winner=NONE;

    private ReadOnlyObjectWrapper<Square>[][] board = new ReadOnlyObjectWrapper[BOARD_X][BOARD_Y];
    /**
     * Constructs the board out of squares
     */
    public BoardGameModel() {
        for (var i = 0; i < BOARD_X; i++) {
            for (var j = 0; j < BOARD_Y; j++) {

                board[i][j] = new ReadOnlyObjectWrapper<Square>(
                        switch (i) {
                            case 0 -> Square.RED;
                            case BOARD_X - 1 -> BLUE;
                            default -> Square.NONE;
                        }
                );
            }
        }
        board[2][4]=new ReadOnlyObjectWrapper<Square>(Square.BLOCKED);
        board[3][2]=new ReadOnlyObjectWrapper<Square>(Square.BLOCKED);
    }
    /**
     * Returns the property of a square.
     * @param i the row which the square is in
     * @param j the column which the square is in
     * @return returns the type of the square.
     */
    public ReadOnlyObjectProperty<Square> squareProperty(int i, int j) {
        return board[i][j].getReadOnlyProperty();
    }

    /**
     * Gives you the square on a given position
     * @param p The position of the square
     * @return Returns the square
     */
    public Square getSquare(Position p) {
        return board[p.row()][p.col()].get();
    }
    private void setSquare(Position p, Square square) {
        board[p.row()][p.col()].set(square);
    }

    /**
     * Moves a square
     * @param from The position of the square before
     * @param to Where the square is going
     */
    public void move(Position from, Position to) {
        setSquare(to, getSquare(from));
        setSquare(from, Square.NONE);
        Logger.info("moved to"+to);
        checkWinner();
    }

    /**
     * Checks if a piece can move
     * @param from The position of the piece
     * @param to The position we want to check is a doable step
     * @return Retruns if a move is doable
     */
    public boolean canMove(Position from, Position to) {

       return isOnBoard(from) && isOnBoard(to) && !isEmpty(from) && (isEmpty(to)||isPawnAttack(from,to))&& isPawnMove(from, to);
    }

    /**
     * Checks if a position is empty
     * @param p The position
     * @return  Returns if a square is empty or not
     */
    public boolean isEmpty(Position p) {
        return getSquare(p) == Square.NONE;

    }

    /**
     * Checks if a position is on the board
     * @param p The position
     * @return Returns if a position is on the board or not
     */
    public static boolean isOnBoard(Position p) {
        return 0 <= p.row() && p.row() < BOARD_X && 0 <= p.col() && p.col() < BOARD_Y;
    }

    /**
     * Checks if the piece can attack a position
     * @param from The position of the piece
     * @param to The position we want to see if it fits the attack pattern
     * @return Returns if the piece can attack a position
     */
    public boolean isPawnAttack(Position from, Position to) {

        var dx = to.row() - from.row();
        var dy = to.col() - from.col();

        return switch (getSquare(from)) {
            case BLUE -> -1 <= dy && dy <= 1 && dx == -1 &&dy!=0&&getSquare(to)!=BLUE&&getSquare(to)!=BLOCKED;
            case RED -> -1 <= dy && dy <= 1 && dx == 1 && dy!=0&&getSquare(to)!=RED&&getSquare(to)!=BLOCKED;
            case BLOCKED -> false;
            case GRAY -> false;
            case NONE -> false;
        };
    }
    /**
     * Checks if the move is based on a pawn's movement
     * @param from The position of the piece
     * @param to The position we want to check if it fits the criteria
     * @return Returns if the move is based on a pawn's movement
     */
    public boolean isPawnMove(Position from, Position to) {

        var dx=to.row()-from.row();
        var dy=to.col() - from.col();

        return switch (getSquare(from)){
            case BLUE -> -1<=dy&&dy<=1&&dx==-1&&getSquare(to)!=BLUE&&getSquare(to)!=BLOCKED;
            case RED -> -1<=dy&&dy<=1&&dx==1&&getSquare(to)!=RED&&getSquare(to)!=BLOCKED;
            case BLOCKED -> false;
            case GRAY ->false;
            case NONE ->false;
        };
    }

    /**
     * Checks if a piece has any legal moves left
     * @param from The position of the piece
     * @return Retruns if a piece has any legal moves left
     */
    public boolean hasMove(Position from) {
        List<Position> positions=new ArrayList<>();
        for (int i=-1;i<=1;i++) {
            var pos=switch (getSquare(from)){
                case BLUE -> new Position(from.row() - 1, from.col() + i);
                case RED -> new Position(from.row() + 1, from.col() + i);
                case BLOCKED -> from;
                case GRAY -> from;
                case NONE -> from;
            };
            if (canMove(from,pos)){
                positions.add(pos);
            }
        }
        return !positions.isEmpty();
    }

    /**
     * Checks if there is a winner and sets the winner
     */
    private void checkWinner(){
        boolean redHasMove=false;
        boolean blueHasMove=false;
        for (var i = 0; i < BOARD_X; i++) {
            for (var j = 0; j < BOARD_Y; j++) {
                var pos=new Position(i,j);
                if (getSquare(pos)==RED){
                    redHasMove=hasMove(pos)||redHasMove;}
                if (getSquare(pos)==BLUE){
                    blueHasMove=hasMove(pos)||blueHasMove;}
            }
        }
        if (redHasMove&&!blueHasMove){
            Logger.info("red won");
            winner=RED;
        }
        if (!redHasMove&&blueHasMove){
            Logger.info("blue won");
            winner=BLUE;
        }

    }

    /**
     * Represents the board as a matrix
     * @return A representation of the board
     */
    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (var i = 0; i < BOARD_X; i++) {
            for (var j = 0; j < BOARD_Y; j++) {
                sb.append(board[i][j].get().ordinal()).append(' ');
            }
            sb.append('\n');
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        var model = new BoardGameModel();
        Logger.info(model);

    }

    /**
     * Makes a map from the json file
     * @param src The location of the json file
     * @return Returns a map generated from the json
     */
    public Map<String,Integer> jsonToMap(String src) {
        Map<String,Integer> returnMap = new HashMap<>();
        File file = new File(src);
        if(file.exists()){
            try(FileReader fileReader = new FileReader(file)){
                JsonArray playerArray = JsonParser.parseReader(fileReader).getAsJsonArray();
                for(var element : playerArray){
                    JsonObject player = element.getAsJsonObject();
                    if(player.has("name") && player.has("wins")){
                        String name = player.get("name").getAsString();
                        Integer wins = player.get("wins").getAsInt();
                        returnMap.put(name,wins);
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return returnMap;
    }

    /**
     * Writes the winner and how many times he won into a file
     * @param playerName The color of the player who won
     */
    public void updatePlayerData(String playerName) {
        File file = new File("scoreboard.json");
        JsonArray playerArray;

        if (file.exists()) {
            try (FileReader fileReader = new FileReader(file)) {
                playerArray = JsonParser.parseReader(fileReader).getAsJsonArray();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        else {
            playerArray = new JsonArray();
        }

        boolean playerExists = false;
        for (var element : playerArray) {
            JsonObject player = element.getAsJsonObject();
            if (player.has("name") && player.get("name").getAsString().equals(playerName)) {
                playerExists = true;
                player.addProperty("wins", player.get("wins").getAsInt() + 1);
                break;
            }
        }

        if (!playerExists) {
            JsonObject newPlayer = new JsonObject();
            newPlayer.addProperty("name", playerName);
            newPlayer.addProperty("wins", 1);
            playerArray.add(newPlayer);
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(playerArray, fileWriter);
            Logger.info("JSON content successfully written to file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
