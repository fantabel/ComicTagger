package com.fantabel.tagger.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TheScreen extends JFrame {

	private static final long serialVersionUID = -6595941892219375486L;

	private JPanel mainPanel;

	Object service;

	private TheScreen() {
		super("Comic Tagger");
		init();

	}

	private void init() {
		this.getContentPane().setLayout(new BorderLayout());

		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainPanel = new JPanel(new BorderLayout());

	}

	public static TheScreen createAndShowGUI() {
		TheScreen frame = new TheScreen();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.addComponentsToPane();

		// Display the window.
		frame.pack();
		return frame;

	}

	private void addComponentsToPane() {
		init();

		service = null;

	}
}
