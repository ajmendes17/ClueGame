package clueGame;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class KnownCardsPanel extends JPanel {
	private JPanel handPanel;
	private JPanel peoplePanel;
	private JPanel roomsPanel;
	private JPanel weaponsPanel;

	public KnownCardsPanel() {
		setLayout(new GridLayout(4, 1));

		handPanel = new JPanel();
		peoplePanel = new JPanel();
		roomsPanel = new JPanel();
		weaponsPanel = new JPanel();

		configureSection(handPanel, "Cards in Hand");
		configureSection(peoplePanel, "People");
		configureSection(roomsPanel, "Rooms");
		configureSection(weaponsPanel, "Weapons");

		add(handPanel);
		add(peoplePanel);
		add(roomsPanel);
		add(weaponsPanel);
	}

	private void configureSection(JPanel panel, String title) {
		panel.setLayout(new GridLayout(0, 1));
		panel.setBorder(new TitledBorder(new EtchedBorder(), title));
		panel.add(new JLabel(" "));
	}
}
