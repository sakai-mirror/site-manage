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
package org.sakaiproject.sitemanage.api;

/**
 * This service is for storing and retrieving the task status.
 * @author zqian
 *
 */
public interface TaskStatusService {
	
	/**
	 * whether the service exist or not
	 * @return
	 */
	public boolean serviceExist();
	
	/**
	 * get most recent task status for user
	 * @param userId
	 * @return
	 */
	public String getMostRecentTaskStatusForUser(String userId);
	
	/**
	 * get status
	 * @param entityReference
	 * @param userId
	 * @return
	 */
	public String getTaskStatus(String entityReference, String userId);
	
	/**
	 * post task status 
	 * @param entityReference
	 * @param userId
	 * @param status
	 */
	public void postTaskStatus(String entityReference, String userId, String status);
	

	/**
	 * create new task status entry
	 * @param entityReference
	 * @param userId
	 * @param status
	 */
	public void addTaskStatus(String entityReference, String userId, String status);

}
