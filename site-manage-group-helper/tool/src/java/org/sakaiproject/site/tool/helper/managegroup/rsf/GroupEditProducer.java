package org.sakaiproject.site.tool.helper.managegroup.rsf;

import java.util.Collection;
import java.net.URLDecoder;
import java.util.Iterator;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.tool.helper.managegroup.impl.SiteManageGroupHandler;
import org.sakaiproject.site.util.Participant;
import org.sakaiproject.site.util.SiteComparator;
import org.sakaiproject.site.util.SiteConstants;
import org.sakaiproject.util.SortedIterator;
import org.sakaiproject.util.SortedIterator;

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
            
		 String[] minVotes = new String[]{"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};
		 String[] maxVotes = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};
		 
		 // for the site members list
		 Collection siteMembers= handler.getSiteParticipant();
		 String[] siteMemberLabels = new String[siteMembers.size()];
		 String[] siteMemberValues = new String[siteMembers.size()];
		 UISelect siteMember = UISelect.make(groupForm,"siteMembers",siteMemberValues,siteMemberLabels,null);
		 int i =0;
		 Iterator<Participant> sIterator = new SortedIterator(siteMembers.iterator(), new SiteComparator(SiteConstants.SORTED_BY_PARTICIPANT_NAME, Boolean.TRUE.toString()));
	     for (; sIterator.hasNext();i++){
	        	Participant p = (Participant) sIterator.next();
				siteMemberLabels[i] = p.getName();
				siteMemberValues[i] = p.getRegId();
	        }
	     
	     // for the group members list
	     Collection<Participant> groupMembers= handler.getGroupParticipant();
		 String[] groupMemberLabels = new String[groupMembers.size()];
		 String[] groupMemberValues = new String[groupMembers.size()];
		 UISelect groupMember = UISelect.make(groupForm,"groupMembers",groupMemberValues,groupMemberLabels,null);
		 i =0;
		 Iterator<Participant> gIterator = new SortedIterator(groupMembers.iterator(), new SiteComparator(SiteConstants.SORTED_BY_PARTICIPANT_NAME, Boolean.TRUE.toString()));
	     for (; gIterator.hasNext();i++){
	        	Participant p = (Participant) gIterator.next();
				groupMemberLabels[i] = p.getName();
				groupMemberValues[i] = p.getRegId();
	        }
	     
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
