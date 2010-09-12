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

public class Pattern {

  private String pattern;
  private String toShow;
  private String toSeason;
  private String toEpisode;

  public Pattern(String pattern, String toShow, String toSeason, String toEpisode) {
    this.pattern = pattern;
    this.toShow = toShow;
    this.toSeason = toSeason;
    this.toEpisode = toEpisode;
  }

  public String getPattern() {
    return pattern;
  }

  public String getToShow() {
    return toShow;
  }

  public String getToSeason() {
    return toSeason;
  }

  public String getToEpisode() {
    return toEpisode;
  }

  public static Pattern parsePattern(String s) {
    if (s.matches("<pattern><p>.+?</p><toShow>.+?</toShow><toSeason>.+?</toSeason><toEpisode>.+?</toEpisode></pattern>")) {
      String f = s.replaceAll("<pattern><p>(.+?)</p><toShow>(.+?)</toShow><toSeason>(.+?)</toSeason><toEpisode>(.+?)</toEpisode></pattern>", "$1");
      String tShow = s.replaceAll("<pattern><p>(.+?)</p><toShow>(.+?)</toShow><toSeason>(.+?)</toSeason><toEpisode>(.+?)</toEpisode></pattern>", "$2");
      String tSeason = s.replaceAll("<pattern><p>(.+?)</p><toShow>(.+?)</toShow><toSeason>(.+?)</toSeason><toEpisode>(.+?)</toEpisode></pattern>", "$2");
      String tEpisode = s.replaceAll("<pattern><p>(.+?)</p><toShow>(.+?)</toShow><toSeason>(.+?)</toSeason><toEpisode>(.+?)</toEpisode></pattern>", "$2");

      return new Pattern(f, tShow, tSeason, tEpisode);
    } else {
      //ignore
      return null;
    }
  }

  @Override
  public String toString() {
    String ret = "<pattern><p>" + pattern + "</p>";
    ret += "<toShow>" + toShow + "</toShow>";
    ret += "<toSeason>" + toSeason + "</toSeason>";
    ret += "<toEpisode>" + toEpisode + "</toEpisode>";
    ret += "</pattern>";
    return ret;
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Pattern) {
      Pattern p = (Pattern) o;
      return (p.toString().equals(toString()));
    }
    return false;
  }

}
