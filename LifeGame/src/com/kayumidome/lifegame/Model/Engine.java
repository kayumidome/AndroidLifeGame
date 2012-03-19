package com.kayumidome.lifegame.Model;
import java.util.Observable;
import java.util.Observer;

import android.os.AsyncTask;
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
	private EngineCore mCore;
	
	public Engine(final int xSize, final int ySize) {
		this.xGridSize = xSize;
		this.yGridSize = ySize;

		this.mCurrentCells = new GridStatus[this.yGridSize][];
		this.mBackCells = new GridStatus[this.yGridSize][];
		
		for(int y = 0; y < ySize; y++) {
			this.mCurrentCells[y] = new GridStatus[this.xGridSize];
			this.mBackCells[y] = new GridStatus[this.xGridSize];
			for(int x = 0; x < xSize; x++) {
				this.mCurrentCells[y][x] = new GridStatus(x,y);
				this.mBackCells[y][x] = new GridStatus(x,y);
			}
		}
		
		this.setEngineStatus(EngineStatus.Stop);
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
	
	public void setEngineStatus(EngineStatus stat) {
		this.mStat = stat;
		this.statusUpdateSubject.notifyObservers(this.mStat);
	}
	
	public GridStatus[][] getGridStatuses() {
		return this.mCurrentCells;
	}
	
	public GridStatus getGridStatus(int x, int y) {
		return this.mCurrentCells[y][x];
	}
	
	public void setGridStatus(GridStatus stat) {
		this.setGridStatus(stat.getXPosition(), stat.getYPosition(), stat.getAlive());
	}
	
	public void setGridStatus(int x, int y, boolean stat) {
		this.mCurrentCells[y][x].setAlive(stat);
		this.gridUpdateSubject.notifyObservers(this.mCurrentCells);
	}
	
	public void Run() {
		if(this.mStat == EngineStatus.Run) {
			//todo:例外
		}

		this.mCore = new EngineCore(this);
		this.mCore.execute(0);
		
		this.calculateExecuteSubject.notifyObservers(null);
	}
	
	private class EngineCore extends AsyncTask<Integer, Integer, Integer> {
		private Engine mEng;
		private Object mLock = new Object();
		
		public EngineCore(Engine eng) {
			super();
			this.mEng = eng;
		}

		@Override
		protected void onPreExecute() {
			this.mEng.setEngineStatus(EngineStatus.Run);
		}
		
		@Override
		protected Integer doInBackground(Integer... params) {
			while(true) {
				synchronized (this.mLock) {
					boolean ret = this.Calculate();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					this.publishProgress(0);
					if(this.isCancelled() || ret)
						break;
				}
			}
			
			this.mEng.setEngineStatus(EngineStatus.Stop);
			this.mEng.abortSubject.notifyObservers(null);
			return 0;
		}
		
		@Override
		protected void onProgressUpdate(Integer... params) {
			this.mEng.gridUpdateSubject.notifyObservers(this.mEng.mCurrentCells);
		}
		
		private boolean Calculate() {
			boolean totalDestruction = true;
			for(int y = 0; y < this.mEng.yGridSize; y++) {
				for(int x = 0; x < this.mEng.xGridSize; x++) {
					boolean alive = this.CalculateCell(x,y);
					if(alive)
						totalDestruction = false;
				}
			}
			
			GridStatus[][] tmp = this.mEng.mCurrentCells;
			this.mEng.mCurrentCells = this.mEng.mBackCells;
			this.mEng.mBackCells = tmp;
			
			this.mEng.gridUpdateSubject.notifyObservers(this.mEng.mCurrentCells);
			return totalDestruction;
		}

		private boolean CalculateCell(int xPos, int yPos) {
			int count = 0;
			boolean alive = false;
			for(int y = yPos-1; y <= yPos+1; y++) {
				for(int x = xPos-1; x <= xPos+1; x++) {
					if((y < 0) || (this.mEng.yGridSize <= y) || (x < 0) || (this.mEng.xGridSize <= x))
						continue;
					if((y == yPos) && (x == xPos)) {
						alive = this.mEng.mCurrentCells[y][x].getAlive();
						continue;
					}
					
					if(this.mEng.mCurrentCells[y][x].getAlive())
						count++;
				}
			}
			
			if(alive) {
				if((count <= 1) || (4 <= count))
					this.mEng.mBackCells[yPos][xPos].setAlive(false);
				else
					this.mEng.mBackCells[yPos][xPos].setAlive(true);
			}
			else {
				if(count == 3)
					this.mEng.mBackCells[yPos][xPos].setAlive(true);
				else
					this.mEng.mBackCells[yPos][xPos].setAlive(false);
			}
			
			return this.mEng.mBackCells[yPos][xPos].getAlive();
		}
	}
	
	public void abort() {
		this.mCore.cancel(true);
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
			super.notifyObservers(null);
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
			super.notifyObservers(null);
			this.clearChanged();
		}
	}
}
