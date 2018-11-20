package com.debadityadey.assetshandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.debadityadey.commonutils.GeoLocation;

public class AssetsDataHandler {

	String assetsFilePath;
	double DEFAULT_ALTITUDE = 100;
	
	public AssetsDataHandler(String assetsFilePath) {
		this.assetsFilePath = assetsFilePath;
	}
	
	public HashMap<String, GeoLocation> readAssets() {
		HashMap<String, GeoLocation> assets = new HashMap<>();
		
		BufferedReader fileReader;
		String line;
		final String header = "asset_name,longitude,latitude,image_names";
		try {
			fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(assetsFilePath))));
			while((line = fileReader.readLine())!=null) {
				if(line.trim().toLowerCase().equals(header.toLowerCase())) {
					continue;
				}
				String[] assetData = line.split(",");
				String assetName = assetData[0].trim();
				double longitude = Double.parseDouble(assetData[1].trim());
				double latitude = Double.parseDouble(assetData[2].trim());
				GeoLocation assetLocation = new GeoLocation(latitude, longitude, DEFAULT_ALTITUDE);
				assets.put(assetName, assetLocation);
			}
		} catch(IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		
		return assets;
	}
	
}
