package clueGame;

import java.awt.BorderLayout;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ClueGame extends JFrame {
	private static final int FRAME_WIDTH = 1000;
	private static final int FRAME_HEIGHT = 750;

	private Board board;
	private GameControlPanel controlPanel;
	private KnownCardsPanel knownCardsPanel;

	public ClueGame() {
		setTitle("Clue Game");
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		board = Board.getInstance();
		board.setConfigFiles("data/ClueMap.csv", "data/ClueSetup.txt");
		board.initialize();
		board.selectAnswer();
		board.dealCards();

		controlPanel = new GameControlPanel();
		knownCardsPanel = new KnownCardsPanel();
		board.setControlPanel(controlPanel);
		controlPanel.setNextPlayerListener(e -> board.processNextPlayer());

		add(board, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);
		add(knownCardsPanel, BorderLayout.EAST);

		updatePanelsForStart();
	}

	private void updatePanelsForStart() {
		Player firstPlayer = getFirstPlayer();
		if (firstPlayer == null) {
			return;
		}

		controlPanel.setTurn(firstPlayer, 0);
		controlPanel.setGuess("No guess yet");
		controlPanel.setGuessResult("No result yet");
		knownCardsPanel.updatePanels(getHumanPlayer(), buildDemoSeenCards());
	}

	private void showSplashScreen() {
		JOptionPane.showMessageDialog(this,
				"You are playing Clue!\nCan you find the solution before the computer players?",
				"Welcome to Clue",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private Map<Card, Player> buildDemoSeenCards() {
		Map<Card, Player> seenCards = new LinkedHashMap<>();

		for (Player player : board.getPlayers()) {
			if (player instanceof HumanPlayer) {
				continue;
			}

			addFirstCardFromPlayer(seenCards, player);
		}

		return seenCards;
	}

	private void addFirstCardFromPlayer(Map<Card, Player> seenCards, Player player) {
		for (Card card : player.getHand()) {
			seenCards.put(card, player);
			return;
		}
	}

	private Player getFirstPlayer() {
		if (board.getPlayers() == null || board.getPlayers().isEmpty()) {
			return null;
		}
		return board.getPlayers().get(0);
	}

	private Player getHumanPlayer() {
		for (Player player : board.getPlayers()) {
			if (player instanceof HumanPlayer) {
				return player;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			ClueGame game = new ClueGame();
			game.setVisible(true);
			game.showSplashScreen();
			game.board.processNextPlayer();
		});
	}
}
