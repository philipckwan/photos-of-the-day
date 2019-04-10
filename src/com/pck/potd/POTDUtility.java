package com.pck.potd;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;

public class POTDUtility {
	private static final String MIME_TYPE_IMAGE_JPEG = "image/jpeg";
	private static final String MIME_TYPE_IMAGE_PNG = "image/png";
	private static final String MIME_TYPE_IMAGE_BMP = "image/x-ms-bmp";
	private static final String MIME_TYPE_IMAGE_GIF = "image/gif";
	private static final Set<String> ACCEPTABLE_FILE_TYPE_SET = new HashSet<String>();

	private static final long MAXIMUM_PHOTO_FILE_SIZE = 10000000L;
	private static MessageDigest md = null;

	static {
		ACCEPTABLE_FILE_TYPE_SET.add(MIME_TYPE_IMAGE_JPEG);
		ACCEPTABLE_FILE_TYPE_SET.add(MIME_TYPE_IMAGE_PNG);
		ACCEPTABLE_FILE_TYPE_SET.add(MIME_TYPE_IMAGE_BMP);
		ACCEPTABLE_FILE_TYPE_SET.add(MIME_TYPE_IMAGE_GIF);

		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("POTDUtility.static: ERROR - NoSuchAlgorithmException:");
			e.printStackTrace();
		}
	}

	public static boolean isFileOfAcceptableType(File aFile) {
		boolean isAcceptable = false;

		if (!aFile.isFile()) {
			//System.out
			//		.println("POTDUtility.isFileOfAcceptableType: this file [" + aFile.getName() + "] is not a file;");
			isAcceptable = false;
		} else if (aFile.length() > MAXIMUM_PHOTO_FILE_SIZE) {
			//System.out.println("POTDUtility.isFileOfAcceptableType: this file [" + aFile.getName() + "] is too large:" + aFile.length() + ";");
			isAcceptable = false;
		} else {

			ByteArrayInputStream bais;
			try {
				bais = new ByteArrayInputStream(FileUtils.readFileToByteArray(aFile));
				TikaConfig config = TikaConfig.getDefaultConfig();
				MediaType mediaType = config.getMimeRepository().detect(bais, new Metadata());
				MimeType mimeType = config.getMimeRepository().forName(mediaType.toString());
				String name = mimeType.getName();

				if (!ACCEPTABLE_FILE_TYPE_SET.contains(name)) {
					//System.out.println("POTDUtility.isFileOfAcceptableType: this file [" + aFile.getName()
					//		+ "] of type [" + name + "], which is not of acceptable type;");
					isAcceptable = false;
				} else {
					isAcceptable = true;
				}
			} catch (IOException e) {
				System.out.println("POTDUtility.isFileOfAcceptableType: ERROR - IOException:");
				e.printStackTrace();
			} catch (MimeTypeException e) {
				System.out.println("POTDUtility.isFileOfAcceptableType: ERROR - MimeTypeException:");
				e.printStackTrace();
			}
		}

		return isAcceptable;
	}

	public static String getMD5Hash(File aFile) {
		String md5Hash = null;
		byte[] fileByteArray = null;
		byte[] md5sum = null;

		try {
			fileByteArray = FileUtils.readFileToByteArray(aFile);
		} catch (IOException e) {
			System.out.println("POTDUtility.getMD5Hash: ERROR - IOException:");
			e.printStackTrace();
		}
		md5sum = md.digest(fileByteArray);
		md5Hash = DatatypeConverter.printHexBinary(md5sum).toUpperCase();
		//System.out.println("POTDUtility.getMD5Hash: file:[" + aFile.getName() + "], hash:[" + md5Hash + "];");
		return md5Hash;
	}

	public static void moveFile(File fileToMove, File directoryToMoveTo) {
		try {
			FileUtils.moveFileToDirectory(fileToMove, directoryToMoveTo, false);
		} catch (IOException e) {
			System.out.println("POTDUtility.moveFile: ERROR - IOException:");
			e.printStackTrace();
		}
	}
}
