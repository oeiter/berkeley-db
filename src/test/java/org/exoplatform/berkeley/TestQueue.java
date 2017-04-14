package org.exoplatform.berkeley;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.dbstudy.Queue;
import com.dbstudy.QueueUtil;

public class TestQueue {

	@Test
	public void testCreateQueue() {
		File queueDir = QueueUtil.createTempSubdir("test-queue");
		Queue queue = new Queue(queueDir.getPath(), "test-queue", 3);
		try {
			assert Arrays.asList(queueDir.listFiles()).contains(
					new File(queueDir, "00000000.jdb"));
		} finally {
			queue.close();
		}
	}

	@Test
	public void testPush() throws Throwable {
		File queueDir = QueueUtil.createTempSubdir("test-queue");
		Queue queue = new Queue(queueDir.getPath(), "test-queue", 3);
		try {
			queue.push("1");
			queue.push("2");
			String head = queue.poll();

			assert head.equals("1");
		} finally {
			queue.close();
		}
	}

	@Test
	public void testQueueSurviveReopen() throws Throwable {
		File queueDir = QueueUtil.createTempSubdir("test-queue");
		Queue queue = new Queue(queueDir.getPath(), "test-queue", 3);
		try {
			queue.push("5");
		} finally {
			queue.close();
		}

		queue = new Queue(queueDir.getPath(), "test-queue", 3);
		try {
			String head = queue.poll();

			assert head.equals("5");
		} finally {
			queue.close();
		}
	}

	@Test
	public void testQueuePushOrder() throws Throwable {
		File queueDir = QueueUtil.createTempSubdir("test-queue");
		final Queue queue = new Queue(queueDir.getPath(), "test-queue", 1000);
		try {
			for (int i = 0; i < 300; i++) {
				queue.push(Integer.toString(i));
			}

			for (int i = 0; i < 300; i++) {
				String element = queue.poll();
				if (!Integer.toString(i).equals(element)) {
					throw new AssertionError("Expected element " + i
							+ ", but got " + element);
				}
			}
		} finally {
			queue.close();
		}

	}
	@Test
	public void testQueuePush() throws Throwable {
		File queueDir = QueueUtil.createTempSubdir("test-queue");
		final Queue queue = new Queue(queueDir.getPath(), "test-queue", 1000);
		try {
			int size = 30000;
			String[] ss = new String[size];
			for (int i = 0; i < 30000; i++) {
				String s = Integer.toString(i);
				ss[i]=s;
				queue.push(s);
			}

			for (int i = 0; i < 30000; i++) {
				String element = queue.poll();
				if (!ss[i].equals(element)) {
					throw new AssertionError("Expected element " + i
							+ ", but got " + element);
				}
			}
			System.out.println(queue.size());
		} finally {
			queue.close();
		}

	}
	@Test
	public void testMultiThreadedPoll() throws Throwable {
		File queueDir = QueueUtil.createTempSubdir("test-queue");
		final Queue queue = new Queue(queueDir.getPath(), "test-queue", 3);
		try {
			int threadCount = 20;
			for (int i = 0; i < threadCount; i++)
				queue.push(Integer.toString(i));

			final Set set = Collections.synchronizedSet(new HashSet());
			final CountDownLatch startLatch = new CountDownLatch(threadCount);
			final CountDownLatch latch = new CountDownLatch(threadCount);

			for (int i = 0; i < threadCount; i++) {
				new Thread() {
					public void run() {
						try {
							startLatch.countDown();
							startLatch.await();

							String val = queue.poll();
							if (val != null) {
								set.add(val);
							}
							latch.countDown();
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}.start();
			}

			latch.await(5, TimeUnit.SECONDS);

			assert set.size() == threadCount;
		} finally {
			queue.close();
		}
	}

	@Test
	public void testMultiThreadedPush() throws Throwable {
		File queueDir = QueueUtil.createTempSubdir("test-queue");
		final Queue queue = new Queue(queueDir.getPath(), "test-queue", 3);
		try {
			int threadCount = 20;

			final CountDownLatch startLatch = new CountDownLatch(threadCount);
			final CountDownLatch latch = new CountDownLatch(threadCount);

			for (int i = 0; i < threadCount; i++) {
				new Thread(Integer.toString(i)) {
					public void run() {
						try {
							startLatch.countDown();
							startLatch.await();

							queue.push(getName());
							latch.countDown();
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}.start();
			}

			latch.await(5, TimeUnit.SECONDS);

			assert queue.size() == threadCount;
		} finally {
			queue.close();
		}
	}
}
