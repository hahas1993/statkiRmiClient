

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StartPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public StartPanel(Client client) {
		
		JButton buttonSave = new JButton();
		buttonSave.setText("start game");
		buttonSave.setPreferredSize(new Dimension(50, 30));

		JTextField textFieldNick = new JTextField();
		textFieldNick.setText("nick");
		textFieldNick.setPreferredSize(new Dimension(150, 20));

		JPanel panelNick = new JPanel();
		panelNick.setPreferredSize(new Dimension(200, 50));
		panelNick.setBorder(BorderFactory.createTitledBorder("Nazwa uzytkownika"));
		panelNick.add(textFieldNick);

		setLayout(new BorderLayout());
		this.add(panelNick, BorderLayout.PAGE_START);
		this.add(buttonSave, BorderLayout.CENTER);

		buttonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!textFieldNick.getText().isEmpty() && !textFieldNick.getText().equals("nick")) {
					client.searchGame(textFieldNick.getText());
					client.getGui().setContentPane(client.getGui().getGamePanel());
					client.getGui().pack();
				}
			}
		});
	}
}
