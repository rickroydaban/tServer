/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.demo.examples.webbrowser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

/**
 * @author Christopher Deckers
 */
public class SimpleWebBrowserExample {
  static ActionListener actionListener;
  static JButton buttonOn, buttonOff;
  static JPanel powerPanel;
  static JPanel controlPanel;
  static JPanel webBrowserPanel; 
  
  public static JComponent createContent() {
	actionListener = getButtonListener();
	
    JPanel contentPane = new JPanel(new BorderLayout());
    webBrowserPanel = new JPanel(new BorderLayout());
    webBrowserPanel.setBorder(BorderFactory.createTitledBorder("Server"));
    final JWebBrowser webBrowser = new JWebBrowser();
    webBrowser.navigate("http://www.google.com");
    webBrowser.setBarsVisible(false);
   
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
//	  powerPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#000")));
  
    powerPanel.setBorder(BorderFactory.createLoweredBevelBorder());
    buttonPanel.add(powerPanel);
    contentPane.add(buttonPanel, BorderLayout.SOUTH);
    
    
    
    return contentPane;
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    NativeInterface.open();
    UIUtils.setPreferredLookAndFeel();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("DJ Native Swing Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(createContent(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
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
