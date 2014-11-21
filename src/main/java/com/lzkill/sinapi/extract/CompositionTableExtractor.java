package com.lzkill.sinapi.extract;

import java.util.logging.Logger;

/**
 * @see <a
 *      href="http://www1.caixa.gov.br/download/asp/ent_hist.asp?download=72752">COMPOSIÇÕES_ES_JUN_2014_SEM_DESONERAÇÃO.PDF
 *      (18/07/2014)</a>
 */
public class CompositionTableExtractor extends SINAPIBaseExtractor {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger
			.getLogger(CompositionTableExtractor.class.getName());

	private final int LINE_HEAD_LENGTH = 15;

	private enum LineType {
		CLASS, GROUP, FAMILY, COMPOSITION, CHILD_COMPOSITION, FRAGMENT, UNKNOWN, EMPTY
	};

	public CompositionTableExtractor(String rawText, String delimiter) {
		super(rawText, delimiter);
	}

	@Override
	protected boolean isStructed(String line) {
		return line != null && !isEmpty(line) && !isFragment(line);
	}

	private boolean isEmpty(String line) {
		return getLineType(line) == LineType.EMPTY;
	}

	private boolean isFragment(String line) {
		return getLineType(line) == LineType.FRAGMENT;
	}

	private boolean isClass(String line) {
		return getLineType(line) == LineType.CLASS;
	}

	private boolean isGroup(String line) {
		return getLineType(line) == LineType.GROUP;
	}
	
	private boolean isFamily(String currentLine, String nextLine) {
		return isUnknown(currentLine) && isChildComposition(nextLine);
	}
	
	private boolean isComposition(String currentLine, String nextLine) {
		return isUnknown(currentLine) && !isChildComposition(nextLine);
	}

	private boolean isChildComposition(String line) {
		return getLineType(line) == LineType.CHILD_COMPOSITION;
	}
	
	//FAMILY or COMPOSITION
	private boolean isUnknown(String line) {
		return getLineType(line) == LineType.UNKNOWN;
	}

	private LineType getLineType(String currentLine) {
		if (currentLine.length() == 0)
			return LineType.EMPTY;

		String lineHead = currentLine.substring(0, LINE_HEAD_LENGTH);
		if (lineHead.matches("  [A-Z]{4}         "))
			return LineType.CLASS;
		
		else if (lineHead.matches("  [0-9]{4}         "))
			return LineType.GROUP;
		
		else if (lineHead.matches("     [0-9]{5}\\/[0-9]{3} "))
			return LineType.CHILD_COMPOSITION;
		
		//FAMILY or COMPOSITION
		else if (lineHead.matches("       [0-9]{3}     ")
				|| lineHead.matches("      [0-9]{4}     ")
				|| lineHead.matches("     [0-9]{5}     "))
			return LineType.UNKNOWN;

		return LineType.FRAGMENT;
	}

	@Override
	// TODO Refactor for smarter and clearer extraction
	protected String makeOutputLine() {
		String result = "";
		String _currentLine = getCurrentLine();
		String _nextLine = getNextLine();

		if (isClass(_currentLine))
			result = makeClassLine();
		else if (isGroup(_currentLine))
			result = makeGroupLine();
		else if (isFamily(_currentLine, _nextLine))
			result = makeFamilyLine();
		else if (isComposition(_currentLine, _nextLine))
			result = makeCompositionLine();
		else if (isChildComposition(_currentLine)) {
			result = makeChildCompositionLine();
		}

		return result;
	}

	private String makeClassLine() {
		String _currentLine = getCurrentLine().trim();
		String _delimiter = getDelimiter();
		String _class = _currentLine.substring(0, 4);
		String name = _currentLine.substring(4).trim();
		return "1" + _delimiter + _class + _delimiter + name + "\n";
	}

	private String makeGroupLine() {
		String _currentLine = getCurrentLine().trim();
		String _delimiter = getDelimiter();
		String group = _currentLine.substring(0, 4);
		String name = _currentLine.substring(4).trim();
		return "2" + _delimiter + group + _delimiter + name + "\n";
	}

	private String makeFamilyLine() {
		String _currentLine = getCurrentLine().trim();
		String _delimiter = getDelimiter();
		String code = _currentLine.substring(0, 5);
		String name = _currentLine.substring(5).trim();
		return "3" + _delimiter + code + _delimiter + name + "\n";
	}

	private String makeCompositionLine() {
		String _currentLine = getCurrentLine();
		String _delimiter = getDelimiter();
		String code = _currentLine.substring(0, 15).trim();
		String mu = findUnitOfMeasureBackwardsFromPosition(_currentLine, 104);
		String price = findPriceForwardsFromPosition(_currentLine, 104);
		String name = findName(_currentLine);
		return "4" + _delimiter + code + _delimiter + name + _delimiter + mu
				+ _delimiter + price + "\n";
	}

	private String makeChildCompositionLine() {
		String _currentLine = getCurrentLine();
		String _delimiter = getDelimiter();
		String code = _currentLine.substring(0, 15).trim();
		String mu = findUnitOfMeasureBackwardsFromPosition(_currentLine, 104);
		String price = findPriceForwardsFromPosition(_currentLine, 104);
		String name = findName(_currentLine);
		return "4" + _delimiter + code + _delimiter + name + _delimiter + mu
				+ _delimiter + price + "\n";
	}

	private String findName(String line) {
		String name = "";
		name += line.substring(15,
				findUnitOfMeasureStartPositionBackwardsPosition(line, 104))
				.trim();
		name += line.substring(
				findPriceEndPositionForwardsFromPosition(line, 104) + 1).trim();
		return name;
	}

	private String findPriceForwardsFromPosition(String line, int startPosition) {
		int i = 0;
		for (i = startPosition; i < line.length(); i++) {
			char c = line.charAt(i);
			if (!Character.isDigit(c))
				continue;
			else
				break;
		}

		int j = i;
		for (; j < line.length(); j++) {
			char c = line.charAt(j);
			if (c == ',') {
				j += 2;
				break;
			}
		}

		return line.substring(i, j + 1);
	}

	private int findPriceEndPositionForwardsFromPosition(String line,
			int startPosition) {
		int i = 0;
		for (i = startPosition; i < line.length(); i++) {
			char c = line.charAt(i);
			if (!Character.isDigit(c))
				continue;
			else
				break;
		}

		int j = i;
		for (; j < line.length(); j++) {
			char c = line.charAt(j);
			if (c == ',') {
				j += 2;
				break;
			}
		}

		return j;
	}

	private String findUnitOfMeasureBackwardsFromPosition(String line,
			int startPosition) {
		int i = 0;
		for (i = startPosition; i >= 0; i--) {
			char c = line.charAt(i);
			if (Character.isSpaceChar(c))
				continue;
			else
				break;
		}

		int j = i;
		for (; j >= 0; j--) {
			char c = line.charAt(j);

			if (!Character.isSpaceChar(c))
				continue;
			else
				break;
		}

		return line.substring(j + 1, i + 1);
	}

	private int findUnitOfMeasureStartPositionBackwardsPosition(String line,
			int startPosition) {
		int i = 0;
		for (i = startPosition; i >= 0; i--) {
			char c = line.charAt(i);
			if (Character.isSpaceChar(c))
				continue;
			else
				break;
		}

		int j = i;
		for (; j >= 0; j--) {
			char c = line.charAt(j);
			if (!Character.isSpaceChar(c))
				continue;
			else
				break;
		}

		return (j + 1);
	}
}
