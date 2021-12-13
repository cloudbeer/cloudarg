package arche.cloud.netty.config;

public class Config {

    private String name;
    private String description;
    private int port;
    private Mysql mysql;
    private String accountUrl;
    private String ticketSecret;

    public String getRedisUri() {
        return redisUri;
    }

    public void setRedisUri(String redisUri) {
        this.redisUri = redisUri;
    }

    private String redisUri;

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

    public Mysql getMysql() {
        return mysql;
    }

    public void setMysql(Mysql mysql) {
        this.mysql = mysql;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAccountUrl() {
        return accountUrl;
    }

    public void setAccountUrl(String accountUrl) {
        this.accountUrl = accountUrl;
    }

    public String getTicketSecret() {
        return ticketSecret;
    }

    public void setTicketSecret(String ticketSecret) {
        this.ticketSecret = ticketSecret;
    }
}
