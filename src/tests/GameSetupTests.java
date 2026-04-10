package tests;

import static org.junit.Assert.*;


import java.awt.Color;

import org.junit.jupiter.api.Test;

import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.HumanPlayer;
import clueGame.Player;

public class GameSetupTests {

	// Check that a card stores the expected name and type.
	@Test
	public void testCardCreation() {
		Card card = new Card("TV Room", CardType.ROOM);
		assertEquals("TV Room", card.getCardName());
		assertEquals(CardType.ROOM, card.getType());
	}

	// Check that the player subclasses keep the expected starting information.
	@Test
	public void testPlayerCreation() {
		Player human = new HumanPlayer("AJ", Color.BLUE, 5, 7);
		Player computer = new ComputerPlayer("Jake", Color.RED, 10, 12);

		assertEquals("AJ", human.getName());
		assertEquals(Color.BLUE, human.getColor());
		assertEquals(5, human.getRow());
		assertEquals(7, human.getColumn());

		assertEquals("Jake", computer.getName());
		assertEquals(Color.RED, computer.getColor());
		assertEquals(10, computer.getRow());
		assertEquals(12, computer.getColumn());
	}

	// Check that player hands can store cards when dealing is implemented.
	@Test
	public void testPlayerHandStoresCards() {
		Player player = new HumanPlayer("AJ", Color.BLUE, 0, 0);
		Card personCard = new Card("AJ", CardType.PERSON);
		Card weaponCard = new Card("Candlestick", CardType.WEAPON);

		player.addCard(personCard);
		player.addCard(weaponCard);

		assertEquals(2, player.getHand().size());
		assertTrue(player.getHand().contains(personCard));
		assertTrue(player.getHand().contains(weaponCard));
	}

	// Check that players are loaded from the game setup config.
	@Test
	public void testLoadPlayersFromConfig() {
		fail("Implement player loading from the setup config file.");
	}

	// Check that weapons are loaded from the game setup config.
	@Test
	public void testLoadWeaponsFromConfig() {
		fail("Implement weapon loading from the setup config file.");
	}

	// Check that the correct number and types of players are created.
	@Test
	public void testPlayerTypesCreated() {
		fail("Implement human and computer player creation from config data.");
	}

	// Check that the deck is built with room, weapon, and person cards.
	@Test
	public void testDeckCreation() {
		fail("Implement dynamic deck creation from setup data.");
	}

	// Check that the solution contains one room, one weapon, and one person.
	@Test
	public void testSelectAnswer() {
		fail("Implement answer selection from the full deck.");
	}

	// Check that all non-solution cards are dealt fairly to players.
	@Test
	public void testDealCards() {
		fail("Implement fair dealing of all remaining cards to players.");
	}

	// Check that no duplicate cards are dealt and all cards are accounted for.
	@Test
	public void testNoDuplicateCardsDealt() {
		fail("Implement duplicate-card and full-accounting checks after dealing.");
	}
}
