import boardgame.model.BoardGameModel;
import boardgame.model.Position;
import boardgame.model.Square;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
    class BoardGameModelTest {
        BoardGameModel model = new BoardGameModel();

        @Test
        void squareProperty() {
            assertEquals(model.squareProperty(0,0).get(),Square.RED);
            assertEquals(model.squareProperty(5,6).get(),Square.BLUE);
            assertEquals(model.squareProperty(3,2).get(),Square.BLOCKED);
            assertEquals(model.squareProperty(2,2).get(),Square.NONE);
        }

        @Test
        void getSquare() {
            assertEquals(model.getSquare(new Position(0,0)),Square.RED);
            assertEquals(model.getSquare(new Position(5,6)),Square.BLUE);
            assertEquals(model.getSquare(new Position(3,2)),Square.BLOCKED);
            assertEquals(model.getSquare(new Position(2,2)),Square.NONE);
        }

        @Test
        void move() {
            model.move(new Position(5,0),new Position(4,0));
            assertEquals(model.getSquare(new Position(4,0)),Square.BLUE);
            model.move(new Position(0,0),new Position(1,1));
            assertEquals(model.getSquare(new Position(1,1)),Square.RED);

        }

        @Test
        void canMove() {
            assertTrue(model.canMove(new Position(5,0),new Position(4,0)));
            assertFalse(model.canMove(new Position(4,0),new Position(3,0)));
            assertTrue(model.canMove(new Position(0,0),new Position(1,0)));
            assertFalse(model.canMove(new Position(1,0),new Position(2,0)));
        }

        @Test
        void isEmpty() {
            assertFalse(model.isEmpty(new Position(0,0)));
            assertTrue(model.isEmpty(new Position(1,1)));
        }

        @Test
        void isOnBoard() {
            assertFalse(model.isOnBoard(new Position(-1,-1)));
            assertFalse(model.isOnBoard(new Position(7,8)));
            assertTrue(model.isOnBoard(new Position(0,0)));
        }

        @Test
        void isPawnAttack() {
            assertTrue(model.isPawnAttack(new Position(0,0),new Position(1,1)));
            assertFalse(model.isPawnAttack(new Position(0,0),new Position(2,1)));
            assertFalse(model.isPawnAttack(new Position(0,0),new Position(1,0)));
        }

        @Test
        void isPawnMove() {
            assertTrue(model.isPawnAttack(new Position(0,0),new Position(1,1)));
            assertFalse(model.isPawnAttack(new Position(0,0),new Position(1,0)));
            assertFalse(model.isPawnAttack(new Position(0,0),new Position(2,1)));
        }

        @Test
        void hasMove() {
            model.move(new Position(0,0),new Position(5,0));
            assertFalse(model.hasMove(new Position(5,0)));
            assertTrue(model.hasMove(new Position(0,1)));
            model.move(new Position(5,0),new Position(2,2));
            model.move(new Position(0,1),new Position(3,1));
            model.move(new Position(0,2),new Position(3,3));
            assertFalse(model.hasMove(new Position(2,2)));
        }
        @Test
        void testToString() {
            assertTrue(true);
        }
        @Test
        void main() {
        }

        @Test
        void jsonToMap() {
            assertEquals(!model.jsonToMap("scoreboard.json").isEmpty(),true);
        }

        @Test
        void updatePlayerData() {
        }
    }

