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
import org.sakaiproject.event.api.SessionState;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.site.util.SiteConstants;

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
		String option = req.getPathInfo();
		String[] parts = option.split("/");
		if ((parts.length == 3) && (parts[0].equals("")) && (parts[1].equals("sitecopystatus")))
		{
			getSiteCopyStatus(parts[2], res);
		}	
	}
	
	/**
	 * 
	 * @param toolId
	 * @param res
	 */
	private void getSiteCopyStatus (String toolId, HttpServletResponse res)
	{
		// get SessionState
		Session session = SessionManager.getCurrentSession();
		if (session != null)
		{
			ToolSession toolSession = session.getToolSession(toolId);
			SessionState state = UsageSessionService.getSessionState(toolSession.getPlacementId());

			try
			{
			String status = state.getAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS) != null?(String) state.getAttribute(SiteConstants.ENTITYCOPY_THREAD_STATUS):"";
			
			res.setContentType("text/plain; charset=UTF-8");
	
			// get the writer
			PrintWriter out = res.getWriter();
	
			// form the head
			out.println(status);
			out.flush();
			}
			catch (IOException e)
			{
				log.warn(this + ":getSiteCopyStatus " + toolId + e.getMessage());
			}
		}
		
	}
	
}
