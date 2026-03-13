package clueGame;

import java.util.HashMap;
import java.util.Map;

public class Board {

	// board structure
	private BoardCell[][] grid;
	private int numRows;
	private int numColumns;

	// config files
	private String layoutConfigFile;
	private String setupConfigFile;

	// room storage
	private Map<Character, Room> roomMap;

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
		roomMap = new HashMap<>();
	}

	// set configuration files
	public void setConfigFiles(String layoutConfigFile, String setupConfigFile) {
		this.layoutConfigFile = layoutConfigFile;
		this.setupConfigFile = setupConfigFile;
	}

	// load setup file
	public void loadSetupConfig() throws BadConfigFormatException {
	}

	// load layout file
	public void loadLayoutConfig() throws BadConfigFormatException {
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
}