package com.debadityadey.videoreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.debadityadey.commonutils.TimeMap;
import com.debadityadey.commonutils.GeoLocation;

public class VideoDataReader {
	
	String videoFilePath;
	final double DEFAULT_ALTITUDE = 100; // 100 meters
	public VideoDataReader(String videoFilePath) {
		this.videoFilePath = videoFilePath;
	}

	public HashMap<TimeMap,GeoLocation> readVideo() {
		Video video = new Video();
		File videoFile = video.readFile(videoFilePath);
		HashMap<TimeMap,GeoLocation> locationMap = null;
		if(videoFile != null) {
			try {
				FileReader fr = new FileReader(videoFile);
				BufferedReader br = new BufferedReader(fr); // Reading the srt file
				String line;
				locationMap = new HashMap<>();
				while((line = br.readLine()) != null) {
					if(line.trim().equals(""))
						continue;
					else if(isNumeric(line.trim())) {
						String timeLine = br.readLine(); // read time stamp line
						String[] times = timeLine.split("-->");
						String requiredTimeStamp = times[0].trim();
						String[] timeSplit = requiredTimeStamp.split(":");
						String[] secondsMilliseconds = timeSplit[timeSplit.length-1].split(",");
						int hours = Integer.parseInt(timeSplit[0]);
						int minutes = Integer.parseInt(timeSplit[1]);
						int seconds = Integer.parseInt(secondsMilliseconds[0]);
						int milliseconds = Integer.parseInt(secondsMilliseconds[1]);
						TimeMap timeMap = new TimeMap(hours,
								minutes,
								seconds,
								milliseconds);
						String geoLocationLine = br.readLine(); // read location line
						String[] geoLocationSplit = geoLocationLine.split(",");
						boolean hasAltitude = false;
						if(geoLocationSplit.length == 3) {
							hasAltitude = true;
						}
						double latitude, longitude, altitude;
						if(hasAltitude) {
							latitude = Double.parseDouble(geoLocationSplit[1]);
							longitude = Double.parseDouble(geoLocationSplit[0]);
							altitude = Double.parseDouble(geoLocationSplit[2]);
						} else {
							latitude = Double.parseDouble(geoLocationSplit[1]);
							longitude = Double.parseDouble(geoLocationSplit[0]);
							altitude = DEFAULT_ALTITUDE;
						}
						// System.out.println("Latitude read at "+hours+":"+minutes+":"+seconds+":"+milliseconds
						//		+" is "+latitude);
						// System.out.println("Longitude read at "+hours+":"+minutes+":"+seconds+":"+milliseconds
						//		+" is "+longitude);
						//System.out.println("Altitude read at "+hours+":"+minutes+":"+seconds+":"+milliseconds
						//		+" is "+altitude);
						GeoLocation geoLocation = new GeoLocation(latitude, longitude, altitude);
						
						locationMap.put(timeMap, geoLocation);
					}
				}
			} catch(IOException e) {
				locationMap = null; // making the map null, as total data may not be read.
				System.out.println(e.getMessage());
			}
		}
		return locationMap;
	}
	
	private boolean isNumeric(String toCheck) {
		try {
			int number = Integer.parseInt(toCheck);
		} catch(NumberFormatException e) {
			// System.out.println(toCheck + " is not a number, ignoring.");
			return false;
		}
		// System.out.println(toCheck + " is a number. Mapping it.");
		return true;
	}
	
}
