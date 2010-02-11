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

import jBittorrentAPI.ExampleCreateTorrent;

/**
 *
 * @author Saleem
 */
public class CreateTorrent extends WindowAdapter implements ActionListener {

    public static boolean isShown = false;
    private final Main main;
    private JFrame frame = null;
    private JTextField path = null;
    private JTextField loc = null;
    private JTextField psize = null;
    private JTextField name = null;
    private JTextField des = null;
    private JTextField announce = null;
    
    private JButton browseBtn = null;
    private JButton okBtn = null;


    public CreateTorrent(Main m) {
        isShown = true;
        main = m;
        path = new JTextField("save path");
        loc = new JTextField("location of .torrent file");
        psize = new JTextField("piece size");
        name = new JTextField("creator's name");
        des = new JTextField("description");
        
        browseBtn = new JButton("Browse");
        browseBtn.addActionListener(this);
        announce = new JTextField("url of tracker");
        okBtn = new JButton("OK");
        okBtn.addActionListener(this);
        frame = new JFrame("Browse for the torrent File");
        Container c = frame.getContentPane();
        GridBagLayout gridBag = new GridBagLayout();
        c.setLayout(gridBag);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 0.9;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        c.add(path, gbc);
        
        gbc.gridy = 1;
        c.add(loc, gbc);
        
        gbc.gridy = 2;
        c.add(announce, gbc);
        
        gbc.gridy = 3;
        c.add(psize, gbc);
        
        gbc.gridy = 4;
        c.add(name, gbc);
        
        gbc.gridy = 5;
        c.add(des, gbc);
        
        gbc.gridy = 0;     
        gbc.weightx = 0.1;
        c.add(browseBtn, gbc);
        gbc.fill = GridBagConstraints.NONE;
        c.add(browseBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        c.add(okBtn, gbc);
        frame.setSize(500, 250);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void loadFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Torrent Files", ".torrent");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            path.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void processFile() {
        isShown = false;
        frame.dispose();
        
        String arg[] = new String[8];
        arg[3] = path.getText();
        arg[0] = loc.getText();
        arg[2] = psize.getText();
        arg[1] = announce.getText();
        arg[4] = "..";
        arg[5] = name.getText();
        arg[6] = "..";
        arg[7] = des.getText();
        
        ExampleCreateTorrent.main(arg);
        	
        
        main.addDownload();
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == browseBtn) {
            loadFile();
        } else if (ae.getSource() == okBtn) {
            //TODO process the torrent file
            processFile();
        }
    }

    @Override
    public void windowClosing(WindowEvent we) {
        isShown = false;
    }
}
