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
	public static final String KEYWORD_MODE = "mode";
	public static final String KEYWORD_DUPLICATES_DIRECTORY = "duplicatesDirectory";

	//public static final String MODESTR_FIND_DUPS = "findDuplicates";

	//public static final OperationMode MODE_POTD = "MODE_POTD";
	//public static final String MODE_FIND_DUPS = "MODE_FIND_DUPS";

	private static String sourceRoot = null;
	private static String destinationRoot = null;
	private static String howManyPhotosToPick = null;
	private static String specificDirectory = null;
	private static String renameFiles = null;
	private static OperationMode mode = OperationMode.PICK_PHOTOS;
	private static String duplicatesRoot = null;

	private static File dirSourceRoot = null;
	private static File dirDestinationRoot = null;
	private static int howManyPhotosToPickInt = 0;
	private static boolean howManyPhotosToPickIsPercentage = false;
	private static File dirDuplicatesRoot = null;

	private static final String PERCENTAGE = "%";

	public static void loadPropertiesAndValidate() {
		PropertiesManager.initWithFile(CONFIG_FILE);

		sourceRoot = PropertiesManager.getProperty(KEYWORD_SOURCE_ROOT);
		destinationRoot = PropertiesManager.getProperty(KEYWORD_DESTINATION_DIRECTORY);
		howManyPhotosToPick = PropertiesManager.getProperty(KEYWORD_HOW_MANY_PHOTOS_TO_PICK);
		specificDirectory = PropertiesManager.getProperty(KEYWORD_SPECIFIC_DIRECTORY);
		renameFiles = PropertiesManager.getProperty(KEYWORD_RENAME_FILES);
		mode = OperationMode.stringToMode(PropertiesManager.getProperty(KEYWORD_MODE));

		duplicatesRoot = PropertiesManager.getProperty(KEYWORD_DUPLICATES_DIRECTORY);

		dirSourceRoot = new File(sourceRoot);
		// validate the source directory
		if (!dirSourceRoot.isDirectory()) {
			System.out.println("ERROR - sourceDirectory is invalid!");
			System.exit(0);
		} else {
			try {
				System.out.println("ConfigurationManager.loadPropertiesAndValidate: sourceDirectory: "
						+ dirSourceRoot.getCanonicalPath() + ";");
			} catch (IOException e) {
				System.out.println("ConfigurationManager.loadPropertiesAndValidate: ERROR - IOException 1:");
				e.printStackTrace();
			}
		}

		dirDestinationRoot = new File(destinationRoot);
		// validate the destination directory
		if (!dirDestinationRoot.isDirectory()) {
			System.out.println("ERROR - destinationDirectory is invalid!");
			System.exit(0);
		} else {
			try {
				System.out.println("ConfigurationManager.loadPropertiesAndValidate: destinationDirectory: "
						+ dirDestinationRoot.getCanonicalPath() + ";");
			} catch (IOException e) {
				System.out.println("ConfigurationManager.loadPropertiesAndValidate: ERROR - IOException 2:");
				e.printStackTrace();
			}
		}

		dirDuplicatesRoot = new File(duplicatesRoot);
		// validate the duplicates directory
		if (!dirDuplicatesRoot.isDirectory()) {
			System.out.println("ERROR - duplicatesDirectory is invalid!");
			System.exit(0);
		} else {
			try {
				System.out.println("ConfigurationManager.loadPropertiesAndValidate: duplicatesDirectory: "
						+ dirDuplicatesRoot.getCanonicalPath() + ";");
			} catch (IOException e) {
				System.out.println("ConfigurationManager.loadPropertiesAndValidate: ERROR - IOException 3:");
				e.printStackTrace();
			}
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

	public static File getDuplicatesDirectory() {
		return dirDuplicatesRoot;
	}

	public static String getSpecificDirectory() {
		return specificDirectory;
	}

	public static String getRenameFiles() {
		return renameFiles;
	}

	public static OperationMode getMode() {
		return mode;
	}
}
