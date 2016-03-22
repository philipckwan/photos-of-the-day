package com.pck;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;

import com.pck.common.PropertiesManager;

public class PhotosOfTheDay {
	public static final String CONFIG_FILE = "input.txt";

	private static String sourceRoot = null;
	private static String destinationRoot = null;
	private static int numPhotosPick = 8;

	public static final String KEYWORD_SOURCE_ROOT = "sourceRoot";
	public static final String KEYWORD_DESTINATION_DIRECTORY = "destinationDirectory";

	public static final String MIME_TYPE_NAME = "image/jpeg";

	public static void main(String[] args) {
		System.out.println("PhotosOfTheDay.main: START");

		PropertiesManager.initWithFile(CONFIG_FILE);

		sourceRoot = PropertiesManager.getProperty(KEYWORD_SOURCE_ROOT);
		destinationRoot = PropertiesManager.getProperty(KEYWORD_DESTINATION_DIRECTORY);

		System.out.println("sourceRoot: " + sourceRoot + ";");
		File dirSourceRoot = new File(sourceRoot);
		File dirDestinationRoot = new File(destinationRoot);

		System.out.println("dir?" + dirSourceRoot.exists());
		File[] dirFirstLevel = dirSourceRoot.listFiles();

		Map<Integer, File> dirMap = new HashMap<Integer, File>();
		Integer counter = 0;

		try {
			if (dirFirstLevel != null) {
				for (File aDir : dirFirstLevel) {
					System.out.println("1st level directory: " + aDir.getCanonicalPath());
					File[] dirSecondLevel = aDir.listFiles();
					if (dirSecondLevel != null) {
						for (File bDir : dirSecondLevel) {
							System.out.println("2nd level directory: " + bDir.getCanonicalPath());
							counter++;
							dirMap.put(counter, bDir);
							// Now, randomly pick one directory

							/*
							File[] filesThirdLevel = bDir.listFiles();
							for (File cFile : filesThirdLevel) {
							
								ByteArrayInputStream bais = new ByteArrayInputStream(FileUtils.readFileToByteArray(cFile));
							
								TikaConfig config = TikaConfig.getDefaultConfig();
								MediaType mediaType = config.getMimeRepository().detect(bais, new Metadata());
								MimeType mimeType = config.getMimeRepository().forName(mediaType.toString());
								String extension = mimeType.getExtension();
								String name = mimeType.getName();
							
								//System.out.println("mtn:" + MIME_TYPE_NAME + ";");
								if (MIME_TYPE_NAME.equalsIgnoreCase(name)) {
									System.out.println(
											"3rd level file: " + cFile.getCanonicalPath() + ";type: " + name + ";");
								}
							}
							*/
						}
					}
				}
			} else {
				System.out.println("ERROR: 1st level directory is empty");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*catch (MimeTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}*/

		Integer idxPick = ((int) (Math.random() * dirMap.size())) + 1;
		System.out.println("dirMap.size:" + dirMap.size() + "; pick:" + idxPick + ";");

		File[] filesThirdLevel = dirMap.get(idxPick).listFiles();
		Map<Integer, File> fileMap = new HashMap<Integer, File>();
		Integer fileMapCounter = 0;

		for (File cFile : filesThirdLevel) {

			ByteArrayInputStream bais;
			try {
				bais = new ByteArrayInputStream(FileUtils.readFileToByteArray(cFile));
				TikaConfig config = TikaConfig.getDefaultConfig();
				MediaType mediaType = config.getMimeRepository().detect(bais, new Metadata());
				MimeType mimeType = config.getMimeRepository().forName(mediaType.toString());
				String name = mimeType.getName();

				if (MIME_TYPE_NAME.equalsIgnoreCase(name)) {
					System.out.println("3rd level file: " + cFile.getCanonicalPath() + ";type: " + name + ";");
					fileMapCounter++;
					fileMap.put(fileMapCounter, cFile);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MimeTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		System.out.println("fileMap.size:" + fileMap.size() + "; numPhotosPick:" + numPhotosPick + ";");
		if (numPhotosPick > fileMap.size()) {
			numPhotosPick = fileMap.size();
		}
		int totalSize = fileMap.size();

		for (int numPhotosPicked = 0; numPhotosPicked < numPhotosPick; numPhotosPicked++) {
			Integer idxFilePick = ((int) (Math.random() * totalSize)) + 1;
			System.out.println("idxFilePick:" + idxFilePick + ";");
			File filePicked = fileMap.remove(idxFilePick);
			if (filePicked == null) {
				numPhotosPicked--;
			} else {
				try {
					FileUtils.copyFileToDirectory(filePicked, dirDestinationRoot);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		System.out.println("PhotosOfTheDay.main: END");
	}

}
