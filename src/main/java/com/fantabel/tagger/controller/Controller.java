package com.fantabel.tagger.controller;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.fantabel.tagger.exception.TaggerException;

public class Controller {

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
