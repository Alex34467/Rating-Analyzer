package DBService;

import Utility.MyLogger;
import java.sql.*;
import java.util.logging.Level;

// Сервис БД.
public class MySQLExecutor implements DBExecutor
{
    // Данные.
    private static MySQLExecutor instance;
    private static final String url = "jdbc:mysql://31.31.196.170:3306/u0389727_gbra13";
    private static final String user = "u0389_user";
    private static final String password = "rjvfylf1";
    private Statement statement;


    // Геттер.
    public static MySQLExecutor getInstance()
    {
        if (instance == null) instance = new MySQLExecutor();
        return instance;
    }

    // Выполнение запроса.
    @Override
    public ResultSet executeQuery(String query)
    {
        MyLogger.log(Level.INFO, "Executing: " + query);
        // Результат.
        ResultSet resultSet = null;

        try
        {
            resultSet = statement.executeQuery(query);
            MyLogger.log(Level.INFO, "Executed.");
            return resultSet;
        }
        catch (SQLException | NullPointerException e)
        {
            MyLogger.log(Level.SEVERE, "Error when executing: " + query);
            MyLogger.log(Level.SEVERE, "Cause: " + e.getMessage());
            return resultSet;
        }
    }

    // Выполнение запроса.
    @Override
    public void executeUpdate(String query)
    {
        MyLogger.log(Level.INFO, "Executing: " + query);
        try
        {
            statement.executeUpdate(query);
            MyLogger.log(Level.INFO, "Executed.");
        }
        catch (SQLException e)
        {
            MyLogger.log(Level.SEVERE, "Error while executing: " + query);
            MyLogger.log(Level.SEVERE, "Cause: " + e.getMessage());
        }
    }

    // Конструктор.
    private MySQLExecutor()
    {
        MyLogger.log(Level.INFO, "Starting MySQLExecutor");
        connect();
    }

    // Подключение.
    private void connect()
    {
        MyLogger.log(Level.INFO, "Connecting to DB at " + url);
        System.out.println("Connecting to DB at " + url);
        try
        {
            // Плдключение.
            Connection connection = DriverManager.getConnection(url, user, password);
            MyLogger.log(Level.INFO, "Successfully connected to database.");
            System.out.println("Successfully connected to database.");

            // Создание состояния.
            statement = connection.createStatement();
        }
        catch (SQLException e)
        {
            MyLogger.log(Level.SEVERE, "Connection failed: " + e.getMessage());
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
