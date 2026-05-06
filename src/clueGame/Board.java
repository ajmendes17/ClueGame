package clueGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JPanel;

public class Board extends JPanel {
	// added NEIGHBOR_OFFSETS as a helper during refactoring
	private static final int[][] NEIGHBOR_OFFSETS = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

	private BoardCell[][] grid;
	private int numRows;
	private int numColumns;

	private String layoutConfigFile;
	private String setupConfigFile;

	private Map<Character, Room> roomMap;
	private Set<Character> roomInitials;
	private ArrayList<Player> players;
	private ArrayList<Card> deck;
	private ArrayList<Card> weapons;
	private Card solutionPerson;
	private Card solutionRoom;
	private Card solutionWeapon;

	private Set<BoardCell> targets;
	private Set<BoardCell> visited;
	private TurnManager turnManager;

	/*
	 * variable and methods used for singleton pattern
	 */
	private static Board theInstance = new Board();

	// constructor is private to ensure only one can be created
	private Board() {
		super();
		turnManager = new TurnManager(this);
		addMouseListener(new BoardMouseListener());
	}

	// this method returns the only Board
	public static Board getInstance() {
		return theInstance;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (grid == null || numRows == 0 || numColumns == 0) {
			return;
		}

		int cellSize = Math.min(getWidth() / numColumns, getHeight() / numRows);
		if (cellSize <= 0) {
			return;
		}

		int xOffset = (getWidth() - numColumns * cellSize) / 2;
		int yOffset = (getHeight() - numRows * cellSize) / 2;

		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numColumns; col++) {
				grid[row][col].draw(g, cellSize, xOffset, yOffset);
			}
		}

		drawRoomNames(g, cellSize, xOffset, yOffset);
		drawPlayers(g, cellSize, xOffset, yOffset);
	}

	private void drawRoomNames(Graphics g, int cellSize, int xOffset, int yOffset) {
		g.setColor(Color.BLACK);
		g.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, cellSize / 2)));
		FontMetrics metrics = g.getFontMetrics();

		for (Room room : roomMap.values()) {
			BoardCell labelCell = room.getLabelCell();
			if (labelCell == null) {
				continue;
			}

			String[] labelLines = getRoomLabelLines(room.getName());
			int x = xOffset + labelCell.getCol() * cellSize + getRoomLabelXAdjustment(room.getName(), cellSize);
			int y = yOffset + labelCell.getRow() * cellSize + cellSize;

			for (String line : labelLines) {
				g.drawString(line, x, y);
				y += metrics.getHeight();
			}
		}
	}

	private String[] getRoomLabelLines(String roomName) {
		if (roomName.equals("Swimming Pool")) {
			return new String[] {"Swimming", "Pool"};
		}
		return new String[] {roomName};
	}

	private int getRoomLabelXAdjustment(String roomName, int cellSize) {
		if (roomName.equals("Swimming Pool")) {
			return -cellSize;
		}
		if (roomName.equals("Isolation Quarters")) {
			return -3 * cellSize;
		}
		return 2;
	}

	private void drawPlayers(Graphics g, int cellSize, int xOffset, int yOffset) {
		if (players == null) {
			return;
		}

		Map<String, Integer> playerCounts = new HashMap<>();
		for (Player player : players) {
			String cellKey = getPlayerCellKey(player);
			playerCounts.put(cellKey, playerCounts.getOrDefault(cellKey, 0) + 1);
		}

		Map<String, Integer> playersDrawn = new HashMap<>();
		for (Player player : players) {
			String cellKey = getPlayerCellKey(player);
			int drawIndex = playersDrawn.getOrDefault(cellKey, 0);
			playersDrawn.put(cellKey, drawIndex + 1);
			drawPlayer(g, player, drawIndex, playerCounts.get(cellKey), cellSize, xOffset, yOffset);
		}
	}

	private String getPlayerCellKey(Player player) {
		return player.getRow() + "," + player.getColumn();
	}

	private void drawPlayer(Graphics g, Player player, int drawIndex, int playerCount,
			int cellSize, int xOffset, int yOffset) {
		BoardCell playerCell = getCell(player.getRow(), player.getColumn());
		if (!playerCell.isRoomCenter() || playerCount == 1) {
			player.draw(g, cellSize, xOffset, yOffset);
			return;
		}

		int cellsPerRow = Math.min(3, Math.max(2, playerCount));
		int rowsNeeded = (playerCount + cellsPerRow - 1) / cellsPerRow;
		int slotWidth = cellSize / cellsPerRow;
		int slotHeight = cellSize / rowsNeeded;
		int playerSize = Math.max(5, Math.min(slotWidth, slotHeight) - 2);
		int rowOffset = drawIndex / cellsPerRow;
		int colOffset = drawIndex % cellsPerRow;
		int x = xOffset + player.getColumn() * cellSize + colOffset * slotWidth + (slotWidth - playerSize) / 2;
		int y = yOffset + player.getRow() * cellSize + rowOffset * slotHeight + (slotHeight - playerSize) / 2;

		g.setColor(player.getColor());
		g.fillOval(x, y, playerSize, playerSize);

		g.setColor(Color.BLACK);
		g.drawOval(x, y, playerSize, playerSize);
	}

	/*
	 * initialize the board (since we are using singleton pattern)
	 */
	public void initialize() {
		targets = new HashSet<>();
		visited = new HashSet<>();
		turnManager.reset();
		try {
			loadSetupConfig();
			loadLayoutConfig();
			setInitialPlayerOccupancy();
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
		roomInitials = new HashSet<>();
		players = new ArrayList<>();
		deck = new ArrayList<>();
		weapons = new ArrayList<>();
		
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
					if (isPlayerType(parts[0]) && parts.length == 5) {
						addPlayer(parts);
						continue;
					}
					if (parts[0].equals("Weapon") && parts.length == 2) {
						addWeapon(parts[1]);
						continue;
					}
					throw new BadConfigFormatException("Bad setup line format: " + line);
				}

				String type = parts[0];
				String name = parts[1];
				char initial = parts[2].charAt(0);

				if (!type.equals("Room") && !type.equals("Space")) {
					throw new BadConfigFormatException("Bad room type: " + line);
				}

				roomMap.put(initial, new Room(name));
				if (type.equals("Room")) {
					roomInitials.add(initial);
				}
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
		buildDeck();
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

	public void setControlPanel(GameControlPanel controlPanel) {
		turnManager.setControlPanel(controlPanel);
	}

	public void setKnownCardsPanel(KnownCardsPanel knownCardsPanel, Map<Card, Player> initialSeenCards) {
		turnManager.setKnownCardsPanel(knownCardsPanel, initialSeenCards);
	}

	public void processNextPlayer() {
		turnManager.processNextPlayer();
	}

	public void handleAccusationButton() {
		turnManager.handleAccusationButton();
	}

	void movePlayer(Player player, BoardCell destination) {
		BoardCell startCell = getCell(player.getRow(), player.getColumn());
		if (!startCell.isRoomCenter()) {
			startCell.setOccupied(false);
		}

		player.setRow(destination.getRow());
		player.setColumn(destination.getCol());

		if (!destination.isRoomCenter()) {
			destination.setOccupied(true);
		}
	}

	public Player moveSuggestedPlayerToRoom(Solution suggestion) {
		Player suggestedPlayer = getPlayerByName(suggestion.getPerson().getCardName());
		BoardCell roomCenter = getRoomCenter(suggestion.getRoom());

		if (suggestedPlayer == null || roomCenter == null) {
			return null;
		}

		movePlayer(suggestedPlayer, roomCenter);
		repaint();
		return suggestedPlayer;
	}

	private Player getPlayerByName(String playerName) {
		if (players == null) {
			return null;
		}

		for (Player player : players) {
			if (player.getName().equals(playerName)) {
				return player;
			}
		}

		return null;
	}

	private BoardCell getRoomCenter(Card roomCard) {
		if (roomCard == null || roomCard.getType() != CardType.ROOM || roomMap == null) {
			return null;
		}

		for (Room room : roomMap.values()) {
			if (room.getName().equals(roomCard.getCardName())) {
				return room.getCenterCell();
			}
		}

		return null;
	}

	void highlightTargets() {
		for (BoardCell target : targets) {
			target.setTarget(true);
		}
	}

	void clearTargetHighlights() {
		if (grid == null) {
			return;
		}

		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numColumns; col++) {
				grid[row][col].setTarget(false);
			}
		}
	}

	BoardCell getClickedCell(MouseEvent event) {
		if (grid == null || numRows == 0 || numColumns == 0) {
			return null;
		}

		int cellSize = Math.min(getWidth() / numColumns, getHeight() / numRows);
		if (cellSize <= 0) {
			return null;
		}

		int xOffset = (getWidth() - numColumns * cellSize) / 2;
		int yOffset = (getHeight() - numRows * cellSize) / 2;
		int clickedCol = (event.getX() - xOffset) / cellSize;
		int clickedRow = (event.getY() - yOffset) / cellSize;

		if (event.getX() < xOffset || event.getY() < yOffset || !isInBounds(clickedRow, clickedCol)) {
			return null;
		}

		return getCell(clickedRow, clickedCol);
	}

	private class BoardMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent event) {
			turnManager.handleBoardClick(event);
		}
	}

	private void setInitialPlayerOccupancy() {
		if (players == null || grid == null) {
			return;
		}

		for (Player player : players) {
			BoardCell playerCell = getCell(player.getRow(), player.getColumn());
			if (!playerCell.isRoomCenter()) {
				playerCell.setOccupied(true);
			}
		}
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

	private boolean isPlayerType(String type) {
		return type.equals("Human") || type.equals("Computer");
	}

	private void addWeapon(String weaponName) {
		Card weaponCard = new Card(weaponName, CardType.WEAPON);
		weapons.add(weaponCard);
	}

	private void addPlayer(String[] parts) throws BadConfigFormatException {
		String type = parts[0];
		String name = parts[1];
		Color color = parseColor(parts[2]);
		int row = parseCoordinate(parts[3], "row");
		int column = parseCoordinate(parts[4], "column");

		Player player;
		if (type.equals("Human")) {
			player = new HumanPlayer(name, color, row, column);
		} else {
			player = new ComputerPlayer(name, color, row, column);
		}

		players.add(player);
	}

	private Color parseColor(String colorName) throws BadConfigFormatException {
		switch (colorName.trim().toLowerCase()) {
		case "blue":
			return Color.BLUE;
		case "red":
			return Color.RED;
		case "green":
			return Color.GREEN;
		case "yellow":
			return Color.YELLOW;
		case "orange":
			return Color.ORANGE;
		case "pink":
			return Color.PINK;
		case "white":
			return Color.WHITE;
		case "black":
			return Color.BLACK;
		case "gray":
			return Color.GRAY;
		default:
			throw new BadConfigFormatException("Unknown player color: " + colorName);
		}
	}

	private int parseCoordinate(String value, String coordinateName) throws BadConfigFormatException {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new BadConfigFormatException("Bad " + coordinateName + " value: " + value);
		}
	}

	public void buildDeck() {
		deck.clear();

		for (char initial : roomInitials) {
			deck.add(new Card(roomMap.get(initial).getName(), CardType.ROOM));
		}

		deck.addAll(weapons);

		for (Player player : players) {
			deck.add(new Card(player.getName(), CardType.PERSON));
		}
	}

	public void selectAnswer() {
		solutionPerson = drawRandomCardOfType(CardType.PERSON);
		solutionWeapon = drawRandomCardOfType(CardType.WEAPON);
		solutionRoom = drawRandomCardOfType(CardType.ROOM);
	}

	private Card drawRandomCardOfType(CardType type) {
		List<Card> matchingCards = new ArrayList<>();
		for (Card card : deck) {
			if (card.getType() == type) {
				matchingCards.add(card);
			}
		}

		if (matchingCards.isEmpty()) {
			return null;
		}

		Card selected = matchingCards.get(new Random().nextInt(matchingCards.size()));
		deck.remove(selected);
		return selected;
	}

	public void dealCards() {
		ArrayList<Card> cardsToDeal = new ArrayList<>(deck);
		Collections.shuffle(cardsToDeal);

		for (Player player : players) {
			player.getHand().clear();
		}

		for (int i = 0; i < cardsToDeal.size(); i++) {
			players.get(i % players.size()).addCard(cardsToDeal.get(i));
		}
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public ArrayList<Card> getWeapons() {
		return weapons;
	}

	public ArrayList<Card> getDeck() {
		return deck;
	}

	public Card getSolutionPerson() {
		return solutionPerson;
	}

	public Card getSolutionRoom() {
		return solutionRoom;
	}

	public Card getSolutionWeapon() {
		return solutionWeapon;
	}

	public void setAnswer(Solution answer) {
		solutionPerson = answer.getPerson();
		solutionWeapon = answer.getWeapon();
		solutionRoom = answer.getRoom();
	}

	public boolean checkAccusation(Solution accusation) {
		return solutionPerson.equals(accusation.getPerson())
				&& solutionWeapon.equals(accusation.getWeapon())
				&& solutionRoom.equals(accusation.getRoom());
	}

	public Card handleSuggestion(Solution suggestion, Player accuser) {
		SuggestionResult result = handleSuggestionWithResult(suggestion, accuser);
		return result.getDisprovingCard();
	}

	public SuggestionResult handleSuggestionWithResult(Solution suggestion, Player accuser) {
		if (players == null || players.isEmpty()) {
			return new SuggestionResult(null, null);
		}

		int accuserIndex = players.indexOf(accuser);
		if (accuserIndex == -1) {
			return new SuggestionResult(null, null);
		}

		for (int offset = 1; offset < players.size(); offset++) {
			Player currentPlayer = players.get((accuserIndex + offset) % players.size());
			Card disprovedCard = currentPlayer.disproveSuggestion(suggestion);
			if (disprovedCard != null) {
				return new SuggestionResult(currentPlayer, disprovedCard);
			}
		}

		return new SuggestionResult(null, null);
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
}
