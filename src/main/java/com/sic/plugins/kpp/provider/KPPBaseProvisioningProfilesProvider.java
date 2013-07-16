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
 * @author mb
 */
public abstract class KPPBaseProvisioningProfilesProvider extends KPPBaseProvider implements ExtensionPoint {
    
    private static String FILE_EXTENSION = ".mobileprovision";
    private List<KPPProvisioningProfile> provisioningProfiles;
    private String provisioningProfilesPath;
    
    /**
     * Updates provisioning profiles information.
     */
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
     * Get the path to provisioning profiles on the master or standalone jenkins instance.
     * @return path
     */
    public String getProvisioningProfilesPath() {
        return provisioningProfilesPath;
    }
    
    /**
     * Set the path to store provisioning profiles on the master or standalone jenkins instance.
     * @param provisioningProfilesPath 
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
     * All regsitered {@link KPPBaseProvisioningProfilesProvider}s.
     */
    public static ExtensionList<KPPBaseProvisioningProfilesProvider> all() {
        return Hudson.getInstance().getExtensionList(KPPBaseProvisioningProfilesProvider.class);
    }
    
    /**
     * Call this method to update provisioning profiles from save action. The provisioning profiles from save action are merged into current provisioning profiles list. 
     * Then this list is sychnronized with the upload folder.
     * @param provisioningProfilesAfterSave 
     */
    public void updateProvisioningProfilesFromSave(List<KPPProvisioningProfile>provisioningProfilesFromSave) {
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
     * @param item
     * @return true, if it is a mobile provision profile file.
     */
    public boolean isMobileProvisionProfileFile(FileItem item) {
        return item.getName().endsWith(FILE_EXTENSION);
    }
    
}