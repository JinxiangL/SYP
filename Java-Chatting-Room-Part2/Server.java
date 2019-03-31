//Server.java

import java.net.*;
import java.util.ArrayList;
import java.io.*;

/**
 *
 * @author lwfcgz
 *
 */
public class Server extends Thread {
    private ServerSocket skt;
    private Socket myClient;
    private ArrayList<ClientThread> clientThread;
    private int port;
    
    private PrintStream theOutputStream;
    private BufferedReader theInputStream;
    private String readin;
    private Face chat;
    
    //      server constructor
    public Server(int port, Face chat) {
        try {
            this.port = port;
            this.clientThread=new ArrayList<ClientThread>();
            this.clientThread.clear();
            skt = new ServerSocket(port,100);
            this.chat = chat;
        } catch (IOException e) {
            chat.ta.append(e.toString());
        }
    }//end constructor
    
    
    //      return client thread
    public ArrayList<ClientThread> getClients(){
        return this.clientThread;
    }
    
    public ServerSocket getServerSocket(){
        return this.skt;
    }
    
    //      return output stream
    public PrintStream getOutputStream(){
        return this.theOutputStream;
    }
    
    //      return input stream
    public BufferedReader getInputStream(){
        return this.theInputStream;
    }
    
    @SuppressWarnings("deprecation")
    public void run() {
        //build connection
        while(true){
            //          this.myClient=null;
            try{
                Socket client;
                client=this.skt.accept();
                ClientThread newClient=new ClientThread(client,this.chat,this);
                newClient.start();
                this.clientThread.add(newClient);
                this.chat.addMessage("Server: New connection from "+client.getInetAddress().getHostName()+"......"+"\n");
                this.dataout("New connection from "+client.getInetAddress().getHostName()+"......");
                System.err.println("in server run");
            }
            catch(Exception e){
                try {
                    if(this.skt!=null)
                        this.skt.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                this.stop();
            }
        }//end while loop
    }//end method run
    
    public void dataout(String data) {
        if(data==null)
            return;
        //          send message to all clients
        for(int i=0;i<this.clientThread.size();i++){
            this.clientThread.get(i).getOutputStream().print("Server: "+data+"\n");
            this.clientThread.get(i).getOutputStream().flush();
        }
    }
}//end class Server
