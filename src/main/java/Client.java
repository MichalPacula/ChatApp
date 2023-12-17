import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket client;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    public Client(Socket client, String username){
        try{
            this.client = client;
            this.out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.username = username;
        } catch(IOException e){
            closeEverything(client, in, out);
        }
    }

    public void sendMessage(){
        try{
            out.write(username);
            out.newLine();
            out.flush();

            Scanner scanner = new Scanner(System.in);
            while(client.isConnected()){
                String messageToSend = scanner.nextLine();
                out.write(username + ": " + messageToSend);
                out.newLine();
                out.flush();
            }
        } catch(IOException e){
            closeEverything(client, in, out);
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupChat;
                while(client.isConnected()){
                    try{
                        messageFromGroupChat = in.readLine();
                        System.out.println(messageFromGroupChat);
                    } catch(IOException e){
                        closeEverything(client, in, out);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket client, BufferedReader in, BufferedWriter out){
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

    public static void main(String[] args) throws IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username to enter the group chat: ");
        String username = scanner.nextLine();
        Socket clientSocket = new Socket("localhost", 3333);
        Client client = new Client(clientSocket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}
