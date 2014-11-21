package com.lzkill.sinapi.extract;

import com.lzkill.sinapi.application.Utils;

//TODO Refactor for more generic and readable coding
public class SINAPIExtractorFactory {

	// TODO Refactor for better matching
	private final static String ITEM_TABLE_IDENTIFIER = "Código Descriçao do Insumo Unid PreçoMediano";
	private final static String COMPOSITION_TABLE_IDENTIFIER = "CUSTO DE COMPOSIÇÕES - SINTÉTICO";
	private final static String ANALYTIC_TABLE_IDENTIFIER = "CLASSE/TIPO CÓDIGOS DESCRIÇÃO UNIDADE COEFICIENTE";

	private final static String ITEM_TABLE_PAGE_HEADER_START = "PREÇOS DE INSUMOS";
	private final static String ITEM_TABLE_PAGE_HEADER_END = "PreçoMediano \\(R\\$\\)";
	private final static String ITEM_TABLE_TEXT_TAIL_START = "Total de Insumos:";
	private final static int ITEM_TABLE_TEXT_TAIL_LENGTH = 1;

	private final static String COMPOSITION_TABLE_PAGE_HEADER_START = "SINAPI - ";
	private final static String COMPOSITION_TABLE_PAGE_HEADER_END = "CAIXA REFERENCIAL";
	private final static int COMPOSITION_TABLE_TEXT_TAIL_LENGTH = 8;

	private final static int ANALYTIC_TABLE_TEXT_HEADER_LENGTH = 2;
	private final static String ANALYTIC_TABLE_TEXT_MATCH_1 = "SINAPI -";
	private final static String ANALYTIC_TABLE_TEXT_MATCH_2 = "página";
	private final static int ANALYTIC_TABLE_REMOVE_FROM_MATCH_1 = 2;
	private final static int ANALYTIC_TABLE_REMOVE_FROM_MATCH_2 = 1;

	public static SINAPIExtractor create(String rawText, String delimiter)
			throws UnexpectedFileContentException {

		if (rawText.contains(ITEM_TABLE_IDENTIFIER))
			return createItemTableExtractor(rawText, delimiter);

		if (rawText.contains(COMPOSITION_TABLE_IDENTIFIER))
			return createCompositionTableExtractor(rawText, delimiter);

		if (rawText.contains(ANALYTIC_TABLE_IDENTIFIER))
			return createAnalyticTableExtractor(rawText, delimiter);

		throw new UnexpectedFileContentException(
				"The selected file is not a SINAPI table");
	}

	private static SINAPIExtractor createItemTableExtractor(String rawText,
			String delimiter) {
		String cleanerRawText = Utils.replaceAll(rawText,
				ITEM_TABLE_PAGE_HEADER_START, ITEM_TABLE_PAGE_HEADER_END, "");
		cleanerRawText = Utils.removeNLinesForwardsFromStringMatch(
				cleanerRawText, ITEM_TABLE_TEXT_TAIL_START,
				ITEM_TABLE_TEXT_TAIL_LENGTH);
		return new ItemTableExtractor(cleanerRawText, delimiter);
	}

	private static SINAPIExtractor createCompositionTableExtractor(
			String rawText, String delimiter) {
		String cleanerRawText = Utils.removeLastNLines(rawText,
				COMPOSITION_TABLE_TEXT_TAIL_LENGTH);
		cleanerRawText = Utils.replaceAll(cleanerRawText,
				COMPOSITION_TABLE_PAGE_HEADER_START,
				COMPOSITION_TABLE_PAGE_HEADER_END, "");
		return new CompositionTableExtractor(cleanerRawText, delimiter);
	}

	private static SINAPIExtractor createAnalyticTableExtractor(String rawText,
			String delimiter) {
		String cleanerRawText = Utils.removeFirstNLines(rawText,
				ANALYTIC_TABLE_TEXT_HEADER_LENGTH);
		cleanerRawText = Utils.removeNLinesForwardsFromStringMatch(
				cleanerRawText, ANALYTIC_TABLE_TEXT_MATCH_1,
				ANALYTIC_TABLE_REMOVE_FROM_MATCH_1);
		cleanerRawText = Utils.removeNLinesForwardsFromStringMatch(
				cleanerRawText, ANALYTIC_TABLE_TEXT_MATCH_2,
				ANALYTIC_TABLE_REMOVE_FROM_MATCH_2);
		return new AnalyticTableExtractor(cleanerRawText, delimiter);
	}
}
