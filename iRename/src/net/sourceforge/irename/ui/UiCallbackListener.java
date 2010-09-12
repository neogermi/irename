package net.sourceforge.irename.ui;

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

import net.sourceforge.irename.util.FileElement;

public interface UiCallbackListener {

    enum Type {
        TvShowName, SeasonNumber, EpisodeNumber, EpisodeName
    };

    void openNewDirectory(File d);

    void dataChanged(Type type, FileElement fe);

    void renameFile(FileElement fe);

    void undoRename(FileElement fe);

    void addValidFileEnding(String newFileEnding);

    void removeValidFileEnding(String newFileEnding);

    void quitApplication();
}
