package tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;
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
}
