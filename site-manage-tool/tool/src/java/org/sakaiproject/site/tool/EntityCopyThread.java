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
import org.sakaiproject.site.util.SiteConstants;
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
		M_log.warn(this + " SiteCopy start tositeId = " + toSiteId);
	}
	
	private List<String> toolIds = null;
	private Hashtable<String, List<String>> importTools = null;
	private String toSiteId = null;
	private boolean migrate = false;
	private boolean byPassSecurity = false;
	private SessionState sessionState = null;
	
	//constructor
	EntityCopyThread(List<String> toolIds, Hashtable<String, List<String>> importTools, String toSiteId, boolean migrate, boolean byPassSecurity, SessionState sessionState)
	{
		this.toolIds = toolIds;
		this.importTools = importTools;
		this.toSiteId = toSiteId;
		this.migrate = migrate;
		this.byPassSecurity = byPassSecurity;
		this.sessionState = sessionState;
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
		M_log.info(this + ":stop EntityCopyThread target site Id=" + toSiteId);
	}
	
	public void run()
	{
		// since we might be running while the component manager is still being created and populated, such as at server
		// startup, wait here for a complete component manager
		ComponentManager.waitTillConfigured();

		while (!m_threadStop)
		{
			
			// running
			sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_RUNNING);
			
			if (byPassSecurity)
			{
				// bypass the security checks
		        SecurityService.pushAdvisor(new SecurityAdvisor()
		            {
		                public SecurityAdvice isAllowed(String userId, String function, String reference)
		                {
		                    return SecurityAdvice.ALLOWED;
		                }
		            });
			}
			
		    try
			{
		    	// import resources first
				boolean resourcesImported = false;
				for (int i = 0; i < toolIds.size() && !resourcesImported; i++) {
					String toolId = (String) toolIds.get(i);

					if (toolId.equalsIgnoreCase("sakai.resources")
							&& importTools.containsKey(toolId)) {
						List importSiteIds = (List) importTools.get(toolId);

						for (int k = 0; k < importSiteIds.size(); k++) {
							String fromSiteId = (String) importSiteIds.get(k);

							String fromSiteCollectionId = m_contentHostingService
									.getSiteCollection(fromSiteId);
							String toSiteCollectionId = m_contentHostingService
									.getSiteCollection(toSiteId);

							transferCopyEntities(toolId, fromSiteCollectionId, toSiteCollectionId, migrate);
							resourcesImported = true;
						}
					}
				}

				// import other tools then
				for (int i = 0; i < toolIds.size(); i++) {
					String toolId = (String) toolIds.get(i);
					if (!toolId.equalsIgnoreCase("sakai.resources")
							&& importTools.containsKey(toolId)) {
						List importSiteIds = (List) importTools.get(toolId);
						for (int k = 0; k < importSiteIds.size(); k++) {
							String fromSiteId = (String) importSiteIds.get(k);
							transferCopyEntities(toolId, fromSiteId, toSiteId, migrate);
						}
					}
				}

				if (byPassSecurity)
				{
					SecurityService.clearAdvisors();
				}
				
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
	
	/**
	 * Transfer a copy of all entites from another context for any entity
	 * producer that claims this tool id.
	 * 
	 * @param toolId
	 *            The tool id.
	 * @param fromContext
	 *            The context to import from.
	 * @param toContext
	 *            The context to import into.
	 * @param migrate Whether to remove the old content or not
	 */
	protected void transferCopyEntities(String toolId, String fromContext,
			String toContext, boolean migrate) {
		// TODO: used to offer to resources first - why? still needed? -ggolden

		// offer to all EntityProducers
		for (Iterator i = EntityManager.getEntityProducers().iterator(); i
				.hasNext();) {
			EntityProducer ep = (EntityProducer) i.next();
			if (ep instanceof EntityTransferrer) {
				try {
					EntityTransferrer et = (EntityTransferrer) ep;

					// if this producer claims this tool id
					if (ArrayUtil.contains(et.myToolIds(), toolId)) {
						if (migrate)
						{
							et.transferCopyEntities(fromContext, toContext, new Vector(), true);
						}
						else
						{
							et.transferCopyEntities(fromContext, toContext, new Vector());
						}
					}
				} catch (Throwable t) {
					M_log.warn(this + ".transferCopyEntities: Error encountered while asking EntityTransfer to transferCopyEntities from: "
									+ fromContext + " to: " + toContext, t);
				}
			}
		}
	}
	
}//EntityCopyThread
