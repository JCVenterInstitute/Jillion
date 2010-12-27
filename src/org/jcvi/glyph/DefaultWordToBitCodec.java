package org.jcvi.glyph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/*
 * @author naxelrod
 */
 
public class DefaultWordToBitCodec implements WordCodec {
	
	private HashSet<String> words = new HashSet<String>();
	
	private static final Map<byte[], String> BYTE_TO_WORD_MAP = new HashMap<byte[], String>();
    private static final Map<String, byte[]> WORD_TO_BYTE_MAP = new HashMap<String, byte[]>();
	
	public DefaultWordToBitCodec(HashSet<String> words) {
		super();
		this.words = words;
		int length = words.size();
		
		// Determine the length of hex representation
		int hexLength = Integer.toString(length, 16).length();
		
		int i = 0;
		for (String word : words) {
			// Convert integer to hex string
			String hex = Integer.toString(i, 16);
			
			// pad left based on binLength - binaryStr.length()
			int padAmount = hexLength - hex.length();
			String padHexStr = String.format("%0" + padAmount + "s", hex);
			
			// Convert binary string to a bit array
			
		}
	}

	public static final byte[] intToByteArray(int value) {
		return new byte[] {
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value};
	}

	@Override
	public List<String> decode(byte[][] encodedWords) {
		List<String> result = new ArrayList<String>(encodedWords.length);
		for (byte[] b : encodedWords) {
			result.add(BYTE_TO_WORD_MAP.get(b));
		}
		return result;
	}

	@Override
	public String decode(byte[][] encodedWords, int index) {
		return BYTE_TO_WORD_MAP.get(encodedWords[index]);
	}


	@Override
	public byte[][] encode(Collection<String> word) {
		int len = word.size();
		byte[][] result = new byte[len][words.size()];
		int i = 0;
		for (String w : word) {
			result[i++] = WORD_TO_BYTE_MAP.get(w);
		}
		return result;
	}}
