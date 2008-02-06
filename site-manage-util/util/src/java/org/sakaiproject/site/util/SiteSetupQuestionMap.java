/**********************************************************************************
 * $URL:  $
 * $Id:  $
 ***********************************************************************************
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

package org.sakaiproject.site.util;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.sakaiproject.site.util.SiteSetupQuestion;

/**
 * The SiteSetupQuestionMap object is to store user-defined questions based on site types. 
 * Those questions could be presented as survey questions during worksite setup process.
 * @author zqian
 *
 */
public class SiteSetupQuestionMap
{
	private Map<String, List<SiteSetupQuestion>> questionsMap;  
	
	/**
	 * get the set of site types which has questions defined 
	 * @return
	 */
	public Set<String> getSiteTypes()
	{
		return questionsMap.keySet();
	}
	
	public List<SiteSetupQuestion> getQuestionListBySiteType(String siteType)
	{
		List<SiteSetupQuestion> rv = new Vector<SiteSetupQuestion>();
		if (questionsMap.containsKey(siteType))
		{
			return (List<SiteSetupQuestion>) questionsMap.get(siteType);
		}
		else
		{
			return rv;
		}	
	}

	/**
	 * get the question map
	 * @return
	 */
	public Map<String, List<SiteSetupQuestion>> getQuestionsMap() {
		return questionsMap;
	}

	/**
	 * set the question map
	 * @param questionsMap
	 */
	public void setQuestionsMap(Map<String, List<SiteSetupQuestion>> questionsMap) {
		this.questionsMap = questionsMap;
	}
	
}