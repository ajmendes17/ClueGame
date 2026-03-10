package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoardCell {
    private int row;
    private int col;
    private Set<TestBoardCell> adjList;
    private boolean isRoom;
    private boolean isOccupied;

    // Constructor
    public TestBoardCell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void addAdjacency(TestBoardCell cell) {
        adjacencies.add(cell);
    }

    // Return empty set
    public Set<TestBoardCell> getAdjList() {
        return new HashSet<>(adjacencies);
    }

    public void setRoom(boolean isRoom) {
        this.isRoomCell = isRoom;
    }

    public boolean isRoom() {
        return isRoomCell;
    }

    // Empty implementation
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean getOccupied() {
        return occupied;
    }
}
