package com.pck;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;

public class PhotosOfTheDay {

	private static int numPhotosPick = 1;

	private static final String SIGNATURE = "PhotosOfTheDay (v0.9)";

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

		Map<Integer, File> dirMap = populateDirectoryMap();

		Integer idxPick = ((int) (Math.random() * dirMap.size())) + 1;
		File directoryPicked = dirMap.get(idxPick);

		//File[] filesThirdLevel = directoryPicked.listFiles();
		System.out.println("PhotosOfTheDay.main: directoryPicked:[" + directoryPicked.getName() + "]");
		//System.out.println(
		//		"PhotosOfTheDay.main: directoryPicked.parent:[" + directoryPicked.getParentFile().getName() + "]");
		//System.exit(0);

		//System.out.println(" it has " + filesThirdLevel.length + " files;");

		// TODO - timer starts here
		long timeStart = System.currentTimeMillis();

		List<File> fileList = new LinkedList<File>(Arrays.asList(directoryPicked.listFiles()));
		System.out.println("__fileList.size (before filtering):" + fileList.size() + ";");
		Random rand = new Random();

		//Map<Integer, File> fileMap = new HashMap<Integer, File>();

		//Integer fileMapCounter = 0;
		/* TODO - if I now move the logic so that I don't do the filtering of all files in this folder,
		 *  then I could run into the problem of removing picked files and will never be able to hit the number of photos to choose
		 */

		/*
		int counter = 0;
		Iterator<File> iter = fileList.iterator();
		while (iter.hasNext()) {
			File cFile = iter.next();
			//System.out.println("[" + ++counter + "], file:" + cFile.getName() + ";");
		
			if (!POTDUtility.isFileOfAcceptableType(cFile)) {
				iter.remove();
			}
		
		}
		*/

		System.out.println(
				"__fileList.size (after filtering):" + fileList.size() + "; numPhotosPick:" + numPhotosPick + ";");
		if (numPhotosPick > fileList.size()) {
			numPhotosPick = fileList.size();
		}
		System.out.println(
				"__fileList.size (after filtering):" + fileList.size() + "; numPhotosPick:" + numPhotosPick + ";");
		int totalSize = fileList.size();
		int numPhotosPicked = 0;

		for (numPhotosPicked = 0; numPhotosPicked < numPhotosPick && fileList.size() > 0;) {
			int idxPicked = rand.nextInt(fileList.size());
			File filePicked = fileList.remove(idxPicked);
			/*System.out.println("run:" + numPhotosPicked + "; picked:" + filePicked.getName() + "; list.size:"
					+ fileList.size() + ";");
					*/

			if (!POTDUtility.isFileOfAcceptableType(filePicked)) {
				continue;
			}

			try {
				FileUtils.copyFileToDirectory(filePicked, ConfigurationManager.getDestinationDirectory());
				numPhotosPicked++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		EmailTemplate.writeToFile(ConfigurationManager.getDestinationDirectory(),
				directoryPicked.getParentFile().getName(), "..." + File.separator + directoryPicked.getName(),
				numPhotosPicked + " photos are picked out of " + totalSize + " ==> "
						+ ((float) numPhotosPicked) / totalSize * 100 + "%",
				SIGNATURE);

		// TODO - timer ends here
		long timeEnd = System.currentTimeMillis();
		System.out.println("__time taken:" + (timeEnd - timeStart) + ";");

		System.out.println("PhotosOfTheDay.main: END");
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
