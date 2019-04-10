package com.pck.potd;

import java.io.File;

public class FileNode {
	private File file;
	private String hash = null;

	public FileNode(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

}
