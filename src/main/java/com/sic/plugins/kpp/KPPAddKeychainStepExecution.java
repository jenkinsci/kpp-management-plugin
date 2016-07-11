package com.sic.plugins.kpp;

import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.EnvStep;
import org.jenkinsci.plugins.workflow.steps.EnvironmentExpander;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Environment;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.tasks.SimpleBuildWrapper;

import com.sic.plugins.kpp.model.KPPKeychainCertificatePair;
import com.sic.plugins.kpp.KPPKeychainsAdding;
import com.sic.plugins.kpp.KPPKeychainsAdding.EnvironmentImpl;;


public class KPPAddKeychainStepExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {
    private static final long serialVersionUID = 1L;

    @StepContextParameter
    private transient TaskListener listener;

    
    @StepContextParameter
    private transient FilePath ws;

    @StepContextParameter
    private transient Run build;
    
    @StepContextParameter
    private transient Launcher launcher;

    @Override
    protected Void run() throws Exception {
        listener.getLogger().println("Running add keychain step.");
        
       
        List<KPPKeychainCertificatePair> keychainCertificatePairs = new ArrayList<KPPKeychainCertificatePair>();
        keychainCertificatePairs.add(new KPPKeychainCertificatePair("iOS-Enterprise-2016-2019.keychain", "iPhone Distribution: sovanta AG", ""));
        listener.getLogger().println("Add list");        
        boolean deleteKeychainsAfterBuild = false;
        boolean overwriteExistingKeychains = false;
        listener.getLogger().println("before setup.");
        KPPKeychainsAdding keychainsBuildWrapper = new KPPKeychainsAdding(keychainCertificatePairs, deleteKeychainsAfterBuild, overwriteExistingKeychains);
        EnvironmentImpl env = keychainsBuildWrapper.setUp(ws, launcher);
        listener.getLogger().println("after setup.");
        EnvVars vars = build.getEnvironment(listener);
        
        List<String> allVars = new ArrayList<String>();
        allVars.add("hey=bla");
      
        EnvStep step = new EnvStep(allVars);
        step.start(getContext());
        
        listener.getLogger().println(step.getOverrides().toString());
        //env.buildEnvVars(vars);
        listener.getLogger().println(vars.toString());
        listener.getLogger().println("after buildenvvars.");//GatlingPublisher publisher = new GatlingPublisher(true);
        //publisher.perform(build, ws, launcher, listener);

        return null;
    }
    
}
