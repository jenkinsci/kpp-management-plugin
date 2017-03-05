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
import com.sic.plugins.kpp.model.KPPProvisioningProfile;
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
 * An extension point for providing {@link KPPProvisioningProfile}
 * @author Michael Bär
 */
public abstract class KPPBaseProvisioningProfilesProvider extends KPPBaseProvider implements ExtensionPoint {
    
    private static String FILE_EXTENSION = ".mobileprovision";
    private List<KPPProvisioningProfile> provisioningProfiles;
    private String provisioningProfilesPath;
    
    @Override
    public void update() {
        getProvisioningProfiles().clear();
        super.update();
    }

    @Override
    protected void merge() {
        List<KPPProvisioningProfile> ppsFromFolder = loadProvisioningProfilesFromUploadFolder();
        setProvisioningProfiles(mergedObjects(provisioningProfiles, ppsFromFolder));
    }
    
    private List<KPPProvisioningProfile> loadProvisioningProfilesFromUploadFolder() {
        List<KPPProvisioningProfile> pps = new ArrayList<KPPProvisioningProfile>();
        
        File[] ppsFiles = getFilesFromUploadDirectory(FILE_EXTENSION);
        for(File ppFile : ppsFiles) {
            KPPProvisioningProfile pp = new KPPProvisioningProfile(ppFile.getName(), null);
            if(StringUtils.isBlank(pp.getFileName())) {
                break;
            }
            pps.add(pp);
        }
        return pps;
    }
    
    /**
     * Get a list with all provisioning profiles.
     * @return list
     */
    public List<KPPProvisioningProfile> getProvisioningProfiles() {
        return this.provisioningProfiles;
    }
    
    private void setProvisioningProfiles(List<KPPProvisioningProfile> provisioningProfiles) {
        this.provisioningProfiles = provisioningProfiles;
    }
    
    /**
     * Get the path to the directory to store provisioning profiles on the master or standalone jenkins instance.
     * @return path
     */
    public String getProvisioningProfilesPath() {
        return provisioningProfilesPath;
    }
    
    /**
     * Set the path to the directory to store provisioning profiles on the master or standalone jenkins instance.
     * @param provisioningProfilesPath the path to store
     */
    public void setProvisioningProfilesPath(String provisioningProfilesPath) {
        this.provisioningProfilesPath = provisioningProfilesPath;
    }
    
    /**
     * Get all the registered {@link KPPKeychain} descriptors.
     *
     * @return all the registered {@link KPPKeychain} descriptors.
     */
    public static DescriptorExtensionList<KPPProvisioningProfile, Descriptor<KPPProvisioningProfile>> allProvisioningProfileDescriptors() {
        return Hudson.getInstance().getDescriptorList(KPPProvisioningProfile.class);
    }
    
    /**
     * @return All registered {@link KPPBaseProvisioningProfilesProvider}s.
     */
    public static ExtensionList<KPPBaseProvisioningProfilesProvider> all() {
        return Hudson.getInstance().getExtensionList(KPPBaseProvisioningProfilesProvider.class);
    }
    
    /**
     * Call this method to update provisioning profiles from save action. The provisioning profiles from save action are merged into current provisioning profiles list. 
     * Then this list is sychnronized with the upload folder.
     * @param provisioningProfilesFromSave the list of profiles
     */
    public void updateProvisioningProfilesFromSave(List<KPPProvisioningProfile> provisioningProfilesFromSave) {
        List<KPPProvisioningProfile> ppsCurrent = new ArrayList<KPPProvisioningProfile>(getProvisioningProfiles());
        List<KPPProvisioningProfile> ppsNew = new ArrayList<KPPProvisioningProfile>(provisioningProfilesFromSave.size());
        
        for (KPPProvisioningProfile ppA : provisioningProfilesFromSave) {
            for (KPPProvisioningProfile ppC : ppsCurrent) {
                if (ppC.equals(ppA)) {
                    ppsNew.add(ppA);
                    ppsCurrent.remove(ppC);
                    break;
                }
            }
        }
        
        if (!ppsCurrent.isEmpty()) {
            // delete provisioning profile from filesystem
            final String folderPath = getUploadDirectoryPath();
            File ppFile;
            for (KPPProvisioningProfile pp : ppsCurrent) {
                ppFile = new File(folderPath + File.separator +pp.getFileName());
                ppFile.delete();
            }
        }
        
        setProvisioningProfiles(ppsNew);
    }
    
    
    /**
     * Checks if a given file item is a mobile provision profile file.
     * @param item the profile to check
     * @return true, if it is a mobile provision profile file.
     */
    public boolean isMobileProvisionProfileFile(FileItem item) {
        return item.getName().endsWith(FILE_EXTENSION);
    }
    
    /**
     * If the fileName contains the uuid at the end, so remove the uuid part.
     * @param fileName the profile
     * @return fileName without uuid
     */
    public static String removeUUIDFromFileName(String fileName) {
        String ret = fileName;
        if (!fileName.endsWith(FILE_EXTENSION)) {
            int index = fileName.indexOf(FILE_EXTENSION) + FILE_EXTENSION.length();
            ret = fileName.substring(0, index);
        }
        return ret;
    }
    
    /**
     * Get the filename of the provisioning profile in the shape of uuid.mobileprovision.
     * @param uuid the UUID
     * @return filename
     */
    public static String getUUIDFileName(String uuid) {
        return String.format("%s%s", uuid, FILE_EXTENSION);
    }
    
}