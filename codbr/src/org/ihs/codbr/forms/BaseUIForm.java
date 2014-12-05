package org.ihs.codbr.forms;

import java.util.Vector;

import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import org.fcitmuk.communication.ConnectionParameter;
import org.ihs.codbr.MainMidlet;

public abstract class BaseUIForm extends Form implements CommandListener
 {
	private MainMidlet 	mainMidlet;
	private Displayable prevDisplayable;
	private String 		startDate;
	private String 		startTime;
	private String 		endTime;
	private Vector 		items;
	
	public Displayable getPrevDisplayable ()
	{
		return prevDisplayable;
	}

	public void setPrevDisplayable (Displayable prvDisplayable)
	{
		this.prevDisplayable = prvDisplayable;
	}

	public BaseUIForm (String title, MainMidlet mainMidlet)
	{
		super (title);
		this.setMainMidlet(mainMidlet);
		items = new Vector();
	}

	public void setMainMidlet(MainMidlet mainMidlet) {
		this.mainMidlet = mainMidlet;
	}

	public MainMidlet getMainMidlet() {
		return mainMidlet;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getEndTime() {
		return endTime;
	}
	
	public int addFormElement(Item item) {
		items.addElement(item);
		return super.append(item);
	}
	public void deleteAll() {
		items.removeAllElements();
		super.deleteAll();
	}
	/**
	 * Returns the index of an item in the formItems array. Returns -1 if item
	 * is not found
	 * 
	 * @param item
	 * @return
	 */
	protected int indexOf (Item item)
	{
		for (int i = 0; i < items.size(); i++)
			if (items.elementAt(i) == item)
				return i;
		return -1;
	}
	
	protected boolean isShown(Item item){
		for (int i = 0; i < size(); i++) {
			if(get(i) == item){
				return true;
			}
		}
		return false;
	}

	/**
	 * Shows an item on the form. If another item is already existing on the
	 * Item's index, it is replaced
	 * 
	 * @param item
	 */
	protected void show (Item item)
	{
		int i = indexOf (item);
		delete (i);
		insert (i, item);
	}

	/**
	 * Hides an item on the form by replacing it with empty String Item
	 * 
	 * @param item
	 */
	protected void hide (Item item)
	{
		int i = indexOf (item);
		delete (i);
		StringItem tmp = new StringItem ("", "");
		insert (i, tmp);
	}
	protected abstract ConnectionParameter createRequestPayload();
	
	public abstract void init(Displayable prvDisplayable);
	
	protected abstract boolean validate();
	
	protected abstract void cleanup();
	
}
