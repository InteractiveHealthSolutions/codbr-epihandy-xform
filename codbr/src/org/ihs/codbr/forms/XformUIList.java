package org.ihs.codbr.forms;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.fcitmuk.communication.ConnectionParameter;
import org.fcitmuk.db.util.Persistent;
import org.fcitmuk.epihandy.FormData;
import org.fcitmuk.epihandy.FormDef;
import org.fcitmuk.epihandy.StudyDef;
import org.fcitmuk.epihandy.StudyDefList;
import org.fcitmuk.epihandy.midp.db.EpihandyDataStorage;
import org.fcitmuk.epihandy.midp.forms.FormManager;
import org.fcitmuk.epihandy.midp.forms.StudyListView;
import org.fcitmuk.openmrs.OpenmrsDataStorage;
import org.fcitmuk.openmrs.RequestHeader;
import org.fcitmuk.openmrs.XformTempList;
import org.ihs.codbr.MainMidlet;
import org.ihs.codbr.constant.ErrorMessages;
import org.ihs.codbr.constant.UIConstants;
import org.ihs.codbr.constant.WebServerConstants;
import org.ihs.codbr.handler.ResponseNotification;
import org.ihs.codbr.util.UIUtils;
import org.ihs.xform.EpihandyXform;
import org.ihs.xform.StringReader;
import org.ihs.xform.Xform;
import org.ihs.xform.XformXml;

public class XformUIList extends BaseUIList implements ResponseNotification{
	private static final int XFORM_LIST_DOWNLOAD = 0;
	private static final int XFORM_DOWNLOAD = 1;
	
	
	private Command cmdOK;
	private Command cmdExit;
	private Command cmdDownload;
	
	private Vector dataitems = new Vector();

	public XformUIList (String title, MainMidlet mainMidlet)
	{
		super (title, mainMidlet);
		cmdOK = new Command ("New", Command.OK, 0);
		cmdExit = new Command ("Back", Command.EXIT, 1);
		cmdDownload = new Command ("Download", Command.OK, 0);
	}

	protected void loadItems ()
	{
		 Vector stored = OpenmrsDataStorage.getXformsList();
		if(stored != null){
			dataitems = stored;
			
			for (int i = 0; i < dataitems.size(); i++) {
				Xform x = ((Xform)dataitems.elementAt(i));
				append(x.getFormId() + ":" + x.getFormName(), null);
			}
		}
	}
	
	public void init (Displayable previousDisplayable)
	{
		setPrevDisplayable(previousDisplayable);
		
		deleteAll ();
		loadItems ();

		addCommand (cmdOK);
		addCommand (cmdExit);
		addCommand(cmdDownload);
		setCommandListener (this);
	}
	
	private int getFormIdFromListItem(){
		String seli = getString(this.getSelectedIndex());
		int fid = Integer.parseInt(seli.substring(0, seli.indexOf(":")));
		return fid;
	}
	
	public void commandAction (Command c, Displayable d)
	{
		final XformUIList curdisp = this;
		if (c == cmdOK)
		{
			//OpenMRS doesnot maintain studies list. there is only one default study with id 1. so nowhere we are using studylisting things
			getMainMidlet().getFormManager().mainDisplayable(curdisp);
			getMainMidlet().getFormManager().showFormDefList(EpihandyDataStorage.getStudy(1));
			
			/*String xml = OpenmrsDataStorage.getXformXml(getFormIdFromListItem()).getFormXml();
			FormDef fdef = EpihandyXform.fromXform2FormDef(new StringReader(xml));
			fdef.getClass();
			FormData fdt00 = new FormData(fdef);*/
			
			//getMainMidlet().getFormManager().showForm(true, fdt00, false, curdisp, xml);
	
		}
		else if (c == cmdExit)
		{
			getMainMidlet().startMainMenu();
		}
		else if(c == cmdDownload){
			UIUtils.showConfirm(UIConstants.TEXT_DOWNLOAD_XFORM_DELETE_EXISTING_CONFIRM, new CommandListener() {
				public void commandAction(Command c, Displayable d) {
					//OpenmrsDataStorage.deleteAllXforms();//TODO display alert that all forms would be deleted before tryinng downloading new forms
					//loadItems();
		
					RequestHeader reqh = RequestHeader.getRequestHeader(RequestHeader.ACTION_DOWNLOAD_FORMS, getMainMidlet().getCurrentXformUser().getName(), getMainMidlet().getCurrentXformUser().getClearTextPassword(), getMainMidlet().getLocale(), null);
					getMainMidlet().sendDownloadRequest(WebServerConstants.SUBURL_XFORM_FORM_DOWNLOAD, true, 
						createRequestPayload(XFORM_DOWNLOAD), false, reqh, new StudyDef(), curdisp, true);

					getMainMidlet().setDisplay(curdisp);
					//getMainMidlet().sendDownloadRequest(WebServerConstants.SUBURL_XFORM_FORM_DOWNLOAD, true, 
						//	createRequestPayload(XFORM_LIST_DOWNLOAD), false, null, new XformTempList(), curdisp, false);
				}
			}, new CommandListener() {
				public void commandAction(Command c, Displayable d) {
					getMainMidlet().setDisplay(curdisp);
				}
			}, getMainMidlet().getDisplay());
				
		}
	}

	protected ConnectionParameter createRequestPayload(int type) {
		ConnectionParameter payload = new ConnectionParameter();

		if(type == XFORM_LIST_DOWNLOAD){
			payload.addParam(WebServerConstants.PARAM_XFORM_TARGET, "xformslist");//TODO
			payload.addParam(WebServerConstants.PARAM_XFORM_EXCLUDE_LAYOUT, "true");
		}
		else if(type == XFORM_DOWNLOAD){
			payload.addParam(WebServerConstants.PARAM_XFORM_TARGET, "xforms");//TODO
			//payload.addParam(WebServerConstants.PARAM_XFORM_CONTENT_TYPE, "xml");
			payload.addParam(WebServerConstants.PARAM_XFORM_EXCLUDE_LAYOUT, "true");
		}
		return payload;
	}

	protected void cleanup() {
		// TODO Auto-generated method stub
		
	}

	public void response(Object otherInformation, Persistent persistentData) {
		final XformUIList curdisp = this;
		//OpenMRS doesnot maintain studies list. there is only one default study with id 1. so nowhere we are using studylisting things
		EpihandyDataStorage.saveStudy((StudyDef) persistentData);
	}

	public void error(Object errorInformation) {
		UIUtils.renderAlert(ErrorMessages.ERROR_SERVER_CONNECTION + "\nError Message is : "+errorInformation.toString(), null, this, getMainMidlet().getDisplay());
	}
}
