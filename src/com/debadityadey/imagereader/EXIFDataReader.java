package com.debadityadey.imagereader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import com.debadityadey.commonutils.GeoLocation;
import com.debadityadey.commonutils.GeoTag;
import com.debadityadey.commonutils.JpegGeoTagReader;
import com.debadityadey.commonutils.TimeMap;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class EXIFDataReader {

	String imageFilesPath;
	
	public EXIFDataReader(String imageFilesPath) {
		this.imageFilesPath = imageFilesPath;
	}
	
	private ArrayList<File> readImages() {
		
		File imageFilesDirectory = new File(imageFilesPath);
		ArrayList<File> imageFiles = null;
		if(imageFilesDirectory.isDirectory()) {
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					String lowercaseName = name.toLowerCase();
					if (lowercaseName.endsWith(".jpg")
							|| lowercaseName.endsWith(".png")) {
						return true;
					} else {
						return false;
					}
				}
			};
			String[] imageFileNames = imageFilesDirectory.list(filter);
			imageFiles = new ArrayList<>();
			for(String imageFileName : imageFileNames) {
				Image image = new Image();
				imageFiles.add(image.readFile(imageFilesDirectory.getAbsolutePath()
						+File.separator
						+imageFileName));
			}
		}
		return imageFiles;
		
	}
	
	public HashMap<String, GeoLocation> readEXIFData() {
		
		HashMap<String,GeoLocation> imageMap = new HashMap<>();
		ArrayList<File> images = readImages();
		for(File image : images) {
			// System.out.println("Filename under consideration is: "+image.getName());
			try {
				GeoLocation geoLocation;
				double latitude = Double.MIN_VALUE;
				double longitude = Double.MIN_VALUE;
				
				JpegGeoTagReader geoTagReader = new JpegGeoTagReader();
				GeoTag geoTag = geoTagReader.readMetadata(image);
				// System.out.println("Latitude read: "+geoTag.getLatitude());
				// System.out.println("Longitude read: "+geoTag.getLongitude());
				// System.out.println("Altitude read: "+geoTag.getAltitude());
				geoLocation = new GeoLocation(geoTag.getLatitude(), geoTag.getLongitude(), geoTag.getAltitude());
				imageMap.put(image.getName(), geoLocation);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
		return imageMap;
		
	}
	
}
