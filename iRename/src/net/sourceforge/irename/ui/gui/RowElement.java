package net.sourceforge.irename.ui.gui;

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

import net.sourceforge.irename.util.FileElement;

public class RowElement {

    public String  filenameBefore;

    public String  tvShowName;
    public String  seasonNumber;
    public String  episodeNumber;
    public String  episodeName;
    public boolean isLoading;

    public String  filenameAfter;

    public boolean performChanges;

    public RowElement(FileElement fe) {
        filenameBefore = fe.getSourceFile().getName();
        tvShowName = fe.getTvShowName();
        seasonNumber = fe.getSeasonNumber();
        episodeNumber = fe.getEpisodeNumber();
        episodeName = fe.getEpisodeName();
        isLoading = false;
        filenameAfter = fe.getTargetFilename();
    }

    public void update(FileElement fe) {
        tvShowName = fe.getTvShowName();
        seasonNumber = fe.getSeasonNumber();
        episodeNumber = fe.getEpisodeNumber();
        episodeName = fe.getEpisodeName();
        filenameAfter = fe.getTargetFilename();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof RowElement) {
            RowElement re = (RowElement) o;
            return re.filenameBefore.equals(filenameBefore);
        }
        return false;
    }

}
