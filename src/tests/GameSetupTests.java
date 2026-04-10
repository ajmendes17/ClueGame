package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.HumanPlayer;
import clueGame.Player;

public class GameSetupTests {
	private Board board;

	@BeforeEach
	public void setUp() {
		board = Board.getInstance();
		board.setConfigFiles("data/ClueMap.csv", "data/ClueSetup.txt");
		board.initialize();
	}

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
		assertEquals(6, board.getPlayers().size());
		assertEquals("AJ Mendes", board.getPlayers().get(0).getName());
		assertEquals(Color.BLUE, board.getPlayers().get(0).getColor());
		assertEquals(5, board.getPlayers().get(0).getRow());
		assertEquals(0, board.getPlayers().get(0).getColumn());
		assertEquals("Bo Nix", board.getPlayers().get(5).getName());
		assertEquals(Color.ORANGE, board.getPlayers().get(5).getColor());
	}

	// Check that weapons are loaded from the game setup config.
	@Test
	public void testLoadWeaponsFromConfig() {
		assertEquals(6, board.getWeapons().size());
		assertEquals(new Card("Candlestick", CardType.WEAPON), board.getWeapons().get(0));
		assertEquals(new Card("Coffee Mug", CardType.WEAPON), board.getWeapons().get(5));
	}

	// Check that the correct number and types of players are created.
	@Test
	public void testPlayerTypesCreated() {
		assertEquals(1, board.getPlayers().stream().filter(player -> player instanceof HumanPlayer).count());
		assertEquals(5, board.getPlayers().stream().filter(player -> player instanceof ComputerPlayer).count());
	}

	// Check that the deck is built with room, weapon, and person cards.
	@Test
	public void testDeckCreation() {
		assertEquals(21, board.getDeck().size());
		assertEquals(9, board.getDeck().stream().filter(card -> card.getType() == CardType.ROOM).count());
		assertEquals(6, board.getDeck().stream().filter(card -> card.getType() == CardType.WEAPON).count());
		assertEquals(6, board.getDeck().stream().filter(card -> card.getType() == CardType.PERSON).count());
	}

	// Check that the solution contains one room, one weapon, and one person.
	@Test
	public void testSelectAnswer() {
		board.selectAnswer();

		assertNotNull(board.getSolutionPerson());
		assertNotNull(board.getSolutionWeapon());
		assertNotNull(board.getSolutionRoom());
		assertEquals(CardType.PERSON, board.getSolutionPerson().getType());
		assertEquals(CardType.WEAPON, board.getSolutionWeapon().getType());
		assertEquals(CardType.ROOM, board.getSolutionRoom().getType());
		assertEquals(18, board.getDeck().size());
	}

	// Check that all non-solution cards are dealt fairly to players.
	@Test
	public void testDealCards() {
		board.selectAnswer();
		board.dealCards();

		for (Player player : board.getPlayers()) {
			assertEquals(3, player.getHand().size());
		}
	}

	// Check that no duplicate cards are dealt and all cards are accounted for.
	@Test
	public void testNoDuplicateCardsDealt() {
		board.selectAnswer();
		board.dealCards();

		Set<Card> dealtCards = new HashSet<>();
		int totalCardsDealt = 0;

		for (Player player : board.getPlayers()) {
			totalCardsDealt += player.getHand().size();
			dealtCards.addAll(player.getHand());
		}

		assertEquals(18, totalCardsDealt);
		assertEquals(18, dealtCards.size());
		assertEquals(new HashSet<>(board.getDeck()), dealtCards);
		assertTrue(!dealtCards.contains(board.getSolutionPerson()));
		assertTrue(!dealtCards.contains(board.getSolutionWeapon()));
		assertTrue(!dealtCards.contains(board.getSolutionRoom()));
	}
}
