package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

public class BoardCell {
	private static final Color WALKWAY_COLOR = new Color(245, 218, 135);
	private static final Color ROOM_COLOR = new Color(190, 190, 190);
	private static final Color UNUSED_COLOR = new Color(45, 45, 45);

	private final int row;
	private final int col;

	private char initial;
	private DoorDirection doorDirection;
	private boolean labelCell;
	private boolean centerCell;
	private char secretPassage;
	private final Set<BoardCell> adjacencies;
	private boolean occupied;

	public BoardCell(int row, int col) {
		this.row = row;
		this.col = col;
		doorDirection = DoorDirection.NONE;
		secretPassage = ' ';
		adjacencies = new HashSet<>();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public char getInitial() {
		return initial;
	}

	public void setInitial(char initial) {
		this.initial = initial;
	}

	public DoorDirection getDoorDirection() {
		return doorDirection;
	}

	public void setDoorDirection(DoorDirection doorDirection) {
		this.doorDirection = doorDirection;
	}

	public boolean isDoorway() {
		return doorDirection != DoorDirection.NONE;
	}

	public boolean isLabel() {
		return labelCell;
	}

	public void setRoomLabel(boolean roomLabel) {
		labelCell = roomLabel;
	}

	public boolean isRoomCenter() {
		return centerCell;
	}

	public void setRoomCenter(boolean roomCenter) {
		centerCell = roomCenter;
	}

	public char getSecretPassage() {
		return secretPassage;
	}

	public void setSecretPassage(char secretPassage) {
		this.secretPassage = secretPassage;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	public void addAdjacency(BoardCell cell) {
		adjacencies.add(cell);
	}

	public void addAdj(BoardCell cell) {
		addAdjacency(cell);
	}

	public void clearAdjacencies() {
		adjacencies.clear();
	}

	public Set<BoardCell> getAdjList() {
		return adjacencies;
	}

	public void draw(Graphics g, int cellSize, int xOffset, int yOffset) {
		int x = xOffset + col * cellSize;
		int y = yOffset + row * cellSize;

		g.setColor(getCellColor());
		g.fillRect(x, y, cellSize, cellSize);

		g.setColor(Color.BLACK);
		g.drawRect(x, y, cellSize, cellSize);
	}

	private Color getCellColor() {
		if (initial == 'W') {
			return WALKWAY_COLOR;
		}
		if (initial == 'X') {
			return UNUSED_COLOR;
		}
		return ROOM_COLOR;
	}
}
