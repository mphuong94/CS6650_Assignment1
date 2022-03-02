package utils;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main class to run different threads
 * and post multiple requests to server
 */
public class SkierPhase implements Runnable {
    private static final SecureRandom random = new SecureRandom();
    // Both phase 2 and 3 starts at 20%
    private static final double PERCENT_TO_START = 0.2;
    private static final int WAIT_TIME_MAX = 10;
    private final Integer numThreads;
    private final Integer numSkiers;
    private final Integer numLifts;
    private final Integer numRuns;
    private final Integer range;
    private final Integer numRequestToSend;
    private final Integer startTime;
    private final Integer endTime;
    private final String url;
    private ClientPartEnum partChosen;
    // fields created on init
    private final AtomicInteger successCount;
    private final CountDownLatch startNext;
    CloseableHttpClient client;
    private final Integer totalCalls;
    private final List<LatencyStat> history = Collections.synchronizedList(new ArrayList<>());

    public SkierPhase(Integer numThreads, Integer numSkiers, Integer numLifts, Integer numRuns, Integer range, Integer numRequestToSend, Integer startTime, Integer endTime, String url, ClientPartEnum partChosen, CloseableHttpClient client) {
        this.numThreads = numThreads;
        this.numSkiers = numSkiers;
        this.numLifts = numLifts;
        this.numRuns = numRuns;
        this.range = range;
        this.numRequestToSend = numRequestToSend;
        this.startTime = startTime;
        this.endTime = endTime;
        this.url = url;
        this.partChosen = partChosen;
        this.successCount = new AtomicInteger(0);
        this.startNext = new CountDownLatch((int) Math.ceil(numThreads * PERCENT_TO_START));
        this.totalCalls = this.numThreads * this.numRequestToSend;
        this.client = client;
    }

    public List<LatencyStat> getHistory() {
        return history;
    }

    public void setPartChosen(ClientPartEnum partChosen) {
        this.partChosen = partChosen;
    }

    public void incrementSuccess() {
        successCount.getAndIncrement();
    }

    public int getSuccessCount() {
        return successCount.get();
    }


    public Integer getTotalCalls() {
        return totalCalls;
    }

    /**
     * Method to make a post request with the defined specifications
     */
    @Override
    public void run() {
        System.out.println("Number of calls being made: " + this.getTotalCalls());
        for (int i = 0; i < this.numThreads; i++) {
            int rangeChunk = (int) Math.ceil(this.range / this.numThreads);
            int startRange = i * rangeChunk;
            int skierID = random.nextInt(rangeChunk) + startRange + 1;
            int liftID = Math.abs(random.nextInt());
            int time = random.nextInt(this.endTime - this.startTime + 1) + this.startTime;
            int waitTime = random.nextInt(WAIT_TIME_MAX);

            Runnable thread = () -> {
                // send a number of POST request
                for (int j = 0; j < this.numRequestToSend; j++) {
                    try {
                        PostConnection newPost = new PostConnection(client, url, skierID, liftID, time, waitTime);
                        LatencyStat result = newPost.makeConnection(this.partChosen);
                        if (result.getResponseCode() == HttpStatus.SC_CREATED) {
                            this.incrementSuccess();
                        } else {
                            System.out.println("FAILURE");
                        }

                        if (this.partChosen == ClientPartEnum.PART2) {
                            this.history.add(result);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // uncomment to run in slow connection
//            try {
//                Thread.sleep(5);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
                this.startNext.countDown();
            };
            new Thread(thread).start();
        }
    }

    public void isNextReady() {
        try {
            this.startNext.await();
        } catch (InterruptedException e) {
            System.err.println("Next Phase Countdown Latch Exception");
            e.printStackTrace();
        }
    }
}

