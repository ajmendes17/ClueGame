package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoard {

    private TestBoardCell[][] grid;
    private Set<TestBoardCell> targets;
    private Set<TestBoardCell> visited;

    public TestBoard() {

        grid = new TestBoardCell[4][4];

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                grid[row][col] = new TestBoardCell(row, col);
            }
        }

        calcAdjacencies();
    }

    private void calcAdjacencies() {

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {

                TestBoardCell cell = grid[row][col];

                if (row > 0) cell.addAdjacency(grid[row - 1][col]);
                if (row < 3) cell.addAdjacency(grid[row + 1][col]);
                if (col > 0) cell.addAdjacency(grid[row][col - 1]);
                if (col < 3) cell.addAdjacency(grid[row][col + 1]);
            }
        }
    }

    public void calcTargets(TestBoardCell startCell, int pathlength) {

        targets = new HashSet<>();
        visited = new HashSet<>();

        visited.add(startCell);
        findAllTargets(startCell, pathlength);
    }

    private void findAllTargets(TestBoardCell cell, int steps) {

        for (TestBoardCell adj : cell.getAdjList()) {

            if (visited.contains(adj) || adj.getOccupied()) continue;

            visited.add(adj);

            // any reachable cell is a target
            targets.add(adj);

            // stop recursion if room, otherwise keep going if steps remain
            if (!adj.isRoom() && steps > 1) {
                findAllTargets(adj, steps - 1);
            }

            visited.remove(adj);
        }
    }

    public TestBoardCell getCell(int row, int col) {
        return grid[row][col];
    }

    public Set<TestBoardCell> getTargets() {
        return targets;
    }
}