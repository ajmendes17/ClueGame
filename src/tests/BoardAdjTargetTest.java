package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;

public class BoardAdjTargetTest {
	private static Board board;

	@BeforeAll
	public static void setUp() {
		board = Board.getInstance();
		// change these filenames if yours are named differently
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();
	}

	// Test room center adjacencies, including secret passages
	@Test
	public void testAdjacencyRoomCenters() {
		// Moon Room center at (2,3), doorway plus secret passage to Yellow Room
		Set<BoardCell> testList = board.getAdjList(2, 3);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(5, 5)));
		assertTrue(testList.contains(board.getCell(22, 25)));

		// TV Room center at (2,25), doorway plus secret passage to Darts Room
		testList = board.getAdjList(2, 25);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(4, 21)));
		assertTrue(testList.contains(board.getCell(22, 3)));

		// Backyard center at (12,25), two doorway walkways
		testList = board.getAdjList(12, 25);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(8, 22)));
		assertTrue(testList.contains(board.getCell(16, 22)));

		// AppleTree center at (5,14), four doorway walkways
		testList = board.getAdjList(5, 14);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(6, 9)));
		assertTrue(testList.contains(board.getCell(6, 19)));
		assertTrue(testList.contains(board.getCell(11, 12)));
		assertTrue(testList.contains(board.getCell(11, 16)));
	}

	// Test that non-center room cells have no adjacencies
	@Test
	public void testAdjacencyRoomNonCenters() {
		Set<BoardCell> testList = board.getAdjList(0, 0);   // Moon Room non-center
		assertEquals(0, testList.size());

		testList = board.getAdjList(3, 12); // AppleTree non-center
		assertEquals(0, testList.size());

		testList = board.getAdjList(21, 24); // Yellow Room non-center
		assertEquals(0, testList.size());

		testList = board.getAdjList(23, 1); // Darts Room non-center
		assertEquals(0, testList.size());
	}

	// Test doorway cells
	@Test
	public void testAdjacencyDoorways() {
		// W^ into Moon Room
		Set<BoardCell> testList = board.getAdjList(5, 5);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(2, 3)));
		assertTrue(testList.contains(board.getCell(5, 4)));
		assertTrue(testList.contains(board.getCell(5, 6)));
		assertTrue(testList.contains(board.getCell(6, 5)));

		// W> into TV Room
		testList = board.getAdjList(4, 21);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(2, 25)));
		assertTrue(testList.contains(board.getCell(4, 20)));
		assertTrue(testList.contains(board.getCell(5, 21)));

		// Wv into Backyard
		testList = board.getAdjList(8, 22);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(12, 25)));
		assertTrue(testList.contains(board.getCell(7, 22)));
		assertTrue(testList.contains(board.getCell(8, 21)));

		// W< into Swimming Pool
		testList = board.getAdjList(15, 4);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(15, 2)));
		assertTrue(testList.contains(board.getCell(14, 4)));
		assertTrue(testList.contains(board.getCell(15, 5)));
		assertTrue(testList.contains(board.getCell(16, 4)));
	}

	// Test ordinary walkway cells
	@Test
	public void testAdjacencyWalkways() {
		// edge walkway
		Set<BoardCell> testList = board.getAdjList(0, 7);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(1, 7)));

		// walkway beside room but not doorway
		testList = board.getAdjList(1, 20);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(0, 20)));
		assertTrue(testList.contains(board.getCell(1, 19)));
		assertTrue(testList.contains(board.getCell(2, 20)));

		// interior walkway with four neighbors
		testList = board.getAdjList(18, 5);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(17, 5)));
		assertTrue(testList.contains(board.getCell(19, 5)));
		assertTrue(testList.contains(board.getCell(18, 4)));
		assertTrue(testList.contains(board.getCell(18, 6)));

		// bottom edge walkway
		testList = board.getAdjList(24, 11);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(23, 11)));
	}

	// Test targets starting in a room with a secret passage
	@Test
	public void testTargetsFromMoonRoom() {
		// roll of 1
		board.calcTargets(board.getCell(2, 3), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCell(5, 5)));
		assertTrue(targets.contains(board.getCell(22, 25)));

		// roll of 3
		board.calcTargets(board.getCell(2, 3), 3);
		targets = board.getTargets();
		assertEquals(6, targets.size());
		assertTrue(targets.contains(board.getCell(5, 3)));
		assertTrue(targets.contains(board.getCell(5, 7)));
		assertTrue(targets.contains(board.getCell(6, 4)));
		assertTrue(targets.contains(board.getCell(6, 6)));
		assertTrue(targets.contains(board.getCell(7, 5)));
		assertTrue(targets.contains(board.getCell(22, 25)));
	}

	// Test targets starting from a doorway
	@Test
	public void testTargetsFromDoorway() {
		// starting at Moon Room doorway
		board.calcTargets(board.getCell(5, 5), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(2, 3)));
		assertTrue(targets.contains(board.getCell(5, 4)));
		assertTrue(targets.contains(board.getCell(5, 6)));
		assertTrue(targets.contains(board.getCell(6, 5)));

		board.calcTargets(board.getCell(5, 5), 3);
		targets = board.getTargets();
		assertEquals(11, targets.size());
		assertTrue(targets.contains(board.getCell(2, 3)));
		assertTrue(targets.contains(board.getCell(5, 2)));
		assertTrue(targets.contains(board.getCell(5, 8)));
		assertTrue(targets.contains(board.getCell(7, 6)));
		assertTrue(targets.contains(board.getCell(9, 2)));
	}

	// Test targets along ordinary walkways
	@Test
	public void testTargetsFromWalkway() {
		board.calcTargets(board.getCell(18, 5), 2);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(7, targets.size());
		assertTrue(targets.contains(board.getCell(16, 5)));
		assertTrue(targets.contains(board.getCell(17, 4)));
		assertTrue(targets.contains(board.getCell(17, 6)));
		assertTrue(targets.contains(board.getCell(18, 3)));
		assertTrue(targets.contains(board.getCell(18, 7)));
		assertTrue(targets.contains(board.getCell(19, 4)));
		assertTrue(targets.contains(board.getCell(19, 6)));

		board.calcTargets(board.getCell(18, 5), 3);
		targets = board.getTargets();
		assertEquals(12, targets.size());
		assertTrue(targets.contains(board.getCell(15, 5)));
		assertTrue(targets.contains(board.getCell(16, 4)));
		assertTrue(targets.contains(board.getCell(16, 6)));
		assertTrue(targets.contains(board.getCell(17, 7)));
		assertTrue(targets.contains(board.getCell(18, 8)));
		assertTrue(targets.contains(board.getCell(19, 7)));
	}

	// Test targets leaving a room without a secret passage
	@Test
	public void testTargetsFromBackyard() {
		board.calcTargets(board.getCell(12, 25), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCell(8, 22)));
		assertTrue(targets.contains(board.getCell(16, 22)));

		board.calcTargets(board.getCell(12, 25), 3);
		targets = board.getTargets();
		assertEquals(10, targets.size());
		assertTrue(targets.contains(board.getCell(6, 22)));
		assertTrue(targets.contains(board.getCell(7, 21)));
		assertTrue(targets.contains(board.getCell(7, 23)));
		assertTrue(targets.contains(board.getCell(15, 21)));
		assertTrue(targets.contains(board.getCell(18, 22)));
	}

	// Test occupied cells
	@Test
	public void testTargetsOccupied() {
		// blocked walkway should not be included and should prevent movement through it
		board.getCell(19, 5).setOccupied(true);
		board.calcTargets(board.getCell(18, 5), 3);
		Set<BoardCell> targets = board.getTargets();
		board.getCell(19, 5).setOccupied(false);

		assertEquals(11, targets.size());
		assertFalse(targets.contains(board.getCell(19, 5)));
		assertFalse(targets.contains(board.getCell(20, 5)));

		// entering a room center should still be allowed even if occupied
		board.getCell(2, 3).setOccupied(true);
		board.calcTargets(board.getCell(5, 5), 1);
		targets = board.getTargets();
		board.getCell(2, 3).setOccupied(false);

		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(2, 3)));
		assertTrue(targets.contains(board.getCell(5, 4)));
		assertTrue(targets.contains(board.getCell(5, 6)));
		assertTrue(targets.contains(board.getCell(6, 5)));
	}
}