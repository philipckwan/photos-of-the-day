package com.pck;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;

public class PhotosOfTheDay {

	private static int numPhotosPick = 1;

	private static final String MIME_TYPE_NAME = "image/jpeg";
	private static final String SIGNATURE = "PhotosOfTheDay (v0.7)";

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

		List<File> fileList = new LinkedList<File>(Arrays.asList(directoryPicked.listFiles()));
		System.out.println("__fileList.size (before filtering):" + fileList.size() + ";");
		Random rand = new Random();

		//Map<Integer, File> fileMap = new HashMap<Integer, File>();
		//Integer fileMapCounter = 0;
		int counter = 0;
		Iterator<File> iter = fileList.iterator();
		while (iter.hasNext()) {
			File cFile = iter.next();
			System.out.println("[" + ++counter + "], file:" + cFile.getName() + ";");

			if (!cFile.isFile()) {
				System.out.println("remove:" + cFile.getName() + ";");
				iter.remove();
			}

			ByteArrayInputStream bais;
			try {
				bais = new ByteArrayInputStream(FileUtils.readFileToByteArray(cFile));
				TikaConfig config = TikaConfig.getDefaultConfig();
				MediaType mediaType = config.getMimeRepository().detect(bais, new Metadata());
				MimeType mimeType = config.getMimeRepository().forName(mediaType.toString());
				String name = mimeType.getName();

				if (!MIME_TYPE_NAME.equalsIgnoreCase(name)) {
					System.out.println("remove:" + cFile.getName() + ";");
					iter.remove();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MimeTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println(
				"fileList.size (after filtering):" + fileList.size() + "; numPhotosPick:" + numPhotosPick + ";");
		if (numPhotosPick > fileList.size()) {
			numPhotosPick = fileList.size();
		}
		int totalSize = fileList.size();

		for (int numPhotosPicked = 0; numPhotosPicked < numPhotosPick; numPhotosPicked++) {
			//File filePicked = fileMap.remove(idxFilePick);
			int idxPicked = rand.nextInt(fileList.size());
			File filePicked = fileList.remove(idxPicked);
			System.out.println("run:" + numPhotosPicked + "; picked:" + filePicked.getName() + "; list.size:"
					+ fileList.size() + ";");
			try {
				FileUtils.copyFileToDirectory(filePicked, ConfigurationManager.getDestinationDirectory());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		EmailTemplate.writeToFile(ConfigurationManager.getDestinationDirectory(),
				directoryPicked.getParentFile().getName(), "..." + File.separator + directoryPicked.getName(),
				numPhotosPick + " photos are picked out of " + totalSize + " ==> "
						+ ((float) numPhotosPick) / totalSize * 100 + "%",
				SIGNATURE);
		System.out.println("PhotosOfTheDay.main: END");
	}

	private static Map<Integer, File> populateDirectoryMap() throws IOException {
		Map<Integer, File> dirMap = new HashMap<Integer, File>();

		File[] dirFirstLevel = ConfigurationManager.getSourceDirectory().listFiles();

		Integer counter = 0;

		if (dirFirstLevel != null) {
			System.out.println("__1st level directory size:" + dirFirstLevel.length + ";");
			for (File aDir : dirFirstLevel) {
				if (!aDir.isDirectory()) {
					continue;
				}
				//System.out.println("1st level directory: " + aDir.getCanonicalPath());
				File[] dirSecondLevel = aDir.listFiles();
				if (dirSecondLevel != null) {
					for (File bDir : dirSecondLevel) {
						//System.out.println("2nd level directory: " + bDir.getCanonicalPath());
						if (!bDir.isDirectory()) {
							continue;
						}
						counter++;
						dirMap.put(counter, bDir);
					}
				}
			}
		} else {
			System.out.println("populateDirectoryMap: ERROR - 1st level directory is empty");
		}

		System.out.println("__dirMap.size:" + dirMap.size() + ";");
		return dirMap;
	}

}
