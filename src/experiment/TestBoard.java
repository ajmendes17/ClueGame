package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoard {
    private TestBoardCell[][] grid;
    private Set<TestBoardCell> targets ;
    private Set<TestBoardCell> visited;
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
    }

    public TestBoardCell getCell(int row, int col) {
        return grid[row][col];
    }

    public Set<TestBoardCell> getTargets() {
        return new HashSet<>();
    }
}
