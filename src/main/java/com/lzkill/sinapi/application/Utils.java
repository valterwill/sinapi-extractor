package com.lzkill.sinapi.application;

import java.io.File;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

//TODO Refactor methods for better coding
public class Utils {
	
	private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

	public static String extractPlainTextFromPDFFile(String pathName,
			int startPage) {
		PDDocument pddDocument = null;
		PDFTextStripper textStripper = null;
		String text = null;
		try {
			pddDocument = PDDocument.load(new File(pathName));
			textStripper = new PDFTextStripper();
			textStripper.setStartPage(startPage);
			text = new String(textStripper.getText(pddDocument));
			pddDocument.close();
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}

		return text;
	}

	public static int numberOfLines(String text) {
		Scanner scanner = new Scanner(text);
		int i = 0;
		while (scanner.hasNextLine()) {
			scanner.nextLine();
			i++;
		}
		scanner.close();
		return i;
	}

	public static String removeFirstNLines(String text, int n) {
		Scanner scanner = new Scanner(text);
		int i = 0;
		while (scanner.hasNextLine() && i < n) {
			scanner.nextLine();
			i++;
		}

		String result = "";
		while (scanner.hasNextLine()) {
			result += scanner.nextLine() + "\n";
		}

		scanner.close();
		return result;
	}

	public static String removeLastNLines(String text, int n) {
		Scanner scanner = new Scanner(text);
		int nLines = 0;
		while (scanner.hasNextLine()) {
			scanner.nextLine();
			nLines++;
		}

		scanner.close();

		String result = "";
		int i = 0;
		scanner = new Scanner(text);
		while (scanner.hasNextLine() && i < nLines - n) {
			result += scanner.nextLine() + "\n";
			i++;
		}

		scanner.close();
		return result;
	}
	
	/**
	 * Replaces all occurrences within a plain text of the string bounded by two
	 * other strings, inclusive.
	 */
	public static String replaceAll(String target, String fromStr,
			String toStr, String replacement) {
		return target.replaceAll("(" + fromStr + ")[\\s\\S]*?(" + toStr + ")",
				replacement);
	}

	/**
	 * Removes the first n lines within a plain text from the position
	 * where the search string is located, inclusive.
	 */
	public static String removeNLinesForwardsFromStringMatch(String text,
			String match, int n) {
		Scanner scanner = new Scanner(text);
		String line = null, result = "";
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();

			if (line.matches("(" + match + ").*")) {
				int i = 0;
				while (scanner.hasNextLine() && i < n) {
					scanner.nextLine();
					i++;
				}
			}

			else
				result += line + "\n";
		}

		scanner.close();
		return result;
	}
	
	public static ImageIcon loadImageIcon(String pathName) {
		URL imgURL = MainWindow.class.getResource(pathName);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			LOGGER.warning(Application.IMAGE_ICON_FILE_NOT_FOUND + pathName);
			return null;
		}
	}
	
	public static boolean isPriceChar(char c) {
		return c == '.' || c == ',' || Character.isDigit(c);
	}
}
