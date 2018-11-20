package com.debadityadey.videoreader;

import java.io.File;

import com.debadityadey.commonutils.MultimediaFile;

public class Video extends MultimediaFile {
	
	File videoFile;

	@Override
	public File readFile(String filePath) {
		videoFile = new File(filePath);
		if(videoFile.exists()) {
			return videoFile;
		}
		return null;	
	}

}
