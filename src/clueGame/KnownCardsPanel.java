package clueGame;

import javax.swing.JPanel;

public class KnownCardsPanel extends JPanel {
	private JPanel handPanel;
	private JPanel peoplePanel;
	private JPanel roomsPanel;
	private JPanel weaponsPanel;

	public KnownCardsPanel() {
		handPanel = new JPanel();
		peoplePanel = new JPanel();
		roomsPanel = new JPanel();
		weaponsPanel = new JPanel();
	}
}
