package com.kayumidome.lifegame.Model;

public class SettingManager {
	
	public enum Speed {
		slow,
		normal,
		high,
		max,
	}

	public SettingManager() {
		//todo:設定ファイルから読み込んだり、ファイルがなければ作ったりする。
	}
	
	public void Save() {
		//todo:現在の設定を設定ファイルに保存する。
	}
	
	private Speed speed;
	
	public Speed getSpeed() {
		return this.speed;
	}
	
	public void setSpeed(Speed speed) {
		this.speed = speed;
		//todo:スピード設定変更に伴う処理
	}
}
