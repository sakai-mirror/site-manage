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
package org.sakaiproject.sitemanage.api.model;

import java.util.List;

/**
 * The SiteSetupQuestion object is to store user-defined question.
 * A question would have two parts: the question part and the list of answers
 * @author zqian
 *
 */
public interface SiteSetupQuestion extends java.io.Serializable {
	
	/**
	 * get id of question
	 * @return
	 */
	public Long getId();
	
	/**
	 * set the id of question
	 * @param id
	 */
	public void setId(Long id);
	
	/**
	 * get the question prompt
	 * @return
	 */
	public String getQuestion();

	/**
	 * set the question prompt
	 * @param question
	 */
	public void setQuestion(String question);

	/**
	 * get the list of answers
	 * @return
	 */
	public List<SiteSetupQuestionAnswer> getAnswers();

	/**
	 * set the list of answers
	 * @param answers
	 */
	public void setAnswers(List<SiteSetupQuestionAnswer> answers);
	
	/**
	 * add into the list of answers
	 * @param answers
	 */
	public void addAnswers(SiteSetupQuestionAnswer answer);
	
	/**
	 * is question required or not
	 * @return
	 */
	public boolean isRequired();

	/**
	 * set required
	 * @param required
	 */
	public void setRequired(boolean required);

	/**
	 * does question have multiple answers
	 * @return
	 */
	public boolean isMultipleAnswsers();

	/**
	 * set flag for multiple answers
	 * @param isMultipleAnswsers
	 */
	public void setMultipleAnswsers(boolean isMultipleAnswers);
	
	/**
	 * get the site type id
	 * @return
	 */
	public Long getSiteTypeId();
	
	/**
	 * set the site type id
	 * @param siteTypeId
	 */
	public void setSiteTypeId(Long siteTypeId);
}