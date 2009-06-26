/**********************************************************************************
 * $URL: $
 * $Id: $
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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.util.BasicAuth;

import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.event.api.SessionState;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.site.util.SiteConstants;

import org.sakaiproject.sitemanage.api.TaskStatusService;

/**
 * this is the servlet to return the status of site copy thread based on the SessionState variable 
 * @author zqian
 *
 */
public class SiteInfoToolServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
    private transient BasicAuth basicAuth;
    protected static final Log log = LogFactory.getLog(SiteInfoToolServlet.class);
    private TaskStatusService m_taskStatusService = (TaskStatusService) ComponentManager.get(TaskStatusService.class);
    
	// --------------------------------------------------------- Public Methods

	/**
	 * Initialize this servlet.
	 */
	public void init() throws ServletException
	{
		super.init();
        try {
            basicAuth = new BasicAuth();
            basicAuth.init();
        } catch (Exception e) {
            log.warn(this + "init " + e.getMessage());
        }
	}
	
	/**
	 * respond to an HTTP GET request
	 * 
	 * @param req
	 *        HttpServletRequest object with the client request
	 * @param res
	 *        HttpServletResponse object back to the client
	 * @exception ServletException
	 *            in case of difficulties
	 * @exception IOException
	 *            in case of difficulties
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		// process any login that might be present
		basicAuth.doLogin(req);
		
		// catch the login helper requests
		// request url is of format https://server_name/sakai-site-manage-tool/tool/sitecopystatus/siteId
		// request url is of format https://server_name/sakai-site-manage-tool/tool/sitecopystatus/
		String option = req.getPathInfo();
		String[] parts = option.split("/");
		if ((parts.length == 3) && (parts[0].equals("")) && (parts[1].equals("sitecopystatus")))
		{
			// within a site, check whether current user has any thread running within this site
			getSiteCopyStatus(parts[2], res);
		}	
		else if ((parts.length == 2) && (parts[0].equals("")) && (parts[1].equals("sitecopystatus")))
		{
			// in the list view of all site, check whether current user has any thread running
			getSiteCopyStatus(null, res);
		}	
	}
	
	/**
	 * 
	 * @param siteId
	 * @param res
	 */
	private void getSiteCopyStatus (String siteId, HttpServletResponse res)
	{
		// get SessionState
		Session session = SessionManager.getCurrentSession();
		if (session != null)
		{
			String userId = session.getUserId();
			try
			{
				// normally the status would be null string. 
				String status = null;
				if (m_taskStatusService.serviceExist())
				{
					if (siteId == null)
					{
						status = m_taskStatusService.getMostRecentTaskStatusForUser(userId);
					}
					else
					{
						status = m_taskStatusService.getTaskStatus(siteId, userId);
					}
				}
				if (status != null)
				{
					res.setContentType("text/plain; charset=UTF-8");
			
					// get the writer
					PrintWriter out = res.getWriter();
			
					// form the head
					out.println(status);
					out.flush();
				}
			}
			catch (IOException e)
			{
				log.warn(this + ":getSiteCopyStatus " + siteId + e.getMessage());
			}
		}
		
	}
	
}
