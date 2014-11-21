package com.lzkill.sinapi.extract;

import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Logger;

public abstract class SINAPIBaseExtractor implements SINAPIExtractor {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger
			.getLogger(SINAPIBaseExtractor.class.getName());

	private String delimiter;
	private String rawText;
	private Scanner rawTextScanner;
	private String currentLine;
	private String nextLine;
	private int numberOfProcessedLines;
	private int numberOfExtractedLines;

	protected SINAPIBaseExtractor(String rawText, String delimiter) {
		this.rawText = rawText;
		this.delimiter = delimiter;
		this.rawTextScanner = new Scanner(rawText);
	}

	protected String getDelimiter() {
		return delimiter;
	}

	protected String getCurrentLine() {
		return currentLine;
	}

	protected String getNextLine() {
		return nextLine;
	}

	@Override
	public int getNumberOfProcessedLines() {
		return numberOfProcessedLines;
	}

	@Override
	public int getNumberOfExtractedLines() {
		return numberOfExtractedLines;
	}
	
	@Override
	public String getRawText() {
		return rawText;
	}

	@Override
	public Iterator<String> iterator() {
		Iterator<String> it = new Iterator<String>() {

			@Override
			public String next() {
				String result = "";
				if (hasNext()) {
					determineCurrentPosition();
					gatherCurrentLinePieces();
					if (!currentLine.isEmpty()) {
						result = makeOutputLine();
						numberOfExtractedLines++;
					}
				}

				return result;
			}

			@Override
			public boolean hasNext() {
				return hasInput() || !nextLine.isEmpty();
			}

			@Override
			public void remove() {
				// TODO Implement Iterator remove()
			}
		};

		return it;
	}

	protected boolean hasInput() {
		return rawTextScanner.hasNext();
	}

	protected void determineCurrentPosition() {
		if (!isStructed(nextLine)) {
			currentLine = rawTextScanner.nextLine();
			numberOfProcessedLines++;
		} else {
			currentLine = nextLine;
		}
		nextLine = "";
	}

	protected void gatherCurrentLinePieces() {
		while (hasInput()) {
			nextLine = rawTextScanner.nextLine();
			numberOfProcessedLines++;
			if (!isStructed(nextLine)) {
				currentLine += " " + nextLine.trim();
			}
			else
				break;
		}
	}

	protected abstract boolean isStructed(String line);

	protected abstract String makeOutputLine();
}
