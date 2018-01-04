package com.fantabel.tagger.controller;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;

import com.fantabel.tagger.App;
import com.fantabel.tagger.model.exception.TaggerException;
import com.fantabel.tagger.view.TheScreen;

public class Controller {

	private TheScreen mainFrame;
	final static Logger logger = Logger.getLogger(Controller.class);

	public static void main(String[] args) {
		logger.debug("Start of program");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				logger.debug("End of program");
			}
		});
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) { // If Nimbus is not available, you can set the
			// GUI to another look and feel. }

		}

		// Schedule a job for event dispatch thread:
		// creating and showing this application's GUI.

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Controller c = new Controller();
				// c.initScreen();
				App.go(args);
				System.exit(0);
			}
		});
	}

	private void initScreen() {
		mainFrame = TheScreen.createAndShowGUI();

		mainFrame.setVisible(true);

	}

	public static boolean showDeleteDialog(String comicName, Image img) {
		Image i = img.getScaledInstance(-1, img.getHeight(null) < 400 ? img.getHeight(null) : 400, Image.SCALE_SMOOTH);
		int n = JOptionPane.showConfirmDialog(null, "Do you wish to keep this image in the archive", comicName,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(i));
		return n == JOptionPane.YES_OPTION;
	}

	public static boolean showCoverDialog(String comicName, Image img) throws TaggerException {
		Image i = img.getScaledInstance(-1, 400, Image.SCALE_SMOOTH);
		int n = JOptionPane.showConfirmDialog(null, "Is this a Cover?", comicName, JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, new ImageIcon(i));
		if (n == JOptionPane.CANCEL_OPTION)
			throw new TaggerException("This should not be in comic.");
		return n == JOptionPane.YES_OPTION;
	}

}
