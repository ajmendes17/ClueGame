package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class Player {
	private String name;
	private Color color;
	private int row;
	private int column;
	private Set<Card> hand;
	private Set<Card> seenCards;

	protected Player(String name, Color color, int row, int column) {
		this.name = name;
		this.color = color;
		this.row = row;
		this.column = column;
		hand = new HashSet<>();
		seenCards = new HashSet<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public Set<Card> getHand() {
		return hand;
	}

	public void setHand(Set<Card> hand) {
		this.hand = hand;
	}

	public Set<Card> getSeenCards() {
		return seenCards;
	}

	public void setSeenCards(Set<Card> seenCards) {
		this.seenCards = seenCards;
	}

	public void addCard(Card card) {
		hand.add(card);
	}

	public void updateHand(Card card) {
		hand.add(card);
		seenCards.add(card);
	}

	public void addSeenCard(Card card) {
		seenCards.add(card);
	}

	public Card disproveSuggestion(Solution suggestion) {
		List<Card> matchingCards = new ArrayList<>();

		for (Card card : hand) {
			if (card.equals(suggestion.getPerson())
					|| card.equals(suggestion.getWeapon())
					|| card.equals(suggestion.getRoom())) {
				matchingCards.add(card);
			}
		}

		if (matchingCards.isEmpty()) {
			return null;
		}

		if (matchingCards.size() == 1) {
			return matchingCards.get(0);
		}

		return matchingCards.get(new Random().nextInt(matchingCards.size()));
	}

	public void draw(Graphics g, int cellSize, int xOffset, int yOffset) {
		int playerSize = Math.max(8, cellSize - 4);
		int x = xOffset + column * cellSize + (cellSize - playerSize) / 2;
		int y = yOffset + row * cellSize + (cellSize - playerSize) / 2;

		g.setColor(color);
		g.fillOval(x, y, playerSize, playerSize);

		g.setColor(Color.BLACK);
		g.drawOval(x, y, playerSize, playerSize);
	}
}
