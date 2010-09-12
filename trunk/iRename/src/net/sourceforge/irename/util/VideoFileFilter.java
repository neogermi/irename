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

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

public class VideoFileFilter implements FileFilter {

  private LinkedList<String> endings;

  /**
   * Needs a ';' separated list of fileendings.
   * @param fileEndings
   */
  public VideoFileFilter(List<String> fileEndings) {
    endings = new LinkedList<String>();
    for (String e : fileEndings)
      endings.add(e);
  }

  public void addFileEnding(String ending) {
    String tmp = ending.toLowerCase().trim();
    if (!endings.contains(tmp))
      endings.add(tmp);
  }

  public void removeFileEnding(String ending) {
    String tmp = ending.toLowerCase().trim();
    if (endings.contains(tmp))
      endings.remove(tmp);
  }

  public boolean accept(File f) {
    String name = f.getName();
    if (f.isDirectory())
      return false;
    else {
      for (String e : endings)
        if (name.toLowerCase().trim().endsWith(e))
          return true;
    }
    return false;
  }

  public String getDescription() {
    return "Filter for most common video files!";
  }

}
