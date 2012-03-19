package com.kayumidome.lifegame.Control;

import com.kayumidome.lifegame.Model.*;

public class Controller {
	private Engine mEng;
	private boolean mProhibition;
	
	public Controller(Engine engine) {
		this.mEng = engine;
		this.mProhibition = false;
	}
	
	public void setAlive(int x, int y, boolean alive) {
		if(this.mProhibition)
			return;
		this.mEng.setGridStatus(x, y, alive);
	}
	
	public void reverseAlive(int x, int y) {
		if(this.mProhibition)
			return;
		GridStatus stat = this.mEng.getGridStatus(x, y);
		if(stat.getAlive())
			this.mEng.setGridStatus(x, y, false);
		else
			this.mEng.setGridStatus(x, y, true);
	}
	
	public void setProhibition(boolean prohibition) {
		this.mProhibition = prohibition;
	}
}
