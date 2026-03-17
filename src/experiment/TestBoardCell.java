package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoardCell {
    private int row;
    private int col;
    private Set<TestBoardCell> adjacencies = new HashSet<>();
    private boolean isRoomCell;
    private boolean occupied;

    // Constructor
    public TestBoardCell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void addAdjacency(TestBoardCell cell) {
        adjacencies.add(cell);
    }

    public Set<TestBoardCell> getAdjList() {
        return adjacencies;
    }

    public void setRoom(boolean isRoom) {
        this.isRoomCell = isRoom;
    }

    public boolean isRoom() {
        return isRoomCell;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean getOccupied() {
        return occupied;
    }
}