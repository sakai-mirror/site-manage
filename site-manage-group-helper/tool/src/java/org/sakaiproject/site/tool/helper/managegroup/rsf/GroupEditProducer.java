package org.sakaiproject.site.tool.helper.managegroup.rsf;

import java.net.URLDecoder;
import java.util.Iterator;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.tool.helper.managegroup.impl.SiteManageGroupHandler;

import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.components.decorators.UILabelTargetDecorator;
import uk.org.ponder.rsf.evolvers.TextInputEvolver;
import uk.org.ponder.rsf.evolvers.FormatAwareDateInputEvolver;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;
import uk.org.ponder.stringutil.StringList;

/**
 * 
 * @author
 *
 */
public class GroupEditProducer implements ViewComponentProducer, ViewParamsReporter {

    public SiteManageGroupHandler handler;
    public static final String VIEW_ID = "GroupEdit";
    public MessageLocator messageLocator;
    
	private TextInputEvolver richTextEvolver;
	public void setRichTextEvolver(TextInputEvolver richTextEvolver) {
				this.richTextEvolver = richTextEvolver;
	}

    public String getViewID() {
        return VIEW_ID;
    }

    public void fillComponents(UIContainer arg0, ViewParameters arg1, ComponentChecker arg2) {
    	
    	 UIForm groupForm = UIForm.make(arg0, "groups-form");

        // List tools = handler.getAvailableTools();

         StringList toolItems = new StringList();
         
         UISelect toolSelect = UISelect.makeMultiple(groupForm, "select-tools",
                       null, "#{SitePageEditHandler.selectedTools}", new String[] {});

         UIOutput.make(groupForm, "prompt", messageLocator.getMessage("group.newgroup"));
         UIOutput.make(groupForm, "instructions", messageLocator.getMessage("editgroup.instruction"));
         
         UIMessage titleTextLabel = UIMessage.make(arg0, "group_title_label", "group.title");
         UIInput titleTextIn = UIInput.make(groupForm, "group_title", "","");
		 UILabelTargetDecorator.targetLabel(titleTextLabel, titleTextIn);
		 

		 UIMessage groupDescrLabel = UIMessage.make(arg0, "group_description_label", "group.description"); 
		 UIInput groupDescr = UIInput.make(groupForm, "group_description", "", ""); 
		 richTextEvolver.evolveTextInput(groupDescr);
		 UILabelTargetDecorator.targetLabel(groupDescrLabel, groupDescr);
		 
		 UIOutput.make(groupForm, "membership_label", messageLocator.getMessage("editgroup.membership"));
		 UIOutput.make(groupForm, "membership_site_label", messageLocator.getMessage("editgroup.generallist"));
		 UIOutput.make(groupForm, "membership_group_label", messageLocator.getMessage("editgroup.grouplist"));
		 
		 UICommand.make(groupForm, "addMember", messageLocator.getMessage("editgroup.addmember"), "#{SiteManageGroupHandler.addmember}");
		 UICommand.make(groupForm, "removeMember", messageLocator.getMessage("editgroup.removemember"), "#{SiteManageGroupHandler.removemember}");
            
		 /*String[] minVotes = new String[]{"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};
		 String[] maxVotes = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};
		 
		 String[] siteMemberLabels = new String[gradebook_items.size()+1];
		 Group g = getStateGroup(state);
			if (g != null) {
				context.put("group", g);
				context.put("newgroup", Boolean.FALSE);
			} else {
				context.put("newgroup", Boolean.TRUE);
			}
			if (state.getAttribute(STATE_GROUP_TITLE) != null) {
				context.put("title", state.getAttribute(STATE_GROUP_TITLE));
			}
			if (state.getAttribute(STATE_GROUP_DESCRIPTION) != null) {
				context.put("description", state
						.getAttribute(STATE_GROUP_DESCRIPTION));
			}
			Iterator siteMembers = new SortedIterator(getParticipantList(state)
					.iterator(), new SiteComparator(SORTED_BY_PARTICIPANT_NAME,
					Boolean.TRUE.toString()));
			if (siteMembers != null && siteMembers.hasNext()) {
				context.put("generalMembers", siteMembers);
			}
		 UISelect siteMember = UISelect.make(groupForm,"siteMembers",siteMemberValues,siteMemberLabels,null);
		 UISelect groupMember = UISelect.make(groupForm,"groupMembers",groupMemberValues,groupMemberLabels,null);*/
		 
       /*  for (int i = 0; i < tools.size(); i++ ) {
             UIBranchContainer toolRow = UIBranchContainer.make(groupForm, "tool-row:", Integer.toString(i));

             Tool tool = (Tool) tools.get(i);
             
             UIOutput.make(toolRow, "tool-name", tool.getTitle());
             UIOutput.make(toolRow, "tool-id", tool.getId());
             UIOutput.make(toolRow, "tool-description", tool.getDescription());  
             UISelectChoice.make(toolRow, "tool-select", toolSelect.getFullID(), i);
             
             toolItems.add(tool.getId());
         }*/
         
         toolSelect.optionlist.setValue(toolItems.toStringArray());
         
         UICommand.make(groupForm, "save", messageLocator.getMessage("editgroup.update"), "#{SiteManageGroupHandler.addGroup}");

         UICommand.make(groupForm, "cancel", messageLocator.getMessage("editgroup.cancel"), "#{SiteManageGroupHandler.back}");
    }
    
    public ViewParameters getViewParameters() {
        GroupEditViewParameters params = new GroupEditViewParameters();

        //Bet you can't guess what my first language was? ;-)
        params.groupId = "nil";
        params.newTitle = "nil";
        params.newConfig = "nil";
        return params;
    }

}
