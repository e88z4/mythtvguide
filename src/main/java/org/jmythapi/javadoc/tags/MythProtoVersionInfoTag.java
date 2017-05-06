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

import static org.jmythapi.protocol.ProtocolVersionInfo.DATE;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.MYTH_RELEASE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jmythapi.javadoc.utils.JavadocUtils;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.ProtocolVersionRange;

import com.sun.javadoc.Doc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * The javadoc tag {@code @mythProtoVersionInfo}.
 * 
 */
public class MythProtoVersionInfoTag  implements Taglet {    
    public static final String NAME = "mythProtoVersionInfo";
    
    public String getName() {
        return NAME;
    }
    
    public boolean inField() {
        return true;
    }

    public boolean inConstructor() {
        return false;
    }
    
    public boolean inMethod() {
        return false;
    }
    
    public boolean inOverview() {
        return false;
    }

    public boolean inPackage() {
        return false;
    }

    public boolean inType() {
        return true;
    }
    
    public boolean isInlineTag() {
        return false;
    }
    
    /**
     * Registers this taglet as custom javadoc tag.
     * 
     * @param tagletMap
     * 		a map the taglet should be registered to.
     */    
    public static void register(Map<String,Object> tagletMap) {
       final MythProtoVersionInfoTag tag = new MythProtoVersionInfoTag();
       tagletMap.remove(tag.getName());
       tagletMap.put(tag.getName(), tag);
    }
    
    public String toString(Tag tag) {
    	if(tag == null) return null;

    	try {
	    	// determine this is the proper enum 
	    	final Doc doc = tag.holder();
	    	if(!doc.isEnumConstant()) return null;
	    	
	    	// getting the protocol version
	    	final String propName = doc.name();
	    	final ProtocolVersion protoVersion = ProtocolVersion.valueOf(propName);
	    	final Map<String,Set<String>> metadata = accumulateMetadata(protoVersion, doc);
	    	if(metadata == null || metadata.isEmpty()) return null;
	    	
	    	// write out the meta data
	    	final StringBuilder buff = new StringBuilder();
	    	buff.append(
		        "<dt>" +
	    			"<B>MythTV Protocol Version Info:</B> " +  
	    		"</dt>"
    		);
	    	
	    	buff.append("<dd><table class='mythProtoMetadata'>");	    	
			for(Entry<String,Set<String>> entry : metadata.entrySet()) {
				final String key = entry.getKey();
				final Set<String> values = entry.getValue();
				if(values == null || values.size() == 0) continue;
				
				String htmlValue = "";
				
				if (key.equals(DATE)) {
					continue;
				} else if (key.equals(GIT_COMMIT)) {
					int idx = 0;
					for(String value : values) {
						htmlValue += "<a " +
							"href='" + JavadocUtils.GIT_COMMIT_URL + value + "' " +
							"title='View Changelog' " + 
							"target='_blank'>" + value + "</a>";
						if(idx<values.size()-1) htmlValue += " | ";
						idx++;
					}
				} else if (key.equals(MYTH_RELEASE)) {
					final String value = values.iterator().next();
					htmlValue = "<a " +
						"href='" + JavadocUtils.MYTHTV_WIKI_URL + value + "' " +
						"title='View Release Notes' " + 
						"target='_blank'>" + value + "</a> ";
				} else {
					int idx = 0;
					for(String value : values) {
						htmlValue += value;
						if(idx<values.size()-1) htmlValue += " | ";
						idx++;
					}
				}
				
				buff.append(
					"<tr>" +
						"<td class='metaDataName'>" + key + "</td>" +
						"<td class='metaDataValue'>" + htmlValue + "</td>" +
					"</dd>"
				);
			}
			buff.append("</table></dd>");
	    	
	    	return buff.toString();
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
    
    private Map<String,Set<String>> accumulateMetadata(ProtocolVersion protoVersion, Doc doc) {
    	final Map<String,Set<String>> allMetaData = new HashMap<String, Set<String>>();
    	
    	// add the protocol token
    	final String protoToken = protoVersion.getToken();
    	if(protoToken != null) {
    		allMetaData.put("Protocol Token",new HashSet<String>(Arrays.asList(new String[]{protoToken})));
    	}
    	
    	// add the protocol version metadata to the map
    	final Map<String,String> protoMetaData = protoVersion.getMetaData();
    	addToMap(protoMetaData, allMetaData);
    	
    	// find links to commands or elements
    	final Tag[] links = doc.inlineTags();
    	if(links != null && links.length > 0) {	    	
	    	for(Tag link : links) {
    			final String tagName = link.name();
    			if(!tagName.equals("@link")) continue;	    		
	    		
	    		// trying to find the protocol-version-range for the linked element
	    		final ProtocolVersionRange protoRange = JavadocUtils.getProtocolVersionRange(((SeeTag)link).referencedMember(),false);
	    		if(protoRange == null) continue;
	    		
	    		if(protoVersion.equals(protoRange.from())) {
	    			addToMap(protoRange.fromInfo(),allMetaData);
	    		} 
	    		if(protoVersion.equals(protoRange.to())) {
	    			addToMap(protoRange.toInfo(),allMetaData);
	    		}
	    	}
    	}
    	
    	return allMetaData;
    }
    
    private void addToMap(Map<String,String> map, Map<String,Set<String>> multiMap) {
    	if(map == null) return;
    	
		for(Entry<String,String> entry : map.entrySet()) {
			final String key = entry.getKey();
			final String[] valueParts = entry.getValue().split(",");
			Set<String> values = null;
			
			if(multiMap.containsKey(key)) {
				values = multiMap.get(key);
			} else {
				values = new LinkedHashSet<String>();
				multiMap.put(key,values);
			}
			
			for(String valuePart : valueParts) {
				values.add(valuePart.trim());
			}
		}  	
    }
}
