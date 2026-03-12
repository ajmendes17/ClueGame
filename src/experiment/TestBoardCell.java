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

    // Empty implementation
    public void addAdjacency(TestBoardCell cell) {
        
    }

    // Return empty set
    public Set<TestBoardCell> getAdjList() {
        return new HashSet<>(adjacencies);
    }

    // Empty implementation
    public void setRoom(boolean isRoom) {
        
    }

    // Return false
    public boolean isRoom() {
        return false;
    }

    // Empty implementation
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    // return false
    public boolean getOccupied() {
        return false;
    }
}
