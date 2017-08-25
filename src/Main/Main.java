package Main;

import DBService.DBService;
import PageProcessing.CrawlerManager;
import java.io.*;
import java.util.logging.LogManager;


// Основной класс.
public class Main
{
    // Точка входа.
    public static void main(String[] args)
    {
        // Тест подключения.
        DBService.getInstance();

        // Настройка логгера.
        try
        {
            InputStream stream = Main.class.getResourceAsStream("/logging.properties");
            LogManager.getLogManager().readConfiguration(stream);
        }
        catch (IOException e)
        {
            System.out.println("Cannot load logging configuration: " + e.toString());
        }

        // Запуск краулера.
        //new CrawlerManager().start(60);
    }
}
