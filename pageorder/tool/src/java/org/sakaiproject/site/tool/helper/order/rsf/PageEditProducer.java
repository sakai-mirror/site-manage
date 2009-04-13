/**********************************************************************************
 * $URL:$
 * $Id:$
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation
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

package org.sakaiproject.site.tool.helper.order.rsf;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.tool.helper.order.impl.SitePageEditHandler;

import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

/**
 * 
 * @author Joshua Ryan joshua.ryan@asu.edu
 *
 */
public class PageEditProducer implements ViewComponentProducer, ViewParamsReporter {

    public SitePageEditHandler handler;
    public static final String VIEW_ID = "PageEdit";

    public String getViewID() {
        return VIEW_ID;
    }

    public void fillComponents(UIContainer tofill, ViewParameters paramso, ComponentChecker arg2) {
        PageEditViewParameters params = (PageEditViewParameters) paramso;

        UIBranchContainer mode = null;

        if (params.pageId != null) {
            if (params.newTitle != null) {
                if (!"".equals(params.newTitle)) {
                    try {
                        String oldTitle = handler.setTitle(params.pageId, params.newTitle);
                      
                        mode = UIBranchContainer.make(tofill, "mode-pass:");
                        UIOutput.make(mode, "page-title", params.newTitle);
                        UIMessage.make(mode, "message", "success_changed", new Object[] {oldTitle, params.newTitle});
                    }
                    catch (Exception e) {
                       ErrorUtil.renderError(tofill, e);
                    }
                }
                else {
                    mode = UIBranchContainer.make(tofill, "mode-failed:");
                    UIMessage.make(mode, "message", "error_title_null");
                }
            }
            
            if (params.newConfig != null) {
                try {
                    // TODO: Add ability to configure any arbitrary setting
                    handler.setConfig(params.pageId, "source", params.newConfig);
                }
                catch (Exception e) {
                  ErrorUtil.renderError(tofill, e);
               }
            }

            if ("true".equals(params.visible) || "false".equals(params.visible)) {
                try {            
                    if ("true".equals(params.visible)) {
                        handler.showPage(params.pageId);
                    }
                    else {
                        handler.hidePage(params.pageId);
                    }
                    Site site = handler.site;
                    SitePage page = site.getPage(params.pageId);
                    String oldTitle = page.getTitle();
                    
                    mode = UIBranchContainer.make(tofill, "mode-pass:");
                    UIOutput.make(mode, "page-title", oldTitle);
                    if ("true".equals(params.visible)) {
                        UIMessage.make(mode, "message", "success_visible",
                            new Object[] {oldTitle});
                    }
                    else {
                      UIMessage.make(mode, "message", "success_hidden",
                          new Object[] {oldTitle});
                    }
                } 
                catch (Exception e) {
                  ErrorUtil.renderError(tofill, e);
               }
            }
        }
        else {
            mode = UIBranchContainer.make(tofill, "mode-failed:");
            UIMessage.make(mode, "message", "error_pageid");
        }
    }
    
    public ViewParameters getViewParameters() {
        return new PageEditViewParameters();
    }

}
