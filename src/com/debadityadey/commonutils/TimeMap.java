package com.debadityadey.commonutils;

public class TimeMap {
	private int hours;
	private int minutes;
	private int seconds;
	private int milliseconds;
	
	public TimeMap(int hours, int minutes, int seconds, int milliseconds) {
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.milliseconds = milliseconds;
	}
	
	public int getHours() {
		return hours;
	}
	public int getMinutes() {
		return minutes;
	}
	public int getSeconds() {
		return seconds;
	}
	public int getMilliseconds() {
		return milliseconds;
	}
}
