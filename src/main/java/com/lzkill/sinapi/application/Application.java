package com.lzkill.sinapi.application;

import java.util.logging.Level;

//TODO Rename and sort constants for better reading
//TODO Remove unused constants
//TODO Externalize and localize strings
public abstract class Application {
	public final static boolean DEBUG_MODE = true;
	public final static String MAIN_WINDOW_TITLE = "CESAN - Extrator SINAPI";
	public final static String APP_VERSION = "v0.0.1";
	
	public final static String PLAIN_TEXT_LOG_PATHNAME = "log.txt";
	public final static String HTML_LOG_PATHNAME = "log.html";
	public final static Level FILE_LOG_LEVEL = Level.WARNING;
	public final static Level CONSOLE_LOG_LEVEL = Level.ALL;
	public final static String APP_ICON_PATHNAME = "res/app32.png";
	public final static String OPEN_FILE_WINDOW_TITLE = "Selecione a tabela SINAPI";
	public final static String SAVE_FILE_WINDOW_TITLE = "Especifique um arquivo para salvar";
	public final static String PDF_SELECT_BUTTON_LABEL = "Selecionar arquivo";
	public final static String PDF_SELECT_BUTTON_ICON_PATHNAME = "res/open16.png";
	public final static String EXTRACT_BUTTON_LABEL = "Extrair";
	public final static String EXTRACT_BUTTON_ICON_PATHNAME = "res/extract16.png";
	public final static String PDF_FILE_TYPE_DESCRIPTION = "Documentos PDF (*.pdf)";
	public final static String TXT_FILE_TYPE_DESCRIPTION = "Documentos de texto (*.txt)";
	public final static String OUTPUT_FILE_DELIMITER = "@@";
	public final static String PDF_FILE_TYPE_EXTENSION = ".pdf";
	public final static String TXT_FILE_TYPE_EXTENSION = ".txt";
	public final static String START_PAGE_NUMBER_MESSAGE = "Ler a partir de qual página?";
	public final static String TEXT_AREA_ERROR_LOG_MESSAGE = "ERRO";
	public final static String TEXT_AREA_INFO_LOG_MESSAGE = "INFO";
	public final static String INVALID_PAGE_NUMBER_MESSAGE = "número de página inválido";
	public final static String FILE_SELECTED_MESSAGE = "arquivo selecionado";
	public final static String START_PAGE_CONFIGURED_MESSAGE = "página inicial configurada";
	public final static String SPECIFY_FILE_MESSAGE = "por favor especifique um arquivo";
	public final static String UNEXPECTED_EXTRACTION_ERROR_MESSAGE = "um problema inesperado ocorreu durante a extração";
	public final static String UNEXPECTED_INPUT_READING_ERROR_MESSAGE = "um problema inesperado ocorreu durante a leitura do arquivo";
	public final static String IMAGE_ICON_FILE_NOT_FOUND = "Couldn't find file: ";
	public final static String FILE_READING_MESSAGE = "lendo o arquivo selecionado";
	public final static String FILE_ANALYZING_MESSAGE = "analisando o arquivo selecionado";
	public final static String UNEXPECTED_FILE_CONTENT_MESSAGE = "o arquivo selecionado possui um formato desconhecido";
	public final static String FILE_EXTRACTING_MESSAGE = "extraindo dados do arquivo selecionado";
	public final static String EXPECTED_NUMBER_OF_LINES_MESSAGE = "Qual é o número esperado de linhas?";
	public final static String INVALID_NUMBER_OF_LINES_MESSAGE = "número de itens inválido";
	public final static String WRONG_EXTRACTION_MESSAGE = "a extração não obteve o número esperado de linhas";
	public final static String SUCCESSFUL_EXTRACTION_MESSAGE = "extração realizada com sucesso ";
	public final static String EXTRACTION_DURATION_FORMAT = "HH:mm:ss";
	public final static String PROCESSED_LINES_MESSAGE = "linhas processadas";
	public final static String FILE_SAVING_MESSAGE = "salvando o arquivo especificado";
	public final static String UNEXPECTED_SAVING_ERROR_MESSAGE = "um problema inesperado ocorreu durante o salvamento do arquivo";
	public final static String SUCCESSFUL_FILE_SAVING_MESSAGE = "arquivo gravado com sucesso";
	
	public static void setup() {
		//TODO Test the logging feature
		ApplicationLogger.setup();
	}
}