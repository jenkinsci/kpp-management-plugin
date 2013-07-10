package com.sic.plugins.kpp.provider;

import hudson.Extension;
import hudson.ExtensionList;
import java.io.File;

/**
 * The {@link KPPBaseKeychainsProvider} that stores and manages profisioning profiles.
 * @author mb
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
}
