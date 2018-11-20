package com.debadityadey.imagereader;

import java.io.File;

import com.debadityadey.commonutils.MultimediaFile;

public class Image extends MultimediaFile {

	File imageFile;
	
	@Override
	public File readFile(String filePath) {
		imageFile = new File(filePath);
		if(imageFile.exists()) {
			return imageFile;
		}
		return null;
	}	
	
}
