package utils;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class SkierPhase implements Runnable {
    private Integer numThreads;
    private Integer numSkiers;
    private Integer numLifts;
    private Integer numRuns;
    private Integer range;
    private Integer numRequestToSend;
    private Integer startTime;
    private Integer endTime;
    private String url;
    private ClientPartEnum partChosen;
    // fields created on init
    private AtomicInteger successCount;
    private AtomicInteger failureCount;
    // Both phase 2 and 3 starts at 20%
    private static double PERCENT_TO_START = 0.2;
    private static int WAIT_TIME_MAX = 10;
    private static final SecureRandom random = new SecureRandom();
    private CountDownLatch startNext;
    private CountDownLatch isComplete;
    private CloseableHttpClient client;

    public SkierPhase(Integer numThreads, Integer numSkiers, Integer numLifts, Integer numRuns, Integer range, Integer numRequestToSend, Integer startTime, Integer endTime, String url, ClientPartEnum partChosen) {
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
        this.failureCount = new AtomicInteger(0);
        this.startNext = new CountDownLatch((int) Math.ceil(numThreads * PERCENT_TO_START));
        this.isComplete = new CountDownLatch(numThreads*numRequestToSend);
        this.client = HttpClients.custom()
                .setServiceUnavailableRetryStrategy(new RetryStrategy())
                .build();
    }

    public Integer getNumThreads() {
        return numThreads;
    }

    public Integer getNumSkiers() {
        return numSkiers;
    }

    public Integer getNumLifts() {
        return numLifts;
    }

    public Integer getNumRuns() {
        return numRuns;
    }

    public Integer getRange() {
        return range;
    }

    public Integer getNumRequestToSend() {
        return numRequestToSend;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public String getUrl() {
        return url;
    }

    public ClientPartEnum getPartChosen() {
        return partChosen;
    }

    public static double getPercentToStart() {
        return PERCENT_TO_START;
    }

    public static int getWaitTimeMax() {
        return WAIT_TIME_MAX;
    }

    public CountDownLatch getStartNext() {
        return startNext;
    }

    public CountDownLatch getIsComplete() {
        return isComplete;
    }

    public void setNumThreads(Integer numThreads) {
        this.numThreads = numThreads;
    }

    public void setNumSkiers(Integer numSkiers) {
        this.numSkiers = numSkiers;
    }

    public void setNumLifts(Integer numLifts) {
        this.numLifts = numLifts;
    }

    public void setNumRuns(Integer numRuns) {
        this.numRuns = numRuns;
    }

    public void setRange(Integer range) {
        this.range = range;
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

    public void incrementFailure() {
        failureCount.getAndIncrement();
    }

    public int getFailureCount() {
        return failureCount.get();
    }


    @Override
    public void run() {
        System.out.println("Number of calls being made: " + this.numThreads*this.numRequestToSend);
        for (int i = 0; i < this.numThreads; i++) {
            int rangeChunk = (int) Math.ceil(this.range / this.numThreads);
            int startRange = i * rangeChunk;
            int endRange = startRange + rangeChunk;
            int skierID = random.nextInt(endRange - startRange + 1) + startRange;
            int liftID = Math.abs(random.nextInt());
            int time = random.nextInt(this.endTime - this.startTime + 1) + this.startTime;
            int waitTime = random.nextInt(WAIT_TIME_MAX);

        Runnable thread = () -> {

                // send a number of POST request
                for (int j = 0; j < this.numRequestToSend; j++) {
                    try {
                        PostConnection newPost = new PostConnection(this.client, this.url, skierID, liftID, time, waitTime);
                        Record result = newPost.makeConnection();

                        if (result.getResponseCode() == HttpStatus.SC_CREATED) {
                            this.incrementSuccess();
                        } else {
                            System.out.println("FAILURE");
                            this.incrementFailure();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        this.isComplete.countDown();
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
        this.isComplete();
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    public void isComplete() {
        try {
            this.isComplete.await();
        } catch (InterruptedException e) {
            System.err.println("Completion Countdown Latch Exception");
            e.printStackTrace();
        }
    }
}

