package com.kayumidome.lifegame.Model;

public class EngineStatus {
	public enum Status {
		Undefined,
		Run,
		Stop,
		Init,
	}
	
	private final Status mStatus;
	
	public EngineStatus(Status stat) {
		this.mStatus = stat;
	}
	
	public Status getStatus() {
		return this.mStatus;
	}
}
