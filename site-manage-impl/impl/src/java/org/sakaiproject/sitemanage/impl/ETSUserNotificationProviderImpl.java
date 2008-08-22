package org.sakaiproject.sitemanage.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.emailtemplateservice.model.RenderedTemplate;
import org.sakaiproject.emailtemplateservice.service.EmailTemplateService;
import org.sakaiproject.sitemanage.api.UserNotificationProvider;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;




public class ETSUserNotificationProviderImpl implements UserNotificationProvider {
	
	private static Log M_log = LogFactory.getLog(ETSUserNotificationProviderImpl.class);
	private EmailService emailService; 
	
	public void setEmailService(EmailService es) {
		emailService = es;
	}
	
	private ServerConfigurationService serverConfigurationService;
	public void setServerConfigurationService(ServerConfigurationService scs) {
		serverConfigurationService = scs;
	}
	
	private UserDirectoryService userDirectoryService;
	public void setUserDirectoryService(UserDirectoryService uds) {
		userDirectoryService = uds;
	}
	
	private EmailTemplateService emailTemplateService;
	public void setEmailTemplateService(EmailTemplateService ets) {
		emailTemplateService = ets;
	}
	
	
	/** portlet configuration parameter values* */
	/** Resource bundle using current language locale */
	private static ResourceLoader rb = new ResourceLoader("UserNotificationProvider");

	public void init() {
		//nothing realy to do
		M_log.info("init()");
	}
	
	public void notifyAddedParticipant(boolean newNonOfficialAccount,
			User user, String siteTitle) {
		
		String from = getSetupRequestEmailAddress();
		//we need to get the template
		


		if (from != null) {
			String productionSiteName = serverConfigurationService.getString(
					"ui.service", "");
			String productionSiteUrl = serverConfigurationService
					.getPortalUrl();
			String nonOfficialAccountUrl = serverConfigurationService.getString(
					"nonOfficialAccount.url", null);
			String emailId = user.getEmail();
			String to = emailId;
			String headerTo = emailId;
			String replyTo = emailId;
			Map<String, String> rv = new HashMap<String, String>();
			rv.put("productionSiteName", productionSiteName);

			
			String content = "";
			/*
			 * $userName
			 * $localSakaiName
			 * $currentUserName
			 * $localSakaiUrl
			 */
			 Map<String, String> replacementValues = new HashMap<String, String>();
	            replacementValues.put("userName", user.getDisplayName());
	            replacementValues.put("userEid", user.getEid());
	            replacementValues.put("localSakaiName",serverConfigurationService.getString(
	    				"ui.service", ""));
	            replacementValues.put("currentUserName",userDirectoryService.getCurrentUser().getDisplayName());
	            replacementValues.put("localSakaiUrl", serverConfigurationService.getPortalUrl());
	            replacementValues.put("siteName", siteTitle);
	            replacementValues.put("productionSiteName", productionSiteName);
	         
	            M_log.debug("getting template: sitemange.notifyAddedParticipant");
	            RenderedTemplate template = null;
	           try { 
				template = emailTemplateService.getRenderedTemplateForUser("sitemange.notifyAddedParticipant", user.getReference(), replacementValues); 
				if (template == null)
					return;	
	           }
	           catch (Exception e) {
	        	   e.printStackTrace();
	           }
			List headers = new ArrayList();
			headers.add("Precedence: bulk");
			
			content = template.getRenderedMessage();	
			emailService.send(from, to, template.getRenderedSubject(), content, headerTo,
					replyTo, headers);

		} // if

	}

	public void notifyNewUserEmail(User user, String newUserPassword,
			String siteTitle) {
		
		
		String from = getSetupRequestEmailAddress();
		String productionSiteName = serverConfigurationService.getString(
				"ui.service", "");
		String productionSiteUrl = serverConfigurationService.getPortalUrl();
		
		String newUserEmail = user.getEmail();
		String to = newUserEmail;
		String headerTo = newUserEmail;
		String replyTo = newUserEmail;
		
		
		
		
		 
		String content = "";

		
	
		
		if (from != null && newUserEmail != null) {
			/*
			 * $userName
			 * $localSakaiName
			 * $currentUserName
			 * $localSakaiUrl
			 */
			 Map<String, String> replacementValues = new HashMap<String, String>();
	            replacementValues.put("userName", user.getDisplayName());
	            replacementValues.put("localSakaiName",serverConfigurationService.getString(
	    				"ui.service", ""));
	            replacementValues.put("currentUserName",userDirectoryService.getCurrentUser().getDisplayName());
	            replacementValues.put("localSakaiUrl", serverConfigurationService.getPortalUrl());
	            replacementValues.put("newPassword",newUserPassword);
	            replacementValues.put("siteName", siteTitle);
	            replacementValues.put("productionSiteName", productionSiteName);
	        RenderedTemplate template = emailTemplateService.getRenderedTemplateForUser("sitemanage.notifyNewUserEmail", user.getReference(), replacementValues);    		
	    	if (template == null)
				return;
	        content = template.getRenderedMessage();
			
			String message_subject = template.getRenderedSubject();
			List headers = new ArrayList();
			headers.add("Precedence: bulk");
			emailService.send(from, to, message_subject, content, headerTo,
					replyTo, headers);
		}
	}

	/*
	 *  Private methods
	 */
	
	private String getSetupRequestEmailAddress() {
		String from = serverConfigurationService.getString("setup.request",
				null);
		if (from == null) {
			M_log.warn(this + " - no 'setup.request' in configuration");
			from = "postmaster@".concat(serverConfigurationService
					.getServerName());
		}
		return from;
	}
	
	
	
}
