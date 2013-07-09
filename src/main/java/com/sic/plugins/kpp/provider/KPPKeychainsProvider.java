package com.sic.plugins.kpp.provider;

import hudson.Extension;
import hudson.ExtensionList;

/**
* The {@link KPPBaseKeychainsProvider} that stores and manages keychains.
* @author mb
*/
@Extension
public class KPPKeychainsProvider extends KPPBaseKeychainsProvider {
    
    public static KPPKeychainsProvider getInstance() {
        ExtensionList<KPPBaseKeychainsProvider> list = KPPBaseKeychainsProvider.all();
        return list.get(KPPKeychainsProvider.class);
    }
    
}
