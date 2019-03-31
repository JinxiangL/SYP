//Client.java
import java.net.*;
import java.util.concurrent.Executors;
import java.io.*;
import javax.swing.JOptionPane;

class Client extends Thread
{
    Socket skt;
    InetAddress host;
    int port;
    Executors h;
    BufferedReader theInputStream;
    PrintStream theOutputStream;
    String readin,userName;
    Face chat;
    
    // client constructor
    public Client(String ip, int p, Face chat) {
        try {
            host = InetAddress.getByName(ip);
            port = p;
            this.chat = chat;
        } catch (IOException e) {
            chat.ta.append(e.toString());
        }
    }//end constructor
    
    // return user name
    public String getUserName(){
        return this.userName;
    }
    
    // set user name
    public void setUserName(String userName){
        this.userName=userName;
    }
    
    // return socket
    public Socket getSocket(){
        return this.skt;
    }
    
    // get output stream
    public PrintStream getOutputStream(){
        return this.theOutputStream;
    }
    
    // get input stream
    public BufferedReader getInputStream(){
        return this.theInputStream;
    }
    
    @SuppressWarnings("deprecation")
    public void run()
    {
        try {
            this.skt=new Socket(this.host,this.port);
            InputStream inputStream=this.skt.getInputStream();
            this.theInputStream=new BufferedReader(new InputStreamReader(inputStream));
            OutputStream outputStream=this.skt.getOutputStream();
            this.theOutputStream=new PrintStream(outputStream);
            // send userName to server
            this.theOutputStream.print(this.userName+"\n");
            this.theOutputStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "Cannot connect to SERVER", "ERROR", JOptionPane.ERROR_MESSAGE);
            this.stop();
            // e.printStackTrace();
        }
        System.err.println("in client run");
        // read in messages
        try {
            while((this.readin=this.theInputStream.readLine()).equals("EXIT")==false){
                String message=this.readin+"\n";
                // add message into area
                this.chat.addMessage(message);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // close the established connection
        finally{
            if(this.skt!=null && this!=null)
                this.dataout("Connection closed from "+this.skt.getInetAddress().getHostName()+"......");
            try {
                if(this.theInputStream != null)
                    this.theInputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(this.theOutputStream!=null){
                this.theOutputStream.close();
            }
            try {
                if(this.skt!=null)
                    this.skt.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }//end method run
    
    public void dataout(String data) {
        if(data==null)
            return;
        if(this.theOutputStream!=null){
            this.theOutputStream.print(this.userName+": "+data+"\n");
            this.theOutputStream.flush();
        }
    }//end function dataout
    
}//end class Client
