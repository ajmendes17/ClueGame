package clueGame;

import java.util.Objects;

public class Card {
	private String cardName;
	private CardType type;

	public Card(String cardName, CardType type) {
		this.cardName = cardName;
		this.type = type;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public CardType getType() {
		return type;
	}

	public void setType(CardType type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Card)) {
			return false;
		}
		Card other = (Card) obj;
		return Objects.equals(cardName, other.cardName) && type == other.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cardName, type);
	}

	@Override
	public String toString() {
		return cardName + " (" + type + ")";
	}
}
