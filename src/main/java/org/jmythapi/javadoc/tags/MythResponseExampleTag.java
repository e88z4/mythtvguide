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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * The javadoc tag {@code @mythCodeExample}.
 * <p>
 * This inline tag can be used to highlight source code fragments.
 */
public class MythResponseExampleTag  implements Taglet {    
    public static final String NAME = "mythResponseExample";
    
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
       final MythResponseExampleTag tag = new MythResponseExampleTag();
       tagletMap.remove(tag.getName());
       tagletMap.put(tag.getName(), tag);
    }
    
    public String toString(Tag tag) {   	
    	if(tag == null) return null;

    	try {
        	String lines = tag.text();
        	if(lines == null) return null;    		
    		
        	lines = lines.replaceAll("<br>", "");
        	lines = lines.replaceAll("<br/>", "");
        	lines = lines.replaceAll("<pre>", "");
        	lines = lines.replaceAll("</pre>", "");
        	lines = lines.trim();

	    	final Pattern propPattern = Pattern.compile("(<\\d+>)(\\w+):\\s*([^|]*)(\\s*\\|\\s|$)");
        	final StringBuilder buff = new StringBuilder("<div class='codeExample'>");
        	
        	String matchType = "";
        	final String[] texts = lines.split("\r\n|\n");
        	for(int j=0; j<texts.length; j++) {
        		final String text = texts[j].trim();
        		if(text.length()==0) continue;
        		
        		// separate multiple lines
        		if(j > 0 && !"text".equals(matchType)) {
        			buff.append("<hr/>");
        		}
        		
        		// try to highlight as AMythResponse.toString() output
        		int matchCount = 0;
		    	final Matcher propMatcher = propPattern.matcher(text);
		    	while(propMatcher.find()) {
		    		if(matchCount == 0) {
		    			buff.append("<code>");
		    		}
		    		
		    		matchCount++;
		    		final String pos = propMatcher.group(1);
		    		final String name = propMatcher.group(2);	    		
		    		final String end = propMatcher.group(4).trim();
		    		String value = propMatcher.group(3).trim();
		    		value = this.highlightFlags(value);
		    		
		    		buff.append("<nobr>");
		    		buff.append("<span class='literal'>").append(pos).append("</span>");
		    		buff.append("<span class='reservedWord'>").append(name).append(":</span> ");
		    		buff.append("<span class='identifier'>").append(value).append("</span>");
		    		buff.append("<span class='separator'> ").append(end).append(" </span>");
		    		buff.append("</nobr> ");
		    	}
		    	if(matchCount > 0) {
		    		matchType = "msg";
		    		buff.append("</code>");
		    	}
		    
		    	// no match found
		    	if(matchCount == 0) {
		    		// try to match a flag group
		    		if(text.matches("[-]?\\d+.*")) {
		    			matchType = "flag";
			    		final String value = this.highlightFlags(text);
			    		if(value != null) {
			    			buff.append("<code>").append(value).append("</code>");
			    		}
		    		} 
		    		
		    		// seems to be a comment
		    		else {
		    			matchType = "text";
		    			buff.append("<div class='codeExampleComment'>").append(text).append("</div>");
		    		}
		    	}
        	}
        	
	    	buff.append("</code></div>");
	    	return buff.toString();
    	} catch (Throwable e) {
    		return tag.text();
    	}
    }

    private String highlightFlags(String value) {
    	final Pattern flagPattern = Pattern.compile("([-]?\\d+)=>\\s\\{([^}]+)\\}");
		final Matcher flagMatcher = flagPattern.matcher(value);
		if(flagMatcher.matches()) {
			final String intValue = flagMatcher.group(1);
			final String[] flags = flagMatcher.group(2).split(",");
			
			final StringBuilder flagBuff = new StringBuilder();
			flagBuff.append(intValue).append("=> {");
			for(int i=0; i < flags.length; i++) {
				flagBuff.append("<span class='flagValue'>").append(flags[i]).append("</span>");
				if(i<flags.length-1) flagBuff.append(",");
			}
			flagBuff.append("}");
			value = flagBuff.toString();
		}
		return value;
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
