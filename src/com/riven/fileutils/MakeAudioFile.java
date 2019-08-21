package com.riven.fileutils;

import java.io.File;
import java.util.Iterator;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.tag.TagField;

public class MakeAudioFile {
	private boolean getAudioMetadata(File file) {
		boolean ret = false;
		if (file.isFile() && file.exists()) {
			String fileName = file.getName();
			String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			switch (fileType) {
			case "ogg":
			case "mp3":
			case "flac":
			case "mp4":
			case "m4a":
			case "m4p":
			case "wav":
			case "ra":
			case "rm":
			case "m4b":
			case "aif":
			case "aiff":
			case "aifc":
			case "dsf":
				try {
					AudioFile f = AudioFileIO.read(file);
					org.jaudiotagger.tag.Tag tag = f.getTag();
					AudioHeader audioHeader = f.getAudioHeader();
					System.out.format("\n[audioHeader] - getTrackLength = %s", audioHeader.getTrackLength());
					System.out.format("\n[audioHeader] - getSampleRateAsNumber = %s", audioHeader.getSampleRateAsNumber());
					if (fileType.equals("mp3")) {
						MP3AudioHeader mp3AudioHeader = (MP3AudioHeader) audioHeader;
						System.out.format("\n[mp3AudioHeader] - getChannels = %s", mp3AudioHeader.getChannels());
						System.out.format("\n[mp3AudioHeader] - isVariableBitRate = %s", mp3AudioHeader.isVariableBitRate());
						System.out.format("\n[mp3AudioHeader] - getTrackLengthAsString = %s",
								mp3AudioHeader.getTrackLengthAsString());
						System.out.format("\n[mp3AudioHeader] - getMpegVersion = %s", mp3AudioHeader.getMpegVersion());
						System.out.format("\n[mp3AudioHeader] - getMpegLayer = %s", mp3AudioHeader.getMpegLayer());
						System.out.format("\n[mp3AudioHeader] - isOriginal = %s", mp3AudioHeader.isOriginal());
						System.out.format("\n[mp3AudioHeader] - isCopyrighted = %s", mp3AudioHeader.isCopyrighted());
						System.out.format("\n[mp3AudioHeader] - isPrivate = %s", mp3AudioHeader.isPrivate());
						System.out.format("\n[mp3AudioHeader] - isProtected = %s", mp3AudioHeader.isProtected());
						System.out.format("\n[mp3AudioHeader] - getBitRate = %s", mp3AudioHeader.getBitRate());
						System.out.format("\n[mp3AudioHeader] - getEncodingType = %s", mp3AudioHeader.getEncodingType());
					}
					Iterator<TagField> iterator = tag.getFields();
					while (iterator.hasNext()) {
						TagField tagField = iterator.next();
						System.out.format("\n[tag] - %s = %s", tagField.getId(), tagField);
					}
					ret = true;
//					System.out.format("\n[tag] - ALBUM = %s", tag.getFirst(FieldKey.ALBUM));
//					System.out.format("\n[tag] - TITLE = %s", tag.getFirst(FieldKey.TITLE));
//					System.out.format("\n[tag] - COMMENT = %s", tag.getFirst(FieldKey.COMMENT));
//					System.out.format("\n[tag] - YEAR = %s", tag.getFirst(FieldKey.YEAR));
//					System.out.format("\n[tag] - TRACK = %s", tag.getFirst(FieldKey.TRACK));
//					System.out.format("\n[tag] - DISC_NO = %s", tag.getFirst(FieldKey.DISC_NO));
//					System.out.format("\n[tag] - COMPOSER = %s", tag.getFirst(FieldKey.COMPOSER));
//					System.out.format("\n[tag] - ARTIST_SORT = %s", tag.getFirst(FieldKey.ARTIST_SORT));
//					System.out.format("\n[tag] - ARTIST = %s", tag.getFirst(FieldKey.ARTIST));

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		return ret;
	}

}
