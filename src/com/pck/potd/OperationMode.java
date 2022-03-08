package com.pck.potd;

public enum OperationMode {
	PICK_PHOTOS, MOVE_DUPLICATES, RECURSIVE_FIND_DUPLICATES, RECURSIVE_MOVE_DUPLICATES;

	public static OperationMode stringToMode(String input) {
		if (input != null) {
			if (input.equals("PICK_PHOTOS")) {
				return PICK_PHOTOS;
			}
			if (input.equals("MOVE_DUPLICATES")) {
				return MOVE_DUPLICATES;
			}
			if (input.equals("RECURSIVE_FIND_DUPLICATES")) {
				return RECURSIVE_FIND_DUPLICATES;
			}
			if (input.equals("RECURSIVE_MOVE_DUPLICATES")) {
				return RECURSIVE_MOVE_DUPLICATES;
			}
		}
		return PICK_PHOTOS;
	}
}
