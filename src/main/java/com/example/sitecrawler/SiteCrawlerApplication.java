package com.example.sitecrawler;


import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SiteCrawlerApplication {

    public static String DirectoryToStorePages = "C:\\crowler-data";
    public static String WebPage;

    public static void main(String[] args) throws Exception {

        if(args.length == 0){
            System.err.println("Please enter a valid url");
            System.exit(0);
        }

        WebPage = args[0];
        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(DirectoryToStorePages);

        config.setMaxPagesToFetch(1000);
        config.setIncludeBinaryContentInCrawling(false);
        config.setResumableCrawling(false);
        config.setMaxDepthOfCrawling(1);
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        controller.addSeed("https://" + args[0]);

        int numberOfCrawlers = 8;
        CrawlController.WebCrawlerFactory<BasicCrawler> factory = BasicCrawler::new;
        controller.start(factory, numberOfCrawlers);
    }

}
