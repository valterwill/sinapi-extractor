package com.lzkill.sinapi.extract;

import java.util.logging.Logger;

import com.lzkill.sinapi.application.Utils;

/**
 * @see <a
 *      href="http://www1.caixa.gov.br/download/asp/ent_hist.asp?download=72551"
 *      >PRECOS_INSUMOS_ES_JUN_2014_COM_DESONERAÇÃO.PDF (17/07/2014)/a>
 */
public class ItemTableExtractor extends SINAPIBaseExtractor {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger
			.getLogger(ItemTableExtractor.class.getName());

	private final int ITEM_CODE_LENGTH = 8;

	public ItemTableExtractor(String rawText, String delimiter) {
		super(rawText, delimiter);
	}

	@Override
	protected boolean isStructed(String line) {
		return line != null && isItem(line);
	}

	private boolean isItem(String line) {
		return line.length() >= ITEM_CODE_LENGTH
				&& line.substring(0, ITEM_CODE_LENGTH).matches(
						"[0-" + (ITEM_CODE_LENGTH + 1) + "]{"
								+ ITEM_CODE_LENGTH + "}");
	}

	@Override
	// TODO Refactor for smarter and clearer extraction
	protected String makeOutputLine() {
		String result = "";
		String _currentLine = getCurrentLine();
		String _delimiter = getDelimiter();

		String code = _currentLine.substring(0, ITEM_CODE_LENGTH).trim()
				.replaceAll(" +", " ");

		String mu = findUnitOfMeasureForwardsFromPosition(_currentLine,
				ITEM_CODE_LENGTH + 1).trim().replaceAll(" +", " ");

		String price = findPriceForwardsFromPosition(_currentLine,
				ITEM_CODE_LENGTH + mu.length() + 2).trim()
				.replaceAll(" +", " ");

		String description = _currentLine
				.substring(ITEM_CODE_LENGTH + mu.length() + price.length() + 2)
				.trim().replaceAll(" +", " ");

		result = code + _delimiter + description + _delimiter + mu + _delimiter
				+ price + "\n";

		return result;
	}

	private String findPriceForwardsFromPosition(String line, int position) {
		int i = position;
		for (; i < line.length() && Utils.isPriceChar(line.charAt(i)); i++) {
		}
		return line.substring(position, i);
	}

	private String findUnitOfMeasureForwardsFromPosition(String line,
			int position) {
		int i = position;
		for (; i < line.length() && !Character.isSpaceChar(line.charAt(i)); i++) {
		}
		return line.substring(position, i);
	}
}
