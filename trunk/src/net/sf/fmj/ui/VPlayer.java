package net.sf.fmj.ui;

import java.awt.BorderLayout;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSlider;


public class VPlayer {

	public static void main(String[] args) {
		FmjStudio f = new FmjStudio();
		String[] str = new String[] { "file:///C:/Users/Hammad/Documents/video.avi" };
		try {
			f.run(str, new RandomAccessFile("C:/Users/Hammad/Documents/video.avi", "r"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
}
