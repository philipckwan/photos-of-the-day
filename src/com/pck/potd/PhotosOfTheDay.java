package com.pck.potd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;

public class PhotosOfTheDay {

	private static int numPhotosPick = 1;

	private static final String SIGNATURE = "PhotosOfTheDay (v0.10)";
	
	private static Map<String, File> mapFolderOfFolders = new HashMap<String, File>();
	private static Map<String, File> mapFolderOfFiles = new HashMap<String, File>();

	public static void main(String[] args) throws IOException {
		System.out.println("PhotosOfTheDay.main: START - " + SIGNATURE);

		ConfigurationManager.loadPropertiesAndValidate();

		// TODO - should we support percentage?
		if (ConfigurationManager.isNumPhotosToPickAPercentage()) {
			System.out.println("PhotosOfTheDay currently does not support percentage value for 'howManyPhotosToPick'");
			System.exit(0);
		}

		//Map<Integer, File> dirMap = new HashMap<Integer, File>();
		numPhotosPick = ConfigurationManager.getNumPhotosToPick();
		Random rand = new Random();
		File directoryPicked = null;
		
		// TODO - timer starts here
		long timeStart = System.currentTimeMillis();
		
		// Strategy 1
		
		populateDirectoryMap2(ConfigurationManager.getSourceDirectory());
		String specificDirectory = ConfigurationManager.getSpecificDirectory();
		if (mapFolderOfFiles.containsKey(specificDirectory)) {
			System.out.println("PhotosOfTheDay.main: Pre-picked a folder of photos:" + specificDirectory + ";");
			directoryPicked = mapFolderOfFiles.get(specificDirectory);
		} else {
			if (mapFolderOfFolders.containsKey(specificDirectory)) {
				System.out.println("PhotosOfTheDay.main: Pre-picked a folder of folders:" + specificDirectory + ";");
				// TODO: HACK - should consider revising
				File specificDirectoryFile = mapFolderOfFolders.get(specificDirectory);
				mapFolderOfFolders.clear();
				mapFolderOfFiles.clear();
				populateDirectoryMap2(specificDirectoryFile);
			} else if (specificDirectory != null && specificDirectory.length() > 0){
				System.out.println("PhotosOfTheDay.main: specific directory is not found:" + specificDirectory + ";");
			}
			File[] listFolderOfFiles = mapFolderOfFiles.values().toArray(new File[0]);
			directoryPicked = listFolderOfFiles[rand.nextInt(listFolderOfFiles.length)];
		}
		
		
		// Strategy 2
		/*
		Map<Integer, File> dirMap = populateDirectoryMap();

		Integer idxPick = ((int) (Math.random() * dirMap.size())) + 1;
		directoryPicked = dirMap.get(idxPick);
		*/		

		// TODO - timer ends here
		long timeEnd = System.currentTimeMillis();
		System.out.println("__time taken:" + (timeEnd - timeStart) + ";");
				
		System.out.println("PhotosOfTheDay.main: directoryPicked:[" + directoryPicked.getName() + "]");

		List<File> fileList = new LinkedList<File>(Arrays.asList(directoryPicked.listFiles()));
		System.out.println("__fileList.size (before filtering):" + fileList.size() + ";");

		System.out.println(
				"__fileList.size (after filtering):" + fileList.size() + "; numPhotosPick:" + numPhotosPick + ";");
		if (numPhotosPick > fileList.size()) {
			numPhotosPick = fileList.size();
		}
		System.out.println("__fileList.size (max between numPhotosPick and fileList.size):" + fileList.size()
				+ "; numPhotosPick:" + numPhotosPick + ";");
		int totalSize = fileList.size();
		int numPhotosPicked = 0;

		for (numPhotosPicked = 0; numPhotosPicked < numPhotosPick && fileList.size() > 0;) {
			int idxPicked = rand.nextInt(fileList.size());
			File filePicked = fileList.remove(idxPicked);

			if (!POTDUtility.isFileOfAcceptableType(filePicked)) {
				continue;
			}

			try {
				FileUtils.copyFileToDirectory(filePicked, ConfigurationManager.getDestinationDirectory());
				numPhotosPicked++;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		

		EmailTemplate.writeToFile(ConfigurationManager.getDestinationDirectory(),
				directoryPicked.getParentFile().getName(), "..." + File.separator + directoryPicked.getName(),
				numPhotosPicked + " photos are picked out of " + totalSize + " ==> "
						+ ((float) numPhotosPicked) / totalSize * 100 + "%",
				SIGNATURE);
		

		System.out.println("PhotosOfTheDay.main: END");
	}
	
	private static void populateDirectoryMap2(File thisFolder) {
		File[] subFolders = thisFolder.listFiles(File::isDirectory);
		File[] subFiles = thisFolder.listFiles(File::isFile);
		
		if (subFolders != null && subFolders.length > 0) {
			mapFolderOfFolders.put(thisFolder.getName(), thisFolder);
			for (File aFolder : subFolders) {
				populateDirectoryMap2(aFolder);
			}
		}
		
		if (subFiles != null && subFiles.length > 0) {
			for (File aFile : subFiles) {
				if (POTDUtility.isFileOfAcceptableType(aFile)) {
					mapFolderOfFiles.put(thisFolder.getName(), thisFolder);
					break;
				}
			}
			
		}
	}
	
	private static void printBothMaps() {
		System.out.println("PhotosOfTheDay.printBothMaps: START");
		
		System.out.println("--mapFolderOfFolders, total:" + mapFolderOfFolders.size() + ";");
		for (String aFolderName : mapFolderOfFolders.keySet()) {
			System.out.println("----:" + aFolderName + ";");
		}
		
		System.out.println("--mapFolderOfFiles, total:" + mapFolderOfFiles.size() + ";");
		for (String aFolderName : mapFolderOfFiles.keySet()) {
			System.out.println("----:" + aFolderName + ";");
		}
		
		System.out.println("PhotosOfTheDay.printBothMaps: END");
	}

	private static Map<Integer, File> populateDirectoryMap() throws IOException {
		
		Map<Integer, File> dirMap = new HashMap<Integer, File>();

		File[] dirFirstLevel = ConfigurationManager.getSourceDirectory().listFiles(File::isDirectory);
		

		Integer counter = 0;

		if (dirFirstLevel == null) {
			System.out.println("populateDirectoryMap: ERROR - 1st level directory is empty");
			return dirMap;
		}

		for (File aDir : dirFirstLevel) {
			/*
			if (!aDir.isDirectory()) {
				continue;
			}
			*/
			//System.out.println("1st level directory: " + aDir.getCanonicalPath());
			File[] dirSecondLevel = aDir.listFiles(File::isDirectory);
			if (dirSecondLevel == null) {
				continue;
			}

			for (File bDir : dirSecondLevel) {
				/*
				if (!bDir.isDirectory()) {
					continue;
				}*/
				//System.out.println("2nd level directory: " + bDir.getCanonicalPath());
				counter++;
				dirMap.put(counter, bDir);

				// To support 3rd level directory
				File[] dirThirdLevel = bDir.listFiles(File::isDirectory);
				if (dirThirdLevel == null) {
					continue;
				}

				for (File cDir : dirThirdLevel) {
					counter++;
					dirMap.put(counter, cDir);
				}

			}

		}

		System.out.println("__dirMap.size:" + dirMap.size() + ";");
		/*
		for (File aDir : dirMap.values()) {
			System.out.println("__:" + aDir.getName() + ";");
		}
		*/
		return dirMap;
	}

}
