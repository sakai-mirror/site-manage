package org.sakaiproject.site.tool;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityAdvisor.SecurityAdvice;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.event.api.SessionState;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.id.cover.IdManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.util.SiteConstants;
import org.sakaiproject.thread_local.cover.ThreadLocalManager;
import org.sakaiproject.util.ArrayUtil;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.util.StringUtil;

/**
 * Thread to run when copying site
 * @author zqian
 *
 */
public class SiteCopyThread extends Observable implements Runnable 
{
	private static Log M_log = LogFactory.getLog(SiteCopyThread.class);
	
	private ContentHostingService m_contentHostingService = (ContentHostingService) ComponentManager.get("org.sakaiproject.content.api.ContentHostingService");
	
	private SiteService m_siteService = (SiteService) ComponentManager.get("org.sakaiproject.site.api.SiteService");
	
	private org.sakaiproject.coursemanagement.api.CourseManagementService cms = (org.sakaiproject.coursemanagement.api.CourseManagementService) ComponentManager.get(org.sakaiproject.coursemanagement.api.CourseManagementService.class);
	
	/** Resource bundle using current language locale */
	private static ResourceLoader rb = new ResourceLoader("sitesetupgeneric");
	
	/** My thread running my timeout checker. */
	protected Thread m_thread = null;

	/** Signal to the timeout checker to stop. */
	protected boolean m_threadStop = false;
	
	private String oSiteId = null;
	private String toTitle = null;
	private String selectedTerm = null;
	private boolean byPassSecurity = false;
	private SessionState sessionState = null;
	
	
	public void init(){}
	
	/**
	 * start the thread
	 */
	public void start()
	{
		if (m_thread != null) return;

		m_thread = new Thread(this, "Sakai.SiteInfo.SiteCopy");
		m_threadStop = false;
		m_thread.setDaemon(true);
		m_thread.start();
		M_log.warn(this + " SiteCopy start from site Id = " + oSiteId);
	}

	/**
	 * constructor
	 * @param oSiteId
	 * @param selectedTerm
	 * @param toTitle
	 * @param byPassSecurity
	 * @param sessionState
	 */
	SiteCopyThread(String oSiteId, String selectedTerm, String toTitle, boolean byPassSecurity, SessionState sessionState)
	{
		this.oSiteId = oSiteId;
		this.toTitle = toTitle;
		this.selectedTerm = selectedTerm;
		this.byPassSecurity = byPassSecurity;
		this.sessionState = sessionState;
	}

	/**
	 * stop the thread
	 */
	public void stop()
	{
		if (m_thread != null)
		{
			m_threadStop = true;
			m_thread.interrupt();
			try
			{
				// wait for it to die
				m_thread.join();
			}
			catch (InterruptedException ignore)
			{
			}
			m_thread = null;
		}
		M_log.info(this + ":stop SiteCopyThread source site Id=" + oSiteId);
	}
	
	public void run()
	{
		// since we might be running while the component manager is still being created and populated, such as at server
		// startup, wait here for a complete component manager
		ComponentManager.waitTillConfigured();

		if (!m_threadStop)
		{
			// running
			sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_RUNNING);
			
			// bypass the security checks
	        SecurityService.pushAdvisor(new SecurityAdvisor()
            {
                public SecurityAdvice isAllowed(String userId, String function, String reference)
                {
                    return SecurityAdvice.ALLOWED;
                }
            });
	        
			String nSiteId = IdManager.createUuid();
			
			// get the original site
			Site oSite = null;
			try {
				// get the original site id
				oSite = m_siteService.getSite(oSiteId);
				
			} catch (Exception ignore) {
			}
				
			try
			{
				// add new site
				Site site = m_siteService.addSite(nSiteId, oSite);
					
				// get the new site icon url
				if (site.getIconUrl() != null)
				{
					site.setIconUrl(CopyUtil.transferSiteResource(oSiteId, nSiteId, site.getIconUrl()));
				}
	
				// set title
				site.setTitle(toTitle);
				
				String courseSiteType = ServerConfigurationService.getString("courseSiteType", "course");
				if (site.getType().equals(courseSiteType)) {
					// for course site, need to
					// read in the input for
					// term information
					String termId = StringUtil.trimToNull(selectedTerm);
					if (termId != null) {
						AcademicSession term = cms.getAcademicSession(termId);
						if (term != null) {
							ResourcePropertiesEdit rp = site.getPropertiesEdit();
							rp.addProperty(SiteConstants.PROP_SITE_TERM, term.getTitle());
							rp.addProperty(SiteConstants.PROP_SITE_TERM_EID, term.getEid());
						} else {
							M_log.warn("termId=" + termId + " not found");
						}
					}
				}
				try {
					m_siteService.save(site);
					
					if (site.getType().equals(courseSiteType)) 
					{
						// also remove the provider id attribute if any
						String realm = m_siteService.siteReference(site.getId());
						try 
						{
							AuthzGroup realmEdit = AuthzGroupService.getAuthzGroup(realm);
							realmEdit.setProviderGroupId(null);
							AuthzGroupService.save(realmEdit);
						} catch (GroupNotDefinedException gndException) {
							sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
							M_log.warn(this + ".run: IdUnusedException, not found, or not an AuthzGroup object "+ realm, gndException);
						} catch (Exception exception) {
							sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
							M_log.warn(this + ".run " + rb.getString("java.problem"), exception);
						}
					}
				} catch (IdUnusedException e) {
					sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
					M_log.warn(this + ".run: IdUnusedException, not able to save site id=" + site.getId() , e);
				} catch (PermissionException e) {
					sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
					M_log.warn(this + ".run: PermissionException, not able to save site id=" + site.getId(), e);
				}
						
				try
				{
					// import tool content
					CopyUtil.importToolContent(nSiteId, oSiteId, site, true, sessionState);
		
				    // finished
					sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_FINISHED);
					m_threadStop = true;
				}
			    catch(Exception e) {
			    	// error
			    	sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
			    }
			    
			    SecurityService.clearAdvisors();
			    

			} catch (IdInvalidException e) {
				M_log.warn(this + ".run: " + rb.getString("java.siteinval") + " site id = " + nSiteId, e);
				sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
			} catch (IdUsedException e) {
				M_log.warn(this + ".run: " + rb.getString("java.sitebeenused") + " site id = " + nSiteId, e);
				sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
			} catch (PermissionException e) {
				M_log.warn(this + ".run: " + rb.getString("java.allowcreate") + " site id = " + nSiteId, e);
				sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
			}			    
			finally
			{
				//clear any current bindings
				ThreadLocalManager.clear();
				stop();
			}
		}
			
	}
	
}//SiteCopyThread
