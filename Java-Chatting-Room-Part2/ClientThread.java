// ClientThread.java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread extends Thread{
    
    private Socket socket;
    private PrintStream theOutputStream;
    private BufferedReader theInputStream;
    private String readin,message,userName;
    private Face face;
    private Server server;
    private boolean first;
    
    //  public constructor
    public ClientThread(Socket socket,Face face,Server server) throws IOException
    {
        this.socket=socket;
        this.face=face;
        this.server=server;
        this.first=true;
        
        // get input stream and output stream
        InputStream inputStream=this.socket.getInputStream();
        this.theInputStream=new BufferedReader(new InputStreamReader(inputStream));
        OutputStream outputStream=this.socket.getOutputStream();
        this.theOutputStream=new PrintStream(outputStream);
        this.theOutputStream.flush();
    }//end constructor
    
    //  return user name
    public String getUserName()
    {
        return this.userName;
    }
    
    //  set user name
    public void setUserName(String userName)
    {
        this.userName=userName;
    }
    
    //  get output stream object
    public PrintStream getOutputStream(){
        return this.theOutputStream;
    }
    
    //  get input stream object
    public BufferedReader getInputStream(){
        return this.theInputStream;
    }
    
    //  return socket
    public Socket getSocket(){
        return this.socket;
    }
    
    // rewrite this method
    public void run()
    {
        // read in messages
        try {
            while((this.readin=this.theInputStream.readLine()).equalsIgnoreCase("EXIT")==false)
            {
                if(this.first==true)
                {
                    this.setUserName(readin);
                    this.first=false;
                    continue;
                }
                message=this.readin+"\n";
                this.face.addMessage(message);
                this.sendMessages(message);
            }
        } catch (Exception e) {
            this.stop();
        }
        // release the resources
        finally{
            try {
                this.theInputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            this.theOutputStream.close();
            try {
                this.socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }//end finally
    }//end method run
    
    //  send messages to all online users
    public void sendMessages(String message){
        ArrayList<ClientThread> clients=this.server.getClients();
        for(int i=0;i<clients.size();i++){
            String name=clients.get(i).getUserName();
            // check if it is the same user
            if(message.length()>=name.length() && (message.substring(0, name.length()).equals(name) ==true ))
                continue;
            clients.get(i).getOutputStream().print(message);
            clients.get(i).getOutputStream().flush();
        }//end for loop
    }//end method sendMessages
    
}//end class ClientThread 
