package clueGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Board {
	// added NEIGHBOR_OFFSETS as a helper during refactoring
	private static final int[][] NEIGHBOR_OFFSETS = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

	private BoardCell[][] grid;
	private int numRows;
	private int numColumns;

	private String layoutConfigFile;
	private String setupConfigFile;

	private Map<Character, Room> roomMap;

	private Set<BoardCell> targets;
	private Set<BoardCell> visited;

	/*
	 * variable and methods used for singleton pattern
	 */
	private static Board theInstance = new Board();

	// constructor is private to ensure only one can be created
	private Board() {
		super();
	}

	// this method returns the only Board
	public static Board getInstance() {
		return theInstance;
	}

	/*
	 * initialize the board (since we are using singleton pattern)
	 */
	public void initialize() {
		targets = new HashSet<>();
		visited = new HashSet<>();
		try {
			loadSetupConfig();
			loadLayoutConfig();
		} catch (BadConfigFormatException e) {
			System.out.println(e.getMessage());
		}
	}

	// set configuration files
	public void setConfigFiles(String layoutConfigFile, String setupConfigFile) {
		this.layoutConfigFile = layoutConfigFile;
		this.setupConfigFile = setupConfigFile;
	}

	// load setup file
	public void loadSetupConfig() throws BadConfigFormatException {
		roomMap = new HashMap<>();
		
		// Put the Scanner into the try statement so we can get rid 
		// of so many repetitive scanner.close statements
		try (Scanner scanner = new Scanner(new File(setupConfigFile))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();

				if (line.isEmpty() || line.startsWith("//")) {
					continue;
				}

				String[] parts = line.split(",\\s*");

				if (parts.length != 3) {
					throw new BadConfigFormatException("Bad setup line format: " + line);
				}

				String type = parts[0];
				String name = parts[1];
				char initial = parts[2].charAt(0);

				if (!type.equals("Room") && !type.equals("Space")) {
					throw new BadConfigFormatException("Bad room type: " + line);
				}

				roomMap.put(initial, new Room(name));
			}
		} catch (FileNotFoundException e) {
			System.out.println("Setup file not found: " + setupConfigFile);
		}
	}

	// load layout file
	public void loadLayoutConfig() throws BadConfigFormatException {
		ArrayList<String[]> lines = new ArrayList<>();
		grid = null;
		numRows = 0;
		numColumns = 0;

		try (Scanner scanner = new Scanner(new File(layoutConfigFile))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if (line.isEmpty()) {
					continue;
				}

				String[] parts = line.split(",");

				if (!lines.isEmpty() && parts.length != lines.get(0).length) {
					throw new BadConfigFormatException("Inconsistent number of columns");
				}

				lines.add(parts);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Layout file not found: " + layoutConfigFile);
			return;
		}

		numRows = lines.size();
		numColumns = lines.get(0).length;
		grid = new BoardCell[numRows][numColumns];

		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numColumns; col++) {
				String token = lines.get(row)[col].trim();

				if (token.length() < 1 || token.length() > 2) {
					throw new BadConfigFormatException("Bad layout token: " + token);
				}

				char initial = token.charAt(0);

				if (!roomMap.containsKey(initial)) {
					throw new BadConfigFormatException("Unknown room initial: " + initial);
				}

				BoardCell cell = new BoardCell(row, col);
				cell.setInitial(initial);

				if (token.length() == 2) {
					char marker = token.charAt(1);

					switch (marker) {
					case '#':
						cell.setRoomLabel(true);
						roomMap.get(initial).setLabelCell(cell);
						break;
					case '*':
						cell.setRoomCenter(true);
						roomMap.get(initial).setCenterCell(cell);
						break;
					case '^':
						cell.setDoorDirection(DoorDirection.UP);
						break;
					case 'v':
						cell.setDoorDirection(DoorDirection.DOWN);
						break;
					case '<':
						cell.setDoorDirection(DoorDirection.LEFT);
						break;
					case '>':
						cell.setDoorDirection(DoorDirection.RIGHT);
						break;
					default:
						if (Character.isLetter(marker) && roomMap.containsKey(marker)) {
							cell.setSecretPassage(marker);
						} else {
							throw new BadConfigFormatException("Bad cell marker: " + token);
						}
					}
				}

				grid[row][col] = cell;
			}
		}

		calcAdjacencies();
	}

	public void calcAdjacencies() {
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numColumns; col++) {
				BoardCell cell = grid[row][col];
				cell.clearAdjacencies();

				if (isUnused(cell)) {
					continue;
				}

				if (isWalkway(cell)) {
					addWalkwayAdjacencies(cell);
				} else if (cell.isRoomCenter()) {
					addRoomCenterAdjacencies(cell);
				}
			}
		}
	}

	private void addWalkwayAdjacencies(BoardCell cell) {
		
		// Got rid of all the addWalkwayOrDoorAdjacency calls
		for (int[] offset : NEIGHBOR_OFFSETS) {
			addWalkwayOrDoorAdjacency(cell, cell.getRow() + offset[0], cell.getCol() + offset[1]);
		}

		if (cell.isDoorway()) {
			BoardCell roomCenter = getDoorwayRoomCenter(cell);
			if (roomCenter != null) {
				cell.addAdj(roomCenter);
			}
		}
	}

	private void addWalkwayOrDoorAdjacency(BoardCell source, int row, int col) {
		if (!isInBounds(row, col)) {
			return;
		}

		BoardCell neighbor = grid[row][col];
		if (isWalkway(neighbor)) {
			source.addAdj(neighbor);
			return;
		}

		if (neighbor.isDoorway() && doorwayFacesCell(neighbor, source)) {
			BoardCell roomCenter = getDoorwayRoomCenter(neighbor);
			if (roomCenter != null) {
				source.addAdj(roomCenter);
			}
		}
	}

	private void addRoomCenterAdjacencies(BoardCell centerCell) {
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numColumns; col++) {
				BoardCell cell = grid[row][col];

				if (!cell.isDoorway()) {
					continue;
				}

				BoardCell doorwayRoomCenter = getDoorwayRoomCenter(cell);
				if (doorwayRoomCenter == centerCell) {
					centerCell.addAdj(cell);
				}
			}
		}
		
		BoardCell secretPassageDestination = getSecretPassageDestination(centerCell);
		if (secretPassageDestination != null) {
			centerCell.addAdj(secretPassageDestination);
		}
	}
	
	// New stepFromDoorway logic got rid of all the case break situations
	private BoardCell getDoorwayDestination(BoardCell doorway) {
		int[] location = stepFromDoorway(doorway);
		if (location == null) {
			return null;
		}

		if (!isInBounds(location[0], location[1])) {
			return null;
		}

		BoardCell destination = grid[location[0]][location[1]];
		return isWalkway(destination) ? destination : null;
	}
	
	// Better formatting and built up methods for getDoorwayRoomCenter
	private BoardCell getDoorwayRoomCenter(BoardCell doorway) {
		int[] location = stepFromDoorway(doorway);
		if (location == null || !isInBounds(location[0], location[1])) {
			return null;
		}

		BoardCell roomCell = grid[location[0]][location[1]];
		Room room = roomMap.get(roomCell.getInitial());
		if (room == null) {
			return null;
		}

		return room.getCenterCell();
	}

	private int[] stepFromDoorway(BoardCell doorway) {
		int row = doorway.getRow();
		int col = doorway.getCol();

		// got rid of all the row +-; break; clauses
		switch (doorway.getDoorDirection()) {
		case UP:
			return new int[] {row - 1, col};
		case DOWN:
			return new int[] {row + 1, col};
		case LEFT:
			return new int[] {row, col - 1};
		case RIGHT:
			return new int[] {row, col + 1};
		case NONE:
		default:
			return null;
		}
	}

	private BoardCell getSecretPassageDestination(BoardCell centerCell) {
		char roomInitial = centerCell.getInitial();

		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numColumns; col++) {
				BoardCell cell = grid[row][col];
				if (cell.getInitial() != roomInitial || cell.getSecretPassage() == ' ') {
					continue;
				}

				Room destinationRoom = roomMap.get(cell.getSecretPassage());
				if (destinationRoom != null) {
					return destinationRoom.getCenterCell();
				}
			}
		}

		return null;
	}

	private boolean doorwayFacesCell(BoardCell doorway, BoardCell candidate) {
		BoardCell destination = getDoorwayDestination(doorway);
		return destination != null && destination == candidate;
	}

	private boolean isWalkway(BoardCell cell) {
		return cell.getInitial() == 'W';
	}

	private boolean isUnused(BoardCell cell) {
		return cell.getInitial() == 'X';
	}

	private boolean isInBounds(int row, int col) {
		return row >= 0 && row < numRows && col >= 0 && col < numColumns;
	}

	public Set<BoardCell> getAdjList(int row, int col) {
		return grid[row][col].getAdjList();
	}

	public void calcTargets(BoardCell startCell, int pathLength) {
		targets = new HashSet<>();
		visited = new HashSet<>();
		visited.add(startCell);
		findAllTargets(startCell, pathLength);
	}

	private void findAllTargets(BoardCell thisCell, int steps) {
		for (BoardCell adjCell : thisCell.getAdjList()) {
			if (visited.contains(adjCell)) {
				continue;
			}
			if (adjCell.isOccupied() && !adjCell.isRoomCenter()) {
				continue;
			}

			visited.add(adjCell);

			if (steps == 1 || adjCell.isRoomCenter()) {
				targets.add(adjCell);
			} else {
				findAllTargets(adjCell, steps - 1);
			}

			visited.remove(adjCell);
		}
	}

	public Set<BoardCell> getTargets() {
		return targets;
	}

	// getters
	public int getNumRows() {
		return numRows;
	}

	public int getNumColumns() {
		return numColumns;
	}

	public BoardCell getCell(int row, int col) {
		return grid[row][col];
	}

	public Map<Character, Room> getRoomMap() {
		return roomMap;
	}

	public Room getRoom(BoardCell cell) {
		return roomMap.get(cell.getInitial());
	}

	public Room getRoom(char initial) {
		return roomMap.get(initial);
	}
}
