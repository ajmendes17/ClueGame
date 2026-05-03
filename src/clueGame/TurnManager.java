package clueGame;

import java.awt.Window;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class TurnManager {
	private Board board;
	private GameControlPanel controlPanel;
	private int currentPlayerIndex;
	private int currentRoll;
	private boolean waitingForHumanMove;
	private Random turnRandom;

	public TurnManager(Board board) {
		this.board = board;
		this.currentPlayerIndex = -1;
		this.currentRoll = 0;
		this.waitingForHumanMove = false;
		this.turnRandom = new Random();
	}

	public void reset() {
		currentPlayerIndex = -1;
		currentRoll = 0;
		waitingForHumanMove = false;
	}

	public void setControlPanel(GameControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}

	public int getCurrentRoll() {
		return currentRoll;
	}

	public boolean isWaitingForHumanMove() {
		return waitingForHumanMove;
	}

	public void processNextPlayer() {
		ArrayList<Player> players = board.getPlayers();
		if (players == null || players.isEmpty()) {
			return;
		}

		if (waitingForHumanMove) {
			JOptionPane.showMessageDialog(board, "Finish your move before advancing to the next player.");
			return;
		}

		board.clearTargetHighlights();
		currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
		Player currentPlayer = players.get(currentPlayerIndex);

		if (currentPlayer instanceof ComputerPlayer
				&& ((ComputerPlayer) currentPlayer).hasPendingAccusation()) {
			currentRoll = 0;
			updateControlPanel(currentPlayer);
			handleComputerAccusation((ComputerPlayer) currentPlayer);
			return;
		}

		currentRoll = rollDie();
		updateControlPanel(currentPlayer);

		BoardCell startCell = board.getCell(currentPlayer.getRow(), currentPlayer.getColumn());
		board.calcTargets(startCell, currentRoll);

		if (board.getTargets().isEmpty()) {
			JOptionPane.showMessageDialog(board, currentPlayer.getName() + " has no valid moves.");
			board.repaint();
			return;
		}

		if (currentPlayer instanceof HumanPlayer) {
			waitingForHumanMove = true;
			board.highlightTargets();
			board.repaint();
			return;
		}

		moveComputerPlayer((ComputerPlayer) currentPlayer);
	}

	public void handleBoardClick(MouseEvent event) {
		if (!waitingForHumanMove) {
			JOptionPane.showMessageDialog(board, "The board can only be clicked during the human player's turn.");
			return;
		}

		BoardCell clickedCell = board.getClickedCell(event);
		if (clickedCell == null || !board.getTargets().contains(clickedCell)) {
			JOptionPane.showMessageDialog(board, "That is not a valid target. Please select a highlighted location.");
			return;
		}

		moveHumanPlayer(clickedCell);
	}

	public void handleAccusationButton() {
		Player currentPlayer = getCurrentPlayer();
		if (!(currentPlayer instanceof HumanPlayer) || !waitingForHumanMove) {
			JOptionPane.showMessageDialog(board,
					"You can only make an accusation at the beginning of your turn.");
			return;
		}

		Window owner = SwingUtilities.getWindowAncestor(board);
		SuggestionDialog dialog = new SuggestionDialog(owner, board);
		dialog.setVisible(true);

		Solution accusation = dialog.getSuggestion();
		if (accusation == null) {
			return;
		}

		handleAccusation(currentPlayer, accusation);
	}

	private int rollDie() {
		return turnRandom.nextInt(6) + 1;
	}

	private Player getCurrentPlayer() {
		ArrayList<Player> players = board.getPlayers();
		if (players == null || currentPlayerIndex < 0 || currentPlayerIndex >= players.size()) {
			return null;
		}
		return players.get(currentPlayerIndex);
	}

	private void updateControlPanel(Player player) {
		if (controlPanel == null) {
			return;
		}

		controlPanel.setTurn(player, currentRoll);
		controlPanel.setGuess("No guess");
		controlPanel.setGuessResult("No result");
	}

	private SuggestionResult processSuggestion(Player accuser, Solution suggestion) {
		board.moveSuggestedPlayerToRoom(suggestion);
		SuggestionResult result = board.handleSuggestionWithResult(suggestion, accuser);

		if (controlPanel != null) {
			controlPanel.setGuess(formatSuggestion(accuser, suggestion));
			controlPanel.setGuessResult(formatSuggestionResult(accuser, result));
		}

		if (result.wasDisproved()) {
			accuser.addSeenCard(result.getDisprovingCard());
		}

		return result;
	}

	private String formatSuggestion(Player accuser, Solution suggestion) {
		return accuser.getName() + " suggested "
				+ suggestion.getPerson().getCardName() + " with "
				+ suggestion.getWeapon().getCardName() + " in "
				+ suggestion.getRoom().getCardName();
	}

	private String formatSuggestionResult(Player accuser, SuggestionResult result) {
		if (!result.wasDisproved()) {
			return "No new clue";
		}

		if (accuser instanceof HumanPlayer) {
			return result.getDisprovingPlayer().getName() + " showed "
					+ result.getDisprovingCard().getCardName();
		}

		return "Suggestion disproved by " + result.getDisprovingPlayer().getName();
	}

	private void handleAccusation(Player accuser, Solution accusation) {
		boolean correct = board.checkAccusation(accusation);
		String resultText = correct ? "Correct accusation. " + accuser.getName() + " wins!"
				: "Incorrect accusation. " + accuser.getName() + " loses.";

		JOptionPane.showMessageDialog(board, formatAccusation(accuser, accusation) + "\n" + resultText);
		System.exit(0);
	}

	private void handleComputerAccusation(ComputerPlayer computer) {
		Solution accusation = computer.getPendingAccusation();
		computer.clearPendingAccusation();
		handleAccusation(computer, accusation);
	}

	private String formatAccusation(Player accuser, Solution accusation) {
		return accuser.getName() + " accused "
				+ accusation.getPerson().getCardName() + " with "
				+ accusation.getWeapon().getCardName() + " in "
				+ accusation.getRoom().getCardName();
	}

	private void moveComputerPlayer(ComputerPlayer player) {
		BoardCell target = player.selectTarget(board.getTargets());
		board.movePlayer(player, target);
		board.repaint();

		if (target.isRoomCenter()) {
			Solution suggestion = player.createSuggestion();
			SuggestionResult result = processSuggestion(player, suggestion);
			updateComputerAccusation(player, suggestion, result);
		}
	}

	private void updateComputerAccusation(ComputerPlayer player, Solution suggestion, SuggestionResult result) {
		if (result.wasDisproved() || playerHasSuggestedCard(player, suggestion)) {
			player.clearPendingAccusation();
			return;
		}

		player.setPendingAccusation(suggestion);
	}

	private boolean playerHasSuggestedCard(Player player, Solution suggestion) {
		return player.getHand().contains(suggestion.getPerson())
				|| player.getHand().contains(suggestion.getWeapon())
				|| player.getHand().contains(suggestion.getRoom());
	}

	private void moveHumanPlayer(BoardCell target) {
		Player human = board.getPlayers().get(currentPlayerIndex);
		board.movePlayer(human, target);
		waitingForHumanMove = false;
		board.clearTargetHighlights();
		board.repaint();

		if (target.isRoomCenter()) {
			promptHumanSuggestion(human, target);
		}
	}

	private void promptHumanSuggestion(Player human, BoardCell roomCell) {
		Card roomCard = new Card(board.getRoom(roomCell).getName(), CardType.ROOM);
		Window owner = SwingUtilities.getWindowAncestor(board);
		SuggestionDialog dialog = new SuggestionDialog(owner, board, roomCard);
		dialog.setVisible(true);

		Solution suggestion = dialog.getSuggestion();
		if (suggestion != null) {
			processSuggestion(human, suggestion);
		}
	}
}
