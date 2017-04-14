/**
 * Copyright (C) 2016 Open Whisper Systems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.thoughtcrime.securesms.scribbles;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.thoughtcrime.securesms.util.AsyncLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

class StickerLoader extends AsyncLoader<String[]> {

  private static final String TAG = StickerLoader.class.getName();

  private final ArrayList<String> assetsDirectories;

  StickerLoader(Context context, ArrayList<String> assetsDirectories) {
    super(context);
    this.assetsDirectories = assetsDirectories;
  }

  @Override
  public @NonNull
  String[] loadInBackground() {
    try {
      ArrayList<String> filesList = new ArrayList<>();
      for (String directory: assetsDirectories) {
        String[] files = getContext().getAssets().list(directory);
        for (int i=0;i<files.length;i++) {
          files[i] = directory + "/" + files[i];
        }
        Collections.addAll(filesList, files);
      }

      return filesList.toArray(new String[0]);
    } catch (IOException e) {
      Log.w(TAG, e);
    }

    return new String[0];
  }
}
