package clueGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class GameControlPanel extends JPanel {
	private JButton nextPlayerButton;
	private JButton accusationButton;
	private JButton suggestionButton;

	private JTextField currentTurnField;
	private JTextField dieRollField;
	private JTextField guessField;
	private JTextField guessResultField;

	public GameControlPanel() {
		setLayout(new GridLayout(2, 1));

		nextPlayerButton = new JButton("Next player");
		accusationButton = new JButton("Make accusation");
		suggestionButton = new JButton("Make suggestion");

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
		panel.setLayout(new BorderLayout());

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(2, 2));
		infoPanel.add(new JLabel("Whose turn?"));
		infoPanel.add(new JLabel("Roll"));
		infoPanel.add(currentTurnField);
		infoPanel.add(dieRollField);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3, 1, 0, 5));
		buttonPanel.add(nextPlayerButton);
		buttonPanel.add(accusationButton);
		buttonPanel.add(suggestionButton);

		panel.add(infoPanel, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.EAST);

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
		currentTurnField.setForeground(getReadableTextColor(player.getColor()));
		dieRollField.setText(String.valueOf(roll));
	}

	private Color getReadableTextColor(Color backgroundColor) {
		int brightness = backgroundColor.getRed() + backgroundColor.getGreen() + backgroundColor.getBlue();
		if (brightness < 384) {
			return Color.WHITE;
		}
		return Color.BLACK;
	}

	public void setGuess(String guess) {
		guessField.setText(guess);
	}

	public void setGuessResult(String guessResult) {
		guessResultField.setText(guessResult);
	}

	public void setNextPlayerListener(ActionListener listener) {
		nextPlayerButton.addActionListener(listener);
	}

	public static void main(String[] args) {
		GameControlPanel panel = new GameControlPanel();
		JFrame frame = new JFrame();
		frame.setContentPane(panel);
		frame.setTitle("Game Control Panel");
		frame.setSize(750, 180);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		panel.setTurn(new ComputerPlayer("Col. Mustard", Color.ORANGE, 0, 0), 5);
		panel.setGuess("I have no guess!");
		panel.setGuessResult("So you have nothing?");
	}
}
