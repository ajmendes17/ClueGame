package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.Room;
import clueGame.DoorDirection;

public class FileInitTestsMD {
	public static final int LEGEND_SIZE = 11;
	public static final int NUM_ROWS = 25;
	public static final int NUM_COLUMNS = 29;
	public static final int NUM_DOORS = 15;

	private static Board board;
	    
    @BeforeEach
	public void setUp() {
		board = Board.getInstance();
		board.setConfigFiles("data/ClueMap.csv", "data/ClueSetup.txt");
		board.initialize();
	}

	// Check that the setup file loads the expected rooms and spaces into the legend.
    @Test
    public void testRoomMap() {
        assertEquals(LEGEND_SIZE, board.getRoomMap().size());

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
	    
	// Check that the board reads the correct number of rows and columns from the layout file.
	    @Test
	public void testBoardDimensions() {
		assertEquals(NUM_ROWS, board.getNumRows());
		assertEquals(NUM_COLUMNS, board.getNumColumns());
	}

	// Test one doorway in each direction and confirm non-door cells return false.
	@Test
	public void testDoorDirections() {
		BoardCell cell = board.getCell(5, 5);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.UP, cell.getDoorDirection());

		cell = board.getCell(8, 22);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.DOWN, cell.getDoorDirection());

		cell = board.getCell(20, 7);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.LEFT, cell.getDoorDirection());

		cell = board.getCell(4, 21);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.RIGHT, cell.getDoorDirection());

		cell = board.getCell(5, 0);
		assertFalse(cell.isDoorway());

		cell = board.getCell(1, 3);
		assertFalse(cell.isDoorway());
	}

	// Check that the layout contains the expected total number of doorway cells.
	@Test
	public void testNumberOfDoorways() {
		int numDoors = 0;
		for (int row = 0; row < board.getNumRows(); row++) {
			for (int col = 0; col < board.getNumColumns(); col++) {
				if (board.getCell(row, col).isDoorway()) {
					numDoors++;
				}
			}
		}

		assertEquals(NUM_DOORS, numDoors);
	}

	// Test representative cells for the expected initials, labels, centers, and room mapping.
	@Test
	public void testCellsAndRoomMetadata() {
		BoardCell cell = board.getCell(2, 3);
		Room room = board.getRoom(cell);
		assertEquals('M', cell.getInitial());
		assertEquals("Moon Room", room.getName());
		assertTrue(cell.isRoomCenter());
		assertSame(cell, room.getCenterCell());

		cell = board.getCell(1, 3);
		room = board.getRoom(cell);
		assertEquals('M', cell.getInitial());
		assertEquals("Moon Room", room.getName());
		assertTrue(cell.isLabel());
		assertSame(cell, room.getLabelCell());

		cell = board.getCell(5, 14);
		room = board.getRoom(cell);
		assertEquals('A', cell.getInitial());
		assertEquals("AppleTree", room.getName());
		assertTrue(cell.isRoomCenter());
		assertSame(cell, room.getCenterCell());

		cell = board.getCell(5, 0);
		room = board.getRoom(cell);
		assertEquals('W', cell.getInitial());
		assertEquals("Walkway", room.getName());
		assertFalse(cell.isRoomCenter());
		assertFalse(cell.isLabel());

		cell = board.getCell(0, 8);
		room = board.getRoom(cell);
		assertEquals('X', cell.getInitial());
		assertEquals("Unused Area", room.getName());
		assertFalse(cell.isRoomCenter());
		assertFalse(cell.isLabel());
	}

	// Check that the custom board's secret passage markers were loaded correctly.
	@Test
	public void testSecretPassages() {
		BoardCell cell = board.getCell(4, 1);
		assertEquals('Y', cell.getSecretPassage());

		cell = board.getCell(20, 28);
		assertEquals('M', cell.getSecretPassage());

		cell = board.getCell(4, 28);
		assertEquals('D', cell.getSecretPassage());

		cell = board.getCell(20, 0);
		assertEquals('T', cell.getSecretPassage());
	}
    
}
