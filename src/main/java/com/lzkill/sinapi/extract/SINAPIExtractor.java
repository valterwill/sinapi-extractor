package com.lzkill.sinapi.extract;

public interface SINAPIExtractor extends Iterable<String> {
	
	public int getNumberOfProcessedLines();
	
	public int getNumberOfExtractedLines();
	
	public String getRawText();
}
