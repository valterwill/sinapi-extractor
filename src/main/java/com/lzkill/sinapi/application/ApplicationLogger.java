package com.lzkill.sinapi.application;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ApplicationLogger {
	private static FileHandler txtFileHandler;
	private static FileHandler htmlFileHandler;
	private static ConsoleHandler consoleHandler;
	private static SimpleFormatter txtFormatter;
	private static Formatter htmlFormatter;

	public static void setup() {
		try {
			setupDefaultHandlers();
		} catch (IOException e) {
			//TODO Localize it
			new RuntimeException("ERRO: um problema inesperado ocorreu durante a criação dos arquivos de log");
		}
		setupConsoleHandler();
		addHandlers();
	}
	
	private static void setupDefaultHandlers() throws IOException {
		//TODO Refactor to a better place for the log files
		//TODO Refactor not to create a new file if it already exists
		txtFileHandler = new FileHandler(Application.PLAIN_TEXT_LOG_PATHNAME);
		htmlFileHandler = new FileHandler(Application.HTML_LOG_PATHNAME);
		
		txtFileHandler.setLevel(Application.FILE_LOG_LEVEL);
		htmlFileHandler.setLevel(Application.FILE_LOG_LEVEL);
		
		txtFormatter = new SimpleFormatter();
		htmlFormatter = new HTMLLogFormatter();
		
		txtFileHandler.setFormatter(txtFormatter);
		htmlFileHandler.setFormatter(htmlFormatter);
	}

	private static void setupConsoleHandler() {
		consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Application.CONSOLE_LOG_LEVEL);
	}

	private static void addHandlers() {
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.addHandler(txtFileHandler);
		logger.addHandler(htmlFileHandler);
		if (Application.DEBUG_MODE)
			logger.addHandler(consoleHandler);
	}
}
