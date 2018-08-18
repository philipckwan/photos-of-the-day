package com.pck.potd;

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
			out.println("https://philipckwan.github.io/");
			out.println();
			out.println(
					"Please visit the above slideshow link as I have picked the \"Photos of the Day\" from my photo archive.");
			out.println("Enter the below passphrase exactly (follow the lower or upper cases):"); 
			out.println("elephant");
			out.println("And then click the button \"Go\". You will then see a slideshow of the photos");
			out.println();
			out.println("These photos are selected from:");
			out.println(pickedPhotoInfo1);
			out.println(pickedPhotoInfo2);
			out.println(pickedPhotoInfo3);
			out.println("");
			out.println();
			out.println(signature);
		}
	}
}
