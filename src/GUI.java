

import javax.swing.JFrame;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private GamePanel gamePanel;

	public GUI(Client client) {
		setTitle("Statki");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(new StartPanel(client));
		setSize(100, 100);
		pack();
		setVisible(true);
		
		gamePanel = new GamePanel(client);
	}

	public GamePanel getGamePanel() {
		return gamePanel;
	}
}
