package com.debadityadey.dronemain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONArray;

import com.debadityadey.assetshandler.AssetsDataHandler;
import com.debadityadey.commonutils.GeoLocation;
import com.debadityadey.commonutils.HaversianDistanceCalculator;
import com.debadityadey.commonutils.TimeMap;
import com.debadityadey.imagereader.EXIFDataReader;
import com.debadityadey.videoreader.VideoDataReader;

public class DroneMain {

public static void main(String[] args) {
	
		double distanceBetween = 0D;
		String videoFilePath = null;
		String imagesFilePath = null;
		
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(isr);
		try {
			System.out.println("What is the distance(in meters) between the drone and the places you want insights about?");
			distanceBetween = Double.parseDouble(reader.readLine());
			System.out.println("Enter location of the video file:");
			videoFilePath = reader.readLine();
			System.out.println("Enter location of the image files directory:");
			imagesFilePath = reader.readLine();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		
		System.out.println("Video file path entered: "+videoFilePath);
		System.out.println("Images file path entered: "+imagesFilePath);
		
		//E:\\Skylark Drones\\technical_assignment_software_developer_4\\software_dev\\images
		//E:\\Skylark Drones\\technical_assignment_software_developer_4\\software_dev\\videos\\DJI_0301.SRT
		EXIFDataReader dataReader = new EXIFDataReader(imagesFilePath);
		HashMap<String, GeoLocation> exifData = dataReader.readEXIFData();
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		VideoDataReader videoDataReader = new VideoDataReader(videoFilePath);
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
				//System.out.println("Distance between drone and image: " + HaversianDistanceCalculator.calculateDistance(droneLat,  imageLat, droneLong, imageLong));
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
		String fileData = "";
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
			String imageNamesList = imageNamesArray.toString().replaceAll(",", ";");
			line += "," + imageNamesList;
			fileData += line+"\n";
			System.out.println(line);
		}
		
		System.out.println("Exporting file in folder \"Exports\"");
		File exportDir = new File("Exports");
		if(!exportDir.exists()) {
			boolean dirCreated = exportDir.mkdirs();
			if(dirCreated) {
				exportFiles(fileData, exportDir, false);
			} else {
				System.out.println("Error in creating directory and writing files.");
			}
		} else {
			exportFiles(fileData, exportDir, false);
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		/// ASSETS ///
		String assetsFilePath = null;
		try {
			System.out.println("Enter the file path of the assets file: ");
			assetsFilePath = reader.readLine();
			System.out.println("What is the distance(in meters) between the drone and the assets you want insights about?");
			distanceBetween = Double.parseDouble(reader.readLine());
		} catch(IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		AssetsDataHandler assetsDataHandler = new AssetsDataHandler(assetsFilePath);
		HashMap<String, GeoLocation> assetsData = assetsDataHandler.readAssets();
		
		HashMap<String, LinkedList<String>> assetMapToExport = new HashMap<>();
		
		Iterator assetDataIterator = assetsData.entrySet().iterator();
		while(assetDataIterator.hasNext()) {
			Map.Entry assetDataPair = (Map.Entry)assetDataIterator.next();
			double assetLat = ((GeoLocation)assetDataPair.getValue()).getLatitude();
			double assetLong = ((GeoLocation)assetDataPair.getValue()).getLongitude();
			Iterator exifDataIter = exifData.entrySet().iterator();
			while(exifDataIter.hasNext()) {
				Map.Entry exifDataPair = (Map.Entry)exifDataIter.next();
				double imageLat = ((GeoLocation)exifDataPair.getValue()).getLatitude();
				double imageLong = ((GeoLocation)exifDataPair.getValue()).getLongitude();
				if((HaversianDistanceCalculator.calculateDistance(assetLat,  imageLat, assetLong, imageLong)*1000) <= distanceBetween) {
					if(assetMapToExport.containsKey(assetDataPair.getKey().toString()
							+ "," + String.valueOf(assetLong)
							+ "," + String.valueOf(assetLat))) {
						
						assetMapToExport.get(assetDataPair.getKey().toString()
							+ "," + String.valueOf(assetLong)
							+ "," + String.valueOf(assetLat)).add(exifDataPair.getKey().toString());
						
					} else {
						LinkedList<String> nameList = new LinkedList<>();
						nameList.add(exifDataPair.getKey().toString());
						
						assetMapToExport.put(assetDataPair.getKey().toString()
							+ "," + String.valueOf(assetLong)
							+ "," + String.valueOf(assetLat), nameList);
					}
				}
			}
		}
		
		exportMapIter = assetMapToExport.entrySet().iterator();
		fileData = "";
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
			String imageNamesList = imageNamesArray.toString().replaceAll(",", ";");
			line += "," + imageNamesList;
			fileData += line+"\n";
			System.out.println(line);
		}
		
		System.out.println("Exporting file in folder \"Exports\"");
		exportDir = new File("Exports");
		if(!exportDir.exists()) {
			boolean dirCreated = exportDir.mkdirs();
			if(dirCreated) {
				exportFiles(fileData, exportDir, true);
			} else {
				System.out.println("Error in creating directory and writing files.");
			}
		} else {
			exportFiles(fileData, exportDir, true);
		}
		
	}

	private static void exportFiles(String fileData, File exportDir, boolean isAsset) {
		File newCsvFile;
		if(isAsset) {
			newCsvFile = new File(exportDir.getAbsolutePath()+File.separator+"EXPORT_DATA_ASSET_" + Calendar.getInstance().getTimeInMillis() + ".csv");
		} else {
			newCsvFile = new File(exportDir.getAbsolutePath()+File.separator+"EXPORT_DATA_" + Calendar.getInstance().getTimeInMillis() + ".csv");
		}
		try {
			if(newCsvFile.createNewFile()) {
				FileOutputStream fos = new FileOutputStream(newCsvFile);
				fos.write(fileData.getBytes());
				fos.close();
				System.out.println("File successfully written.");
			}
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
}
