package clueGame;

import java.awt.BorderLayout;
import java.util.HashMap;

import javax.swing.JFrame;
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
		knownCardsPanel.updatePanels(getHumanPlayer(), new HashMap<Card, Player>());
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
		});
	}
}
