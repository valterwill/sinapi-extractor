package com.lzkill.sinapi.application;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FileUtils;

import com.lzkill.sinapi.extract.SINAPIExtractor;
import com.lzkill.sinapi.extract.SINAPIExtractorFactory;
import com.lzkill.sinapi.extract.UnexpectedFileContentException;

//TODO Sort methods for better reading
public class MainWindow extends JPanel implements ActionListener,
		PropertyChangeListener {

	private static final long serialVersionUID = -1422789535878271027L;

	// Layout components
	private static JButton inputFileSelectButton;
	private static JButton extractButton;
	private static JFileChooser pdfFileChooser;
	private static JFileChooser txtFileChooser;
	private static JProgressBar progressBar;
	private static JScrollPane logScrollPane;
	private static JPanel buttonsPanel;
	private static JPanel progressBarPanel;
	private static JTextArea logTextArea;

	private static String inputFileName;
	private static String outputFileName;
	private static int startPageNumber = 1;
	private static String rawInputText;
	private static String extractedLines;

	private static final Logger LOGGER = Logger.getLogger(MainWindow.class
			.getName());

	public MainWindow() {
		super(new BorderLayout());
		setupComponents();
		setupLayout();
		initComponents();
	}

	private void setupComponents() {
		setupLogTextArea();
		setupPDFFileChooser();
		setupTXTFileChooser();
		setupPDFFileSelectButton();
		setupExtractButton();
		setupButtonPanel();
		setupProgressBar();
		setupProgressBarPanel();
		setupLogScrollPane();
	}

	private void setupLayout() {
		add(buttonsPanel, BorderLayout.PAGE_START);
		add(progressBarPanel, BorderLayout.PAGE_END);
		add(logScrollPane, BorderLayout.CENTER);
	}

	private void initComponents() {
		disableExtractButton();
		setProgressBarDeterminate();
	}

	// TODO Extract constants to Application class
	// TODO Adjust text area size to fit the file path length
	private void setupLogTextArea() {
		logTextArea = new JTextArea(15, 35);
		logTextArea.setMargin(new Insets(5, 5, 5, 5));
		logTextArea.setEditable(false);
	}

	private void setupPDFFileSelectButton() {
		inputFileSelectButton = new JButton(
				Application.PDF_SELECT_BUTTON_LABEL,
				Utils.loadImageIcon(Application.PDF_SELECT_BUTTON_ICON_PATHNAME));
		inputFileSelectButton.addActionListener(this);
	}

	private void setupExtractButton() {
		extractButton = new JButton(Application.EXTRACT_BUTTON_LABEL,
				Utils.loadImageIcon(Application.EXTRACT_BUTTON_ICON_PATHNAME));
		extractButton.addActionListener(this);
	}

	private void setupProgressBar() {
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setString("");
	}

	private void setupButtonPanel() {
		buttonsPanel = new JPanel();
		buttonsPanel.add(inputFileSelectButton);
		buttonsPanel.add(extractButton);
	}

	private void setupProgressBarPanel() {
		progressBarPanel = new JPanel();
		progressBarPanel.add(progressBar);
	}

	private void setupLogScrollPane() {
		logScrollPane = new JScrollPane(logTextArea);
	}

	private void setupPDFFileChooser() {
		pdfFileChooser = new JFileChooser();
		pdfFileChooser.setDialogTitle(Application.OPEN_FILE_WINDOW_TITLE);
		pdfFileChooser.setAcceptAllFileFilterUsed(false);

		pdfFileChooser.addChoosableFileFilter(new FileFilter() {
			public String getDescription() {
				return Application.PDF_FILE_TYPE_DESCRIPTION;
			}

			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				} else {
					return f.getName().toLowerCase()
							.endsWith(Application.PDF_FILE_TYPE_EXTENSION);
				}
			}
		});
	}

	private void setupTXTFileChooser() {
		txtFileChooser = new JFileChooser();
		txtFileChooser.setDialogTitle(Application.SAVE_FILE_WINDOW_TITLE);
		txtFileChooser.setAcceptAllFileFilterUsed(false);

		txtFileChooser.addChoosableFileFilter(new FileFilter() {
			public String getDescription() {
				return Application.TXT_FILE_TYPE_DESCRIPTION;
			}

			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				} else {
					return f.getName().toLowerCase()
							.endsWith(Application.TXT_FILE_TYPE_EXTENSION);
				}
			}
		});
	}

	private void enableExtractButton() {
		extractButton.setEnabled(true);
	}

	private void disableExtractButton() {
		extractButton.setEnabled(false);
	}

	private void enableInputFileSelectButton() {
		inputFileSelectButton.setEnabled(true);
	}

	private void disableInputFileSelectButton() {
		inputFileSelectButton.setEnabled(false);
	}

	private void setProgressBarDeterminate() {
		progressBar.setIndeterminate(false);
	}

	private void setProgressBarIndeterminate() {
		progressBar.setIndeterminate(true);
	}

	private void enableWaitCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	private void disableWaitCursor() {
		setCursor(null);
	}

	// TODO Refactor for better naming
	private void setWaitModeOn() {
		disableInputFileSelectButton();
		disableExtractButton();
		setProgressBarIndeterminate();
		enableWaitCursor();
	}

	// TODO Refactor for better naming
	private void setWaitModeOff() {
		enableInputFileSelectButton();
		enableExtractButton();
		setProgressBarDeterminate();
		disableWaitCursor();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == inputFileSelectButton) {
			selectInputFile();
			if (wasInputFileSelected()) {
				askForStartPageNumber();
				setWaitModeOn(); //setWaitModeOff is called in the task's done()
				readInputFile();
			}

		} else if (e.getSource() == extractButton) {
			setWaitModeOn(); //setWaitModeOff is called in the task's done()
			extract();
		}
	}

	private void selectInputFile() {
		int selectionStatus = pdfFileChooser.showOpenDialog(MainWindow.this);
		if (selectionStatus == JFileChooser.APPROVE_OPTION) {
			setInputFileName();
		}
	}

	private void setInputFileName() {
		File inputFile = pdfFileChooser.getSelectedFile();
		inputFileName = inputFile.getAbsolutePath();
	}

	private boolean wasInputFileSelected() {
		return inputFileName != null;
	}

	private void readInputFile() {
		InputReadTask inputReadTask = new InputReadTask();
		try {
			inputReadTask.execute();
		} catch (Exception e) {
			TextAreaLogger
					.error(Application.UNEXPECTED_INPUT_READING_ERROR_MESSAGE);
			LOGGER.severe(e.getMessage());
		}
	}

	private void extract() {
		ExtractionTask extractionTask = new ExtractionTask(rawInputText);
		try {
			extractionTask.execute();
		} catch (Exception e) {
			TextAreaLogger
					.error(Application.UNEXPECTED_EXTRACTION_ERROR_MESSAGE);
			LOGGER.severe(e.getMessage());
		}
	}

	private SINAPIExtractor getExtractor(String rawText) {
		SINAPIExtractor extractor = null;
		try {
			extractor = SINAPIExtractorFactory.create(rawText,
					Application.OUTPUT_FILE_DELIMITER);
		} catch (UnexpectedFileContentException e) {
			TextAreaLogger.error(Application.UNEXPECTED_FILE_CONTENT_MESSAGE);
		}
		return extractor;
	}

	private void askForStartPageNumber() {
		String providedPageNumber = JOptionPane.showInputDialog(this,
				Application.START_PAGE_NUMBER_MESSAGE, 1);

		// TODO Refactor this if statement for better reading
		if (providedPageNumber != null && !providedPageNumber.isEmpty()) {
			try {
				startPageNumber = Integer.parseInt(providedPageNumber);
			} catch (NumberFormatException e1) {
				TextAreaLogger.error(Application.INVALID_PAGE_NUMBER_MESSAGE);
			}
		}

		TextAreaLogger.info(Application.START_PAGE_CONFIGURED_MESSAGE + " ["
				+ startPageNumber + "]");
	}

	private void handleOutputSaving() {
		selectOutputFile();
		if (wasOutputFileSelected()) {
			saveOutput();
		}
	}

	private void selectOutputFile() {
		int userSelection = txtFileChooser.showSaveDialog(MainWindow.this);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			setOutputFileName();
			TextAreaLogger.info(Application.FILE_SAVING_MESSAGE + " ["
					+ outputFileName + "]");
		}
	}

	private void setOutputFileName() {
		File outputFile = txtFileChooser.getSelectedFile();
		outputFileName = outputFile.getAbsoluteFile() + ".txt";
	}

	private boolean wasOutputFileSelected() {
		return outputFileName != null;
	}

	private void saveOutput() {
		try {
			FileUtils.writeStringToFile(new File(outputFileName),
					extractedLines);
			TextAreaLogger.info(Application.SUCCESSFUL_FILE_SAVING_MESSAGE);
		} catch (IOException e) {
			TextAreaLogger.error(Application.UNEXPECTED_SAVING_ERROR_MESSAGE);
			LOGGER.severe(e.getMessage());
		}
	}

	private void resetState() {
		inputFileName = null;
		outputFileName = null;
		startPageNumber = 1;
		rawInputText = null;
		extractedLines = null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

	}

	private class InputReadTask extends SwingWorker<Void, Void> {

		@Override
		public Void doInBackground() {
			TextAreaLogger.info(Application.FILE_READING_MESSAGE + " ["
					+ inputFileName + "]");
			rawInputText = Utils.extractPlainTextFromPDFFile(inputFileName,
					startPageNumber);
			return null;
		}

		@Override
		public void done() {
			setWaitModeOff();
			enableExtractButton();
		}
	}

	private class ExtractionTask extends SwingWorker<Void, Void> {

		private SINAPIExtractor extractor;
		private String rawText;
		private String result = "";

		public ExtractionTask(String rawText) {
			this.rawText = rawText;
		}

		@Override
		public Void doInBackground() {
			TextAreaLogger.info(Application.FILE_ANALYZING_MESSAGE);
			extractor = getExtractor(rawText);
			if (extractor != null) {
				TextAreaLogger.info(Application.FILE_EXTRACTING_MESSAGE);
				extract();
			}

			return null;
		}

		private void extract() {
			Iterator<String> it = extractor.iterator();
			while (it.hasNext()) {
				result += it.next();
			}
		}

		@Override
		public void done() {
			alertFinished();
			setWaitModeOff();
			disableExtractButton();
			handleResult();
			resetState();
		}

		private void handleResult() {
			if (extractor != null) {
				extractedLines = result;
				handleOutputSaving();
			}
		}

		private void alertFinished() {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	private static class TextAreaLogger {
		public static void error(String message) {
			log(Application.TEXT_AREA_ERROR_LOG_MESSAGE, message);
			refresh();
		}

		public static void info(String message) {
			log(Application.TEXT_AREA_INFO_LOG_MESSAGE, message);
			refresh();
		}

		private static void log(String messageType, String message) {
			logTextArea.append(messageType + ": " + message + "\n");

		}

		private static void refresh() {
			logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
			logTextArea.update(logTextArea.getGraphics());
		}
	}

	private static JFrame createGUI() {
		JFrame frame = new JFrame(Application.MAIN_WINDOW_TITLE + " "
				+ Application.APP_VERSION);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(Utils.loadImageIcon(Application.APP_ICON_PATHNAME)
				.getImage());
		frame.add(new MainWindow());
		frame.pack();
		return frame;
	}

	private static void showGUI(final JFrame frame) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}

	public static void main(String[] args) {
		Application.setup();
		JFrame frame = createGUI();
		showGUI(frame);
	}
}
