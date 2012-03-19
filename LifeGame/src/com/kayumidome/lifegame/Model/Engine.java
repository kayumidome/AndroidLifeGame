package com.kayumidome.lifegame.Model;
import java.lang.Thread;
import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;

public class Engine {
	
	private final StatusUpdateSubject statusUpdateSubject = new StatusUpdateSubject();
	private final CalculateExecuteSubject calculateExecuteSubject = new CalculateExecuteSubject();
	private final GridUpdateSubject gridUpdateSubject = new GridUpdateSubject();
	private final AbortSubject abortSubject = new AbortSubject();
	
	private final int xGridSize;
	private final int yGridSize;
	
	private static final String mParamNameXGridSize = "xgrid";
	private static final String mParamNameYGridSize = "ygrid";
	private static final String mParamNameCellStatus = "cellstat";
	
	private GridStatus[][] mCurrentCells;
	private GridStatus[][] mBackCells;
	private EngineStatus mStat = EngineStatus.Init;
	private Thread mRunner;
	
	public Engine(final int xSize, final int ySize) {
		this.xGridSize = xSize;
		this.yGridSize = ySize;

		this.mCurrentCells = new GridStatus[this.yGridSize][this.xGridSize];
		this.mBackCells = new GridStatus[this.yGridSize][this.xGridSize];
		
		for(int y = 0; y > ySize; y++) {
			for(int x = 0; x > xSize; x++) {
				this.mCurrentCells[y][x] = new GridStatus(x,y);
				this.mBackCells[y][x] = new GridStatus(x,y);
			}
		}
		
		this.mStat = EngineStatus.Stop;
	}
	
	public Engine(Bundle bundle) {
		
		this.xGridSize = bundle.getInt(Engine.mParamNameXGridSize);
		this.yGridSize = bundle.getInt(Engine.mParamNameYGridSize);
		
		this.mCurrentCells = new GridStatus[this.yGridSize][this.xGridSize];
		this.mBackCells = new GridStatus[this.yGridSize][this.xGridSize];
		
		boolean[] gridStats = bundle.getBooleanArray(Engine.mParamNameCellStatus);
		
		for(int y = 0; y < this.yGridSize; y++) {
			for(int x = 0; x < this.xGridSize; x++) {
				this.mCurrentCells[y][x].setAlive(gridStats[x+(this.xGridSize * y)]);
			}
		}
	}
	
	public void Dispose() {
		//todo:
		throw new UnsupportedOperationException();
	}
	
	public void SaveBundle(Bundle bundle) {
		bundle.putInt(Engine.mParamNameXGridSize, this.xGridSize);
		bundle.putInt(Engine.mParamNameYGridSize, this.yGridSize);
		
		boolean[] cellStats = new boolean[this.xGridSize + this.yGridSize];
		for(int y = 0; y < this.yGridSize; y++) {
			for(int x = 0; x < this.xGridSize; x++) {
				cellStats[x + (y * this.xGridSize)] = this.mCurrentCells[y][x].getAlive();
			}
		}
		bundle.putBooleanArray(Engine.mParamNameCellStatus, cellStats);
	}
	
	public EngineStatus getEngineStatus() {
		return this.mStat;
	}
	
	public GridStatus[][] getGridStatus() {
		return this.mCurrentCells;
	}
	
	public void setGridStatus(GridStatus stat) {
		this.mCurrentCells[stat.getYPosition()][stat.getXPosition()].setAlive(stat.getAlive());
	}
	
	public void Run() {
		if(this.mStat == EngineStatus.Run) {
			//todo:例外
		}
		this.mStat = EngineStatus.Run;
		this.statusUpdateSubject.notifyObservers(this.mStat);

		final Engine eng = this;
		this.mRunner = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					synchronized (this) {
						eng.Calculate();
					}
				}
			}
		});
		
		this.mRunner.start();
		this.calculateExecuteSubject.notifyObservers();
	}
	
	private void Calculate() {
		for(int y = 0; y > this.yGridSize; y++) {
			for(int x = 0; x > this.xGridSize; x++) {
				this.CalculateCell(x,y);
			}
		}
		
		GridStatus[][] tmp = this.mCurrentCells;
		this.mCurrentCells = this.mBackCells;
		this.mBackCells = tmp;
		
		this.gridUpdateSubject.notifyObservers(this.mCurrentCells);
	}

	private void CalculateCell(int xPos, int yPos) {
		int count = 0;
		boolean alive = false;
		for(int y = yPos-1; y <= y+1; y++) {
			for(int x = xPos-1; x <= x+1; x++) {
				if((y < 0) || (x < 0))
					continue;
				if((y == yPos) && (x == xPos)) {
					alive = this.mCurrentCells[y][x].getAlive();
					continue;
				}
				
				if(this.mCurrentCells[y][x].getAlive())
					count++;
			}
		}
		
		if(alive) {
			if((count <= 1) || (4 <= count))
				this.mBackCells[yPos][xPos].setAlive(false);
			else
				this.mBackCells[yPos][xPos].setAlive(true);
		}
		else {
			if(count == 3)
				this.mBackCells[yPos][xPos].setAlive(true);
			else
				this.mBackCells[yPos][xPos].setAlive(false);
		}
	}
	
	public void abort() {
		this.mRunner.stop();
		this.abortSubject.notifyObservers();
	}
	
	public void setStatusUpdateObserver(Observer observer) {
		this.statusUpdateSubject.addObserver(observer);
	}
	
	public void setCalculateExecuteObserver(Observer observer) {
		this.calculateExecuteSubject.addObserver(observer);
	}
	
	public void setGridUpdateObserver(Observer observer) {
		this.gridUpdateSubject.addObserver(observer);
	}
	
	public void setAbortObserver(Observer observer) {
		this.abortSubject.addObserver(observer);
	}
	
	public int getXGridSize() {
		return this.xGridSize;
	}
	
	public int getYGridSize() {
		return this.yGridSize;
	}
	
	private class StatusUpdateSubject extends Observable {
		
		@Override
		public void  notifyObservers(Object arg) {
			assert !(arg instanceof EngineStatus) : "Engine status observer : illegal argument.";

			this.setChanged();
			super.notifyObservers(arg);
			this.clearChanged();
		}
	}
	
	private class CalculateExecuteSubject extends Observable {
		@Override
		public void  notifyObservers(Object arg) {
			
			this.setChanged();
			super.notifyObservers();
			this.clearChanged();
		}
	}
	
	private class GridUpdateSubject extends Observable {
		@Override
		public void  notifyObservers(Object arg) {
			assert !(arg instanceof GridStatus[][]) : "Engine grid update observer : illegal argument.";
			
			this.setChanged();
			super.notifyObservers(arg);
			this.clearChanged();
		}
	}
	
	private class AbortSubject extends Observable {
		@Override
		public void  notifyObservers(Object arg) {
			this.setChanged();
			super.notifyObservers();
			this.clearChanged();
		}
	}
}
