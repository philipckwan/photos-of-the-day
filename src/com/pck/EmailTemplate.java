package com.pck;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EmailTemplate {

	public static final String filename = "email_template.txt";

	public static void writeToFile(File destinationDirectory, String pickedPhotoInfo1, String pickedPhotoInfo2,
			String pickedPhotoInfo3, String signature) throws IOException {
		String filepath = destinationDirectory.getCanonicalPath() + File.separator + filename;
		System.out.println("EmailTemplate.writeToFile: filepath:" + filepath + ";");
		try (PrintWriter out = new PrintWriter(filepath)) {
			out.println("Photos of the Day - " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
			out.println();
			out.println("<insert Dropbox link here>");
			out.println();
			out.println(
					"Please visit the above Dropbox link as I have picked the \"Photos of the Day\" from my photo archive.");
			out.println();
			out.println("These photos of the day are selected from:");
			out.println(pickedPhotoInfo1);
			out.println(pickedPhotoInfo2);
			out.println(pickedPhotoInfo3);
			out.println("");
			out.println(
					"As the name suggested, the Photos of the Day from yesterday are already removed, you only be able to view today's photos.");
			out.println(
					"Note also that the photos are randomly chosen, so it might not be very satisfying. I can control the number of photos to be chosen each day, you can give me a suggestion.");
			out.println();
			out.println(signature);
		}
	}
}
