package com.pck.potd;

public enum OperationMode {
	PICK_PHOTOS, MOVE_DUPLICATES, PICK_PHOTOS_AND_MOVE_DUPLICATES;

	public static OperationMode stringToMode(String input) {
		if (input == null) {
			return PICK_PHOTOS;
		}
		if (input.equals("PICK_PHOTOS")) {
			return PICK_PHOTOS;
		}
		if (input.equals("MOVE_DUPLICATES")) {
			return MOVE_DUPLICATES;
		}
		if (input.equals("PICK_PHOTOS_AND_MOVE_DUPLICATES")) {
			return PICK_PHOTOS_AND_MOVE_DUPLICATES;
		}
		return PICK_PHOTOS;
	}
}
