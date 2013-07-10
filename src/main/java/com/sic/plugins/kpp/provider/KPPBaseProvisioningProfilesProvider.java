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
import org.apache.commons.lang.StringUtils;

/**
 * An extension point for providing {@link KPPProvisioningProfile}
 * @author mb
 */
public class KPPBaseProvisioningProfilesProvider extends KPPBaseProvider implements ExtensionPoint {
    
    private List<KPPProvisioningProfile> provisioningProfiles = new ArrayList<KPPProvisioningProfile>();
    
    /**
     * Updates provisioning profiles information.
     */
    @Override
    public void update() {
        getProvisioningProfiles().clear();
        super.update();
    }

    /**
     * {@inherited}
     */
    @Override
    protected void merge() {
        List<KPPProvisioningProfile> ppsFromFolder = loadProvisioningProfilesFromUploadFolder();
        provisioningProfiles = mergedObjects(provisioningProfiles, ppsFromFolder);
    }
    
    private List<KPPProvisioningProfile> loadProvisioningProfilesFromUploadFolder() {
        List<KPPProvisioningProfile> pps = new ArrayList<KPPProvisioningProfile>();
        
        File[] ppsFiles = getFilesFromUploadDirectory(".mobileprovision");
        for(File ppFile : ppsFiles) {
            KPPProvisioningProfile pp = new KPPProvisioningProfile(ppFile.getName());
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
        return provisioningProfiles;
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
        
        provisioningProfiles = ppsNew;
    }
    
}