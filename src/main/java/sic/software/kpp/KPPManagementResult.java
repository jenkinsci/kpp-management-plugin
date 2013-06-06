/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sic.software.kpp;

import hudson.model.Hudson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 *
 * @author michaelbar
 */
public class KPPManagementResult {

    public void doSaveConfig(StaplerRequest req, StaplerResponse rsp) throws ServletException, IOException {

        FileItem file = req.getFileItem("myFile.file");

        if (file == null) {
            throw new ServletException("no file upload");
        }

        byte[] data = file.get();

        File pathToDirectory = new File(Hudson.getInstance().getRootDir() + "/kpp_upload");
        pathToDirectory.mkdir();
        File toUploadFile = new File(pathToDirectory, file.getName());
        OutputStream os = new FileOutputStream(toUploadFile);
        try {
            os.write(data);
        } finally {
            os.close();
        }
        
        rsp.sendRedirect2("../manageZpp/");
    }
}
