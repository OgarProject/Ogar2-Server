/**
 * This file is part of Ogar.
 *
 * Ogar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ogar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Ogar.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ogarproject.ogar.server.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class JsonConfiguration {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T load(File file, Class<T> expectedClass) {
        try {
            if (!file.isFile()) {
                return expectedClass.newInstance();
            }

            return gson.fromJson(new FileReader(file), expectedClass);
        } catch (IOException | ReflectiveOperationException ex) {
            return null;
        }
    }

    public boolean save(File file) {
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(gson.toJson(this));
            fw.flush();

            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
