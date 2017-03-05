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

import hudson.XmlFile;
import hudson.model.Hudson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.fileupload.FileItem;

/**
 * Abstract base class for providers.
 * @author Michael Bär
 */
public abstract class KPPBaseProvider {
    
    final static Logger LOGGER = Logger.getLogger(KPPBaseProvider.class.getName());
    private final static String DEFAULT_UPLOAD_DIRECTORY_PATH = Hudson.getInstance().getRootDir() + File.separator + "kpp_upload";
    private final String defaultConfigXmlFileName;
    
    /**
     * Constructor
     */
    public KPPBaseProvider() {
        this.defaultConfigXmlFileName = String.format("%s.xml", this.getClass().getName());
        initialize();
    }
    
    private void initialize() {
        checkAndCreateUploadFolder();
        load();
        merge();
        try {
            save();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Failed to save file %s.", getConfigXmlFileName()), e);
        }
    }
    
    /**
     * Load provider config.
     */
    private void load() {
        try {
            XmlFile xml = getConfigXmlFile();
            if (xml.exists()) {
                xml.unmarshal(this);
            }
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, String.format("No %s file found.", getConfigXmlFileName()), e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Failed to read file %s.", getConfigXmlFileName()), e);
        }
    }
    
    /**
     * Check if upload folder exists. If folder not exists than create the folder.
     */
    protected void checkAndCreateUploadFolder() {
        File uploadFolder = new File((getUploadDirectoryPath()));
        if (!uploadFolder.exists()) {
            uploadFolder.mkdir();
        }
    }
    
    /**
     * Get the default Upload Directory Path for Keychains and Provisioning Profiles files.
     * @return path
     */
    public String getUploadDirectoryPath() {
        return DEFAULT_UPLOAD_DIRECTORY_PATH;
    }
    
    /**
     * Store uploaded file inside upload directory.
     * @param fileItemToUpload the file object
     * @throws FileNotFoundException if the file isn't found
     * @throws IOException if the file can't be opened
     */
    public void upload(FileItem fileItemToUpload) throws FileNotFoundException, IOException {
        // save uploaded file
        byte[] fileData = fileItemToUpload.get();
        File toUploadFile = new File(getUploadDirectoryPath(), fileItemToUpload.getName());
        OutputStream os = new FileOutputStream(toUploadFile);
        os.write(fileData);
    }
    
    /**
     * Get the provider config file.
     * @return xmlfile
     */
    public XmlFile getConfigXmlFile() {
        return new XmlFile(new File(Hudson.getInstance().getRootDir(), getConfigXmlFileName()));
    }
    
    /**
     * Get provider config xml filename.
     * @return filename
     */
    public String getConfigXmlFileName() {
        return defaultConfigXmlFileName;
    }
    
    /**
     * Save provider config xml.
     * @throws IOException if the file can't be opened
     */
    public final void save() throws IOException {
        try {
            getConfigXmlFile().write(this);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, String.format("Could not save %s.", getConfigXmlFileName()), ex);
        }
    }
    
    /**
     * Update provider from config and content of upload folder.
     * If you override this method, call super after your implementation.
     */
    public void update() {
        load();
        merge();
    }
    
    /**
     * Get all files filtered by filetype from upload directory.
     * @param fileExtension, e.g. ".keychain"
     * @return array with all files
     */
    public File[] getFilesFromUploadDirectory(String fileExtension) {
        return new File(getUploadDirectoryPath()).listFiles(new KPPBaseProvider.FileExtensionFilenameFilter(fileExtension));
    }
    
    /**
     * Merge two lists of objects.
     * @param <T> type
     * @param objectsFromXml objects loaded from xml
     * @param objectsFromFolder objects loaded from upload folder
     * @return merged objects
     */
    protected <T> List<T> mergedObjects(List<T>objectsFromXml, List<T>objectsFromFolder) {
        List<T> objects = new ArrayList<T>();
        
        if (objectsFromFolder==null || objectsFromXml==null) {
            if (objectsFromFolder==null) {
                objects.addAll(objectsFromXml);
            } else if (objectsFromXml==null) {
                objects.addAll(objectsFromFolder);
            }
            return objects;
        }
        
        List<T>objectsFolder = new ArrayList<T>(objectsFromFolder);
        for (T oXml : objectsFromXml) {
            for (T oFolder : objectsFromFolder) {
                if (oXml.equals(oFolder)) {
                    objects.add(oXml);
                    objectsFolder.remove(oFolder);
                    break;
                }
            }
        }
        
        if(!objectsFolder.isEmpty()) {
            objects.addAll(objectsFolder);
        }
        
        return objects;
    }
    
    /**
     * Filename filter to get only files with a special extension.
     */
    private class FileExtensionFilenameFilter implements FilenameFilter {
        
        private final String fileExtension;
        
        public FileExtensionFilenameFilter(String fileExtension) {
            this.fileExtension = fileExtension;
        }
        
        public boolean accept(File file, String name) {
            boolean ret = false;
            if (name.endsWith(this.fileExtension)) {
                ret = true;
            }
            return ret;
        }
    }
    
    /**
     * Merge from file.
     */
    protected abstract void merge();
}
