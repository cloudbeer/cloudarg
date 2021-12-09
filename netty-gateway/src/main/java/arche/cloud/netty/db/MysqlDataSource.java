package arche.cloud.netty.db;

import arche.cloud.netty.config.ConfigFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MysqlDataSource {
    private static HikariDataSource ds;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" +
                ConfigFactory.config.getMysql().getHost() + ":" +
                ConfigFactory.config.getMysql().getPort() + "/" +
                ConfigFactory.config.getMysql().getDatabase());
        config.setUsername(ConfigFactory.config.getMysql().getUsername());
        config.setPassword(ConfigFactory.config.getMysql().getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        System.err.println(config);
        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private MysqlDataSource() {

    }
}
