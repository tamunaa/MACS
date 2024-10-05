/*
 * File: HangmanLexicon.java
 * -------------------------
 * This file contains a stub implementation of the HangmanLexicon
 * class that you will reimplement for Part III of the assignment.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import acm.util.*;

public class HangmanLexicon {
	private ArrayList<String> lex = new ArrayList<>();

	public HangmanLexicon(){
		try {
			BufferedReader rd = new BufferedReader(new FileReader("HangmanLexicon.txt"));
			
			while(true) {
				String line = rd.readLine();
				if(line == null)break;
				lex.add(line);
			}
			rd.close();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

/** Returns the number of words in the lexicon. */
	public int getWordCount() {
		return lex.size();
	}

/** Returns the word at the specified index. */
	public String getWord(int index) {
		return lex.get(index);
	}
}
