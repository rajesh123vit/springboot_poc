package com.ge.crawler.data;

import java.util.Enumeration;
import java.util.Hashtable;

import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;

/**
 * Shared data used by the crawler. Used to track and save results.
 *
 * Uses four Hashtable objects: 
 *     VisitedList, SuccessList, SkipList, ErrorList. 
 * Hashtable is already thread safe, search speed is constant 
 * even for large tables.
 *
 * Some of the methods have additional synchronization.
 * 
 * @author Rajesh Ranjan
 * 
 * 04/27/2019
 * 
 */


public class CrawlerData {

    private Hashtable<String, Boolean> visitedList;

    private Hashtable<String, Boolean> successList;
    private Hashtable<String, Boolean> errorList;
    private Hashtable<String, Boolean> skippedList;

    /**
     * Creates a data store for the webcrawler
     * 
     * visitedList - urls that have have been checked already. 
     * successList - urls that were successfully parsed.
     * errorList   - urks that were not reachable. 
     * skippedList - urls that were encountered but already checked.
     * 
     * The Boolean values are not currently used.
     */
    public CrawlerData() {
        this.visitedList = new Hashtable<String, Boolean>();
        this.successList = new Hashtable<String, Boolean>();
        this.errorList   = new Hashtable<String, Boolean>();
        this.skippedList = new Hashtable<String, Boolean>();
    }

    /**
     * Prints the data to System.out
     */
    public void print() {

        System.out.println("Success:");
        System.out.println(createJsonArray(successList));
        System.out.println("Skipped:");
        System.out.println(createJsonArray(skippedList));
        System.out.println("Error:");
        System.out.println(createJsonArray(errorList));
    }

    /**
     * 
     * Creates a JsonArray from one of the hashtables
     * 
     * @params list one of this object's hashtables
     * @return a JsonArray representation of the hashtable
     * 
     */
    private JsonArray createJsonArray(Hashtable<String, Boolean> list) {

        JsonArray jsArr = new JsonArray();

        for (Enumeration<String> e = list.keys(); e.hasMoreElements();)
            jsArr.add(e.nextElement());

        return jsArr;
    }

    /**
     * Adds a url to the visited list if it is not already in it.
     * 
     * Solves a synchronization problem by checking the list and adding to it in one
     * synchronized method
     * 
     * @param url the address to attempt to add
     * @return true if new url is added, false if the url is already visited
     * @throws NullPointerException if url is null
     */
    public synchronized boolean ifNotVisitedPut(String url) {

        // was initially planning to utilize the true/false in hashtables,
        // but get<Key> returns null if key doesn't already exist.
        // in future can implement this object's own synchronized contains and put

        if (visitedList.containsKey(url)) {
            return false;
        }

        visitedList.put(url, true);
        return true;
    }

    /**
     * Checks if address was already checked
     * 
     * @param url the address to check
     * @return true if the address has already been seen, false if not
     * @throws NullPointerException if url is null
     */
    public synchronized boolean wasVisited(String url) {
        return visitedList.containsKey(url);
    }

    /**
     * Puts a url in the success hashtable
     * 
     * @param url the address to add
     * @throws NullPointerException if url is null
     */
    public void putSuccess(String url) {
        this.successList.put(url, true);
    }

    /**
     * Puts a url in the error hashtable
     * 
     * @param url the address to add
     * @throws NullPointerException if url is null
     */
    public void putError(String url) {
        this.errorList.put(url, true);
    }

    /**
     * Puts a url in the skipped hashtable
     *
     * @param url the address to add
     * @throws NullPointerException if url is null
     */
    public void putSkipped(String url) {
        this.skippedList.put(url, true);

    }
}
