package com.kayumidome.lifegame;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.kayumidome.lifegame.Control.Controller;
import com.kayumidome.lifegame.Model.*;

public class LifeGameActivity extends Activity {
	private static int xSize = 13;
	private static int ySize = LifeGameActivity.xSize;
	
	private Engine mEng = new Engine(LifeGameActivity.xSize, LifeGameActivity.ySize);
	private Controller mCntl;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.mCntl = new Controller(this.mEng);
        Button playbtn = (Button)findViewById(R.id.playbutton);
        playbtn.setOnClickListener(new PlayButtonListener(this.mEng));
        this.mEng.setGridUpdateObserver(new GridUpdateObserver());
        this.mEng.setStatusUpdateObserver(new EngineStatusChangeObserver());
        this.mEng.setAbortObserver(new EngineAbortObserver(this.mCntl));
        this.mEng.setCalculateExecuteObserver(new EngineRunObserver(this.mCntl));
        
        GridView grid = (GridView)findViewById(R.id.gridView1);
        grid.setNumColumns(LifeGameActivity.xSize);
        grid.setAdapter(new EmptyGridCreator(LifeGameActivity.xSize, LifeGameActivity.ySize, this.mCntl));
    }
    
    class PlayButtonListener implements OnClickListener {

    	private Engine mEng;
    	
    	public PlayButtonListener(Engine eng) {
    		this.mEng = eng;
    	}
    	
		@Override
		public void onClick(View v) {
			if(this.mEng.getEngineStatus() == EngineStatus.Stop)
				this.mEng.Run();
			else
				this.mEng.abort();
		}
    }
	
	class EngineStatusChangeObserver implements Observer {
		
		private Handler mHdr;
		
		public EngineStatusChangeObserver() {
			this.mHdr = new Handler();
		}
		
		@Override
		public void update(Observable observable, Object data) {
			final EngineStatus stat = (EngineStatus)data;
			
			this.mHdr.post(new Runnable() {
				@Override
				public void run() {
					Button playbtn = (Button)findViewById(R.id.playbutton);
					if(stat == EngineStatus.Run)
						playbtn.setBackgroundResource(android.R.drawable.ic_media_pause);
					else
						playbtn.setBackgroundResource(android.R.drawable.ic_media_play);
					}
				}
			);
		}
	}
	
	class EngineRunObserver implements Observer {
		
		private Controller mCntl;
		
		public EngineRunObserver(Controller cntl) {
			this.mCntl = cntl;
		}

		@Override
		public void update(Observable observable, Object data) {
			this.mCntl.setProhibition(true);
		}
	}
	
	class EngineAbortObserver implements Observer {
		private Controller mCntl;
		
		public EngineAbortObserver(Controller cntl) {
			this.mCntl = cntl;
		}

		@Override
		public void update(Observable observable, Object data) {
			this.mCntl.setProhibition(false);
		}
	}
	
	class GridUpdateObserver implements Observer {
		
		private Handler mHdr;
		
		public GridUpdateObserver() {
			this.mHdr = new Handler();
		}

		@Override
		public void update(Observable observable, Object data) {
			final GridStatus[][] gridStats = (GridStatus[][])data;
			assert gridStats.length != 0;
			assert gridStats[0].length != 0;
			
			this.mHdr.post(new Runnable() {
				@Override
				public void run() {
					GridView grid = (GridView)findViewById(R.id.gridView1);
					
					for(int y = 0; y < gridStats.length; y++) {
						for(int x = 0; x < gridStats[0].length; x++) {
							ImageView img = (ImageView) grid.getChildAt((y*gridStats[0].length)+x);
							if(gridStats[y][x].getAlive())
								img.setImageResource(R.drawable.panel_picture_frame_bg_pressed_blue);
							else
								img.setImageResource(R.drawable.panel_picture_frame_bg_normal);
							}
					}
				}
			}
			);
		}
	}
	
	class EmptyGridCreator extends BaseAdapter {

		private int mX;
		private int mY;
		private Controller mCntl;
		
		EmptyGridCreator(int x, int y, Controller cntl) {
			super();
			
			this.mX = x;
			this.mY = y;
			this.mCntl = cntl;
		}
		
		@Override
		public int getCount() {
			return this.mY * this.mX;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
    	    ImageView imageView;
	    	 
    	    if (convertView == null) {
    	        imageView = new ImageView(LifeGameActivity.this);
    	        imageView.setLayoutParams(new GridView.LayoutParams(40,40));
    	        
    	    } else {
    	        imageView = (ImageView) convertView;
    	    }
    	    
    	    imageView.setImageResource(R.drawable.panel_picture_frame_bg_normal);
    	    imageView.setOnClickListener(new CellClickListener(position%this.mX, (int)(position/this.mX), this.mCntl));
    	 
    	    return imageView;
		}
		
		private class CellClickListener implements OnClickListener {
			private int mXPos;
			private int mYPos;
			private Controller mCntl;
			
			public CellClickListener(int x, int y, Controller cntl) {
				this.mXPos = x;
				this.mYPos = y;
				this.mCntl = cntl;
			}
			
			@Override
			public void onClick(View v) {
				this.mCntl.reverseAlive(this.mXPos, this.mYPos);
			}
		}
	}
}