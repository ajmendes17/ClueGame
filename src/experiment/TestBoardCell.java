//add header block

package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoardCell {
    private int row;
    private int col;
    private Set<TestBoardCell> adjList = new HashSet<>();
    private boolean isRoom;
    private boolean isOccupied;

    // Constructor
    public TestBoardCell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void addAdjacency(TestBoardCell cell) {
        adjList.add(cell);
    }

    // Return empty set
    public Set<TestBoardCell> getAdjList() {
        return new HashSet<>(adjList);
    }

    public void setRoom(boolean isRoom) {
        this.isRoom = isRoom;
    }

    public boolean isRoom() {
        return isRoom;
    }

    // Empty implementation
    public void setOccupied(boolean occupied) {
        this.isOccupied = occupied;
    }

    public boolean getOccupied() {
        return isOccupied;
    }
}
