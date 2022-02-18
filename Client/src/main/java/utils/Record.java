package utils;

public class Record {
    private final Long startTime;
    private final String requestType;
    private final Integer responseCode;
    private final Long latency;

    public Record(Long startTime, String requestType, Integer responseCode, Long latency) {
        this.startTime = startTime;
        this.requestType = requestType;
        this.responseCode = responseCode;
        this.latency = latency;
    }

    public Record() {
        this((long)-1,"None",-1,(long)-1);
    }

    public Long getStartTime() {
        return startTime;
    }

    public String getRequestType() {
        return requestType;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public Long getLatency() {
        return latency;
    }

    @Override
    public String toString() {
        return "Record{" +
                "startTime=" + startTime +
                ", requestType='" + requestType + '\'' +
                ", responseCode=" + responseCode +
                ", latency=" + latency +
                '}';
    }

}
