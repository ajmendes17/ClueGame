package clueGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
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

	public void setSeenCards(Map<Card, Player> seenCards) {
		resetSection(peoplePanel, "People");
		resetSection(roomsPanel, "Rooms");
		resetSection(weaponsPanel, "Weapons");

		if (seenCards != null && !seenCards.isEmpty()) {
			List<Card> sortedCards = new ArrayList<>(seenCards.keySet());
			sortedCards.sort(Comparator.comparing(Card::getType).thenComparing(Card::getCardName));

			for (Card card : sortedCards) {
				Player owner = seenCards.get(card);
				addSeenCard(card, owner);
			}
		}

		addEmptyMessageIfNeeded(peoplePanel);
		addEmptyMessageIfNeeded(roomsPanel);
		addEmptyMessageIfNeeded(weaponsPanel);

		revalidate();
		repaint();
	}

	public void updatePanels(Player humanPlayer, Map<Card, Player> seenCards) {
		setHand(humanPlayer);
		setSeenCards(seenCards);
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
		addCardField(panel, card, background, card.getType().toString());
	}

	private void addCardField(JPanel panel, Card card, Color background, String tooltip) {
		JTextField cardField = new JTextField(card.getCardName());
		cardField.setEditable(false);
		cardField.setBackground(background);
		cardField.setForeground(getReadableTextColor(background));
		cardField.setOpaque(true);
		cardField.setToolTipText(tooltip);
		panel.add(cardField);
	}

	private void addSeenCard(Card card, Player owner) {
		Color background = owner == null ? Color.WHITE : getHighlightColor(owner.getColor());
		JPanel panel = getPanelForCardType(card.getType());
		String tooltip = owner == null ? "Unknown holder" : "Shown by " + owner.getName();
		addCardField(panel, card, background, tooltip);
	}

	private JPanel getPanelForCardType(CardType type) {
		if (type == CardType.PERSON) {
			return peoplePanel;
		}
		if (type == CardType.ROOM) {
			return roomsPanel;
		}
		return weaponsPanel;
	}

	private void addEmptyMessageIfNeeded(JPanel panel) {
		if (panel.getComponentCount() == 0) {
			panel.add(new JLabel("None seen"));
		}
	}

	private Color getReadableTextColor(Color background) {
		int brightness = background.getRed() + background.getGreen() + background.getBlue();
		return brightness < 380 ? Color.WHITE : Color.BLACK;
	}

	private Color getHighlightColor(Color playerColor) {
		int red = blendWithWhite(playerColor.getRed());
		int green = blendWithWhite(playerColor.getGreen());
		int blue = blendWithWhite(playerColor.getBlue());
		return new Color(red, green, blue);
	}

	private int blendWithWhite(int colorValue) {
		return (int) (colorValue * 0.25 + 255 * 0.75);
	}

	public static void main(String[] args) {
		KnownCardsPanel panel = new KnownCardsPanel();

		HumanPlayer human = new HumanPlayer("AJ Mendes", Color.BLUE, 0, 0);
		human.updateHand(new Card("Moon Room", CardType.ROOM));
		human.updateHand(new Card("Candlestick", CardType.WEAPON));
		human.updateHand(new Card("AJ Mendes", CardType.PERSON));
		human.updateHand(new Card("Dagger", CardType.WEAPON));

		Player mustard = new ComputerPlayer("Col. Mustard", Color.ORANGE, 0, 0);
		Player peacock = new ComputerPlayer("Mrs. Peacock", Color.CYAN, 0, 0);
		Player scarlet = new ComputerPlayer("Miss Scarlet", Color.RED, 0, 0);
		Player plum = new ComputerPlayer("Professor Plum", Color.MAGENTA, 0, 0);
		Player green = new ComputerPlayer("Mr. Green", Color.GREEN, 0, 0);

		Map<Card, Player> seenCards = new HashMap<>();
		seenCards.put(new Card("Rope", CardType.WEAPON), mustard);
		seenCards.put(new Card("Wrench", CardType.WEAPON), peacock);
		seenCards.put(new Card("Lead Pipe", CardType.WEAPON), plum);
		seenCards.put(new Card("Revolver", CardType.WEAPON), scarlet);
		seenCards.put(new Card("Danny DeVito", CardType.PERSON), scarlet);
		seenCards.put(new Card("Colonel Mustard", CardType.PERSON), mustard);
		seenCards.put(new Card("Mrs. Peacock", CardType.PERSON), peacock);
		seenCards.put(new Card("Professor Plum", CardType.PERSON), plum);
		seenCards.put(new Card("Mr. Green", CardType.PERSON), green);
		seenCards.put(new Card("Movie Theater", CardType.ROOM), mustard);
		seenCards.put(new Card("Library", CardType.ROOM), peacock);
		seenCards.put(new Card("Kitchen", CardType.ROOM), scarlet);
		seenCards.put(new Card("Conservatory", CardType.ROOM), plum);
		seenCards.put(new Card("Ballroom", CardType.ROOM), green);

		panel.updatePanels(human, seenCards);

		JFrame frame = new JFrame("Known Cards Panel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 600);
		frame.add(panel, BorderLayout.CENTER);
		frame.setVisible(true);
	}
}
