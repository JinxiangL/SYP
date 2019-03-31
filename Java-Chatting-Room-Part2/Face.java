//Face.java
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class Face extends Frame
{
    private static final long serialVersionUID = 1L;
    Button clientBtn, serverBtn,sendMessage,closeConnection;
    JLabel serverIP,serverPort;
    JPanel southPanel,northPanel,westPanel,eastPanel,centerPanel;
    TextArea ta,tftype;
    TextField tfaddress, tfport;
    int port;
    int screenWidth,screenHeight,frameWidth,frameHeight;
    Client client;
    Server server;
    boolean iamserver;
    static Face frm;
    String userName;
    
    // add message
    public void addMessage(String msg)
    {
        this.ta.append(msg);
    }//end method addMessage
    
    // return server object
    public Server getServer()
    {
        return this.server;
    }
    
    // constructor
    public Face()
    {
        this.southPanel=new JPanel(new BorderLayout());
        this.northPanel=new JPanel(new FlowLayout(1,0,0));
        this.westPanel=new JPanel(new BorderLayout());
        this.eastPanel=new JPanel(new BorderLayout());
        this.centerPanel=new JPanel(new BorderLayout());
        clientBtn = new Button("CLIENT");
        serverBtn = new Button("SERVER");
        this.closeConnection=new Button("Disconnection");
        this.closeConnection.setEnabled(false);
        this.serverIP=new JLabel("IP Address",JLabel.CENTER);
        
        this.serverPort=new JLabel("  Port ",JLabel.CENTER);
        ta = new TextArea("", 10, 50, TextArea.SCROLLBARS_VERTICAL_ONLY);
        tfaddress = new TextField("127.0.0.1");
        tfport = new TextField("2000");
        tftype = new TextArea("", 1, 62, TextArea.SCROLLBARS_VERTICAL_ONLY);
        this.sendMessage=new Button("Send");
        this.userName="";
        
        // add key listener
        tftype.addKeyListener(new TFListener());
        ta.setEditable(false);
        
        this.setLayout(new BorderLayout());
        
        // center panel
        this.centerPanel.add(this.ta,BorderLayout.CENTER);
        this.centerPanel.setBorder(new TitledBorder("Received Messages"));
        this.add(this.centerPanel,BorderLayout.CENTER);
        
        // northPanel
        this.northPanel.add(this.serverIP);
        this.northPanel.add(this.tfaddress);
        this.northPanel.add(this.serverPort);
        this.northPanel.add(this.tfport);
        this.northPanel.add(this.clientBtn);
        this.northPanel.add(this.serverBtn);
        this.northPanel.add(this.closeConnection);
        this.add(this.northPanel,BorderLayout.NORTH);
        
        // south panel
        this.southPanel.add(this.tftype,BorderLayout.WEST);
        this.southPanel.add(this.sendMessage,BorderLayout.EAST);
        this.southPanel.setBorder(new TitledBorder("Compose new message"));
        this.add(this.southPanel,BorderLayout.SOUTH);
        
        this.frameWidth=600;
        this.frameHeight=600;
        setSize(this.frameWidth,this.frameHeight);
        setTitle("Chat Room");
        
        // get screen size and set the location in the center of the screen
        this.screenWidth=(int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        this.screenHeight=(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        this.setLocation((this.screenWidth-this.frameWidth)/2, (this.screenHeight-this.frameHeight)/2);
        this.setVisible(true);
        
        // add listener for send Button
        this.sendMessage.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                String temp;
                temp=tftype.getText();
                temp=temp.replaceAll("\\r|\\n", "");
                ta.append(userName+": " + temp + "\n");
                if (iamserver)
                    server.dataout(temp);
                else
                    client.dataout(temp);
                // clear the input area
                tftype.setText("");
            }//end actionPerformed function
            
        });//end actionListener
        
        // add action listener for client button
        clientBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // get the port
                port = Integer.parseInt(tfport.getText());
                // create new client
                client = new Client(tfaddress.getText(), port, frm);
                
                // get user name
                while(true){
                    userName=JOptionPane.showInputDialog(null, "What is your screen name", "Login", JOptionPane.QUESTION_MESSAGE);
                    if(userName==null)
                        JOptionPane.showMessageDialog(null, "Screen name cannot be empty", "ERROR", JOptionPane.ERROR_MESSAGE);
                    if(userName.equals("Server")==true){
                        JOptionPane.showMessageDialog(null, "Your screen name cannot be SERVER", "ERROR", JOptionPane.ERROR_MESSAGE);
                        userName=null;
                    }
                    if(userName!=null)
                        break;
                }//end while loop
                
                client.setUserName(userName);
                // start the client process
                client.start();
                setTitle(userName+"\'s Chat Room");
                // become a client
                iamserver=false;
                tfaddress.setText("CLIENT");
                
                // enable corresponding buttons
                tfaddress.setEnabled(false);
                tfport.setEnabled(false);
                serverBtn.setEnabled(false);
                clientBtn.setEnabled(false);
                closeConnection.setEnabled(true);
            }
        });//end client button action listener
        
        // add action listener for server button
        serverBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                // get the port
                port = Integer.parseInt(tfport.getText());
                // create new server
                server = new Server(port, frm);
                // start the server process
                server.start();
                userName="Server";
                
                // become a server
                iamserver = true;
                tfaddress.setText("SERVER");
                setTitle(userName+"\' Chat Room");
                
                // enable corresponding buttons
                tfaddress.setEnabled(false);
                tfport.setEnabled(false);
                serverBtn.setEnabled(false);
                clientBtn.setEnabled(false);
                closeConnection.setEnabled(true);
            }
        });//end server button listener
        
        // add action listener to close connection
        this.closeConnection.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                try {
                    closeConnection();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                clientBtn.setEnabled(true);
                serverBtn.setEnabled(true);
                closeConnection.setEnabled(false);
                ta.setText("");
                tftype.setText("");
                tfaddress.setText("127.0.0.1");
                tfaddress.setEnabled(true);
                tfaddress.setEditable(true);
                tfport.setText("2000");
                tfport.setEnabled(true);
                tfport.setEditable(true);
                
            }//end method action performed
            
        });//end action listener
        
        // add window listener for frame
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // exit normally
                try {
                    closeConnection();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                System.exit(0);
            }
        });//end window listener
        
    }//end constructor function
    
    public static void main(String args[])
    {
        frm = new Face();
    }
    
    // close current established connections
    @SuppressWarnings("deprecation")
    public void closeConnection() throws IOException{
        if(this.iamserver==false && this.client!=null && this.client.getSocket()!=null){
            this.client.dataout(" has left the Chat Room");
            this.client.getSocket().close();
            this.client.getOutputStream().close();
            this.client.getInputStream().close();
            this.client.stop();
        }
        else if(this.iamserver==true && this.server!=null && this.server.getServerSocket()!=null){
            // close all connections for clients
            for(int i=0;i<this.server.getClients().size();i++){
                this.server.getClients().get(i).getSocket().close();
                this.server.getClients().get(i).getOutputStream().close();
                this.server.getClients().get(i).getInputStream().close();
                this.server.getClients().get(i).stop();
            }
            // close server connection
            this.server.getServerSocket().close();
            this.server.stop();
        }
    }//end method closeConnection
    
    // an inherited listener
    private class TFListener implements KeyListener
    {
        // when user press a key
        public void keyPressed(KeyEvent e) {
            // when press "Enter", send the text to area
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if(clientBtn.isEnabled()==true || serverBtn.isEnabled()==true){
                    JOptionPane.showMessageDialog(null, "Please choose to be as SERVER or CLIENT firstly", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String temp;
                temp=tftype.getText();
                temp=temp.replaceAll("\\r|\\n", "");
                ta.append(userName+": " + temp + "\n");
                
                if (iamserver)
                    server.dataout(temp);
                else
                    client.dataout(temp);
                // clear the input area
                tftype.setText("");
            }
        }//end method keyPressed
        
        // when the user type a key
        public void keyTyped(KeyEvent e) {}
        
        // when user release a key
        public void keyReleased(KeyEvent e)
        {
            if(e.getKeyCode() == KeyEvent.VK_ENTER)
            {
                tftype.setText("");
            }
        }//end function keyReleased
        
    }//end class TFLister
    }//end class Face 
