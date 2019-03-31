//Server.java
import java.net.*;
import java.io.*;

public class Server extends Thread
{
    private ServerSocket skt;
    private Socket myClient;
    private int port;
    
    private PrintStream theOutputStream;
    private BufferedReader theInputStream;
    private String readin,message,userName;
    private Face chat;
    private boolean first;
    
    // server constructor
    public Server(int port, Face chat)
    {
        try
        {
            this.port = port;
            skt = new ServerSocket(port);
            this.chat = chat;
            this.first = true;
        } catch (IOException e)
        {
            chat.ta.append(e.toString());
        }
    }// end constructor
    
    // set user name
    public void setUserName(String userName)
    {
        this.userName=userName;
    }
    
    public Socket getClients()
    {
        return this.myClient;
    }
    
    public ServerSocket getServerSocket()
    {
        return this.skt;
    }
    
    // return output stream
    public PrintStream getOutputStream()
    {
        return this.theOutputStream;
    }
    
    // return input stream
    public BufferedReader getInputStream()
    {
        return this.theInputStream;
    }
    
    @SuppressWarnings("deprecation")
    public void run()
    {
        try
        {
            this.myClient=this.skt.accept();
            this.chat.addMessage("Server: New connection from "+myClient.getInetAddress().getHostName()+"......"+"\n");
            this.dataout("New connection from "+myClient.getInetAddress().getHostName()+"......");
            System.err.println("in server run");
        }
        catch(Exception e)
        {
            try
            {
                if(this.skt!=null)
                    this.skt.close();
            } catch (IOException e1)
            {
                // Auto-generated catch block
                e1.printStackTrace();
            }
            this.stop();
        }
        
        try
        {
            // get input stream and output stream
            InputStream inputStream=this.myClient.getInputStream();
            this.theInputStream=new BufferedReader(new InputStreamReader(inputStream));
            OutputStream outputStream=this.myClient.getOutputStream();
            this.theOutputStream=new PrintStream(outputStream);
            this.theOutputStream.flush();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        
        // read in messages
        try
        {
            while((this.readin=this.theInputStream.readLine()).equalsIgnoreCase("EXIT")==false)
            {
                if(this.first==true)
                {
                    this.setUserName(readin);
                    this.first=false;
                    continue;
                }
                message=this.readin+"\n";
                this.chat.addMessage(message);
            }
        }
        catch (Exception e)
        {
            this.stop();
        }
        // release the resources
        finally
        {
            try
            {
                this.theInputStream.close();
            }
            catch (IOException e)
            {
                // Auto-generated catch block
                e.printStackTrace();
            }
            this.theOutputStream.close();
            try
            {
                this.myClient.close();
            }
            catch (IOException e)
            {
                // Auto-generated catch block
                e.printStackTrace();
            }
        }// end finally
    }// end method run
    
    public void dataout(String data)
    {
        if(data==null)
            return;
        try
        {
            OutputStream outputStream=this.myClient.getOutputStream();
            this.theOutputStream=new PrintStream(outputStream);
            this.theOutputStream.print("Server: "+data+"\n");
            this.theOutputStream.flush();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
    }
}// end class Server
