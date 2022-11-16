package com.pck.potd;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;

public class PhotosOfTheDay {

	private static int numPhotosPick = 1;

	private static final String SIGNATURE = "PhotosOfTheDay (v0.14)";

	private static Map<String, File> mapFolderOfFolders = new HashMap<String, File>();
	private static Map<String, File> mapFolderOfFiles = new HashMap<String, File>();

	private static int renameFileNumberIdx = 0;

	public static void main(String[] args) {
		System.out.println("PhotosOfTheDay.main: START - " + SIGNATURE);

		ConfigurationManager.loadPropertiesAndValidate();

		switch (ConfigurationManager.getMode()) {
		case PICK_PHOTOS:
			System.out.println("PhotosOfTheDay.main: Will run in mode [PICK_PHOTOS];");
			pickPhotos();
			break;
		case MOVE_DUPLICATES:
			System.out.println("PhotosOfTheDay.main: Will run in mode [MOVE_DUPLICATES];");
			findDuplicates();
			break;
		case RECURSIVE_FIND_DUPLICATES:
			System.out.println("PhotosOfTheDay.main: Will run in mode [RECURSIVE_FIND_DUPLICATES];");
			findDuplicatesRecursive(false);
			break;
		case RECURSIVE_MOVE_DUPLICATES:
			System.out.println("PhotosOfTheDay.main: Will run in mode [RECURSIVE_MOVE_DUPLICATES];");
			findDuplicatesRecursive(true);
			break;	
		default:
			System.out.println("PhotosOfTheDay.main: ERROR - not sure what mode to run in;");
			break;
		}

		System.out.println("PhotosOfTheDay.main: END");
	}

	private static void pickPhotos() {
		System.out.println("PhotosOfTheDay.pickPhotos: 1.0;");

		// TODO - should we support percentage?
		if (ConfigurationManager.isNumPhotosToPickAPercentage()) {
			System.out.println(
					"PhotosOfTheDay.pickPhotos: ERROR - PhotosOfTheDay currently does not support percentage value for 'howManyPhotosToPick'");
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
			System.out.println("PhotosOfTheDay.pickPhotos: Pre-picked a folder of photos:" + specificDirectory + ";");
			directoryPicked = mapFolderOfFiles.get(specificDirectory);
		} else {
			if (mapFolderOfFolders.containsKey(specificDirectory)) {
				System.out.println(
						"PhotosOfTheDay.pickPhotos: Pre-picked a folder of folders:" + specificDirectory + ";");
				// TODO: HACK - should consider revising
				File specificDirectoryFile = mapFolderOfFolders.get(specificDirectory);
				mapFolderOfFolders.clear();
				mapFolderOfFiles.clear();
				populateDirectoryMap2(specificDirectoryFile);
			} else if (specificDirectory != null && specificDirectory.length() > 0) {
				System.out.println(
						"PhotosOfTheDay.pickPhotos: specific directory is not found:" + specificDirectory + ";");
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

		System.out.println("PhotosOfTheDay.pickPhotos: directoryPicked:[" + directoryPicked.getName() + "]");

		File[] fileArray = directoryPicked.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				String name = file.getName();
				if (name.startsWith(".")) {
					return false;
				}
				return true;
			}

		});
		List<File> fileList = new LinkedList<File>(Arrays.asList(fileArray));
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
				if ("number".equals(ConfigurationManager.getRenameFiles())) {
					renameFileNumberIdx++;
					File newFile = new File(ConfigurationManager.getDestinationDirectory(),
							String.format("%02d", renameFileNumberIdx) + ".jpg");
					FileUtils.copyFile(filePicked, newFile);
				} else {
					FileUtils.copyFileToDirectory(filePicked, ConfigurationManager.getDestinationDirectory(), false);
				}
				numPhotosPicked++;
			} catch (IOException e) {
				System.out.println("PhotosOfTheDay.pickPhotos: ERROR - IOException:");
				e.printStackTrace();
			}

		}

		EmailTemplate.writeToFile(ConfigurationManager.getDestinationDirectory(),
				directoryPicked.getParentFile().getName(), "..." + File.separator + directoryPicked.getName(),
				numPhotosPicked + " photos are picked out of " + totalSize + " ==> "
						+ ((float) numPhotosPicked) / totalSize * 100 + "%",
				SIGNATURE);

	}
	
	private static void findDuplicatesRecursive(boolean moveDuplicates) {
		System.out.println("PhotosOfTheDay.findDuplicatesRecursive: 1.1;");
		
		Map<Long, List<FileNode>> fileSizeToFileListMap = new HashMap<Long, List<FileNode>>();
		List<File> files = listFiles(ConfigurationManager.getSourceDirectory());
		
		
		if (ConfigurationManager.getFindDuplicatesSecondDirectory() != null) {
			System.out.println("PhotosOfTheDay.findDuplicatesRecursive: findDuplicatesSecondDirectory is specified, will compare these 2 directories for duplicates:");
			System.out.println("[" + ConfigurationManager.getSourceDirectory() + "]");
			System.out.println("[" + ConfigurationManager.getFindDuplicatesSecondDirectory() + "]");
			files.addAll(listFiles(ConfigurationManager.getFindDuplicatesSecondDirectory()));
		}
		
		System.out.println("PhotosOfTheDay.findDuplicatesRecursive: number of files:" + files.size() + ";");
		
		for (File aFile : files) {
			FileNode fn = new FileNode(aFile);
			long aFileSize = aFile.length();
			//System.out.println("__aFile.name:[" + aFile.getName() + "], size:[" + aFileSize + "];");
			List<FileNode> fileNodeList = fileSizeToFileListMap.get(aFileSize);
			if (fileNodeList != null) {
				String md5ThisFile = POTDUtility.getMD5Hash(aFile);
				fn.setHash(md5ThisFile);

				for (FileNode aFileNode : fileNodeList) {
					if (aFileNode.getHash() == null) {
						aFileNode.setHash(POTDUtility.getMD5Hash(aFileNode.getFile()));
					}
				}

				for (FileNode aFileNode : fileNodeList) {
					if (fn.getHash().equals(aFileNode.getHash())) {
						System.out.println("-----found duplicate, listed with linux 'rm' command:");
						
						File fileA, fileB;
						if (aFileNode.getFile().getPath().compareTo(fn.getFile().getPath()) > 0) {
							fileA = fn.getFile();
							fileB = aFileNode.getFile();
						} else {
							fileA = aFileNode.getFile();
							fileB = fn.getFile();
						}
						if (!moveDuplicates) {
							System.out.println("rm '" + fileA.getPath() + "'");
							System.out.println("rm '" + fileB.getPath() + "'");
						} else {
							System.out.println("-- " + fileA.getPath());
							System.out.println("-- " + fileB.getPath() + " <-- moved;");
							POTDUtility.moveFile(fileB, ConfigurationManager.getDuplicatesDirectory());
						}
						break;
					}
					
				}
			} else {
				fileNodeList = new ArrayList<FileNode>();
				fileSizeToFileListMap.put(aFileSize, fileNodeList);
			}
			fileNodeList.add(fn);
		}
		System.out.println("PhotosOfTheDay.findDuplicatesRecursive: END;");
		
	}
	
	private static List<File> listFiles(File directory) {
		List<File> files = new ArrayList<File>();
		File[] filesAndDirectories = directory.listFiles();
		
		for(File aFileAndDirectory : filesAndDirectories) {
			if (aFileAndDirectory.isDirectory()) {
				files.addAll(listFiles(aFileAndDirectory));
			} else {
				files.add(aFileAndDirectory);
			}
		}
		
		return files;
	}

	private static void findDuplicates() {
		System.out.println("PhotosOfTheDay.findDuplicates: 1.0;");

		/*
		 * FileNode {
		 * 	File file; // the file itself
		 *  String md5; // the md5 of this file, doesn't always need to be computed
		 * }
		 *
		 * Approach:
		 * iterate the files under the src folder
		 * build a map of <key>:<value> being <filesize in bytes>:<list of FileNode>
		 * if a file has a filesize that is new in the map, then add to the map
		 * else if a file hits an existing entry in map (i.e. having same filesize of a previous file):
		 *  get (or calculate) the md5 of the files in the current list (there should be at least 1 file in the list)
		 *  calculate the md5 of the file
		 *  compare the md5 of the file and the files in the list
		 *  if md5 match:
		 *   this file is a duplicate
		 *   move this file to the dst folder
		 *   add this log to console or an output file so that user can see what is the results
		 *  else:
		 *   this file is not a duplicate, but only have same filesize by coincident
		 *  finally:
		 *   add this file to the list
		 *   return
		 */

		Map<Long, List<FileNode>> fileSizeToFileListMap = new HashMap<Long, List<FileNode>>();

		File[] files = ConfigurationManager.getSourceDirectory().listFiles(File::isFile);
		Arrays.sort(files, Collections.reverseOrder());

		for (File aFile : files) {
			FileNode fn = new FileNode(aFile);
			long aFileSize = aFile.length();
			//System.out.println("__aFile.name:[" + aFile.getName() + "], size:[" + aFileSize + "];");
			List<FileNode> fileNodeList = fileSizeToFileListMap.get(aFileSize);
			if (fileNodeList != null) {
				String md5ThisFile = POTDUtility.getMD5Hash(aFile);
				fn.setHash(md5ThisFile);

				for (FileNode aFileNode : fileNodeList) {
					if (aFileNode.getHash() == null) {
						aFileNode.setHash(POTDUtility.getMD5Hash(aFileNode.getFile()));
					}
				}

				for (FileNode aFileNode : fileNodeList) {
					if (fn.getHash().equals(aFileNode.getHash())) {
						System.out.println("PhotosOfTheDay.findDuplicates: found duplicate:["
								+ aFileNode.getFile().getName() + "] and [" + fn.getFile().getName()
								+ "], will move the later file to duplicatesDirectory");
						POTDUtility.moveFile(fn.getFile(), ConfigurationManager.getDuplicatesDirectory());
						break;
					}
				}
			} else {
				fileNodeList = new ArrayList<FileNode>();
				fileSizeToFileListMap.put(aFileSize, fileNodeList);
			}
			fileNodeList.add(fn);
		}

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
