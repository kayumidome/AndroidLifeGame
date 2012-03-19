package com.kayumidome.lifegame.Model;

public class EngineStartFailedException extends Exception {

	private static final long serialVersionUID = 3674518414879720353L;
	
	public EngineStartFailedException() {
		super();
	}
	
	public EngineStartFailedException(String msg) {
		super(msg);
	}
}
