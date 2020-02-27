package dao;//dao——>数据访问层，这里的类围绕着数据操作展开

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/java_image_server?characterEncoding=utf8&useSSL=true";
    private static String USERNAME ="root";
    private static String PASSWORD ="hr0127";

    private static volatile DataSource dataSource=null;//③加volatile

    //线程安全问题：①加锁  ②双重判断  ③加volatile

    public static DataSource getDataSource(){
        //通过这个方法创建 DataSource 的实例
        if(dataSource==null){//②双重判断
            synchronized (DBUtil.class){//①加锁
                if(dataSource==null){
                    dataSource=new MysqlDataSource();
                    MysqlDataSource tmpDataSouce=(MysqlDataSource)dataSource;
                    tmpDataSouce.setURL(URL);
                    tmpDataSouce.setUser(USERNAME);
                    tmpDataSouce.setPassword(PASSWORD);
                }
            }
        }
        return dataSource;
    }

    public static Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        try {
            if(resultSet!=null){
                resultSet.close();
            }
            if(statement!=null){
                statement.close();
            }
            if(connection!=null){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
