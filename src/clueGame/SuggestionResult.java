package clueGame;

public class SuggestionResult {
	private Player disprovingPlayer;
	private Card disprovingCard;

	public SuggestionResult(Player disprovingPlayer, Card disprovingCard) {
		this.disprovingPlayer = disprovingPlayer;
		this.disprovingCard = disprovingCard;
	}

	public Player getDisprovingPlayer() {
		return disprovingPlayer;
	}

	public Card getDisprovingCard() {
		return disprovingCard;
	}

	public boolean wasDisproved() {
		return disprovingPlayer != null && disprovingCard != null;
	}
}
