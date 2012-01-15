package com.kayumidome.lifegame.Model;

public class SettingManager {
	
	public enum Speed {
		slow,
		normal,
		high,
		max,
	}

	public SettingManager() {
		//todo:設定ファイルから情報を読み込んだり、ファイルがなければ作ったり
	}
	
	public void Save() {
		//todo:現在の設定をファイルに保存
	}
	
	private Speed speed;
	
	public Speed getSpeed() {
		return this.speed;
	}
	
	public void setSpeed(Speed speed) {
		this.speed = speed;
		//todo:スピード変更に伴う処理
	}
}
