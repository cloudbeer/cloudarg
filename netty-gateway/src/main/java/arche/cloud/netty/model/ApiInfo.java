package arche.cloud.netty.model;

/**
 * Api 描述
 */
public class ApiInfo {
    public ApiInfo() {
        schema = "http";
    }

    private String name;
    private String description;
    private String schema;

    /**
     * 该 API 可以支持的 http method
     */
    private String method;
    /**
     * 该 API 的入口
     */
    private String path;
    /**
     * 允许的角色
     */
    private String[] authorizedRoles;

    /**
     * 禁止的角色
     */
    private String[] forbiddenRoles;

    /**
     * 是否封装结果
     */
    private boolean wrapperResponse;
    private boolean cors;
    private String backendHost;
    private String backendPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String[] getAuthorizedRoles() {
        return authorizedRoles;
    }

    public void setAuthorizedRoles(String[] authorizedRoles) {
        this.authorizedRoles = authorizedRoles;
    }

    public String[] getForbiddenRoles() {
        return forbiddenRoles;
    }

    public void setForbiddenRoles(String[] forbiddenRoles) {
        this.forbiddenRoles = forbiddenRoles;
    }

    public boolean isWrapperResponse() {
        return wrapperResponse;
    }

    public void setWrapperResponse(boolean wrapperResponse) {
        this.wrapperResponse = wrapperResponse;
    }

    public boolean isCors() {
        return cors;
    }

    public void setCors(boolean cors) {
        this.cors = cors;
    }

    public String getBackendHost() {
        return backendHost;
    }

    public void setBackendHost(String backendHost) {
        this.backendHost = backendHost;
    }

    public String getBackendPath() {
        return backendPath;
    }

    public void setBackendPath(String backendPath) {
        this.backendPath = backendPath;
    }
}
