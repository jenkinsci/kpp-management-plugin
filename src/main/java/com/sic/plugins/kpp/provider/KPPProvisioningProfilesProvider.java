package com.sic.plugins.kpp.provider;

import hudson.Extension;
import hudson.ExtensionList;

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
        return list.get(KPPProvisioningProfilesProvider.class);
    }
    
    
}
