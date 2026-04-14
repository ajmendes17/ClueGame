package clueGame;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GameControlPanel extends JPanel {
	private JButton nextPlayerButton;
	private JButton accusationButton;

	private JTextField currentTurnField;
	private JTextField dieRollField;
	private JTextField guessField;
	private JTextField guessResultField;

	public GameControlPanel() {
		nextPlayerButton = new JButton("Next player");
		accusationButton = new JButton("Make accusation");

		currentTurnField = new JTextField(15);
		dieRollField = new JTextField(5);
		guessField = new JTextField(25);
		guessResultField = new JTextField(25);
	}
}
