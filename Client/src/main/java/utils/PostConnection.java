package utils;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PostConnection {
    private String url;
    private Integer skierID;
    private Integer liftID;
    private Integer minuteRange;
    private Integer waitTime;

    public PostConnection(String url, Integer skierID, Integer liftID, Integer minuteRange, Integer waitTime) {
        this.url = url;
        this.skierID = skierID;
        this.liftID = liftID;
        this.minuteRange = minuteRange;
        this.waitTime = waitTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSkierID() {
        return skierID;
    }

    public void setSkierID(Integer skierID) {
        this.skierID = skierID;
    }

    public Integer getLiftID() {
        return liftID;
    }

    public void setLiftID(Integer liftID) {
        this.liftID = liftID;
    }

    public Integer getMinuteRange() {
        return minuteRange;
    }

    public void setMinuteRange(Integer minuteRange) {
        this.minuteRange = minuteRange;
    }

    public Integer getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(Integer waitTime) {
        this.waitTime = waitTime;
    }

    public Record makeConnection() throws IOException {
        Record result = new Record();
        CloseableHttpClient client = HttpClients.custom()
//                .setRetryHandler(new RequestRetryHandler())
                .build();
        HttpPost request = new HttpPost(this.url);
        List<BasicNameValuePair> urlParameters = new ArrayList<>();

        urlParameters.add(new BasicNameValuePair("SkierID", this.getSkierID().toString()));
        urlParameters.add(new BasicNameValuePair("LiftID", this.getLiftID().toString()));
        urlParameters.add(new BasicNameValuePair("Minute Range", this.getMinuteRange().toString()));
        urlParameters.add(new BasicNameValuePair("Wait Time", this.getWaitTime().toString()));

        HttpEntity postParams = new UrlEncodedFormEntity(urlParameters);
        request.setEntity(postParams);

        try {
            long start = System.currentTimeMillis();
            CloseableHttpResponse response = client.execute(request);
            int status = response.getStatusLine().getStatusCode();
            String requestType = "POST";
            // Execute the method.
            if (status != HttpStatus.SC_CREATED) {
                System.err.println("Method failed: " + status);
            }
            response.close();
            long end = System.currentTimeMillis();
            long latency = end - start;
            result = new Record(start,requestType,status,latency);
        } catch (HttpException e) {
            System.err.println("Fatal protocol violation: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Release the connection.
            client.close();
            return result;
        }
    }
}
