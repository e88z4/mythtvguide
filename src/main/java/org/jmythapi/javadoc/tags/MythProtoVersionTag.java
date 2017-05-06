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

import java.util.HashMap;
import java.util.Map;

import org.jmythapi.javadoc.utils.JavadocUtils;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.ProtocolVersionRange;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * The javadoc tag {@code @mythProtoVersion}.
 * <p>
 * This inline tag can be used to link to a given MythTV protocol-version, e.g.
 * <pre>
 *    &#064;since {&#064;mythProtoVersion 42}
 * </pre>
 */
public class MythProtoVersionTag  implements Taglet {    
    private static final String FALLBACK_FROM = "fromFallback";
    private static final String FALLBACK_TO = "toFallback";
	public static final String NAME = "mythProtoVersion";
    
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
       final MythProtoVersionTag tag = new MythProtoVersionTag();
       tagletMap.remove(tag.getName());
       tagletMap.put(tag.getName(), tag);
    }
    
    public String generateVersionHref(Tag tag, ProtoVersionInfoHolder versionInfo) {
    	// generate the link
    	final String versionLink = JavadocUtils.getProtocolVersionLink(tag, versionInfo.getVersionEnum());
    	if(versionLink == null) return null;
    	
    	// generate title	
    	final String versionTitle = JavadocUtils.getProtocolVersionInfoTitle(versionInfo.getVersionEnum());
    	
    	// generate the href
    	return String.format(
    		"<a class='version' title='%s' href='%s'>%02d</a>",
    		versionTitle,
    		versionLink,
    		versionInfo.getVersionEnum().getVersion()
    	);
    }
    
    public String toString(Tag tag) {   	
    	if(tag == null) return null;

    	// determine the javadoc type
    	String docType = null;
    	
    	final Doc tagHolder = tag.holder();    	
		if(tagHolder != null) {
			if(tagHolder instanceof MethodDoc) {
				docType = "function";
			} else if (tagHolder instanceof ClassDoc) {
				docType = "object";
			} else {
				docType = "element";
			}
		}    	
    	
    	// determine the annotated version
    	final ProtoVersionInfoHolder versionInfo = getProtoVersionInfo(tag);
    	if(versionInfo == null) return null;
    	
    	// starting to render html
    	final StringBuilder buff = new StringBuilder("<span>");
    	buff.append(generateVersionHref(tag, versionInfo));
    	
    	// check for additional infos
    	final Map<String,String> additionalVersionInfo = versionInfo.getAdditionalInfos();
    	if(additionalVersionInfo.containsKey(FALLBACK_FROM)) {
    		final String fallbackVersion = additionalVersionInfo.get(FALLBACK_FROM);
    		final ProtoVersionInfoHolder fallbackVersionInfo = getProtoVersion(fallbackVersion,tag);
    		    		
    		buff.append("<span class='fallbackFrom' title='");
    		buff.append(String.format(
    			"The %s can be used with restrictions starting with version %d.",
    			docType,
    			fallbackVersionInfo.getVersionEnum().getVersion()
    		));    		
    		buff.append("'>");
    		buff.append(" (fallback-from: ");
    		buff.append(generateVersionHref(tag, fallbackVersionInfo));
    		buff.append(")");
    		buff.append("</span>");
    	} else	if(additionalVersionInfo.containsKey(FALLBACK_TO)) {
    		final String fallbackVersion = additionalVersionInfo.get(FALLBACK_TO);
    		final ProtoVersionInfoHolder fallbackVersionInfo = getProtoVersion(fallbackVersion,tag);
    		
    		buff.append("<span class='fallbackTo' title='");
    		buff.append(String.format(
    			"The %s can be used with restrictions till version %d.",
    			docType,
    			fallbackVersionInfo.getVersionEnum().getPredecessor().getVersion()
    		));    		
    		buff.append("'>");
    		buff.append(" (fallback-to: ");
    		buff.append(generateVersionHref(tag, fallbackVersionInfo));
    		buff.append(")");
    		buff.append("</span>");
    	}
    	buff.append("</span>");
    	
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
    
    public static ProtoVersionInfoHolder getProtoVersionInfo(Tag tag) {
    	// determine the requested version
    	String text = tag.text();
    	if(text == null) return null;    	
    	return getProtoVersion(text,tag);
    }
    	
    public static ProtoVersionInfoHolder getProtoVersion(String text, Tag tag) {
      	if(text == null) return null;    	
    	
    	final Map<String,String> additionalInfos = new HashMap<String, String>();
    	String additionalText = "";    	
    	ProtocolVersion version = null;
    	
    	int idx = text.indexOf(' ');
    	if(idx != -1) {
    		additionalText = text.substring(idx+1).trim();    		
    		text = text.substring(0,idx);
    		
    		// test if any fallback version is given
    		if(additionalText.startsWith(FALLBACK_FROM + "=")) {
    			idx = additionalText.indexOf('=');
    			final String fallbackVersionNr = additionalText.substring(idx+1).trim();
    			additionalInfos.put(FALLBACK_FROM,fallbackVersionNr);
    		} else if(additionalText.startsWith(FALLBACK_TO + "=")) {
    			idx = additionalText.indexOf('=');
    			final String fallbackVersionNr = additionalText.substring(idx+1).trim();
    			additionalInfos.put(FALLBACK_TO,fallbackVersionNr);
    		}
    	}
    	
    	// read the version number from the annotation
    	if(tag != null && text.contains("from") || text.contains("to")) {
        	// determine the version range
        	ProtocolVersionRange range = JavadocUtils.getProtocolVersionRange(tag, false);
        	if(range == null) return null;
        	
        	if(text.contains("from")) {
        		version = range.from();
        	} else if (text.contains("to")) {
        		version = range.to();
        	}
    	} 
    	
    	// the tag contains a reference to the enum
    	else if(text.matches("PROTO_VERSION_\\d+")) {
    		version = ProtocolVersion.valueOf(text);
    	} 
    	
    	// the tag contains just a number
    	else if(text.matches("[-]?\\d+")) {
    		version = ProtocolVersion.valueOf(Integer.valueOf(text));
    	} else {
    		// unknown content
    		return null;
    	}
    	
    	return new ProtoVersionInfoHolder(version,additionalInfos,"");
    }
}
