package com.lzkill.sinapi.extract;

import java.util.logging.Logger;

/**
 * @see <a
 *      href="http://downloads.caixa.gov.br/_arquivos/sinapi/relat_comp_analit/CATALOGO_COMPOSICOES_ANALITICAS_JUNHO_2014.pdf">CATALOGO_COMPOSICOES_ANALITICAS_JUNHO_2014</a>
 */
public class AnalyticTableExtractor extends SINAPIBaseExtractor {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger
			.getLogger(AnalyticTableExtractor.class.getName());

	private enum LineType {
		COMPOSITION, CHILD_COMPOSITION, CHILD_ITEM, OTHER, EMPTY
	};

	public AnalyticTableExtractor(String rawText, String delimiter) {
		super(rawText, delimiter);
	}
	
	@Override
	protected boolean isStructed(String line) {
		return line != null && !isEmpty(line) && !isOther(line);
	}
	
	//TODO Review matching
	private LineType getLineType(String line) {

		if (line.length() == 0)
			return LineType.EMPTY;

		if (line.matches("(COMPOSICAO) [0-9]{1}.*"))
			return LineType.CHILD_COMPOSITION;

		if (line.matches("(INSUMO) [0-9]{1}.*"))
			return LineType.CHILD_ITEM;

		if (line.matches("[A-Z]{4} [0-9]{5}\\/[0-9]{3}.*"))
			return LineType.COMPOSITION;

		if (line.matches("[A-Z]{4} [0-9]{5}\\/[0-9]{2}.*"))
			return LineType.COMPOSITION;

		if (line.matches("[A-Z]{4} [0-9]{5}\\/[0-9]{1}.*"))
			return LineType.COMPOSITION;

		if (line.matches("[A-Z]{4} [0-9]{5}"))
			return LineType.COMPOSITION;

		if (line.matches("[A-Z]{4} [0-9]{4}"))
			return LineType.COMPOSITION;

		if (line.matches("[A-Z]{4} [0-9]{3}"))
			return LineType.COMPOSITION;

		if (line.matches("[A-Z]{4} [0-9]{5} .*"))
			return LineType.COMPOSITION;

		if (line.matches("[A-Z]{4} [0-9]{4} .*"))
			return LineType.COMPOSITION;

		return LineType.OTHER;
	}

	@Override
	// TODO Refactor for smarter and clearer extraction
	protected String makeOutputLine() {
		String result = "";
		String _currentLine = getCurrentLine();

		if (isComposition(_currentLine))
			result = makeCompositionLine();
		else if (isChildComposition(_currentLine))
			result = makeChildCompositionLine();
		else if (isChildItem(_currentLine)) {
			result = makeChildItemLine();
		}

		return result;
	}
	
	private boolean isComposition(String line) {
		return getLineType(line) == LineType.COMPOSITION;
	}
	
	private boolean isChildComposition(String line) {
		return getLineType(line) == LineType.CHILD_COMPOSITION;
	}
	
	private boolean isChildItem(String line) {
		return getLineType(line) == LineType.CHILD_ITEM;
	}
	
	private boolean isEmpty(String line) {
		return getLineType(line) == LineType.EMPTY;
	}
	
	private boolean isOther(String line) {
		return getLineType(line) == LineType.OTHER;
	}

	private String makeCompositionLine() {
		String _currentLine = getCurrentLine().trim();
		String _delimiter = getDelimiter();
		String mu = _currentLine.substring(
				findUnitOfMeasureStartPositionBackwardsFromTail(_currentLine))
				.trim();
		String _class = _currentLine.substring(0, 5).trim();
		String code = findCodeForwardsFromHead(_currentLine);
		String name = _currentLine.substring(5 + code.length(),
				findUnitOfMeasureStartPositionBackwardsFromTail(_currentLine))
				.trim();

		return "1" + _delimiter + _class + _delimiter + code + _delimiter
				+ name + _delimiter + mu + "\n";
	}

	private String makeChildCompositionLine() {
		String _currentLine = getCurrentLine().trim();
		String _delimiter = getDelimiter();
		String amount = _currentLine.substring(
				findAmountStartPositionBackwardsFromTail(_currentLine)).trim();
		String code = findCodeForwardsFromHead(_currentLine);
		String name = _currentLine.substring(11 + code.length(),
				findUnitOfMeasureStartPositionBackwardsFromTail(_currentLine))
				.trim();
		String mu = findUnitOfMeasureBackwardsFromTail(_currentLine);
		return "2" + _delimiter + "COMPOSICAO" + _delimiter + code + _delimiter
				+ name + _delimiter + mu + _delimiter + amount + "\n";
	}

	private String makeChildItemLine() {
		String _currentLine = getCurrentLine().trim();
		String _delimiter = getDelimiter();
		String amount = _currentLine.substring(
				findAmountStartPositionBackwardsFromTail(_currentLine)).trim();
		String code = findCodeForwardsFromHead(_currentLine);
		String name = _currentLine.substring(7 + code.length(),
				findUnitOfMeasureStartPositionBackwardsFromTail(_currentLine))
				.trim();
		String mu = findUnitOfMeasureBackwardsFromTail(_currentLine);

		return "2" + _delimiter + "INSUMO" + _delimiter + code + _delimiter
				+ name + _delimiter + mu + _delimiter + amount + "\n";
	}

	private int findUnitOfMeasureStartPositionBackwardsFromTail(String line) {
		int i = line.length() - 1;
		for (; i > 0; i--) {
			char c = line.charAt(i);

			if (Character.isLetter(c))
				break;
		}

		int j = i;
		for (; j > 0; j--) {
			char c = line.charAt(j);

			if (Character.isSpaceChar(c))
				break;
		}

		return j + 1;
	}

	private String findUnitOfMeasureBackwardsFromTail(String line) {
		int i = line.length() - 1;
		for (; i > 0; i--) {
			char c = line.charAt(i);

			if (Character.isLetter(c))
				break;
		}

		int j = i;
		for (; j > 0; j--) {
			char c = line.charAt(j);

			if (Character.isSpaceChar(c))
				break;
		}

		return line.substring(j + 1, i + 1);
	}

	private int findAmountStartPositionBackwardsFromTail(String line) {
		int i = line.length() - 1;
		for (; i > 0; i--) {
			char c = line.charAt(i);

			if (Character.isSpaceChar(c))
				break;
		}

		return i + 1;
	}

	private String findCodeForwardsFromHead(String line) {
		int i = 0;
		for (; i < line.length(); i++) {
			char c = line.charAt(i);

			if (Character.isDigit(c))
				break;
		}

		int j = i;
		for (; j < line.length(); j++) {
			char c = line.charAt(j);

			if (Character.isDigit(c) || c == '/')
				continue;
			else
				break;
		}

		return line.substring(i, j);
	}
}
