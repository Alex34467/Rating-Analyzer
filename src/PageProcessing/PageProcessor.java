package PageProcessing;

import DBService.DBService;
import Entities.*;
import PageProcessing.XMLParser.XMLParser;
import Parsing.*;
import Utility.MyLogger;
import Utility.Util;
import org.jsoup.Jsoup;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;


// Обработчик страниц.
public class PageProcessor implements Runnable
{
    // Данные.
    private String name;
    private Collection<Person> persons;
    private CrawlerManager manager;
    private KeywordParser parser;
    private int sleepTime = 5000;


    // Конструктор.
    public PageProcessor(CrawlerManager manager, String name)
    {
        this.name = name;
        this.manager = manager;
        persons = manager.getPersons();
        this.parser = new KeywordParser();
    }

    // Выполнение.
    @Override
    public void run()
    {
        MyLogger.log(Level.INFO, name +  ": started.");
        while (!Thread.currentThread().isInterrupted())
        {
            try
            {
                // Получение страницы.
                Page page = manager.getPage();

                if (page != null)
                {
                    // Обработка станицы.
                    MyLogger.log(Level.INFO, name + ": take page " + page.getUrl());
                    processPage(page);
                }
                else
                {
                    // Пауза.
                    MyLogger.log(Level.INFO, "No page found. Sleeping for " + sleepTime + " mills.");
                    Thread.sleep(sleepTime);
                }
            }
            catch (InterruptedException e)
            {
                MyLogger.log(Level.INFO, "Stopping.");
            }
        }
    }

    // Обработка страницы.
    public void processPage(Page page)
    {
        // Анализ страницы.
        MyLogger.log(Level.INFO, name +": process page: " + page.getUrl());
        String url = page.getUrl().toLowerCase();
        if (url.endsWith("robots.txt"))
        {
            MyLogger.log(Level.INFO, name +  ": it's robots.txt page.");
            processRobots(page);
        }
        else if (url.contains("sitemap") && url.endsWith(".xml"))
        {
            MyLogger.log(Level.INFO, name +  ": it's Sitemap.xml page.");
            processSitemap(page);
        }
        else if (url.endsWith(".gz"))
        {
            MyLogger.log(Level.INFO, name + ": it's Sitemap archive.");
        }
        else
        {
            MyLogger.log(Level.INFO, name + ": it's usual page.");
            processUsualPage(page);
        }

        // Обновление информации о странице.
        DBService.getInstance().updatePageScanDate(page, Util.getCuttentDateTime());
        manager.completePageProcessing(page);
        MyLogger.log(Level.INFO, name + ": page processed.");
    }

    // Обработка robots.txt.
    public void processRobots(Page page)
    {
        // Получение ссылки на Sitemap.
        MyLogger.log(Level.INFO, name + ": looking for Sitemap in: " + page.getUrl());
        String url = RobotsParser.getSitemapUrl(page.getUrl());

        // Анализ.
        if (url != null)
        {
            MyLogger.log(Level.INFO, name +  ": found Sitemap at: " + url);

            // Сборка страницы.
            Page page2 = new Page(url, page.getSiteId(), Util.getCuttentDateTime(), null);

            // Добавление станицы.
            DBService.getInstance().addPage(page2);
        }
        else
        {
            MyLogger.log(Level.INFO, name + ": Sitemap not found.");
        }
    }

    // Обработка Sitemap.
    public void processSitemap(Page page)
    {
        // Получение ссылок.
        Set<String> links = XMLParser.parseXML(page.getUrl());

        // Добавление ссылок.
        MyLogger.log(Level.INFO, name +  ": found " + links.size() + " links.");
        for (String link : links)
        {
            // Сборка страницы.
            Page page2 = new Page(link, page.getSiteId(), Util.getCuttentDateTime(), null);

            // Добавление страницы.
            DBService.getInstance().addPage(page2);
        }
        MyLogger.log(Level.INFO, name +  ": all links processed.");
    }

    // Обработка обычной страницы.
    public void processUsualPage(Page page)
    {
        // Получение списка личностей.
        try
        {
            // Получение страницы.
            String document = Jsoup.connect(page.getUrl()).get().html();
            int pageSize = document.length() / 1000;
            MyLogger.log(Level.INFO, ": parse text data: ~" + pageSize + "k chars.");

            // Обход личностей.
            for (Person person : persons)
            {
                // Получение ключевых слов.
                Collection<Keyword> keywords = person.getKeywords();

                // Обход ключевых слов.
                int matches = 0;
                for (Keyword keyword : keywords)
                {
                    // Подсчет совпадеий.
                    matches += parser.countMatches(document, keyword.getName());
                }

                // Запись результата в БД.
                PersonsPageRank rank = new PersonsPageRank(person.getId(), page.getId(), matches);
                DBService.getInstance().addPersonsPageRank(rank);
            }
        }
        catch (IOException e)
        {
            MyLogger.log(Level.SEVERE, name + ": " + e.toString());
        }
    }
}
