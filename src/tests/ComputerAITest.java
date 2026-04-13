package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.Solution;

public class ComputerAITest {
	protected static Card ajCard;
	protected static Card jakeCard;
	protected static Card johnCard;
	protected static Card candlestickCard;
	protected static Card ropeCard;
	protected static Card leadPipeCard;
	protected static Card daggerCard;
	protected static Card revolverCard;
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
		leadPipeCard = new Card("Lead Pipe", CardType.WEAPON);
		daggerCard = new Card("Dagger", CardType.WEAPON);
		revolverCard = new Card("Revolver", CardType.WEAPON);

		moonRoomCard = new Card("Moon Room", CardType.ROOM);
		appleTreeCard = new Card("AppleTree", CardType.ROOM);
		tvRoomCard = new Card("TV Room", CardType.ROOM);
	}

	@BeforeEach
	public void setUpBoard() {
		board = Board.getInstance();
		board.setConfigFiles("data/ClueMap.csv", "data/ClueSetup.txt");
		board.initialize();
	}

	// Check that the room in a computer suggestion matches the room entered.
	@Test
	public void testCreateSuggestionMatchesRoom() {
		ComputerPlayer player = new ComputerPlayer("Jake DiVito", Color.BLACK, 0, 0);
		Solution suggestion = player.createSuggestion(moonRoomCard);
		assertEquals(moonRoomCard, suggestion.getRoom());
	}

	// Check that the only unseen person and weapon are selected when just one remains of each.
	@Test
	public void testCreateSuggestionSingleUnseenChoices() {
		ComputerPlayer player = new ComputerPlayer("Jake DiVito", Color.BLACK, 0, 0);
		player.addSeenCard(ajCard);
		player.addSeenCard(jakeCard);
		player.addSeenCard(new Card("Danny Devito", CardType.PERSON));
		player.addSeenCard(new Card("Max Verstappen", CardType.PERSON));
		player.addSeenCard(new Card("Bo Nix", CardType.PERSON));

		player.addSeenCard(candlestickCard);
		player.addSeenCard(ropeCard);
		player.addSeenCard(new Card("Wrench", CardType.WEAPON));
		player.addSeenCard(leadPipeCard);
		player.addSeenCard(daggerCard);

		Solution suggestion = player.createSuggestion(appleTreeCard);
		assertEquals(johnCard, suggestion.getPerson());
		assertEquals(revolverCard, suggestion.getWeapon());
		assertEquals(appleTreeCard, suggestion.getRoom());
	}

	// Check that unseen people and weapons are chosen randomly when multiple options remain.
	@Test
	public void testCreateSuggestionMultipleUnseenChoices() {
		ComputerPlayer player = new ComputerPlayer("Jake DiVito", Color.BLACK, 0, 0);
		player.addSeenCard(ajCard);
		player.addSeenCard(candlestickCard);

		boolean sawJake = false;
		boolean sawJohn = false;
		boolean sawRope = false;
		boolean sawLeadPipe = false;

		for (int i = 0; i < 100; i++) {
			Solution suggestion = player.createSuggestion(tvRoomCard);
			assertEquals(tvRoomCard, suggestion.getRoom());
			if (suggestion.getPerson().equals(jakeCard)) {
				sawJake = true;
			}
			if (suggestion.getPerson().equals(johnCard)) {
				sawJohn = true;
			}
			if (suggestion.getWeapon().equals(ropeCard)) {
				sawRope = true;
			}
			if (suggestion.getWeapon().equals(leadPipeCard)) {
				sawLeadPipe = true;
			}
		}

		assertTrue(sawJake);
		assertTrue(sawJohn);
		assertTrue(sawRope);
		assertTrue(sawLeadPipe);
	}
}
