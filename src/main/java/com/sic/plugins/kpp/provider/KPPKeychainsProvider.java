package com.sic.plugins.kpp.provider;

import hudson.Extension;

/**
* The {@link KPPBaseKeychainsProvider} that stores and manages keychains.
*/
@Extension
public class KPPKeychainsProvider extends KPPBaseKeychainsProvider {
    
    public static KPPKeychainsProvider getInstance() {
        return KPPBaseKeychainsProvider.all().get(KPPKeychainsProvider.class);
    }
    
}
