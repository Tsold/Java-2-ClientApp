package hr.algebra.chat;

import com.sun.jndi.rmi.registry.RegistryContextFactory;
import hr.algebra.chat.interfaces.Chat;
import hr.algebra.chat.interfaces.Server;
import hr.algebra.controllers.ChatController;
import hr.algebra.jndi.InitialDirContextCloseable;
import javax.naming.Context;
import javax.naming.NamingException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatClient extends UnicastRemoteObject implements Chat {

    private String name;
    private Server server;
    private static final long serialVersionUID = 1L;
    private ChatController chatController;
    private static final String RMI_URL = "rmi://localhost:1099";


    public ChatClient(ChatController chatController) throws RemoteException, MalformedURLException, NotBoundException {
        super();
        this.chatController = chatController;
        fetchServer();
    }





    @Override
    public void sendUserList(String[] userList) throws RemoteException
    {
        new Thread(() -> chatController.newUserList(userList)).start();
    }

    @Override
    public void receiveMessage(String message)
    {
        new Thread(() -> chatController.setMessage(message) ).start();
    }




    private void fetchServer() {

        final Hashtable<String, String> properties = new Hashtable<>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, RegistryContextFactory.class.getName());
        properties.put(Context.PROVIDER_URL, RMI_URL);

        try (InitialDirContextCloseable context = new InitialDirContextCloseable(properties)) {
            server = (Server) context.lookup(Server.DEFAULT_NAME);
        } catch (NamingException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    // Send Vehicle Request
    public boolean requestLogin(int id) throws RemoteException
    {
        if (server.requestAvailable(id))
        {
            return true;
        }
        return false;
    }


    public void logoutRequest() throws RemoteException {
        this.server.emptyRequests();
    }



    //Chat server
    public void serverLogin(String name) throws RemoteException
    {
            server.addUser(name, this);
            this.name = name;
    }


    public void sendMessage(String message)
    {
        new Thread(() -> this.send(message)).start();
    }

    private void send(String message)
    {
        try
        {
            this.server.postMessage(this.name, message);
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    public void logout()
    {
        try
        {
            this.server.logOut(this.name);
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
}
