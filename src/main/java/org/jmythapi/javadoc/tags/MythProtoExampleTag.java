/*
 * Copyright (C) ${year} Martin Thelian
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, please email thelian@users.sourceforge.net
 */
package org.jmythapi.javadoc.tags;

import java.util.Map;

import org.jmythapi.protocol.IMythPacket;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * The javadoc tag {@code @mythProtoExample}.
 * <p>
 * This inline tag can be used to generate a table with an MythTV protocol example, e.g.
 * <pre>
 *    {@mythProtoExample
 *    10      QUERY_LOAD
 *    22      0.04[]:[]0.03[]:[]0.08
 *    }
 * </pre>
 */
public class MythProtoExampleTag  implements Taglet {    
    private static final String NAME = "mythProtoExample";
    
    public String getName() {
        return NAME;
    }
    
    public boolean inField() {
        return true;
    }

    public boolean inConstructor() {
        return true;
    }
    
    public boolean inMethod() {
        return true;
    }
    
    public boolean inOverview() {
        return true;
    }

    public boolean inPackage() {
        return true;
    }

    public boolean inType() {
        return true;
    }
    
    public boolean isInlineTag() {
        return true;
    }
    
    /**
     * Registers this taglet as custom javadoc tag.
     * 
     * @param tagletMap
     * 		a map the taglet should be registered to.
     */
    public static void register(Map<String,Object> tagletMap) {
       final MythProtoExampleTag tag = new MythProtoExampleTag();
       tagletMap.remove(tag.getName());
       tagletMap.put(tag.getName(), tag);
    }
    
    public String toString(Tag tag) {   	
    	if(tag == null) return null;
    	final String text = tag.text();
    	if(text == null || text.length()==0) return null;
    	
    	boolean noTitle = false;
    	
    	// splitting the text into multiple lines    	
    	final String[] lines = text.split("\r\n|\n");
    	if(lines.length > 0 && lines[0] != null && lines[0].length() > 0) {
    		if(lines[0].contains("notitle")) {
    			noTitle = true;
    		}
    	}
    	
    	// starting to render html
    	final StringBuilder buff = new StringBuilder();
    	buff.append("<table class='mythProtoExample'>");
    	if(!noTitle) {
    	    buff.append("<tr class='head'><td colspan='2'>MythTV protocol example:</td></tr>");
    	}
    	
    	for(String line: lines) {
    		if(line.length() < 8) continue;
    		line = line.trim();
    		
    		buff.append("<tr>");
    		if(line.matches("\\d+.*")) {
	    		final String msgLength = line.substring(0,8);
	    		String msgPayload = line.substring(8);
	    		msgPayload = msgPayload.replace(IMythPacket.DELIM,"<font color='grey'>" + IMythPacket.DELIM + "</font>");
	    		
	    		buff.append("<td valign='top' style='width:1%'><pre style='margin:0px;'>").append(msgLength).append("</td>")
	    			.append("<td><code>").append(msgPayload).append("</td>");
    		} else {
    			buff.append("<td colspan='2' class='subhead'>").append(line).append("</td>");
    		}
    		buff.append("</tr>");
    	}
    	
    	buff.append("</table>");
    	return buff.toString();
    }
    
    public String toString(Tag[] tags) {
    	if(tags == null) return null;
    	final StringBuilder buff = new StringBuilder();
    	for(Tag tag : tags) {
    		buff.append(toString(tag));
    	}
    	return buff.toString();
    }	   
}
