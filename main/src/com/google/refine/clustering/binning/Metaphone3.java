/*

Copyright 2010, Lawrence Philips
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the
distribution.
    * Neither the name of Google Inc. nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,           
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY           
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

/*
 * A request from the author: Please comment and sign any changes you make to 
 * the Metaphone 3 reference implementation.
 * <br>
 * Please do NOT reformat this module to Refine's coding standard,
 * but instead keep the original format so that it can be more easily compared
 * to any modified fork of the original.
 */

/**
 * Metaphone 3<br>
 * VERSION 2.1.3
 * 
 * by Lawrence Philips<br>
 * 
 * Metaphone 3 is designed to return an *approximate* phonetic key (and an alternate
 * approximate phonetic key when appropriate) that should be the same for English
 * words, and most names familiar in the United States, that are pronounced *similarly*.
 * The key value is *not* intended to be an *exact* phonetic, or even phonemic,
 * representation of the word. This is because a certain degree of 'fuzziness' has
 * proven to be useful in compensating for variations in pronunciation, as well as
 * misheard pronunciations. For example, although americans are not usually aware of it,
 * the letter 's' is normally pronounced 'z' at the end of words such as "sounds".<br><br>
 * 
 * The 'approximate' aspect of the encoding is implemented according to the following rules:<br><br>
 * 
 * (1) All vowels are encoded to the same value - 'A'. If the parameter encodeVowels
 * is set to false, only *initial* vowels will be encoded at all. If encodeVowels is set
 * to true, 'A' will be encoded at all places in the word that any vowels are normally
 * pronounced. 'W' as well as 'Y' are treated as vowels. Although there are differences in
 * the pronunciation of 'W' and 'Y' in different circumstances that lead to their being
 * classified as vowels under some circumstances and as consonants in others, for the purposes
 * of the 'fuzziness' component of the Soundex and Metaphone family of algorithms they will
 * be always be treated here as vowels.<br><br>
 *
 * (2) Voiced and un-voiced consonant pairs are mapped to the same encoded value. This
 * means that:<br>
 * 'D' and 'T' -> 'T'<br>
 * 'B' and 'P' -> 'P'<br>
 * 'G' and 'K' -> 'K'<br>
 * 'Z' and 'S' -> 'S'<br>
 * 'V' and 'F' -> 'F'<br><br>
 *
 * - In addition to the above voiced/unvoiced rules, 'CH' and 'SH' -> 'X', where 'X'
 * represents the "-SH-" and "-CH-" sounds in Metaphone 3 encoding.<br><br>
 *
 * - Also, the sound that is spelled as "TH" in English is encoded to '0' (zero symbol). (Although
 * Americans are not usually aware of it, "TH" is pronounced in a voiced (e.g. "that") as
 * well as an unvoiced (e.g. "theater") form, which are naturally mapped to the same encoding.)<br><br>
 * 
 * The encodings in this version of Metaphone 3 are according to pronunciations common in the
 * United States. This means that they will be inaccurate for consonant pronunciations that
 * are different in the United Kingdom, for example "tube" -> "CHOOBE" -> XAP rather than american TAP.<br><br>
 *
 * Metaphone 3 was preceded by by Soundex, patented in 1919, and Metaphone and Double Metaphone,
 * developed by Lawrence Philips. All of these algorithms resulted in a significant number of
 * incorrect encodings. Metaphone3 was tested against a database of about 100 thousand English words,
 * names common in the United States, and non-English words found in publications in the United States,
 * with an emphasis on words that are commonly mispronounced, prepared by the Moby Words website,
 * but with the Moby Words 'phonetic' encodings algorithmically mapped to Double Metaphone encodings.
 * Metaphone3 increases the accuracy of encoding of english words, common names, and non-English
 * words found in american publications from the 89% for Double Metaphone, to over 98%.<br><br>
 *
 * DISCLAIMER:
 * Anthropomorphic Software LLC claims only that Metaphone 3 will return correct encodings,
 * within the 'fuzzy' definition of correct as above, for a very high percentage of correctly
 * spelled English and commonly recognized non-English words. Anthropomorphic Software LLC
 * warns the user that a number of words remain incorrectly encoded, that misspellings may not
 * be encoded 'properly', and that people often have differing ideas about the pronunciation
 * of a word. Therefore, Metaphone 3 is not guaranteed to return correct results every time, and
 * so a desired target word may very well be missed. Creators of commercial products should
 * keep in mind that systems like Metaphone 3 produce a 'best guess' result, and should
 * condition the expectations of end users accordingly.<br><br>
 *
 * METAPHONE3 IS PROVIDED "AS IS" WITHOUT
 * WARRANTY OF ANY KIND. LAWRENCE PHILIPS AND ANTHROPOMORPHIC SOFTWARE LLC
 * MAKE NO WARRANTIES, EXPRESS OR IMPLIED, THAT IT IS FREE OF ERROR,
 * OR ARE CONSISTENT WITH ANY PARTICULAR STANDARD OF MERCHANTABILITY, 
 * OR THAT IT WILL MEET YOUR REQUIREMENTS FOR ANY PARTICULAR APPLICATION.
 * LAWRENCE PHILIPS AND ANTHROPOMORPHIC SOFTWARE LLC DISCLAIM ALL LIABILITY
 * FOR DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES RESULTING FROM USE 
 * OF THIS SOFTWARE. 
 *
 * @author Lawrence Philips
 * 
 * Metaphone 3 is designed to return an <i>approximate</i> phonetic key (and an alternate
 * approximate phonetic key when appropriate) that should be the same for English
 * words, and most names familiar in the United States, that are pronounced "similarly".
 * The key value is <i>not</i> intended to be an exact phonetic, or even phonemic,
 * representation of the word. This is because a certain degree of 'fuzziness' has
 * proven to be useful in compensating for variations in pronunciation, as well as
 * misheard pronunciations. For example, although americans are not usually aware of it,
 * the letter 's' is normally pronounced 'z' at the end of words such as "sounds".<br><br>
 * 
 * The 'approximate' aspect of the encoding is implemented according to the following rules:<br><br>
 * 
 * (1) All vowels are encoded to the same value - 'A'. If the parameter encodeVowels
 * is set to false, only *initial* vowels will be encoded at all. If encodeVowels is set
 * to true, 'A' will be encoded at all places in the word that any vowels are normally
 * pronounced. 'W' as well as 'Y' are treated as vowels. Although there are differences in
 * the pronunciation of 'W' and 'Y' in different circumstances that lead to their being
 * classified as vowels under some circumstances and as consonants in others, for the purposes
 * of the 'fuzziness' component of the Soundex and Metaphone family of algorithms they will
 * be always be treated here as vowels.<br><br>
 *
 * (2) Voiced and un-voiced consonant pairs are mapped to the same encoded value. This
 * means that:<br>
 * 'D' and 'T' -> 'T'<br>
 * 'B' and 'P' -> 'P'<br>
 * 'G' and 'K' -> 'K'<br>
 * 'Z' and 'S' -> 'S'<br>
 * 'V' and 'F' -> 'F'<br><br>
 *
 * - In addition to the above voiced/unvoiced rules, 'CH' and 'SH' -> 'X', where 'X'
 * represents the "-SH-" and "-CH-" sounds in Metaphone 3 encoding.<br><br>
 *
 * - Also, the sound that is spelled as "TH" in English is encoded to '0' (zero symbol). (Although
 * americans are not usually aware of it, "TH" is pronounced in a voiced (e.g. "that") as
 * well as an unvoiced (e.g. "theater") form, which are naturally mapped to the same encoding.)<br><br>
 *
 * In the "Exact" encoding, voiced/unvoiced pairs are <i>not</i> mapped to the same encoding, except
 * for the voiced and unvoiced versions of 'TH', sounds such as 'CH' and 'SH', and for 'S' and 'Z',
 * so that the words whose metaph keys match will in fact be closer in pronunciation that with the
 * more approximate setting. Keep in mind that encoding settings for search strings should always
 * be exactly the same as the encoding settings of the stored metaph keys in your database!
 * Because of the considerably increased accuracy of Metaphone3, it is now possible to use this
 * setting and have a very good chance of getting a correct encoding.
 * <br><br>
 * In the Encode Vowels encoding, all non-initial vowels and diphthongs will be encoded to
 * 'A', and there will only be one such vowel encoding character between any two consonants.
 * It turns out that there are some surprising wrinkles to encoding non-initial vowels in
 * practice, pre-eminently in inversions between spelling and pronunciation such as e.g.
 * "wrinkle" => 'RANKAL', where the last two sounds are inverted when spelled.
 * <br><br>
 * The encodings in this version of Metaphone 3 are according to pronunciations common in the
 * United States. This means that they will be inaccurate for consonant pronunciations that
 * are different in the United Kingdom, for example "tube" -> "CHOOBE" -> XAP rather than american TAP.
 * <br><br>
 * 
 */

package com.google.refine.clustering.binning;

public class Metaphone3 {

    /** Length of word sent in to be encoded, as 
	* measured at beginning of encoding. */
	int  mLength;

    /** Length of encoded key string. */
    int mMetaphLength;

    /** Flag whether or not to encode non-initial vowels. */
    boolean mEncodeVowels;

    /** Flag whether or not to encode consonants as exactly 
	* as possible. */
	boolean mEncodeExact;

	/** Internal copy of word to be encoded, allocated separately
	* from string pointed to in incoming parameter. */
	String mInWord;
    
	/** Running copy of primary key. */
    StringBuffer mPrimary;

    /** Running copy of secondary key. */
    StringBuffer mSecondary;

    /** Index of character in mInWord currently being
	* encoded. */
    int mCurrent;

    /** Index of last character in mInWord. */
	int mLast;

	/** Flag that an AL inversion has already been done. */
	boolean flagAlInversion;
	
	/** Default size of key storage allocation */
	int maxKeyAllocation = 32;
	
	/** Default maximum length of encoded key. */
	int defaultMaxKeyLength = 8;
	
	////////////////////////////////////////////////////////////////////////////////
	// Metaphone3 class definition
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor, default. This constructor is most convenient when
	 * encoding more than one word at a time. New words to encode can
	 * be set using SetWord(char *).
	 *
	 */
	Metaphone3()
	{
	    mPrimary = new StringBuffer();
	    mSecondary = new StringBuffer();

	    mMetaphLength = defaultMaxKeyLength;
	    mEncodeVowels = false;
		mEncodeExact = false;
	}

	/**
	 * Constructor, parameterized. The Metaphone3 object will
	 * be initialized with the incoming string, and can be called
	 * on to encode this string. This constructor is most convenient
	 * when only one word needs to be encoded.
	 * 
	 * @param in pointer to char string of word to be encoded.
	 *
	 */
	Metaphone3(String in)
	{
		this();
		
		SetWord(in);
	}

	/**
	 * Sets word to be encoded.
	 * 
	 * @param in pointer to EXTERNALLY ALLOCATED char string of 
	 * the word to be encoded.
	 *
	 */
	void SetWord(String in)
	{
		mInWord = in.toUpperCase();;
		mLength = mInWord.length();
	}

	/**
	 * Sets length allocated for output keys.
	 * If incoming number is greater than maximum allowable 
	 * length returned by GetMaximumKeyLength(), set key length
	 * to maximum key length and return false;  otherwise, set key 
	 * length to parameter value and return true.
	 * 
	 * @param inKeyLength new length of key.
	 * @return true if able to set key length to requested value.
	 *
	*/
	boolean SetKeyLength(int inKeyLength)
	{
		int key = inKeyLength;
	    if (key < 1)
		{
			// can't have that -
			// no room for terminating null
	    	key = 1;
		}

		if (key > maxKeyAllocation)
	    {
	        mMetaphLength = maxKeyAllocation;
	        return false;
	    }

	     mMetaphLength = key;
	     return true;
	}

    /**
     * Adds an encoding character to the encoded key value string - one parameter version.
     * 
     * @param main primary encoding character to be added to encoded key string.
     */
    void MetaphAdd(String in)
    {
    	if (!(in.equals("A") 
    			&& (mPrimary.length() > 0) 
    			&& (mPrimary.charAt(mPrimary.length() - 1) == 'A')))
    	{
    		mPrimary.append(in);
    	}
    	
    	if (!(in.equals("A") 
    			&& (mSecondary.length() > 0) 
    			&& (mSecondary.charAt(mSecondary.length() - 1) == 'A')))
    	{
    		mSecondary.append(in);
    	}
    }
    
	/**
	 * Adds an encoding character to the encoded key value string - two parameter version
	 * 
	 * @param main primary encoding character to be added to encoded key string
	 * @param alt alternative encoding character to be added to encoded alternative key string
	 *
	 */
	void MetaphAdd(String main, String alt)
	{
    	if (!(main.equals("A") 
    			&& (mPrimary.length() > 0) 
    			&& (mPrimary.charAt(mPrimary.length() - 1) == 'A')))
    	{
    		mPrimary.append(main);
    	}
    	
    	if (!(alt.equals("A") 
    			&& (mSecondary.length() > 0) 
    			&& (mSecondary.charAt(mSecondary.length() - 1) == 'A')))
    	{
			if (!alt.isEmpty())
			{
				mSecondary.append(alt);
			}
    	}
	}

	/**
	 * Adds an encoding character to the encoded key value string - Exact/Approx version
	 * 
	 * @param mainExact primary encoding character to be added to encoded key string if 
	 * mEncodeExact is set
	 *
	 * @param altExact alternative encoding character to be added to encoded alternative 
	 * key string if mEncodeExact is set
	 *
	 * @param main primary encoding character to be added to encoded key string
	 *
	 * @param alt alternative encoding character to be added to encoded alternative key string
	 *
	 */	
	void MetaphAddExactApprox(String mainExact, String altExact, String main, String alt)
	{
		if (mEncodeExact)
		{
			MetaphAdd(mainExact, altExact);
		}
		else
		{
			MetaphAdd(main, alt);
		}
	}

	/**
	 * Adds an encoding character to the encoded key value string - Exact/Approx version
	 * 
	 * @param mainExact primary encoding character to be added to encoded key string if 
	 * mEncodeExact is set
	 *
	 * @param main primary encoding character to be added to encoded key string
	 *
	 */	
	void MetaphAddExactApprox(String mainExact, String main)
	{
		if (mEncodeExact)
		{
			MetaphAdd(mainExact);
		}
		else
		{
			MetaphAdd(main);
		}
	}	 
	/** Retrieves maximum number of characters currently allocated for encoded key. 
	 *
	 * @return short integer representing the length allowed for the key.
	 */
   int GetKeyLength(){ return mMetaphLength; }

	/** Retrieves maximum number of characters allowed for encoded key. 
	 *
	 * @return short integer representing the length of allocated storage for the key.
	 */
   int GetMaximumKeyLength(){ return (int)maxKeyAllocation; }

	/** Sets flag that causes Metaphone3 to encode non-initial vowels. However, even 
	 * if there are more than one vowel sound in a vowel sequence (i.e. 
	 * vowel diphthong, etc.), only one 'A' will be encoded before the next consonant or the
	 * end of the word.
	 *
	 * @param inEncodeVowels Non-initial vowels encoded if true, not if false. 
	 */
   void SetEncodeVowels(boolean inEncodeVowels){ mEncodeVowels = inEncodeVowels; }

	/** Retrieves setting determining whether or not non-initial vowels will be encoded. 
	 *
	 * @return true if the Metaphone3 object has been set to encode non-initial vowels, false if not.
	 */
   boolean GetEncodeVowels(){ return mEncodeVowels; }

	/** Sets flag that causes Metaphone3 to encode consonants as exactly as possible.
	 * This does not include 'S' vs. 'Z', since americans will pronounce 'S' at the
	 * at the end of many words as 'Z', nor does it include "CH" vs. "SH". It does cause
	 * a distinction to be made between 'B' and 'P', 'D' and 'T', 'G' and 'K', and 'V'
	 * and 'F'.
	 *
	 * @param inEncodeExact consonants to be encoded "exactly" if true, not if false. 
	 */
   void SetEncodeExact(boolean inEncodeExact){ mEncodeExact = inEncodeExact; }

	/** Retrieves setting determining whether or not consonants will be encoded "exactly".
	 *
	 * @return true if the Metaphone3 object has been set to encode "exactly", false if not.
	 */
   boolean GetEncodeExact(){ return mEncodeExact; }

	/** Retrieves primary encoded key.
	 *
	 * @return a character pointer to the primary encoded key
	 */
	String GetMetaph()
	{
		String primary = new String(mPrimary);
		return primary;
	}

	/** Retrieves alternate encoded key, if any. 
	 *
	 * @return a character pointer to the alternate encoded key
	 */
	String GetAlternateMetaph()
	{
		String secondary = new String(mSecondary);
		return secondary;
	}

	/**
	 * Test for close front vowels
	 *
	 * @return true if close front vowel
	 */
	boolean Front_Vowel(int at)
	{
		if (((CharAt(at) == 'E') || (CharAt(at) == 'I') || (CharAt(at) == 'Y')))
		{
			return true;
		}

		return false;
	}

	/**
	 * Detect names or words that begin with spellings
	 * typical of german or slavic words, for the purpose
	 * of choosing alternate pronunciations correctly
	 *
	 */
	boolean SlavoGermanic()
	{
		if (StringAt(0, 3, "SCH", "")
			|| StringAt(0, 2, "SW", "")
			|| (CharAt(0) == 'J')
			|| (CharAt(0) == 'W'))
		{
			return true;
		}

		return false;
	}	
	/**
	 * Tests if character is a vowel
	 * 
	 * @param inChar character to be tested in string to be encoded
	 * @return true if character is a vowel, false if not
	 *
	 */
	boolean IsVowel(char inChar)
	{
	    if ((inChar == 'A') 
			|| (inChar == 'E') 
			|| (inChar == 'I') 
			|| (inChar == 'O') 
			|| (inChar == 'U') 
			|| (inChar == 'Y') 
			|| (inChar == 'À')
			|| (inChar == 'Á')
			|| (inChar == 'Â')
			|| (inChar == 'Ã')
			|| (inChar == 'Ä')
			|| (inChar == 'Å')
			|| (inChar == 'Æ')
			|| (inChar == 'È')
			|| (inChar == 'É')
	 		|| (inChar == 'Ê')
			|| (inChar == 'Ë')
			|| (inChar == 'Ì')
			|| (inChar == 'Í')
			|| (inChar == 'Î')
	 		|| (inChar == 'Ï')
			|| (inChar == 'Ò')
			|| (inChar == 'Ó')
			|| (inChar == 'Ô')
			|| (inChar == 'Õ')
			|| (inChar == 'Ö')
			|| (inChar == '')
			|| (inChar == 'Ø')
			|| (inChar == 'Ù')
			|| (inChar == 'Ú')
			|| (inChar == 'Û')
			|| (inChar == 'Ü')
			|| (inChar == 'Ý')
			|| (inChar == ''))
		{
	        return true;
		}

	    return false;
	}

	/**
	 * Tests if character in the input string is a vowel
	 * 
	 * @param at position of character to be tested in string to be encoded
	 * @return true if character is a vowel, false if not
	 *
	 */
	boolean IsVowel(int at)
	{
	    if ((at < 0) || (at >= mLength))
		{
	        return false;
		}

	    char it = CharAt(at);

	    if (IsVowel(it))
		{
	        return true;
		}

	    return false;
	}

	/**
	 * Skips over vowels in a string. Has exceptions for skipping consonants that
	 * will not be encoded.
	 *
	 * @param at position, in string to be encoded, of character to start skipping from
	 *
	 * @return position of next consonant in string to be encoded 
	 */
	int SkipVowels(int at)
	{
		int control = at;
	    if (control < 0)
		{
	        return 0;
		}

	    if (control >= mLength)
		{
	        return mLength;
		}

	    char it = CharAt(control);

	    while (IsVowel(it) || (it == 'W'))
	    {
	        if (StringAt(control, 4, "WICZ", "WITZ", "WIAK", "")
				|| StringAt((control - 1), 5, "EWSKI", "EWSKY", "OWSKI", "OWSKY", "")
				|| (StringAt(control, 5, "WICKI", "WACKI", "") && ((control) == mLast)))
	        {
	            break;
	        }

	        control++;
	        if (((CharAt(control - 1) == 'W') && (CharAt(control) == 'H'))
	            && !(StringAt(control, 3, "HOP", "")
	                  || StringAt(control, 4, "HIDE", "HARD", "HEAD", "HAWK", "HERD", "HOOK", "HAND", "HOLE", "")
	                  || StringAt(control, 5, "HEART", "HOUSE", "HOUND", "")
	                  || StringAt(control, 6, "HAMMER", "")))
			{
	        	control++;
			}
	        
	        if (control > (mLength - 1))
	        {
	        	break;
	        }
	        it = CharAt(control);
	    }

	    return control;
	}

	/**
	 * Advanced counter mCurrent so that it indexes the next character to be encoded
	 *
	 * @param ifNotEncodeVowels number of characters to advance if not encoding internal vowels
	 * @param ifEncodeVowels number of characters to advance if encoding internal vowels
	 *
	 */
	void AdvanceCounter(int ifNotEncodeVowels, int ifEncodeVowels)
	{
		if (!mEncodeVowels)
		{
			mCurrent += ifNotEncodeVowels;
		}
		else
		{
			mCurrent += ifEncodeVowels;
		}
	}
	
	
    /**
     * Subscript safe .charAt()
     * 
	 * @param at index of character to access
	 * @return null if index out of bounds, .charAt() otherwise
	 */
	char CharAt(int at)
	{
	    // check substring bounds
		if ((at < 0)
	    	|| (at > (mLength - 1)))
	    {
	    	return '\0';
	    }

	    return mInWord.charAt(at);
	}
	
	/**
	 * Tests whether the word is the root or a regular english inflection
	 * of it, e.g. "ache", "achy", "aches", "ached", "aching", "achingly"
	 * This is for cases where we want to match only the root and corresponding
	 * inflected forms, and not completely different words which may have the
	 * same substring in them.
	 */
	boolean RootOrInflections(String inWord, String root)
	{
		int len = root.length();
		String rootMod;
		rootMod = root;
		String test;
		test = rootMod + "S";
		if ((inWord.equals(rootMod))
				|| (inWord.equals(test)))
		{
			return true;
		}

		if (rootMod.charAt(len - 1) != 'E')
		{
			test = rootMod + "ES";
		}
		
		if (inWord.equals(test))
		{
			return true;
		}

		if (rootMod.charAt(len - 1) != 'E')
		{
			test = rootMod + "ED";
		}
		else
		{
			test = rootMod + "D";
		}
		
		if (inWord.equals(test))
		{
			return true;
		}

		if (rootMod.charAt(len - 1) == 'E')
		{
			rootMod = rootMod.substring(0, len - 1);
		}

		test = rootMod + "ING";
		if (inWord.equals(test))
		{
			return true;
		}

		test = rootMod + "INGLY";
		if (inWord.equals(test))
		{
			return true;
		}

		test = rootMod + "Y";
		if (inWord.equals(test))
		{
			return true;
		}

		return false;
	}
	
    /**
	 * Determines if one of the substrings sent in is the same as
	 * what is at the specified position in the string being encoded.
	 * 
	 * @param start
	 * @param length
	 * @param compareStrings
	 * @return
	 */
	boolean StringAt(int start, int length, String... compareStrings)
	{
	    // check substring bounds
		if ((start < 0)
	    	|| (start > (mLength - 1))
	    	|| ((start + length - 1) > (mLength - 1)))
	    {
	    	return false;
	    }

	    String target = mInWord.substring(start, (start + length));
	      
		for (String strFragment : compareStrings)
		{
			if (target.equals(strFragment))
			{
				return true;
			}			
		}
	    return false;
	}
	
	/**
	 * Encodes input string to one or two key values according to Metaphone 3 rules.
	 *
	 */
	void Encode()
	{
	    flagAlInversion = false;

	    mCurrent = 0;

		mPrimary.setLength(0);
	    mSecondary.setLength(0);

	    if (mLength < 1)
		{
	        return;
		}

	    //zero based index
		mLast = mLength - 1;

	    ///////////main loop//////////////////////////
		while (!(mPrimary.length() > mMetaphLength) && !(mSecondary.length() > mMetaphLength))
	    {
 	        if (mCurrent >= mLength)
			{
	            break;
			}

 	        switch (CharAt(mCurrent))
	        {	                        
	            case 'B':

					Encode_B();
	                break;
	                        
	            case 'ß':
				case 'Ç':

	                MetaphAdd("S");
	                mCurrent++;
	                break;

	            case 'C':

					Encode_C();
	                break;

	            case 'D':

					Encode_D();
	                break;

	            case 'F':
	                    
					Encode_F();
	                break;

	            case 'G':

					Encode_G();
	                break;

	            case 'H':

					Encode_H();
	                break;
	                
	            case 'J':

					Encode_J();
	                break;

	            case 'K':

					Encode_K();
	                break;

	            case 'L':

					Encode_L();
	                break;

	            case 'M':

					Encode_M();
	                break;

	            case 'N':

					Encode_N();
	                break;

	            case 'Ñ':
	                
	                MetaphAdd("N");
	                mCurrent++;
	                break;

	            case 'P':

					Encode_P();
	                break;

	            case 'Q':

					Encode_Q();
	                break;

	            case 'R':

					Encode_R();
					break;

	            case 'S':

					Encode_S();
	                break;

	            case 'T':

					Encode_T();
	                break;

	            case 'Ð': // eth
				case 'Þ': // thorn

	                MetaphAdd("0");
	                mCurrent++;
	                break;

	           case 'V':

					Encode_V();
	                break;

	            case 'W':

					Encode_W();
	                break;

	            case 'X':

					Encode_X();
	                break;

	            case '':

	                MetaphAdd("X");
	                mCurrent++;
	                break;

				case '':

	                MetaphAdd("S");
	                mCurrent++;
	                break;

	            case 'Z':
					
	                Encode_Z();
	                break;

	            default:
	            	
					if (IsVowel(CharAt(mCurrent)))
					{
						Encode_Vowels();
						break;
					}
	            
	                mCurrent++;
	                
	        }
	    }

	    //only give back mMetaphLength number of chars in m_metaph
		if (mPrimary.length() > mMetaphLength)
	    {
			mPrimary.setLength(mMetaphLength);
	    }

		if (mSecondary.length() > mMetaphLength)
	    {
			mSecondary.setLength(mMetaphLength);
	    }

		// it is possible for the two metaphs to be the same 
		// after truncation. lose the second one if so
		if ((mPrimary.toString()).equals(mSecondary.toString()))
		{
			mSecondary.setLength(0);
		}
	}

	/**
	 * Encodes all initial vowels to A.
	 *
	 * Encodes non-initial vowels to A if mEncodeVowels is true
	 * 
	 * 
	*/
	void Encode_Vowels()
	{
		if (mCurrent == 0)
		{
			// all init vowels map to 'A' 
			// as of Double Metaphone
			MetaphAdd("A"); 
		}
		else if (mEncodeVowels)
		{
			if (CharAt(mCurrent) != 'E')
			{
				if (Skip_Silent_UE())
				{
					return;
				}

                if (O_Silent())
                {
                    mCurrent++;
                    return;
                }

				// encode all vowels and
				// diphthongs to the same value
				MetaphAdd("A"); 
			}
			else
			{
				Encode_E_Pronounced();
			}
		}

		if (!(!IsVowel(mCurrent - 2) && StringAt((mCurrent - 1), 4, "LEWA", "LEWO", "LEWI", "")))
		{
			mCurrent = SkipVowels(mCurrent);
		}
		else
		{
			mCurrent++;
		}
	}
	
	/**
	 * Encodes cases where non-initial 'e' is pronounced, taking
	 * care to detect unusual cases from the greek.
	 *
	 * Only executed if non initial vowel encoding is turned on
	 * 
	 * 
	 */
	void Encode_E_Pronounced()
	{
		// special cases with two pronunciations
		// 'agape' 'lame' 'resume'
		if ((StringAt(0, 4, "LAME", "SAKE", "PATE", "") && (mLength == 4))
			|| (StringAt(0, 5, "AGAPE", "") && (mLength == 5))
			|| ((mCurrent == 5) && StringAt(0, 6, "RESUME", "")))
		{
			MetaphAdd("", "A");
			return;
		}
		
		// special case "inge" => 'INGA', 'INJ'
		if (StringAt(0, 4, "INGE", "")
			&& (mLength == 4))
		{
			MetaphAdd("A", "");
			return;
		}
		
		// special cases with two pronunciations
		// special handling due to the difference in
		// the pronunciation of the '-D'
		if ((mCurrent == 5) && StringAt(0, 7, "BLESSED", "LEARNED", ""))
		{
			MetaphAddExactApprox("D", "AD", "T", "AT");
			mCurrent += 2;
			return;
		}
		
		// encode all vowels and diphthongs to the same value
		if ((!E_Silent() 
				&& !flagAlInversion 
				&& !Silent_Internal_E())
				|| E_Pronounced_Exceptions())
		{
			MetaphAdd("A"); 
		}
		
		// now that we've visited the vowel in question
		flagAlInversion = false;
	}

    /**
     * Tests for cases where non-initial 'o' is not pronounced
     * Only executed if non initial vowel encoding is turned on
     * 
     * @return true if encoded as silent - no addition to m_metaph key
     *
    */
    boolean O_Silent()
    {
        // if "iron" at beginning or end of word and not "irony"
        if ((CharAt(mCurrent) == 'O') 
            && StringAt((mCurrent - 2), 4, "IRON", ""))
        {
            if ((StringAt(0, 4, "IRON", "")
                || (StringAt((mCurrent - 2), 4, "IRON", "")
                    && (mLast == (mCurrent + 1))))
                && !StringAt((mCurrent - 2), 6, "IRONIC", ""))
            {
                return true;
            }
        }

        return false;
    }
    
	/**
	 * Tests and encodes cases where non-initial 'e' is never pronounced
	 * Only executed if non initial vowel encoding is turned on
	 * 
	 * @return true if encoded as silent - no addition to m_metaph key
	 *
	*/
	boolean E_Silent()
	{	
		if (E_Pronounced_At_End())
		{
			return false;
		}

		// 'e' silent when last letter, altho
		if ((mCurrent == mLast)
			// also silent if before plural 's'
			// or past tense or participle 'd', e.g.
			// 'grapes' and 'banished' => PNXT
			|| ((StringAt(mLast, 1, "S", "D", "")
				&& (mCurrent > 1)
				&& ((mCurrent + 1) == mLast)
					// and not e.g. "nested", "rises", or "pieces" => RASAS
					&& !(StringAt((mCurrent - 1), 3, "TED", "SES", "CES", "")
						|| StringAt(0, 9, "ANTIPODES", "ANOPHELES", "")
						|| StringAt(0, 8, "MOHAMMED", "MUHAMMED", "MOUHAMED", "")
						|| StringAt(0, 7, "MOHAMED", "")
						|| StringAt(0, 6, "NORRED", "MEDVED", "MERCED", "ALLRED", "KHALED", "RASHED", "MASJED", "")	
						|| StringAt(0, 5, "JARED", "AHMED", "HAMED", "JAVED", "") 
						|| StringAt(0, 4, "ABED", "IMED", ""))))
				// e.g.  'wholeness', 'boneless', 'barely'
				|| (StringAt((mCurrent + 1), 4, "NESS", "LESS", "") && ((mCurrent + 4) == mLast))
				|| (StringAt((mCurrent + 1), 2, "LY", "") && ((mCurrent + 2) == mLast)
						&& !StringAt(0, 6, "CICELY", "")))
		{
			return true;
		}

		return false;
	}

	/**
	 * Tests for words where an 'E' at the end of the word
	 * is pronounced
	 *
	 * special cases, mostly from the greek, spanish, japanese, 
	 * italian, and french words normally having an acute accent. 
	 * also, pronouns and articles
	 * 
	 * Many Thanks to ali, QuentinCompson, JeffCO, ToonScribe, Xan,
	 * Trafalz, and VictorLaszlo, all of them atriots from the Eschaton, 
	 * for all their fine contributions!
	 * 
	 * @return true if 'E' at end is pronounced
	 * 
	*/
	boolean E_Pronounced_At_End()
	{
		if ((mCurrent == mLast)
			&& (StringAt((mCurrent - 6), 7, "STROPHE", "")
			// if a vowel is before the 'E', vowel eater will have eaten it. 
			//otherwise, consonant + 'E' will need 'E' pronounced
			|| (mLength == 2)
			|| ((mLength == 3) && !IsVowel(0))
			// these german name endings can be relied on to have the 'e' pronounced
			|| (StringAt((mLast - 2), 3, "BKE", "DKE", "FKE", "KKE", "LKE", 
										 "NKE", "MKE", "PKE", "TKE", "VKE", "ZKE", "")
				&& !StringAt(0, 5, "FINKE", "FUNKE", "")
				&& !StringAt(0, 6, "FRANKE", ""))
			|| StringAt((mLast - 4), 5, "SCHKE", "")
			|| (StringAt(0, 4, "ACME", "NIKE", "CAFE", "RENE", "LUPE", "JOSE", "ESME", "") && (mLength == 4))
			|| (StringAt(0, 5, "LETHE", "CADRE", "TILDE", "SIGNE", "POSSE", "LATTE", "ANIME", "DOLCE", "CROCE", 
								"ADOBE", "OUTRE", "JESSE", "JAIME", "JAFFE", "BENGE", "RUNGE", 
								"CHILE", "DESME", "CONDE", "URIBE", "LIBRE", "ANDRE", "") && (mLength == 5))
			|| (StringAt(0, 6, "HECATE", "PSYCHE", "DAPHNE", "PENSKE", "CLICHE", "RECIPE", 
							   "TAMALE", "SESAME", "SIMILE", "FINALE", "KARATE", "RENATE", "SHANTE",  
							   "OBERLE", "COYOTE", "KRESGE", "STONGE", "STANGE", "SWAYZE", "FUENTE", 
							   "SALOME", "URRIBE", "") && (mLength == 6))
			|| (StringAt(0, 7, "ECHIDNE", "ARIADNE", "MEINEKE", "PORSCHE", "ANEMONE", "EPITOME", 
								"SYNCOPE", "SOUFFLE", "ATTACHE", "MACHETE", "KARAOKE", "BUKKAKE", 
								"VICENTE", "ELLERBE", "VERSACE", "") && (mLength == 7))
			|| (StringAt(0, 8, "PENELOPE", "CALLIOPE", "CHIPOTLE", "ANTIGONE", "KAMIKAZE", "EURIDICE", 
							   "YOSEMITE", "FERRANTE", "") && (mLength == 8))
			|| (StringAt(0, 9, "HYPERBOLE", "GUACAMOLE", "XANTHIPPE", "") && (mLength == 9))
			|| (StringAt(0, 10, "SYNECDOCHE", "") && (mLength == 10))))
		{
			return true;
		}

		return false;
	}
	
	/**
	 * Detect internal silent 'E's e.g. "roseman",
	 * "firestone"
	 * 
	 */
	boolean Silent_Internal_E()
	{
		// 'olesen' but not 'olen'	RAKE BLAKE 
		if ((StringAt(0, 3, "OLE", "") 
				&& E_Silent_Suffix(3) && !E_Pronouncing_Suffix(3))
		   || (StringAt(0, 4, "BARE", "FIRE", "FORE", "GATE", "HAGE", "HAVE",
				             "HAZE", "HOLE", "CAPE", "HUSE", "LACE", "LINE", 
				             "LIVE", "LOVE", "MORE", "MOSE", "MORE", "NICE", 
				             "RAKE", "ROBE", "ROSE", "SISE", "SIZE", "WARE", 
				             "WAKE", "WISE", "WINE", "") 
				&& E_Silent_Suffix(4) && !E_Pronouncing_Suffix(4))
		   || (StringAt(0, 5, "BLAKE", "BRAKE", "BRINE", "CARLE", "CLEVE", "DUNNE",
				   			 "HEDGE", "HOUSE", "JEFFE", "LUNCE", "STOKE", "STONE", 
				   			 "THORE", "WEDGE", "WHITE", "") 
				 &&  E_Silent_Suffix(5) && !E_Pronouncing_Suffix(5))
		   || (StringAt(0, 6, "BRIDGE", "CHEESE", "") 
				 && E_Silent_Suffix(6) && !E_Pronouncing_Suffix(6))
		   || StringAt((mCurrent - 5), 7, "CHARLES", ""))
		{
			return true;
		}
		
		return false;
	}

	/**
	 * Detect conditions required
	 * for the 'E' not to be pronounced
	 * 
	 */
	boolean E_Silent_Suffix(int at)
	{
		if ((mCurrent == (at - 1)) 
				&& (mLength > (at + 1)) 
				&& (IsVowel((at + 1)) 
				|| (StringAt(at, 2, "ST", "SL", "") 
					&& (mLength > (at + 2)))))
		{
			return true;	
		}
		
		return false;
	}
	
	/**
	 * Detect endings that will
	 * cause the 'e' to be pronounced
	 * 
	 */
	boolean E_Pronouncing_Suffix(int at)
	{		
		// e.g. 'bridgewood' - the other vowels will get eaten
		// up so we need to put one in here
		if ((mLength == (at + 4)) && StringAt(at, 4, "WOOD", ""))
		{
			return true;
		}
		
		// same as above
		if ((mLength == (at + 5)) && StringAt(at, 5, "WATER", "WORTH", ""))
		{
			return true;
		}
				
		// e.g. 'bridgette'
		if ((mLength == (at + 3)) && StringAt(at, 3, "TTE", "LIA", "NOW", "ROS", "RAS", ""))
		{
			return true;
		}
		
		// e.g. 'olena'
		if ((mLength == (at + 2)) && StringAt(at, 2, "TA", "TT", "NA", "NO", "NE",
													 "RS", "RE", "LA", "AU", "RO", "RA", ""))
		{
			return true;
		}
		
		// e.g. 'bridget'
		if ((mLength == (at + 1)) && StringAt(at, 1, "T", "R", ""))
		{
			return true;
		}
		
		return false;
	}

	/**
	 * Exceptions where 'E' is pronounced where it
	 * usually wouldn't be, and also some cases
	 * where 'LE' transposition rules don't apply
	 * and the vowel needs to be encoded here
	 *
	 * @return true if 'E' pronounced 
	 *  
	 */
	boolean E_Pronounced_Exceptions()
	{
		// greek names e.g. "herakles" or hispanic names e.g. "robles", where 'e' is pronounced, other exceptions
		if ((((mCurrent + 1) == mLast) 
				&& (StringAt((mCurrent - 3), 5, "OCLES", "ACLES", "AKLES", "")
					|| StringAt(0, 4, "INES", "")
					|| StringAt(0, 5, "LOPES", "ESTES", "GOMES", "NUNES", "ALVES", "ICKES", 
							          "INNES", "PERES", "WAGES", "NEVES", "BENES", "DONES", "")
					|| StringAt(0, 6, "CORTES", "CHAVES", "VALDES", "ROBLES", "TORRES", "FLORES", "BORGES", 
									  "NIEVES", "MONTES", "SOARES", "VALLES", "GEDDES", "ANDRES", "VIAJES", 
									  "CALLES", "FONTES", "HERMES", "ACEVES", "BATRES", "MATHES", "")
					|| StringAt(0, 7, "DELORES", "MORALES", "DOLORES", "ANGELES", "ROSALES", "MIRELES", "LINARES", 
							          "PERALES", "PAREDES", "BRIONES", "SANCHES", "CAZARES", "REVELES", "ESTEVES", 
									  "ALVARES", "MATTHES", "SOLARES", "CASARES", "CACERES", "STURGES", "RAMIRES", 
									  "FUNCHES", "BENITES", "FUENTES", "PUENTES", "TABARES", "HENTGES", "VALORES", "")
					|| StringAt(0, 8, "GONZALES", "MERCEDES", "FAGUNDES", "JOHANNES", "GONSALES", "BERMUDES", 
									  "CESPEDES", "BETANCES", "TERRONES", "DIOGENES", "CORRALES", "CABRALES", 
									  "MARTINES", "GRAJALES", "")
					|| StringAt(0, 9, "CERVANTES", "FERNANDES", "GONCALVES", "BENEVIDES", "CIFUENTES", "SIFUENTES",
							  		  "SERVANTES", "HERNANDES", "BENAVIDES", "")
					|| StringAt(0, 10, "ARCHIMEDES", "CARRIZALES", "MAGALLANES", "")))
			|| StringAt(mCurrent - 2, 4, "FRED", "DGES", "DRED", "GNES", "")
			|| StringAt((mCurrent - 5), 7, "PROBLEM", "RESPLEN", "")
			|| StringAt((mCurrent - 4), 6, "REPLEN", "")
			|| StringAt((mCurrent - 3), 4, "SPLE", ""))
		{
			return true;
		}
		
		return false;
	}

	/**
	 * Encodes "-UE".
	 * 
	 * @return true if encoding handled in this routine, false if not
	 */
	boolean Skip_Silent_UE()
	{
		// always silent except for cases listed below
		if ((StringAt((mCurrent - 1), 3, "QUE", "GUE", "") 
			&& !StringAt(0, 8, "BARBEQUE", "PALENQUE", "APPLIQUE", "")
			// '-que' cases usually french but missing the acute accent
			&& !StringAt(0, 6, "RISQUE", "")
			&& !StringAt((mCurrent - 3), 5, "ARGUE", "SEGUE", "")
			&& !StringAt(0, 7, "PIROGUE", "ENRIQUE", "")
			&& !StringAt(0, 10, "COMMUNIQUE", ""))
			&& (mCurrent > 1) 
				&& (((mCurrent + 1) == mLast) 
					|| StringAt(0, 7, "JACQUES", "")))
		{
			mCurrent = SkipVowels(mCurrent);
			return true;
		}

		return false;
	}

	/**
	 * Encodes 'B'
	 * 
	 *
	 */
	void Encode_B()
	{
		if (Encode_Silent_B())
		{
			return;
		}

		// "-mb", e.g", "dumb", already skipped over under
		// 'M', altho it should really be handled here...
		MetaphAddExactApprox("B", "P");

		if ((CharAt(mCurrent + 1) == 'B')
			|| ((CharAt(mCurrent + 1) == 'P')
			&& ((mCurrent + 1 < mLast) && (CharAt(mCurrent + 2) != 'H'))))
		{
			mCurrent += 2;
		}
		else
		{
			mCurrent++;
		}
	}

	/**
	 * Encodes silent 'B' for cases not covered under "-mb-"
	 * 
	 * 
	 * @return true if encoding handled in this routine, false if not
	 *
	*/
	boolean Encode_Silent_B()
	{
		//'debt', 'doubt', 'subtle'
		if (StringAt((mCurrent - 2), 4, "DEBT", "") 
			|| StringAt((mCurrent - 2), 5, "SUBTL", "") 
			|| StringAt((mCurrent - 2), 6, "SUBTIL", "") 
			|| StringAt((mCurrent - 3), 5, "DOUBT", ""))
		{
			MetaphAdd("T");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encodes 'C'
	 * 
	 */
	void Encode_C()
	{

		if (Encode_Silent_C_At_Beginning()
			|| Encode_CA_To_S()
			|| Encode_CO_To_S()
			|| Encode_CH()
			|| Encode_CCIA()
			|| Encode_CC()
			|| Encode_CK_CG_CQ()
			|| Encode_C_Front_Vowel()
			|| Encode_Silent_C()
			|| Encode_CZ()
			|| Encode_CS())
		{
			return;
		}

		//else
		if (!StringAt((mCurrent - 1), 1, "C", "K", "G", "Q", ""))
		{
			MetaphAdd("K");
		}

		//name sent in 'mac caffrey', 'mac gregor
		if (StringAt((mCurrent + 1), 2, " C", " Q", " G", ""))
		{
			mCurrent += 2;
		}
		else
		{
			if (StringAt((mCurrent + 1), 1, "C", "K", "Q", "") 
				&& !StringAt((mCurrent + 1), 2, "CE", "CI", ""))
			{
				mCurrent += 2;
				// account for combinations such as Ro-ckc-liffe
				if (StringAt((mCurrent), 1, "C", "K", "Q", "") 
					&& !StringAt((mCurrent + 1), 2, "CE", "CI", ""))
				{
					mCurrent++;
				}
			}
			else
			{
				mCurrent++;
			}
		}
	}

	/**
	 * Encodes cases where 'C' is silent at beginning of word
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Silent_C_At_Beginning()
	{
	    //skip these when at start of word
	    if ((mCurrent == 0)
			&& StringAt(mCurrent, 2, "CT", "CN", ""))
		{
	        mCurrent += 1;
			return true;
		}

		return false;
	}


	/**
	 * Encodes exceptions where "-CA-" should encode to S
	 * instead of K including cases where the cedilla has not been used
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_CA_To_S()
	{
		// Special case: 'caesar'. 
		// Also, where cedilla not used, as in "linguica" => LNKS
		if (((mCurrent == 0) && StringAt(mCurrent, 4, "CAES", "CAEC", "CAEM", ""))
			|| StringAt(0, 8, "FRANCAIS", "FRANCAIX", "LINGUICA", "")
			|| StringAt(0, 6, "FACADE", "")
			|| StringAt(0, 9, "GONCALVES", "PROVENCAL", ""))
		{
			MetaphAdd("S");
			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encodes exceptions where "-CO-" encodes to S instead of K
	 * including cases where the cedilla has not been used
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_CO_To_S()
	{
		// e.g. 'coelecanth' => SLKN0
		if ((StringAt(mCurrent, 4, "COEL", "") 
				&& (IsVowel(mCurrent + 4) || ((mCurrent + 3) == mLast)))
			|| StringAt(mCurrent, 5, "COENA", "COENO", "")
			|| StringAt(0, 8, "FRANCOIS", "MELANCON", "")
			|| StringAt(0, 6, "GARCON", ""))
		{
			MetaphAdd("S");
			AdvanceCounter(3, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode "-CH-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_CH()
	{
		if (StringAt(mCurrent, 2, "CH", ""))
		{       
			if (Encode_CHAE()
				|| Encode_CH_To_H()
				|| Encode_Silent_CH()
				|| Encode_ARCH()
				// Encode_CH_To_X() should be
				// called before the germanic
				// and greek encoding functions
				|| Encode_CH_To_X()
				|| Encode_English_CH_To_K()
				|| Encode_Germanic_CH_To_K()
				|| Encode_Greek_CH_Initial()
				|| Encode_Greek_CH_Non_Initial())
			{
				return true;
			}

			if (mCurrent > 0)
			{
				if (StringAt(0, 2, "MC", "") 
						&& (mCurrent == 1))
				{
					//e.g., "McHugh"
					MetaphAdd("K");
				}
				else
				{
					MetaphAdd("X", "K");
				}
			}
			else
			{
				MetaphAdd("X");
			}
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encodes "-CHAE-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_CHAE()
	{
		// e.g. 'michael'
		if (((mCurrent > 0) && StringAt((mCurrent + 2), 2, "AE", "")))
		{
			if (StringAt(0, 7, "RACHAEL", ""))
			{
				MetaphAdd("X");
			}
			else if (!StringAt((mCurrent - 1), 1, "C", "K", "G", "Q", ""))
			{
				MetaphAdd("K");
			}

			AdvanceCounter(4, 2);
			return true;
		}

		return false;
	}

	/**
	 * Encdoes transliterations from the hebrew where the
	 * sound 'kh' is represented as "-CH-". The normal pronounciation
	 * of this in english is either 'h' or 'kh', and alternate
	 * spellings most often use "-H-"
	 *
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_CH_To_H()
	{
		// hebrew => 'H', e.g. 'channukah', 'chabad'
		if (((mCurrent == 0) 
			&& (StringAt((mCurrent + 2), 3, "AIM", "ETH", "ELM", "")
			|| StringAt((mCurrent + 2), 4, "ASID", "AZAN", "")
			|| StringAt((mCurrent + 2), 5, "UPPAH", "UTZPA", "ALLAH", "ALUTZ", "AMETZ", "")
			|| StringAt((mCurrent + 2), 6, "ESHVAN", "ADARIM", "ANUKAH", "")
			|| StringAt((mCurrent + 2), 7, "ALLLOTH", "ANNUKAH", "AROSETH", "")))
			// and an irish name with the same encoding
			|| StringAt((mCurrent - 3), 7, "CLACHAN", ""))
		{
			MetaphAdd("H");
			AdvanceCounter(3, 2);
			return true;
		}

		return false;
	}

	/**
	 * Encodes cases where "-CH-" is not pronounced
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Silent_CH()
	{
		// '-ch-' not pronounced
		if (StringAt((mCurrent - 2), 7, "FUCHSIA", "")
			|| StringAt((mCurrent - 2), 5, "YACHT", "")
			|| StringAt(0, 8, "STRACHAN", "")
			|| StringAt(0, 8, "CRICHTON", "")
			|| (StringAt((mCurrent - 3), 6, "DRACHM", ""))
				&& !StringAt((mCurrent - 3), 7, "DRACHMA", ""))
		{
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encodes "-CH-" to X
	 * English language patterns
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_CH_To_X()
	{
		// e.g. 'approach', 'beach'
		if ((StringAt((mCurrent - 2), 4, "OACH", "EACH", "EECH", "OUCH", "OOCH", "MUCH", "SUCH", "")
				&& !StringAt((mCurrent - 3), 5, "JOACH", ""))
			// e.g. 'dacha', 'macho'
			|| (((mCurrent + 2) == mLast ) && StringAt((mCurrent - 1), 4, "ACHA", "ACHO", ""))
			|| (StringAt(mCurrent, 4, "CHOT", "CHOD", "CHAT", "") && ((mCurrent + 3) == mLast))
			|| ((StringAt((mCurrent - 1), 4, "OCHE", "") && ((mCurrent + 2) == mLast))
					&& !StringAt((mCurrent - 2), 5, "DOCHE", ""))		
			|| StringAt((mCurrent - 4), 6, "ATTACH", "DETACH", "KOVACH", "")
			|| StringAt((mCurrent - 5), 7, "SPINACH", "")
			|| StringAt(0, 6, "MACHAU", "")
			|| StringAt((mCurrent - 4), 8, "PARACHUT", "")
			|| StringAt((mCurrent - 5), 8, "MASSACHU", "")
			|| (StringAt((mCurrent - 3), 5, "THACH", "") && !StringAt((mCurrent - 1), 4, "ACHE", ""))
			|| StringAt((mCurrent - 2), 6, "VACHON", "") )
		{
			MetaphAdd("X");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encodes "-CH-" to K in contexts of
	 * initial "A" or "E" follwed by "CH"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_English_CH_To_K()
	{
		//'ache', 'echo', alternate spelling of 'michael'
		if (((mCurrent == 1) && RootOrInflections(mInWord, "ACHE"))
			|| (((mCurrent > 3) && RootOrInflections(mInWord.substring(mCurrent - 1), "ACHE"))
				&& (StringAt(0, 3, "EAR", "")
					|| StringAt(0, 4, "HEAD", "BACK", "")
					|| StringAt(0, 5, "HEART", "BELLY", "TOOTH", "")))
			|| StringAt((mCurrent - 1), 4, "ECHO", "")
			|| StringAt((mCurrent - 2), 7, "MICHEAL", "")
			|| StringAt((mCurrent - 4), 7, "JERICHO", "")
			|| StringAt((mCurrent - 5), 7, "LEPRECH", ""))
		{
			MetaphAdd("K", "X");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encodes "-CH-" to K in mostly germanic context
	 * of internal "-ACH-", with exceptions
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Germanic_CH_To_K()
	{
		// various germanic
		// "<consonant><vowel>CH-"implies a german word where 'ch' => K
		if (((mCurrent > 1)
			&& !IsVowel(mCurrent - 2) 
			&& StringAt((mCurrent - 1), 3, "ACH", "")
			&& !StringAt((mCurrent - 2), 7, "MACHADO", "MACHUCA", "LACHANC", "LACHAPE", "KACHATU", "")
			&& !StringAt((mCurrent - 3), 7, "KHACHAT", "")
			&& ((CharAt(mCurrent + 2) != 'I') 
				&& ((CharAt(mCurrent + 2) != 'E')
				|| StringAt((mCurrent - 2), 6, "BACHER", "MACHER", "MACHEN", "LACHER", "")) )
						// e.g. 'brecht', 'fuchs'
				|| (StringAt((mCurrent + 2), 1, "T", "S", "") 
						&& !(StringAt(0, 11, "WHICHSOEVER", "") || StringAt(0, 9, "LUNCHTIME", "") ))
						// e.g. 'andromache'
				|| StringAt(0, 4, "SCHR", "")
				|| ((mCurrent > 2) && StringAt((mCurrent - 2), 5, "MACHE", ""))
				|| ((mCurrent == 2) && StringAt((mCurrent - 2), 4, "ZACH", ""))
				|| StringAt((mCurrent - 4), 6, "SCHACH", "")
				|| StringAt((mCurrent - 1), 5, "ACHEN", "")
				|| StringAt((mCurrent - 3), 5, "SPICH", "ZURCH", "BUECH", "")
				|| (StringAt((mCurrent - 3), 5, "KIRCH", "JOACH", "BLECH", "MALCH", "")
						// "kirch" and "blech" both get 'X'
						&& !(StringAt((mCurrent - 3), 8, "KIRCHNER", "") || ((mCurrent + 1) == mLast)))
				|| (((mCurrent + 1) == mLast) && StringAt((mCurrent - 2), 4, "NICH", "LICH", "BACH", ""))
				|| (((mCurrent + 1) == mLast) 
						&& StringAt((mCurrent - 3), 5, "URICH", "BRICH", "ERICH", "DRICH", "NRICH", "")
						&& !StringAt((mCurrent - 5), 7, "ALDRICH", "") 
						&& !StringAt((mCurrent - 6), 8, "GOODRICH", "")
						&& !StringAt((mCurrent - 7), 9, "GINGERICH", "")))
				|| (((mCurrent + 1) == mLast) && StringAt((mCurrent - 4), 6, "ULRICH", "LFRICH", "LLRICH", 
																				"EMRICH", "ZURICH", "EYRICH", ""))
			// e.g., 'wachtler', 'wechsler', but not 'tichner'
			|| ((StringAt((mCurrent - 1), 1, "A", "O", "U", "E", "") || (mCurrent == 0)) 
						&& StringAt((mCurrent + 2), 1, "L", "R", "N", "M", "B", "H", "F", "V", "W", " ", "")))
		{       
			// "CHR/L-" e.g. 'chris' do not get
			// alt pronunciation of 'X'
			if (StringAt((mCurrent + 2), 1, "R", "L", "")
				|| SlavoGermanic())
			{
				MetaphAdd("K");
			}
			else
			{
				MetaphAdd("K", "X");
			}
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-ARCH-". Some occurances are from greek roots and therefore encode
	 * to 'K', others are from english words and therefore encode to 'X'
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_ARCH()
	{
		if (StringAt((mCurrent - 2), 4, "ARCH", ""))
		{
			// "-ARCH-" has many combining forms where "-CH-" => K because of its
			// derivation from the greek
			if (((IsVowel(mCurrent + 2) && StringAt((mCurrent - 2), 5, "ARCHA", "ARCHI", "ARCHO", "ARCHU", "ARCHY", ""))
				|| StringAt((mCurrent - 2), 6, "ARCHEA", "ARCHEG", "ARCHEO", "ARCHET", "ARCHEL", "ARCHES", "ARCHEP", 
												"ARCHEM", "ARCHEN", "")
				|| (StringAt((mCurrent - 2), 4, "ARCH", "") && (((mCurrent + 1) == mLast)))
				|| StringAt(0, 7, "MENARCH", ""))
				&& (!RootOrInflections(mInWord, "ARCH")
					&& !StringAt((mCurrent - 4), 6, "SEARCH", "POARCH", "")
					&& !StringAt(0, 9, "ARCHENEMY", "ARCHIBALD", "ARCHULETA", "ARCHAMBAU", "")
					&& !StringAt(0, 6, "ARCHER", "ARCHIE", "") 
					&& !((((StringAt((mCurrent - 3), 5, "LARCH", "MARCH", "PARCH", "")
							|| StringAt((mCurrent - 4), 6, "STARCH", ""))
							&& !(StringAt(0, 6, "EPARCH", "")
									|| StringAt(0, 7, "NOMARCH", "")
									|| StringAt(0, 8, "EXILARCH", "HIPPARCH", "MARCHESE", "")
									|| StringAt(0, 9, "ARISTARCH", "")
									|| StringAt(0, 9, "MARCHETTI", "")) )
							|| RootOrInflections(mInWord, "STARCH"))
							&& (!StringAt((mCurrent - 2), 5, "ARCHU", "ARCHY", "")
									|| StringAt(0, 7, "STARCHY", ""))))) 
			{
				MetaphAdd("K", "X");
			}
			else
			{
				MetaphAdd("X");
			}
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-CH-" to K when from greek roots
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Greek_CH_Initial()
	{
		// greek roots e.g. 'chemistry', 'chorus', ch at beginning of root
		if ((StringAt(mCurrent, 6, "CHAMOM", "CHARAC", "CHARIS", "CHARTO", "CHARTU", "CHARYB", "CHRIST", "CHEMIC", "CHILIA", "") 
			|| (StringAt(mCurrent, 5, "CHEMI", "CHEMO", "CHEMU", "CHEMY", "CHOND", "CHONA", "CHONI", "CHOIR", "CHASM", 
									   "CHARO", "CHROM", "CHROI", "CHAMA", "CHALC", "CHALD", "CHAET", "CHIRO", "CHILO", "CHELA", "CHOUS", 
									   "CHEIL", "CHEIR", "CHEIM", "CHITI", "CHEOP", "")
				&& !(StringAt(mCurrent, 6, "CHEMIN", "") || StringAt((mCurrent - 2), 8, "ANCHONDO", "")))
			|| (StringAt(mCurrent, 5, "CHISM", "CHELI", "")
			// exclude spanish "machismo"
				&& !(StringAt(0, 8, "MACHISMO", "")
				// exclude some french words
					|| StringAt(0, 10, "REVANCHISM", "")
					|| StringAt(0, 9, "RICHELIEU", "")
					|| (StringAt(0, 5, "CHISM", "") && (mLength == 5))
					|| StringAt(0, 6, "MICHEL", "")))
			// include e.g. "chorus", "chyme", "chaos"
			|| (StringAt(mCurrent, 4, "CHOR", "CHOL", "CHYM", "CHYL", "CHLO", "CHOS", "CHUS", "CHOE", "")
					&& !StringAt(0, 6, "CHOLLO", "CHOLLA", "CHORIZ", ""))
			// "chaos" => K but not "chao"
			|| (StringAt(mCurrent, 4, "CHAO", "") && ((mCurrent + 3) != mLast))
			// e.g. "abranchiate"
			|| (StringAt(mCurrent, 4, "CHIA", "")  && !(StringAt(0, 10, "APPALACHIA", "") || StringAt(0, 7, "CHIAPAS", "")))
			// e.g. "chimera"
			|| StringAt(mCurrent, 7, "CHIMERA", "CHIMAER", "CHIMERI", "") 
			// e.g. "chameleon"
			|| ((mCurrent == 0) && StringAt(mCurrent, 5, "CHAME", "CHELO", "CHITO", "") )
			// e.g. "spirochete"
			|| ((((mCurrent + 4) == mLast) || ((mCurrent + 5) == mLast)) && StringAt((mCurrent - 1), 6, "OCHETE", "")))
			// more exceptions where "-CH-" => X e.g. "chortle", "crocheter"
				&& !((StringAt(0, 5, "CHORE",  "CHOLO", "CHOLA", "") && (mLength == 5))
					|| StringAt(mCurrent, 5, "CHORT", "CHOSE", "")
					|| StringAt((mCurrent - 3), 7, "CROCHET", "")
					|| StringAt(0, 7, "CHEMISE", "CHARISE", "CHARISS", "CHAROLE", "")) )
		{
			// "CHR/L-" e.g. 'christ', 'chlorine' do not get
			// alt pronunciation of 'X'
			if (StringAt((mCurrent + 2), 1, "R", "L", ""))
			{
				MetaphAdd("K");
			}
			else
			{
				MetaphAdd("K", "X");
			}
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode a variety of greek and some german roots where "-CH-" => K
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Greek_CH_Non_Initial()
	{
		//greek & other roots e.g. 'tachometer', 'orchid', ch in middle or end of root
		if (StringAt((mCurrent - 2), 6, "ORCHID", "NICHOL", "MECHAN", "LICHEN", "MACHIC", "PACHEL", "RACHIF", "RACHID", 
										"RACHIS", "RACHIC", "MICHAL", "")
			|| StringAt((mCurrent - 3), 5, "MELCH", "GLOCH", "TRACH", "TROCH", "BRACH", "SYNCH", "PSYCH", 
											"STICH", "PULCH", "EPOCH", "")
			|| (StringAt((mCurrent - 3), 5, "TRICH", "") && !StringAt((mCurrent - 5), 7, "OSTRICH", ""))
			|| (StringAt((mCurrent - 2), 4, "TYCH", "TOCH", "BUCH", "MOCH", "CICH", "DICH", "NUCH", "EICH", "LOCH", 
											 "DOCH", "ZECH", "WYCH", "")
				&& !(StringAt((mCurrent - 4), 9, "INDOCHINA", "") || StringAt((mCurrent - 2), 6, "BUCHON", "")))
			|| StringAt((mCurrent - 2), 5, "LYCHN", "TACHO", "ORCHO", "ORCHI", "LICHO", "")
			|| (StringAt((mCurrent - 1), 5, "OCHER", "ECHIN", "ECHID", "") && ((mCurrent == 1) || (mCurrent == 2)))
			|| StringAt((mCurrent - 4), 6, "BRONCH", "STOICH", "STRYCH", "TELECH", "PLANCH", "CATECH", "MANICH", "MALACH", 
											"BIANCH", "DIDACH", "")
			|| (StringAt((mCurrent - 1), 4, "ICHA", "ICHN", "") && (mCurrent == 1))
			|| StringAt((mCurrent - 2), 8, "ORCHESTR", "")
			|| StringAt((mCurrent - 4), 8, "BRANCHIO", "BRANCHIF", "")
			|| (StringAt((mCurrent - 1), 5, "ACHAB", "ACHAD", "ACHAN", "ACHAZ", "")
				&& !StringAt((mCurrent - 2), 7, "MACHADO", "LACHANC", ""))
			|| StringAt((mCurrent - 1), 6, "ACHISH", "ACHILL", "ACHAIA", "ACHENE", "")
			|| StringAt((mCurrent - 1), 7, "ACHAIAN", "ACHATES", "ACHIRAL", "ACHERON", "")
			|| StringAt((mCurrent - 1), 8, "ACHILLEA", "ACHIMAAS", "ACHILARY", "ACHELOUS", "ACHENIAL", "ACHERNAR", "")
			|| StringAt((mCurrent - 1), 9, "ACHALASIA", "ACHILLEAN", "ACHIMENES", "")
			|| StringAt((mCurrent - 1), 10, "ACHIMELECH", "ACHITOPHEL", "")
			// e.g. 'inchoate'
			|| (((mCurrent - 2) == 0) && (StringAt((mCurrent - 2), 6, "INCHOA", "")
			// e.g. 'ischemia'
			|| StringAt(0, 4, "ISCH", "")) )
			// e.g. 'ablimelech', 'antioch', 'pentateuch'
			|| (((mCurrent + 1) == mLast) && StringAt((mCurrent - 1), 1, "A", "O", "U", "E", "") 
				&& !(StringAt(0, 7, "DEBAUCH", "")
						|| StringAt((mCurrent - 2), 4, "MUCH", "SUCH", "KOCH", "")
						|| StringAt((mCurrent - 5), 7, "OODRICH", "ALDRICH", ""))))
		{
			MetaphAdd("K", "X");
			mCurrent += 2;
			return true;
		}

		return false;
	}
	
	/**
	 * Encodes reliably italian "-CCIA-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_CCIA()
	{
		//e.g., 'focaccia'
		if (StringAt((mCurrent + 1), 3, "CIA", ""))
		{
			MetaphAdd("X", "S");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-CC-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_CC()
	{
		//double 'C', but not if e.g. 'McClellan'
		if (StringAt(mCurrent, 2, "CC", "") && !((mCurrent == 1) && (CharAt(0) == 'M')))
		{
			// exception
			if (StringAt((mCurrent - 3), 7, "FLACCID", ""))
			{
				MetaphAdd("S");
				AdvanceCounter(3, 2);
				return true;
			}

			//'bacci', 'bertucci', other italian
			if ((((mCurrent + 2) == mLast) && StringAt((mCurrent + 2), 1, "I", ""))
				|| StringAt((mCurrent + 2), 2, "IO", "")
				|| (((mCurrent + 4) == mLast) && StringAt((mCurrent + 2), 3, "INO", "INI", "")))
			{
				MetaphAdd("X");
				AdvanceCounter(3, 2);
				return true;
			}

			//'accident', 'accede' 'succeed'
			if (StringAt((mCurrent + 2), 1, "I", "E", "Y", "")
				//except 'bellocchio','bacchus', 'soccer' get K
				&& !((CharAt(mCurrent + 2) == 'H') 
					|| StringAt((mCurrent - 2), 6, "SOCCER", "")))
			{
				MetaphAdd("KS");
				AdvanceCounter(3, 2);
				return true;

			}
			else
			{
				//Pierce's rule
				MetaphAdd("K");
				mCurrent += 2;
				return true;
			}
		}

		return false;
	}

	/**
	 * Encode cases where the consonant following "C" is redundant
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_CK_CG_CQ()
	{
		if (StringAt(mCurrent, 2, "CK", "CG", "CQ", ""))
		{
			// eastern european spelling e.g. 'gorecki' == 'goresky'
			if (StringAt(mCurrent, 3, "CKI", "CKY", "") 
				&& ((mCurrent + 2) == mLast)
				&& (mLength > 6))
			{
				MetaphAdd("K", "SK");
			}
			else
			{
				MetaphAdd("K");
			}
			mCurrent += 2;

			if (StringAt(mCurrent, 1, "K", "G", "Q", ""))
			{
				mCurrent++;
			}
			return true;
		}

		return false;
	}

	/**
	 * Encode cases where "C" preceeds a front vowel such as "E", "I", or "Y".
	 * These cases most likely => S or X
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_C_Front_Vowel()
	{
		if (StringAt(mCurrent, 2, "CI", "CE", "CY", ""))
		{
			if (Encode_British_Silent_CE()
				|| Encode_CE()
				|| Encode_CI()
				|| Encode_Latinate_Suffixes())
			{
				AdvanceCounter(2, 1);
				return true;
			}

			MetaphAdd("S");
			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_British_Silent_CE()
	{
		// english place names like e.g.'gloucester' pronounced glo-ster
		if ((StringAt((mCurrent + 1), 5, "ESTER", "") && ((mCurrent + 5) == mLast))
			|| StringAt((mCurrent + 1), 10, "ESTERSHIRE", ""))
		{
			return true;
		}

		return false;
	}

	/**
	 *  
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_CE()
	{
		// 'ocean', 'commercial', 'provincial', 'cello', 'fettucini', 'medici'
		if ((StringAt((mCurrent + 1), 3, "EAN", "") && IsVowel(mCurrent - 1))
			// e.g. 'rosacea'
			|| (StringAt((mCurrent - 1), 4, "ACEA", "") 
				&& ((mCurrent + 2) == mLast)
				&& !StringAt(0, 7, "PANACEA", ""))
			// e.g. 'botticelli', 'concerto'
			|| StringAt((mCurrent + 1), 4, "ELLI", "ERTO", "EORL", "") 
			// some italian names familiar to americans
			|| (StringAt((mCurrent - 3), 5, "CROCE", "") && ((mCurrent + 1) == mLast)) 
			|| StringAt((mCurrent - 3), 5, "DOLCE", "") 
			// e.g. 'cello'
			|| (StringAt((mCurrent + 1), 4, "ELLO", "") 
				&& ((mCurrent + 4) == mLast)))
		{
			MetaphAdd("X", "S");
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_CI()
	{
		// with consonant before C
		// e.g. 'fettucini', but exception for the americanized pronunciation of 'mancini'
		if (((StringAt((mCurrent + 1), 3, "INI", "") && !StringAt(0, 7, "MANCINI", "")) && ((mCurrent + 3) == mLast))
			// e.g. 'medici'
			|| (StringAt((mCurrent - 1), 3, "ICI", "") && ((mCurrent + 1) == mLast))
			// e.g. "commercial', 'provincial', 'cistercian'
			|| StringAt((mCurrent - 1), 5, "RCIAL", "NCIAL", "RCIAN", "UCIUS", "")
			// special cases
			|| StringAt((mCurrent - 3), 6, "MARCIA", "")
			|| StringAt((mCurrent - 2), 7, "ANCIENT", ""))
		{
			MetaphAdd("X", "S");
			return true;
		}

		// with vowel before C (or at beginning?)
		if (((StringAt(mCurrent, 3, "CIO", "CIE", "CIA", "")
			&& IsVowel(mCurrent - 1))
			// e.g. "ciao"
			|| StringAt((mCurrent + 1), 3, "IAO", ""))
			&& !StringAt((mCurrent - 4), 8, "COERCION", ""))
		{			
			if ((StringAt(mCurrent, 4, "CIAN", "CIAL", "CIAO", "CIES", "CIOL", "CION", "")
				// exception - "glacier" => 'X' but "spacier" = > 'S'
				|| StringAt((mCurrent - 3), 7, "GLACIER", "")
				|| StringAt(mCurrent, 5, "CIENT", "CIENC", "CIOUS", "CIATE", "CIATI", "CIATO", "CIABL", "CIARY", "")
				|| (((mCurrent + 2) == mLast) && StringAt(mCurrent, 3, "CIA", "CIO", ""))
				|| (((mCurrent + 3) == mLast) && StringAt(mCurrent, 3, "CIAS", "CIOS", "")))
				// exceptions
				&& !(StringAt((mCurrent - 4), 11, "ASSOCIATION", "")
					|| StringAt(0, 4, "OCIE", "")
					// exceptions mostly because these names are usually from 
					// the spanish rather than the italian in america
					|| StringAt((mCurrent - 2), 5, "LUCIO", "")
					|| StringAt((mCurrent - 2), 6, "MACIAS", "")
					|| StringAt((mCurrent - 3), 6, "GRACIE", "GRACIA", "")  
					|| StringAt((mCurrent - 2), 7, "LUCIANO", "") 
					|| StringAt((mCurrent - 3), 8, "MARCIANO", "")
					|| StringAt((mCurrent - 4), 7, "PALACIO", "") 
					|| StringAt((mCurrent - 4), 9, "FELICIANO", "") 
					|| StringAt((mCurrent - 5), 8, "MAURICIO", "") 
					|| StringAt((mCurrent - 7), 11, "ENCARNACION", "")
					|| StringAt((mCurrent - 4), 8, "POLICIES", "")
					|| StringAt((mCurrent - 2), 8, "HACIENDA", "")
					|| StringAt((mCurrent - 6), 9, "ANDALUCIA", "")
					|| StringAt((mCurrent - 2), 5, "SOCIO", "SOCIE", "")))
			{
				MetaphAdd("X", "S");
			}
			else
			{
				MetaphAdd("S", "X");
			}
			
			return true;
		}
		
		// exception
		if (StringAt((mCurrent - 4), 8, "COERCION", ""))
		{
			MetaphAdd("J");
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Latinate_Suffixes()
	{
		if (StringAt((mCurrent + 1), 4, "EOUS", "IOUS", ""))
		{
			MetaphAdd("X", "S");
			return true;
		}

		return false;
	}

	/**
	 * Encodes some exceptions where "C" is silent
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Silent_C()
	{
		if (StringAt((mCurrent + 1), 1, "T", "S", ""))
		{
			if (StringAt(0, 11, "CONNECTICUT", "") 
				|| StringAt(0, 6, "INDICT", "TUCSON", ""))
			{       
				mCurrent++;
				return true;
			}
		}

		return false;
	}

	/**
	 * Encodes slavic spellings or transliterations
	 * written as "-CZ-"
	 *
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_CZ()
	{
		if (StringAt((mCurrent + 1), 1, "Z", "")
			&& !StringAt((mCurrent - 1), 6, "ECZEMA", ""))
		{
			if (StringAt(mCurrent, 4, "CZAR", ""))
			{
				MetaphAdd("S");
			}
			// otherwise most likely a czech word...
			else
			{
				MetaphAdd("X");
			}
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * "-CS" special cases
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_CS()
	{
		// give an 'etymological' 2nd
		// encoding for "kovacs" so
		// that it matches "kovach"
		if (StringAt(0, 6, "KOVACS", ""))
		{
			MetaphAdd("KS", "X");
			mCurrent += 2;
			return true;
		}
	
		if (StringAt((mCurrent - 1), 3, "ACS", "")
			&& ((mCurrent + 1) == mLast)
			&& !StringAt((mCurrent - 4), 6, "ISAACS", ""))
		{
			MetaphAdd("X");
			mCurrent += 2;
			return true;
		}
		
		return false;
	}

	/**
	 * Encode "-D-"
	 * 
	 */
	void Encode_D()
	{
		if (Encode_DG()
			|| Encode_DJ()
			|| Encode_DT_DD()
			|| Encode_D_To_J()
			|| Encode_DOUS()
			|| Encode_Silent_D())
		{
			return;
		}

		if (mEncodeExact)
		{
			// "final de-voicing" in this case
			// e.g. 'missed' == 'mist'
			if ((mCurrent == mLast)
				&& StringAt((mCurrent - 3), 4, "SSED", ""))
			{
				MetaphAdd("T");
			}
			else
			{
				MetaphAdd("D");
			}
		}
		else
		{
			MetaphAdd("T");
		}
		mCurrent++;
	}

	/**
	 * Encode "-DG-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_DG()
	{
		if (StringAt(mCurrent, 2, "DG", ""))
		{
			// excludes exceptions e.g. 'edgar', 
			// or cases where 'g' is first letter of combining form 
			// e.g. 'handgun', 'waldglas'
			if (StringAt((mCurrent + 2), 1, "A", "O", "")
				// e.g. "midgut"
				|| StringAt((mCurrent + 1), 3, "GUN", "GUT", "")
				// e.g. "handgrip"
				|| StringAt((mCurrent + 1), 4, "GEAR", "GLAS", "GRIP", "GREN", "GILL", "GRAF", "")
				// e.g. "mudgard"
				|| StringAt((mCurrent + 1), 5, "GUARD", "GUILT", "GRAVE", "GRASS", "")
				// e.g. "woodgrouse"
				|| StringAt((mCurrent + 1), 6, "GROUSE", ""))
			{
				MetaphAddExactApprox("DG", "TK");
			}
			else
			{
				//e.g. "edge", "abridgment"
				MetaphAdd("J");
			}
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-DJ-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_DJ()
	{
		// e.g. "adjacent"
		if (StringAt(mCurrent, 2, "DJ", ""))
		{
			MetaphAdd("J");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-DD-" and "-DT-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_DT_DD()
	{
		// eat redundant 'T' or 'D'
		if (StringAt(mCurrent, 2, "DT", "DD", ""))
		{
			if (StringAt(mCurrent, 3, "DTH",  ""))
			{
				MetaphAddExactApprox("D0", "T0");
				mCurrent += 3;
			}
			else
			{
				if (mEncodeExact)
				{
					// devoice it
					if (StringAt(mCurrent, 2, "DT", ""))
					{
						MetaphAdd("T");
					}
					else
					{
						MetaphAdd("D");
					}						
				}
				else
				{
					MetaphAdd("T");
				}
				mCurrent += 2;
			}
			return true;
		}

		return false;
	}

	/**
	 * Encode cases where "-DU-" "-DI-", and "-DI-" => J
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_D_To_J()
	{
		// e.g. "module", "adulate"
		if ((StringAt(mCurrent, 3, "DUL", "") 
				&& (IsVowel(mCurrent - 1) && IsVowel(mCurrent + 3)))
			// e.g. "soldier", "grandeur", "procedure"
			|| (((mCurrent + 3) == mLast) 
				&& StringAt((mCurrent - 1) , 5, "LDIER", "NDEUR", "EDURE", "RDURE", ""))
			|| StringAt((mCurrent - 3), 7, "CORDIAL", "")
			// e.g.  "pendulum", "education"
			|| StringAt((mCurrent - 1), 5, "NDULA", "NDULU", "EDUCA", "")
			// e.g. "individual", "individual", "residuum"
			|| StringAt((mCurrent - 1), 4, "ADUA", "IDUA", "IDUU", ""))
		{
			MetaphAddExactApprox("J", "D", "J", "T");
			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode latinate suffix "-DOUS" where 'D' is pronounced as J
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_DOUS()
	{
		// e.g. "assiduous", "arduous"
		if (StringAt((mCurrent + 1), 4, "UOUS", ""))
		{
			MetaphAddExactApprox("J", "D", "J", "T");
			AdvanceCounter(4, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode silent "-D-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 *	 
	 */
	boolean Encode_Silent_D()
	{
		// silent 'D' e.g. 'wednesday', 'handsome'
		if (StringAt((mCurrent - 2), 9, "WEDNESDAY", "")
			|| StringAt((mCurrent - 3), 7, "HANDKER", "HANDSOM", "WINDSOR", "")
			// french silent D at end in words or names familiar to americans
			|| StringAt((mCurrent - 5), 6, "PERNOD", "ARTAUD", "RENAUD", "")
			|| StringAt((mCurrent - 6), 7, "RIMBAUD", "MICHAUD", "BICHAUD", ""))
		{
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-F-"
	 * 
	 */
	void Encode_F()
	{
		// Encode cases where "-FT-" => "T" is usually silent
		// e.g. 'often', 'soften'
		// This should really be covered under "T"!
		if (StringAt((mCurrent - 1), 5, "OFTEN", ""))
		{
			MetaphAdd("F", "FT");
			mCurrent += 2;
			return;
		}

		// eat redundant 'F'
		if (CharAt(mCurrent + 1) == 'F')
		{
			mCurrent += 2;
		}
		else
		{
			mCurrent++;
		}

		MetaphAdd("F");

	}

	/**
	 * Encode "-G-"
	 * 
	 */
	void Encode_G()
	{
		if (Encode_Silent_G_At_Beginning()
			|| Encode_GG()
			|| Encode_GK()
			|| Encode_GH()
			|| Encode_Silent_G()
			|| Encode_GN()
			|| Encode_GL()
			|| Encode_Initial_G_Front_Vowel()
			|| Encode_NGER()
			|| Encode_GER()
			|| Encode_GEL()
			|| Encode_Non_Initial_G_Front_Vowel()
			|| Encode_GA_To_J())
		{
			return;
		}

		if (!StringAt((mCurrent - 1), 1, "C", "K", "G", "Q", ""))
		{
			MetaphAddExactApprox("G", "K");
		}

		mCurrent++;
	}

	/**
	 * Encode cases where 'G' is silent at beginning of word
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Silent_G_At_Beginning()
	{    
		//skip these when at start of word
	    if ((mCurrent == 0)
			&& StringAt(mCurrent, 2, "GN", ""))
		{
	        mCurrent += 1;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-GG-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_GG()
	{
		if (CharAt(mCurrent + 1) == 'G')
		{
			// italian e.g, 'loggia', 'caraveggio', also 'suggest' and 'exaggerate'
			if (StringAt((mCurrent - 1), 5, "AGGIA", "OGGIA", "AGGIO", "EGGIO", "EGGIA", "IGGIO", "")
				// 'ruggiero' but not 'snuggies'
				|| (StringAt((mCurrent - 1), 5, "UGGIE", "") && !(((mCurrent + 3) == mLast) || ((mCurrent + 4) == mLast)))
				|| (((mCurrent + 2) == mLast) && StringAt((mCurrent - 1), 4, "AGGI", "OGGI", ""))
				|| StringAt((mCurrent - 2), 6, "SUGGES", "XAGGER", "REGGIE", ""))
			{
				// expection where "-GG-" => KJ
				if (StringAt((mCurrent - 2), 7, "SUGGEST", ""))
				{
					MetaphAddExactApprox("G", "K");
				}

				MetaphAdd("J");
				AdvanceCounter(3, 2);
			}
			else
			{
				MetaphAddExactApprox("G", "K");
				mCurrent += 2;
			}
			return true;
		}
		
		return false;
	}

	/**
	 * Encode "-GK-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_GK()
	{
		// 'gingko'
		if (CharAt(mCurrent + 1) == 'K')
		{
			MetaphAdd("K");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-GH-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_GH()
	{
		if (CharAt(mCurrent + 1) == 'H')
		{
			if (Encode_GH_After_Consonant()
				|| Encode_Initial_GH()
				|| Encode_GH_To_J()
				|| Encode_GH_To_H()
				|| Encode_UGHT()
				|| Encode_GH_H_Part_Of_Other_Word()
				|| Encode_Silent_GH()
				|| Encode_GH_To_F())
			{
				return true;
			}

			MetaphAddExactApprox("G", "K");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 *
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_GH_After_Consonant()
	{
		// e.g. 'burgher', 'bingham'
		if ((mCurrent > 0) 
			&& !IsVowel(mCurrent - 1)
			// not e.g. 'greenhalgh'
			&& !(StringAt((mCurrent - 3), 5, "HALGH", "") 
					&& ((mCurrent + 1) == mLast)))
		{
			MetaphAddExactApprox("G", "K");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 *
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Initial_GH()
	{
		if (mCurrent < 3)
		{
			// e.g. "ghislane", "ghiradelli"
			if (mCurrent == 0)
			{ 
				if (CharAt(mCurrent + 2) == 'I')
				{
					MetaphAdd("J");
				}
				else
				{
					MetaphAddExactApprox("G", "K");
				}
				mCurrent += 2;
				return true;
			}
		}

		return false;
	}


	/**
	 *
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_GH_To_J()
	{
		// e.g., 'greenhalgh', 'dunkenhalgh', english names
		if (StringAt((mCurrent - 2), 4, "ALGH", "") && ((mCurrent + 1) == mLast))
		{
			MetaphAdd("J", "");
			mCurrent += 2;
			return true;
		}

		return false;
	}
	
	/**
	 *
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_GH_To_H()
	{
		// special cases
		// e.g., 'donoghue', 'donaghy'
		if ((StringAt((mCurrent - 4), 4, "DONO", "DONA", "") && IsVowel(mCurrent + 2))
			|| StringAt((mCurrent - 5), 9, "CALLAGHAN", ""))
		{
			MetaphAdd("H");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 *
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_UGHT()
	{
		//e.g. "ought", "aught", "daughter", "slaughter"    
		if (StringAt((mCurrent - 1), 4, "UGHT", ""))
		{
			if ((StringAt((mCurrent - 3), 5, "LAUGH", "")
				&& !(StringAt((mCurrent - 4), 7, "SLAUGHT", "")
					|| StringAt((mCurrent - 3), 7, "LAUGHTO", "")))
					|| StringAt((mCurrent - 4), 6, "DRAUGH", ""))
			{
				MetaphAdd("FT");
			}
			else
			{
				MetaphAdd("T");
			}
			mCurrent += 3;
			return true;
		}

		return false;
	}

	/**
	 *
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_GH_H_Part_Of_Other_Word()
	{
		// if the 'H' is the beginning of another word or syllable
		if (StringAt((mCurrent + 1), 4, "HOUS", "HEAD", "HOLE", "HORN", "HARN", ""))
		{
			MetaphAddExactApprox("G", "K");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 *
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Silent_GH()
	{
		//Parker's rule (with some further refinements) - e.g., 'hugh'
		if (((((mCurrent > 1) && StringAt((mCurrent - 2), 1, "B", "H", "D", "G", "L", "") )
			//e.g., 'bough'
			|| ((mCurrent > 2) 
				&& StringAt((mCurrent - 3), 1, "B", "H", "D", "K", "W", "N", "P", "V", "")
				&& !StringAt(0, 6, "ENOUGH", ""))
			//e.g., 'broughton'
			|| ((mCurrent > 3) && StringAt((mCurrent - 4), 1, "B", "H", "") )
			//'plough', 'slaugh'
			|| ((mCurrent > 3) && StringAt((mCurrent - 4), 2, "PL", "SL", "") )  
			|| ((mCurrent > 0) 
					// 'sigh', 'light'
					&& ((CharAt(mCurrent - 1) == 'I')
						|| StringAt(0, 4, "PUGH", "")
						// e.g. 'MCDONAGH', 'MURTAGH', 'CREAGH'
						|| (StringAt((mCurrent - 1), 3, "AGH", "") 
								&& ((mCurrent + 1) == mLast))
						|| StringAt((mCurrent - 4), 6, "GERAGH", "DRAUGH", "")
						|| (StringAt((mCurrent - 3), 5, "GAUGH", "GEOGH", "MAUGH", "")
								&& !StringAt(0, 9, "MCGAUGHEY", ""))
						// exceptions to 'tough', 'rough', 'lough'
						|| (StringAt((mCurrent - 2), 4, "OUGH", "") 
								&& (mCurrent > 3) 
								&& !StringAt((mCurrent - 4), 6, "CCOUGH", "ENOUGH", "TROUGH", "CLOUGH", "")))))
			// suffixes starting w/ vowel where "-GH-" is usually silent
			&& (StringAt((mCurrent - 3), 5, "VAUGH", "FEIGH", "LEIGH", "")
				|| StringAt((mCurrent - 2), 4, "HIGH", "TIGH", "")
				|| ((mCurrent + 1) == mLast)
				|| (StringAt((mCurrent + 2), 2, "IE", "EY", "ES", "ER", "ED", "TY", "") 
					&& ((mCurrent + 3) == mLast)
					&& !StringAt((mCurrent - 5), 9, "GALLAGHER", ""))
				|| (StringAt((mCurrent + 2), 1, "Y", "") && ((mCurrent + 2) == mLast))
				|| (StringAt((mCurrent + 2), 3, "ING", "OUT", "") && ((mCurrent + 4) == mLast))
				|| (StringAt((mCurrent + 2), 4, "ERTY", "") && ((mCurrent + 5) == mLast))
				|| (!IsVowel(mCurrent + 2) 
						|| StringAt((mCurrent - 3), 5, "GAUGH", "GEOGH", "MAUGH", "")
						|| StringAt((mCurrent - 4), 8, "BROUGHAM", ""))))
			// exceptions where '-g-' pronounced
			&& !(StringAt(0, 6, "BALOGH", "SABAGH", "")	
				|| StringAt((mCurrent - 2), 7, "BAGHDAD", "")						
				|| StringAt((mCurrent - 3), 5, "WHIGH", "")
				|| StringAt((mCurrent - 5), 7, "SABBAGH", "AKHLAGH", "")))			
		{
			// silent - do nothing
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 *
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_GH_Special_Cases()
	{
		boolean handled = false;

		// special case: 'hiccough' == 'hiccup'
		if (StringAt((mCurrent - 6), 8, "HICCOUGH", ""))
		{
			MetaphAdd("P");
			handled = true;
		}
		// special case: 'lough' alt spelling for scots 'loch'
		else if (StringAt(0, 5, "LOUGH", ""))
		{
			MetaphAdd("K");
			handled = true;
		}
		// hungarian
		else if (StringAt(0, 6, "BALOGH", ""))
		{
			MetaphAddExactApprox("G", "", "K", "");
			handled = true;
		}
		// "maclaughlin"
		else if (StringAt((mCurrent - 3), 8, "LAUGHLIN", "COUGHLAN", "LOUGHLIN", ""))
		{
			MetaphAdd("K", "F");
			handled = true;
		}
		else if (StringAt((mCurrent - 3), 5, "GOUGH", "")
				|| StringAt((mCurrent - 7), 9, "COLCLOUGH", ""))
		{
			MetaphAdd("", "F");
			handled = true;
		}
		
		if (handled)
		{
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 *
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_GH_To_F()
	{
		// the cases covered here would fall under
		// the GH_To_F rule below otherwise
		if (Encode_GH_Special_Cases())
		{
			return true;
		}
		else
		{
			//e.g., 'laugh', 'cough', 'rough', 'tough'
			if ((mCurrent > 2) 
				&& (CharAt(mCurrent - 1) == 'U')
				&& IsVowel(mCurrent - 2)
				&& StringAt((mCurrent - 3), 1, "C", "G", "L", "R", "T", "N", "S", "")
				&& !StringAt((mCurrent - 4), 8, "BREUGHEL", "FLAUGHER", ""))
			{
				MetaphAdd("F");
				mCurrent += 2;
				return true;
			}
		}

		return false;
	}

	/**
	 * Encode some contexts where "g" is silent
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Silent_G()
	{
		// e.g. "phlegm", "apothegm", "voigt"
		if ((((mCurrent + 1) == mLast) 
			&& (StringAt((mCurrent - 1), 3, "EGM", "IGM", "AGM", "")
				|| StringAt(mCurrent, 2, "GT", "")))
			|| (StringAt(0, 5, "HUGES", "") && (mLength == 5)))
		{
			mCurrent++;
			return true;
		}
		
		// vietnamese names e.g. "Nguyen" but not "Ng"
		if (StringAt(0, 2, "NG", "") && (mCurrent != mLast))
		{
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * ENcode "-GN-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_GN()
	{
		if (CharAt(mCurrent + 1) == 'N')
		{
			// 'align' 'sign', 'resign' but not 'resignation'
			// also 'impugn', 'impugnable', but not 'repugnant'
			if (((mCurrent > 1) 
				&& ((StringAt((mCurrent - 1), 1, "I", "U", "E", "") 
					|| StringAt((mCurrent - 3), 9, "LORGNETTE", "")
					|| StringAt((mCurrent - 2), 9, "LAGNIAPPE", "")
					|| StringAt((mCurrent - 2), 6, "COGNAC", "")
					|| StringAt((mCurrent - 3), 7, "CHAGNON", "")
					|| StringAt((mCurrent - 5), 9, "COMPAGNIE", "")
					|| StringAt((mCurrent - 4), 6, "BOLOGN", ""))
				// Exceptions: following are cases where 'G' is pronounced
				// in "assign" 'g' is silent, but not in "assignation"
				&& !(StringAt((mCurrent + 2), 5, "ATION", "")
					|| StringAt((mCurrent + 2), 4, "ATOR", "")
					|| StringAt((mCurrent + 2), 3, "ATE", "ITY", "")
				// exception to exceptions, not pronounced:
				|| (StringAt((mCurrent + 2), 2, "AN", "AC", "IA", "UM", "") 
					&& !(StringAt((mCurrent - 3), 8, "POIGNANT", "")
						|| StringAt((mCurrent - 2), 6, "COGNAC", "")))
				|| StringAt(0, 7, "SPIGNER", "STEGNER", "")
				|| (StringAt(0, 5, "SIGNE", "") && (mLength == 5))
				|| StringAt((mCurrent - 2), 5, "LIGNI", "LIGNO", "REGNA", "DIGNI", "WEGNE", 
												"TIGNE", "RIGNE", "REGNE", "TIGNO", "")
				|| StringAt((mCurrent - 2), 6, "SIGNAL", "SIGNIF", "SIGNAT", "")
				|| StringAt((mCurrent - 1), 5, "IGNIT", ""))
				&& !StringAt((mCurrent - 2), 6, "SIGNET", "LIGNEO", "") ))
				//not e.g. 'cagney', 'magna'
				|| (((mCurrent + 2) == mLast) 
						&& StringAt(mCurrent, 3, "GNE", "GNA", "")
						&& !StringAt((mCurrent - 2), 5, "SIGNA", "MAGNA", "SIGNE", "")))
			{
				MetaphAddExactApprox("N", "GN", "N", "KN");
			}
			else
			{
				MetaphAddExactApprox("GN", "KN");
			}
			mCurrent += 2;
			return true;
		}
		return false;
	}

	/**
	 * Encode "-GL-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_GL()
	{
		//'tagliaro', 'puglia' BUT add K in alternative 
		// since americans sometimes do this
		if (StringAt((mCurrent + 1), 3, "LIA", "LIO", "LIE", "") 
			&& IsVowel(mCurrent - 1))
		{
			MetaphAddExactApprox("L", "GL", "L", "KL");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Initial_G_Soft()
	{
		if (((StringAt((mCurrent + 1), 2, "EL", "EM", "EN", "EO", "ER", "ES", "IA", "IN", "IO", "IP", "IU", "YM", "YN", "YP", "YR", "EE", "")
				|| StringAt((mCurrent + 1), 3, "IRA", "IRO", ""))
			// except for smaller set of cases where => K, e.g. "gerber"
			&& !(StringAt((mCurrent + 1), 3, "ELD", "ELT", "ERT", "INZ", "ERH", "ITE", "ERD", "ERL", "ERN", 
											  "INT", "EES", "EEK", "ELB", "EER", "")
					|| StringAt((mCurrent + 1), 4, "ERSH", "ERST", "INSB", "INGR", "EROW", "ERKE", "EREN", "")
					|| StringAt((mCurrent + 1), 5, "ELLER", "ERDIE", "ERBER", "ESUND", "ESNER", "INGKO", "INKGO", 
													"IPPER", "ESELL", "IPSON", "EEZER", "ERSON", "ELMAN", "")
					|| StringAt((mCurrent + 1), 6, "ESTALT", "ESTAPO", "INGHAM", "ERRITY", "ERRISH", "ESSNER", "ENGLER", "")
					|| StringAt((mCurrent + 1), 7, "YNAECOL", "YNECOLO", "ENTHNER", "ERAGHTY", "")
					|| StringAt((mCurrent + 1), 8, "INGERICH", "EOGHEGAN", "")))
			|| (IsVowel(mCurrent + 1)
				&& (StringAt((mCurrent + 1), 3, "EE ", "EEW", "")
						|| (StringAt((mCurrent + 1), 3, "IGI", "IRA", "IBE", "AOL", "IDE", "IGL", "") 
														&& !StringAt((mCurrent + 1), 5, "IDEON", "") )
					|| StringAt((mCurrent + 1), 4, "ILES", "INGI", "ISEL", "")
					|| (StringAt((mCurrent + 1), 5, "INGER", "") && !StringAt((mCurrent + 1), 8, "INGERICH", "")) 
					|| StringAt((mCurrent + 1), 5, "IBBER", "IBBET", "IBLET", "IBRAN", "IGOLO", "IRARD", "IGANT", "")
					|| StringAt((mCurrent + 1), 6, "IRAFFE", "EEWHIZ", "")
					|| StringAt((mCurrent + 1), 7, "ILLETTE", "IBRALTA", ""))))
		{
			return true;
		}

		return false;
	}

	/**
	 * Encode cases where 'G' is at start of word followed
	 * by a "front" vowel e.g. 'E', 'I', 'Y'
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Initial_G_Front_Vowel()
	{
		// 'g' followed by vowel at beginning
		if ((mCurrent == 0) && Front_Vowel(mCurrent + 1))
		{
			// special case "gila" as in "gila monster"
			if (StringAt((mCurrent + 1), 3, "ILA", "")
				&& (mLength == 4))
			{
				MetaphAdd("H");
			}
			else if (Initial_G_Soft())
			{
				MetaphAddExactApprox("J", "G", "J", "K");
			}
			else
			{
				// only code alternate 'J' if front vowel
				if ((mInWord.charAt(mCurrent + 1) == 'E') || (mInWord.charAt(mCurrent + 1) == 'I'))
				{
					MetaphAddExactApprox("G", "J", "K", "J");
				}
				else
				{
					MetaphAddExactApprox("G", "K");
				}
			}

			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode "-NGER-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_NGER()
	{
		if ((mCurrent > 1)
			&& StringAt((mCurrent - 1), 4, "NGER", ""))
		{
			// default 'G' => J  such as 'ranger', 'stranger', 'manger', 'messenger', 'orangery', 'granger'
			// 'boulanger', 'challenger', 'danger', 'changer', 'harbinger', 'lounger', 'ginger', 'passenger'
			// except for these the following
			if (!(RootOrInflections(mInWord, "ANGER")
				|| RootOrInflections(mInWord, "LINGER")
				|| RootOrInflections(mInWord, "MALINGER")
				|| RootOrInflections(mInWord, "FINGER")
				|| (StringAt((mCurrent - 3), 4, "HUNG", "FING", "BUNG", "WING", "RING", "DING", "ZENG", 
												 "ZING", "JUNG", "LONG", "PING", "CONG", "MONG", "BANG", 
												 "GANG", "HANG", "LANG", "SANG", "SING", "WANG", "ZANG", "")
					// exceptions to above where 'G' => J	
					&& !(StringAt((mCurrent - 6), 7, "BOULANG", "SLESING", "KISSING", "DERRING", "")
							|| StringAt((mCurrent - 8), 9, "SCHLESING", "")
							|| StringAt((mCurrent - 5), 6, "SALING", "BELANG", "")
							|| StringAt((mCurrent - 6), 7, "BARRING", "")
							|| StringAt((mCurrent - 6), 9, "PHALANGER", "")
							|| StringAt((mCurrent - 4), 5, "CHANG", "")))
				|| StringAt((mCurrent - 4), 5, "STING", "YOUNG", "")
				|| StringAt((mCurrent - 5), 6, "STRONG", "")
				|| StringAt(0, 3, "UNG", "ENG", "ING", "")
				|| StringAt(mCurrent, 6, "GERICH", "")
				|| StringAt(0, 6, "SENGER", "")
				|| StringAt((mCurrent - 3), 6, "WENGER", "MUNGER", "SONGER", "KINGER", "")
				|| StringAt((mCurrent - 4), 7, "FLINGER", "SLINGER", "STANGER", "STENGER", "KLINGER", "CLINGER", "")
				|| StringAt((mCurrent - 5), 8, "SPRINGER", "SPRENGER", "")
				|| StringAt((mCurrent - 3), 7, "LINGERF", "")
				|| StringAt((mCurrent - 2), 7, "ANGERLY", "ANGERBO", "INGERSO", "") ))
			{
				MetaphAddExactApprox("J", "G", "J", "K");
			}
			else
			{
				MetaphAddExactApprox("G", "J", "K", "J");
			}

			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}
	
	/**
	 * Encode "-GER-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_GER()
	{
		if ((mCurrent > 0)
			&& StringAt((mCurrent + 1), 2, "ER", ""))
		{
			// Exceptions to 'GE' where 'G' => K
			// e.g. "JAGER", "TIGER", "LIGER", "LAGER", "LUGER", "AUGER", "EAGER", "HAGER", "SAGER"
			if ((((mCurrent == 2) && IsVowel(mCurrent - 1) && !IsVowel(mCurrent - 2) 
					&& !(StringAt((mCurrent - 2), 5, "PAGER", "WAGER", "NIGER", "ROGER", "LEGER", "CAGER", ""))
				|| StringAt((mCurrent - 2), 5, "AUGER", "EAGER", "INGER", "YAGER", "")) 
				|| StringAt((mCurrent - 3), 6, "SEEGER", "JAEGER", "GEIGER", "KRUGER", "SAUGER", "BURGER", 
												"MEAGER", "MARGER", "RIEGER", "YAEGER", "STEGER", "PRAGER", "SWIGER", 
												"YERGER", "TORGER", "FERGER", "HILGER", "ZEIGER", "YARGER", 
												"COWGER", "CREGER", "KROGER", "KREGER", "GRAGER", "STIGER", "BERGER", "")
				// 'berger' but not 'bergerac'
				|| (StringAt((mCurrent - 3), 6, "BERGER", "") && ((mCurrent + 2) == mLast))
				|| StringAt((mCurrent - 4), 7, "KREIGER", "KRUEGER", "METZGER", "KRIEGER", "KROEGER", "STEIGER", 
												"DRAEGER", "BUERGER", "BOERGER", "FIBIGER", "")
				// e.g. 'harshbarger', 'winebarger'
				|| (StringAt((mCurrent - 3), 6, "BARGER", "") && (mCurrent > 4))
				// e.g. 'weisgerber'
				|| (StringAt(mCurrent, 6, "GERBER", "") && (mCurrent > 0))
				|| StringAt((mCurrent - 5), 8, "SCHWAGER",	"LYBARGER",	"SPRENGER", "GALLAGER", "WILLIGER", "")
				|| StringAt(0, 4, "HARGER", "")
				|| (StringAt(0, 4, "AGER", "EGER", "") && (mLength == 4))
				|| StringAt((mCurrent - 1), 6, "YGERNE", "") 
				|| StringAt((mCurrent - 6), 9, "SCHWEIGER", "")) 
				&& !(StringAt((mCurrent - 5), 10, "BELLIGEREN", "")
						|| StringAt(0, 7, "MARGERY", "")
						|| StringAt((mCurrent - 3), 8, "BERGERAC", "")))
			{				
				if (SlavoGermanic())
				{
					MetaphAddExactApprox("G", "K");
				}
				else
				{
					MetaphAddExactApprox("G", "J", "K", "J");											
				}
			}
			else
			{
				MetaphAddExactApprox("J", "G", "J", "K");
			}

			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}

	/**
	 * ENcode "-GEL-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_GEL()
	{
		// more likely to be "-GEL-" => JL
		if (StringAt((mCurrent + 1), 2, "EL", "")
			&& (mCurrent > 0))
		{
			// except for
			// "BAGEL", "HEGEL", "HUGEL", "KUGEL", "NAGEL", "VOGEL", "FOGEL", "PAGEL"
			if (((mLength == 5) 
					&& IsVowel(mCurrent - 1) 
					&& !IsVowel(mCurrent - 2)
					&& !StringAt((mCurrent - 2), 5, "NIGEL", "RIGEL", ""))
				// or the following as combining forms
				|| StringAt((mCurrent - 2), 5, "ENGEL", "HEGEL", "NAGEL", "VOGEL", "")
				|| StringAt((mCurrent - 3), 6, "MANGEL", "WEIGEL", "FLUGEL", "RANGEL", "HAUGEN", "RIEGEL", "VOEGEL", "")
				|| StringAt((mCurrent - 4), 7, "SPEIGEL", "STEIGEL", "WRANGEL", "SPIEGEL", "")
				|| StringAt((mCurrent - 4), 8, "DANEGELD", ""))
			{
				if (SlavoGermanic())
				{
					MetaphAddExactApprox("G", "K");
				}
				else
				{
					MetaphAddExactApprox("G", "J", "K", "J");											
				}
			}
			else
			{
				MetaphAddExactApprox("J", "G", "J", "K");
			}

			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode "-G-" followed by a vowel when non-initial leter.
	 * Default for this is a 'J' sound, so check exceptions where
	 * it is pronounced 'G'
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Non_Initial_G_Front_Vowel()
	{
		// -gy-, gi-, ge-
		if (StringAt((mCurrent + 1), 1, "E", "I", "Y", ""))
		{
			// '-ge' at end
			// almost always 'j 'sound
			if (StringAt(mCurrent, 2, "GE", "") && (mCurrent == (mLast - 1)))
			{
				if (Hard_GE_At_End())
				{
					if (SlavoGermanic())
					{
						MetaphAddExactApprox("G", "K");
					}
					else
					{
						MetaphAddExactApprox("G", "J", "K", "J");											
					}
				}
				else
				{
					MetaphAdd("J");
				}
			}
			else
			{
				if (Internal_Hard_G())
				{
					// don't encode KG or KK if e.g. "mcgill"
					if (!((mCurrent == 2) && StringAt(0, 2, "MC", "")) 
						   || StringAt(0, 3, "MAC", ""))
					{
						if (SlavoGermanic())
						{
							MetaphAddExactApprox("G", "K");
						}
						else
						{
							MetaphAddExactApprox("G", "J", "K", "J");											
						}
					}
				}
				else
				{
					MetaphAddExactApprox("J", "G", "J", "K");
				}
			}

			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}
	
	/*
	 * Detect german names and other words that have
	 * a 'hard' 'g' in the context of "-ge" at end
	 * 
	 * @return true if encoding handled in this routine, false if not
	 */
	boolean Hard_GE_At_End()
	{
		if (StringAt(0, 6, "RENEGE", "STONGE", "STANGE", "PRANGE", "KRESGE", "") 
			|| StringAt(0, 5, "BYRGE", "BIRGE", "BERGE", "HAUGE", "")
			|| StringAt(0, 4, "HAGE", "")					
			|| StringAt(0, 5, "LANGE", "SYNGE", "BENGE", "RUNGE", "HELGE", "")
			|| StringAt(0, 4, "INGE", "LAGE", ""))
		{
			return true;
		}
			
		return false;
	}

	/**
	 * Exceptions to default encoding to 'J':
	 * encode "-G-" to 'G' in "-g<frontvowel>-" words
	 * where we are not at "-GE" at the end of the word
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Internal_Hard_G()
	{
		// if not "-GE" at end
		if (!(((mCurrent + 1) == mLast) && (CharAt(mCurrent + 1) == 'E') ) 
				&& (Internal_Hard_NG()
					|| Internal_Hard_GEN_GIN_GET_GIT()
					|| Internal_Hard_G_Open_Syllable()
					|| Internal_Hard_G_Other()))
		{
			return true;
		}
			
		return false;
	}
	
	/**
	 * Detect words where "-ge-" or "-gi-" get a 'hard' 'g'
	 * even though this is usually a 'soft' 'g' context
	 *  
	 * @return true if 'hard' 'g' detected
	 * 
	 */
	boolean Internal_Hard_G_Other()
	{
		if ((StringAt(mCurrent, 4, "GETH", "GEAR", "GEIS", "GIRL", "GIVI", "GIVE", "GIFT", 
								   "GIRD", "GIRT", "GILV", "GILD", "GELD", "")
					&& !StringAt((mCurrent - 3), 6, "GINGIV", "") )
				// "gish" but not "largish"
				|| (StringAt((mCurrent + 1), 3, "ISH", "") && (mCurrent > 0) && !StringAt(0, 4, "LARG", ""))
				|| (StringAt((mCurrent - 2), 5, "MAGED", "MEGID", "") && !((mCurrent + 2) == mLast))
				|| StringAt(mCurrent, 3, "GEZ", "") 
				|| StringAt(0, 4, "WEGE", "HAGE", "") 
				|| (StringAt((mCurrent - 2), 6, "ONGEST", "UNGEST", "") 
					&& ((mCurrent + 3) == mLast)
					&& !StringAt((mCurrent - 3), 7, "CONGEST", "")) 
				|| StringAt(0, 5, "VOEGE", "BERGE", "HELGE", "")
				|| (StringAt(0, 4, "ENGE", "BOGY", "") && (mLength == 4))
				|| StringAt(mCurrent, 6, "GIBBON", "") 
				|| StringAt(0, 10, "CORREGIDOR", "") 
				|| StringAt(0, 8, "INGEBORG", "") 
				|| (StringAt(mCurrent, 4, "GILL", "") 
						&& (((mCurrent + 3) == mLast) || ((mCurrent + 4) == mLast))
						&& !StringAt(0, 8, "STURGILL", "")))
		{
			return true;
		}

		return false;
	}

	/**
	 * Detect words where "-gy-", "-gie-", "-gee-", 
	 * or "-gio-" get a 'hard' 'g' even though this is 
	 * usually a 'soft' 'g' context
	 *  
	 * @return true if 'hard' 'g' detected
	 * 
	 */
	boolean Internal_Hard_G_Open_Syllable()
	{
		if (StringAt((mCurrent + 1), 3, "EYE", "")
			|| StringAt((mCurrent - 2), 4, "FOGY", "POGY", "YOGI", "") 
			|| StringAt((mCurrent - 2), 5, "MAGEE", "MCGEE", "HAGIO", "") 
			|| StringAt((mCurrent - 1), 4, "RGEY", "OGEY", "") 
			|| StringAt((mCurrent - 3), 5, "HOAGY", "STOGY", "PORGY", "") 
			|| StringAt((mCurrent - 5), 8, "CARNEGIE", "") 
			|| (StringAt((mCurrent - 1), 4, "OGEY", "OGIE", "") && ((mCurrent + 2) == mLast)))
		{
			return true;
		}

		return false;
	}
	
	/**
	 * Detect a number of contexts, mostly german names, that
	 * take a 'hard' 'g'.
	 * 
	 * @return true if 'hard' 'g' detected, false if not
	 *  
	 */
	boolean Internal_Hard_GEN_GIN_GET_GIT()
	{
		if ((StringAt((mCurrent - 3), 6, "FORGET", "TARGET", "MARGIT", "MARGET", "TURGEN", 
										 "BERGEN", "MORGEN", "JORGEN", "HAUGEN", "JERGEN", 
										 "JURGEN", "LINGEN", "BORGEN", "LANGEN", "KLAGEN", "STIGER", "BERGER", "") 
					&& !StringAt(mCurrent, 7, "GENETIC", "GENESIS", "")
					&& !StringAt((mCurrent - 4), 8, "PLANGENT", ""))
			|| (StringAt((mCurrent - 3), 6, "BERGIN", "FEAGIN", "DURGIN", "") && ((mCurrent + 2) == mLast))
			|| (StringAt((mCurrent - 2), 5, "ENGEN", "") && !StringAt((mCurrent + 3), 3, "DER", "ETI", "ESI", ""))
			|| StringAt((mCurrent - 4), 7, "JUERGEN", "")
			|| StringAt(0, 5, "NAGIN", "MAGIN", "HAGIN", "")
			|| (StringAt(0, 5, "ENGIN", "DEGEN", "LAGEN", "MAGEN", "NAGIN", "") && (mLength == 5))
			|| (StringAt((mCurrent - 2), 5, "BEGET", "BEGIN", "HAGEN", "FAGIN", 
										 "BOGEN", "WIGIN", "NTGEN", "EIGEN", 
										 "WEGEN", "WAGEN", "")
				&& !StringAt((mCurrent - 5), 8, "OSPHAGEN", "")))
		{
			return true;
		}

		return false;
	}
	/**
	 * Detect a number of contexts of '-ng-' that will
	 * take a 'hard' 'g' despite being followed by a
	 * front vowel.
	 * 
	 * @return true if 'hard' 'g' detected, false if not
	 * 
	 */
	boolean Internal_Hard_NG()
	{
		if ((StringAt((mCurrent - 3), 4, "DANG", "FANG", "SING", "") 
			// exception to exception
					&& !StringAt((mCurrent - 5), 8, "DISINGEN", "") )
			|| StringAt(0, 5, "INGEB", "ENGEB", "")
			|| (StringAt((mCurrent - 3), 4, "RING", "WING", "HANG", "LONG", "")
					&& !(StringAt((mCurrent - 4), 5, "CRING", "FRING", "ORANG", "TWING", "CHANG", "PHANG", "")
						|| StringAt((mCurrent - 5), 6, "SYRING", "") 
						|| StringAt((mCurrent - 3), 7, "RINGENC", "RINGENT", "LONGITU", "LONGEVI", "") 
						// e.g. 'longino', 'mastrangelo'
						|| (StringAt(mCurrent, 4, "GELO", "GINO", "") && ((mCurrent + 3) == mLast))))
			|| (StringAt((mCurrent - 1), 3, "NGY", "")
			// exceptions to exception
					&& !(StringAt((mCurrent - 3), 5, "RANGY", "MANGY", "MINGY", "")
						|| StringAt((mCurrent - 4), 6, "SPONGY", "STINGY", ""))))
		{
			return true;
		}

		return false;
	}
	
	/**
	 * Encode special case where "-GA-" => J
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_GA_To_J()
	{
		// 'margary', 'margarine'
		if ((StringAt((mCurrent - 3), 7, "MARGARY", "MARGARI", "")
			// but not in spanish forms such as "margatita"
			&& !StringAt((mCurrent - 3), 8, "MARGARIT", ""))
			|| StringAt(0, 4, "GAOL", "")
			|| StringAt((mCurrent - 2), 5, "ALGAE", ""))
		{
			MetaphAddExactApprox("J", "G", "J", "K");
			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode 'H'
	 * 
	 * 
	 */
	void Encode_H()
	{
		if (Encode_Initial_Silent_H()
			|| Encode_Initial_HS()
			|| Encode_Initial_HU_HW()
			|| Encode_Non_Initial_Silent_H())
		{
			return;
		}

		//only keep if first & before vowel or btw. 2 vowels
		if (!Encode_H_Pronounced())
		{
			//also takes care of 'HH'
			mCurrent++;
		}
	}

	/**
	 * Encode cases where initial 'H' is not pronounced (in American)
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Initial_Silent_H()
	{
		//'hour', 'herb', 'heir', 'honor'
		if (StringAt((mCurrent + 1), 3, "OUR", "ERB", "EIR", "")
			|| StringAt((mCurrent + 1), 4, "ONOR", "")
			|| StringAt((mCurrent + 1), 5, "ONOUR", "ONEST", ""))
		{
			// british pronounce H in this word
			// americans give it 'H' for the name,
			// no 'H' for the plant
			if ((mCurrent == 0) && StringAt(mCurrent, 4, "HERB", ""))
			{
				if (mEncodeVowels)
				{
					MetaphAdd("HA", "A");
				}
				else
				{
					MetaphAdd("H", "A");
				}
			}
			else if ((mCurrent == 0) || mEncodeVowels)
			{
				MetaphAdd("A");
			}

			mCurrent++;
			// don't encode vowels twice
			mCurrent = SkipVowels(mCurrent);
			return true;
		}

		return false;
	}

	/**
	 * Encode "HS-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Initial_HS()
	{
		// old chinese pinyin transliteration
		// e.g., 'HSIAO'
		if ((mCurrent == 0) && StringAt(0, 2, "HS", ""))
		{
			MetaphAdd("X");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode cases where "HU-" is pronounced as part of a vowel dipthong
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Initial_HU_HW()
	{
		// spanish spellings and chinese pinyin transliteration
		if (StringAt(0, 3, "HUA", "HUE", "HWA", ""))
		{
			if (!StringAt(mCurrent, 4, "HUEY", ""))
			{
				MetaphAdd("A");

				if (!mEncodeVowels)
				{
					mCurrent += 3;
				}
				else
				{
					mCurrent++;
					// don't encode vowels twice
					while (IsVowel(mCurrent) || (CharAt(mCurrent) == 'W'))
					{
						mCurrent++;
					}
				}
				return true;
			}
		}

		return false;
	}

	/**
	 * Encode cases where 'H' is silent between vowels
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Non_Initial_Silent_H()
	{
		//exceptions - 'h' not pronounced
		// "PROHIB" BUT NOT "PROHIBIT"
		if (StringAt((mCurrent - 2), 5, "NIHIL", "VEHEM", "LOHEN", "NEHEM", 
										"MAHON", "MAHAN", "COHEN", "GAHAN", "")
			|| StringAt((mCurrent - 3), 6, "GRAHAM", "PROHIB", "FRAHER", 
											"TOOHEY", "TOUHEY", "")
			|| StringAt((mCurrent - 3), 5, "TOUHY", "")
			|| StringAt(0, 9, "CHIHUAHUA", ""))
		{
			if (!mEncodeVowels)
			{
				mCurrent += 2;
			}
			else
			{
				mCurrent++;
				// don't encode vowels twice
				mCurrent = SkipVowels(mCurrent);
			}
			return true;
		}

		return false;
	}

	/**
	 * Encode cases where 'H' is pronounced
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_H_Pronounced()
	{
		if ((((mCurrent == 0) 
				|| IsVowel(mCurrent - 1) 
				|| ((mCurrent > 0) 
					&& (CharAt(mCurrent - 1) == 'W'))) 
			&& IsVowel(mCurrent + 1))
			// e.g. 'alWahhab'
			|| ((CharAt(mCurrent + 1) == 'H') && IsVowel(mCurrent + 2))) 
		{
			MetaphAdd("H");
			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode 'J'
	 * 
	 */
	void Encode_J()
	{
		if (Encode_Spanish_J()
			|| Encode_Spanish_OJ_UJ())
		{
			return;
		}

		Encode_Other_J();
	}

	/**
	 * Encode cases where initial or medial "j" is in a spanish word or name
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Spanish_J()
	{
		//obvious spanish, e.g. "jose", "san jacinto"
		if ((StringAt((mCurrent + 1), 3, "UAN", "ACI", "ALI", "EFE", "ICA", "IME", "OAQ", "UAR", "")
				&& !StringAt(mCurrent, 8, "JIMERSON", "JIMERSEN", ""))
			|| (StringAt((mCurrent + 1), 3, "OSE", "") && ((mCurrent + 3) == mLast))
			|| StringAt((mCurrent + 1), 4, "EREZ", "UNTA", "AIME", "AVIE", "AVIA", "")
			|| StringAt((mCurrent + 1), 6, "IMINEZ", "ARAMIL", "")
			|| (((mCurrent + 2) == mLast) && StringAt((mCurrent - 2), 5, "MEJIA", ""))
			|| StringAt((mCurrent - 2), 5, "TEJED", "TEJAD", "LUJAN", "FAJAR", "BEJAR", "BOJOR", "CAJIG", 
											"DEJAS", "DUJAR", "DUJAN", "MIJAR", "MEJOR", "NAJAR", 
											"NOJOS", "RAJED", "RIJAL", "REJON", "TEJAN", "UIJAN", "")
			|| StringAt((mCurrent - 3), 8, "ALEJANDR", "GUAJARDO", "TRUJILLO", "")
			|| (StringAt((mCurrent - 2), 5, "RAJAS", "") && (mCurrent > 2))
			|| (StringAt((mCurrent - 2), 5, "MEJIA", "") && !StringAt((mCurrent - 2), 6, "MEJIAN", ""))
			|| StringAt((mCurrent - 1), 5, "OJEDA", "")
			|| StringAt((mCurrent - 3), 5, "LEIJA", "MINJA", "")
			|| StringAt((mCurrent - 3), 6, "VIAJES", "GRAJAL", "")
			|| StringAt(mCurrent, 8, "JAUREGUI", "")
			|| StringAt((mCurrent - 4), 8, "HINOJOSA", "")
			|| StringAt(0, 4, "SAN ", "") 
			|| (((mCurrent + 1) == mLast)
			&& (CharAt(mCurrent + 1) == 'O')
			// exceptions
			&& !(StringAt(0, 4, "TOJO", "") 
					|| StringAt(0, 5, "BANJO", "") 
					|| StringAt(0, 6, "MARYJO", ""))))
		{
			// americans pronounce "juan" as 'wan'
			// and "marijuana" and "tijuana" also
			// do not get the 'H' as in spanish, so
			// just treat it like a vowel in these cases
			if (!(StringAt(mCurrent, 4, "JUAN", "") || StringAt(mCurrent, 4, "JOAQ", "")))
			{
				MetaphAdd("H");
			}
			else
			{
				if (mCurrent == 0)
				{
					MetaphAdd("A");
				}
			}
			AdvanceCounter(2, 1);
			return true;
		}
		
		// Jorge gets 2nd HARHA. also JULIO, JESUS
		if (StringAt((mCurrent + 1), 4, "ORGE", "ULIO", "ESUS", "")
			&& !StringAt(0, 6, "JORGEN", ""))
		{
			// get both consonants for "jorge"
			if (((mCurrent + 4) == mLast) && StringAt((mCurrent + 1), 4, "ORGE", ""))
			{
				if (mEncodeVowels)
				{
					MetaphAdd("JARJ", "HARHA");				
				}
				else
				{
					MetaphAdd("JRJ", "HRH");
				}				
				AdvanceCounter(5, 5);
				return true;				
			}
			
			MetaphAdd("J", "H");			
			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode cases where 'J' is clearly in a german word or name
	 * that americans pronounce in the german fashion
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_German_J()
	{
		if (StringAt((mCurrent + 1), 2, "AH", "")
			|| (StringAt((mCurrent + 1), 5, "OHANN", "") && ((mCurrent + 5) == mLast))
			|| (StringAt((mCurrent + 1), 3, "UNG", "") && !StringAt((mCurrent + 1), 4, "UNGL", ""))
			|| StringAt((mCurrent + 1), 3, "UGO", ""))
		{
			MetaphAdd("A");
			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode "-JOJ-" and "-JUJ-" as spanish words
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Spanish_OJ_UJ()
	{
		if (StringAt((mCurrent + 1), 5, "OJOBA", "UJUY ", ""))
		{
			if (mEncodeVowels)
			{
				MetaphAdd("HAH");
			}
			else
			{
				MetaphAdd("HH");
			}

			AdvanceCounter(4, 3);
			return true;
		}

		return false;
	}

	/**
	 * Encode 'J' => J
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_J_To_J()
	{
		if (IsVowel(mCurrent + 1))
		{
			if ((mCurrent == 0) 
				&& Names_Beginning_With_J_That_Get_Alt_Y())
			{
				// 'Y' is a vowel so encode
				// is as 'A'
				if (mEncodeVowels)
				{
					MetaphAdd("JA", "A");
				}
				else
				{
					MetaphAdd("J", "A");
				}
			}
			else
			{
				if (mEncodeVowels)
				{
					MetaphAdd("JA");
				}
				else
				{
					MetaphAdd("J");
				}
			}

			mCurrent++;
			mCurrent = SkipVowels(mCurrent);
			return false;
		}
		else
		{
			MetaphAdd("J");
			mCurrent++;
			return true;
		}

//		return false;
	}

	/**
	 * Encode 'J' toward end in spanish words
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Spanish_J_2()
	{
		// spanish forms e.g. "brujo", "badajoz"
		if ((((mCurrent - 2) == 0) 
			&& StringAt((mCurrent - 2), 4, "BOJA", "BAJA", "BEJA", "BOJO", "MOJA", "MOJI", "MEJI", ""))
			|| (((mCurrent - 3) == 0)
			&& StringAt((mCurrent - 3), 5, "FRIJO", "BRUJO", "BRUJA", "GRAJE", "GRIJA", "LEIJA", "QUIJA", ""))
			|| (((mCurrent + 3) == mLast) 
			&& StringAt((mCurrent - 1), 5, "AJARA", ""))
			|| (((mCurrent + 2) == mLast) 
			&& StringAt((mCurrent - 1), 4, "AJOS", "EJOS", "OJAS", "OJOS", "UJON", "AJOZ", "AJAL", "UJAR", "EJON", "EJAN", ""))
			|| (((mCurrent + 1) == mLast) 
			&& (StringAt((mCurrent - 1), 3, "OJA", "EJA", "") && !StringAt(0, 4, "DEJA", ""))))
		{
			MetaphAdd("H");
			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode 'J' as vowel in some exception cases
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_J_As_Vowel()
	{
		if (StringAt(mCurrent, 5, "JEWSK", ""))
		{
			MetaphAdd("J", "");
			return true;
		}
		
		// e.g. "stijl", "sejm" - dutch, scandanavian, and eastern european spellings
		if ((StringAt((mCurrent + 1), 1, "L", "T", "K", "S", "N", "M", "")
				// except words from hindi and arabic
				&& !StringAt((mCurrent + 2), 1, "A", ""))
			|| StringAt(0, 9, "HALLELUJA", "LJUBLJANA", "")
			|| StringAt(0, 4, "LJUB", "BJOR", "")
			|| StringAt(0, 5, "HAJEK", "")
			|| StringAt(0, 3, "WOJ", "")
			// e.g. 'fjord'
			|| StringAt(0, 2, "FJ", "")
			// e.g. 'rekjavik', 'blagojevic'
			|| StringAt(mCurrent, 5, "JAVIK", "JEVIC", "")
			|| (((mCurrent + 1) == mLast) && StringAt(0, 5, "SONJA", "TANJA", "TONJA", "")))

		{
			return true;
		}
		return false;
	}

	/**
	 * Call routines to encode 'J', in proper order
	 * 
	 */
	void Encode_Other_J()
	{
		if (mCurrent == 0)
		{
			if (Encode_German_J())
			{
				return;
			}
			else
			{
				if (Encode_J_To_J())
				{
					return;
				}
			}
		}
		else
		{
			if (Encode_Spanish_J_2())
			{
				return;
			}
			else if (!Encode_J_As_Vowel())
			{
				MetaphAdd("J");
			}
			
			//it could happen! e.g. "hajj"
			// eat redundant 'J'
			if (CharAt(mCurrent + 1) == 'J')
			{
				mCurrent += 2;
			}
			else
			{
				mCurrent++;
			}
		}
	}

	/**
	 * Encode 'K'
	 * 
	 * 
	 */
	void Encode_K()
	{
		if (!Encode_Silent_K())
		{
			MetaphAdd("K");

			// eat redundant 'K's and 'Q's
			if ((CharAt(mCurrent + 1) == 'K')
				|| (CharAt(mCurrent + 1) == 'Q'))
			{
				mCurrent += 2;
			}
			else
			{
				mCurrent++;
			}
		}
	}

	/**
	 * Encode cases where 'K' is not pronounced
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Silent_K()
	{
	    //skip this except for special cases
	    if ((mCurrent == 0)
			&& StringAt(mCurrent, 2, "KN", ""))
	    {
	        if (!(StringAt((mCurrent + 2), 5, "ESSET", "IEVEL", "") || StringAt((mCurrent + 2), 3, "ISH", "") ))
	        {
	            mCurrent += 1;
				return true;
	        }
	    }

		// e.g. "know", "knit", "knob"	
		if ((StringAt((mCurrent + 1), 3, "NOW", "NIT", "NOT", "NOB", "")
				// exception, "slipknot" => SLPNT but "banknote" => PNKNT
				&& !StringAt(0, 8, "BANKNOTE", ""))
			|| StringAt((mCurrent + 1), 4, "NOCK", "NUCK", "NIFE", "NACK", "")
			|| StringAt((mCurrent + 1), 5, "NIGHT", ""))
		{
			// N already encoded before
			// e.g. "penknife"
			if ((mCurrent > 0) && CharAt(mCurrent - 1) == 'N')
			{
				mCurrent += 2;
			}
			else
			{
				mCurrent++;
			}

			return true;
		}
		
		return false;
	}

	/**
	 * Encode 'L'
	 *
	 * Includes special vowel transposition 
	 * encoding, where 'LE' => AL
	 * 
	 */
	void Encode_L()
	{
		// logic below needs to know this
		// after 'mCurrent' variable changed 
		int save_current = mCurrent;

		Interpolate_Vowel_When_Cons_L_At_End();
		
		if (Encode_LELY_To_L()
			|| Encode_COLONEL()
			|| Encode_French_AULT()
			|| Encode_French_EUIL()
			|| Encode_French_OULX()
			|| Encode_Silent_L_In_LM()
			|| Encode_Silent_L_In_LK_LV()
			|| Encode_Silent_L_In_OULD())
		{
			return;
		}

		if (Encode_LL_As_Vowel_Cases())
		{
			return;
		}

		Encode_LE_Cases(save_current);
	}

	/**
	 * Cases where an L follows D, G, or T at the
	 * end have a schwa pronounced before the L
	 * 
	 */
	void Interpolate_Vowel_When_Cons_L_At_End()
	{
		if (mEncodeVowels == true)
		{
			// e.g. "ertl", "vogl"
			if ((mCurrent == mLast) 
				&& StringAt((mCurrent - 1), 1, "D", "G", "T", ""))
			{
				MetaphAdd("A");
			}
		}
	}
	
	/**
	 * Catch cases where 'L' spelled twice but pronounced
	 * once, e.g., 'DOCILELY' => TSL
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_LELY_To_L()
	{
		// e.g. "agilely", "docilely"
		if (StringAt((mCurrent - 1), 5, "ILELY", "")
			&& ((mCurrent + 3) == mLast))
		{
			MetaphAdd("L");
			mCurrent += 3;
			return true;
		}

		return false;
	}

	/**
	 * Encode special case "colonel" => KRNL. Can somebody tell
	 * me how this pronounciation came to be?
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_COLONEL()
	{
		if (StringAt((mCurrent - 2), 7, "COLONEL", ""))
		{
			MetaphAdd("R");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-AULT-", found in a french names
	 * 
	 * @return true if encoding handled in this routine, false if not
	 *  
	 */
	boolean Encode_French_AULT()
	{
		// e.g. "renault" and "foucault", well known to americans, but not "fault"
		if ((mCurrent > 3)
			&& (StringAt((mCurrent - 3), 5, "RAULT", "NAULT", "BAULT", "SAULT", "GAULT", "CAULT", "")
				|| StringAt((mCurrent - 4), 6, "REAULT", "RIAULT", "NEAULT", "BEAULT", ""))
			&& !(RootOrInflections(mInWord, "ASSAULT") 
				|| StringAt((mCurrent - 8), 10, "SOMERSAULT", "")
				|| StringAt((mCurrent - 9), 11, "SUMMERSAULT", "")))
		{
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-EUIL-", always found in a french word
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_French_EUIL()
	{
		// e.g. "auteuil"
		if (StringAt((mCurrent - 3), 4, "EUIL", "") && (mCurrent == mLast))
		{
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-OULX", always found in a french word
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_French_OULX()
	{
		// e.g. "proulx"
		if (StringAt((mCurrent - 2), 4, "OULX", "") && ((mCurrent + 1) == mLast))
		{
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encodes contexts where 'L' is not pronounced in "-LM-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Silent_L_In_LM()
	{
		if (StringAt(mCurrent, 2, "LM", "LN", ""))
		{
			// e.g. "lincoln", "holmes", "psalm", "salmon"
			if ((StringAt((mCurrent - 2), 4, "COLN", "CALM", "BALM", "MALM", "PALM", "")
				|| (StringAt((mCurrent - 1), 3, "OLM", "") && ((mCurrent + 1) == mLast))
				|| StringAt((mCurrent - 3), 5, "PSALM", "QUALM", "")
				|| StringAt((mCurrent - 2), 6,  "SALMON", "HOLMES", "")
				|| StringAt((mCurrent - 1), 6,  "ALMOND", "")
				|| ((mCurrent == 1) && StringAt((mCurrent - 1), 4, "ALMS", "") ))
				&& (!StringAt((mCurrent + 2), 1, "A", "") 
					&& !StringAt((mCurrent - 2), 5, "BALMO", "")
					&& !StringAt((mCurrent - 2), 6, "PALMER", "PALMOR", "BALMER", "")
					&& !StringAt((mCurrent - 3), 5, "THALM", "")))
			{
				mCurrent++;
				return true;
			}
			else
			{
				MetaphAdd("L");
				mCurrent++;
				return true;
			}
		}

		return false;
	}

	/**
	 * Encodes contexts where '-L-' is silent in 'LK', 'LV'
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Silent_L_In_LK_LV()
	{
		if ((StringAt((mCurrent - 2), 4, "WALK", "YOLK", "FOLK", "HALF", "TALK", "CALF", "BALK", "CALK", "")
			|| (StringAt((mCurrent - 2), 4, "POLK", "") 
				&& !StringAt((mCurrent - 2), 5, "POLKA", "WALKO", ""))
			|| (StringAt((mCurrent - 2), 4, "HALV", "") 
				&& !StringAt((mCurrent - 2), 5, "HALVA", "HALVO", ""))
			|| (StringAt((mCurrent - 3), 5, "CAULK", "CHALK", "BAULK", "FAULK", "")
				&& !StringAt((mCurrent - 4), 6, "SCHALK", ""))
			|| (StringAt((mCurrent - 2), 5, "SALVE", "CALVE", "")
			|| StringAt((mCurrent - 2), 6, "SOLDER", ""))
			// exceptions to above cases where 'L' is usually pronounced
			&& !StringAt((mCurrent - 2), 6, "SALVER", "CALVER", ""))
			&& !StringAt((mCurrent - 5), 9, "GONSALVES", "GONCALVES", "")
			&& !StringAt((mCurrent - 2), 6, "BALKAN", "TALKAL", "")
			&& !StringAt((mCurrent - 3), 5, "PAULK", "CHALF", ""))
		{
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * Encode 'L' in contexts of "-OULD-" where it is silent
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Silent_L_In_OULD()
	{
		//'would', 'could'
		if (StringAt((mCurrent - 3), 5, "WOULD", "COULD", "") 
			|| (StringAt((mCurrent - 4), 6, "SHOULD", "") 
				&& !StringAt((mCurrent - 4), 8, "SHOULDER", "")))
		{
			MetaphAddExactApprox("D", "T");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-ILLA-" and "-ILLE-" in spanish and french
	 * contexts were americans know to pronounce it as a 'Y'
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_LL_As_Vowel_Special_Cases()
	{
		if (StringAt((mCurrent - 5), 8, "TORTILLA", "") 
			|| StringAt((mCurrent - 8), 11, "RATATOUILLE", "")
			// e.g. 'guillermo', "veillard"
			|| (StringAt(0, 5, "GUILL", "VEILL", "GAILL", "")
				// 'guillotine' usually has '-ll-' pronounced as 'L' in english 
				&& !(StringAt((mCurrent - 3), 7, "GUILLOT", "GUILLOR", "GUILLEN", "")
					|| (StringAt(0, 5, "GUILL", "") && (mLength == 5))))
			// e.g. "brouillard", "gremillion"
			|| StringAt(0, 7, "BROUILL", "GREMILL", "ROBILL", "")
			// e.g. 'mireille'
			|| (StringAt((mCurrent - 2), 5, "EILLE", "") 
					&& ((mCurrent + 2) == mLast)
					// exception "reveille" usually pronounced as 're-vil-lee'
					&& !StringAt((mCurrent - 5), 8, "REVEILLE", "")))
		{
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode other spanish cases where "-LL-" is pronounced as 'Y'
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_LL_As_Vowel()
	{
		//spanish e.g. "cabrillo", "gallegos" but also "gorilla", "ballerina" -
		// give both pronounciations since an american might pronounce "cabrillo"
		// in the spanish or the american fashion.
		if ((((mCurrent + 3) == mLength) 
			&& StringAt((mCurrent - 1), 4, "ILLO", "ILLA", "ALLE", ""))
			|| (((StringAt((mLast - 1), 2, "AS", "OS", "")
					|| StringAt(mLast, 2, "AS", "OS", "")
					|| StringAt(mLast, 1, "A", "O", "")) 
						&& StringAt((mCurrent - 1), 2, "AL", "IL", ""))
				&& !StringAt((mCurrent - 1), 4, "ALLA", ""))
			|| StringAt(0, 5, "VILLE", "VILLA", "")
			|| StringAt(0, 8, "GALLARDO", "VALLADAR", "MAGALLAN", "CAVALLAR", "BALLASTE", "")
			|| StringAt(0, 3, "LLA", ""))
		{
			MetaphAdd("L", "");
			mCurrent += 2;
			return true;
		}
		return false;
	}

	/**
	 * Call routines to encode "-LL-", in proper order
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_LL_As_Vowel_Cases()
	{
		if (CharAt(mCurrent + 1) == 'L')
		{
			if (Encode_LL_As_Vowel_Special_Cases())
			{
				return true;
			}
			else if (Encode_LL_As_Vowel())
			{
				return true;
			}
			mCurrent += 2;

		}
		else
		{
			mCurrent++;
		}

		return false;
	}

	/**
	 * Encode vowel-encoding cases where "-LE-" is pronounced "-EL-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Vowel_LE_Transposition(int save_current)
	{
		// transposition of vowel sound and L occurs in many words,
		// e.g. "bristle", "dazzle", "goggle" => KAKAL
		if (mEncodeVowels 
			&& (save_current > 1) 
			&& !IsVowel(save_current - 1) 
			&& (CharAt(save_current + 1) == 'E')
			&& (CharAt(save_current - 1) != 'L')
			&& (CharAt(save_current - 1) != 'R')
			// lots of exceptions to this:
			&& !IsVowel(save_current + 2)
			&& !StringAt(0, 7, "ECCLESI", "COMPLEC", "COMPLEJ", "ROBLEDO", "")
			&& !StringAt(0, 5, "MCCLE", "MCLEL", "")
			&& !StringAt(0, 6, "EMBLEM", "KADLEC", "")
			&& !(((save_current + 2) == mLast) && StringAt(save_current, 3, "LET", ""))
			&& !StringAt(save_current, 7, "LETTING", "")
			&& !StringAt(save_current, 6, "LETELY", "LETTER", "LETION", "LETIAN", "LETING", "LETORY", "")
			&& !StringAt(save_current, 5, "LETUS", "LETIV", "")
			&& !StringAt(save_current, 4, "LESS", "LESQ", "LECT", "LEDG", "LETE", "LETH", "LETS", "LETT", "")
			&& !StringAt(save_current, 3, "LEG", "LER", "LEX", "")
			// e.g. "complement" !=> KAMPALMENT
			&& !(StringAt(save_current, 6, "LEMENT", "")
				&& !(StringAt((mCurrent - 5), 6, "BATTLE", "TANGLE", "PUZZLE", "RABBLE", "BABBLE", "")
						|| StringAt((mCurrent - 4), 5, "TABLE", "")))
			&& !(((save_current + 2) == mLast) && StringAt((save_current - 2), 5, "OCLES", "ACLES", "AKLES", ""))
			&& !StringAt((save_current - 3), 5, "LISLE", "AISLE", "")
			&& !StringAt(0, 4, "ISLE", "")
			&& !StringAt(0, 6, "ROBLES", "")
			&& !StringAt((save_current - 4), 7, "PROBLEM", "RESPLEN", "")
			&& !StringAt((save_current - 3), 6, "REPLEN", "")
			&& !StringAt((save_current - 2), 4, "SPLE", "")
			&& (CharAt(save_current - 1) != 'H')
			&& (CharAt(save_current - 1) != 'W'))
		{
			MetaphAdd("AL");
			flagAlInversion = true;

			// eat redundant 'L'
			if (CharAt(save_current + 2) == 'L')
			{
				mCurrent = save_current + 3;
			}
			return true;
		}

		return false;
	}

	/**
	 * Encode special vowel-encoding cases where 'E' is not
	 * silent at the end of a word as is the usual case
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Vowel_Preserve_Vowel_After_L(int save_current)
	{
		// an example of where the vowel would NOT need to be preserved
		// would be, say, "hustled", where there is no vowel pronounced
		// between the 'l' and the 'd'
		if (mEncodeVowels
			&& !IsVowel(save_current - 1) 
			&& (CharAt(save_current + 1) == 'E')
			&& (save_current > 1) 
			&& ((save_current + 1) != mLast) 
			&& !(StringAt((save_current + 1), 2, "ES", "ED", "") 
			&& ((save_current + 2) == mLast))
			&& !StringAt((save_current - 1), 5, "RLEST", "") )
		{
			MetaphAdd("LA");
			mCurrent = SkipVowels(mCurrent);
			return true;
		}

		return false;
	}

	/**
	 * Call routines to encode "-LE-", in proper order
	 *
	 * @param save_current index of actual current letter
	 *
	 */
	void Encode_LE_Cases(int save_current)
	{
		if (Encode_Vowel_LE_Transposition(save_current))
		{
			return;
		}
		else
		{
			if (Encode_Vowel_Preserve_Vowel_After_L(save_current))
			{
				return;
			}
			else
			{
				MetaphAdd("L");
			}
		}
	}

	/**
	 * Encode "-M-"
	 * 
	 */
	void Encode_M()
	{
		if (Encode_Silent_M_At_Beginning()
			|| Encode_MR_And_MRS()
			|| Encode_MAC()
			|| Encode_MPT())
		{
			return;
		}

		// Silent 'B' should really be handled
		// under 'B", not here under 'M'!
		Encode_MB();

		MetaphAdd("M");
	}

	/**
	 * Encode cases where 'M' is silent at beginning of word
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Silent_M_At_Beginning()
	{    
		//skip these when at start of word
	    if ((mCurrent == 0)
			&& StringAt(mCurrent, 2, "MN", ""))
		{
	        mCurrent += 1;
			return true;
		}

		return false;
	}

	/**
	 * Encode special cases "Mr." and "Mrs."
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_MR_And_MRS()
	{
		if ((mCurrent == 0) && StringAt(mCurrent, 2, "MR", ""))
		{
			// exceptions for "mr." and "mrs."
			if ((mLength == 2) && StringAt(mCurrent, 2, "MR", ""))
			{
				if (mEncodeVowels)
				{
					MetaphAdd("MASTAR");
				}
				else
				{
					MetaphAdd("MSTR");
				}
				mCurrent += 2;
				return true;
			}
			else if ((mLength == 3) && StringAt(mCurrent, 3, "MRS", "")) 
			{
				if (mEncodeVowels)
				{
					MetaphAdd("MASAS");
				}
				else
				{
					MetaphAdd("MSS");
				}
				mCurrent += 3;
				return true;
			}
		}

		return false;
	}

	/**
	 * Encode "Mac-" and "Mc-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_MAC()
	{
		// should only find irish and 
		// scottish names e.g. 'macintosh'
		if ((mCurrent == 0) 
				&& (StringAt(0, 7, "MACIVER", "MACEWEN", "")
				|| StringAt(0, 8, "MACELROY", "MACILROY", "")
				||  StringAt(0, 9, "MACINTOSH", "")
				|| StringAt(0, 2, "MC", "")	))
		{
			if (mEncodeVowels)
			{
				MetaphAdd("MAK");
			}
			else
			{
				MetaphAdd("MK");
			}
			
			if (StringAt(0, 2, "MC", ""))
			{	
				if (StringAt((mCurrent + 2), 1, "K", "G", "Q", "")
					// watch out for e.g. "McGeorge"
					&& !StringAt((mCurrent + 2), 4, "GEOR", ""))
				{
					mCurrent += 3;				
				}
				else
				{
					mCurrent += 2;
				}
			}
			else
			{	
				mCurrent += 3;
			}
				
			return true;
		}

		return false;
	}

	/**
	 * Encode silent 'M' in context of "-MPT-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_MPT()
	{
		if (StringAt((mCurrent - 2), 8, "COMPTROL", "")
			|| StringAt((mCurrent - 4), 7, "ACCOMPT", ""))

		{
			MetaphAdd("N");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Test if 'B' is silent in these contexts
	 * 
	 * @return true if 'B' is silent in this context
	 * 
	 */
	boolean Test_Silent_MB_1()
	{
		// e.g. "LAMB", "COMB", "LIMB", "DUMB", "BOMB"
		// Handle combining roots first
		if (((mCurrent == 3) 
				&& StringAt((mCurrent - 3), 5, "THUMB", ""))
			|| ((mCurrent == 2)
				&& StringAt((mCurrent - 2), 4, "DUMB", "BOMB", "DAMN", "LAMB", "NUMB", "TOMB", "") ))
		{
			return true;
		}

		return false;
	}

	/**
	 * Test if 'B' is pronounced in this context
	 * 
	 * @return true if 'B' is pronounced in this context
	 * 
	 */
	boolean Test_Pronounced_MB()
	{
		if (StringAt((mCurrent - 2), 6, "NUMBER", "")
			|| (StringAt((mCurrent + 2), 1, "A", "") 
				&& !StringAt((mCurrent - 2), 7, "DUMBASS", ""))
			|| StringAt((mCurrent + 2), 1, "O", "") 
			|| StringAt((mCurrent - 2), 6, "LAMBEN", "LAMBER", "LAMBET", "TOMBIG", "LAMBRE", ""))
		{
			return true;
		}

		return false;
	}

	/**
	 * Test whether "-B-" is silent in these contexts
	 * 
	 * @return true if 'B' is silent in this context
	 * 
	 */
	boolean Test_Silent_MB_2()
	{
		// 'M' is the current letter
		if ((CharAt(mCurrent + 1) == 'B') && (mCurrent > 1)
			&& (((mCurrent + 1) == mLast)
			// other situations where "-MB-" is at end of root
			// but not at end of word. The tests are for standard
			// noun suffixes.
			// e.g. "climbing" => KLMNK
			|| StringAt((mCurrent + 2), 3, "ING", "ABL", "")
			|| StringAt((mCurrent + 2), 4, "LIKE", "")
			|| ((CharAt(mCurrent + 2) == 'S') && ((mCurrent + 2) == mLast))
			|| StringAt((mCurrent - 5), 7, "BUNCOMB", "")
			// e.g. "bomber", 
			|| (StringAt((mCurrent + 2), 2, "ED", "ER", "") 
			&& ((mCurrent + 3) == mLast) 
			&& (StringAt(0, 5, "CLIMB", "PLUMB", "")
			// e.g. "beachcomber"
			|| !StringAt((mCurrent - 1), 5, "IMBER", "AMBER", "EMBER", "UMBER", ""))
			// exceptions
			&& !StringAt((mCurrent - 2), 6, "CUMBER", "SOMBER", "") ) ) )
		{
			return true;
		}

		return false;
	}

	/**
	 * Test if 'B' is pronounced in these "-MB-" contexts
	 * 
	 * @return true if "-B-" is pronounced in these contexts
	 * 
	 */
	boolean Test_Pronounced_MB_2()
	{
		// e.g. "bombastic", "umbrage", "flamboyant"
		if (StringAt((mCurrent - 1), 5, "OMBAS", "OMBAD", "UMBRA", "")
			|| StringAt((mCurrent - 3), 4, "FLAM", "") )
		{
			return true;
		}

		return false;
	}

	/**
	 * Tests for contexts where "-N-" is silent when after "-M-"
	 * 
	 * @return true if "-N-" is silent in these contexts
	 * 
	 */
	boolean Test_MN()
	{

		if ((CharAt(mCurrent + 1) == 'N') 
			&& (((mCurrent + 1) == mLast)
			// or at the end of a word but followed by suffixes
			|| (StringAt((mCurrent + 2), 3, "ING", "EST", "") && ((mCurrent + 4) == mLast))
			|| ((CharAt(mCurrent + 2) == 'S') && ((mCurrent + 2) == mLast))
			|| (StringAt((mCurrent + 2), 2, "LY", "ER", "ED", "") 
				&& ((mCurrent + 3) == mLast))  
			|| StringAt((mCurrent - 2), 9, "DAMNEDEST", "")
			|| StringAt((mCurrent - 5), 9, "GODDAMNIT", "") ))
		{
			return true; 
		}

		return false;
	}

	/**
	 * Call routines to encode "-MB-", in proper order
	 * 
	 */
	void Encode_MB()
	{
		if (Test_Silent_MB_1())
		{
			if (Test_Pronounced_MB())
			{
				mCurrent++;
			}
			else
			{
				mCurrent += 2; 
			}
		}
		else if (Test_Silent_MB_2())
		{
			if (Test_Pronounced_MB_2())
			{
				mCurrent++;
			}
			else
			{
				mCurrent += 2; 
			}
		}
		else if (Test_MN())
		{
			mCurrent += 2; 
		}
		else
		{
			// eat redundant 'M'
			if (CharAt(mCurrent + 1) == 'M')
			{
				mCurrent += 2;
			}
			else
			{
				mCurrent++;
			}
		}
	}

	/**
	 * Encode "-N-"
	 * 
	 */
	void Encode_N()
	{
		if (Encode_NCE())
		{
			return;
		}

		// eat redundant 'N'
		if (CharAt(mCurrent + 1) == 'N')
		{
			mCurrent += 2;
		}
		else
		{
			mCurrent++;
		}

		if (!StringAt((mCurrent - 3), 8, "MONSIEUR", "")
			// e.g. "aloneness", 
			&& !StringAt((mCurrent - 3), 6, "NENESS", ""))
		{
			MetaphAdd("N");
		}
	}

	/**
	 * Encode "-NCE-" and "-NSE-"
	 * "entrance" is pronounced exactly the same as "entrants"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_NCE()
	{
		//'acceptance', 'accountancy'
		if (StringAt((mCurrent + 1), 1, "C", "S", "")
			&& StringAt((mCurrent + 2), 1, "E", "Y", "I", "")
			&& (((mCurrent + 2) == mLast)
				|| (((mCurrent + 3) == mLast)) 
					&& (CharAt(mCurrent + 3) == 'S')))
		{
			MetaphAdd("NTS");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-P-"
	 * 
	 */
	void Encode_P()
	{
		if (Encode_Silent_P_At_Beginning()
		   || Encode_PT()
		   || Encode_PH()
		   || Encode_PPH()
		   || Encode_RPS()
		   || Encode_COUP()
		   || Encode_PNEUM()
		   || Encode_PSYCH()
		   || Encode_PSALM())
		{
			return;
		}

		Encode_PB();

		MetaphAdd("P");
	}

	/**
	 * Encode cases where "-P-" is silent at the start of a word
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Silent_P_At_Beginning()
	{
	    //skip these when at start of word
	    if ((mCurrent == 0)
			&& StringAt(mCurrent, 2, "PN", "PF", "PS", "PT", ""))
		{
	        mCurrent += 1;
			return true;
		}

		return false;
	}

	/**
	 * Encode cases where "-P-" is silent before "-T-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_PT()
	{
		// 'pterodactyl', 'receipt', 'asymptote'
		if ((CharAt(mCurrent + 1) == 'T'))
		{
			if (((mCurrent == 0) && StringAt(mCurrent, 5, "PTERO", ""))
				|| StringAt((mCurrent - 5), 7, "RECEIPT", "")
				|| StringAt((mCurrent - 4), 8, "ASYMPTOT", ""))
			{
				MetaphAdd("T");
				mCurrent += 2;
				return true;
			}
		}
		return false;
	}

	/**
	 * Encode "-PH-", usually as F, with exceptions for
	 * cases where it is silent, or where the 'P' and 'T'
	 * are pronounced seperately because they belong to 
	 * two different words in a combining form
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_PH()
	{
		if (CharAt(mCurrent + 1) == 'H')
		{
			// 'PH' silent in these contexts
			if (StringAt(mCurrent, 9, "PHTHALEIN", "")
				|| ((mCurrent == 0) && StringAt(mCurrent, 4, "PHTH", ""))
				|| StringAt((mCurrent - 3), 10, "APOPHTHEGM", ""))
			{
				MetaphAdd("0");
				mCurrent += 4;
			}
			// combining forms
			//'sheepherd', 'upheaval', 'cupholder'
			else if ((mCurrent > 0)
				&& (StringAt((mCurrent + 2), 3, "EAD", "OLE", "ELD", "ILL", "OLD", "EAP", "ERD", 
												 "ARD", "ANG", "ORN", "EAV", "ART", "")
					|| StringAt((mCurrent + 2), 4, "OUSE", "")
					|| (StringAt((mCurrent + 2), 2, "AM", "") && !StringAt((mCurrent - 1), 5, "LPHAM", ""))
					|| StringAt((mCurrent + 2), 5, "AMMER", "AZARD", "UGGER", "")
					|| StringAt((mCurrent + 2), 6, "OLSTER", ""))
						&& !StringAt((mCurrent - 3), 5, "LYMPH", "NYMPH", ""))
			{
				MetaphAdd("P");
				AdvanceCounter(3, 2);
			}
			else
			{
				MetaphAdd("F");
				mCurrent += 2;
			}
			return true;
		}

		return false;
	}

	/**
	 * Encode "-PPH-". I don't know why the greek poet's
	 * name is transliterated this way...
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_PPH()
	{
		// 'sappho'
		if ((CharAt(mCurrent + 1) == 'P') 
				&& ((mCurrent + 2) < mLength) && (CharAt(mCurrent + 2) == 'H'))
		{
			MetaphAdd("F");
			mCurrent += 3;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-CORPS-" where "-PS-" not pronounced
	 * since the cognate is here from the french
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_RPS()
	{
		//'-corps-', 'corpsman'
		if (StringAt((mCurrent - 3), 5, "CORPS", "") 
			&& !StringAt((mCurrent - 3), 6, "CORPSE", ""))
		{
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-COUP-" where "-P-" is not pronounced
	 * since the word is from the french
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_COUP()
	{
		//'coup'
		if ((mCurrent == mLast) 
			&& StringAt((mCurrent - 3), 4, "COUP", "")
			&& !StringAt((mCurrent - 5), 6, "RECOUP", ""))
		{
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * Encode 'P' in non-initial contexts of "-PNEUM-" 
	 * where is also silent
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_PNEUM()
	{
		//'-pneum-'
		if (StringAt((mCurrent + 1), 4, "NEUM", ""))
		{
			MetaphAdd("N");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode special case "-PSYCH-" where two encodings need to be
	 * accounted for in one syllable, one for the 'PS' and one for
	 * the 'CH'
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_PSYCH()
	{
		//'-psych-'
		if (StringAt((mCurrent + 1), 4, "SYCH", ""))
		{
			if (mEncodeVowels)
			{
				MetaphAdd("SAK");
			}
			else
			{
				MetaphAdd("SK");
			}

			mCurrent += 5;
			return true;
		}

		return false;
	}

	/**
	 * Encode 'P' in context of "-PSALM-", where it has
	 * become silent
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_PSALM()
	{
		//'-psalm-'
		if (StringAt((mCurrent + 1), 4, "SALM", ""))
		{
			// go ahead and encode entire word
			if (mEncodeVowels)
			{
				MetaphAdd("SAM");
			}
			else
			{
				MetaphAdd("SM");
			}

			mCurrent += 5;
			return true;
		}

		return false;
	}

	/**
	 * Eat redundant 'B' or 'P'
	 *
	 */
	void Encode_PB()
	{
		// e.g. "campbell", "raspberry"
		// eat redundant 'P' or 'B'
		if (StringAt((mCurrent + 1), 1, "P", "B", ""))
		{
			mCurrent += 2;
		}
		else
		{
			mCurrent++;
		}
	}

	/**
	 * Encode "-Q-"
	 * 
	 */
	void Encode_Q()
	{
		// current pinyin
		if (StringAt(mCurrent, 3, "QIN", ""))
		{
			MetaphAdd("X");
			mCurrent++;
			return;
		}

		// eat redundant 'Q'
		if (CharAt(mCurrent + 1) == 'Q')
		{
			mCurrent += 2;
		}
		else
		{
			mCurrent++;
		}

		MetaphAdd("K");
	}

	/**
	 * Encode "-R-"
	 * 
	 */
	void Encode_R()
	{
		if (Encode_RZ())
		{
			return;
		}
		
		if (!Test_Silent_R())
		{
			if (!Encode_Vowel_RE_Transposition())
			{
				MetaphAdd("R");
			}
		}
		
		// eat redundant 'R'; also skip 'S' as well as 'R' in "poitiers"
		if ((CharAt(mCurrent + 1) == 'R') || StringAt((mCurrent - 6), 8, "POITIERS", ""))
		{
			mCurrent += 2;
		}
		else
		{
			mCurrent++;
		}
	}
	
	/**
	 * Encode "-RZ-" according
	 * to american and polish pronunciations
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_RZ()
	{
		if (StringAt((mCurrent - 2), 4, "GARZ", "KURZ", "MARZ", "MERZ", "HERZ", "PERZ", "WARZ", "")
			|| StringAt(mCurrent, 5, "RZANO", "RZOLA", "")
			|| StringAt((mCurrent - 1), 4, "ARZA", "ARZN", ""))
		{
			return false;
		}
		
		// 'yastrzemski' usually has 'z' silent in
		// united states, but should get 'X' in poland
		if (StringAt((mCurrent - 4), 11, "YASTRZEMSKI", ""))
		{
			MetaphAdd("R", "X");
			mCurrent += 2;
			return true;
		}
		// 'BRZEZINSKI' gets two pronunciations
		// in the united states, neither of which
		// are authentically polish
		if (StringAt((mCurrent - 1), 10, "BRZEZINSKI", ""))
		{
			MetaphAdd("RS", "RJ");
			// skip over 2nd 'Z'
			mCurrent += 4;
			return true;
		}
		// 'z' in 'rz after voiceless consonant gets 'X'
		// in alternate polish style pronunciation
		else if (StringAt((mCurrent - 1), 3, "TRZ", "PRZ", "KRZ", "")
				|| (StringAt(mCurrent, 2, "RZ", "")
						&& (IsVowel(mCurrent - 1) || (mCurrent == 0))))
		{
			MetaphAdd("RS", "X");
			mCurrent += 2;
			return true;
		}
		// 'z' in 'rz after voiceled consonant, vowel, or at
		// beginning gets 'J' in alternate polish style pronunciation
		else if (StringAt((mCurrent - 1), 3, "BRZ", "DRZ", "GRZ", ""))
		{
			MetaphAdd("RS", "J");
			mCurrent += 2;
			return true;
		}

		return false;
	}
	
	/**
	 * Test whether 'R' is silent in this context
	 *
	 * @return true if 'R' is silent in this context
	 * 
	 */
	boolean Test_Silent_R()
	{
		// test cases where 'R' is silent, either because the 
		// word is from the french or because it is no longer pronounced.
		// e.g. "rogier", "monsieur", "surburban"
		if (((mCurrent == mLast)
			// reliably french word ending
			&& StringAt((mCurrent - 2), 3, "IER", "")
			// e.g. "metier"
			&& (StringAt((mCurrent - 5), 3, "MET", "VIV", "LUC", "")
			// e.g. "cartier", "bustier"
			|| StringAt((mCurrent - 6), 4, "CART", "DOSS", "FOUR", "OLIV", "BUST", "DAUM", "ATEL", 
											"SONN", "CORM", "MERC", "PELT", "POIR", "BERN", "FORT", "GREN", 
											"SAUC", "GAGN", "GAUT", "GRAN", "FORC", "MESS", "LUSS", "MEUN", 
											"POTH", "HOLL", "CHEN", "")
			// e.g. "croupier"
			|| StringAt((mCurrent - 7), 5, "CROUP", "TORCH", "CLOUT", "FOURN", "GAUTH", "TROTT", 
											"DEROS", "CHART", "")
			// e.g. "chevalier"
			|| StringAt((mCurrent - 8), 6, "CHEVAL", "LAVOIS", "PELLET", "SOMMEL", "TREPAN", "LETELL", "COLOMB", "")
			|| StringAt((mCurrent - 9), 7, "CHARCUT", "")
			|| StringAt((mCurrent - 10), 8, "CHARPENT", "")))
			|| StringAt((mCurrent - 2), 7, "SURBURB", "WORSTED", "")
			|| StringAt((mCurrent - 2), 9, "WORCESTER", "")
			|| StringAt((mCurrent - 7), 8, "MONSIEUR", "")
			|| StringAt((mCurrent - 6), 8, "POITIERS", "") )
		{
			return true;
		}

		return false;
	}

	/**
	 * Encode '-re-" as 'AR' in contexts
	 * where this is the correct pronunciation
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Vowel_RE_Transposition()
	{
		// -re inversion is just like
		// -le inversion
		// e.g. "fibre" => FABAR or "centre" => SANTAR
		if ((mEncodeVowels)
			&& (CharAt(mCurrent + 1) == 'E')
			&& (mLength > 3)
			&& !StringAt(0, 5, "OUTRE", "LIBRE", "ANDRE", "")
			&& !(StringAt(0, 4, "FRED", "TRES", "") && (mLength == 4))
			&& !StringAt((mCurrent - 2), 5, "LDRED", "LFRED", "NDRED", "NFRED", "NDRES", "TRES", "IFRED", "")
			&& !IsVowel(mCurrent - 1)
			&& (((mCurrent + 1) == mLast)
				|| (((mCurrent + 2) == mLast) 
						&& StringAt((mCurrent + 2), 1, "D", "S", ""))))
		{
			MetaphAdd("AR");
			return true;
		}

		return false;
	}

	/**
	 * Encode "-S-"
	 * 
	 */
	void Encode_S()
	{
		if (Encode_SKJ()
			|| Encode_Special_SW()
			|| Encode_SJ()
			|| Encode_Silent_French_S_Final()
			|| Encode_Silent_French_S_Internal()
			|| Encode_ISL()
			|| Encode_STL()
			|| Encode_Christmas()
			|| Encode_STHM()
			|| Encode_ISTEN()
			|| Encode_Sugar()
			|| Encode_SH()
			|| Encode_SCH()
			|| Encode_SUR()
			|| Encode_SU()
			|| Encode_SSIO()
			|| Encode_SS()
			|| Encode_SIA()
			|| Encode_SIO()
			|| Encode_Anglicisations()
			|| Encode_SC()
			|| Encode_SEA_SUI_SIER()
			|| Encode_SEA())
		{
			return;
		}

		MetaphAdd("S");

		if (StringAt((mCurrent + 1), 1, "S", "Z", "")
			&& !StringAt((mCurrent + 1), 2, "SH", ""))
		{
			mCurrent += 2;
		}
		else
		{
			mCurrent++;
		}
	}

	/**
	 * Encode a couple of contexts where scandinavian, slavic
	 * or german names should get an alternate, native 
	 * pronunciation of 'SV' or 'XV'
	 * 
	 * @return true if handled
	 * 
	 */
	boolean Encode_Special_SW()
	{
		if (mCurrent == 0)
		{
			// 
			if (Names_Beginning_With_SW_That_Get_Alt_SV())
			{
				MetaphAdd("S", "SV");
				mCurrent += 2;
				return true;
			}
	
			// 
			if (Names_Beginning_With_SW_That_Get_Alt_XV())
			{
				MetaphAdd("S", "XV");
				mCurrent += 2;
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Encode "-SKJ-" as X ("sh"), since americans pronounce
	 * the name Dag Hammerskjold as "hammer-shold"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_SKJ()
	{
		// scandinavian
		if (StringAt(mCurrent, 4, "SKJO", "SKJU", "")
			&& IsVowel(mCurrent + 3))
		{
			MetaphAdd("X");
			mCurrent += 3;
			return true;
		}

		return false;
	}

	/**
	 * Encode initial swedish "SJ-" as X ("sh")
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_SJ()
	{
		if (StringAt(0, 2, "SJ", ""))
		{
			MetaphAdd("X");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode final 'S' in words from the french, where they
	 * are not pronounced
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Silent_French_S_Final()
	{
		// "louis" is an exception because it gets two pronuncuations
		if (StringAt(0, 5, "LOUIS", "") && (mCurrent == mLast))
		{
			MetaphAdd("S", "");
			mCurrent++;
			return true;			
		}
				
		// french words familiar to americans where final s is silent
		if ((mCurrent == mLast) 
			&& (StringAt(0, 4, "YVES", "")
			|| (StringAt(0, 4, "HORS", "") && (mCurrent == 3))
			|| StringAt((mCurrent - 4), 5, "CAMUS", "YPRES", "")
			|| StringAt((mCurrent - 5), 6, "MESNES", "DEBRIS", "BLANCS", "INGRES", "CANNES", "")
			|| StringAt((mCurrent - 6), 7, "CHABLIS", "APROPOS", "JACQUES", "ELYSEES", "OEUVRES", 
											"GEORGES", "DESPRES", "")
			|| StringAt(0, 8, "ARKANSAS", "FRANCAIS", "CRUDITES", "BRUYERES", "")
			|| StringAt(0, 9, "DESCARTES", "DESCHUTES", "DESCHAMPS", "DESROCHES", "DESCHENES", "")
			|| StringAt(0, 10, "RENDEZVOUS", "")
			|| StringAt(0, 11, "CONTRETEMPS", "DESLAURIERS", ""))
			|| ((mCurrent == mLast) 
					&& StringAt((mCurrent - 2), 2, "AI", "OI", "UI", "") 
					&& !StringAt(0, 4, "LOIS", "LUIS", "")))
		{
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * Encode non-final 'S' in words from the french where they
	 * are not pronounced.
	 * 
	 * @return true if encoding handled in this routine, false if not
	 *
	 */
	boolean Encode_Silent_French_S_Internal()
	{
		// french words familiar to americans where internal s is silent
		if (StringAt((mCurrent - 2), 9, "DESCARTES", "")
			|| StringAt((mCurrent - 2), 7, "DESCHAM", "DESPRES", "DESROCH", "DESROSI", "DESJARD", "DESMARA", 
						"DESCHEN", "DESHOTE", "DESLAUR", "")
			|| StringAt((mCurrent - 2), 6, "MESNES", "")
			|| StringAt((mCurrent - 5), 8, "DUQUESNE", "DUCHESNE", "")
			|| StringAt((mCurrent - 7), 10, "BEAUCHESNE", "")
			|| StringAt((mCurrent - 3), 7, "FRESNEL", "")
			|| StringAt((mCurrent - 3), 9, "GROSVENOR", "")
			|| StringAt((mCurrent - 4), 10, "LOUISVILLE", "")
			|| StringAt((mCurrent - 7), 10, "ILLINOISAN", ""))
		{
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * Encode silent 'S' in context of "-ISL-"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_ISL()
	{
		//special cases 'island', 'isle', 'carlisle', 'carlysle'
		if ((StringAt((mCurrent - 2), 4, "LISL", "LYSL", "AISL", "") 
			&& !StringAt((mCurrent - 3), 7, "PAISLEY", "BAISLEY", "ALISLAM", "ALISLAH", "ALISLAA", ""))
			|| ((mCurrent == 1) 
				&& ((StringAt((mCurrent - 1), 4, "ISLE", "") 
					|| StringAt((mCurrent - 1), 5, "ISLAN", ""))
					&& !StringAt((mCurrent - 1), 5, "ISLEY", "ISLER", ""))))
		{
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-STL-" in contexts where the 'T' is silent. Also
	 * encode "-USCLE-" in contexts where the 'C' is silent
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_STL()
	{
		//'hustle', 'bustle', 'whistle'
		if ((StringAt(mCurrent, 4, "STLE", "STLI", "") 
				&& !StringAt((mCurrent + 2), 4, "LESS", "LIKE", "LINE", ""))
			|| StringAt((mCurrent - 3), 7, "THISTLY", "BRISTLY",  "GRISTLY", "")
			// e.g. "corpuscle"
			|| StringAt((mCurrent - 1), 5, "USCLE", ""))
		{
			// KRISTEN, KRYSTLE, CRYSTLE, KRISTLE all pronounce the 't'
			// also, exceptions where "-LING" is a nominalizing suffix
			if (StringAt(0, 7, "KRISTEN", "KRYSTLE", "CRYSTLE", "KRISTLE", "")
				|| StringAt(0, 11, "CHRISTENSEN", "CHRISTENSON", "")
				|| StringAt((mCurrent - 3), 9, "FIRSTLING", "")
				|| StringAt((mCurrent - 2), 8,  "NESTLING",  "WESTLING", ""))
			{
				MetaphAdd("ST");
				mCurrent += 2;
			}
			else
			{
				if (mEncodeVowels 
					&& (CharAt(mCurrent + 3) == 'E') 
					&& (CharAt(mCurrent + 4) != 'R')
					&& !StringAt((mCurrent + 3), 4, "ETTE", "ETTA", "")
					&& !StringAt((mCurrent + 3), 2, "EY", ""))
				{
					MetaphAdd("SAL");
					flagAlInversion = true;			
				}
				else
				{
					MetaphAdd("SL");
				}
				mCurrent += 3;
			}
			return true;
		}

		return false;
	}

	/**
	 * Encode "christmas". Americans always pronounce this as "krissmuss"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Christmas()
	{
		//'christmas'
		if (StringAt((mCurrent - 4), 8, "CHRISTMA", ""))
		{
			MetaphAdd("SM");
			mCurrent += 3;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-STHM-" in contexts where the 'TH'
	 * is silent.
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_STHM()
	{
		//'asthma', 'isthmus'
		if (StringAt(mCurrent, 4, "STHM", ""))
		{
			MetaphAdd("SM");
			mCurrent += 4;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-ISTEN-" and "-STNT-" in contexts
	 * where the 'T' is silent
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_ISTEN()
	{
		// 't' is silent in verb, pronounced in name
		if (StringAt(0, 8, "CHRISTEN", ""))
		{
			// the word itself
			if (RootOrInflections(mInWord, "CHRISTEN")
				|| StringAt(0, 11, "CHRISTENDOM", ""))
			{
				MetaphAdd("S", "ST");
			}
			else
			{
				// e.g. 'christenson', 'christene'				
				MetaphAdd("ST");				
			}
			mCurrent += 2;
			return true;
		}
		
		//e.g. 'glisten', 'listen'
		if (StringAt((mCurrent - 2), 6, "LISTEN", "RISTEN", "HASTEN", "FASTEN", "MUSTNT", "")
			|| StringAt((mCurrent - 3), 7,  "MOISTEN", ""))
		{
			MetaphAdd("S");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode special case "sugar"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Sugar()
	{
		//special case 'sugar-'
		if (StringAt(mCurrent, 5, "SUGAR", ""))
		{
			MetaphAdd("X");
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-SH-" as X ("sh"), except in cases
	 * where the 'S' and 'H' belong to different combining
	 * roots and are therefore pronounced seperately
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_SH()
	{
		if (StringAt(mCurrent, 2, "SH", ""))
		{
			// exception
			if (StringAt((mCurrent - 2), 8, "CASHMERE", ""))
			{
				MetaphAdd("J");				
				mCurrent += 2;
				return true;
			}
			
			//combining forms, e.g. 'clotheshorse', 'woodshole'
			if ((mCurrent > 0)
				// e.g. "mishap"
				&& ((StringAt((mCurrent + 1), 3, "HAP", "") && ((mCurrent + 3) == mLast))
				// e.g. "hartsheim", "clothshorse"
				|| StringAt((mCurrent + 1), 4, "HEIM", "HOEK", "HOLM", "HOLZ", "HOOD", "HEAD", "HEID", 
											    "HAAR", "HORS", "HOLE", "HUND", "HELM", "HAWK", "HILL", "")
				// e.g. "dishonor"
				|| StringAt((mCurrent + 1), 5, "HEART", "HATCH", "HOUSE", "HOUND", "HONOR", "")
				// e.g. "mishear"
				|| (StringAt((mCurrent + 2), 3, "EAR", "") && ((mCurrent + 4) == mLast))
				// e.g. "hartshorn"
				|| (StringAt((mCurrent + 2), 3, "ORN", "") && !StringAt((mCurrent - 2), 7, "UNSHORN", ""))
				// e.g. "newshour" but not "bashour", "manshour"
				|| (StringAt((mCurrent + 1), 4, "HOUR", "") 
					&& !(StringAt(0, 7, "BASHOUR", "") || StringAt(0, 8, "MANSHOUR", "") || StringAt(0, 6, "ASHOUR", "") ))
				// e.g. "dishonest", "grasshopper"
				|| StringAt((mCurrent + 2), 5, "ARMON", "ONEST", "ALLOW", "OLDER", "OPPER", "EIMER", "ANDLE", "ONOUR", "")
				// e.g. "dishabille", "transhumance"
				|| StringAt((mCurrent + 2), 6, "ABILLE", "UMANCE", "ABITUA", "")))
			{
				if (!StringAt((mCurrent - 1), 1, "S", ""))
					MetaphAdd("S");
			}
			else
			{
				MetaphAdd("X");
			}

			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-SCH-" in cases where the 'S' is pronounced
	 * seperately from the "CH", in words from the dutch, italian,
	 * and greek where it can be pronounced SK, and german words
	 * where it is pronounced X ("sh")
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_SCH()
	{
		// these words were combining forms many centuries ago
		if (StringAt((mCurrent + 1), 2, "CH", ""))
		{
			if ((mCurrent > 0)
				// e.g. "mischief", "escheat"
				&& (StringAt((mCurrent + 3), 3, "IEF", "EAT", "")
				// e.g. "mischance"
				|| StringAt((mCurrent + 3), 4, "ANCE", "ARGE", "")
				// e.g. "eschew"
				|| StringAt(0, 6, "ESCHEW", "")))
			{
				MetaphAdd("S");
				mCurrent++;
				return true;
			}

			//Schlesinger's rule
			//dutch, danish, italian, greek origin, e.g. "school", "schooner", "schiavone", "schiz-"
			if ((StringAt((mCurrent + 3), 2, "OO", "ER", "EN", "UY", "ED", "EM", "IA", "IZ", "IS", "OL", "")
					&& !StringAt(mCurrent, 6, "SCHOLT", "SCHISL", "SCHERR", ""))
				|| StringAt((mCurrent + 3), 3, "ISZ", "")
				|| (StringAt((mCurrent - 1), 6, "ESCHAT", "ASCHIN", "ASCHAL", "ISCHAE", "ISCHIA", "")
						&& !StringAt((mCurrent - 2), 8, "FASCHING", ""))
				|| (StringAt((mCurrent - 1), 5, "ESCHI", "")  && ((mCurrent + 3) == mLast))
				|| (CharAt(mCurrent + 3) == 'Y'))
			{
				// e.g. "schermerhorn", "schenker", "schistose"
				if (StringAt((mCurrent + 3), 2, "ER", "EN", "IS", "")
					&& (((mCurrent + 4) == mLast) 
						|| StringAt((mCurrent + 3), 3, "ENK", "ENB", "IST", "")))
				{
					MetaphAdd("X", "SK");
				}
				else
				{
					MetaphAdd("SK");
				}
				mCurrent += 3;
				return true;
			}
			else
			{	
				MetaphAdd("X");
				mCurrent += 3;
				return true;
			}
		}

		return false;
	}

	/**
	 * Encode "-SUR<E,A,Y>-" to J, unless it is at the beginning,
	 * or preceeded by 'N', 'K', or "NO"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_SUR()
	{
		// 'erasure', 'usury'
		if (StringAt((mCurrent + 1), 3, "URE", "URA", "URY", ""))
		{
			//'sure', 'ensure'
			if ((mCurrent == 0)
				|| StringAt((mCurrent - 1), 1, "N", "K", "")
				|| StringAt((mCurrent - 2), 2, "NO", ""))
			{
				MetaphAdd("X");
			}
			else
			{
				MetaphAdd("J");
			}

			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode "-SU<O,A>-" to X ("sh") unless it is preceeded by
	 * an 'R', in which case it is encoded to S, or it is
	 * preceeded by a vowel, in which case it is encoded to J
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_SU()
	{
		//'sensuous', 'consensual'
		if (StringAt((mCurrent + 1), 2, "UO", "UA", "") && (mCurrent != 0))
		{
			// exceptions e.g. "persuade"
			if (StringAt((mCurrent - 1), 4, "RSUA", ""))
			{
				MetaphAdd("S");
			}
			// exceptions e.g. "casual"
			else if (IsVowel(mCurrent - 1))
			{
				MetaphAdd("J", "S");
			}
			else
			{
				MetaphAdd("X", "S");
			}

			AdvanceCounter(3, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encodes "-SSIO-" in contexts where it is pronounced
	 * either J or X ("sh")
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_SSIO()
	{
		if (StringAt((mCurrent + 1), 4, "SION", ""))
		{
			//"abcission"
			if (StringAt((mCurrent - 2), 2, "CI", ""))
			{
				MetaphAdd("J");
			}
			//'mission'
			else
			{
				if (IsVowel(mCurrent - 1))
				{
					MetaphAdd("X");
				}
			}

			AdvanceCounter(4, 2);
			return true;
		}

		return false;
	}

	/**
	 * Encode "-SS-" in contexts where it is pronounced X ("sh")
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_SS()
	{
		// e.g. "russian", "pressure"
		if (StringAt((mCurrent - 1), 5, "USSIA", "ESSUR", "ISSUR", "ISSUE", "")
			// e.g. "hessian", "assurance"
			|| StringAt((mCurrent - 1), 6, "ESSIAN", "ASSURE", "ASSURA", "ISSUAB", "ISSUAN", "ASSIUS", ""))
		{
			MetaphAdd("X");
			AdvanceCounter(3, 2);
			return true;
		}

		return false;
	}

	/**
	 * Encodes "-SIA-" in contexts where it is pronounced
	 * as X ("sh"), J, or S
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_SIA()
	{
		// e.g. "controversial", also "fuchsia", "ch" is silent
		if (StringAt((mCurrent - 2), 5, "CHSIA", "")
			|| StringAt((mCurrent - 1), 5, "RSIAL", ""))
		{
			MetaphAdd("X");
			AdvanceCounter(3, 1);
			return true;
		}
		
		// names generally get 'X' where terms, e.g. "aphasia" get 'J'
		if ((StringAt(0, 6, "ALESIA", "ALYSIA", "ALISIA", "STASIA", "")
				&& (mCurrent == 3)
				&& !StringAt(0, 9, "ANASTASIA", ""))
			|| StringAt((mCurrent - 5), 9, "DIONYSIAN", "")
			|| StringAt((mCurrent - 5), 8, "THERESIA", ""))
		{
			MetaphAdd("X", "S");
			AdvanceCounter(3, 1);
			return true;
		}

		if ((StringAt(mCurrent, 3, "SIA", "") && ((mCurrent + 2) == mLast)) 
			|| (StringAt(mCurrent, 4, "SIAN", "") && ((mCurrent + 3) == mLast))
			|| StringAt((mCurrent - 5), 9, "AMBROSIAL", ""))
		{
			if ((IsVowel(mCurrent - 1) || StringAt((mCurrent - 1), 1, "R", ""))
				// exclude compounds based on names, or french or greek words
				&& !(StringAt(0, 5, "JAMES", "NICOS", "PEGAS", "PEPYS", "")
				|| StringAt(0, 6, "HOBBES", "HOLMES", "JAQUES", "KEYNES", "")
				|| StringAt(0, 7, "MALTHUS", "HOMOOUS", "")
				|| StringAt(0, 8, "MAGLEMOS", "HOMOIOUS", "")
				|| StringAt(0, 9, "LEVALLOIS", "TARDENOIS", "") 
				|| StringAt((mCurrent - 4), 5, "ALGES", "") ))
			{
				MetaphAdd("J");
			}
			else
			{
				MetaphAdd("S");
			}
			
			AdvanceCounter(2, 1);
			return true;
		}
		return false;
	}

	/**
	 * Encodes "-SIO-" in contexts where it is pronounced
	 * as J or X ("sh")
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_SIO()
	{
		// special case, irish name
		if (StringAt(0, 7, "SIOBHAN", ""))
		{
			MetaphAdd("X");
			AdvanceCounter(3, 1);
			return true;
		}
		
		if (StringAt((mCurrent + 1), 3, "ION", ""))
		{
			// e.g. "vision", "version"
			if (IsVowel(mCurrent - 1) || StringAt((mCurrent - 2), 2, "ER", "UR", ""))
			{
				MetaphAdd("J");
			}
			else // e.g. "declension"
			{
				MetaphAdd("X");
			}

			AdvanceCounter(3, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode cases where "-S-" might well be from a german name
	 * and add encoding of german pronounciation in alternate m_metaph
	 * so that it can be found in a genealogical search
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Anglicisations()
	{
		//german & anglicisations, e.g. 'smith' match 'schmidt', 'snider' match 'schneider'
		//also, -sz- in slavic language altho in hungarian it is pronounced 's'
		if (((mCurrent == 0) 
			&& StringAt((mCurrent + 1), 1, "M", "N", "L", ""))
			|| StringAt((mCurrent + 1), 1, "Z", ""))
		{
			MetaphAdd("S", "X");

			// eat redundant 'Z'
			if (StringAt((mCurrent + 1), 1, "Z", ""))
			{
				mCurrent += 2;
			}
			else
			{
				mCurrent++;
			}

			return true;
		}

		return false;
	}

	/**
	 * Encode "-SC<vowel>-" in contexts where it is silent,
	 * or pronounced as X ("sh"), S, or SK
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_SC()
	{
		if (StringAt(mCurrent, 2, "SC", ""))
		{
			// exception 'viscount'
			if (StringAt((mCurrent - 2), 8, "VISCOUNT", ""))
			{
				mCurrent += 1;
				return true;
			}

			// encode "-SC<front vowel>-"
			if (StringAt((mCurrent + 2), 1, "I", "E", "Y", ""))
			{
				// e.g. "conscious"
				if (StringAt((mCurrent + 2), 4, "IOUS", "") 
					// e.g. "prosciutto"
					|| StringAt((mCurrent + 2), 3, "IUT", "")
					|| StringAt((mCurrent - 4), 9, "OMNISCIEN", "")
					// e.g. "conscious"
					|| StringAt((mCurrent - 3), 8, "CONSCIEN", "CRESCEND", "CONSCION", "")
					|| StringAt((mCurrent - 2), 6, "FASCIS", ""))
				{
					MetaphAdd("X");
				}
				else if (StringAt(mCurrent, 7, "SCEPTIC", "SCEPSIS", "")
							|| StringAt(mCurrent, 5, "SCIVV", "SCIRO", "")
							// commonly pronounced this way in u.s.
							|| StringAt(mCurrent, 6, "SCIPIO", "")
							|| StringAt((mCurrent - 2), 10, "PISCITELLI", ""))
				{
					MetaphAdd("SK");
				}
				else
				{
					MetaphAdd("S");
				}
				mCurrent += 2;
				return true;
			}

			MetaphAdd("SK");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-S<EA,UI,IER>-" in contexts where it is pronounced
	 * as J
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_SEA_SUI_SIER()
	{
		// "nausea" by itself has => NJ as a more likely encoding. Other forms
		// using "nause-" (see Encode_SEA()) have X or S as more familiar pronounciations
		if ((StringAt((mCurrent - 3), 6, "NAUSEA", "") && ((mCurrent + 2) == mLast))
			// e.g. "casuistry", "frasier", "hoosier"
			|| StringAt((mCurrent - 2), 5, "CASUI", "")
			|| (StringAt((mCurrent - 1), 5, "OSIER", "ASIER", "")
					&& !(StringAt(0, 6, "EASIER", "") 
						|| StringAt(0, 5, "OSIER", "") 
						|| StringAt((mCurrent - 2), 6, "ROSIER", "MOSIER", ""))))
		{
			MetaphAdd("J", "X");
			AdvanceCounter(3, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode cases where "-SE-" is pronounced as X ("sh")
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_SEA()
	{
		if ((StringAt(0, 4, "SEAN", "") && ((mCurrent + 3) == mLast))
			|| (StringAt((mCurrent - 3), 6, "NAUSEO", "")
			&& !StringAt((mCurrent - 3), 7, "NAUSEAT", "")))
		{
			MetaphAdd("X");
			AdvanceCounter(3, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode "-T-"
	 * 
	 */
	void Encode_T()
	{
		if (Encode_T_Initial()
			|| Encode_TCH()
			|| Encode_Silent_French_T()
			|| Encode_TUN_TUL_TUA_TUO()
			|| Encode_TUE_TEU_TEOU_TUL_TIE()
			|| Encode_TUR_TIU_Suffixes()
			|| Encode_TI()
			|| Encode_TIENT()
			|| Encode_TSCH()
			|| Encode_TZSCH()
			|| Encode_TH_Pronounced_Separately()
			|| Encode_TTH()
			|| Encode_TH())
		{
			return;
		}

		// eat redundant 'T' or 'D'
		if (StringAt((mCurrent + 1), 1, "T", "D", ""))
		{
			mCurrent += 2;
		}
		else
		{
			mCurrent++;
		}

		MetaphAdd("T");
	}

	/**
	 * Encode some exceptions for initial 'T'
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_T_Initial()
	{
		if (mCurrent == 0)
		{
			// americans usually pronounce "tzar" as "zar"
			if (StringAt((mCurrent + 1), 3, "SAR", "ZAR", ""))
			{
				mCurrent++;
				return true;
			}

			// old 'École française d'Extrême-Orient' chinese pinyin where 'ts-' => 'X'
			if (((mLength == 3) && StringAt((mCurrent + 1), 2, "SO", "SA", "SU", ""))
				||	((mLength == 4) && StringAt((mCurrent + 1), 3, "SAO", "SAI", ""))
				||	((mLength == 5) && StringAt((mCurrent + 1), 4, "SING", "SANG", "")))
			{
				MetaphAdd("X");
				AdvanceCounter(3, 2);
				return true;
			}
			
			// "TS<vowel>-" at start can be pronounced both with and without 'T'
			if (StringAt((mCurrent + 1), 1, "S", "") && IsVowel(mCurrent + 2))
			{
				MetaphAdd("TS", "S");
				AdvanceCounter(3, 2);
				return true;
			}
			
			// e.g. "Tjaarda"
			if (StringAt((mCurrent + 1), 1, "J", ""))
			{
				MetaphAdd("X");
				AdvanceCounter(3, 2);
				return true;
			}

			// cases where initial "TH-" is pronounced as T and not 0 ("th")
			if ((StringAt((mCurrent + 1), 2, "HU", "") && (mLength == 3))
				|| StringAt((mCurrent + 1), 3, "HAI", "HUY", "HAO", "")
				|| StringAt((mCurrent + 1), 4, "HYME", "HYMY", "HANH", "")
				|| StringAt((mCurrent + 1), 5, "HERES", ""))
			{
				MetaphAdd("T");
				AdvanceCounter(3, 2);
				return true;
			}
		}

		return false;
	}

	/**
	 * Encode "-TCH-", reliably X ("sh", or in this case, "ch")
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_TCH()
	{
		if (StringAt((mCurrent + 1), 2, "CH", ""))
		{
			MetaphAdd("X");
			mCurrent += 3;
			return true;
		}

		return false;
	}

	/**
	 * Encode the many cases where americans are aware that a certain word is
	 * french and know to not pronounce the 'T'
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * TOUCHET CHABOT BENOIT
	 */
	boolean Encode_Silent_French_T()
	{
		// french silent T familiar to americans
		if (((mCurrent == mLast) && StringAt((mCurrent - 4), 5, "MONET", "GENET", "CHAUT", ""))
			|| StringAt((mCurrent - 2), 9, "POTPOURRI", "")
			|| StringAt((mCurrent - 3), 9, "BOATSWAIN", "")
			|| StringAt((mCurrent - 3), 8, "MORTGAGE", "")
			|| (StringAt((mCurrent - 4), 5, "BERET", "BIDET", "FILET", "DEBUT", "DEPOT", "PINOT", "TAROT", "")
			|| StringAt((mCurrent - 5), 6, "BALLET", "BUFFET", "CACHET", "CHALET", "ESPRIT", "RAGOUT", "GOULET",
											"CHABOT", "BENOIT", "")
			|| StringAt((mCurrent - 6), 7, "GOURMET", "BOUQUET", "CROCHET", "CROQUET", "PARFAIT", "PINCHOT", 
											"CABARET", "PARQUET", "RAPPORT", "TOUCHET", "COURBET", "DIDEROT", "")
			|| StringAt((mCurrent - 7), 8, "ENTREPOT", "CABERNET", "DUBONNET", "MASSENET", "MUSCADET", "RICOCHET", "ESCARGOT", "")
			|| StringAt((mCurrent - 8), 9, "SOBRIQUET", "CABRIOLET", "CASSOULET", "OUBRIQUET", "CAMEMBERT", ""))
			&& !StringAt((mCurrent + 1), 2, "AN", "RY", "IC", "OM", "IN", ""))
		{
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-TU<N,L,A,O>-" in cases where it is pronounced
	 * X ("sh", or in this case, "ch")
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_TUN_TUL_TUA_TUO()
	{
		// e.g. "fortune", "fortunate"               
		if (StringAt((mCurrent - 3), 6, "FORTUN", "")
			// e.g. "capitulate"
			|| (StringAt(mCurrent, 3, "TUL", "")
				&& (IsVowel(mCurrent - 1) && IsVowel(mCurrent + 3)))
			// e.g. "obituary", "barbituate"
			||  StringAt((mCurrent - 2), 5, "BITUA", "BITUE", "")
			// e.g. "actual"
			|| ((mCurrent > 1) && StringAt(mCurrent, 3, "TUA", "TUO", "")))
		{
			MetaphAdd("X", "T");
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-T<vowel>-" forms where 'T' is pronounced as X 
	 * ("sh", or in this case "ch")
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_TUE_TEU_TEOU_TUL_TIE()
	{
		// 'constituent', 'pasteur'
		if (StringAt((mCurrent + 1), 4, "UENT", "")
			|| StringAt((mCurrent - 4), 9, "RIGHTEOUS",  "")
			|| StringAt((mCurrent - 3), 7, "STATUTE",  "")
			|| StringAt((mCurrent - 3), 7, "AMATEUR",  "")
			// e.g. "blastula", "pasteur"
			|| (StringAt((mCurrent - 1), 5, "NTULE", "NTULA", "STULE", "STULA", "STEUR", ""))
			// e.g. "statue"
			|| (((mCurrent + 2) == mLast) && StringAt(mCurrent, 3, "TUE", ""))
			// e.g. "constituency"
			|| StringAt(mCurrent, 5, "TUENC", "")
			// e.g. "statutory"
			|| StringAt((mCurrent - 3), 8, "STATUTOR", "")
			// e.g. "patience"
			|| (((mCurrent + 5) == mLast) && StringAt(mCurrent, 6, "TIENCE", "")))
		{
			MetaphAdd("X", "T");
			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode "-TU-" forms in suffixes where it is usually
	 * pronounced as X ("sh")
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_TUR_TIU_Suffixes()
	{
		// 'adventure', 'musculature'
		if ((mCurrent > 0) && StringAt((mCurrent + 1), 3, "URE", "URA", "URI", "URY", "URO", "IUS", ""))
		{
			// exceptions e.g. 'tessitura', mostly from romance languages
			if ((StringAt((mCurrent + 1), 3, "URA", "URO", "") 
				//&& !StringAt((mCurrent + 1), 4, "URIA", "") 
				&& ((mCurrent + 3) == mLast))
				&& !StringAt((mCurrent - 3), 7, "VENTURA", "")
				// e.g. "kachaturian", "hematuria"
				|| StringAt((mCurrent + 1), 4, "URIA", ""))
			{
				MetaphAdd("T");
			}
			else
			{
				MetaphAdd("X", "T");
			}

			AdvanceCounter(2, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode "-TI<O,A,U>-" as X ("sh"), except
	 * in cases where it is part of a combining form,
	 * or as J
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_TI()
	{
		// '-tio-', '-tia-', '-tiu-'
		// except combining forms where T already pronounced e.g 'rooseveltian'
		if ((StringAt((mCurrent + 1), 2, "IO", "") && !StringAt((mCurrent - 1), 5, "ETIOL", ""))
			|| StringAt((mCurrent + 1), 3, "IAL", "")
			|| StringAt((mCurrent - 1), 5, "RTIUM", "ATIUM", "")
			|| ((StringAt((mCurrent + 1), 3, "IAN", "") && (mCurrent > 0))
					&& !(StringAt((mCurrent - 4), 8, "FAUSTIAN", "")
			|| StringAt((mCurrent - 5), 9, "PROUSTIAN", "")
			|| StringAt((mCurrent - 2), 7, "TATIANA", "")
			|| (StringAt((mCurrent - 3), 7, "KANTIAN", "GENTIAN", "")
			|| StringAt((mCurrent - 8), 12, "ROOSEVELTIAN", "")))
			|| (((mCurrent + 2) == mLast) 
					&& StringAt(mCurrent, 3, "TIA", "")
			// exceptions to above rules where the pronounciation is usually X
			&& !(StringAt((mCurrent - 3), 6, "HESTIA", "MASTIA", "")
				|| StringAt((mCurrent - 2), 5, "OSTIA", "")
				|| StringAt(0, 3, "TIA", "")
				|| StringAt((mCurrent - 5), 8, "IZVESTIA", "")))
			|| StringAt((mCurrent + 1), 4, "IATE", "IATI", "IABL", "IATO", "IARY", "")
			|| StringAt((mCurrent - 5), 9, "CHRISTIAN", "")))
		{
			if (((mCurrent == 2) && StringAt(0, 4, "ANTI", ""))
				|| StringAt(0, 5, "PATIO", "PITIA", "DUTIA", ""))
			{
				MetaphAdd("T");
			}
			else if (StringAt((mCurrent - 4), 8, "EQUATION", ""))
			{
				MetaphAdd("J");
			}
			else
			{
				if (StringAt(mCurrent, 4, "TION", ""))
				{
					MetaphAdd("X");
				}
				else if (StringAt(0, 5, "KATIA", "LATIA", ""))
				{
					MetaphAdd("T", "X");
				}
				else
				{
					MetaphAdd("X", "T");
				}
			}

			AdvanceCounter(3, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode "-TIENT-" where "TI" is pronounced X ("sh")
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_TIENT()
	{
		// e.g. 'patient'
		if (StringAt((mCurrent + 1), 4, "IENT", ""))
		{
			MetaphAdd("X", "T");
			AdvanceCounter(3, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode "-TSCH-" as X ("ch")
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_TSCH()
	{
		//'deutsch'
		if (StringAt(mCurrent, 4, "TSCH", "")
			// combining forms in german where the 'T' is pronounced seperately
			&& !StringAt((mCurrent - 3), 4, "WELT", "KLAT", "FEST", ""))
		{
			// pronounced the same as "ch" in "chit" => X
			MetaphAdd("X");
			mCurrent += 4;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-TZSCH-" as X ("ch")
	 * 
	 * "Neitzsche is peachy"
	 *
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_TZSCH()
	{
		//'neitzsche'
		if (StringAt(mCurrent, 5, "TZSCH", ""))
		{
			MetaphAdd("X");
			mCurrent += 5;
			return true;
		}

		return false;
	}

	/**
	 * Encodes cases where the 'H' in "-TH-" is the beginning of
	 * another word in a combining form, special cases where it is
	 * usually pronounced as 'T', and a special case where it has
	 * become pronounced as X ("sh", in this case "ch")
	 *
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_TH_Pronounced_Separately()
	{
		//'adulthood', 'bithead', 'apartheid'
		if (((mCurrent > 0) 
				&& StringAt((mCurrent + 1), 4, "HOOD", "HEAD", "HEID", "HAND", "HILL", "HOLD", 
												"HAWK", "HEAP", "HERD", "HOLE", "HOOK", "HUNT",   
												"HUMO", "HAUS", "HOFF", "HARD", "")
				&& !StringAt((mCurrent - 3), 5, "SOUTH", "NORTH", ""))
			|| StringAt((mCurrent + 1), 5, "HOUSE", "HEART", "HASTE", "HYPNO", "HEQUE", "")
			// watch out for greek root "-thallic"
			|| (StringAt((mCurrent + 1), 4, "HALL", "") 
				&& ((mCurrent + 4) == mLast)
				&& !StringAt((mCurrent - 3), 5, "SOUTH", "NORTH", "")) 
			|| (StringAt((mCurrent + 1), 3, "HAM", "") 
					&& ((mCurrent + 3) == mLast) 
					&& !(StringAt(0, 6, "GOTHAM", "WITHAM", "LATHAM", "")
						 || StringAt(0, 7, "BENTHAM", "WALTHAM", "WORTHAM", "")
						 || StringAt(0, 8, "GRANTHAM", "")))
			|| (StringAt((mCurrent + 1), 5, "HATCH", "")
			&& !((mCurrent == 0) || StringAt((mCurrent - 2), 8, "UNTHATCH", "")))
			|| StringAt((mCurrent - 3), 7, "WARTHOG", "")
			// and some special cases where "-TH-" is usually pronounced 'T'
			|| StringAt((mCurrent - 2), 6, "ESTHER", "")
			|| StringAt((mCurrent - 3), 6, "GOETHE", "")
			|| StringAt((mCurrent - 2), 8, "NATHALIE", ""))
		{
			// special case
			if (StringAt((mCurrent - 3), 7, "POSTHUM", ""))
			{
				MetaphAdd("X");
			}
			else
			{
				MetaphAdd("T");
			}
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode the "-TTH-" in "matthew", eating the redundant 'T'
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_TTH()
	{
		// 'matthew' vs. 'outthink'
		if (StringAt(mCurrent, 3, "TTH", ""))
		{
			if (StringAt((mCurrent - 2), 5, "MATTH", ""))
			{
				MetaphAdd("0");
			}
			else
			{
				MetaphAdd("T0");
			}
			mCurrent += 3;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-TH-". 0 (zero) is used in Metaphone to encode this sound
	 * when it is pronounced as a dipthong, either voiced or unvoiced
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_TH()
	{
		if (StringAt(mCurrent, 2, "TH", "") )
		{
			//'-clothes-'
			if (StringAt((mCurrent - 3), 7, "CLOTHES", ""))
			{
				// vowel already encoded so skip right to S
				mCurrent += 3;
				return true;
			}

			//special case "thomas", "thames", "beethoven" or germanic words
			if (StringAt((mCurrent + 2), 4, "OMAS", "OMPS", "OMPK", "OMSO", "OMSE", 
											"AMES", "OVEN", "OFEN", "ILDA", "ILDE", "") 
				|| (StringAt(0, 4, "THOM", "")  && (mLength == 4))
				|| (StringAt(0, 5, "THOMS", "")  && (mLength == 5))
				|| StringAt(0, 4, "VAN ", "VON ", "") 
				|| StringAt(0, 3, "SCH", ""))
			{
				MetaphAdd("T");

			}
			else
			{
				// give an 'etymological' 2nd
				// encoding for "smith"
				if (StringAt(0, 2, "SM", ""))
				{
					MetaphAdd("0", "T");
				}
				else
				{
					MetaphAdd("0");
				}
			}

			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-V-"
	 * 
	 */
	void Encode_V()
	{
		// eat redundant 'V'
		if (CharAt(mCurrent + 1) == 'V')
		{
			mCurrent += 2;
		}
		else
		{
			mCurrent++;
		}

		MetaphAddExactApprox("V", "F");
	}

	/**
	 * Encode "-W-"
	 * 
	 */
	void Encode_W()
	{
		if (Encode_Silent_W_At_Beginning()
			|| Encode_WITZ_WICZ()
			|| Encode_WR()
			|| Encode_Initial_W_Vowel()
			|| Encode_WH()
			|| Encode_Eastern_European_W())
		{
			return;
		}

		// e.g. 'zimbabwe'
		if (mEncodeVowels
			&& StringAt(mCurrent, 2, "WE", "")
			&& ((mCurrent + 1) == mLast))
		{
			MetaphAdd("A");
		}
		
		//else skip it
		mCurrent++;

	}

	/**
	 * Encode cases where 'W' is silent at beginning of word
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Silent_W_At_Beginning()
	{    
		//skip these when at start of word
	    if ((mCurrent == 0)
			&& StringAt(mCurrent, 2, "WR", ""))
		{
	        mCurrent += 1;
			return true;
		}

		return false;
	}

	/**
	 * Encode polish patronymic suffix, mapping
	 * alternate spellings to the same encoding,
	 * and including easern european pronounciation
	 * to the american so that both forms can
	 * be found in a genealogy search
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_WITZ_WICZ()
	{
		//polish e.g. 'filipowicz'
		if (((mCurrent + 3) == mLast) && StringAt(mCurrent, 4, "WICZ", "WITZ", ""))
		{
			if (mEncodeVowels)
			{
				if ((mPrimary.length() > 0)
					&& mPrimary.charAt(mPrimary.length() - 1) == 'A')
				{
					MetaphAdd("TS", "FAX");
				}
				else
				{
					MetaphAdd("ATS", "FAX");					
				}
			}
			else
			{
				MetaphAdd("TS", "FX");
			}
			mCurrent += 4;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-WR-" as R ('W' always effectively silent)
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_WR()
	{
		//can also be in middle of word
		if (StringAt(mCurrent, 2, "WR", ""))
		{
			MetaphAdd("R");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "W-", adding central and eastern european
	 * pronounciations so that both forms can be found
	 * in a genealogy search
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Initial_W_Vowel()
	{
		if ((mCurrent == 0) && IsVowel(mCurrent + 1))
		{
			//Witter should match Vitter
			if (Germanic_Or_Slavic_Name_Beginning_With_W())
			{
				if (mEncodeVowels)
				{
					MetaphAddExactApprox("A", "VA", "A", "FA");
				}
				else
				{
					MetaphAddExactApprox("A", "V", "A", "F");
				}
			}
			else
			{
				MetaphAdd("A");
			}

			mCurrent++;
			// don't encode vowels twice
			mCurrent = SkipVowels(mCurrent);
			return true;
		}

		return false;
	}

	/**
	 * Encode "-WH-" either as H, or close enough to 'U' to be
	 * considered a vowel
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_WH()
	{
		if (StringAt(mCurrent, 2, "WH", ""))
		{
			// cases where it is pronounced as H
			// e.g. 'who', 'whole'
			if ((CharAt(mCurrent + 2) == 'O')
				// exclude cases where it is pronounced like a vowel
				&& !(StringAt((mCurrent + 2), 4, "OOSH", "")
				|| StringAt((mCurrent + 2), 3, "OOP", "OMP", "ORL", "ORT", "")
				|| StringAt((mCurrent + 2), 2, "OA", "OP", "")))
			{
				MetaphAdd("H");
				AdvanceCounter(3, 2);
				return true;
			}
			else
			{
				// combining forms, e.g. 'hollowhearted', 'rawhide'
				if (StringAt((mCurrent + 2), 3, "IDE", "ARD", "EAD", "AWK", "ERD", 
												"OOK", "AND", "OLE", "OOD", "")
					|| StringAt((mCurrent + 2), 4, "EART", "OUSE", "OUND", "")
					|| StringAt((mCurrent + 2), 5, "AMMER", ""))
				{
					MetaphAdd("H");
					mCurrent += 2;
					return true;
				}
				else if (mCurrent == 0)
				{
					MetaphAdd("A");
					mCurrent += 2;
					// don't encode vowels twice
					mCurrent = SkipVowels(mCurrent);
					return true;
				}
			}
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-W-" when in eastern european names, adding
	 * the eastern european pronounciation to the american so
	 * that both forms can be found in a genealogy search
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Eastern_European_W()
	{
		//Arnow should match Arnoff
		if (((mCurrent == mLast) && IsVowel(mCurrent - 1)) 
			|| StringAt((mCurrent - 1), 5, "EWSKI", "EWSKY", "OWSKI", "OWSKY", "")
			|| (StringAt(mCurrent, 5, "WICKI", "WACKI", "") && ((mCurrent + 4) == mLast))
			|| StringAt(mCurrent, 4, "WIAK", "") && ((mCurrent + 3) == mLast)
			|| StringAt(0, 3, "SCH", ""))
		{
			MetaphAddExactApprox("", "V", "", "F");
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-X-"
	 * 
	 */
	void Encode_X()
	{
		if (Encode_Initial_X()
			|| Encode_Greek_X()
			|| Encode_X_Special_Cases()
			|| Encode_X_To_H()
			|| Encode_X_Vowel()
			|| Encode_French_X_Final())
		{
			return;
		}

		// eat redundant 'X' or other redundant cases
		if (StringAt((mCurrent + 1), 1, "X", "Z", "S", "")
			// e.g. "excite", "exceed"
			|| StringAt((mCurrent + 1), 2, "CI", "CE", ""))
		{
			mCurrent += 2;
		}
		else
		{
			mCurrent++;
		}
	}

	/**
	 * Encode initial X where it is usually pronounced as S
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Initial_X()
	{		
		// current chinese pinyin spelling
		if (StringAt(0, 3, "XIA", "XIO", "XIE", "")
			|| StringAt(0, 2, "XU", ""))
		{
			MetaphAdd("X");
			mCurrent++;
			return true;
		}
		
		// else
		if ((mCurrent == 0))
		{
			MetaphAdd("S");
			mCurrent++;
			return true;
		}

		return false;
	}
	
	/**
	 * Encode X when from greek roots where it is usually pronounced as S
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_Greek_X()
	{
		// 'xylophone', xylem', 'xanthoma', 'xeno-'
		if (StringAt((mCurrent + 1), 3, "YLO", "YLE", "ENO", "")
			|| StringAt((mCurrent + 1), 4, "ANTH", ""))
		{
			MetaphAdd("S");
			mCurrent++;
			return true;
		}

		return false;
	}
	
	/**
	 * Encode special cases, "LUXUR-", "Texeira"
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_X_Special_Cases()
	{
		// 'luxury'
		if (StringAt((mCurrent - 2), 5, "LUXUR", ""))
		{
			MetaphAddExactApprox("GJ", "KJ");
			mCurrent++;
			return true;
		}
		
		// 'texeira' portuguese/galician name
		if (StringAt(0, 7, "TEXEIRA", "")
			|| StringAt(0, 8, "TEIXEIRA", ""))
		{
			MetaphAdd("X");
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * Encode special case where americans know the
	 * proper mexican indian pronounciation of this name
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_X_To_H()
	{
		// TODO: look for other mexican indian words
		// where 'X' is usually pronounced this way
		if (StringAt((mCurrent - 2), 6, "OAXACA", "")
			|| StringAt((mCurrent - 3), 7, "QUIXOTE", ""))
		{
			MetaphAdd("H");
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-X-" in vowel contexts where it is usually 
	 * pronounced KX ("ksh")
	 * account also for BBC pronounciation of => KS
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_X_Vowel()
	{
		// e.g. "sexual", "connexion" (british), "noxious"
		if (StringAt((mCurrent + 1), 3, "UAL", "ION", "IOU", ""))
		{
			MetaphAdd("KX", "KS");
			AdvanceCounter(3, 1);
			return true;
		}

		return false;
	}

	/**
	 * Encode cases of "-X", encoding as silent when part
	 * of a french word where it is not pronounced
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_French_X_Final()
	{
		//french e.g. "breaux", "paix"
		if (!((mCurrent == mLast) 
			&& (StringAt((mCurrent - 3), 3, "IAU", "EAU", "IEU", "") 
			|| StringAt((mCurrent - 2), 2, "AI", "AU", "OU", "OI", "EU", ""))) )
		{
			MetaphAdd("KS");
		}

		return false;
	}

	/**
	 * Encode "-Z-"
	 * 
	 */
	void Encode_Z()
	{
		if (Encode_ZZ()
			|| Encode_ZU_ZIER_ZS()
			|| Encode_French_EZ()
			|| Encode_German_Z())
		{
			return;
		}

		if (Encode_ZH())
		{
			return;
		}
		else
		{
			MetaphAdd("S");
		}

		// eat redundant 'Z'
		if (CharAt(mCurrent + 1) == 'Z')
		{
			mCurrent += 2;
		}
		else
		{
			mCurrent++;
		}
	}

	/**
	 * Encode cases of "-ZZ-" where it is obviously part
	 * of an italian word where "-ZZ-" is pronounced as TS
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_ZZ()
	{
		// "abruzzi", 'pizza' 
		if ((CharAt(mCurrent + 1) == 'Z') 
			&& ((StringAt((mCurrent + 2), 1, "I", "O", "A", "") 
			&& ((mCurrent + 2) == mLast))
			|| StringAt((mCurrent - 2), 9, "MOZZARELL", "PIZZICATO", "PUZZONLAN", "")))
		{
			MetaphAdd("TS", "S");
			mCurrent += 2;
			return true;
		}

		return false;
	}

	/**
	 * Encode special cases where "-Z-" is pronounced as J
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_ZU_ZIER_ZS()
	{
		if (((mCurrent == 1) && StringAt((mCurrent - 1), 4, "AZUR", "")) 
			|| (StringAt(mCurrent, 4, "ZIER", "") 
					&& !StringAt((mCurrent - 2), 6, "VIZIER", ""))
			|| StringAt(mCurrent, 3, "ZSA", ""))
		{
			MetaphAdd("J", "S");
			
			if (StringAt(mCurrent, 3, "ZSA", ""))
			{
				mCurrent += 2;
			}
			else
			{
				mCurrent++;
			}
			return true;
		}

		return false;
	}

	/**
	 * Encode cases where americans recognize "-EZ" as part
	 * of a french word where Z not pronounced
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_French_EZ()
	{
		if (((mCurrent == 3) && StringAt((mCurrent - 3), 4, "CHEZ", ""))
			|| StringAt((mCurrent - 5), 6, "RENDEZ", ""))
		{
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * Encode cases where "-Z-" is in a german word
	 * where Z => TS in german
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_German_Z()
	{
		if (((mCurrent == 2) && ((mCurrent + 1) == mLast) && StringAt((mCurrent - 2), 4, "NAZI", ""))
			|| StringAt((mCurrent - 2), 6, "NAZIFY", "MOZART", "")
			|| StringAt((mCurrent - 3), 4, "HOLZ", "HERZ", "MERZ", "FITZ", "")
			|| (StringAt((mCurrent - 3), 4, "GANZ", "") && !IsVowel(mCurrent + 1))
			|| StringAt((mCurrent - 4), 5, "STOLZ", "PRINZ", "")
			|| StringAt((mCurrent - 4), 7, "VENEZIA", "")
			|| StringAt((mCurrent - 3), 6, "HERZOG", "")
			// german words beginning with "sch-" but not schlimazel, schmooze
			|| (mInWord.contains("SCH") && !(StringAt((mLast - 2), 3, "IZE", "OZE", "ZEL", "")))
			|| ((mCurrent > 0) && StringAt(mCurrent, 4, "ZEIT", ""))
			|| StringAt((mCurrent - 3), 4, "WEIZ", ""))
		{
			if ((mCurrent > 0) && mInWord.charAt(mCurrent - 1) == 'T')
			{
				MetaphAdd("S");
			}
			else
			{
				MetaphAdd("TS");
			}
			mCurrent++;
			return true;
		}

		return false;
	}

	/**
	 * Encode "-ZH-" as J 
	 * 
	 * @return true if encoding handled in this routine, false if not
	 * 
	 */
	boolean Encode_ZH()
	{
		//chinese pinyin e.g. 'zhao', also english "phonetic spelling"
		if (CharAt(mCurrent + 1) == 'H')
		{
			MetaphAdd("J");
			mCurrent += 2;
			return true;
		}

		return false;
	}
	
	/**
	 * Test for names derived from the swedish,
	 * dutch, or slavic that should get an alternate
	 * pronunciation of 'SV' to match the native
	 * version
	 * 
	 * @return true if swedish, dutch, or slavic derived name
	 */
	boolean Names_Beginning_With_SW_That_Get_Alt_SV()
	{
		if (StringAt(0, 7, "SWANSON", "SWENSON", "SWINSON", "SWENSEN", 
						  "SWOBODA", "")
			|| StringAt(0, 9, "SWIDERSKI", "SWARTHOUT", "")
			|| StringAt(0, 10, "SWEARENGIN", ""))
		{
			return true;
		}

		return false;
	}
	
	/**
	 * Test for names derived from the german
	 * that should get an alternate pronunciation
	 * of 'XV' to match the german version spelled
	 * "schw-"
	 * 
	 * @return true if german derived name
	 */
	boolean Names_Beginning_With_SW_That_Get_Alt_XV()
	{
		if (StringAt(0, 5, "SWART", "")
			|| StringAt(0, 6, "SWARTZ", "SWARTS", "SWIGER", "")
			|| StringAt(0, 7, "SWITZER", "SWANGER", "SWIGERT", 
						      "SWIGART", "SWIHART", "")
			|| StringAt(0, 8, "SWEITZER", "SWATZELL", "SWINDLER", "")
			|| StringAt(0, 9, "SWINEHART", "")
			|| StringAt(0, 10, "SWEARINGEN", ""))
		{
			return true;
		}

		return false;
	}
	
	/**
	 * Test whether the word in question
	 * is a name of germanic or slavic origin, for
	 * the purpose of determining whether to add an
	 * alternate encoding of 'V' 
	 * 
	 * @return true if germanic or slavic name
	 */
	boolean Germanic_Or_Slavic_Name_Beginning_With_W()
	{
		if (StringAt(0, 3, "WEE", "WIX", "WAX", "")
			|| StringAt(0, 4, "WOLF", "WEIS", "WAHL", "WALZ", "WEIL", "WERT", 
							  "WINE", "WILK", "WALT", "WOLL", "WADA", "WULF", 
							  "WEHR", "WURM", "WYSE", "WENZ", "WIRT", "WOLK", 
							  "WEIN", "WYSS", "WASS", "WANN", "WINT", "WINK", 
							  "WILE", "WIKE", "WIER", "WELK", "WISE", "")
			|| StringAt(0, 5, "WIRTH", "WIESE", "WITTE", "WENTZ", "WOLFF", "WENDT", 
							  "WERTZ", "WILKE", "WALTZ", "WEISE", "WOOLF", "WERTH", 
							  "WEESE", "WURTH", "WINES", "WARGO", "WIMER", "WISER", 
							  "WAGER", "WILLE", "WILDS", "WAGAR", "WERTS", "WITTY", 
							  "WIENS", "WIEBE", "WIRTZ", "WYMER", "WULFF", "WIBLE", 
							  "WINER", "WIEST", "WALKO", "WALLA", "WEBRE", "WEYER", 
							  "WYBLE", "WOMAC", "WILTZ", "WURST", "WOLAK", "WELKE", 
							  "WEDEL", "WEIST", "WYGAN", "WUEST", "WEISZ", "WALCK", 
							  "WEITZ", "WYDRA", "WANDA", "WILMA", "WEBER", "")
			|| StringAt(0, 6, "WETZEL", "WEINER", "WENZEL", "WESTER", "WALLEN", "WENGER", 
							  "WALLIN", "WEILER", "WIMMER", "WEIMER", "WYRICK", "WEGNER", 
							  "WINNER", "WESSEL", "WILKIE", "WEIGEL", "WOJCIK", "WENDEL", 
							  "WITTER", "WIENER", "WEISER", "WEXLER", "WACKER", "WISNER", 
							  "WITMER", "WINKLE", "WELTER", "WIDMER", "WITTEN", "WINDLE", 
							  "WASHER", "WOLTER", "WILKEY", "WIDNER", "WARMAN", "WEYANT", 
							  "WEIBEL", "WANNER", "WILKEN", "WILTSE", "WARNKE", "WALSER", 
							  "WEIKEL", "WESNER", "WITZEL", "WROBEL", "WAGNON", "WINANS", 
							  "WENNER", "WOLKEN", "WILNER", "WYSONG", "WYCOFF", "WUNDER", 
							  "WINKEL", "WIDMAN", "WELSCH", "WEHNER", "WEIGLE", "WETTER", 
							  "WUNSCH", "WHITTY", "WAXMAN", "WILKER", "WILHAM", "WITTIG", 
							  "WITMAN", "WESTRA", "WEHRLE", "WASSER", "WILLER", "WEGMAN", 
							  "WARFEL", "WYNTER", "WERNER", "WAGNER", "WISSER", "")
			|| StringAt(0, 7, "WISEMAN", "WINKLER", "WILHELM", "WELLMAN", "WAMPLER", "WACHTER", 
							  "WALTHER", "WYCKOFF", "WEIDNER", "WOZNIAK", "WEILAND", "WILFONG", 
							  "WIEGAND", "WILCHER", "WIELAND", "WILDMAN", "WALDMAN", "WORTMAN", 
							  "WYSOCKI", "WEIDMAN", "WITTMAN", "WIDENER", "WOLFSON", "WENDELL", 
							  "WEITZEL", "WILLMAN", "WALDRUP", "WALTMAN", "WALCZAK", "WEIGAND", 
							  "WESSELS", "WIDEMAN", "WOLTERS", "WIREMAN", "WILHOIT", "WEGENER", 
							  "WOTRING", "WINGERT", "WIESNER", "WAYMIRE", "WHETZEL", "WENTZEL", 
							  "WINEGAR", "WESTMAN", "WYNKOOP", "WALLICK", "WURSTER", "WINBUSH", 
							  "WILBERT", "WALLACH", "WYNKOOP", "WALLICK", "WURSTER", "WINBUSH", 
							  "WILBERT", "WALLACH", "WEISSER", "WEISNER", "WINDERS", "WILLMON", 
							  "WILLEMS", "WIERSMA", "WACHTEL", "WARNICK", "WEIDLER", "WALTRIP", 
							  "WHETSEL", "WHELESS", "WELCHER", "WALBORN", "WILLSEY", "WEINMAN", 
							  "WAGAMAN", "WOMMACK", "WINGLER", "WINKLES", "WIEDMAN", "WHITNER", 
							  "WOLFRAM", "WARLICK", "WEEDMAN", "WHISMAN", "WINLAND", "WEESNER", 
							  "WARTHEN", "WETZLER", "WENDLER", "WALLNER", "WOLBERT", "WITTMER", 
							  "WISHART", "WILLIAM", "")
			|| StringAt(0, 8, "WESTPHAL", "WICKLUND", "WEISSMAN", "WESTLUND", "WOLFGANG", "WILLHITE", 
							  "WEISBERG", "WALRAVEN", "WOLFGRAM", "WILHOITE", "WECHSLER", "WENDLING", 
							  "WESTBERG", "WENDLAND", "WININGER", "WHISNANT", "WESTRICK", "WESTLING", 
							  "WESTBURY", "WEITZMAN", "WEHMEYER", "WEINMANN", "WISNESKI", "WHELCHEL", 
							  "WEISHAAR", "WAGGENER", "WALDROUP", "WESTHOFF", "WIEDEMAN", "WASINGER", 
							  "WINBORNE", "")
			|| StringAt(0, 9, "WHISENANT", "WEINSTEIN", "WESTERMAN", "WASSERMAN", "WITKOWSKI", "WEINTRAUB", 
						      "WINKELMAN", "WINKFIELD", "WANAMAKER", "WIECZOREK", "WIECHMANN", "WOJTOWICZ", 
						      "WALKOWIAK", "WEINSTOCK", "WILLEFORD", "WARKENTIN", "WEISINGER", "WINKLEMAN",  
						      "WILHEMINA", "")
			|| StringAt(0, 10, "WISNIEWSKI", "WUNDERLICH", "WHISENHUNT", "WEINBERGER", "WROBLEWSKI", 
							   "WAGUESPACK", "WEISGERBER", "WESTERVELT", "WESTERLUND", "WASILEWSKI", 
							   "WILDERMUTH", "WESTENDORF", "WESOLOWSKI", "WEINGARTEN", "WINEBARGER", 
							   "WESTERBERG", "WANNAMAKER", "WEISSINGER", "")
			|| StringAt(0, 11, "WALDSCHMIDT", "WEINGARTNER", "WINEBRENNER", "")
			|| StringAt(0, 12, "WOLFENBARGER", "")
			|| StringAt(0, 13, "WOJCIECHOWSKI", ""))
		{
			return true;
		}
		
		return false;		
	}
	
	/**
	 * Test whether the word in question
	 * is a name starting with 'J' that should
	 * match names starting with a 'Y' sound.
	 * All forms of 'John', 'Jane', etc, get
	 * and alt to match e.g. 'Ian', 'Yana'. Joelle
	 * should match 'Yael', 'Joseph' should match
	 * 'Yusef'. German and slavic last names are
	 * also included.
	 * 
	 * @return true if name starting with 'J' that
	 * should get an alternate encoding as a vowel
	 */
	boolean Names_Beginning_With_J_That_Get_Alt_Y()
	{
		if (StringAt(0, 3, "JAN", "JON", "JAN", "JIN", "JEN", "")
			|| StringAt(0, 4, "JUHL", "JULY", "JOEL", "JOHN", "JOSH", 
							  "JUDE", "JUNE", "JONI", "JULI", "JENA", 
							  "JUNG", "JINA", "JANA", "JENI", "JOEL", 
							  "JANN", "JONA", "JENE", "JULE", "JANI", 
							  "JONG", "JOHN", "JEAN", "JUNG", "JONE", 
							  "JARA", "JUST", "JOST", "JAHN", "JACO", 
							  "JANG", "JUDE", "JONE", "") 
			|| StringAt(0, 5, "JOANN", "JANEY", "JANAE", "JOANA", "JUTTA", 
							  "JULEE", "JANAY", "JANEE", "JETTA", "JOHNA", 
							  "JOANE", "JAYNA", "JANES", "JONAS", "JONIE", 
							  "JUSTA", "JUNIE", "JUNKO", "JENAE", "JULIO", 
							  "JINNY", "JOHNS", "JACOB", "JETER", "JAFFE", 
							  "JESKE", "JANKE", "JAGER", "JANIK", "JANDA", 
							  "JOSHI", "JULES", "JANTZ", "JEANS", "JUDAH", 
							  "JANUS", "JENNY", "JENEE", "JONAH", "JONAS", 
							  "JACOB", "JOSUE", "JOSEF", "JULES", "JULIE", 
							  "JULIA", "JANIE", "JANIS", "JENNA", "JANNA", 
							  "JEANA", "JENNI", "JEANE", "JONNA", "")
			|| StringAt(0, 6, "JORDAN", "JORDON", "JOSEPH", "JOSHUA", "JOSIAH", 
							  "JOSPEH", "JUDSON", "JULIAN", "JULIUS", "JUNIOR", 
							  "JUDITH", "JOESPH", "JOHNIE", "JOANNE", "JEANNE", 
							  "JOANNA", "JOSEFA", "JULIET", "JANNIE", "JANELL", 
							  "JASMIN", "JANINE", "JOHNNY", "JEANIE", "JEANNA", 
							  "JOHNNA", "JOELLE", "JOVITA", "JOSEPH", "JONNIE", 
							  "JANEEN", "JANINA", "JOANIE", "JAZMIN", "JOHNIE", 
							  "JANENE", "JOHNNY", "JONELL", "JENELL", "JANETT", 
							  "JANETH", "JENINE", "JOELLA", "JOEANN", "JULIAN", 
							  "JOHANA", "JENICE", "JANNET", "JANISE", "JULENE", 
							  "JOSHUA", "JANEAN", "JAIMEE", "JOETTE", "JANYCE", 
							  "JENEVA", "JORDAN", "JACOBS", "JENSEN", "JOSEPH", 
							  "JANSEN", "JORDON", "JULIAN", "JAEGER", "JACOBY", 
							  "JENSON", "JARMAN", "JOSLIN", "JESSEN", "JAHNKE", 
							  "JACOBO", "JULIEN", "JOSHUA", "JEPSON", "JULIUS", 
							  "JANSON", "JACOBI", "JUDSON", "JARBOE", "JOHSON", 
							  "JANZEN", "JETTON", "JUNKER", "JONSON", "JAROSZ", 
							  "JENNER", "JAGGER", "JASMIN", "JEPSEN", "JORDEN", 
							  "JANNEY", "JUHASZ", "JERGEN", "JAKOB", "")
			|| StringAt(0, 7, "JOHNSON", "JOHNNIE", "JASMINE", "JEANNIE", "JOHANNA", 
							  "JANELLE", "JANETTE", "JULIANA", "JUSTINA", "JOSETTE", 
							  "JOELLEN", "JENELLE", "JULIETA", "JULIANN", "JULISSA", 
							  "JENETTE", "JANETTA", "JOSELYN", "JONELLE", "JESENIA", 
							  "JANESSA", "JAZMINE", "JEANENE", "JOANNIE", "JADWIGA", 
							  "JOLANDA", "JULIANE", "JANUARY", "JEANICE", "JANELLA", 
							  "JEANETT", "JENNINE", "JOHANNE", "JOHNSIE", "JANIECE", 
							  "JOHNSON", "JENNELL", "JAMISON", "JANSSEN", "JOHNSEN", 
							  "JARDINE", "JAGGERS", "JURGENS", "JOURDAN", "JULIANO", 
							  "JOSEPHS", "JHONSON", "JOZWIAK", "JANICKI", "JELINEK", 
							  "JANSSON", "JOACHIM", "JANELLE", "JACOBUS", "JENNING", 
							  "JANTZEN", "JOHNNIE",  "")
			|| StringAt(0, 8, "JOSEFINA", "JEANNINE", "JULIANNE", "JULIANNA", "JONATHAN", 
							  "JONATHON", "JEANETTE", "JANNETTE", "JEANETTA", "JOHNETTA", 
							  "JENNEFER", "JULIENNE", "JOSPHINE", "JEANELLE", "JOHNETTE", 
							  "JULIEANN", "JOSEFINE", "JULIETTA", "JOHNSTON", "JACOBSON", 
							  "JACOBSEN", "JOHANSEN", "JOHANSON", "JAWORSKI", "JENNETTE", 
							  "JELLISON", "JOHANNES", "JASINSKI", "JUERGENS", "JARNAGIN", 
							  "JEREMIAH", "JEPPESEN", "JARNIGAN", "JANOUSEK", "")
			|| StringAt(0, 9, "JOHNATHAN", "JOHNATHON", "JORGENSEN", "JEANMARIE", "JOSEPHINA", 
						      "JEANNETTE", "JOSEPHINE", "JEANNETTA", "JORGENSON", "JANKOWSKI", 
						      "JOHNSTONE", "JABLONSKI", "JOSEPHSON", "JOHANNSEN", "JURGENSEN", 
						      "JIMMERSON", "JOHANSSON", "")
			|| StringAt(0, 10, "JAKUBOWSKI", ""))
			{
				return true;
			}

			return false;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// example code
		
		Metaphone3 m3 = new Metaphone3();

		//m3.SetEncodeVowels(true);
		//m3.SetEncodeExact(true);

		m3.SetWord("iron");

		m3.Encode();

		System.out.println("iron : " + m3.GetMetaph());
		System.out.println("iron : (alt) " + m3.GetAlternateMetaph());	

		m3.SetWord("witz");

		m3.Encode();

		System.out.println("witz : " + m3.GetMetaph());
		System.out.println("witz : (alt) " + m3.GetAlternateMetaph());	
		
		m3.SetWord("");

		m3.Encode();

		System.out.println("BLANK : " + m3.GetMetaph());
		System.out.println("BLANK : (alt) " + m3.GetAlternateMetaph());	

		// these settings default to false
		m3.SetEncodeExact(true);
		m3.SetEncodeVowels(true);

		String test = new String("Guillermo");
		m3.SetWord(test);
		m3.Encode();
		System.out.println(test + " : " + m3.GetMetaph());
		System.out.println(test + " : (alt) " + m3.GetAlternateMetaph());	
		
		test = "VILLASENOR";
		m3.SetWord(test);
		m3.Encode();
		System.out.println(test + " : " + m3.GetMetaph());	
		System.out.println(test + " : (alt) " + m3.GetAlternateMetaph());	
		
		test = "GUILLERMINA";
		m3.SetWord(test);
		m3.Encode();
		System.out.println(test + " : " + m3.GetMetaph());	
		System.out.println(test + " : (alt) " + m3.GetAlternateMetaph());	
		
		test = "PADILLA";
		m3.SetWord(test);
		m3.Encode();
		System.out.println(test + " : " + m3.GetMetaph());	
		System.out.println(test + " : (alt) " + m3.GetAlternateMetaph());	
		
		test = "BJORK";
		m3.SetWord(test);
		m3.Encode();
		System.out.println(test + " : " + m3.GetMetaph());	
		System.out.println(test + " : (alt) " + m3.GetAlternateMetaph());
		
		test = "belle";
		m3.SetWord(test);
		m3.Encode();
		System.out.println(test + " : " + m3.GetMetaph());	
		System.out.println(test + " : (alt) " + m3.GetAlternateMetaph());	
		
		test = "ERICH";
		m3.SetWord(test);
		m3.Encode();
		System.out.println(test + " : " + m3.GetMetaph());	
		System.out.println(test + " : (alt) " + m3.GetAlternateMetaph());	
		
		test = "CROCE";
		m3.SetWord(test);
		m3.Encode();
		System.out.println(test + " : " + m3.GetMetaph());	
		System.out.println(test + " : (alt) " + m3.GetAlternateMetaph());	
		
		test = "GLOWACKI";
		m3.SetWord(test);
		m3.Encode();
		System.out.println(test + " : " + m3.GetMetaph());	
		System.out.println(test + " : (alt) " + m3.GetAlternateMetaph());	
		
		test = "qing";
		m3.SetWord(test);
		m3.Encode();
		System.out.println(test + " : " + m3.GetMetaph());	
		System.out.println(test + " : (alt) " + m3.GetAlternateMetaph());	
				
		test = "tsing";
		m3.SetWord(test);
		m3.Encode();
		System.out.println(test + " : " + m3.GetMetaph());	
		System.out.println(test + " : (alt) " + m3.GetAlternateMetaph());
		
	}
}

