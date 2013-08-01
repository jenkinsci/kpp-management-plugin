/*
 * The MIT License
 *
 * Copyright 2013 Michael Bär SIC! Software GmbH.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.sic.plugins.kpp.provider;

import static com.sic.plugins.kpp.provider.KPPBaseProvider.LOGGER;
import hudson.Extension;
import hudson.ExtensionList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

/**
 * The {@link KPPBaseKeychainsProvider} that stores and manages profisioning profiles.
 * @author Michael Bär
 */
@Extension
public class KPPProvisioningProfilesProvider extends KPPBaseProvisioningProfilesProvider {
    
    /**
     * Get the instance from extensionlist.
     * @return instance
     */
    public static KPPProvisioningProfilesProvider getInstance() {
        ExtensionList<KPPBaseProvisioningProfilesProvider> list = KPPBaseProvisioningProfilesProvider.all();
        KPPProvisioningProfilesProvider provider = list.get(KPPProvisioningProfilesProvider.class);
        return provider;
    }
    
    /**
     * Get the file for a given provisioning profile file name.
     * @param fileName
     * @return file
     */
    public File getProvisioningFile(String fileName) {
        String path = String.format("%s%s%s", getUploadDirectoryPath(), File.separator, fileName);
        File file = new File(path);
        return file;
    }
    
    /**
     * Parse the UUID from the content of the provisioning profile file.
     * @param fileName name of the provisioning profile file
     * @return UUID
     */
    public static String parseUUIDFromProvisioningProfileFile(String fileName) {
        String uuid = "UUID not found";
        BufferedReader br = null;
        try {
            String fileNameWithoutUUID = KPPBaseProvisioningProfilesProvider.removeUUIDFromFileName(fileName);
            File file = KPPProvisioningProfilesProvider.getInstance().getProvisioningFile(fileNameWithoutUUID);
            FileReader reader = new FileReader(file);
            br = new BufferedReader(reader);
            String line;
            boolean foundUUID = false;
            while (br!=null && (line = br.readLine()) != null) {
                if (line.contains("<key>UUID</key>")) {
                    foundUUID = true; // next line is value
                } else if (foundUUID) {
                    final String openTag = "<string>";
                    final String closeTag = "</string>";
                    // parse value for UUID key
                    int indexOfOpenTag = line.indexOf(openTag);
                    int indexOfCloseTag = line.indexOf(closeTag);
                    if (indexOfOpenTag != -1 && indexOfCloseTag != -1 && indexOfOpenTag < indexOfCloseTag) {
                        uuid = line.substring(indexOfOpenTag+openTag.length(), indexOfCloseTag);
                    }
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return uuid;
    }
}
