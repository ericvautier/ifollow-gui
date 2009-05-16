package net.vautier.ifollow.test.client.util;

import com.google.gwt.user.client.Random;

public final class RandomText {

	private static String[] keywords = new String[] { "test-driven", "vautier", "unit-test", "mock" };

	/**
	 * Generates a random word
	 * @return	A random word.
	 */
	public static String randomWord() {
		if ( Random.nextInt( 100 ) > 95 ) {
			return keywords[ Random.nextInt(keywords.length) ];
		} else {
			String s = "";
			for ( int i=0; i<Random.nextInt(14); i++) {
				Character c = new Character( (char) (Random.nextInt(26) + 97) );
				s += c;
			}
			return s;
		}		
	}
	
	/**
	 * Generates a random sentence.
	 * @return	A random sentence of 100 words or less.
	 */
	public static String randomSentence() {
		String s = "";
		for ( int i=0; i<Random.nextInt(100); i++) {
			s += RandomText.randomWord() + " ";
		}
		return s;
	}

	/**
	 * Generates a list of random sentences.
	 * @return A list of sentences.
	 */
	public static String[] randomSentences(int count) {
		String sentences[] = new String[ count ];
		for (int i=0; i<count; i++) {
			sentences[i] = RandomText.randomSentence();
		}
		return sentences;
	}

	
}
