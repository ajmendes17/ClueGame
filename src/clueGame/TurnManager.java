package clueGame;

import java.util.Random;


/*
 * Board currently owns display, player movement, click validation, dice rolling, target highlighting, and turn advancement all in one place.
 * Adding a new file to do all of this works to refactor turn flow into dedicated manager
 */
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

	public void setControlPanel(GameControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}

	public int getCurrentRoll() {
		return currentRoll;
	}

	public boolean isWaitingForHumanMove() {
		return waitingForHumanMove;
	}
}
