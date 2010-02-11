/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sproj;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import jBittorrentAPI.ExampleDownloadFiles;
/**
 *
 * @author Saleem
 */
public class DownloadTorrent extends WindowAdapter implements ActionListener {

    public static boolean isShown = false;
    private final Main main;
    private JFrame frame = null;
    private JTextField path = null;
    private JTextField loc = null;
    private JButton okBtn = null;
    private JButton browseBtn = null;
    public DownloadTorrent(Main m) {
        isShown = true;
        main = m;
        path = new JTextField("[URL]");
        loc = new JTextField("Location");
        browseBtn = new JButton("Browse");
        browseBtn.addActionListener(this);
        okBtn = new JButton("OK");
        okBtn.addActionListener(this);
        frame = new JFrame("Enter URL of the File");
        Container c = frame.getContentPane();
        GridBagLayout gridBag = new GridBagLayout();
        c.setLayout(gridBag);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 0.9;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        c.add(path, gbc);
        gbc.weightx = 0.9;
        gbc.gridy = 1;
        c.add(loc, gbc);
        
        gbc.weightx = 0.3;
        gbc.gridx = 1;
        gbc.gridy = 0;
        
        
        c.add(browseBtn, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        c.add(okBtn, gbc);
        
        
        
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.NONE;
        c.add(okBtn, gbc);

        frame.setSize(500, 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void loadFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Torrent Files", "torrent");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            path.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    public void setExit() {
    	if(edf != null) {
    		edf.setExit();
    	}
    }

    public void process(){
        isShown = false;
        frame.dispose();
        String url = path.getText();
        if(!(url.length()>0)){
            return;
        }
        else {
        	final String arg[] = new String[2];
        	arg[0] = url;
        	arg[1] = loc.getText();
        	
        	new Thread(new Runnable() {
				
				@Override
				public void run() {
					edf.DownloadFiles(arg);
					exit = true;
					
				}
			}).start();
        	
        	new Thread(new Runnable() {
				
				@Override
				public void run() {
					//System.out.println("run started");
					while(complete < 100 && !exit) {
						if(edf != null) {
							complete = edf.getCompleted();
							
							//System.out.println("complete == " + complete);
						}
						else {
							//System.out.println("edf == null");
						}
						
					}
					System.out.println("returning");
					
					
				}
			}).start();
        	
        }
        
        main.addDownload();
    }
    
    boolean exit = false;
    ExampleDownloadFiles edf = new ExampleDownloadFiles();
    float complete = 0;
    
    public float getCompleted() {
    	return complete;
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == okBtn) {
            process();
        }
        else if(ae.getSource() == browseBtn) {
        	this.loadFile();
        }
    }

    @Override
    public void windowClosing(WindowEvent we) {
        isShown = false;
    }
}
