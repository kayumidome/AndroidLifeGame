package com.kayumidome.lifegame.Model;
import java.util.Observable;

public class Engine {
	
	private class StatusUpdateObserver extends Observable {
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
	
	private class CalculateExecuteObserver extends Observable {
		@Override
		public void  notifyObservers(Object arg) {
			this.setChanged();
			super.notifyObservers();
			this.clearChanged();
		}
	}
	
	private class GridUpdateObserver extends Observable {
		@Override
		public void  notifyObservers(Object arg) {
			this.setChanged();
			super.notifyObservers();
			this.clearChanged();
		}
	}
	
	private class AbortObserver extends Observable {
		@Override
		public void  notifyObservers(Object arg) {
			this.setChanged();
			super.notifyObservers();
			this.clearChanged();
		}
	}
}
