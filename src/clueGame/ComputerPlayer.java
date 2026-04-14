package clueGame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ComputerPlayer extends Player {
	private static final Random random = new Random();

	public ComputerPlayer(String name, Color color, int row, int column) {
		super(name, color, row, column);
	}

	public Solution createSuggestion() {
		List<Card> unseenPeople = new ArrayList<>();
		List<Card> unseenWeapons = new ArrayList<>();

		Board board = Board.getInstance();
		Card roomCard = new Card(board.getRoom(board.getCell(getRow(), getColumn())).getName(), CardType.ROOM);

		for (Player player : board.getPlayers()) {
			Card personCard = new Card(player.getName(), CardType.PERSON);
			if (!getSeenCards().contains(personCard)) {
				unseenPeople.add(personCard);
			}
		}

		for (Card weaponCard : board.getWeapons()) {
			if (!getSeenCards().contains(weaponCard)) {
				unseenWeapons.add(weaponCard);
			}
		}

		Card personChoice = unseenPeople.get(random.nextInt(unseenPeople.size()));
		Card weaponChoice = unseenWeapons.get(random.nextInt(unseenWeapons.size()));
		return new Solution(personChoice, weaponChoice, roomCard);
	}

	public BoardCell selectTarget(Set<BoardCell> targets) {
		List<BoardCell> unseenRoomTargets = new ArrayList<>();
		Board board = Board.getInstance();

		for (BoardCell cell : targets) {
			if (!cell.isRoomCenter()) {
				continue;
			}

			Card roomCard = new Card(board.getRoom(cell).getName(), CardType.ROOM);
			if (!getSeenCards().contains(roomCard)) {
				unseenRoomTargets.add(cell);
			}
		}

		if (!unseenRoomTargets.isEmpty()) {
			return unseenRoomTargets.get(random.nextInt(unseenRoomTargets.size()));
		}

		List<BoardCell> allTargets = new ArrayList<>(targets);
		return allTargets.get(random.nextInt(allTargets.size()));
	}
}
