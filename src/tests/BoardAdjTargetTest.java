package tests;

import static org.junit.Assert.*;

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
		board.setConfigFiles("data/ClueMap.csv", "data/ClueSetup.txt");
		board.initialize();
	}

	// Check a walkway that should only connect to surrounding walkways.
	@Test
	public void testAdjacencyWalkwayOnly() {
		Set<BoardCell> testList = board.getAdjList(3, 8);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(2, 8)));
		assertTrue(testList.contains(board.getCell(4, 8)));
		assertTrue(testList.contains(board.getCell(3, 7)));
		assertTrue(testList.contains(board.getCell(3, 9)));
	}

	// Check that a room cell that is not the center has no adjacencies.
	@Test
	public void testAdjacencyInsideRoomNotCenter() {
		Set<BoardCell> testList = board.getAdjList(0, 0);
		assertEquals(0, testList.size());
	}

	// Check walkway cells on the edges of the board.
	@Test
	public void testAdjacencyAtBoardEdges() {
		Set<BoardCell> testList = board.getAdjList(0, 7);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(1, 7)));

		testList = board.getAdjList(18, 0);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(18, 1)));

		testList = board.getAdjList(18, 28);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(18, 27)));

		testList = board.getAdjList(24, 11);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(23, 11)));
	}

	// Check that a walkway next to a room wall does not connect through a non-door room cell.
	@Test
	public void testAdjacencyBesideRoomWall() {
		Set<BoardCell> testList = board.getAdjList(0, 7);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(1, 7)));
		assertFalse(testList.contains(board.getCell(0, 6)));
	}

	// Check doorway walkway cells in all four directions.
	@Test
	public void testAdjacencyDoorways() {
		Set<BoardCell> testList = board.getAdjList(5, 5);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(5, 4)));
		assertTrue(testList.contains(board.getCell(5, 6)));
		assertTrue(testList.contains(board.getCell(6, 5)));
		assertTrue(testList.contains(board.getCell(2, 3)));

		testList = board.getAdjList(8, 22);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(7, 22)));
		assertTrue(testList.contains(board.getCell(8, 21)));
		assertTrue(testList.contains(board.getCell(12, 25)));

		testList = board.getAdjList(20, 7);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(19, 7)));
		assertTrue(testList.contains(board.getCell(20, 8)));
		assertTrue(testList.contains(board.getCell(22, 3)));

		testList = board.getAdjList(4, 21);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(4, 20)));
		assertTrue(testList.contains(board.getCell(5, 21)));
		assertTrue(testList.contains(board.getCell(2, 25)));
	}

	// Check that room centers connect through secret passages.
	@Test
	public void testAdjacencySecretPassage() {
		Set<BoardCell> testList = board.getAdjList(2, 3);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(5, 5)));
		assertTrue(testList.contains(board.getCell(22, 25)));
	}

	// Check walkway targets at different path lengths.
	@Test
	public void testTargetsAlongWalkways() {
		board.calcTargets(board.getCell(3, 8), 2);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(2, 7)));
		assertTrue(targets.contains(board.getCell(4, 7)));
		assertTrue(targets.contains(board.getCell(4, 9)));
		assertTrue(targets.contains(board.getCell(5, 8)));

		board.calcTargets(board.getCell(5, 4), 1);
		targets = board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(5, 3)));
		assertTrue(targets.contains(board.getCell(5, 5)));
		assertTrue(targets.contains(board.getCell(6, 4)));
	}

	// Check that a target calculation can include entering a room.
	@Test
	public void testTargetsEnteringRoom() {
		board.calcTargets(board.getCell(5, 4), 2);
		Set<BoardCell> targets = board.getTargets();
		assertTrue(targets.contains(board.getCell(2, 3)));
	}

	// Check targets when leaving a room without a secret passage.
	@Test
	public void testTargetsLeavingRoomWithoutPassage() {
		board.calcTargets(board.getCell(5, 14), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(6, 9)));
		assertTrue(targets.contains(board.getCell(6, 19)));
		assertTrue(targets.contains(board.getCell(11, 12)));
		assertTrue(targets.contains(board.getCell(11, 16)));
	}

	// Check targets when leaving a room with a secret passage.
	@Test
	public void testTargetsLeavingRoomWithPassage() {
		board.calcTargets(board.getCell(2, 3), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCell(5, 5)));
		assertTrue(targets.contains(board.getCell(22, 25)));
	}

	// Check that occupied cells block paths during target calculation.
	@Test
	public void testTargetsBlockedByOccupiedCell() {
		board.getCell(4, 8).setOccupied(true);
		board.calcTargets(board.getCell(3, 8), 2);
		board.getCell(4, 8).setOccupied(false);

		Set<BoardCell> targets = board.getTargets();
		assertTrue(targets.contains(board.getCell(2, 7)));
		assertTrue(targets.contains(board.getCell(4, 7)));
		assertTrue(targets.contains(board.getCell(4, 9)));
		assertFalse(targets.contains(board.getCell(5, 8)));
	}
}
