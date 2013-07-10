package com.sic.plugins.kpp;

import com.sic.plugins.kpp.provider.KPPKeychainsProvider;
import com.sic.plugins.kpp.model.KPPKeychain;
import com.sic.plugins.kpp.model.KPPProvisioningProfile;
import com.sic.plugins.kpp.provider.KPPProvisioningProfilesProvider;
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
 * @author mb
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
        return KPPKeychainsProvider.getInstance().getKeychains();
    }
    
    /**
     * Get all provisioning profiles.
     * @return  all provisioning profiles
     */
    public List<KPPProvisioningProfile> getProvisioningProfiles() {
        List<KPPProvisioningProfile> list = KPPProvisioningProfilesProvider.getInstance().getProvisioningProfiles();
        return list;
    }
    
    public void doUploadFile(StaplerRequest req, StaplerResponse rsp) throws
            ServletException,
            IOException,
            NoSuchAlgorithmException {
        
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        
        FileItem file = req.getFileItem("file");
        if (file == null || file.getSize() == 0) {
            throw new ServletException("no file selected");
        }
        
        KPPKeychainsProvider kProvider = KPPKeychainsProvider.getInstance();
        KPPProvisioningProfilesProvider ppProvider = KPPProvisioningProfilesProvider.getInstance();
        if (kProvider.isKeychainFile(file)) {
            kProvider.upload(file);
            kProvider.update();
        } else if (ppProvider.isMobileProvisionProfileFile(file)) {
            ppProvider.upload(file);
            ppProvider.update();
        } else {
            throw new ServletException("Wrong filetype. Uploaded file is no keychain or provisioning profile file.");
        }
        
        rsp.sendRedirect2("../"+getUrlName()+"/"); //we stay on page
    }
    
    public void doSave(StaplerRequest req, StaplerResponse rsp) throws
            ServletException,
            IOException,
            NoSuchAlgorithmException {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        
        JSONObject data = req.getSubmittedForm();
        List<KPPKeychain> keychains = req.bindJSONToList(KPPKeychain.class, data.get("keychain"));
        KPPKeychainsProvider.getInstance().updateKeychainsFromSave(keychains);
        Object object = data.get("profile");
        List<KPPProvisioningProfile> pps = req.bindJSONToList(KPPProvisioningProfile.class, object);
        KPPProvisioningProfilesProvider.getInstance().updateProvisioningProfilesFromSave(pps);
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
        KPPKeychainsProvider.getInstance().update();
        return this;
    }

    @Override
    public void save() throws IOException {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        KPPKeychainsProvider.getInstance().save();
    }
}
