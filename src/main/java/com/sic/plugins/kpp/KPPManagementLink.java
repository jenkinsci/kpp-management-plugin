/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sic.plugins.kpp;

import com.sic.plugins.kpp.model.KPPKeychain;
import com.sic.plugins.kpp.model.KPPKeychainsProvider;
import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.ManagementLink;
import static hudson.model.ManagementLink.all;
import hudson.model.Saveable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.kohsuke.stapler.StaplerProxy;

/**
 *
 * @author michaelbar
 */
@Extension
public class KPPManagementLink extends ManagementLink implements StaplerProxy, Saveable, Describable<KPPManagementLink> {
    
    private final static Logger LOGGER = Logger.getLogger(KPPManagementLink.class.getName());
    
    /**
     * Our keychains
     */
    private List<KPPKeychain> keychains = new ArrayList<KPPKeychain>();
    
    /**
     * Constructor.
     */
    public KPPManagementLink() {
        
    }
    
    /**
     * Gets the singletion instance.
     * @return the singletion instance.
     */
    public static KPPManagementLink getInstance() {
        return all().get(KPPManagementLink.class);
    }

    /**
     * Get all keychains
     * 
     * @return all keychains.
     */
    @SuppressWarnings("unused") // used by stapler
    public List<KPPKeychain> getKeychains() {
        return KPPKeychainsProvider.all().get(ProviderImpl.class).getKeychains();
    }
    
    /**
     * Gets all the keychain descriptors.
     *
     * @return all the credentials descriptors.
     */
    @SuppressWarnings("unused") // used by stapler
    public DescriptorExtensionList<KPPKeychain, Descriptor<KPPKeychain>> getCredentialDescriptors() {
        return KPPKeychainsProvider.allKeychainDescriptors();
    }
    
    @Override
    public String getIconFileName() {
        return "document.gif";
    }

    @Override
    public String getDisplayName() {
        return "KPP Management";
    }

    @Override
    public String getUrlName() {
        return "kppmanagment";
    }

    @Override
    public Object getTarget() {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        return this;
        
        //return Jenkins.getInstance().getDescriptor(KPPManagement.class);
        //return KPPManagement.getInstance();
        //return new KPPManagementResult();
    }

    public void save() throws IOException {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        
        
    }

    public Descriptor<KPPManagementLink> getDescriptor() {
        return Hudson.getInstance().getDescriptorOrDie(getClass());
    }
    
    @Extension
    @SuppressWarnings("unused") // used by Jenkins
    public static final class DescriptorImpl extends Descriptor<KPPManagementLink> {

        @Override
        public String getDisplayName() {
            return "";
        }   
    }
    
    @Extension
    @SuppressWarnings("unused") // used by Jenkins
    public static class ProviderImpl extends KPPKeychainsProvider {
        public String test = null;
        public ProviderImpl() {
            test = "Hallo";
        }
    }
}
