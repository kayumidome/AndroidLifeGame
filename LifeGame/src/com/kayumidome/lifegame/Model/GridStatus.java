package com.kayumidome.lifegame.Model;

public class GridStatus {
	
	public GridStatus() {
		this.x = -1;
		this.y = -1;
		this.alive = false;
	}
	
	private int x;
	
	public int getXPosition() {
		return this.x;
	}
	
	public void setXPosition(int xPos) {
		this.x = xPos;
	}
	
	private int y;
	
	public int getYPosition() {
		return this.y;
	}
	
	public void setYPosition(int yPos) {
		this.y = yPos;
	}
	
	private boolean alive;
	
	public boolean getAlive() {
		return this.alive;
	}
	
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
