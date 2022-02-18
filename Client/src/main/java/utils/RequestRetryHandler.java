package utils;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class RequestRetryHandler implements HttpRequestRetryHandler  {
    private final static Integer NUM_RETRIES = 5;
    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        return executionCount <= NUM_RETRIES;
    }
}
