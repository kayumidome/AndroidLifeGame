package com.kayumidome.lifegame.Model;
import java.util.Observable;
import java.util.Observer;

public class Engine {
	
	private final StatusUpdateSubject statusUpdateSubject = new StatusUpdateSubject();
	private final CalculateExecuteSubject calculateExecuteSubject = new CalculateExecuteSubject();
	private final GridUpdateSubject gridUpdateSubject = new GridUpdateSubject();
	private final AbortSubject abortSubject = new AbortSubject();
	
	public Engine() {
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
	
	private class StatusUpdateSubject extends Observable {
		private EngineStatus.Status mStat;
		
		@Override
		public void  notifyObservers(Object arg) {
			assert !(arg instanceof EngineStatus) : "Engine status observer : illegal argument.";
			EngineStatus statInfo = (EngineStatus)arg;

			if(this.mStat == statInfo.getStatus()){
				return;
			}
			
			this.mStat = statInfo.getStatus();
			
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
			this.setChanged();
			super.notifyObservers();
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
