package tests;

import static org.junit.jupiter.api.Assertions.*;


import java.awt.Color;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.HumanPlayer;
import clueGame.Player;
import clueGame.Solution;

public class GameSolutionTest {
	protected static Card ajCard;
	protected static Card jakeCard;
	protected static Card johnCard;
	protected static Card candlestickCard;
	protected static Card ropeCard;
	protected static Card laptopCard;
	protected static Card moonRoomCard;
	protected static Card appleTreeCard;
	protected static Card tvRoomCard;
	private Board board;

	@BeforeAll
	public static void setUpCards() {
		ajCard = new Card("AJ Mendes", CardType.PERSON);
		jakeCard = new Card("Jake DiVito", CardType.PERSON);
		johnCard = new Card("John Cena", CardType.PERSON);

		candlestickCard = new Card("Candlestick", CardType.WEAPON);
		ropeCard = new Card("Rope", CardType.WEAPON);
		laptopCard = new Card("Laptop", CardType.WEAPON);

		moonRoomCard = new Card("Moon Room", CardType.ROOM);
		appleTreeCard = new Card("AppleTree", CardType.ROOM);
		tvRoomCard = new Card("TV Room", CardType.ROOM);
	}

	@BeforeEach
	public void setUpBoard() {
		board = Board.getInstance();
	}

	// Check that an accusation returns true when person, weapon, and room all match.
	@Test
	public void testCheckAccusationCorrect() {
		board.setAnswer(new Solution(ajCard, candlestickCard, moonRoomCard));
		assertTrue(board.checkAccusation(new Solution(ajCard, candlestickCard, moonRoomCard)));
	}

	// Check that an accusation returns false when the person is wrong.
	@Test
	public void testCheckAccusationWrongPerson() {
		board.setAnswer(new Solution(ajCard, candlestickCard, moonRoomCard));
		assertFalse(board.checkAccusation(new Solution(jakeCard, candlestickCard, moonRoomCard)));
	}

	// Check that an accusation returns false when the weapon is wrong.
	@Test
	public void testCheckAccusationWrongWeapon() {
		board.setAnswer(new Solution(ajCard, candlestickCard, moonRoomCard));
		assertFalse(board.checkAccusation(new Solution(ajCard, ropeCard, moonRoomCard)));
	}

	// Check that an accusation returns false when the room is wrong.
	@Test
	public void testCheckAccusationWrongRoom() {
		board.setAnswer(new Solution(ajCard, candlestickCard, moonRoomCard));
		assertFalse(board.checkAccusation(new Solution(ajCard, candlestickCard, tvRoomCard)));
	}

	// Check that a player returns the only matching card that can disprove a suggestion.
	@Test
	public void testDisproveSuggestionSingleMatch() {
		Player player = new HumanPlayer("AJ Mendes", java.awt.Color.BLUE, 0, 0);
		player.updateHand(ropeCard);
		player.updateHand(tvRoomCard);
		player.updateHand(ajCard);

		Card disproved = player.disproveSuggestion(new Solution(johnCard, ropeCard, moonRoomCard));
		assertEquals(ropeCard, disproved);
	}

	// Check that a player returns null if no cards can disprove the suggestion.
	@Test
	public void testDisproveSuggestionNoMatch() {
		Player player = new HumanPlayer("AJ Mendes", java.awt.Color.BLUE, 0, 0);
		player.updateHand(ropeCard);
		player.updateHand(tvRoomCard);
		player.updateHand(ajCard);

		Card disproved = player.disproveSuggestion(new Solution(johnCard, candlestickCard, moonRoomCard));
		assertEquals(null, disproved);
	}

	// Check that a player randomly returns one of the matching cards when more than one can disprove.
	@Test
	public void testDisproveSuggestionMultipleMatches() {
		Player player = new HumanPlayer("AJ Mendes", java.awt.Color.BLUE, 0, 0);
		player.updateHand(ropeCard);
		player.updateHand(moonRoomCard);
		player.updateHand(ajCard);

		boolean sawWeapon = false;
		boolean sawRoom = false;

		for (int i = 0; i < 50; i++) {
			Card disproved = player.disproveSuggestion(new Solution(johnCard, ropeCard, moonRoomCard));
			assertNotNull(disproved);
			assertTrue(disproved.equals(ropeCard) || disproved.equals(moonRoomCard));
			if (disproved.equals(ropeCard)) {
				sawWeapon = true;
			}
			if (disproved.equals(moonRoomCard)) {
				sawRoom = true;
			}
		}

		assertTrue(sawWeapon);
		assertTrue(sawRoom);
	}

	// Check that a suggestion returns null when no player can disprove it.
	@Test
	public void testHandleSuggestionNoDisproof() {
		Player human = new HumanPlayer("AJ Mendes", Color.BLUE, 0, 0);
		Player computerOne = new ComputerPlayer("Jake DiVito", Color.BLACK, 0, 1);
		Player computerTwo = new ComputerPlayer("John Cena", Color.GREEN, 0, 2);
		computerOne.updateHand(tvRoomCard);
		computerTwo.updateHand(laptopCard);

		ArrayList<Player> players = new ArrayList<>();
		players.add(human);
		players.add(computerOne);
		players.add(computerTwo);
		board.setPlayers(players);

		assertNull(board.handleSuggestion(new Solution(ajCard, candlestickCard, moonRoomCard), human));
	}

	// Check that a suggestion returns null when only the accuser can disprove it.
	@Test
	public void testHandleSuggestionOnlyAccuserCanDisprove() {
		Player human = new HumanPlayer("AJ Mendes", Color.BLUE, 0, 0);
		Player computerOne = new ComputerPlayer("Jake DiVito", Color.BLACK, 0, 1);
		Player computerTwo = new ComputerPlayer("John Cena", Color.GREEN, 0, 2);
		human.updateHand(candlestickCard);

		ArrayList<Player> players = new ArrayList<>();
		players.add(human);
		players.add(computerOne);
		players.add(computerTwo);
		board.setPlayers(players);

		assertNull(board.handleSuggestion(new Solution(jakeCard, candlestickCard, moonRoomCard), human));
	}

	// Check that the human player can disprove a suggestion when no one earlier can.
	@Test
	public void testHandleSuggestionHumanDisproves() {
		Player human = new HumanPlayer("AJ Mendes", Color.BLUE, 0, 0);
		Player computerOne = new ComputerPlayer("Jake DiVito", Color.BLACK, 0, 1);
		Player computerTwo = new ComputerPlayer("John Cena", Color.GREEN, 0, 2);
		human.updateHand(moonRoomCard);

		ArrayList<Player> players = new ArrayList<>();
		players.add(human);
		players.add(computerOne);
		players.add(computerTwo);
		board.setPlayers(players);

		assertEquals(moonRoomCard,
				board.handleSuggestion(new Solution(jakeCard, candlestickCard, moonRoomCard), computerTwo));
	}

	// Check that the first player in order after the accuser is the one who disproves.
	@Test
	public void testHandleSuggestionStopsAtFirstDisprover() {
		Player human = new HumanPlayer("AJ Mendes", Color.BLUE, 0, 0);
		Player computerOne = new ComputerPlayer("Jake DiVito", Color.BLACK, 0, 1);
		Player computerTwo = new ComputerPlayer("John Cena", Color.GREEN, 0, 2);
		computerOne.updateHand(ropeCard);
		computerTwo.updateHand(ropeCard);

		ArrayList<Player> players = new ArrayList<>();
		players.add(human);
		players.add(computerOne);
		players.add(computerTwo);
		board.setPlayers(players);

		assertEquals(ropeCard, board.handleSuggestion(new Solution(ajCard, ropeCard, moonRoomCard), human));
	}
}
