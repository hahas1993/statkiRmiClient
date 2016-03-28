
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.ssl.SslRMIClientSocketFactory;

import server.Server;
import utils.BoardStates;
import utils.InfoMsg;
import utils.Player;
import utils.Point;
import utils.RemoteObserver;

public class Client extends UnicastRemoteObject implements RemoteObserver {

    private static final long serialVersionUID = -5142389190052534862L;
    private static final int PORT = 4242;
    private GUI gui;
    private Player player;
    private Server server;
    
    private Client() throws RemoteException, InterruptedException, NotBoundException {
        setSettings();
        Registry registry = LocateRegistry.getRegistry(null, PORT, new SslRMIClientSocketFactory());
		server = (Server) registry.lookup("Server");
    	gui = new GUI(this);
    }

    public static void main(String[] args) throws RemoteException, InterruptedException, NotBoundException {
        new Client();
    }
    
    public void searchGame(String nick){
    	try {
			player = server.searchGame(nick);
			server.addObserver(this, player.getGameId());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }
    
    public void setShips(BoardStates[][] boardStates){
    	try {
    		player.setBoard(boardStates);
			server.fillBoard(player);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }
    
    public void move(int x, int y){
    	try {
			server.move(player.getGameId(), player.getId(), new Point(x, y));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }

    private static void setSettings() {
        String pass = "password";
        System.setProperty("javax.net.ssl.debug", "all");
        System.setProperty("javax.net.ssl.keyStore", "keys/clientkeystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", pass);
        System.setProperty("javax.net.ssl.trustStore", "keys/clienttruststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", pass);
    }
    
    @Override
    public void update(Object observable, Object updateMsg) throws RemoteException {
        InfoMsg infoMsg = (InfoMsg) updateMsg;
        
        switch (infoMsg.getMsg()) {
        	case "start":
        		gui.getGamePanel().setTurn(player.getId() == infoMsg.getTurn());
        		break;
        	case "move":
        		gui.getGamePanel().updateShips(infoMsg.getPlayerId() == player.getId(), infoMsg.getPoint(), infoMsg.isHit());
        		gui.getGamePanel().setTurn(player.getId() == infoMsg.getTurn());
        		break;
        	case "end":
        		gui.getGamePanel().end(infoMsg.getPlayerId() == player.getId());
        		break;
        }
        System.out.println(infoMsg);
    }

	public GUI getGui() {
		return gui;
	}

}
