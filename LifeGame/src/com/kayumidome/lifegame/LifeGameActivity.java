package com.kayumidome.lifegame;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.kayumidome.lifegame.Model.*;

public class LifeGameActivity extends Activity {
	private static int xSize = 10;
	private static int ySize = LifeGameActivity.xSize;
	
	private Engine mEng = new Engine(LifeGameActivity.xSize, LifeGameActivity.ySize);
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button playbtn = (Button)findViewById(R.id.playbutton);
        playbtn.setOnClickListener(new PlayButtonListener(this.mEng));
        this.mEng.setStatusUpdateObserver(new EngineStatusChangeObserver());
        this.mEng.setGridUpdateObserver(new GridUpdateObserver());
        
        GridView grid = (GridView)findViewById(R.id.gridView1);
        grid.setNumColumns(LifeGameActivity.xSize);
        grid.setAdapter(new EmptyGridCreator(LifeGameActivity.xSize, LifeGameActivity.ySize));
    }
    
    class PlayButtonListener implements OnClickListener {

    	private Engine mEng;
    	
    	public PlayButtonListener(Engine eng) {
    		super();
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
		
		@Override
		public void update(Observable observable, Object data) {
			EngineStatus stat = (EngineStatus)data;
			Button playbtn = (Button)findViewById(R.id.playbutton);
			
			if(stat == EngineStatus.Run)
				playbtn.setBackgroundResource(android.R.drawable.ic_media_pause);
			else
				playbtn.setBackgroundResource(android.R.drawable.ic_media_play);
		}
	}
	
	class EngineRunObserver implements Observer {

		@Override
		public void update(Observable observable, Object data) {
			// TODO　グリッドへの入力を受け付けないように
			
		}
		
	}
	
	class EngineAbortObserver implements Observer {

		@Override
		public void update(Observable observable, Object data) {
			// TODO グリッドの入力を受け付けるように
			
		}
		
	}
	
	class GridUpdateObserver implements Observer {

		@Override
		public void update(Observable observable, Object data) {
			GridStatus[][] gridStats = (GridStatus[][])data;
			assert gridStats.length != 0;
			assert gridStats[0].length != 0;
			
			GridView grid = (GridView)findViewById(R.id.gridView1);
			
			grid.removeAllViews();
			grid.setAdapter(new GridCreator(gridStats));
		}
		
	    public class GridCreator extends BaseAdapter {
	    	
	    	private GridStatus[][] mData;
	    	
	    	public GridCreator(GridStatus[][] data) {
	    		this.mData = data;
	    	}
	    	
	    	public View getView(int position, View convertView, ViewGroup parent) {
	    	    ImageView imageView;
	    	 
	    	    if (convertView == null) {
	    	        imageView = new ImageView(LifeGameActivity.this);
	    	        imageView.setLayoutParams(new GridView.LayoutParams(50, 50));
	    	        
	    	    } else {
	    	        imageView = (ImageView) convertView;
	    	    }
	    	    
	    	    if(this.mData[(int)position/this.mData.length][position%this.mData.length].getAlive())
	    	    	imageView.setImageResource(R.drawable.panel_picture_frame_bg_pressed_blue);
	    	    else
	    	    	imageView.setImageResource(R.drawable.panel_picture_frame_bg_normal);
	    	 
	    	    return imageView;
	    	}

			@Override
			public int getCount() {
				return this.mData.length * this.mData[0].length;
			}

			@Override
			public Object getItem(int position) {
				return position;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}
	    }
	}
	
	class EmptyGridCreator extends BaseAdapter {

		private int mX;
		private int mY;
		
		EmptyGridCreator(int x, int y) {
			this.mX = x;
			this.mY = y;
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
    	        imageView.setLayoutParams(new GridView.LayoutParams(50, 50));
    	        
    	    } else {
    	        imageView = (ImageView) convertView;
    	    }
    	    
    	    imageView.setImageResource(R.drawable.panel_picture_frame_bg_normal);
    	 
    	    return imageView;
		}
	}
}