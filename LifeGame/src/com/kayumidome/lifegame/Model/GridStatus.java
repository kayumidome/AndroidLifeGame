package com.kayumidome.lifegame.Model;

public class GridStatus {
	
	private final int x;
	private final int y;
	
	public GridStatus(final int x, final int y) {
		this.x = x;
		this.y = y;
		this.alive = false;
	}
	
	public int getXPosition() {
		return this.x;
	}
	
	public int getYPosition() {
		return this.y;
	}
	
	private boolean alive;
	
	public boolean getAlive() {
		return this.alive;
	}
	
	public void setAlive(final boolean alive) {
		this.alive = alive;
	}
}
