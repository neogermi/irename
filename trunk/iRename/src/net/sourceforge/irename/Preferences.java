package net.sourceforge.irename;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import net.sourceforge.irename.util.Pattern;

public class Preferences {

    public static int             width                          = 900;
    public static int             height                         = 450;

    private static String         filename                       = "preferences.ini";
    public static String          lastOpenedDirectory;
    public static String          replacementRule;
    public static List<String>    validFileEndings;
    public static List<Pattern>   patterns;
    public static int[]           columnWidth                    = new int[] {
            220, 100, 10, 10, 100, 220, 20
                                                                 };
    public static boolean         retrieveOnlineData             = true;

    public static String          episodeNameDatabasePagePattern = "http://services.tvrage.com/feeds/episodeinfo.php?key=nQpHEaNBqs74sXpFNHQR&show=%show%&ep=%season%x%episode%";

    private static ResourceBundle myResourceBundle;

    static {
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line = "";
            while ((line = in.readLine()) != null) {
                String[] split = line.split("=");
                if (split[0].equals("WIDTH")) {
                    width = Integer.parseInt(split[1]);
                }
                else if (split[0].equals("HEIGHT")) {
                    height = Integer.parseInt(split[1]);
                }
                else if (split[0].equals("RETRIEVEONLINEDATA")) {
                    retrieveOnlineData = Boolean.parseBoolean(split[1]);
                }
                else if (split[0].equals("REPLACEMENTRULE")) {
                    replacementRule = split[1];
                }
                else if (split[0].equals("LASTOPENEDDIRECTORY")) {
                    lastOpenedDirectory = split[1];
                }
                else if (split[0].equals("LOCALE")) {
                    myResourceBundle = ResourceBundle.getBundle("i18n.Localization", new Locale(
                            "en", "US"));
                }
                else if (split[0].equals("ENDINGS")) {
                    validFileEndings = new LinkedList<String>();
                    for (String s : split[1].split(";"))
                        validFileEndings.add(s);
                }
                else if (split[0].equals("PATTERNS")) {
                    patterns = new LinkedList<Pattern>();
                    for (String s : split[1].split(";")) {
                        Pattern p = Pattern.parsePattern(s);
                        if (p != null)
                            patterns.add(p);
                    }
                }
            }
            in.close();
        }
        catch (Exception e) {
            //TODO: e.printStackTrace();
            myResourceBundle = ResourceBundle
                    .getBundle("i18n.Localization", new Locale("en", "US"));
            validFileEndings = new LinkedList<String>();
            for (String s : "avi;mpeg;flv;mpg;3gp;divx;wmv;mkv".split(";"))
                validFileEndings.add(s);

            patterns = new LinkedList<Pattern>();
            patterns.add(new Pattern("(.+).*?[Ss]([0-9]{1,2}).*?[Ee]([0-9]{1,2}).*", "$1", "$2",
                    "$3"));
            patterns.add(new Pattern("(.+).*?([0-9]{1,2})x([0-9]{1,2}).*", "$1", "$2", "$3"));
            patterns.add(new Pattern("(.+).*?([0-9]{1})([0-9]{2}).*", "$1", "$2", "$3"));
            patterns.add(new Pattern("(.+).*?()([0-9]{2}).*", "$1", "$2", "$3"));

            replacementRule = "%show%-S%Season%E%Episode%-%name%";

            lastOpenedDirectory = System.getProperty("user.home");
        }
    }

    public static Object getLocalizationValue(String key) {
        return myResourceBundle.getObject(key);
    }

    public static String getLocalizationString(String key) {
        return myResourceBundle.getString(key);
    }

    public static void writeChanges() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write("WIDTH=" + width);
            out.newLine();
            out.write("HEIGHT=" + height);
            out.newLine();
            out.write("REPLACEMENTRULE=" + replacementRule);
            out.newLine();
            out.write("RETRIEVEONLINEDATA=" + retrieveOnlineData);
            out.newLine();
            out.write("LASTOPENEDDIRECTORY=" + lastOpenedDirectory);
            out.newLine();
            out.write("ENDINGS=");
            boolean first = true;
            for (String s : validFileEndings) {
                out.write(((first) ? "" : ";") + s);
                first = false;
            }
            out.newLine();
            out.write("PATTERNS=");
            first = true;
            for (Pattern s : patterns) {
                out.write(((first) ? "" : ";") + s.toString());
                first = false;
            }
            out.newLine();
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
