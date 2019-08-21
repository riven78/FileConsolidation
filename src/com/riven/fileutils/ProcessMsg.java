package com.riven.fileutils;

public class ProcessMsg {

	public int index = 0;
	public int count = 0;
	public String msg = null;

	public ProcessMsg(int index, int count) {
		this.index = index;
		this.count = count;
	}

	public ProcessMsg(int index, int count, String msg) {
		this.index = index;
		this.count = count;
		this.msg = msg;
	}
}
