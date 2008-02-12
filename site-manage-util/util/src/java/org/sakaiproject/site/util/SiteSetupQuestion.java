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
import java.util.Vector;

/**
 * The SiteSetupQuestion object is to store user-defined question.
 * A question would have two parts: the question part and the list of answers
 * @author zqian
 *
 */
public class SiteSetupQuestion
{
	/* the question prompt */
	private String question = "";
	
	/* whether the question is required */
	private boolean required = false;
	
	/* whether the question has multiple answers*/
	private boolean isMultipleAnswsers = false;
	
	/* the answer list */
	private List<SiteSetupQuestionAnswer> answers = new Vector<SiteSetupQuestionAnswer>();

	/**
	 * get the question prompt
	 * @return
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * set the question prompt
	 * @param question
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * get the list of answers
	 * @return
	 */
	public List<SiteSetupQuestionAnswer> getAnswers() {
		return answers;
	}

	/**
	 * set the list of answers
	 * @param answers
	 */
	public void setAnswers(List<SiteSetupQuestionAnswer> answers) {
		this.answers = answers;
	}
	
	/**
	 * add into the list of answers
	 * @param answers
	 */
	public void addAnswers(SiteSetupQuestionAnswer answer) {
		List<SiteSetupQuestionAnswer> currentAnswers = getAnswers();
		if (currentAnswers == null)
		{
			currentAnswers = new Vector<SiteSetupQuestionAnswer>();
		}
		currentAnswers.add(answer);
		setAnswers(currentAnswers);
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isMultipleAnswsers() {
		return isMultipleAnswsers;
	}

	public void setMultipleAnswsers(boolean isMultipleAnswsers) {
		this.isMultipleAnswsers = isMultipleAnswsers;
	}
	
}