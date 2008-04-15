/**********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.sitemanage.api.model;

import java.util.Collection;

/**
 * This is the interface for the Service of SiteSetupQuestion. It contains the backend logic for the tool
 * @author zqian
 *
 */
public interface SiteSetupQuestionService {
	
	/**
	 * Get the list of SiteSetupQuestion objects for the site type
	 * @param siteType
	 * @return
	 */
	public Collection<SiteSetupQuestion> getAllTasks(String siteType);

	/**
	 * Add question
	 * @param q
	 */
	public void addSiteSetupQuestion(SiteSetupQuestion q);
	
	/**
	 * create a new instance of SiteSetupQuestion
	 * @return
	 */
	public SiteSetupQuestion newSiteSetupQuestion();
	
	/**
	 * get a new instance of SiteSetupQuestionAnswer
	 * @return
	 */
	public SiteSetupQuestionAnswer newSiteSetupQuestionAnswer();

	/**
	 * Remove question
	 * @param q
	 */
	public void removeTask(SiteSetupQuestion q);

}
