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
public class KPPManagement extends ManagementLink implements StaplerProxy {
    
    @SuppressWarnings("unused")
    public KPPManagement() {}

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
        return new KPPManagementResult();
    }
}
