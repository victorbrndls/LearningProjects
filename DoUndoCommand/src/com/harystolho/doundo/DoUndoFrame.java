package com.harystolho.doundo;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

public class DoUndoFrame {

	private static final int WIDTH = 600;
	private static final int HEIGHT = 600;

	private JFrame frame;

	public DoUndoFrame() {
		frame = new JFrame("Do/Undo Project");

		initializeFrame();

		createCanvas();
	}

	private void initializeFrame() {
		frame.setSize(WIDTH, HEIGHT);
		centralizeFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setVisible(true);
	}

	private void createCanvas() {
		Canvas canvas = new DoUndoCanvasImpl();
		canvas.setSize(frame.getWidth(), frame.getHeight());

		CanvasWrapper wrapper = new CanvasWrapper((DoUndoCanvas) canvas);
		wrapper.setSize(frame.getWidth(), frame.getHeight());

		canvas.addMouseListener(wrapper);
		canvas.addKeyListener(wrapper);

		wrapper.add(canvas);
		frame.add(wrapper);
	}

	private void centralizeFrame() {
		frame.setLocation((int) Utils.getScreenWidth() / 2 - (frame.getWidth() / 2),
				(int) Utils.getScreenHeight() / 2 - (frame.getHeight() / 2));
	}

}
