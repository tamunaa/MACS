/*
 * File: NameSurfer.java
 * ---------------------
 * When it is finished, this program will implements the viewer for
 * the baby-name database described in the assignment handout.
 */

import acm.graphics.GRect;
import acm.program.*;
import java.awt.event.*;
import javax.swing.*;

public class NameSurfer extends Program implements NameSurferConstants {
	private JTextField name;
	private JButton graph;
	private JButton clear;
	
	private NameSurferGraph gp;
	
	private NameSurferDataBase nsuf;

/* Method: init() */
/**
 * This method has the responsibility for reading in the data base
 * and initializing the interactors at the bottom of the window.
 */
	public void init() {
		name = new JTextField(10);
		JLabel tx = new JLabel("name");
		graph = new JButton("GRAPH");
		clear = new JButton("CLEAR");
		
		
		add(tx, SOUTH);	
		add(name, SOUTH);
		name.addActionListener(this);
		add(graph, SOUTH);
		add(clear, SOUTH);
	
		addActionListeners();	
		nsuf = new NameSurferDataBase(NAMES_DATA_FILE);
		gp = new NameSurferGraph();
		add(gp);
	}
	
	
/* Method: actionPerformed(e) */
/**
 * This class is responsible for detecting when the buttons are
 * clicked, so you will have to define a method to respond to
 * button actions.
 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == name || e.getSource() == graph) {
			String userInput = name.getText();
			NameSurferEntry en = nsuf.findEntry(userInput);
			if(en != null) {
			    gp.addEntry(en);
			    gp.update();
			}
			name.setText("");
			
		}else if(e.getSource() == clear) {
			gp.clear();
			gp.update();
		}
	}
}
