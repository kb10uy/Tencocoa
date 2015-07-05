package org.kb10uy.tencocoa.model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import twitter4j.Dispatcher;
import twitter4j.conf.Configuration;

public final class TencocoaDispatcher implements Dispatcher {
    public ExecutorService service;

    public TencocoaDispatcher(Configuration config) {
        service = Executors.newFixedThreadPool(config.getAsyncNumThreads());
    }

    @Override
    public void invokeLater(Runnable task) {
        if (task != null) service.execute(task);
    }

    @Override
    public void shutdown() {
        if (!service.isShutdown()) service.shutdown();
    }
}