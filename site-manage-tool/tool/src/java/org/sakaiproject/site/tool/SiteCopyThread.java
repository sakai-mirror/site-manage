/**********************************************************************************
 * $URL:$
 * $Id:$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

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
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.thread_local.cover.ThreadLocalManager;
import org.sakaiproject.util.ArrayUtil;
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
	
	private UserDirectoryService m_userDirectoryService = (UserDirectoryService) ComponentManager.get("org.sakaiproject.user.api.UserDirectoryService");
	
	private SiteService m_siteService = (SiteService) ComponentManager.get("org.sakaiproject.site.api.SiteService");
	
	private SessionManager m_sessionManager = (SessionManager) ComponentManager.get("org.sakaiproject.tool.api.SessionManager");
	
	private org.sakaiproject.coursemanagement.api.CourseManagementService cms = (org.sakaiproject.coursemanagement.api.CourseManagementService) ComponentManager.get(org.sakaiproject.coursemanagement.api.CourseManagementService.class);
	
	/** My thread running my timeout checker. */
	protected Thread m_thread = null;

	/** Signal to the timeout checker to stop. */
	protected boolean m_threadStop = false;
	
	private String sourceSiteId = null;
	private String targetSiteId = null;
	private boolean createNewSite = false;
	private String toTitle = null;
	private String selectedTerm = null;
	private boolean byPassSecurity = false;
	private SessionState sessionState = null;
	private String userId = null;
	private String m_taskStatusStreamUrl = null;
	
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
		
		M_log.info(this + " SiteCopy start from site Id = " + sourceSiteId);
	}

	/**
	 * constructor
	 * @param sourceSiteId
	 * @param targetSiteId
	 * @param createNewSite
	 * @param selectedTerm
	 * @param toTitle
	 * @param sessionState
	 * @param userId
	 */
	SiteCopyThread(String sourceSiteId, String targetSiteId, boolean createNewSite, String selectedTerm, String toTitle, boolean byPassSecurity, SessionState sessionState, String userId, String taskStatusStreamUrl)
	{
		this.sourceSiteId = sourceSiteId;
		this.targetSiteId = targetSiteId;
		this.createNewSite = createNewSite;
		this.toTitle = toTitle;
		this.selectedTerm = selectedTerm;
		this.byPassSecurity = byPassSecurity;
		this.sessionState = sessionState;
		this.userId = userId;
		this.m_taskStatusStreamUrl = taskStatusStreamUrl;
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
		M_log.info(this + ":stop SiteCopyThread source site Id=" + sourceSiteId);
	}
	
	public void run()
	{
		// since we might be running while the component manager is still being created and populated, such as at server
		// startup, wait here for a complete component manager
		ComponentManager.waitTillConfigured();

		if (!m_threadStop)
		{
			org.sakaiproject.tool.api.Session s = null;
			if (s == null)
			{
				s = m_sessionManager.startSession();
				try
				{
					User u = m_userDirectoryService.getUser(userId);
					s.setUserId(u.getId());
					m_sessionManager.setCurrentSession(s);
				}
				catch (Exception e)
				{
					M_log.warn(this + ":run cannot find user with id " + userId + e.getMessage());
				}
			}
			
			// running
			sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_RUNNING);
			TaskStatusGetPost.postStatus(m_taskStatusStreamUrl, SiteConstants.ENTITYCOPY_THREAD_STATUS_RUNNING, "Worksite Setup", "copying site");
	        
			String targetSiteId = this.targetSiteId;
			
			// get the original site
			Site sourceSite = null;
			try {
				// get the original site id
				sourceSite = m_siteService.getSite(sourceSiteId);
				
			} catch (Exception e) {
				M_log.warn(this + "run: cannot get sour site with id = " + sourceSiteId + e.getMessage());
			}
				
			try
			{
				Site site = null;
				if (createNewSite)
				{
					targetSiteId = IdManager.createUuid();
					
					// add new site
					site = m_siteService.addSite(targetSiteId, sourceSite);
						
					// get the new site icon url
					if (site.getIconUrl() != null)
					{
						site.setIconUrl(CopyUtil.transferSiteResource(sourceSiteId, targetSiteId, site.getIconUrl()));
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
								TaskStatusGetPost.postStatus(m_taskStatusStreamUrl, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR, "Worksite Setup", "error copying site");
								M_log.warn(this + ".run: IdUnusedException, not found, or not an AuthzGroup object "+ realm, gndException);
							} catch (Exception exception) {
								sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
								TaskStatusGetPost.postStatus(m_taskStatusStreamUrl, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR, "Worksite Setup", "error copying site");
								M_log.warn(this + ".run problem of get realm for site " + site.getId() , exception);
							}
						}
					} catch (IdUnusedException e) {
						sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
						TaskStatusGetPost.postStatus(m_taskStatusStreamUrl, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR, "Worksite Setup", "error copying site");
						M_log.warn(this + ".run: IdUnusedException, not able to save site id=" + site.getId() , e);
					} catch (PermissionException e) {
						sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
						TaskStatusGetPost.postStatus(m_taskStatusStreamUrl, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR, "Worksite Setup", "error copying site");
						M_log.warn(this + ".run: PermissionException, not able to save site id=" + site.getId(), e);
					}
				}
				else
				{
					// no need to create a new site
					site = m_siteService.getSite(targetSiteId);
				}
						
				try
				{
					// import tool content
					CopyUtil.importToolContent(targetSiteId, sourceSiteId, true, sessionState);
		
				    // finished
					sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_FINISHED);

					TaskStatusGetPost.postStatus(m_taskStatusStreamUrl, SiteConstants.ENTITYCOPY_THREAD_STATUS_FINISHED, "Worksite Setup", "Finished copying site");
					m_threadStop = true;
				}
			    catch(Exception e) {
			    	// error
			    	sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
					TaskStatusGetPost.postStatus(m_taskStatusStreamUrl, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR, "Worksite Setup", "error copying site");
			    }
			    
			} catch (IdUnusedException e) {
				M_log.warn(this + ".run: cannot find site with id =" + targetSiteId, e);
				sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
				TaskStatusGetPost.postStatus(m_taskStatusStreamUrl, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR, "Worksite Setup", "error copying site");
			} catch (IdInvalidException e) {
				M_log.warn(this + ".run: invalid site id = " + targetSiteId, e);
				sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
				TaskStatusGetPost.postStatus(m_taskStatusStreamUrl, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR, "Worksite Setup", "error copying site");
			} catch (IdUsedException e) {
				M_log.warn(this + ".run: site id = " + targetSiteId + " has been used", e);
				sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
				TaskStatusGetPost.postStatus(m_taskStatusStreamUrl, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR, "Worksite Setup", "error copying site");
			} catch (PermissionException e) {
				M_log.warn(this + ".run: no right permission for adding or getting site id = " + targetSiteId, e);
				sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
				TaskStatusGetPost.postStatus(m_taskStatusStreamUrl, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR, "Worksite Setup", "error copying site");
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
