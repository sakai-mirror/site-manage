/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 The Sakai Foundation
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

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.site.tool.helper.order.impl.SitePageEditHandler;
import org.sakaiproject.tool.api.Tool;

import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.components.UISelectChoice;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.stringutil.StringList;

/**
 * 
 * @author Joshua Ryan joshua.ryan@asu.edu
 *
 */
public class PageAddProducer implements ViewComponentProducer, NavigationCaseReporter {
    
    public static final String VIEW_ID = "PageAdd";

    public SitePageEditHandler handler;
    public MessageLocator messageLocator;

    public String getViewID() {
        return VIEW_ID;
    }

    public void fillComponents(UIContainer tofill, ViewParameters arg1, ComponentChecker arg2) {
     
        UIForm toolsForm = UIForm.make(tofill, "tools-form");

        List<Tool> tools = handler.getAvailableTools();

        StringList toolItems = new StringList();
        
        UISelect toolSelect = UISelect.makeMultiple(toolsForm, "select-tools",
                      null, "#{SitePageEditHandler.selectedTools}", new String[] {});

        UIMessage.make(toolsForm, "prompt", "add_prompt", new Object[] {handler.title});
  
        for (int i = 0; i < tools.size(); i++ ) {
            UIBranchContainer toolRow = UIBranchContainer.make(toolsForm, "tool-row:", Integer.toString(i));

            Tool tool = tools.get(i);
            
            UIOutput.make(toolRow, "tool-name", tool.getTitle());
            UIOutput.make(toolRow, "tool-id", tool.getId());
            UIOutput.make(toolRow, "tool-description", tool.getDescription());  
            UISelectChoice.make(toolRow, "tool-select", toolSelect.getFullID(), i);
            
            toolItems.add(tool.getId());
        }
        
        toolSelect.optionlist.setValue(toolItems.toStringArray());
        
        UICommand.make(toolsForm, "save", UIMessage.make("add_selected"), 
                       "#{SitePageEditHandler.addTools}");

        UICommand.make(toolsForm, "cancel", UIMessage.make("cancel")).setReturn("back");
   
    }
    
    public List reportNavigationCases() {
        List togo = new ArrayList();
        togo.add(new NavigationCase("error", new SimpleViewParameters(VIEW_ID)));
        togo.add(new NavigationCase("success", 
               new SimpleViewParameters(PageListProducer.VIEW_ID)));
        togo.add(new NavigationCase("back", 
               new SimpleViewParameters(PageListProducer.VIEW_ID)));
        return togo;
    }

}
