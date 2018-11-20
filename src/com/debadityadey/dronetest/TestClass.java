package com.debadityadey.dronetest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.debadityadey.commonutils.GeoLocation;
import com.debadityadey.commonutils.HaversianDistanceCalculator;
import com.debadityadey.commonutils.TimeMap;
import com.debadityadey.imagereader.EXIFDataReader;
import com.debadityadey.videoreader.VideoDataReader;

public class TestClass {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		System.out.println("What is the distance between the drone and the places you want insights about?\n");
		double distanceBetween = sc.nextDouble();
		
		EXIFDataReader dataReader = new EXIFDataReader("E:\\Skylark Drones\\technical_assignment_software_developer_4\\software_dev\\images");
		HashMap<String, GeoLocation> exifData = dataReader.readEXIFData();
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		VideoDataReader videoDataReader = new VideoDataReader("E:\\Skylark Drones\\technical_assignment_software_developer_4\\software_dev\\videos\\DJI_0301.SRT");
		HashMap<TimeMap,GeoLocation> videoData = videoDataReader.readVideo();
		Iterator videoDataIter = videoData.entrySet().iterator();
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/*
		 * Pre-process the data in the video file and image files to help in real time and faster insights
		 */
		System.out.println("Pre-processing. Please wait.");
		HashMap<Integer, LinkedList<String>> mapToExport = new HashMap<>();
		int timeChecker = Integer.MIN_VALUE;
		while(videoDataIter.hasNext()) {
			Map.Entry videoDataPair = (Map.Entry)videoDataIter.next();
			double droneLat = ((GeoLocation)videoDataPair.getValue()).getLatitude();
			double droneLong = ((GeoLocation)videoDataPair.getValue()).getLongitude();
			double droneAlt = ((GeoLocation)videoDataPair.getValue()).getAltitude();
			TimeMap droneTimeStamp = (TimeMap)videoDataPair.getKey();
			if(droneTimeStamp.getSeconds() == timeChecker) {
				continue;
			}
			timeChecker = droneTimeStamp.getSeconds();
			Iterator exifDataIter = exifData.entrySet().iterator();
			while(exifDataIter.hasNext()) {
				Map.Entry exifDataPair = (Map.Entry)exifDataIter.next();
				double imageLat = ((GeoLocation)exifDataPair.getValue()).getLatitude();
				double imageLong = ((GeoLocation)exifDataPair.getValue()).getLongitude();
				double imageAlt = ((GeoLocation)exifDataPair.getValue()).getAltitude();
				System.out.println("Distance between drone and image: " + HaversianDistanceCalculator.calculateDistance(droneLat,  imageLat, droneLong, imageLong)*1000);
				if((HaversianDistanceCalculator.calculateDistance(droneLat,  imageLat, droneLong, imageLong)*1000) <= distanceBetween) {
					if(mapToExport.containsKey(timeChecker)) {
						mapToExport.get(timeChecker).add(exifDataPair.getKey().toString());
					} else {
						LinkedList<String> imageList = new LinkedList<>();
						imageList.add(exifDataPair.getKey().toString());
						mapToExport.put(timeChecker, imageList);
					}
				}
			}
		}
		System.out.println("Pre-processing complete.");
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		Iterator exportMapIter = mapToExport.entrySet().iterator();
		String line;
		System.out.println("\n\nExport data:");
		while(exportMapIter.hasNext()) {
			Map.Entry exportDataItem = (Map.Entry)exportMapIter.next();
			line = String.valueOf(exportDataItem.getKey());
			LinkedList<String> imageNames = (LinkedList<String>)exportDataItem.getValue();
			Iterator imageNameIterator = imageNames.iterator();
			JSONArray imageNamesArray = new JSONArray();
			while(imageNameIterator.hasNext()) {
				imageNamesArray.put(imageNameIterator.next().toString());
			}
			line += "," + imageNamesArray.toString();
			
			System.out.println(line);
		}
	}

}
