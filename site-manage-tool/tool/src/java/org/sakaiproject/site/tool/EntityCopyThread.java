package org.sakaiproject.site.tool;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityAdvisor.SecurityAdvice;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.event.api.SessionState;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.util.SiteConstants;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.thread_local.cover.ThreadLocalManager;
import org.sakaiproject.util.ArrayUtil;

/**
 * Thread to run the entity copy task needed when duplicating, copying, and importing site(s).
 * @author zqian
 *
 */
public class EntityCopyThread extends Observable implements Runnable 
{
	private static Log M_log = LogFactory.getLog(EntityCopyThread.class);
	
	private ContentHostingService m_contentHostingService = (ContentHostingService) ComponentManager.get("org.sakaiproject.content.api.ContentHostingService");
	
	private SessionManager m_sessionManager = (SessionManager) ComponentManager.get("org.sakaiproject.tool.api.SessionManager");
	
	private UserDirectoryService m_userDirectoryService = (UserDirectoryService) ComponentManager.get("org.sakaiproject.user.api.UserDirectoryService");
	
	private SiteService m_siteService = (SiteService) ComponentManager.get("org.sakaiproject.site.api.SiteService");
	
	/** My thread running my timeout checker. */
	protected Thread m_thread = null;

	/** Signal to the timeout checker to stop. */
	protected boolean m_threadStop = false;
	
	public void init(){}
	public void start()
	{
		if (m_thread != null) return;

		m_thread = new Thread(this, "Sakai.SiteInfo.SiteCopy");
		m_threadStop = false;
		m_thread.setDaemon(true);
		m_thread.start();
		M_log.info(this + ":start EntityCopyThread for target site id = " + targetSiteId);
	}
	
	private List<String> toolIds = null;
	private Hashtable<String, List<String>> importTools = null;
	private String targetSiteId = null;
	private boolean migrate = false;
	private boolean byPassSecurity = false;
	private SessionState sessionState = null;
	private String userId = null;
	
	//constructor
	public EntityCopyThread(List<String> toolIds, Hashtable<String, List<String>> importTools, String targetSiteId, boolean migrate, boolean byPassSecurity, SessionState sessionState, String userId)
	{
		this.toolIds = toolIds;
		this.importTools = importTools;
		this.targetSiteId = targetSiteId;
		this.migrate = migrate;
		this.byPassSecurity = byPassSecurity;
		this.sessionState = sessionState;
		this.userId = userId;
	}

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
		M_log.info(this + ":stop EntityCopyThread target site Id=" + targetSiteId);
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
				
			try
			{
				// import tool content
				CopyUtil.importToolIntoSite(toolIds, importTools, targetSiteId, true, false, sessionState);
			    // finished
				sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_FINISHED);
				m_threadStop = true;
			}
		    catch(Exception e) {
		    	// error
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
	
}//EntityCopyThread
