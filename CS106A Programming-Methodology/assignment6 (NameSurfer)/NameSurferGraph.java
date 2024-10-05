
/*
 * File: NameSurferGraph.java
 * ---------------------------
 * This class represents the canvas on which the graph of
 * names is drawn. This class is responsible for updating
 * (redrawing) the graphs whenever the list of entries changes or the window is resized.
 */

import acm.graphics.*;
import acm.util.RandomGenerator;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class NameSurferGraph extends GCanvas implements NameSurferConstants, ComponentListener {
	private RandomGenerator rgen = RandomGenerator.getInstance();

	/**
	 * Creates a new NameSurferGraph object that displays the data.
	 */

	private ArrayList<NameSurferEntry> list = new ArrayList<>();
	private HashMap<NameSurferEntry, Color> map;

	public NameSurferGraph() {
		addComponentListener(this);
		map = new HashMap<>();
	}

	/**
	 * Clears the list of name surfer entries stored inside this class.
	 */
	public void clear() {
		list.clear();
	}

	/* Method: addEntry(entry) */
	/**
	 * Adds a new NameSurferEntry to the list of entries on the display. Note that
	 * this method does not actually draw the graph, but simply stores the entry;
	 * the graph is drawn by calling update.
	 */
	public void addEntry(NameSurferEntry entry) {
		list.add(entry);
		// giving graphs random colors
		map.put(entry, rgen.nextColor());
	}

	/**
	 * Updates the display image by deleting all the graphical objects from the
	 * canvas and then reassembling the display according to the list of entries.
	 * Your application must call update after calling either clear or addEntry;
	 * update is also called whenever the size of the canvas changes.
	 */
	public void update() {
		if(list.size() != 0) {
			drawGraphs(list);
		}else{
			removeAll();
		}
		drawConstraction();
	}

	private void drawGraphs(ArrayList<NameSurferEntry> arr) {
		for (int i = 0; i < arr.size(); i++) {
			drawGraph(arr.get(i),map.get(arr.get(i)));
		}
	}

	private void drawGraph(NameSurferEntry suf, Color cl) {
		int sep = getWidth() / (NDECADES);

		for (int i = 1; i <= NDECADES; i++) {
			int lineStartX = (i-1) * sep;
			double lineStartY = (getHeight() - 2 * GRAPH_MARGIN_SIZE) * (double)suf.getRank(i) / MAX_RANK + GRAPH_MARGIN_SIZE;
			//*
			// to put name label on the last column
			// in this loop
			// *//
			if(i < NDECADES){
				int lineEndX = i  * sep;
				double lineEndY =(getHeight() - 2 * GRAPH_MARGIN_SIZE) *(double) suf.getRank(i+1) / MAX_RANK + GRAPH_MARGIN_SIZE;
				GLine line = new GLine(lineStartX, lineStartY, lineEndX, lineEndY);
				line.setColor(cl);
				add(line);
			}
			String text = createString(suf, i);
			addNameLabel(text,lineStartX, lineStartY, map.get(suf));
		}
	}
	/*
	* returning String which will appear on canvas
	* */

	private String createString(NameSurferEntry suf, int ind){
		String text = suf.getName();
		if(suf.getRank(ind) > 0){
			text += " " + suf.getRank(ind);
		}else{
			text += " *";
		}
		return text;
	}

	private void addNameLabel(String text, double x, double y, Color cl){
		GLabel name = new GLabel(text);
		name.setColor(cl);
		add(name, x, y);
	}

	/*
	* drawing main graphic
	* */
	private void drawConstraction() {
		drawVerticalLines();
		drawHorizontalLines();
		addLabels();
	}

	private void drawHorizontalLines() {
		GLine line1 = new GLine(0, GRAPH_MARGIN_SIZE, getWidth(), GRAPH_MARGIN_SIZE);
		GLine line2 = new GLine(0, getHeight() - GRAPH_MARGIN_SIZE, getWidth(), getHeight() - GRAPH_MARGIN_SIZE);
		add(line1);
		add(line2);
	}

	private void drawVerticalLines() {
		int sep = getWidth() / (NDECADES);
		for (int i = 0; i < NDECADES; i++) {
			GLine line = new GLine(i * sep, 0, i * sep, getHeight());
			add(line);
		}
	}

	private void addLabels() {
		int sep = getWidth() / (NDECADES);
		int text = START_DECADE;
		for (int i = 0; i < NDECADES; i++) {
			GLabel lab = new GLabel("" + text);
			add(lab, i * sep, getHeight());
			text += 10;
		}
	}

	/* Implementation of the ComponentListener interface */
	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
		removeAll();
		update();
	}

	public void componentShown(ComponentEvent e) {
	}
}
