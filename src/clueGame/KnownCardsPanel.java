package clueGame;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
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

	public void setHand(Player humanPlayer) {
		if (humanPlayer == null) {
			setHand((Set<Card>) null);
			return;
		}
		setHand(humanPlayer.getHand());
	}

	public void setHand(Set<Card> cards) {
		resetSection(handPanel, "Cards in Hand");

		if (cards == null || cards.isEmpty()) {
			handPanel.add(new JLabel("No cards"));
		} else {
			List<Card> sortedCards = new ArrayList<>(cards);
			sortedCards.sort(Comparator.comparing(Card::getType).thenComparing(Card::getCardName));

			for (Card card : sortedCards) {
				addCardField(handPanel, card, Color.WHITE);
			}
		}

		handPanel.revalidate();
		handPanel.repaint();
	}

	private void configureSection(JPanel panel, String title) {
		resetSection(panel, title);
		panel.add(new JLabel(" "));
	}

	private void resetSection(JPanel panel, String title) {
		panel.removeAll();
		panel.setLayout(new GridLayout(0, 1));
		panel.setBorder(new TitledBorder(new EtchedBorder(), title));
	}

	private void addCardField(JPanel panel, Card card, Color background) {
		JTextField cardField = new JTextField(card.getCardName());
		cardField.setEditable(false);
		cardField.setBackground(background);
		cardField.setToolTipText(card.getType().toString());
		panel.add(cardField);
	}
}
