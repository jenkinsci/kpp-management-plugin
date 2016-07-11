package com.sic.plugins.kpp;

import javax.annotation.Nonnull;

import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

public class KPPAddKeychainStep extends AbstractStepImpl {
    @DataBoundConstructor
    public KPPAddKeychainStep() {}

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {
        public DescriptorImpl() { super(KPPAddKeychainStepExecution.class); }


		@Override
        public String getFunctionName() {
            return "addKeychain";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Add Keychain";
        }
    }
}
