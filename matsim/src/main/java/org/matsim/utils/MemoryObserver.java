package org.matsim.utils;

import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author mrieser / Simunto GmbH
 */
public class MemoryObserver {

	private final static Logger LOG = Logger.getLogger(MemoryObserver.class);
	private static final AtomicReference<MemoryPrinter> CURRENT_PRINTER = new AtomicReference<>();

	public static void start(int interval_seconds) {
		startMillis(interval_seconds * 1000L);
	}

	static void startMillis(long interval) {
		MemoryPrinter newPrinter = new MemoryPrinter(interval);
		MemoryPrinter previousPrinter = CURRENT_PRINTER.getAndSet(newPrinter);
		stopPrinter(previousPrinter);
    }

	public static void stop() {
		stopPrinter(CURRENT_PRINTER.getAndSet(null));
	}

	private static void stopPrinter(MemoryPrinter printer) {
		if (printer != null) {
			printer.stop();
		}
	}

	public static void printMemory() {
		long totalMem = Runtime.getRuntime().totalMemory();
		long freeMem = Runtime.getRuntime().freeMemory();
		long usedMem = totalMem - freeMem;
		LOG.info("used RAM: " + (usedMem/1024/1024) + " MB  free: " + (freeMem/1024/1024) + " MB  total: " + (totalMem/1024/1024) + " MB");
	}

	private static class MemoryPrinter implements Runnable {

		private final long millis;
		private final AtomicBoolean stopFlag = new AtomicBoolean(false);
		private final Thread thread;

		MemoryPrinter(long millis) {
			this.millis = millis;
			this.thread = new Thread(this, "MemoryPrinter");
			this.thread.setDaemon(true);
			this.thread.start();
		}

		public void run() {
			while (true) {
				MemoryObserver.printMemory();

				try {
					Thread.sleep(this.millis);
				} catch (InterruptedException e) {
					if (this.stopFlag.get()) {
						return;
					}
					e.printStackTrace();
				}
			}
		}

		private void stop() {
			this.stopFlag.set(true);
			this.thread.interrupt();
		}
	}


}
