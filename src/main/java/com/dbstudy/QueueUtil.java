package com.dbstudy;

import java.io.File;

public class QueueUtil {
	public static void doInQueue(File queueDir, String queueName, int cacheSize, ActionInQueue action)throws Throwable{
		final Queue queue = new Queue(queueDir.getPath(), queueName, cacheSize);
		doInQueue(queue, action);
		
	}
	public static void doInQueue(Queue queue, ActionInQueue action)throws Throwable{
		try{
			action.doAction(queue);
		}finally{
			queue.close();
		}
		
	}
	public static File createTempSubdir(String string) {
		File f = new File("E:/tempqueue/"+string);
		if(f.exists()){
			f.delete();
		}
		f.mkdir();
		return f;
	}
}

