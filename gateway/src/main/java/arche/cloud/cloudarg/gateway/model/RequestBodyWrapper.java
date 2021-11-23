package arche.cloud.cloudarg.gateway.model;

public class RequestBodyWrapper {
    private String ticket;
    private String requestId;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
