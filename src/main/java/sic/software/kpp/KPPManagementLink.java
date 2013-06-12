/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sic.software.kpp;

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
        return "document.gif";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getDisplayName() {
        return "KPP Management";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getUrlName() {
        return "manageZpp";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getTarget() {
        return KPPManagement.getInstance();
        //return new KPPManagementResult();
    }
}
