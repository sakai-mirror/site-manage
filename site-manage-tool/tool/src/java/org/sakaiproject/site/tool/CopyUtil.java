package org.sakaiproject.site.tool;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.event.api.SessionState;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.util.SiteConstants;
import org.sakaiproject.thread_local.cover.ThreadLocalManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.util.ArrayUtil;
import org.sakaiproject.util.StringUtil;

public class CopyUtil {
	
	public static Log M_log = LogFactory.getLog(SiteCopyThread.class);
	
	public static ContentHostingService m_contentHostingService = (ContentHostingService) ComponentManager.get("org.sakaiproject.content.api.ContentHostingService");
	
	
	/**
	 * 
	 * @param nSiteId
	 * @param oSiteId
	 * @param site
	 */
	public static void importToolContent(String nSiteId, String oSiteId, Site site, boolean bypassSecurity, SessionState state) {
		List<String> toolIds = new Vector<String>();
		Hashtable<String, List<String>> siteIds = new Hashtable<String, List<String>>();
		List pageList = site.getPages();
		if (!((pageList == null) || (pageList.size() == 0))) {
			for (ListIterator i = pageList
					.listIterator(); i.hasNext();) {
				SitePage page = (SitePage) i.next();

				List pageToolList = page.getTools();
				if (!(pageToolList == null || pageToolList.size() == 0))
				{
					Tool tool = ((ToolConfiguration) pageToolList.get(0)).getTool();
					if (tool != null)
					{
						String toolId = StringUtil.trimToNull(tool.getId());
						if (toolId != null)
						{
							// populating the tool id list for importing
							
							// only have one sour site
							List<String> sIds = new Vector<String>();
							sIds.add(oSiteId);
							
							toolIds.add(toolId);
							siteIds.put(toolId, sIds);
							
							if (toolId.equalsIgnoreCase(SiteConstants.SITE_INFORMATION_TOOL)) 
							{
								// handle Home tool specially, need to update the site infomration display url if needed
								String newSiteInfoUrl = transferSiteResource(oSiteId, nSiteId, site.getInfoUrl());
								site.setInfoUrl(newSiteInfoUrl);
							}
						}
					}
				}
			}
		}
		
		// now that we have the tool list, do the copy content
		importToolIntoSite(toolIds, siteIds, site.getId(), false, true, state);
	}
	
	/**
	 * This is used to update exsiting site attributes with encoded site id in it. A new resource item is added to new site when needed
	 * 
	 * @param oSiteId
	 * @param nSiteId
	 * @param siteAttribute
	 * @return the new migrated resource url
	 */
	public static String transferSiteResource(String oSiteId, String nSiteId, String siteAttribute) {
		String rv = "";
		
		String accessUrl = ServerConfigurationService.getAccessUrl();
		if (siteAttribute!= null && siteAttribute.indexOf(oSiteId) != -1 && accessUrl != null)
		{
			// stripe out the access url, get the relative form of "url"
			Reference ref = EntityManager.newReference(siteAttribute.replaceAll(accessUrl, ""));
			try
			{
				ContentResource resource = m_contentHostingService.getResource(ref.getId());
				// the new resource
				ContentResource nResource = null;
				String nResourceId = resource.getId().replaceAll(oSiteId, nSiteId);
				try
				{
					nResource = m_contentHostingService.getResource(nResourceId);
				}
				catch (Exception n2Exception)
				{
					// copy the resource then
					try
					{
						nResourceId = m_contentHostingService.copy(resource.getId(), nResourceId);
						nResource = m_contentHostingService.getResource(nResourceId);
					}
					catch (Exception n3Exception)
					{
					}
				}
				
				// get the new resource url
				rv = nResource != null?nResource.getUrl(false):"";
				
			}
			catch (Exception refException)
			{
				M_log.warn("CopyUtil:transferSiteResource: cannot find resource with ref=" + ref.getReference() + " " + refException.getMessage());
			}
		}
		
		return rv;
	}
	
	// import tool content into site
	public static void importToolIntoSite(List<String> toolIds, Hashtable<String, List<String>> importTools, String siteId, boolean migrate, boolean byPassSecurity, SessionState sessionState) {
		if (importTools != null) {
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
									.getSiteCollection(siteId);

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
							transferCopyEntities(toolId, fromSiteId, siteId, migrate);
						}
					}
				}

				if (byPassSecurity)
				{
					SecurityService.clearAdvisors();
				}
			}
		    catch(Exception e) {
		    	// error
		    	sessionState.setAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS, SiteConstants.ENTITYCOPY_THREAD_STATUS_ERROR);
		    }
		    finally
			{
			}
		}
	} // importToolIntoSite
	
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
	public static void transferCopyEntities(String toolId, String fromContext,
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
					M_log.warn(".transferCopyEntities: Error encountered while asking EntityTransfer to transferCopyEntities from: "
									+ fromContext + " to: " + toContext, t);
				}
			}
		}
	}
}
