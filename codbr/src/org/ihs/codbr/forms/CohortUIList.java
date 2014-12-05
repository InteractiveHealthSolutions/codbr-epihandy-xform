package org.ihs.codbr.forms;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.fcitmuk.communication.ConnectionParameter;
import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.openmrs.Cohort;
import org.fcitmuk.openmrs.CohortList;
import org.fcitmuk.openmrs.OpenmrsDataStorage;
import org.fcitmuk.openmrs.RequestHeader;
import org.ihs.codbr.MainMidlet;
import org.ihs.codbr.constant.ErrorMessages;
import org.ihs.codbr.constant.UIConstants;
import org.ihs.codbr.constant.WebServerConstants;
import org.ihs.codbr.handler.ResponseNotification;
import org.ihs.codbr.util.UIUtils;

public class CohortUIList extends BaseUIList implements ResponseNotification{
	
	private Command cmdOK;
	private Command cmdExit;
	private Command cmdDownload;
	private Command cmdDelete;
	
	private CohortList dataitems = new CohortList();;

	public CohortUIList (String title, MainMidlet mainMidlet)
	{
		super (title, mainMidlet);
		cmdOK = new Command ("Choose", Command.OK, 0);
		cmdExit = new Command ("Back", Command.EXIT, 1);
		cmdDownload = new Command ("Download", Command.OK, 0);
		cmdDelete = new Command ("Delete", Command.OK, 1);
	}

	protected void loadItems ()
	{
		CohortList stored = OpenmrsDataStorage.getCohorts();
		if(stored != null){
			dataitems = stored;
			
			for (int i = 0; i < dataitems.size(); i++) {
				Cohort coh = ((Cohort)dataitems.getCohorts().elementAt(i));
				append(coh.getName(), null);
			}
		}
	}
	
	public void getCohorts(){
		
	}
	
	public void init (Displayable previousDisplayable)
	{
		setPrevDisplayable(previousDisplayable);
		
		deleteAll ();
		loadItems ();

		addCommand (cmdOK);
		addCommand (cmdExit);
		addCommand(cmdDownload);
		addCommand(cmdDelete);
		setCommandListener (this);
	}

	public void commandAction (Command c, Displayable d)
	{
		final String itemName = this.getSelectedIndex ()>-1?getString (this.getSelectedIndex ()):null;
		if (c == cmdOK)
		{
			
		}
		else if (c == cmdExit)
		{
			getMainMidlet().startMainMenu();
		}
		else if(c == cmdDownload){
			RequestHeader reqh = RequestHeader.getRequestHeader(RequestHeader.ACTION_DOWNLOAD_COHORTS, getMainMidlet().getCurrentXformUser().getName(), getMainMidlet().getCurrentXformUser().getClearTextPassword(), getMainMidlet().getLocale(), null);
			getMainMidlet().sendDownloadRequest(WebServerConstants.SUBURL_XFORM_COHORT_DOWNLOAD, true, 
					createRequestPayload(-1), false, reqh, new CohortList(), this, true);

		}
		else if(c == cmdDelete){
			if(itemName != null){
				UIUtils.showConfirm(UIConstants.TEXT_DELETE_COHORT_CONFIRM, new CommandListener() {
					public void commandAction(Command c, Displayable d) {
						OpenmrsDataStorage.deleteCohort(dataitems.getCohort(itemName).getId());
						loadItems();
					}
				}, null, getMainMidlet().getDisplay());
			}
			else{
				UIUtils.showPopup(ErrorMessages.NO_ITEM_SELECTED, null, getMainMidlet().getDisplay());
			}
			
		}
	}

	protected ConnectionParameter createRequestPayload(int type) {
		ConnectionParameter payload = new ConnectionParameter();

		payload.addParam(WebServerConstants.PARAM_DOWNLOAD_COHORT, "true");
		//payload.addParam(WebServerConstants.PARAM_SERIALIZER_KEY, RequestHeader.XFORMS_COHORT_SERIALIZER);
		
		return payload;
	}

	protected void cleanup() {
		// TODO Auto-generated method stub
		
	}

	public void response(Object otherInformation, Persistent persistentData) {
		CohortList dcoh = (CohortList) persistentData;
		if(dcoh != null && dcoh.size() > 0){
			OpenmrsDataStorage.saveCohorts(dcoh);
			loadItems();
		}
		else if(otherInformation != null){
			UIUtils.renderAlert(ErrorMessages.ERROR_SERVER_CONNECTION +"\nDetails: "+ otherInformation.toString(), null, this, getMainMidlet().getDisplay());
		}
		else{
			UIUtils.renderAlert(ErrorMessages.NO_COHORTS_DOWNLOADED, null, this, getMainMidlet().getDisplay());
		}
	}

	public void error(Object errorInformation) {
		UIUtils.renderAlert(ErrorMessages.ERROR_SERVER_CONNECTION + "\nError Message is : "+errorInformation.toString(), null, this, getMainMidlet().getDisplay());
	}
}
