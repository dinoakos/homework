package boardgame.util;

import boardgame.model.BoardGameModel;
import boardgame.model.Position;
import boardgame.model.Square;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.tinylog.Logger;

public class BoardGameMoveSelector {

    public enum Phase {
        SELECT_FROM,
        SELECT_TO,
        READY_TO_MOVE

    }
    public enum Turn{
        BLUE,
        RED
    }
    public static boolean a= false;

    private BoardGameModel model;
    private ReadOnlyObjectWrapper<Phase> phase = new ReadOnlyObjectWrapper<>(Phase.SELECT_FROM);
    private ReadOnlyObjectWrapper<Turn> turn = new ReadOnlyObjectWrapper<>(Turn.BLUE);
    private boolean invalidSelection = false;
    private Position from;
    private Position to;

    public BoardGameMoveSelector(BoardGameModel model) {
        this.model = model;
    }

    public Phase getPhase() {
        return phase.get();
    }
    public Turn getTurn(){return turn.get();}

    public ReadOnlyObjectProperty<Phase> phaseProperty() {
        return phase.getReadOnlyProperty();
    }
    public ReadOnlyObjectProperty<Turn> turnProperty() {
        return turn.getReadOnlyProperty();
    }

    public boolean isReadyToMove() {
        return phase.get() == Phase.READY_TO_MOVE;
    }

    public void select(Position position) {

        switch (phase.get()) {
            case SELECT_FROM -> selectFrom(position);
            case SELECT_TO -> selectTo(position);
            case READY_TO_MOVE -> throw new IllegalStateException();
        }
    }

    private void selectFrom(Position position) {

        if (!model.isEmpty(position)) {
            from = position;
            phase.set(Phase.SELECT_TO);
            invalidSelection = false;
            BoardGameMoveSelector.a = true;
        } else {
            invalidSelection = true;
        }
    }

    private void selectTo(Position position) {

        if (model.canMove(from, position)) {
            to = position;
            phase.set(Phase.READY_TO_MOVE);
            invalidSelection = false;
            BoardGameMoveSelector.a = false;
        } else {
            invalidSelection = true;
        }

    }

    public Position getFrom() {
        if (phase.get() == Phase.SELECT_FROM) {
            throw new IllegalStateException();
        }
        return from;
    }

    public Position getTo() {
        if (phase.get() != Phase.READY_TO_MOVE) {
            throw new IllegalStateException();
        }
        return to;
    }


    public boolean isInvalidSelection(Position position) {
        var selected =model.squareProperty(position.row(),position.col()).get();
        var correctTurn=selected.toString()==getTurn().toString()||(selected==Square.NONE||a)&&selected!=Square.BLOCKED;//
        if(!correctTurn){
            return invalidSelection=true;
        }else{
            return invalidSelection=false;
        }
    }

    public void makeMove() {
        if (phase.get() != Phase.READY_TO_MOVE) {
            throw new IllegalStateException();
        }
        model.move(from, to);
        reset();
        nextTurn();

    }
    public void nextTurn() {

        switch (getTurn()){
            case RED -> turn.set(Turn.BLUE);
            case BLUE -> turn.set(Turn.RED);
        }
        Logger.info("nextTurn "+getTurn());


    }

    public void reset() {
        from = null;
        to = null;
        phase.set(Phase.SELECT_FROM);
        invalidSelection = false;
    }

}
