
/*
 * File: Hangman.java
 * ------------------
 * This program will eventually play the Hangman game from
 * Assignment #4.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.*;

public class Hangman extends ConsoleProgram {
	private RandomGenerator rgen = RandomGenerator.getInstance();

	private int CHANCENUM = 8;
	HangmanCanvas canvas;

	public void init() {
		canvas = new HangmanCanvas();
		add(canvas);
	}

	public void run() {
		playNTimes();

	}

	private void playNTimes() {
		while (true) {
			canvas.reset(0);
			HangmanLexicon lex = new HangmanLexicon();
			int wordIndex = rgen.nextInt(0, lex.getWordCount() - 1);
			String word = lex.getWord(wordIndex).toLowerCase();
			String wordToShowUser = misteryWord(word);
			intro(wordToShowUser);
			playing(word, wordToShowUser);
		}
	}

	private void intro(String str) {
		println("hallo!, hallo!");
		println("Welcome to hangman!");
		println("your word looks like this: " + str);
		println("best of luck");
		println();

	}

	private String misteryWord(String str) {
		String res = "";
		for (int i = 0; i < str.length(); i++) {
			res = res + "-";
		}
		return res;
	}

	private void playing(String str, String blank) {
		int chanceCount = CHANCENUM;

		while (!blank.equals(str) && chanceCount > 0) {
			String ourText = "your guess: ";
			String userInput = readChar(ourText).toLowerCase();
			char usersChar = userInput.charAt(0);

			if (containsUsersChar(str, usersChar)) {
				blank = addNewChar(blank, usersChar, str);
			} else {
				println("there are no " + usersChar + "'s in this word");
				canvas.noteIncorrectGuess(usersChar);
				chanceCount--;
			}
			println("your result looks like this: " + addNewChar(blank, usersChar, str));
			canvas.displayWord(addNewChar(blank, usersChar, str));
			println("you have " + chanceCount + " chances left");
			println();
			canvas.reset(CHANCENUM - chanceCount);
		}

		gameResult(blank, str);
		canvas.removeAll();
		canvas.reset(0);
	}

	private void gameResult(String a, String b) {
		if (a.equals(b)) {
			println("you did iiiit ^_^ ");
		} else {
			println("you lost, try agin ;{ ");
		}

		println();

	}

	private String addNewChar(String str, char user, String original) {
		String newString = str;
		for (int i = 0; i < original.length(); i++) {
			if (original.charAt(i) == user) {
				newString = newString.substring(0, i) + user + newString.substring(i + 1);
			}
		}
		return newString;
	}

	private boolean containsUsersChar(String str, char user) {
		for (int i = 0; i < str.length(); i++) {
			if (user == str.charAt(i)) {
				return true;
			}
		}
		return false;
	}

	private String readChar(String str) {
		while (true) {
			String line = readLine(str);
			if (line.length() == 1) {
				return line;
			} else {
				println("please enter character ");
			}
			println();
		}
	}
}
