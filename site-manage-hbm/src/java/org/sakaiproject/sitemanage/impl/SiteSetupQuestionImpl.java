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

import java.util.List;

import org.sakaiproject.sitemanage.api.model.*;

public class SiteSetupQuestionImpl implements SiteSetupQuestion {
		
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	/**
	 * {@inheritDoc}
	 */
	public Long getId()
	{
		return id;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setId(Long id)
	{
		this.id = id;
	}
	
	private String question;
	
	/**
	 * {@inheritDoc}
	 */
	public String getQuestion()
	{
		return question;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setQuestion(String question)
	{
		this.question = question;
	}

	List<SiteSetupQuestionAnswer> answers;
	/**
	 * {@inheritDoc}
	 */
	public List<SiteSetupQuestionAnswer> getAnswers()
	{
		return answers;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setAnswers(List<SiteSetupQuestionAnswer> answers)
	{
		this.answers = answers;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addAnswers(SiteSetupQuestionAnswer answer)
	{
		this.answers.add(answer);
	}
	
	boolean required;
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isRequired()
	{
		return required;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setRequired(boolean required)
	{
		this.required = required;
	}

	boolean isMultipleAnswers;
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isMultipleAnswsers()
	{
		return isMultipleAnswers;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setMultipleAnswsers(boolean isMultipleAnswers)
	{
		this.isMultipleAnswers = isMultipleAnswers;
	}
	
	Long siteTypeId;
	/**
	 * {@inheritDoc}
	 */
	public Long getSiteTypeId()
	{
		return siteTypeId;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setSiteType(Long siteTypeId)
	{
		this.siteTypeId = siteTypeId;
	}
}
