package com.ge.crawler.threadmngmt;

import java.util.concurrent.Executor;

import com.ge.crawler.data.CrawlerData;
import com.ge.crawler.fileReader.JsonReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * The worker for the web crawler.
 * 
 * @author Rajesh Ranjan
 * 
 * 04/27/2019
 *
 */
public class CrawlerThread implements Runnable {

    private String       url;
    private CrawlerData  crawlerDB;
    private JsonReader internet;
    private Executor     executor;

    /**
     * Creates a crawler thread
     * 
     * @param url       the address to process
     * @param crawlerDB the shared data
     * @param internet  the test "Internet"
     * @param executor  the executor being used
     */
    public CrawlerThread(String url, CrawlerData crawlerDB, JsonReader internet, Executor executor) {
        this.url       = url;
        this.crawlerDB = crawlerDB;
        this.internet  = internet;
        this.executor  = executor;
    }

    /**
     * runs this thread/process
     */
    @Override
    public void run() {

        // create an empty array for the links
        JsonArray links = new JsonArray();

        // can we connect to this page?
        if (internet.connect(url)) {

            // if url has not been visited, visit it
            if (crawlerDB.ifNotVisitedPut(url)) {

                // get list of links (read and parse page)
                links = internet.parse(url);

                // mark successful
                crawlerDB.putSuccess(url);

                // submit new thread for each link
                for (JsonElement newUrl : links) {
                    String newUrlStr = newUrl.getAsString();

                    // spawn a new thread if address was not already visited
                    if (crawlerDB.wasVisited(newUrlStr))
                        crawlerDB.putSkipped(newUrlStr);
                    else {
                        executor.execute(new CrawlerThread(newUrlStr, crawlerDB, internet, executor));
                    }
                }

            } else {
                // we already saw this page, skip it
                crawlerDB.putSkipped(url);
            }

        } else {

            // can't connect, add to ErrorList
            crawlerDB.putError(url);
        }
    }
}
