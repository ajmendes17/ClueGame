package tests;

import org.junit.jupiter.api.BeforeAll;

import clueGame.Card;
import clueGame.CardType;

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
}
