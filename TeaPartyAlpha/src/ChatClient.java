import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

//http://www.mediafire.com/file/9gn4vsbqpdwfmol/TeaParty.jar

public class ChatClient {

	protected BufferedReader in;
    protected PrintWriter out;
    protected JFrame frame = new JFrame("TeaParty");
    protected JTextField textField = new JTextField(40);
    protected JTextPane messageArea = new JTextPane();
    protected StyledDocument doc = messageArea.getStyledDocument();
    
    public ChatClient() {

        // Layout GUI
        textField.setEditable(false);
        textField.setMaximumSize(
            new Dimension(Integer.MAX_VALUE,
            textField.getPreferredSize().height));
        //textField.setMaximumSize( textField.getPreferredSize() );
        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalGlue());
        box.add(textField);
        box.add(Box.createVerticalGlue());
        messageArea.setEditable(false);
		ImageIcon img = new ImageIcon(getClass().getResource("/tp.jpg"));
		frame.setIconImage(img.getImage());
		JLabel label = new JLabel(new ImageIcon(new ImageIcon(getClass().getResource("/tp.jpg")).getImage().getScaledInstance(250, 250, Image.SCALE_DEFAULT)), SwingConstants.CENTER);
		frame.getContentPane().add(label, "North");
        frame.getContentPane().add(box, "Center");
        //messageArea.setBounds(0,0,400,400);
        JScrollPane p = new JScrollPane(messageArea);
        p.setPreferredSize(new Dimension(200,200));
        frame.getContentPane().add(p, "South");
        frame.pack();

        // Add Listeners
        textField.addActionListener(new ActionListener() {
            /**
			 * When enter is pressed the message will be sent to the server
			 * The box is then cleared
             */
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }

    //Display a message box to get the server IP
    @SuppressWarnings("unused")
	private String getServerAddress() {
        return JOptionPane.showInputDialog(
            frame,
            "Enter IP Address of the Server:",
            "Welcome to the Chatter",
            JOptionPane.QUESTION_MESSAGE);
    }

    //Get their display name from a message box
    private String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }

    private void run() throws IOException {

    	SimpleAttributeSet keyWord = new SimpleAttributeSet();
    	StyleConstants.setForeground(keyWord, Color.BLUE);
    	StyleConstants.setBold(keyWord, true);
    	
        String serverAddress = "90.210.36.136"; //getServerAddress();
        Socket socket = new Socket(serverAddress, 25410); //Create a new socket to the server
        //Define the input and output streams
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) {
            String line = in.readLine();
            if (line.startsWith("ENTERNAME")) {
                out.println(getName());
                //Send the result of the getName dialogue box
            } else if (line.startsWith("NAMEACCEPTED")) {
                textField.setEditable(true);
                //If the name is accepted then allow them to type a message
            } else if (line.startsWith("INFO")) {
            	//messageArea.append("INFO: " + line.substring(5) + "\n");
            	try {
					doc.insertString(doc.getLength(), "INFO: " + line.substring(5) + "\n", keyWord);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	 playSoundInternal(new File(getClass().getResource("/online.wav").getFile()));
            } else if (line.startsWith("MESSAGE")) {
                //messageArea.append(line.substring(8) + "\n");
            	try {
					doc.insertString(doc.getLength(), line.substring(8) + "\n", null);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                //Toolkit.getDefaultToolkit().beep();
                playSoundInternal(new File(getClass().getResource("/newalert.wav").getFile()));
                //Add new message to the chat box
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient(); //Create a new chat client
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //When JFrame closed, close application
        client.frame.setVisible(true); //Make the JFrame visible
        client.run(); //Run the client
    }

    private void playSoundInternal(File f) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(f);
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                try {
                    clip.start();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    clip.drain();
                } finally {
                    clip.close();
                }
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } finally {
                audioInputStream.close();
            }
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
}
