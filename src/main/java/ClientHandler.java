import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket client;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    public ClientHandler(Socket client){
        try{
            this.client = client;
            this.out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.username = in.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + username + " has entered the chat!");
        } catch(IOException e){
            closeEverything(client, in, out);
        }
    }

    @Override
    public void run(){
        String messageFromClient;

        while(client.isConnected()){
            try{
                messageFromClient = in.readLine();
                broadcastMessage(messageFromClient);
            } catch(IOException e){
                closeEverything(client, in ,out);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend){
        for(ClientHandler clientHandler: clientHandlers){
            try{
                if(!clientHandler.username.equals(username)){
                    clientHandler.out.write(messageToSend);
                    clientHandler.out.newLine();
                    clientHandler.out.flush();
                }
            } catch(IOException e){
                closeEverything(client, in, out);
            }
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + username + " has left the chat!");
    }

    public void closeEverything(Socket client, BufferedReader in, BufferedWriter out){
        removeClientHandler();
        try{
            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
            if(client != null){
                client.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
