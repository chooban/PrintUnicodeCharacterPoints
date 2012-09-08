package com.rosshendry.printunicodecharacterpoints;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.CharUtils;

/**
 * A very simple little program that came about from the need to be able to
 * embed Unicode into test files in a safe manner.
 * 
 * Due to the fact that editors have bugs, saving actual UTF-8 into files is a
 * bit dangerous. All it takes is for someone to not be paying attention and the
 * files will be saved with a stream of question marks.
 * 
 * To get around that, this application will convert the non-ASCII characters in
 * a string into Unicode code points. It'll also read a string containing
 * escaped characters and print out the interpreted value.
 * 
 * @author ross
 * 
 */
public class PrintUnicode {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		Options options = new Options();
		options.addOption( "s", true, "UTF-8 String to be converted to code points" );
		options.addOption( "c", true, "Code points to be read in, parsed and converted to a string" );
		options.addOption( "h", false, "display this help" );

		CommandLineParser parser = new GnuParser();
		CommandLine line = null;
		try {
			line = parser.parse( options, args );
		} catch (ParseException e) {
			System.out.println( e.getMessage() );
		}

		if ( line.hasOption( "h" ) ) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "PrintUnicode", options );
			System.exit( 0 );
		}

		if ( line.hasOption( "s" ) ) {
			String inputString = line.getOptionValue( "s" );

			// Might be useful just to make sure both you and the application are on
			// the same page.
			System.out.println( "The input was " + inputString );
			char c;

			// Iterate through the characters of the input string. If they're not
			// ASCII then escape them.
			for ( int i = 0; i < inputString.length(); i++ ) {
				c = inputString.charAt( i );
				if ( CharUtils.isAscii( c ) ) {
					System.out.print( c );
				} else {
					System.out.print( CharUtils.unicodeEscaped( inputString.charAt( i ) ) );
				}
			}

			// Your command line now has a string you can copy and paste into your
			// source.
			System.out.println( "" );
			System.out.println( "Done" );
		}

		if ( line.hasOption( "c" ) ) {
			String charPointString = line.getOptionValue( "c" );
			System.out.println( "The input was " + charPointString );

			char[] inputArray = charPointString.toCharArray();
			char[] eventualOutput = new char[inputArray.length];
			char currentChar;

			// Used to keep track of which element of the array we're now inserting
			// into. If we use the value of the iterator which is going through the
			// input string then we end up with large gaps.
			int lastCharacter = 0;

			for ( int i = 0; i < inputArray.length; i++ ) {
				currentChar = inputArray[i];
				if ( currentChar == '\\' ) {
					String unicodeCP = "";

					// Ignore the slash, the following 'u', and grab the four characters.
					// Brittle as all hell, but this isn't meant to be robust.
					for ( int j = (i + 2); j < (i + 6); j++ ) {
						unicodeCP += String.valueOf( inputArray[j] );
					}
					eventualOutput[lastCharacter++] = (char) Integer.parseInt( unicodeCP, 16 );
					i += 5;
				} else {
					eventualOutput[lastCharacter++] = currentChar;
				}
			}

			System.out.println( new String( eventualOutput ) );
			System.out.println( "Done" );
		}
	}

}
