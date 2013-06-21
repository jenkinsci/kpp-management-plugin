/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sic.plugins.kpp;

import com.sic.plugins.kpp.model.KPPBaseKeychainsProvider;
import com.sic.plugins.kpp.model.KPPKeychain;
import hudson.Extension;
import hudson.model.Hudson;
import hudson.model.ManagementLink;
import static hudson.model.ManagementLink.all;
import hudson.model.Saveable;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.servlet.ServletException;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 *
 * @author michaelbar
 */
@Extension
public class KPPManagementLink extends ManagementLink implements StaplerProxy, Saveable {
    
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
        return KPPBaseKeychainsProvider.getInstance().getKeychains();
    }
    
    public void doUploadKeychain(StaplerRequest req, StaplerResponse rsp) throws
            ServletException,
            IOException,
            NoSuchAlgorithmException {
        
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        
        FileItem file = req.getFileItem("keychain.file");
        if (file == null || file.getSize() == 0) {
            throw new ServletException("no file selected");
        }
        
        KPPBaseKeychainsProvider.getInstance().uploadKeychain(file);
        KPPBaseKeychainsProvider.getInstance().update();
        
        rsp.sendRedirect2("../"+getUrlName()+"/"); //we stay on page
    }
    
    public void doSave(StaplerRequest req, StaplerResponse rsp) throws
            ServletException,
            IOException,
            NoSuchAlgorithmException {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        
        JSONObject data = req.getSubmittedForm();
        List<KPPKeychain> keychains = req.bindJSONToList(KPPKeychain.class, data.get("keychain"));
        KPPBaseKeychainsProvider.getInstance().updateKeychainsFromSave(keychains);
        save();
        rsp.sendRedirect2("../manage"); //we go back on management page
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
        KPPBaseKeychainsProvider.getInstance().update();
        return this;
    }

    @Override
    public void save() throws IOException {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        KPPBaseKeychainsProvider.getInstance().save();
    }
}
