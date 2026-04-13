package clueGame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComputerPlayer extends Player {
	private static final Random random = new Random();

	public ComputerPlayer(String name, Color color, int row, int column) {
		super(name, color, row, column);
	}

	public Solution createSuggestion(Card roomCard) {
		List<Card> unseenPeople = new ArrayList<>();
		List<Card> unseenWeapons = new ArrayList<>();

		Board board = Board.getInstance();

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
}
