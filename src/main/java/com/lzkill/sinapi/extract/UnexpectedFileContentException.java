package com.lzkill.sinapi.extract;

public class UnexpectedFileContentException extends Exception {
	private static final long serialVersionUID = 4732875002715913680L;
	
	public UnexpectedFileContentException(String message) {
		super(message);
	}
}
