package com.pck.potd;

import java.io.File;
import java.io.IOException;

import com.pck.common.PropertiesManager;

public class ConfigurationManager {

	public static final String CONFIG_FILE = "input.txt";
	public static final String KEYWORD_SOURCE_ROOT = "sourceDirectory";
	public static final String KEYWORD_DESTINATION_DIRECTORY = "destinationDirectory";
	public static final String KEYWORD_HOW_MANY_PHOTOS_TO_PICK = "howManyPhotosToPick";
	public static final String KEYWORD_SPECIFIC_DIRECTORY = "specificDirectory";
	public static final String KEYWORD_RENAME_FILES = "renameFiles";

	private static String sourceRoot = null;
	private static String destinationRoot = null;
	private static String howManyPhotosToPick = null;
	private static String specificDirectory = null;
	private static String renameFiles = null;

	private static File dirSourceRoot = null;
	private static File dirDestinationRoot = null;
	private static int howManyPhotosToPickInt = 0;
	private static boolean howManyPhotosToPickIsPercentage = false;

	private static final String PERCENTAGE = "%";

	public static void loadPropertiesAndValidate() throws IOException {
		PropertiesManager.initWithFile(CONFIG_FILE);

		sourceRoot = PropertiesManager.getProperty(KEYWORD_SOURCE_ROOT);
		destinationRoot = PropertiesManager.getProperty(KEYWORD_DESTINATION_DIRECTORY);
		howManyPhotosToPick = PropertiesManager.getProperty(KEYWORD_HOW_MANY_PHOTOS_TO_PICK);
		specificDirectory = PropertiesManager.getProperty(KEYWORD_SPECIFIC_DIRECTORY);
		renameFiles = PropertiesManager.getProperty(KEYWORD_RENAME_FILES);

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

	public static int getNumPhotosToPick() {
		return howManyPhotosToPickInt;
	}

	public static boolean isNumPhotosToPickAPercentage() {
		return howManyPhotosToPickIsPercentage;
	}

	public static File getSourceDirectory() {
		return dirSourceRoot;
	}

	public static File getDestinationDirectory() {
		return dirDestinationRoot;
	}

	public static String getSpecificDirectory() {
		return specificDirectory;
	}

	public static String getRenameFiles() {
		return renameFiles;
	}
}
