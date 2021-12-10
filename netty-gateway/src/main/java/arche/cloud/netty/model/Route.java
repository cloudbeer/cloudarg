package arche.cloud.netty.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    /** 编号 */
    private Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(Long _id) {
        this.id = _id;
    }

    /** 标题 */
    private String title;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String _title) {
        this.title = _title;
    }

    /** 描述 */
    private String description;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String _description) {
        this.description = _description;
    }

    /** 项目 */
    @SerializedName("project_name")
    private String projectName;

    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName(String _projectName) {
        this.projectName = _projectName;
    }

    /** 版本 */
    private String version;

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String _version) {
        this.version = _version;
    }

    /** 路径 */
    private String path;

    public String getPath() {
        return this.path;
    }

    public void setPath(String _path) {
        this.path = _path;
    }

    /** 完整路径 */
    @SerializedName("full_path")
    private String fullPath;

    public String getFullPath() {
        return this.fullPath;
    }

    public void setFullPath(String _fullPath) {
        this.fullPath = _fullPath;
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

    /** qps */
    private Integer qps;

    public Integer getQps() {
        return this.qps;
    }

    public void setQps(Integer _qps) {
        this.qps = _qps;
    }

    private ArrayList<Backend> backends;

    public ArrayList<Backend> getBackends() {
        return backends;
    }

    public void setBackends(ArrayList<Backend> backends) {
        this.backends = backends;
    }

}
