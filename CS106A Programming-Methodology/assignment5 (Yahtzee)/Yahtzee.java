
/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import java.util.Arrays;

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	private RandomGenerator rgen = RandomGenerator.getInstance();

	private static final int numberOfTurns = 13;

	private int[] player1Scores = new int[N_SCORING_CATEGORIES + 4];
	private int[] player2Scores = new int[N_SCORING_CATEGORIES + 4];
	private int[] player3Scores = new int[N_SCORING_CATEGORIES + 4];
	private int[] player4Scores = new int[N_SCORING_CATEGORIES + 4];

	public static void main(String[] args) {
		new Yahtzee().start(args);
	}

	public void run() {
		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players");
		playerNames = new String[nPlayers];

		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}

		display = new YahtzeeDisplay(getGCanvas(), playerNames);

		initializeNArrays(nPlayers);

		for (int i = 0; i < numberOfTurns; i++) {
			for (int j = 0; j < nPlayers; j++) {
				display.printMessage(playerNames[j] + "'s turn");
				playGame(j, returnPlayersScoreArray(j + 1));
				println("player" + (j + 1) + "scores" + Arrays.toString(returnPlayersScoreArray(j + 1)));
			}
		}

		int winerNumber = findWiner(nPlayers);
		display.printMessage("the winer is: " + playerNames[winerNumber - 1]);

	}

	private int findWiner(int n) {
		int[] firstPlayersArr = returnPlayersScoreArray(1);
		int maxInd = 1;

		for (int i = 1; i <= n; i++) {
			int[] arrNext = returnPlayersScoreArray(i);
			if (firstPlayersArr[firstPlayersArr.length - 1] < arrNext[arrNext.length - 1]) {
				maxInd = i;
			}
		}
		return maxInd;
	}

	private int[] returnPlayersScoreArray(int playerN) {
		if (playerN == 1)
			return player1Scores;
		if (playerN == 2)
			return player2Scores;
		if (playerN == 3)
			return player3Scores;
		if (playerN == 4)
			return player4Scores;
		return new int[0];
	}

	private void initializeNArrays(int playerNum) {
		if (playerNum == 1) {
			initializeScoreArray(player1Scores);
		}
		if (playerNum == 2) {
			initializeScoreArray(player1Scores);
			initializeScoreArray(player2Scores);
		}
		if (playerNum == 3) {
			initializeScoreArray(player1Scores);
			initializeScoreArray(player2Scores);
			initializeScoreArray(player3Scores);
		}
		if (playerNum == 4) {
			initializeScoreArray(player1Scores);
			initializeScoreArray(player2Scores);
			initializeScoreArray(player3Scores);
			initializeScoreArray(player4Scores);
		}
	}
	/*
	 * this function writes -1 on every index of start score array so that we know
	 * what category will be valid for a player
	 */

	private void initializeScoreArray(int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = -1;
		}
	}

	/*
	 * this method includes playing one turn for a player
	 */
	private void playGame(int playerNum, int[] playerNScores) {
		int[] dice = generateRolls();
		oneTurn(playerNum + 1, dice, playerNScores);
	}

	private void oneTurn(int playerNum, int[] dice, int[] playerNScores) {

		display.waitForPlayerToClickRoll(playerNum);
		display.displayDice(dice);
		display.waitForPlayerToSelectDice();

		rolls(dice, N_ROLLS, playerNum);
		int category = display.waitForPlayerToSelectCategory();

		while (playerNScores[category - 1] != -1) {
			category = display.waitForPlayerToSelectCategory();
			display.printMessage("this category is already selected, please try another one");
		}

		int score = countScore(dice, category);
		playerNScores[category - 1] = score;

		display.updateScorecard(category, playerNum, score);
		updateScores(playerNum, playerNScores);
	}

	/*
	 * this method does the rest of the rolls if dice is selected we roll it again
	 */
	private void rolls(int[] dice, int n, int playerNum) {

		for (int i = 0; i < n - 1; i++) {
			rollIfSelected(dice);
			display.displayDice(dice);
			if (i < n - 2) {
				display.waitForPlayerToSelectDice();
			}
		}
	}

	/*
	 * just updating every score category
	 */
	private void updateScores(int playerNum, int[] playerN) {
		int upperScore = countUpperSum(playerN);
		int lowerScore = countLowerSum(playerN);
		int bonus = 0;

		if (upperScore > 0) {
			display.updateScorecard(UPPER_SCORE, playerNum, upperScore);
			if (upperScore >= scoreToGetBonus) {
				display.updateScorecard(UPPER_BONUS, playerNum, BONUS);
				bonus = BONUS;
			}
		}
		if (lowerScore > 0) {
			display.updateScorecard(LOWER_SCORE, playerNum, lowerScore);
		}
		display.updateScorecard(TOTAL, playerNum, upperScore + lowerScore + bonus);
	}

	/*
	 * counting upper sum
	 */
	private int countUpperSum(int[] arr) {
		int res = 0;
		for (int i = ONES - 1; i < UPPER_SCORE; i++) {
			if (arr[i] != -1) {
				res = res + arr[i];
			}
		}
		return res;
	}

	/*
	 * counting lower sum
	 */
	private int countLowerSum(int[] arr) {
		int res = 0;
		for (int i = THREE_OF_A_KIND - 1; i < LOWER_SCORE; i++) {
			if (arr[i] != -1) {
				res = res + arr[i];
			}
		}
		return res;
	}

	/*
	 * counts dice sum
	 */
	private int countDiceSum(int[] arr) {
		int res = 0;
		for (int i = 0; i < arr.length; i++) {
			res = res + arr[i];
		}
		return res;
	}

	private int countScore(int[] arr, int category) {
		int result = returnScore(category, arr);
		return result;
	}

	/*
	 * this method returns appropriate points for each category
	 */
	private int returnScore(int category, int[] arr) {
		int res = 0;
		if (category >= ONES && category <= SIXES) {
			res = scoreForNs(arr, category);
		}
		if (category == THREE_OF_A_KIND || category == FOUR_OF_A_KIND) {
			res = scoreForN_OF_A_KIND(category - 6, arr);
		}
		if (category == YAHTZEE) {
			res = scoreForYAHTZEE(arr);
		}
		if (category == SMALL_STRAIGHT) {
			res = scoreFor3Straight(arr);
		}
		if (category == LARGE_STRAIGHT) {
			res = res + scoreFor4Straight(arr);
		}
		if (category == CHANCE) {
			res = countDiceSum(arr);
		}
		if (category == FULL_HOUSE) {
			res = scoreForFullHouse(arr);
		}

		return res;
	}

	/*
	 * we check if any of dices are selected if so we roll again and display next
	 * random values
	 */
	private void rollIfSelected(int[] arr) {
		for (int i = 0; i < N_DICE; i++) {
			if (display.isDieSelected(i)) {
				changeSelectedDice(arr, i);
			}
		}
	}

	private int[] changeSelectedDice(int[] arr, int n) {
		arr[n] = rgen.nextInt(1, 6);
		return arr;
	}

	private int[] generateRolls() {
		int[] arr = new int[N_DICE];

		for (int i = 0; i < N_DICE; i++) {
			arr[i] = rgen.nextInt(1, 6);
		}
		return arr;
	}

	private int scoreForNs(int[] dice, int n) {
		int res = 0;
		for (int i = 0; i < N_DICE; i++) {
			if (dice[i] == n) {
				res = res + n;
			}
		}
		return res;
	}

	private int scoreForN_OF_A_KIND(int n, int[] arr) {
		int[] frequency = countFrequencys(arr);
		int score = 0;
		for (int i = 0; i < frequency.length; i++) {
			if (frequency[i] >= n) {
				score = countDiceSum(arr);
			}
		}
		return score;
	}

	private int scoreForYAHTZEE(int[] arr) {
		int[] frequency = countFrequencys(arr);
		for (int i = 0; i < frequency.length; i++) {
			if (frequency[i] == N_DICE) {
				return YAHTZEES;
			}
		}
		return 0;
	}

	private int scoreFor3Straight(int[] arr) {
		int[] frequency = countFrequencys(arr);
		for (int i = 0; i <= arr.length - 3; i++) {
			if (frequency[i] >= 1 && frequency[i + 1] >= 1 && frequency[i + 2] >= 1 && frequency[i + 3] >= 1) {
				return smallStraight;
			}
		}
		return 0;
	}

	private int scoreFor4Straight(int[] arr) {
		int[] frequency = countFrequencys(arr);

		for (int i = 0; i <= arr.length - 4; i++) {
			if (arr[i] >= 1 && arr[i + 1] >= 1 && arr[i + 2] >= 1 && arr[i + 3] >= 1 && frequency[i + 4] >= 1) {
				return largeStraight;
			}
		}
		return 0;
	}

	private int scoreForFullHouse(int[] arr) {
		int[] fre = countFrequencys(arr);
		boolean same3 = false;
		boolean same2 = false;
		for (int i = 0; i < fre.length; i++) {
			if (fre[i] == 3)
				same3 = true;
			if (fre[i] == 2)
				same2 = true;
		}
		if (same3 && same2) {
			return fullHouse;
		}
		return 0;
	}

	/*
	 * we count the frequency of each num(1 - 6) for example if we have [6,1,1,6,4]
	 * we return an array
	 * 
	 * [2,0,0,1,0,2] this means we have two 1 one 3 and two 6
	 * 
	 */
	private int[] countFrequencys(int[] arr) {
		int[] frequency = new int[diceSidesNum];

		for (int i = 1; i <= diceSidesNum; i++) {
			int num = 0;
			for (int j = 0; j < arr.length; j++) {
				if (i == arr[j]) {
					num++;
				}
			}
			frequency[i - 1] = num;
		}
		return frequency;
	}

	/* Private instance variables */
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;

	private int N_ROLLS = 3;
	private int BONUS = 35;
	private int scoreToGetBonus = 63;
	private int diceSidesNum = 6;
	private int YAHTZEES = 50;
	private int smallStraight = 30;
	private int largeStraight = 40;
	private int fullHouse = 25;

}
