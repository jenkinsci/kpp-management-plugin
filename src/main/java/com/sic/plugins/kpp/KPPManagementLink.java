/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sic.plugins.kpp;

import hudson.Extension;
import hudson.model.ManagementLink;
import org.kohsuke.stapler.StaplerProxy;

/**
 *
 * @author michaelbar
 */
@Extension
public class KPPManagementLink extends ManagementLink implements StaplerProxy {
    
    @SuppressWarnings("unused")
    public KPPManagementLink() {}

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
        //return Jenkins.getInstance().getDescriptor(KPPManagement.class);
        
        return KPPManagement.getInstance();
        //return new KPPManagementResult();
    }
}
