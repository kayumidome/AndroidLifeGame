package com.kayumidome.lifegame.Model;

public class SettingManager {
	
	public enum Speed {
		slow,
		normal,
		high,
		max,
	}

	public SettingManager() {
		//todo:�ݒ�t�@�C���������ǂݍ��񂾂�A�t�@�C�����Ȃ���΍������
	}
	
	public void Save() {
		//todo:���݂̐ݒ���t�@�C���ɕۑ�
	}
	
	private Speed speed;
	
	public Speed getSpeed() {
		return this.speed;
	}
	
	public void setSpeed(Speed speed) {
		this.speed = speed;
		//todo:�X�s�[�h�ύX�ɔ�������
	}
}
