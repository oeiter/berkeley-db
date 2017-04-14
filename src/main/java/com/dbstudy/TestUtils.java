package com.dbstudy;

import java.io.File;

public class TestUtils {

	public static File createTempSubdir(String string) {
		File f = new File("E:/tempqueue/"+string);
		if(f.exists()){
			f.delete();
		}
		f.mkdir();
		return f;
	}

}
