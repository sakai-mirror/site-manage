package org.sakaiproject.site.tool.helper.participant.rsf;

import java.util.Collection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.site.tool.helper.participant.rsf.AddViewParameters;
import org.sakaiproject.site.tool.helper.participant.impl.SiteAddParticipantHandler;
import org.sakaiproject.site.util.Participant;
import org.sakaiproject.site.util.SiteComparator;
import org.sakaiproject.site.util.SiteConstants;
import org.sakaiproject.util.SortedIterator;
import org.sakaiproject.util.SortedIterator;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;

import uk.ac.cam.caret.sakai.rsf.producers.FrameAdjustingProducer;
import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.messageutil.TargettedMessageList;
import uk.org.ponder.rsf.components.UIBoundBoolean;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.components.decorators.UILabelTargetDecorator;
import uk.org.ponder.rsf.components.decorators.UITooltipDecorator;
import uk.org.ponder.rsf.evolvers.TextInputEvolver;
import uk.org.ponder.rsf.evolvers.FormatAwareDateInputEvolver;
import uk.org.ponder.rsf.flow.jsfnav.DynamicNavigationCaseReporter;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.DefaultView;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;
import uk.org.ponder.stringutil.StringList;

/**
 * Assign same role while adding participant
 * @author
 *
 */
public class SameRoleProducer implements ViewComponentProducer, DynamicNavigationCaseReporter, DefaultView {

	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(SameRoleProducer.class);
	
    public SiteAddParticipantHandler handler;
    public static final String VIEW_ID = "Add";
    public MessageLocator messageLocator;
    public FrameAdjustingProducer frameAdjustingProducer;
    public SessionManager sessionManager;
    public SiteService siteService = null;
    public void setSiteService(SiteService siteService)
	{
		this.siteService = siteService;
	}

    public String getViewID() {
        return VIEW_ID;
    }
    
    private TargettedMessageList tml;
	public void setTargettedMessageList(TargettedMessageList tml) {
		this.tml = tml;
	}
	
	public UserDirectoryService userDirectoryService;
	public void setUserDiretoryService(UserDirectoryService userDirectoryService)
	{
		this.userDirectoryService = userDirectoryService;
	}

    public void fillComponents(UIContainer tofill, ViewParameters arg1, ComponentChecker arg2) {
    	
    	String state="";
    	UIOutput.make(tofill, "page-title", messageLocator.getMessage("add.addpart") + " " + handler.getSiteTitle());
    	
    	boolean isCourseSite = handler.isCourseSite();
    	if (isCourseSite)
    	{
    		// show specific instructions for adding participant into course site
	    	UIOutput.make(tofill, "add.official", messageLocator.getMessage("add.official"));
	    	UIOutput.make(tofill, "add.official1", messageLocator.getMessage("add.official1"));
	    	UIOutput.make(tofill, "add.official.instruction", messageLocator.getMessage("add.official.instruction"));
	    }
    	
    	UIForm participantForm = UIForm.make(tofill, "participant-form");
    	
    	// official participant
    	UIInput.make(participantForm, "officialAccountParticipant", "", "");
    	UIOutput.make(tofill, "officialAccountSectionTitle", handler.getServerConfigurationString("officialAccountSectionTitle"));
    	UIOutput.make(tofill, "officialAccountName", handler.getServerConfigurationString("officialAccountName"));
    	UIOutput.make(tofill, "officialAccountLabel", handler.getServerConfigurationString("officialAccountLabel"));
    	UIOutput.make(tofill, "official.add.multiple", messageLocator.getMessage("add.multiple"));
    	
    	String pickerAction = handler.getServerConfigurationString("officialAccountPickerAction");
		if (pickerAction != null && !"".equals(pickerAction))
		{
			UIOutput.make(tofill, "officialAccountPickerLabel", handler.getServerConfigurationString("officialAccountPickerLabel"));
			UIOutput.make(tofill, "officialAccountPickerAction", pickerAction);
		}
    	
		// non official participant
    	String allowAddNonOfficialParticipant = handler.getServerConfigurationString("nonOfficialAccount", "true");
    	if (allowAddNonOfficialParticipant.equalsIgnoreCase("true"))
    	{
    		UIInput.make(participantForm, "nonOfficialAccountParticipant", "", "");
	    	UIOutput.make(tofill, "nonOfficialAccountSectionTitle", handler.getServerConfigurationString("nonOfficialAccountSectionTitle"));
	    	UIOutput.make(tofill, "nonOfficialAccountName", handler.getServerConfigurationString("nonOfficialAccountName"));
	    	UIOutput.make(tofill, "nonOfficialAccountLabel", handler.getServerConfigurationString("nonOfficialAccountLabel"));UIOutput.make(tofill, "official.add.multiple", messageLocator.getMessage("add.multiple"));
	    	UIOutput.make(tofill, "nonOfficial.add.multiple", messageLocator.getMessage("add.multiple"));
    	}
    	
    	UIOutput.make(tofill, "roles.instruction", messageLocator.getMessage("add.participants"));
    	UIOutput.make(tofill, "roles.label.sameroles", messageLocator.getMessage("add.assign"));
    	UIOutput.make(tofill, "roles.label.diffroles", messageLocator.getMessage("add.assign2"));
    	UIBoundBoolean.make(participantForm, "role", "", Boolean.TRUE);
		
    	UICommand.make(participantForm, "save", messageLocator.getMessage("gen.continue"), "#{siteAddParticipantHandler.processGetParticipant}");

        UICommand.make(participantForm, "cancel", messageLocator.getMessage("gen.cancel"), "#{siteAddParticipantHandler.processCancel}");
        
        frameAdjustingProducer.fillComponents(tofill, "resize", "resetFrame");
         
    }
    
    public ViewParameters getViewParameters() {
    	AddViewParameters params = new AddViewParameters();

        params.id = null;
        return params;
    }
    
    public List reportNavigationCases() {
        List togo = new ArrayList();
        togo.add(new NavigationCase("sameRole", new SimpleViewParameters(SameRoleProducer.VIEW_ID)));
        togo.add(new NavigationCase("differentRole", new SimpleViewParameters(DifferentRoleProducer.VIEW_ID)));
        return togo;
    }

}
