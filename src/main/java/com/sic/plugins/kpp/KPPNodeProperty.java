package com.sic.plugins.kpp;

import hudson.Extension;
import hudson.model.Computer;
import hudson.model.Hudson;
import hudson.model.Node;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 *  Configure Slave Node Properties
 */
public class KPPNodeProperty extends NodeProperty<Node>{
    private String provisioningProfilesPath;
    
    @DataBoundConstructor
    public KPPNodeProperty(String provisioningProfilesPath) {
        this.provisioningProfilesPath = provisioningProfilesPath;
    }
    
    public String getProvisioningProfilesPath() {
        return provisioningProfilesPath;
    }
    
    public static KPPNodeProperty getCurrentNodeProperties() {
        KPPNodeProperty property = Computer.currentComputer().getNode().getNodeProperties().get(KPPNodeProperty.class);
        if(property == null) {
            property = Hudson.getInstance().getGlobalNodeProperties().get(KPPNodeProperty.class);
        }
        return property;
    }
    
    @Extension
    public static final class DescriptorImpl extends NodePropertyDescriptor {
        
        public DescriptorImpl() {
            super(KPPNodeProperty.class);
        }
        
        @Override
        public String getDisplayName() {
            return Messages.KPPNodeProperty_DisplayName();
        }
        
    }
}
