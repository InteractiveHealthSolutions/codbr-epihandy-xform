package minixpath;

import java.util.Vector;

import org.kxml2.kdom.*;

import java.util.*;
/**
 * @author Cosmin
 *
 */
//TODO: descendant axis doesn't work
//
public class XPathLocationStep 
{
	String axis = null;
	String nodeTest = null;
	String nodePrefix = null;
	String functionName = null;
	String predicate = null;
	
	private void parseLocationStep(String locationStep)
	{
		//todo: should check if the whole xpath expression
		//is parameter to a function
		//todo: optimizations -> next.toCharArray !!!
		String next = locationStep;
		int pattIndex = 0;

		axis = "";
		if(next.equals("//")) {
			nodeTest = "//";
			return;
		} else if(next.equals("/")) {
			nodeTest = "/";
			return;
		}

		//test if we have a relative path
		//for example: ../../../@zipcode
		//in this case the nodeTest will be null
		if(next.startsWith("..")) {
			axis = "parent";
			nodeTest = "..";
		} else if(next.startsWith(".")) {
			//don't know what axis to set here
			//child:: is probably incorrect
			nodeTest = ".";
		} else 
			//test if we have an axis
			if(next.indexOf("::") == -1)
				if(next.startsWith("@")) {
					axis = "attribute";
					next = new String(next.toCharArray(), 1, next.length()-1);
				} else
					axis = "child";
			else {
				pattIndex = next.indexOf("::");
				if(pattIndex != -1) {
					axis = new String(next.toCharArray(), 0, pattIndex);
					next = new String(next.toCharArray(), pattIndex+2, next.length()-pattIndex-2);
				}
			}
		
		pattIndex = next.indexOf("[");
		if(pattIndex != -1) {
			nodeTest = new String(next.toCharArray(), 0, pattIndex);
			next = new String(next.toCharArray(), pattIndex+1, next.length()-pattIndex-1);
			
			pattIndex = next.lastIndexOf(']');
			//pattIndex shouldn't be -1 in this case
			//maybe we should throw an exception??
			//for now assume that the expression is 
			//formed correctly
			predicate = new String(next.toCharArray(), 0, pattIndex);
		} else nodeTest = next;

		//test for prefix
		if((pattIndex = nodeTest.indexOf(":")) != -1) {
			nodePrefix = new String(nodeTest.toCharArray(), 0, pattIndex);
			nodeTest = new String(nodeTest.toCharArray(), pattIndex+1, next.length()-pattIndex-1);
		}
										
//		System.out.println("this partial location: "+locationStep+" is parsed into");
//		System.out.println("functionName="+functionName+" axis="+axis+" nodeTest="+nodeTest+" predicate="+predicate);
	}		
			
	public XPathLocationStep(String locationStep)
	{
		parseLocationStep(locationStep);	
	}//constructor

	/**the contextNodeSet is made of nodes
	* that are instances of Element
	* A fix is needed here: to the result vector I only add
	* Element-s or String. This is not correct. I should only add
	* Node-s
	*/
	public Vector getResult(Vector contextNodeSet)
	{
		Vector outputNodeSet = new Vector();
		int nodeCount = contextNodeSet.size();
		int i = 0;
		
		if(axis.equals("child") || axis.equals("descendant")) {
			for(i = 0; i < nodeCount; i++) {
				Node node = (Node)contextNodeSet.elementAt(i);
				int childCount = node.getChildCount();
				
				for(int j = 0; j < childCount; j++) {					
					if(node.getType(j) == Node.ELEMENT) {
						Node childNode = (Node)node.getChild(j);
						String childName = null;
						if ( childNode instanceof Element )
							childName = ((Element)childNode).getName();
						
						String prefix = null;
						if(nodePrefix != null) {
							Element element = (Element)childNode;
							prefix = getPrefix(element);
						}
						
						if(nodeTest.equals("*") ||
							nodeTest.equals(childName) ||
							nodeTest.equals("node()")) {
							if(((nodePrefix != null) && (nodePrefix.equals(prefix))) ||
								(nodePrefix == null))
								outputNodeSet.addElement(childNode);
						} else if(nodeTest.equals("text()")) {
							String text = getText(childNode);
							if ( text != null )
								outputNodeSet.addElement(text);
						}
					
						if(axis.equals("descendant")) {
							Vector descendants = null;
							descendants = getMatchingDescendants(childNode);

							for(int k = 0; k < descendants.size(); k++)
								outputNodeSet.addElement(descendants.elementAt(k));
						}
					} else if(node.getType(j) == Node.TEXT) {
						if(nodeTest.equals("text()"))
							outputNodeSet.addElement(node.getText(j));
					}
				}
			}
		}

		if(axis.equals("parent")) {
			for(i = 0; i < nodeCount; i++) {
				Node cn = (Node)contextNodeSet.elementAt(i);
				
				if(cn instanceof Element)
					outputNodeSet.addElement(((Element)cn).getParent());
			}
		}
		
		if(axis.equals("attribute")) {
			for(i = 0; i < nodeCount; i++) {
				Node n = (Node)contextNodeSet.elementAt(i);

				if(n instanceof Element) {
					String value = ((Element)n).getAttributeValue(nodePrefix, nodeTest);
					if(value != null)
						outputNodeSet.addElement(value);
				}
			}
		}
		
		//other axes go here
		
		//no axis whatsoever (or maybe unknown to me :)
		if(axis.equals("")) { 
			if(nodeTest.equals("/")) {
				Object startNode = null;
				//find first element in the contextNodeSet
				for(Enumeration en = contextNodeSet.elements(); en.hasMoreElements(); ) {
					startNode = en.nextElement();
					if(startNode instanceof Element)
						break;
				}
				
				if(startNode instanceof Element) {
					Node tmp = null;
					do {
						tmp = ((Element)startNode).getParent();
						if ( tmp != null && tmp instanceof Element )
							startNode = tmp;
						else
							break;
					} while( tmp != null );
					outputNodeSet.addElement(startNode);
				} else {
//					System.out.println("couldn't find root");
					//couldn't find any elements in context
					return contextNodeSet;
				}
			} else if (nodeTest.equals(".")) {
				//simply copy the input vector
				for(Enumeration enumeration = contextNodeSet.elements(); 
					enumeration.hasMoreElements(); )
					outputNodeSet.addElement(enumeration.nextElement());
			}
		}
		
		if(predicate != null) {
			Predicate predicateEvaluator = 
				new Predicate(outputNodeSet, predicate); 
			outputNodeSet = predicateEvaluator.getResult();
		}
		return outputNodeSet;
	}
		

	private Vector getMatchingDescendants(Node node)
	{
		Vector matchingDescendants = new Vector();
		int childCount = node.getChildCount();
		
		for(int j = 0; j < childCount; j++) {			
			//this is were we test if the
			//node test part of our xpath expression
			//matches this node
			if(node.getType(j) == Node.ELEMENT) {
				Node childNode = (Node)node.getChild(j);
				String name = ((Element)childNode).getName();
				if(nodeTest.equals("*") ||
					nodeTest.equals(name))
					matchingDescendants.addElement(node);
				
				Node[] moreDescendants = null;
				
				Vector tmp = getMatchingDescendants(childNode);
				
				moreDescendants = new Node[tmp.size()];
				tmp.copyInto(moreDescendants);
				tmp = null;
				
				for(int i = 0; i < moreDescendants.length; i++)
					matchingDescendants.addElement(moreDescendants[i]);
			}
		}
		return matchingDescendants;
	}
	
	private String getPrefix(Element element) {
		String n = element.getNamespace();
		String prefix = "";
		for ( int i = 0; i < element.getNamespaceCount(); ++i ) {
			if ( n.equals(element.getNamespaceUri(i)) ) {
				prefix = element.getNamespacePrefix(i);
			}
		}
		return prefix;
	}
	
	private String getText(Node node) {
		String result = null;
		for ( int i = 0; i < node.getChildCount(); ++i ) {
			if ( node.isText(i) )
				result += node.getText(i);
		}
		return result;
	}
}
