package com.pck;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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

	static {
		ACCEPTABLE_FILE_TYPE_SET.add(MIME_TYPE_IMAGE_JPEG);
		ACCEPTABLE_FILE_TYPE_SET.add(MIME_TYPE_IMAGE_PNG);
		ACCEPTABLE_FILE_TYPE_SET.add(MIME_TYPE_IMAGE_BMP);
		ACCEPTABLE_FILE_TYPE_SET.add(MIME_TYPE_IMAGE_GIF);
	}

	public static boolean isFileOfAcceptableType(File aFile) {
		boolean isAcceptable = false;

		if (!aFile.isFile()) {
			System.out
					.println("POTDUtility.isFileOfAcceptableType: this file [" + aFile.getName() + "] is not a file;");
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
					System.out.println("POTDUtility.isFileOfAcceptableType: this file [" + aFile.getName()
							+ "] of type [" + name + "], which is not of acceptable type;");
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
}
