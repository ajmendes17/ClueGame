package tests;

import experiment.TestBoard;
import experiment.TestBoardCell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*; // don't need Assert.

/**
 * JUnit tests for TestBoard adjacency lists and calcTargets.
 */
class BoardTestsExp {

    private TestBoard board;

    @BeforeEach
    void setUp() {
        board = new TestBoard();
    }

    // Top-left corner [0][0] should have 2 adjacencies: right and down
    @Test
    void testAdjacencyTopLeftCorner() {
        TestBoardCell cell = board.getCell(0, 0);
        Set<TestBoardCell> adjList = cell.getAdjList();
        assertEquals(2, adjList.size());
        assertTrue(adjList.contains(board.getCell(0, 1)));
        assertTrue(adjList.contains(board.getCell(1, 0)));
    }

    // Bottom-right corner [3][3] should have 2 adjacencies: left and up
    @Test
    void testAdjacencyBottomRightCorner() {
        TestBoardCell cell = board.getCell(3, 3);
        Set<TestBoardCell> adjList = cell.getAdjList();
        assertEquals(2, adjList.size());
        assertTrue(adjList.contains(board.getCell(3, 2)));
        assertTrue(adjList.contains(board.getCell(2, 3)));
    }

    // Right edge [1][3] should have 3 adjacencies: left, up, down
    @Test
    void testAdjacencyRightEdge() {
        TestBoardCell cell = board.getCell(1, 3);
        Set<TestBoardCell> adjList = cell.getAdjList();
        assertEquals(3, adjList.size());
        assertTrue(adjList.contains(board.getCell(1, 2)));
        assertTrue(adjList.contains(board.getCell(0, 3)));
        assertTrue(adjList.contains(board.getCell(2, 3)));
    }

    // Left edge [3][0] should have 2 adjacencies: right and up
    @Test
    void testAdjacencyLeftEdge() {
        TestBoardCell cell = board.getCell(3, 0);
        Set<TestBoardCell> adjList = cell.getAdjList();
        assertEquals(2, adjList.size());
        assertTrue(adjList.contains(board.getCell(3, 1)));
        assertTrue(adjList.contains(board.getCell(2, 0)));
    }

    // Middle of grid [2][2] should have 4 adjacencies: up, down, left, right
    @Test
    void testAdjacencyMiddleOfGrid() {
        TestBoardCell cell = board.getCell(2, 2);
        Set<TestBoardCell> adjList = cell.getAdjList();
        assertEquals(4, adjList.size());
        assertTrue(adjList.contains(board.getCell(2, 1)));
        assertTrue(adjList.contains(board.getCell(2, 3)));
        assertTrue(adjList.contains(board.getCell(1, 2)));
        assertTrue(adjList.contains(board.getCell(3, 2)));
    }

    // From [0][0] with pathlength 1, targets should be [0][1] and [1][0]
    @Test
    void testTargetsFromTopLeftOneStep() {
        board.calcTargets(board.getCell(0, 0), 1);
        Set<TestBoardCell> targets = board.getTargets();
        assertEquals(2, targets.size());
        assertTrue(targets.contains(board.getCell(0, 1)));
        assertTrue(targets.contains(board.getCell(1, 0)));
    }

    // From [1][1] with pathlength 2, should reach cells exactly 2 steps away
    @Test
    void testTargetsFromCenterTwoSteps() {
        board.calcTargets(board.getCell(1, 1), 2);
        Set<TestBoardCell> targets = board.getTargets();
        assertTrue(targets.size() > 0);
        assertTrue(targets.contains(board.getCell(0, 1)));
        assertTrue(targets.contains(board.getCell(2, 1)));
        assertTrue(targets.contains(board.getCell(1, 0)));
        assertTrue(targets.contains(board.getCell(1, 2)));
    }

    // From [2][2] with pathlength 6 (max die roll)
    @Test
    void testTargetsMaxRollSix() {
        board.calcTargets(board.getCell(2, 2), 6);
        Set<TestBoardCell> targets = board.getTargets();
        assertTrue(targets.size() > 0);
    }

    // From [3][3] with pathlength 1, targets should be [3][2] and [2][3]
    @Test
    void testTargetsFromBottomRightOneStep() {
        board.calcTargets(board.getCell(3, 3), 1);
        Set<TestBoardCell> targets = board.getTargets();
        assertEquals(2, targets.size());
        assertTrue(targets.contains(board.getCell(3, 2)));
        assertTrue(targets.contains(board.getCell(2, 3)));
    }

    // From [0][0] with pathlength 3
    @Test
    void testTargetsFromTopLeftThreeSteps() {
        board.calcTargets(board.getCell(0, 0), 3);
        Set<TestBoardCell> targets = board.getTargets();
        assertTrue(targets.size() > 0);
    }

    // With a room at [1][1], moving from [0][1] with pathlength 2 should stop at room
    @Test
    void testTargetsWithRoom() {
        board.getCell(1, 1).setRoom(true);
        board.calcTargets(board.getCell(0, 1), 2);
        Set<TestBoardCell> targets = board.getTargets();
        assertTrue(targets.contains(board.getCell(1, 1)));
    }

    // Room as target from adjacent cell with pathlength 1
    @Test
    void testTargetsRoomAdjacent() {
        board.getCell(2, 2).setRoom(true);
        board.calcTargets(board.getCell(2, 1), 1);
        Set<TestBoardCell> targets = board.getTargets();
        assertTrue(targets.contains(board.getCell(2, 2)));
    }

    // With [1][0] occupied, from [0][0] pathlength 2 should not include [1][0] or paths through it
    @Test
    void testTargetsWithOccupied() {
        board.getCell(1, 0).setOccupied(true);
        board.calcTargets(board.getCell(0, 0), 2);
        Set<TestBoardCell> targets = board.getTargets();
        assertFalse(targets.contains(board.getCell(1, 0)));
    }

    // Occupied cell blocks path
    @Test
    void testTargetsOccupiedBlocksPath() {
        board.getCell(1, 1).setOccupied(true);
        board.calcTargets(board.getCell(0, 1), 3);
        Set<TestBoardCell> targets = board.getTargets();
        assertFalse(targets.contains(board.getCell(1, 1)));
    }

    // Room and occupied both affect targets
    @Test
    void testTargetsMixedRoomAndOccupied() {
        board.getCell(1, 1).setRoom(true);
        board.getCell(2, 1).setOccupied(true);
        board.calcTargets(board.getCell(0, 1), 3);
        Set<TestBoardCell> targets = board.getTargets();
        assertTrue(targets.contains(board.getCell(1, 1)));
        assertFalse(targets.contains(board.getCell(2, 1)));
    }

    @Test
    void testTargetsMixedTwo() {
        board.getCell(3, 2).setRoom(true);
        board.getCell(2, 2).setOccupied(true);
        board.calcTargets(board.getCell(3, 1), 2);
        Set<TestBoardCell> targets = board.getTargets();
        assertFalse(targets.contains(board.getCell(2, 2)));
    }
}
