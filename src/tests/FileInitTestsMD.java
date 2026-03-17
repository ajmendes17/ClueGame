package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.Room;
import clueGame.DoorDirection;

public class FileInitTestsMD {
	public static final int LEGEND_SIZE = 11;
	public static final int NUM_ROWS = 0; // Gotta change this later I'm just out of time
	public static final int NUM_COLUMNS = 0; // Gotta change this later I'm just out of time

	private static Board board;
    
    @BeforeEach
	public void setUp() {
		board = Board.getInstance();
		board.setConfigFiles("data/ClueMap.csv", "data/ClueSetup.txt");
		board.initialize();
	}

    @Test
    public void testRoomMap() {
        assertEquals(11, board.getRoomMap().size());

        assertEquals("TV Room", board.getRoom('T').getName());
        assertEquals("AppleTree", board.getRoom('A').getName());
        assertEquals("Moon Room", board.getRoom('M').getName());
        assertEquals("Knitting Room", board.getRoom('K').getName());
        assertEquals("Swimming Pool", board.getRoom('S').getName());
        assertEquals("Darts Room", board.getRoom('D').getName());
        assertEquals("Isolation Quarters", board.getRoom('I').getName());
        assertEquals("Yellow Room", board.getRoom('Y').getName());
        assertEquals("Backyard", board.getRoom('B').getName());

        assertEquals("Walkway", board.getRoom('W').getName());
        assertEquals("Unused Area", board.getRoom('X').getName());
    }
    
    @Test
	public void testBoardDimensions() {
		assertEquals(NUM_ROWS, board.getNumRows());
		assertEquals(NUM_COLUMNS, board.getNumColumns());
	}
    
}