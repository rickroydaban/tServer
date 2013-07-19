/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserFunction;

/**
 * @author Christopher Deckers
 */
public class SimpleWebBrowserExample {
  static ActionListener actionListener;
  static JButton buttonOn, buttonOff;
  static JPanel powerPanel;
  static JPanel controlPanel;
  static JPanel webBrowserPanel;
  static JTextField statusField, taxiCountField, clientCountField;
  static double screenWidth, screenHeight;
  static Server server;
  public static JComponent createContent() {
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	screenWidth	 = screenSize.getWidth()-30;
	screenHeight = screenSize.getHeight()-140;
	
	actionListener = getButtonListener();
	
    JPanel contentPane = new JPanel(new BorderLayout());
    webBrowserPanel = new JPanel(new BorderLayout());
    webBrowserPanel.setBorder(BorderFactory.createTitledBorder("Server"));
    final JWebBrowser webBrowser = new JWebBrowser();
    webBrowser.navigate("http://localhost/thesis/multiplemarkers.html");
    webBrowser.setBarsVisible(false);
    
    webBrowser.registerFunction(new WebBrowserFunction("invokeJava") {
        @Override
        public Object invoke(JWebBrowser webBrowser, Object... args) {
          return new Object[] {screenWidth,screenHeight};
        }
      });
    
    webBrowserPanel.add(webBrowser, BorderLayout.CENTER);
    webBrowserPanel.setVisible(false);
    contentPane.add(webBrowserPanel, BorderLayout.CENTER);
    // Create an additional bar allowing to show/hide the menu bar of the web browser.
    
    buttonOn = new JButton(" Turn On");
    buttonOff = new JButton("Turn Off");
	buttonOff.setVisible(false);
	buttonOn.setVisible(true);
    
    buttonOn.addActionListener(actionListener);
    buttonOff.addActionListener(actionListener);
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
    powerPanel = new JPanel(new BorderLayout());
    powerPanel.setPreferredSize(new Dimension(95,30));
    powerPanel.add(buttonOn,BorderLayout.WEST);
    powerPanel.add(buttonOff,BorderLayout.EAST);
    powerPanel.setBackground(Color.WHITE);
    powerPanel.setBorder(BorderFactory.createLoweredBevelBorder());
  
    //panel for status
    JLabel statusLabel = new JLabel("Status: ");
    statusLabel.setPreferredSize(new Dimension(70,30));
    statusLabel.setHorizontalAlignment(JLabel.RIGHT);
    statusLabel.setFont(new Font("Calibri",Font.BOLD,15));
    statusField=new JTextField();
    statusField.setEditable(false);
    statusField.setPreferredSize(new Dimension(500,30));
    statusField.setText("Please switch on to start server...");
    statusField.setBorder(BorderFactory.createCompoundBorder(
    		              statusField.getBorder(), 
                          BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    statusField.setFont(new Font("Calibri",Font.PLAIN,15));
    statusField.setBackground(Color.WHITE);
    
    //panel for number of taxi on operation
    JLabel taxiCountLabel = new JLabel("Units: ");
    taxiCountLabel.setPreferredSize(new Dimension(50,30));
    taxiCountLabel.setHorizontalAlignment(JLabel.RIGHT);
    taxiCountLabel.setFont(new Font("Calibri",Font.BOLD,15));
    taxiCountField = new JTextField("0");
    taxiCountField.setEditable(false);
    taxiCountField.setPreferredSize(new Dimension(50,30));
    taxiCountField.setHorizontalAlignment(JTextField.CENTER);
    taxiCountField.setFont(new Font("Calibri",Font.PLAIN,14));
    taxiCountField.setBackground(Color.WHITE);
    
    //panel for number of requesting passengers
    JLabel clientCountLabel = new JLabel("Clients: ");
    clientCountLabel.setPreferredSize(new Dimension(60,30));
    clientCountLabel.setHorizontalAlignment(JLabel.RIGHT);
    clientCountLabel.setFont(new Font("Calibri",Font.BOLD,15));
    clientCountField = new JTextField("0");
    clientCountField.setEditable(false);
    clientCountField.setPreferredSize(new Dimension(50,30));
    clientCountField.setHorizontalAlignment(JTextField.CENTER);
    clientCountField.setFont(new Font("Calibri",Font.PLAIN,14));
    clientCountField.setBackground(Color.WHITE);

    JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
    JButton zoomInButton = new JButton("Zoom In");
    JButton zoomOutButton = new JButton("Zoom Out");
    zoomPanel.setBackground(Color.WHITE);
    zoomPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#BBBBBB")));
    
    zoomPanel.add(zoomInButton);
    zoomPanel.add(zoomOutButton);
    

    buttonPanel.add(powerPanel);
    buttonPanel.add(statusLabel);
    buttonPanel.add(statusField);
    buttonPanel.add(taxiCountLabel);
    buttonPanel.add(taxiCountField);
    buttonPanel.add(clientCountLabel);
    buttonPanel.add(clientCountField);

    JPanel controlPanel = new JPanel(new BorderLayout());
    controlPanel.add(buttonPanel,BorderLayout.CENTER);
    controlPanel.add(zoomPanel,BorderLayout.EAST);
    contentPane.add(controlPanel, BorderLayout.SOUTH);
    
    
    
    return contentPane;
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
	//get the user screen resolution
    NativeInterface.open();
    UIUtils.setPreferredLookAndFeel();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("DJ Native Swing Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(createContent(), BorderLayout.CENTER);
        frame.setSize((int)GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth(), 
        		      (int)GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight());
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
 		frame.setResizable(false);
      }
    });
    NativeInterface.runEventPump();
  }
  
  private static ActionListener getButtonListener(){
	  ActionListener listener;
	  
	  listener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == buttonOn){
			  buttonOff.setVisible(true);
			  buttonOn.setVisible(false);
			  buttonOn.setBackground(Color.WHITE);
			  buttonOff.setBackground(Color.decode("#22AA22"));
			  powerPanel.setBackground(Color.decode("#22AA22"));
			  webBrowserPanel.setVisible(true);
			  
			  server = new Server();
			  new Thread(server).start();
			  
			}else{
			  buttonOff.setVisible(false);
			  buttonOn.setVisible(true);
			  buttonOn.setBackground(Color.WHITE);
			  buttonOff.setBackground(Color.GREEN);
			  powerPanel.setBackground(Color.WHITE);
			  webBrowserPanel.setVisible(false);
			}
		}
	};
	  
	  return listener;
  }
}
