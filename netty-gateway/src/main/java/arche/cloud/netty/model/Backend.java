package arche.cloud.netty.model;

import arche.cloud.netty.utils.GsonUtil;

/**
 * 后端
 *
 * @Author Cloudust
 */
public class Backend {

    public String toString() {
        return GsonUtil.toString(this);
    }

    public String toUrl() {
        schema = schema == null ? "http" : schema;
        port = port > 0 ? port : 80;
        return schema + "://" +
                host + ":" +
                port + path;
    }

    /**
     * 编号
     */
    private Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 标题
     */
    private String title;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 路由
     */
    private Long routeId;

    public Long getRouteId() {
        return this.routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    /**
     * 环境
     */
    private String env;

    public String getEnv() {
        return this.env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    /**
     * 主机
     */
    private String host;

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 端口
     */
    private Integer port;

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * 协议
     */
    private String schema;

    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * 路径
     */
    private String path;

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * header 匹配
     */
    private String headerPattern;

    public String getHeaderPattern() {
        return this.headerPattern;
    }

    public void setHeaderPattern(String headerPattern) {
        this.headerPattern = headerPattern;
    }

    /**
     * 路径匹配
     */
    private String pathPattern;

    public String getPathPattern() {
        return this.pathPattern;
    }

    public void setPathPattern(String pathPattern) {
        this.pathPattern = pathPattern;
    }

    /**
     * query匹配
     */
    private String queryPattern;

    public String getQueryPattern() {
        return this.queryPattern;
    }

    public void setQueryPattern(String queryPattern) {
        this.queryPattern = queryPattern;
    }

    /**
     * body匹配
     */
    private String bodyPattern;

    public String getBodyPattern() {
        return this.bodyPattern;
    }

    public void setBodyPattern(String bodyPattern) {
        this.bodyPattern = bodyPattern;
    }

    /**
     * 权重
     */
    private Integer weight;

    public Integer getWeight() {
        return this.weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

}
