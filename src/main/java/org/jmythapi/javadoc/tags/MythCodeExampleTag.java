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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import com.Ostermiller.Syntax.ToHTML;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * The javadoc tag {@code @mythCodeExample}.
 * <p>
 * This inline tag can be used to highlight source code fragments.
 */
public class MythCodeExampleTag  implements Taglet {    
    public static final String NAME = "mythCodeExample";
    
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
       final MythCodeExampleTag tag = new MythCodeExampleTag();
       tagletMap.remove(tag.getName());
       tagletMap.put(tag.getName(), tag);
    }
    
    public String toString(Tag tag) {   	
    	if(tag == null) return null;

    	String text = tag.text();
    	if(text == null) return null;
    	
    	// replace some strings
    	text = text.replaceAll("&#47;","/");
    	text = text.replaceAll("&#064;","@");
    	text = text.replaceAll("&#123;","{");
    	text = text.replaceAll("&#125;","}");
    	text = text.replaceAll("&#60;","<");
    	text = text.replaceAll("&#62;",">");
    	text = text.replaceAll("&lt;","<");
    	text = text.replaceAll("&gt;",">");
    	text = text.replaceAll("<pre>","");
    	text = text.replaceAll("</pre>","");
    	
    	StringWriter out = new StringWriter();
    	try {
    		ToHTML toHtml = new ToHTML();
    		toHtml.setInput(new StringReader(text));
    		toHtml.setOutput(out);
    		toHtml.setMimeType("text/x-java");
    		toHtml.setFileExt("java");
    		toHtml.writeHTMLFragment();
    		return "<div class='codeExample'>" + out.toString() + "</div>";
    	} catch (Throwable e) {
    		return null;
    	}
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
