package arche.cloud.netty.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import arche.cloud.netty.utils.GsonUtil;

/*
 *  路由配置
 *  @Author Cloudust
 *
 * */
public class Route {
    public Route() {
        backends = new ArrayList<>();
    }

    public String toString() {
        return GsonUtil.toString(this);
    }

    /** 编号 */
    private Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** 标题 */
    private String title;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /** 描述 */
    private String description;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /** 项目 */
    @SerializedName("project_name")
    private String projectName;

    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /** 版本 */
    private String version;

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /** 路径 */
    private String path;

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /** 完整路径 */
    @SerializedName("full_path")
    private String fullPath;

    public String getFullPath() {
        return this.fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    /**
     * 允许的角色
     */
    @SerializedName("authorized_roles")
    private String[] authorizedRoles;

    public String[] getAuthorizedRoles() {
        return authorizedRoles;
    }

    public void setAuthorizedRoles(String[] authorizedRoles) {
        this.authorizedRoles = authorizedRoles;
    }

    /**
     * 禁止的角色
     */
    @SerializedName("forbidden_roles")
    private String[] forbiddenRoles;

    public String[] getForbiddenRoles() {
        return forbiddenRoles;
    }

    public void setForbiddenRoles(String[] forbiddenRoles) {
        this.forbiddenRoles = forbiddenRoles;
    }

    private int cors;

    public int getCors() {
        return cors;
    }

    public void setCors(int cors) {
        this.cors = cors;
    }

    private int wrapper;

    public int getWrapper() {
        return wrapper;
    }

    public void setWrapper(int wrapper) {
        this.wrapper = wrapper;
    }

    /** 限流 */
    @SerializedName("rate_limit")
    private Integer rateLimit;

    public Integer getRateLimit() {
        return this.rateLimit;
    }

    public void setRateLimit(Integer rateLimit) {
        this.rateLimit = rateLimit;
    }

    /** 降级方法 */
    private Short failover;

    public Short getFailover() {
        return this.failover;
    }

    public void setFailover(Short _failover) {
        this.failover = _failover;
    }

    /** 降级MIME */
    private String failoverContentType;

    public String getFailoverContentType() {
        return this.failoverContentType;
    }

    public void setFailoverContentType(String _failoverContentType) {
        this.failoverContentType = _failoverContentType;
    }

    /** 降级内容 */
    private Object failoverContent;

    public Object getFailoverContent() {
        return this.failoverContent;
    }

    public void setFailoverContent(Object _failoverContent) {
        this.failoverContent = _failoverContent;
    }

    /** 降级Url */
    private String failoverUrl;

    public String getFailoverUrl() {
        return this.failoverUrl;
    }

    public void setFailoverUrl(String _failoverUrl) {
        this.failoverUrl = _failoverUrl;
    }

    /** 黑名单 */
    @SerializedName("black_list")
    private String[] blackList;

    public String[] getBlackList() {
        return this.blackList;
    }

    public void setBlackList(String[] blackList) {
        this.blackList = blackList;
    }

    /** 白名单 */
    @SerializedName("white_list")
    private String[] whiteList;

    public String[] getWhiteList() {
        return this.whiteList;
    }

    public void setWhiteList(String[] whiteList) {
        this.whiteList = whiteList;
    }

    /** 是否mock */
    private Short mock;

    public Short getMock() {
        return this.mock;
    }

    public void setMock(Short _mock) {
        this.mock = _mock;
    }

    /** mock 数据类型 */
    @SerializedName("mock_content_type")
    private String mockContentType;

    public String getMockContentType() {
        return this.mockContentType;
    }

    public void setMockContentType(String _mockContentType) {
        this.mockContentType = _mockContentType;
    }

    /** Mock内容 */
    @SerializedName("mock_content")
    private String mockContent;

    public String getMockContent() {
        return this.mockContent;
    }

    public void setMockContent(String _mockContent) {
        this.mockContent = _mockContent;
    }

    /** mock地址 */
    @SerializedName("mock_content_url")
    private String mockContentUrl;

    public String getMockContentUrl() {
        return this.mockContentUrl;
    }

    public void setMockContentUrl(String _mockContentUrl) {
        this.mockContentUrl = _mockContentUrl;
    }

    private List<Backend> backends;

    public List<Backend> getBackends() {
        return backends;
    }

    public void setBackends(List<Backend> backends) {
        this.backends = backends;
    }

}
