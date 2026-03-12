//add comments

package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoard {
    private TestBoardCell[][] grid;
    private Set<TestBoardCell> targets = new HashSet<>();
    private Set<TestBoardCell> visited = new HashSet<>();;
    final static int COLS = 4;
    final static int ROWS = 4;

    // Constructor that does just enough to test
    public TestBoard() {
        grid = new TestBoardCell[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = new TestBoardCell(row, col);
            }
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (row - 1 >= 0) grid[row][col].addAdjacency(grid[row - 1][col]);
                if (row + 1 < ROWS) grid[row][col].addAdjacency(grid[row + 1][col]);
                if (col - 1 >= 0) grid[row][col].addAdjacency(grid[row][col - 1]);
                if (col + 1 < COLS) grid[row][col].addAdjacency(grid[row][col + 1]);
            }
        }
    }

    public void calcTargets(TestBoardCell startCell, int pathlength) {
        targets.clear();
        visited.clear();
        calcTargetsRecurse(startCell, pathlength, pathlength);
    }

    private void calcTargetsRecurse(TestBoardCell cell, int stepsLeft, int pathlength) {
        if (visited.contains(cell)) {
        	return;
        }
        visited.add(cell);
        if (!cell.getOccupied() && stepsLeft < pathlength) {
            targets.add(cell);
        }
        if (stepsLeft == 0) {
            visited.remove(cell);
            return;
        }
        if (cell.isRoom()) {
            visited.remove(cell);
            return;
        }
        for (TestBoardCell neighbor : cell.getAdjList()) {
            if (!visited.contains(neighbor) && !neighbor.getOccupied()) {
                calcTargetsRecurse(neighbor, stepsLeft - 1, pathlength);
            }
        }
        visited.remove(cell);
    }

    public TestBoardCell getCell(int row, int col) {
        return grid[row][col];
    }

    public Set<TestBoardCell> getTargets() {
        return targets;
    }
}
