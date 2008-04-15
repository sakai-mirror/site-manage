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


package org.sakaiproject.sitemanage.impl;

import org.sakaiproject.sitemanage.api.model.*;

public class SiteSetupUserAnswerImpl implements SiteSetupUserAnswer {
	
	private static final long serialVersionUID = 1L;
	
	private String userId;
	
	/**
	 * {@inheritDoc}
	 */
	public String getUserId()
	{
		return userId;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	
	private String siteId;
	
	/**
	 * {@inheritDoc}
	 */
	public String getSiteId()
	{
		return siteId;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setSiteId(String siteId)
	{
		this.siteId = siteId;
	}
	
	private Long questionId;
	/**
	 * {@inheritDoc}
	 */
	public Long getQuestionId()
	{
		return questionId;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setQuestionId(Long questionId)
	{
		this.questionId = questionId;
	}
	
	private Long answerId;
	
	/**
	 * {@inheritDoc}
	 */
	public Long getAnswerId()
	{
		return answerId;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setAnswerId(Long answerId)
	{
		this.answerId = answerId;
	}
	
	private String answerString;
	
	/**
	 * {@inheritDoc}
	 */
	public String getAnswerString()
	{
		return answerString;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setAnswerString(String answerString)
	{
		this.answerString = answerString;
	}
}
