package com.zxxkj.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool extends ThreadPoolExecutor{

	private int corePoolSize;

	private static long keepAliveTime = 1;

	private static TimeUnit unit = TimeUnit.SECONDS;

	private static RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();


	public ThreadPool(int corePoolSize, int workQueueSize) {

		super(corePoolSize, corePoolSize, keepAliveTime, unit, new ArrayBlockingQueue<Runnable>(workQueueSize), handler);
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public ExecutorService getThreadPool(int corePoolSize) {
//		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(corePoolSize);
		return Executors.newFixedThreadPool(corePoolSize);
	}
}
