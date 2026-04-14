package clueGame;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class GameControlPanel extends JPanel {
	private JButton nextPlayerButton;
	private JButton accusationButton;

	private JTextField currentTurnField;
	private JTextField dieRollField;
	private JTextField guessField;
	private JTextField guessResultField;

	public GameControlPanel() {
		setLayout(new GridLayout(2, 1));

		nextPlayerButton = new JButton("Next player");
		accusationButton = new JButton("Make accusation");

		currentTurnField = new JTextField(15);
		dieRollField = new JTextField(5);
		guessField = new JTextField(25);
		guessResultField = new JTextField(25);

		currentTurnField.setEditable(false);
		dieRollField.setEditable(false);
		guessField.setEditable(false);
		guessResultField.setEditable(false);

		add(createTurnPanel());
		add(createGuessPanel());
	}

	private JPanel createTurnPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 3));

		panel.add(new JLabel("Whose turn?"));
		panel.add(new JLabel("Roll"));
		panel.add(new JLabel(""));

		panel.add(currentTurnField);
		panel.add(dieRollField);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(nextPlayerButton);
		buttonPanel.add(accusationButton);
		panel.add(buttonPanel);

		panel.setBorder(new TitledBorder(new EtchedBorder(), "Game Control"));
		return panel;
	}

	private JPanel createGuessPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 2));

		panel.add(new JLabel("Guess"));
		panel.add(new JLabel("Guess Result"));
		panel.add(guessField);
		panel.add(guessResultField);

		panel.setBorder(new TitledBorder(new EtchedBorder(), "Guess Information"));
		return panel;
	}

	public void setTurn(Player player, int roll) {
		currentTurnField.setText(player.getName());
		currentTurnField.setBackground(player.getColor());
		dieRollField.setText(String.valueOf(roll));
	}

	public void setGuess(String guess) {
		guessField.setText(guess);
	}

	public void setGuessResult(String guessResult) {
		guessResultField.setText(guessResult);
	}
}
