
/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;

	/* Method: run() */
	/** Runs the Breakout program. */

	public void init() {

		addMouseListeners();

	}

	private RandomGenerator rgen = RandomGenerator.getInstance();

	private double VX;
	private double VY;

	private int count = 0;
	private GRect paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);

	private GOval Ball = new GOval(2 * BALL_RADIUS, 2 * BALL_RADIUS);

	private GObject targetBrick;

	private double paddleX;
	private double paddleY;

	public void run() {

		initializeGame();
		playNTimes();

		returnResult();

	}

	private void removeEverything() {
		removeAll();
	}

	boolean alive = true;

	private void playNTimes() {
		play();

		for (int i = 0; i < NTURNS - 1; i++) {
			if (!alive) {
				// we will come here after we die

				alive = true; // then we resurrect and continue playing
				add(Ball, getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS);
				play();
			}
		}

	}

	private void returnResult() {
		removeEverything();

		if (count == NBRICKS_PER_ROW * NBRICK_ROWS) {

			gameResult("you win the game", Color.GREEN);

		} else {

			gameResult("you lost the game", Color.RED);

		}
	}

	private void gameResult(String a, Color b) {
		GLabel win = new GLabel(a);
		win.setFont("futura-30");
		win.setColor(b);
		add(win, getWidth() / 2 - win.getWidth() / 2, getHeight() / 2);

	}

	public void mouseMoved(MouseEvent e) {
		// for edge cases
		if (e.getX() < PADDLE_WIDTH) {
			paddle.setLocation(0, paddleY);
		} else if (e.getX() + PADDLE_WIDTH / 2 > getWidth()) {
			paddle.setLocation(getWidth() - PADDLE_WIDTH, paddleY);
		}

		// for the rest territory
		else {
			paddle.setLocation(e.getX() - PADDLE_WIDTH / 2, paddleY);
		}

	}

	private void moveBall() {

		Ball.move(VX, VY);

	}

	private void play() {

		waitForClick();

		while (alive && count != NBRICKS_PER_ROW * NBRICK_ROWS) {

			moveBall();
			checkForCollisions(Ball);
			checkTheHit();
			pause(9);

		}
	}

	private void checkForCollisions(GOval b) {

		if (Ball.getY() > getHeight()) {
			alive = false;
		}

		// Calculating important coordinates of the ball

		double upperLeftX = Ball.getX();
		double upperLeftY = Ball.getY();

		double bottomRightX = upperLeftX + 2 * BALL_RADIUS;
		double bottomRightY = upperLeftY + 2 * BALL_RADIUS;

		if (upperLeftX <= 0 && upperLeftY >= 0) {
			VX = -VX;
		}
		if (bottomRightX >= getWidth() && upperLeftY >= 0) {
			VX = -VX;
		}
		if (upperLeftX >= 0 && upperLeftY <= 0) {
			VY = -VY;
		}

		if (Ball.getX() >= paddle.getX() - 2 * BALL_RADIUS && Ball.getX() <= paddle.getX() + PADDLE_WIDTH + BALL_RADIUS
				&& paddle.getY() <= Ball.getY() + 2 * BALL_RADIUS && paddle.getY() + 3 >= bottomRightY && VY > 0) {

			VY = -VY;
		}

	}

	private void checkTheHit() {
		// calculating coordinates where we probably remove brick after each hit

		double ballUpSideX = Ball.getX() + BALL_RADIUS;
		double ballUpSideY = Ball.getY() - 1;

		double ballDownSideX = Ball.getX() + BALL_RADIUS;
		double ballDownSideY = Ball.getY() + 2 * BALL_RADIUS + 1;

		double ballRightSideX = Ball.getX() + 2 * BALL_RADIUS + 1;
		double ballRightSideY = Ball.getY() + BALL_RADIUS;

		double ballLeftSideX = Ball.getX() - 1;
		double ballLeftSideY = Ball.getY() + BALL_RADIUS;

		upOrDownHit(ballUpSideX, ballUpSideY);

		leftOrRightHit(ballRightSideX, ballRightSideY);

		leftOrRightHit(ballLeftSideX, ballLeftSideY);

		upOrDownHit(ballDownSideX, ballDownSideY);

	}

	// we have two types of hit to determine how ball changes direction

	private void upOrDownHit(double X, double Y) {
		// in this case we change only ball VY direction

		targetBrick = getElementAt(X, Y);

		if (targetBrick != null && targetBrick != paddle) {
			remove(targetBrick);
			count++;
			VY = -VY;

		}

	}

	private void leftOrRightHit(double X, double Y) {
		// here we change VX direction

		targetBrick = getElementAt(X, Y);

		if (targetBrick != null && targetBrick != paddle) {

			remove(targetBrick);
			count++;
			if (VX == 0 && (getElementAt(X + BRICK_SEP / 2, Y) == null || getElementAt(X - BRICK_SEP / 2, Y) != null)) {
				VY = -VY;

			}

			VX = -VX;

		}

	}

	private void initializeGame() {
		initializeSpeed();
		addBricks();
		addPaddle();
		addBall();

	}

	private void addBall() {
		add(Ball, getWidth() / 2 - BALL_RADIUS, getHeight() / 2 - BALL_RADIUS);

		Ball.setFilled(true);
		Ball.setFillColor(Color.black);
		Ball.setColor(Color.black);
	}

	private void initializeSpeed() {
		VX = rgen.nextDouble(1.0, 3.0);

		VY = 3;
		if (rgen.nextBoolean()) {
			VX = -VX;
		}
	}

	public void addPaddle() {
		paddleX = (getWidth() - PADDLE_WIDTH) / 2;
		paddleY = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add(paddle, paddleX, paddleY);

		paddle.setFilled(true);
		paddle.setFillColor(Color.black);
		paddle.setColor(Color.black);
	}

	private Color paintBricks(int n) {
		if (n < 2) {
			return Color.RED;
		} else if (n < 4) {
			return Color.ORANGE;
		} else if (n < 6) {
			return Color.YELLOW;
		} else if (n < 8) {
			return Color.GREEN;
		} else if (n < 10) {
			return Color.CYAN;
		}
		return Color.MAGENTA;

	}

	private void addBricks() {

		double startX = (WIDTH - (NBRICKS_PER_ROW * BRICK_WIDTH + BRICK_SEP * (NBRICKS_PER_ROW - 1))) / 2.0;

		for (int i = 0; i < NBRICK_ROWS; i++) {

			int startYOfCurrentBrick = BRICK_Y_OFFSET + i * (BRICK_HEIGHT + BRICK_SEP);

			for (int j = 0; j < NBRICKS_PER_ROW; j++) {

				int startXOfCurrentBrick = (int) (startX + j * (BRICK_WIDTH + BRICK_SEP));

				GRect brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);

				add(brick, startXOfCurrentBrick, startYOfCurrentBrick);
				brick.setFilled(true);
				brick.setFillColor(paintBricks(i));
				brick.setColor(paintBricks(i));

			}

		}
	}
}