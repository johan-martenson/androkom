package org.lysator.lattekom;

import java.util.LinkedList;

public class AsynchInvoker extends Thread {
    LinkedList<Runnable> runnables = new LinkedList<Runnable>();

    public AsynchInvoker() {
        setName("AsynchInvokerThread");
    }

    boolean run = true;

    public void run() {
        while (run) {
            Debug.println("Started.");
            try {
                while (!runnables.isEmpty()) {
                    Runnable nextRunnable = null;
                    synchronized (runnables) {
                        nextRunnable = (Runnable) runnables.removeFirst();
                    }
                    if (nextRunnable != null) {
                        Debug.println("Executing " + nextRunnable.toString());
                        nextRunnable.run();
                    } else {
                        Debug.println("Woke up without events: run=" + run);
                    }
                }
                synchronized (runnables) {
                    Debug.println("Waiting.");
                    runnables.wait();
                }
            } catch (InterruptedException ex1) {
                Debug.println("Interrupted.");
            }
        }
        Debug.println("Finished.");
    }

    public void quit() {
        run = false;
        synchronized (runnables) {
            runnables.notify();
        }

    }

    public void enqueue(Runnable r) {
        synchronized (runnables) {
            runnables.addLast(r);
            runnables.notifyAll();
        }
        if (!isAlive())
            start();
    }

}
