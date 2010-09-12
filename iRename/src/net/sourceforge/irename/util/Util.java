package net.sourceforge.irename.util;

/*
 * Copyright (C) 2010 Sebastian Germesin
 * 
 *  This file is part of iRename.
 *
 *  iRename is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  iRename is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with iRename (see .../iRename/LICENSE.txt)
 *  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.sourceforge.irename.Preferences;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class Util {

    /**
     * Trys to extract the show's name from the filename.
     * Returns <b>""</b> if this is not successful.
     * @param filename
     * @return
     */
    public static String guessTvShowName(FileElement fe) {
        String filename = fe.getSourceFile().getName();
        String name = filename;
        int i = 0;
        for (Pattern p : Preferences.patterns) {
            String replaced = name.replaceAll(p.getPattern(), p.getToShow());
            if (!name.equals(replaced)) {
                name = replaced;
                break;
            }
            i++;
        }

        //clean up
        name = name.replaceAll("[^a-zA-Z0-9]", " ");
        name = name.replaceAll("[ ]+", " ");
        name = name.trim();
        if (name.equals(filename) || name.equals("")) {
            return "";
        }
        else
            return name;
    }

    /**
     * Trys to extract the season's number from the filename.
     * Returns <b>""</b> if this is not successful.
     * @param filename
     * @return
     */
    public static String guessSeasonNumber(FileElement fe) {
        String filename = fe.getSourceFile().getName();
        String name = filename;
        int i = 0;
        for (Pattern p : Preferences.patterns) {
            String replaced = name.replaceAll(p.getPattern(), p.getToSeason());
            if (!name.equals(replaced)) {
                name = replaced;
                break;
            }
            i++;
        }
        name = name.trim();
        name = name.replaceAll("^0+(.+)$", "$1"); //removes leading 0's
        if (name.equals(filename) || name.equals("")) {
            return "";
        }
        else
            return name;
    }

    /**
     * Trys to extract the episode's number from the filename.
     * Returns <b>""</b> if this is not successful.
     * @param filename
     * @return
     */
    public static String guessEpisodeNumber(FileElement fe) {
        String filename = fe.getSourceFile().getName();
        String name = filename;
        int i = 0;
        for (Pattern p : Preferences.patterns) {
            String replaced = name.replaceAll(p.getPattern(), p.getToEpisode());
            if (!name.equals(replaced)) {
                name = replaced;
                break;
            }
            i++;
        }
        name = name.trim();
        name = name.replaceAll("^0+(.+)$", "$1"); //removes leading 0's
        if (name.equals(filename) || name.equals("")) {
            return "";
        }
        else
            return name;
    }

    public static String applyPattern(FileElement fe) {
        String result = Preferences.replacementRule;
        result = result.replaceAll("%show%", fe.getTvShowName());
        if (result.contains("%season%")) {
            result = result.replaceAll("%season%", fe.getSeasonNumber());
        }
        else if (result.contains("%Season%")) {
            int seasonNumber = -1;
            try {
                seasonNumber = Integer.parseInt(fe.getSeasonNumber());
            }
            catch (Exception e) {
                seasonNumber = Integer.MAX_VALUE;
            }
            if (seasonNumber > 9)
                result = result.replaceAll("%Season%", fe.getSeasonNumber());
            else
                result = result.replaceAll("%Season%", "0" + fe.getSeasonNumber());
        }
        if (result.contains("%episode%")) {
            result = result.replaceAll("%episode%", fe.getEpisodeNumber());
        }
        else if (result.contains("%Episode%")) {
            int episodeNumber = -1;
            try {
                episodeNumber = Integer.parseInt(fe.getEpisodeNumber());
            }
            catch (Exception e) {
                episodeNumber = Integer.MAX_VALUE;
            }
            if (episodeNumber > 10)
                result = result.replaceAll("%Episode%", fe.getEpisodeNumber());
            else
                result = result.replaceAll("%Episode%", "0" + fe.getEpisodeNumber());
        }
        result = result.replaceAll("%name%", fe.getEpisodeName());
        result = result.trim();

        return result + "." + extractFileending(fe);
    }

    /* public static String retrieveTvShowName(FileElement fe) {
         System.out.println("retrieveTvShowName(" + fe.getTvShowName() + ")");
         String urlString = Preferences.episodeNameDatabasePagePattern;
         urlString = urlString.replaceFirst("%show%", fe.getTvShowName()).replaceAll(" ", "");
         urlString = urlString.replaceFirst("%season%", "");
         urlString = urlString.replaceFirst("%episode%", "");

         try {
             String episodeName = "";
             HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();

             BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

             String line = "";
             while ((line = in.readLine()) != null) {
                 if (line.matches("^Show Name.+")) {
                     episodeName = line.replaceFirst(".+?@(.+?)", "$1");
                 }
             }
             in.close();

             return episodeName;
         }
         catch (MalformedURLException e) {
             //TODO e.printStackTrace();
         }
         catch (IOException e) {
             //TODO e.printStackTrace();
         }
         return fe.getTvShowName();
     }*/

    public static String retrieveEpisodeName(FileElement fe) {
        System.out.println("retrieveEpisodeName(" + fe.getTvShowName() + ")");
        String urlString = Preferences.episodeNameDatabasePagePattern;
        urlString = urlString.replaceFirst("%show%", fe.getTvShowName()).replaceAll(" ", "%20");
        urlString = urlString.replaceFirst("%season%", fe.getSeasonNumber());
        urlString = urlString.replaceFirst("%episode%", fe.getEpisodeNumber());

        String responseBody = "";

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            XPath xpath = XPathFactory.newInstance().newXPath();

            HttpGet httpget = new HttpGet(urlString);

            HttpClient httpclient = new DefaultHttpClient();

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            responseBody = httpclient.execute(httpget, responseHandler);
            httpclient.getConnectionManager().shutdown();

            Document doc = dbf.newDocumentBuilder().parse(
                    new InputSource(new StringReader(responseBody)));

            Element titleElement = (Element) xpath.evaluate("./show/episode/title", doc,
                    XPathConstants.NODE);

            String title = titleElement.getTextContent().trim();

            return title;
        }
        catch (NullPointerException e) {
        }
        catch (Throwable t) {
            t.printStackTrace();
            System.err.println("'" + responseBody + "'");
        }
        return fe.getEpisodeName();
    }

    public static String extractFileending(FileElement fe) {
        return fe.getSourceFile().getName().replaceAll(".*?\\.([^\\.]+)$", "$1");
    }

}
