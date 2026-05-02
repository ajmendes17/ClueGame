package clueGame;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SuggestionDialog extends JDialog {
	private JComboBox<String> personCombo;
	private JComboBox<String> weaponCombo;
	private JComboBox<String> roomCombo;
	private Card roomCard;
	private Solution suggestion;

	public SuggestionDialog(Window owner, Board board, Card roomCard) {
		super(owner, "Make a Suggestion", ModalityType.APPLICATION_MODAL);
		this.roomCard = roomCard;
		this.suggestion = null;
		this.roomCombo = null;

		personCombo = new JComboBox<>();
		for (Player player : board.getPlayers()) {
			personCombo.addItem(player.getName());
		}

		weaponCombo = new JComboBox<>();
		for (Card weapon : board.getWeapons()) {
			weaponCombo.addItem(weapon.getCardName());
		}

		setLayout(new BorderLayout());
		add(createSelectionPanel(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(owner);
	}

	public SuggestionDialog(Window owner, Board board) {
		super(owner, "Make an Accusation", ModalityType.APPLICATION_MODAL);
		this.roomCard = null;
		this.suggestion = null;

		personCombo = new JComboBox<>();
		for (Player player : board.getPlayers()) {
			personCombo.addItem(player.getName());
		}

		weaponCombo = new JComboBox<>();
		for (Card weapon : board.getWeapons()) {
			weaponCombo.addItem(weapon.getCardName());
		}

		roomCombo = new JComboBox<>();
		for (Room room : board.getRoomMap().values()) {
			if (!room.getName().equals("Walkway") && !room.getName().equals("Unused Area")) {
				roomCombo.addItem(room.getName());
			}
		}

		setLayout(new BorderLayout());
		add(createSelectionPanel(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(owner);
	}

	public Solution getSuggestion() {
		return suggestion;
	}

	private JPanel createSelectionPanel() {
		JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

		panel.add(new JLabel("Person"));
		panel.add(personCombo);
		panel.add(new JLabel("Weapon"));
		panel.add(weaponCombo);
		panel.add(new JLabel("Room"));
		if (roomCombo == null) {
			panel.add(new JLabel(roomCard.getCardName()));
		} else {
			panel.add(roomCombo);
		}

		return panel;
	}

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel();
		JButton submitButton = new JButton("Submit");
		JButton cancelButton = new JButton("Cancel");

		submitButton.addActionListener(e -> submitSuggestion());
		cancelButton.addActionListener(e -> dispose());

		panel.add(submitButton);
		panel.add(cancelButton);
		return panel;
	}

	private void submitSuggestion() {
		Card person = new Card((String) personCombo.getSelectedItem(), CardType.PERSON);
		Card weapon = new Card((String) weaponCombo.getSelectedItem(), CardType.WEAPON);
		Card selectedRoom = roomCard;
		if (roomCombo != null) {
			selectedRoom = new Card((String) roomCombo.getSelectedItem(), CardType.ROOM);
		}
		suggestion = new Solution(person, weapon, selectedRoom);
		dispose();
	}
}
