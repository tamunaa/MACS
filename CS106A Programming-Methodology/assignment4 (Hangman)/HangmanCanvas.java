
/*
 * File: HangmanCanvas.java
 * ------------------------
 * This file keeps track of the Hangman display.
 */

import java.awt.Color;

import acm.graphics.*;

public class HangmanCanvas extends GCanvas {
	GLabel lab;
	GLabel incorrectGuess;
	String res = "";
	

	/** Resets the display so that only the scaffold appears */
	public void reset(int num) {
		if(num == 0) {
			res = "";
		}
		addGibbet();
		hanging(num);
		
	}
	
	
	private void hanging(int n) {
		
		// some impostant coordinates
		double startX = this.getWidth() / 2 - HEAD_RADIUS;
		double startY = (2.0 / 15) * this.getHeight();
		double bodyStartX = this.getWidth() / 2;
		double bodyStartY = startY + 2 * HEAD_RADIUS;
		double upperArmY = bodyStartY + ARM_OFFSET_FROM_HEAD;
		double hipStartX = bodyStartX - HIP_WIDTH / 2;
		double hipEndX = bodyStartX + HIP_WIDTH / 2;
		double bodyEndY = startY + 2 * HEAD_RADIUS + BODY_LENGTH;
		
		switch(n) {
		case 1:
			addHead(startX, startY);
			break;
		case 2: 
			addBody(bodyStartX, bodyStartY);
			break;
		case 3: 
			addRightHand(bodyStartX,upperArmY);
			break;
		case 4:
			addLeftHand(bodyStartX, upperArmY);
			break;
		case 5:
			addHip(bodyStartX, bodyEndY);
			break;
		case 6:
			addLeftLeg(hipEndX, bodyEndY);
			break;
		case 7: 
			addRightLeg(hipStartX, bodyEndY);
			break;
		}	
	}

	/**
	 * Updates the word on the screen to correspond to the current state of the
	 * game. The argument string shows what letters have been guessed so far;
	 * unguessed letters are indicated by hyphens.
	 */
	public void displayWord(String word) {
		// we will update word label every time
		if( lab != null) {
			remove(lab);
		}
		lab = new GLabel(word);
		add(lab, this.getWidth()/2 - lab.getWidth(),(11.0/13)* this.getHeight());
		lab.setFont("-30");
		lab.setColor(Color.red);
	}

	/**
	 * Updates the display to correspond to an incorrect guess by the user. Calling
	 * this method causes the next body part to appear on the scaffold and adds the
	 * letter to the list of incorrect guesses that appears at the bottom of the
	 * window.
	 */
	public void noteIncorrectGuess(char letter) {
		res = res + letter;
		GLabel incorrectGuess = new GLabel(res);
		add(incorrectGuess, 30,this.getHeight() * 18.0/19);
		incorrectGuess.setFont("-20");
	}

	/* Constants for the simple version of the picture (in pixels) */
	private static final int SCAFFOLD_HEIGHT = 290;
	private static final int BEAM_LENGTH = 140;
	private static final int ROPE_LENGTH = 18;

	private static final int HEAD_RADIUS = 32;
	private static final int BODY_LENGTH = 100;

	private static final int ARM_OFFSET_FROM_HEAD = 28;
	private static final int UPPER_ARM_LENGTH = 50;
	private static final int LOWER_ARM_LENGTH = 44;

	private static final int HIP_WIDTH = 36;
	private static final int LEG_LENGTH = 90;
	private static final int FOOT_LENGTH = 20;

	private void addGibbet() {
		double startXH = this.getWidth() / 2 - HEAD_RADIUS;
		double startYH = (2.0 / 15) * this.getHeight();

		// coordinates of scaffold
		double startX = startXH + HEAD_RADIUS - BEAM_LENGTH;
		double startY = startYH - ROPE_LENGTH + SCAFFOLD_HEIGHT;

		addScaffold(startX, startY);

		addBeam(startX, startY);

		addRope(startX, startY);

	}

	private void addScaffold(double a, double b) {
		GLine scaffold = new GLine(a, b, a, b - SCAFFOLD_HEIGHT);
		add(scaffold);
	}

	private void addBeam(double a, double b) {
		GLine beam = new GLine(a, b - SCAFFOLD_HEIGHT, a + BEAM_LENGTH, b - SCAFFOLD_HEIGHT);
		add(beam);
	}

	private void addRope(double a, double b) {
		GLine rope = new GLine(a + BEAM_LENGTH, b - SCAFFOLD_HEIGHT, a + BEAM_LENGTH,
				b - SCAFFOLD_HEIGHT + ROPE_LENGTH);
		add(rope);
	}

	private void addHead(double a, double b) {
		GOval head = new GOval(2 * HEAD_RADIUS, 2 * HEAD_RADIUS);
		add(head, a, b);
	}

	private void addBody(double a, double b) {
		GLine body = new GLine(a, b, a, b + BODY_LENGTH);
		add(body);
	}

	private void addRightHand(double a, double b) {
		GLine upperArm = new GLine(a, b, a - UPPER_ARM_LENGTH, b);
		add(upperArm);

		GLine wrist = new GLine(a - UPPER_ARM_LENGTH, b, a - UPPER_ARM_LENGTH,
				b + LOWER_ARM_LENGTH);
		add(wrist);
	}

	private void addLeftHand(double a, double b) {
		GLine upperArm = new GLine(a, b, a + UPPER_ARM_LENGTH, b);
		add(upperArm);

		GLine wrist = new GLine(a + UPPER_ARM_LENGTH, b, a + UPPER_ARM_LENGTH,
				b + LOWER_ARM_LENGTH);
		add(wrist);
	}

	private void addRightLeg(double a, double b) {
		GLine rightLeg = new GLine(a, b, a, b + LEG_LENGTH);
		add(rightLeg);
		
		GLine rightFoot = new GLine( a, b + LEG_LENGTH, a - FOOT_LENGTH,b + LEG_LENGTH);
		add(rightFoot);
	}
	
	private void addLeftLeg(double a, double b) {	
		GLine leftLeg = new GLine(a, b, a, b + LEG_LENGTH);
		add(leftLeg);
		
		GLine leftFoot = new GLine( a, b + LEG_LENGTH, a + FOOT_LENGTH, b + LEG_LENGTH);
		add(leftFoot);
	}
	
	private void addHip(double a, double b) {	
		GLine hip = new GLine(a - HIP_WIDTH / 2, b, a + HIP_WIDTH / 2, b);
		add(hip);
	}
}
