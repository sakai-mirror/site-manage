/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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

import java.io.IOException;

import java.util.Properties;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.api.SessionState;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService.SortType;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolException;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;

/**
 * <p>
 * HierarchyAction allows site owners to connect a site to a parent.
 * </p>
 */
public class HierarchyAction extends VelocityPortletPaneledAction
{
	/** Resource bundle using current language locale */
	protected static ResourceLoader rb = new ResourceLoader("hierarchy");

	private static final Log logger = LogFactory.getLog(HierarchyAction.class);

        private static final String HIERARCHY_MODE = "hierarchy_mode";
        private static final String MODE_DONE = "helper.done";

	/**
	 * Get the current site page our current tool is placed on.
	 * 
	 * @return The site page id on which our tool is placed.
	 */
	protected String getCurrentSitePageId()
	{
		ToolSession ts = SessionManager.getCurrentToolSession();
		if (ts != null)
		{
			ToolConfiguration tool = SiteService.findTool(ts.getPlacementId());
			if (tool != null)
			{
				return tool.getPageId();
			}
		}
		
		return null;
	}

	/**
	 * Get the current site id
	 * @throws SessionDataException
	 * @return Site id (GUID)
	 */
	private String getSiteId() throws SessionDataException
	{
		Placement placement = ToolManager.getCurrentPlacement();

		if (placement == null)
		{
			throw new SessionDataException("No current tool placement");
		}
		return placement.getContext();
	}

	/**
	 * Get a site property by name
	 *
	 * @param name Property name
	 * @throws IdUnusedException, SessionDataException
	 * @return The property value (null if none)
	 */
	private String getSiteProperty(String name) throws IdUnusedException, SessionDataException
	{
		Site site;

		site = SiteService.getSite(getSiteId());
		return site.getProperties().getProperty(name);
	}

	/**
	 * Setup the velocity context and choose the template for the response.
	 */
	public String buildMainPanelContext(VelocityPortlet portlet, Context context, RunData rundata, SessionState state)
	{
		// set the resource bundle with our strings
		context.put("tlang", rb);

                context.put("doSave", BUTTON + "doSave");
                context.put("doCancel", BUTTON + "doCancel");
                context.put("doRemove", BUTTON + "doRemove");
		try 
		{ 
			Site site;

			site = SiteService.getSite(getSiteId());
			String parentId = site.getProperties().getProperty("sakai:parent-id");
			// String parentId = getSiteProperty("sakai:parent-id");
                        context.put("currentSite", site);
			if ( parentId != null ) {
                		context.put("parentId", parentId);
			} else {
				context.put("sites", SiteService.getSites(
					org.sakaiproject.site.api.SiteService.SelectionType.UPDATE,
					null, null, null, SortType.TITLE_ASC, null));
			}
		} 
		catch (Exception e) 
		{
			// WTF
		}

		return "sakai_hierarchy";
	}

	/**
	 * Handle the configure context's update button
	 */
	public void doSave(RunData data, Context context)
	{
		// TODO: if we do limit the initState() calls, we need to make sure we get a new one after this call -ggolden

		String peid = ((JetspeedRunData) data).getJs_peid();
		SessionState state = ((JetspeedRunData) data).getPortletSessionState(peid);
                ParameterParser params = data.getParameters();
		String parentId = params.getString("parentSite");

		if ( ! SiteService.allowUpdateSite(parentId) ) 
		{
			addAlert(state,rb.getString("error.cannot.update"));
			return;
		}

		try
		{
			Site site;
			site = SiteService.getSite(getSiteId());
			ResourcePropertiesEdit rpe = site.getPropertiesEdit();
			rpe.addProperty("sakai:parent-id", parentId);
			SiteService.save(site);
			SessionManager.getCurrentToolSession().setAttribute(HIERARCHY_MODE, MODE_DONE);
                	scheduleTopRefresh();
		} 
		catch (Exception e)
		{
			addAlert(state,rb.getString("error.cannot.update"));
		}

	}

	/**
	 * doRemove - Clear the parent id value
	 */
	public void doRemove(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData) data).getJs_peid();
		SessionState state = ((JetspeedRunData) data).getPortletSessionState(peid);

		try
		{
			Site site;
			site = SiteService.getSite(getSiteId());
			ResourcePropertiesEdit rpe = site.getPropertiesEdit();
			rpe.removeProperty("sakai:parent-id");
			SiteService.save(site);
			SessionManager.getCurrentToolSession().setAttribute(HIERARCHY_MODE, MODE_DONE);
                	scheduleTopRefresh();
		} 
		catch (Exception e)
		{
			addAlert(state,rb.getString("error.cannot.remove"));
		}
	}

	/**
	 * doCancel called for form input tags type="submit" named="eventSubmit_doCancel" cancel the options process
	 */
	public void doCancel(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData) data).getJs_peid();
		SessionState state = ((JetspeedRunData) data).getPortletSessionState(peid);
		SessionManager.getCurrentToolSession().setAttribute(HIERARCHY_MODE, MODE_DONE);
                scheduleTopRefresh();
	}

	/**
	 * Note a "local" problem (we failed to get session or site data)
	 */
	private static class SessionDataException extends Exception
	{
		public SessionDataException(String text)
		{
			super(text);
		}
	}


	/* (non-Javadoc)
	 * @see org.sakaiproject.cheftool.VelocityPortletPaneledAction#toolModeDispatch(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void toolModeDispatch(String methodBase, String methodExt, HttpServletRequest req, HttpServletResponse res)
			throws ToolException
	{
		ToolSession toolSession = SessionManager.getCurrentToolSession();
		SessionState state = getState(req);

		if (MODE_DONE.equals(toolSession.getAttribute(HIERARCHY_MODE)))
		{

			Tool tool = ToolManager.getCurrentTool();

			String url = (String) SessionManager.getCurrentToolSession().getAttribute(tool.getId() + Tool.HELPER_DONE_URL);

			SessionManager.getCurrentToolSession().removeAttribute(tool.getId() + Tool.HELPER_DONE_URL);
			SessionManager.getCurrentToolSession().removeAttribute(HIERARCHY_MODE);

			try
			{
				res.sendRedirect(url);
			}
			catch (IOException e)
			{
				logger.warn("IOException: ", e);
			}
			return;
		}
		else if(sendToHelper(req, res, req.getPathInfo()))
		{
			return;
		}
		else
		{
			super.toolModeDispatch(methodBase, methodExt, req, res);
		}
	}
}
