

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;

import utils.BoardStates;
import utils.Point;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	Client client;
	
	private boolean moveFlag = false;

	private JButton[][] playerGrid;
	private JButton[][] comGrid;
	private JTextArea textArea;
	private JButton buttonConfirmShips;
	private Checkbox playerTurn;
	private Checkbox comTurn;
	private ActionListener panelPlayerActionListener;
	private ActionListener panelComActionListener;

	public GamePanel(Client client) {
		this.client = client;
		int width = 10;
		int height = 10;
		
		createActionListeners();
		
		Border playerBorder = BorderFactory.createTitledBorder("Twoje statki");	
		Border comBorder = BorderFactory.createTitledBorder("Statki przeciwnika");
		
		JPanel panelPlayer = new JPanel();
		panelPlayer.setBorder(playerBorder);
		panelPlayer.setLayout(new GridLayout(10, 10));
		
		JPanel panelCom = new JPanel();
		panelCom.setBorder(comBorder);
		panelCom.setLayout(new GridLayout(10, 10));
		
		CheckboxGroup cbg = new CheckboxGroup();
		playerTurn = new Checkbox("Gracz", cbg, false);
		comTurn = new Checkbox("Przeciwnik", cbg, false);
		playerTurn.setEnabled(false);
		comTurn.setEnabled(false);
		
		buttonConfirmShips = new JButton("Zatwierdz");
		buttonConfirmShips.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setShips();
				buttonConfirmShips.setEnabled(false);
			}
		});
		
		playerGrid = new JButton[width][height];
		comGrid = new JButton[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				playerGrid[x][y] = new JButton();
				panelPlayer.add(playerGrid[x][y]);
				playerGrid[x][y].setBackground(Color.WHITE);
				playerGrid[x][y].setPreferredSize(new Dimension(35, 35));
				playerGrid[x][y].addActionListener(panelPlayerActionListener);
				playerGrid[x][y].setName(x + "" + y);
				
				comGrid[x][y] = new JButton();
				panelCom.add(comGrid[x][y]);
				comGrid[x][y].setBackground(Color.LIGHT_GRAY);
				comGrid[x][y].setPreferredSize(new Dimension(35, 35));
				comGrid[x][y].setName(x + "" + y);
				comGrid[x][y].addActionListener(panelComActionListener);
			}
		}
		
		JPanel container = new JPanel();
		container.add(panelPlayer);
		container.add(panelCom);
		
		JPanel bottomRight = new JPanel();
		bottomRight.add(buttonConfirmShips);
		bottomRight.add(playerTurn);
		bottomRight.add(comTurn);
		bottomRight.setLayout(new BoxLayout(bottomRight, BoxLayout.Y_AXIS));
		
        textArea = new JTextArea(10, 68);
        textArea.setText("Rozstaw statki: jeden 4-kratkowy, dwa 3-kratkowe, trzy 2-kratkowe i cztery 1-kratkowe\n");
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        textArea.setEditable(false);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JPanel bottomLeft = new JPanel();
		bottomLeft.add(scrollPane);

		JPanel third = new JPanel();
		third.add(bottomLeft);
		third.add(bottomRight);
		third.setBorder(BorderFactory.createTitledBorder("Statki"));
	
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(container);
		this.add(third);
	}
	
	public void end(boolean win){
		if(win){
			textArea.setText(textArea.getText() + "Wygrana\n");
		}
		else {
			textArea.setText(textArea.getText() + "Przegrana\n");
		}
	}
	
	public void setTurn(boolean turn){
		if(turn){
			textArea.setText(textArea.getText() + "Twoj ruch\n");
		}
		else {
			textArea.setText(textArea.getText() + "Oczekiwanie na ruch przeciwnika...\n");
		}
		playerTurn.setState(turn);
		comTurn.setState(!turn);
		moveFlag = turn;
	}
	
	public void updateShips(boolean me, Point point, boolean hit){
		if(me){
			if(hit){
				comGrid[point.getX()][point.getY()].setBackground(Color.RED);
				textArea.setText(textArea.getText() + "Trafiles statek przeciwnika\n");
			}
			else {
				comGrid[point.getX()][point.getY()].setBackground(Color.BLUE);
				textArea.setText(textArea.getText() + "Spudlowales\n");
			}
		}
		else {
			if(hit){
				textArea.setText(textArea.getText() + "Przeciwnik trafil Twoj statek\n");
				playerGrid[point.getX()][point.getY()].setBackground(Color.RED);
			}
			else {
				textArea.setText(textArea.getText() + "Przeciwnik spudlowal\n");
			}
		}
		
	}
	
	private void setShips(){
		BoardStates[][] boardStates = new BoardStates[10][10];
        for(int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                if (playerGrid[i][j].getBackground().equals(Color.GREEN)){
                	boardStates[i][j] = BoardStates.SHIP;
                }
                if (playerGrid[i][j].getBackground().equals(Color.WHITE)){
                	boardStates[i][j] = BoardStates.EMPTY;
                }
                playerGrid[i][j].setEnabled(false);
            }
        }
        textArea.setText(textArea.getText() + "Oczekiwanie na rozstawienie statkow przez przeciwnika...\n");
        client.setShips(boardStates);
    }

	private void createActionListeners() {
		panelPlayerActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playerButtonClicked((JButton) e.getSource());
			}
		};
		
		panelComActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				comButtonClicked((JButton) e.getSource());
			}
		};
	}

	private void playerButtonClicked(JButton b) {
		if (b.getBackground() == Color.WHITE) {
			b.setBackground(Color.GREEN);
		}
		else {
			b.setBackground(Color.WHITE);
		}
	}
	
	private void comButtonClicked(JButton b) {
		int x = Integer.parseInt(b.getName().substring(0, 1));
		int y = Integer.parseInt(b.getName().substring(1, 2));
		if (moveFlag && b.getBackground() == Color.LIGHT_GRAY) {
			textArea.setText(textArea.getText() + "Strzelasz w pole (" + x + "," + y + ")\n");
			moveFlag = false;
			client.move(x, y);
		}
	}
}
