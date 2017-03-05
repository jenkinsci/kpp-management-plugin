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

import com.sic.plugins.kpp.model.KPPKeychain;
import hudson.DescriptorExtensionList;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

/**
 * An extension point for providing {@link KPPKeychain}.
 * @author Michael Bär
 */
public abstract class KPPBaseKeychainsProvider extends KPPBaseProvider implements ExtensionPoint {
    
    private static String FILE_EXTENSION = ".keychain";
    private List<KPPKeychain> keychains;
    
    @Override
    protected void merge() {
        List<KPPKeychain> keychainsFromFolder = loadKeychainsFromUploadFolder();
        keychains = mergedObjects(keychains, keychainsFromFolder);
    }
    
    @Override
    public void update() {
        getKeychains().clear();
        super.update();
    }
    
    /**
     * Load the keychains from the upload directory.
     * @return list of keychains
     */
    private List<KPPKeychain> loadKeychainsFromUploadFolder() {
        List<KPPKeychain> ks = new ArrayList<KPPKeychain>();
        
        File[] keychainFiles = getFilesFromUploadDirectory(FILE_EXTENSION);
        for(File keychainFile : keychainFiles) {
            KPPKeychain keychain = new KPPKeychain(keychainFile.getName());
            if(StringUtils.isBlank(keychain.getFileName())) {
            break;
            }
            ks.add(keychain);
        }
        return ks;
    }
    
    /**
     * Get a list with all keychains.
     * 
     * @return all keychains.
     */
    public List<KPPKeychain> getKeychains() {
        return keychains;
    }

    /**
     * Get all the registered {@link KPPKeychain} descriptors.
     *
     * @return all the registered {@link KPPKeychain} descriptors.
     */
    public static DescriptorExtensionList<KPPKeychain, Descriptor<KPPKeychain>> allKeychainDescriptors() {
        return Hudson.getInstance().getDescriptorList(KPPKeychain.class);
    }
    
    /**
     * @return All registered {@link KPPBaseKeychainsProvider}s.
     */
    public static ExtensionList<KPPBaseKeychainsProvider> all() {
        return Hudson.getInstance().getExtensionList(KPPBaseKeychainsProvider.class);
    }
    
    /**
     * Call this method to update keychains from save action. The keychains from save action are merged into current keychains list. 
     * Then this list is sychnronized with the upload folder.
     * @param keychainsFromSave the list of keychains
     */
    public void updateKeychainsFromSave(List<KPPKeychain> keychainsFromSave) {
        List<KPPKeychain> ksCurrent = new ArrayList<KPPKeychain>(getKeychains());
        List<KPPKeychain> ksNew = new ArrayList<KPPKeychain>(keychainsFromSave.size());
        
        for (KPPKeychain kS : keychainsFromSave) {
            for (KPPKeychain kC : ksCurrent) {
                if (kC.equals(kS)) {
                    ksNew.add(kS);
                    ksCurrent.remove(kC);
                    break;
                }
            }
        }
        
        if (!ksCurrent.isEmpty()) {
            // delete keychains from filesystem
            final String ksFolderPath = getUploadDirectoryPath();
            File kFile;
            for (KPPKeychain k : ksCurrent) {
                kFile = new File(ksFolderPath + File.separator +k.getFileName());
                kFile.delete();
            }
        }
        
        keychains = ksNew;
    }
    
    /**
     * Checks if a given file item is a keychain file.
     * @param item the file to check
     * @return true, if it is a keychain file.
     */
    public boolean isKeychainFile(FileItem item) {
        return item.getName().endsWith(FILE_EXTENSION);
    }
    
}
