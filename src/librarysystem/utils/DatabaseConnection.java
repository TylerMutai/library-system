package librarysystem.utils;

import java.sql.Connection;
import java.sql.SQLException;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 *
 * 
 * Establishes and maintains connections to a database.
 *
 */

public class DatabaseConnection {
	public static final String DATABASE_NAME = "LibrarySystem";
	//We'll use different threads for different Database connections for sakes of efficiency.
	//Also, we'll use a thread pool so that we can re-use threads.
		 private static HikariConfig config = new HikariConfig();
		    private static HikariDataSource ds;
		    static { 
		        config.setJdbcUrl("jdbc:sqlite:" + "database/" + DATABASE_NAME + ".db");
		        config.addDataSourceProperty("cachePrepStmts", "true");
		        config.addDataSourceProperty("prepStmtCacheSize", "250");
		        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		        ds = new HikariDataSource(config);
		    }
		     
		    public static Connection getConnection() throws SQLException {
		        return ds.getConnection();
		    }
		     
	   }
	

