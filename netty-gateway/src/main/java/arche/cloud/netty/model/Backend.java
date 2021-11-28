package arche.cloud.netty.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 后端
 *
 * @Author Cloudust
 */
public class Backend {

    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
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

    public void setId(Long _id) {
        this.id = _id;
    }

    /**
     * 标题
     */
    private String title;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String _title) {
        this.title = _title;
    }

    /**
     * 路由
     */
    private Long routeId;

    public Long getRouteId() {
        return this.routeId;
    }

    public void setRouteId(Long _routeId) {
        this.routeId = _routeId;
    }

    /**
     * 环境
     */
    private String env;

    public String getEnv() {
        return this.env;
    }

    public void setEnv(String _env) {
        this.env = _env;
    }

    /**
     * 主机
     */
    private String host;

    public String getHost() {
        return this.host;
    }

    public void setHost(String _host) {
        this.host = _host;
    }

    /**
     * 端口
     */
    private Integer port;

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer _port) {
        this.port = _port;
    }

    /**
     * 协议
     */
    private String schema;

    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String _schema) {
        this.schema = _schema;
    }

    /**
     * 路径
     */
    private String path;

    public String getPath() {
        return this.path;
    }

    public void setPath(String _path) {
        this.path = _path;
    }

    /**
     * header 匹配
     */
    private String headerPattern;

    public String getHeaderPattern() {
        return this.headerPattern;
    }

    public void setHeaderPattern(String _headerPattern) {
        this.headerPattern = _headerPattern;
    }

    /**
     * 路径匹配
     */
    private String pathPattern;

    public String getPathPattern() {
        return this.pathPattern;
    }

    public void setPathPattern(String _pathPattern) {
        this.pathPattern = _pathPattern;
    }

    /**
     * query匹配
     */
    private String queryPattern;

    public String getQueryPattern() {
        return this.queryPattern;
    }

    public void setQueryPattern(String _queryPattern) {
        this.queryPattern = _queryPattern;
    }

    /**
     * body匹配
     */
    private String bodyPattern;

    public String getBodyPattern() {
        return this.bodyPattern;
    }

    public void setBodyPattern(String _bodyPattern) {
        this.bodyPattern = _bodyPattern;
    }

    /**
     * 权重
     */
    private Integer weight;

    public Integer getWeight() {
        return this.weight;
    }

    public void setWeight(Integer _weight) {
        this.weight = _weight;
    }


}

