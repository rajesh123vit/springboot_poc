package com.ge.crawler;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ge.crawler.data.CrawlerData;
import com.ge.crawler.fileReader.JsonReader;
import com.ge.crawler.threadmngmt.CrawlerThread;

/**
 * 
 * For the purposes of this project, we define the “Internet” 
 * as the test JSON file included and a web crawler as software that requests pages
 * from the “Internet”, parses the content to extract all the links in the page,
 * and visits the links to crawl those pages, to an infinite depth
 * 
 * @author Rajesh Ranjan
 * 
 * 04/27/2019
 *
 */

@Controller
@RequestMapping("/gedigital")
public class WebCrawlerController {

	  @RequestMapping("/crawler")
	  public void crawlGeDigitalPage() {
		  
	    // How long to wait for threads to finish
        long     timeout  = 5;
        TimeUnit t_unit   = TimeUnit.SECONDS;
        int      poolSize = 10;

        String filename = new String();
        
        filename = "internet_1.json";
        
        // Create the "Internet"
        JsonReader internet  = new JsonReader(filename);
        CrawlerData  crawlerDB = new CrawlerData();

        ExecutorService pool = Executors.newFixedThreadPool(poolSize);

        
        String firstAddress = "";
        Scanner in = new Scanner(System.in);
        System.out.println("Please provide the address eg page-01: ");
        firstAddress = in.nextLine();
        
        // Launch the crawler
        try {

            pool.submit(new CrawlerThread(firstAddress, crawlerDB, internet, pool));

        } catch (RejectedExecutionException e) {
            e.printStackTrace();
        }

        // Shut it down
        try {

            pool.awaitTermination(timeout, t_unit);
            pool.shutdownNow();

        } catch (InterruptedException e) {
            e.printStackTrace();
            pool.shutdownNow();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        // display the results
        crawlerDB.print();
	  
	  }
}
