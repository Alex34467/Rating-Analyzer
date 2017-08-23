package Parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

// Парсер robots.txt
public class RobotsParser
{
    // Парсинг robots.txt.
    public static String getSitemapUrl(String siteUrl)
    {
        try
        {
            // Выделение хоста.
            URL url = new URL(siteUrl);

            // Поиск Sitemap.
            String line;
            String result = null;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((line = bufferedReader.readLine()) != null)
            {
                if (line.startsWith("Sitemap"))
                {
                    result = line.substring(9);
                }
            }
            bufferedReader.close();

            // Возврат результата.
            return result;
        }
        catch (IOException e)
        {
            return null;
        }
    }
}
