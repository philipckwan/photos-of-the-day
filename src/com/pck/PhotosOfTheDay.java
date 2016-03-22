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
	public static final String KEYWORD_SOURCE_ROOT = "sourceDirectory";
	public static final String KEYWORD_DESTINATION_DIRECTORY = "destinationDirectory";
	public static final String KEYWORD_HOW_MANY_PHOTOS_TO_PICK = "howManyPhotosToPick";

	private static String sourceRoot = null;
	private static String destinationRoot = null;
	private static String howManyPhotosToPick = null;

	private static File dirSourceRoot = null;
	private static File dirDestinationRoot = null;
	private static int howManyPhotosToPickInt = 0;
	private static boolean howManyPhotosToPickIsPercentage = false;

	private static int numPhotosPick = 1;

	private static final String MIME_TYPE_NAME = "image/jpeg";
	private static final String PERCENTAGE = "%";

	public static void main(String[] args) throws IOException {
		System.out.println("PhotosOfTheDay.main: START");

		loadPropertiesAndValidate();

		//Map<Integer, File> dirMap = new HashMap<Integer, File>();
		numPhotosPick = howManyPhotosToPickInt;

		Map<Integer, File> dirMap = populateDirectoryMap();

		Integer idxPick = ((int) (Math.random() * dirMap.size())) + 1;
		File directoryPicked = dirMap.get(idxPick);

		File[] filesThirdLevel = directoryPicked.listFiles();
		System.out.println("PhotosOfTheDay.main: directoryPicked:[" + directoryPicked.getName() + "], it has "
				+ filesThirdLevel.length + " files;");

		/*
		if (true) {
			System.out.println("PhotosOfTheDay.main: 1.2");
			System.exit(0);
		}
		*/

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

	private static Map<Integer, File> populateDirectoryMap() throws IOException {
		Map<Integer, File> dirMap = new HashMap<Integer, File>();

		File[] dirFirstLevel = dirSourceRoot.listFiles();
		Integer counter = 0;

		if (dirFirstLevel != null) {
			for (File aDir : dirFirstLevel) {
				//System.out.println("1st level directory: " + aDir.getCanonicalPath());
				File[] dirSecondLevel = aDir.listFiles();
				if (dirSecondLevel != null) {
					for (File bDir : dirSecondLevel) {
						//System.out.println("2nd level directory: " + bDir.getCanonicalPath());
						counter++;
						dirMap.put(counter, bDir);
					}
				}
			}
		} else {
			System.out.println("populateDirectoryMap: ERROR - 1st level directory is empty");
		}

		return dirMap;
	}

	private static void loadPropertiesAndValidate() throws IOException {
		PropertiesManager.initWithFile(CONFIG_FILE);

		sourceRoot = PropertiesManager.getProperty(KEYWORD_SOURCE_ROOT);
		destinationRoot = PropertiesManager.getProperty(KEYWORD_DESTINATION_DIRECTORY);
		howManyPhotosToPick = PropertiesManager.getProperty(KEYWORD_HOW_MANY_PHOTOS_TO_PICK);

		dirSourceRoot = new File(sourceRoot);
		// validate the source directory
		if (!dirSourceRoot.isDirectory()) {
			System.out.println("ERROR - sourceDirectory is invalid!");
			System.exit(0);
		} else {
			System.out.println("sourceDirectory: " + dirSourceRoot.getCanonicalPath() + ";");
		}

		dirDestinationRoot = new File(destinationRoot);
		// validate the destination directory
		if (!dirDestinationRoot.isDirectory()) {
			System.out.println("ERROR - destinationDirectory is invalid!");
			System.exit(0);
		} else {
			System.out.println("destinationDirectory: " + dirDestinationRoot.getCanonicalPath() + ";");
		}

		// validate the number of photos to pick
		boolean howManyPhotosToPickError = false;
		if (howManyPhotosToPick != null) {
			try {
				if (PERCENTAGE.equals(String.valueOf(howManyPhotosToPick.charAt(howManyPhotosToPick.length() - 1)))) {
					howManyPhotosToPickIsPercentage = true;
					howManyPhotosToPickInt = Integer
							.parseInt(howManyPhotosToPick.substring(0, howManyPhotosToPick.length() - 1));
				} else {
					howManyPhotosToPickInt = Integer.parseInt(howManyPhotosToPick);
				}
				if (howManyPhotosToPickInt < 1) {
					howManyPhotosToPickError = true;
				}
			} catch (NumberFormatException nfe) {
				howManyPhotosToPickError = true;
			}
		} else {
			howManyPhotosToPickError = true;
		}

		if (howManyPhotosToPickError) {
			System.out.println("ERROR - howManyPhotosToPick is invalid!");
			System.exit(0);
		} else {
			System.out.println("howManyPhotosToPickInt:" + howManyPhotosToPickInt + "; isPercent? "
					+ howManyPhotosToPickIsPercentage + ";");
		}
	}

}