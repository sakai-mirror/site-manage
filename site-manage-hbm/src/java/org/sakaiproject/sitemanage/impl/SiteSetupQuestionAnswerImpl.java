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

public class SiteSetupQuestionAnswerImpl implements SiteSetupQuestionAnswer{
	
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
	
	boolean isFillInBlank;
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isFillInBlank()
	{
		return isFillInBlank;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFillInBlank(boolean isFillInBlank)
	{
		this.isFillInBlank = isFillInBlank;
	}
	
	String answer;

	/**
	 * {@inheritDoc}
	 */
	public String getAnswer()
	{
		return answer;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setAnswer(String answer)
	{
		this.answer = answer;
	}

	String answerString;
	
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
