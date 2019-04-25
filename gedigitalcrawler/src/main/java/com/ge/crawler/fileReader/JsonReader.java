package com.ge.crawler.fileReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.*;

/**
 * Class to load and use an "Internet" test case 
 * The internal representation is JSON Array.
 * 
 * @author Rajesh Ranjan
 * 
 * 04/27/2019
 */
public class JsonReader {
	/**
     * The internal JsonArray of a test "Internet"
     */
    private JsonArray internet;

    /**
     * Creates an Internet from the specified json file
     * 
     * @param filename the json file
     */
    public JsonReader(String filename) {

        JsonParser parser = new JsonParser();
        try (FileReader myReader = new FileReader(filename)) {

            JsonObject jsonObj = new JsonObject();
            jsonObj = (JsonObject) parser.parse(myReader);

            this.internet = jsonObj.getAsJsonArray("pages");

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File not found " + e);
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("I/O ERROR: " + e);
            System.exit(-1);
        } catch (JsonParseException e) {
            System.out.println("ERROR: Unable to parse " + e);
            System.exit(-1);
        }
    }

    /**
     * Creates a String representation of this Internet
     * 
     * @return a pretty formatted string of this Internet
     **/
    @Override
    public String toString() {

        // PrettyPrint the "Internet"
        Gson   gson       = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(internet);
        return jsonOutput.toString();

    }

    /**
     * Creates a complete clone/deep copy of the internet JSON 
     * This is to make using the internet member more readable. 
     * Instead of using obj.internet.method(), can just  use internet.method();
     * 
     * @return a copy of the
     */
    public JsonArray toJsonArray() {

        JsonArray internetCopy = internet.deepCopy();
        return internetCopy;
    }

    /**
     * Checks if address exists in our Internet
     * 
     * @param address the address to look for
     * @return true if address is a page in our Internet
     */
    public boolean connect(String address) {

        // Thread.wait(100);
        for (JsonElement page : internet) {

            if (((JsonObject) page).get("address").getAsString().equals(address)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves an array of all of the links on a page. 
     * Could be redesigned to return ArrayList
     * 
     * @param address the page address
     * @return an array of links
     */
    public JsonArray parse(String address) {

        JsonArray jsonLinks = new JsonArray();

        if (connect(address)) {
            for (JsonElement page : internet) {
                if (((JsonObject) page).get("address").getAsString().equals(address)) {

                    // Get list of links
                    jsonLinks = ((JsonObject) page).getAsJsonArray("links");
                    break;
                }
            }
        }
        return jsonLinks;
    }

    /**
     * Gets the first address from our test "Internet"
     * 
     * @return the first address
     */
    public String getFirstAddress() {

        String firstAddress = ((JsonObject) internet.get(0)).get("address").getAsString();
        return firstAddress;
    }
}
