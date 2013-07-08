package com.sic.plugins.kpp.provider;

import com.sic.plugins.kpp.model.KPPKeychain;
import com.sic.plugins.kpp.model.KPPProvisioningProfile;
import hudson.ExtensionPoint;
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
     * Call this method to update provisioning profiles after save action.
     * This method updates provisioinig profiles information and removes provisioinig profiles from upload folder if they are deleted.
     * @param provisioningProfilesAfterSave 
     */
    public void updateProvisioningProfilesAfterSave(List<KPPProvisioningProfile>provisioningProfilesAfterSave) {
        List<KPPProvisioningProfile> ppsCurrent = new ArrayList<KPPProvisioningProfile>(getProvisioningProfiles());
        List<KPPProvisioningProfile> ppsNew = new ArrayList<KPPProvisioningProfile>(provisioningProfilesAfterSave.size());
        
        for (KPPProvisioningProfile ppA : provisioningProfilesAfterSave) {
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