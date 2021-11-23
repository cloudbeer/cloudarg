package arche.cloud.cloudarg.gateway.model;

import java.util.Map;

public class RequestUser {
    private String id;
    private String url;
    private String method;
    private Map<String, String> query;
    private String formData;
    private String contentType;
}
