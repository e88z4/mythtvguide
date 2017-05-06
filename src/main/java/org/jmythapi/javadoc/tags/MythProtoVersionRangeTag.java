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

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_LATEST;
import static org.jmythapi.protocol.ProtocolVersionInfo.DATE;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.MYTH_RELEASE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jmythapi.javadoc.utils.JavadocUtils;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.ProtocolVersionRange;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * The javadoc tag {@code @mythProtoVersionRange}.
 * <p>
 * This outline tag can be used to include a table into the javadoc of an element which 
 * displays the protocol version range of the element and some metadata about the upper and
 * lower value of the range.
 */
public class MythProtoVersionRangeTag implements Taglet {    
	private static final String VERSION_FALLBACK_FROM = "Fallback";
    private static final String VERSION_REMOVED = "Removed";
	private static final String VERSION_CHANGED = "Changed";
	private static final String VERSION_ADDED = "Added";
	private static final String VERSION_FALLBACK_TO = "Fallback";
	
	private static final String NAME = "mythProtoVersionRange";
    
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
        return false;
    }
    
    /**
     * Registers this taglet as custom javadoc tag.
     * 
     * @param tagletMap
     * 		a map the taglet should be registered to.
     * @throws Throwable 
     * 		on errors
     */    
    public static void register(Map<String,Object> tagletMap) throws Throwable {
    	try {
    		final MythProtoVersionRangeTag tag = new MythProtoVersionRangeTag();
    		tagletMap.remove(tag.getName());
    		tagletMap.put(tag.getName(), tag);
    	} catch (Throwable e) {
    		e.printStackTrace();
    		throw e;
    	}
    }
    
    public String toString(Tag tag) { 
    	try {
	    	if(tag == null) return null;
	    	
	    	// getting the protocol version link prefix
	    	final String versionLinkPrefix = JavadocUtils.getProtocolVersionLinkPrefix(tag);
	    	
	    	// determine the version range
	    	final ProtocolVersionRange range = JavadocUtils.getProtocolVersionRange(tag, true);
	    	    	
	    	// determine the documentation type
    		final Doc tagHolder = tag.holder();
    		String tagType = "";
    		
    		if(tagHolder != null) {
    			if(tagHolder instanceof MethodDoc) {
    				tagType = "function";
    			} else if (tagHolder instanceof ClassDoc) {
    				tagType = "object";
    			} else {
    				tagType = "element";
    			}
    		}	    	
	    	
	    	// print the range header    		
	    	final StringBuilder buff = new StringBuilder();
	    	buff.append(String.format(
	    		"<dt>" +
	    			"<B>MythTV protocol range:</B> " +
	    			"<span class='protocolRange'>" +
	    				"[<a class='version' href='%s'>%02d</a>,<a class='version' href='%s'>%02d</a>)" + 
	    		"</dt>\n",
	    		versionLinkPrefix + range.from().name(),
	    		range.from().getVersion(),
	    		versionLinkPrefix + range.to().name(),
	    		range.to().getVersion()
	    	));
	    	
			// RANGE TABLE START
			buff.append(
				"<dd>" +
					"<table class='mythProtoRange'>" +
						"<tr class='head'>" + 
							"<th>&nbsp;</th>" +
							"<th>Version</th>" +
							"<th>Date</th>" +
							"<th>Additional Version Info</th>" +
						"</tr>"
			);
			
			// check for from fallback
			if(range.fromFallback() != null) {
				final String commentText = String.format(
					"The %s can be can be used with restrictions starting with version %d.",
					tagType, range.fromFallback().getVersion()
				);
				this.appendDetails(VERSION_FALLBACK_FROM, versionLinkPrefix,"rangeFallbackFrom", range.fromFallback(), buff, false, commentText);
			}
			
			// print the lower version		
			this.appendDetails(VERSION_ADDED, versionLinkPrefix,"rangeFrom", range.from(), buff, true, null);
			
			// print version where the method has changed
			final Map<ProtocolVersion,List<Tag>> changedVersions = this.getChangedVersions(range, tag);
			if(changedVersions != null && !changedVersions.isEmpty()) {
				for(Entry<ProtocolVersion,List<Tag>> changedEntry: changedVersions.entrySet()) {
					this.appendDetails(VERSION_CHANGED, versionLinkPrefix, "rangeChanged", changedEntry.getKey(), buff, true, null);
				}
			}
			
			// print the upper version
			this.appendDetails(VERSION_REMOVED, versionLinkPrefix, range.to().equals(PROTO_VERSION_LATEST)?"rangeToLatest":"rangeTo", range.to(), buff, true, null);
			
			// check for from fallback
			if(range.toFallback() != null) {
				final String commentText = String.format(
					"The %s can be can be used with restrictions till version %d.",
					tagType, range.toFallback().getPredecessor().getVersion()
				);				
				this.appendDetails(VERSION_FALLBACK_TO, versionLinkPrefix,"rangeFallbackTo", range.toFallback(), buff, false, commentText);
			}			
			
			// RANGE TABLE END
			buff.append(
					"</table>" +
				"</dd>"
			);
			
	    	return buff.toString();
    	} catch(Throwable e) {
    		e.printStackTrace();
    		return "";
    	}
    }
    
    private Map<ProtocolVersion,List<Tag>> getChangedVersions(ProtocolVersionRange range, Tag tag) {
    	if(tag == null) return Collections.emptyMap();
    	final Doc doc = tag.holder();
    	if(doc == null) return Collections.emptyMap();;
    	
    	final EnumMap<ProtocolVersion,List<Tag>> versionMap = new EnumMap<ProtocolVersion,List<Tag>>(ProtocolVersion.class);

    	// collect inline tags
    	final Tag[] docInlineTags = doc.inlineTags();
    	this.collectProtoVersions(range, versionMap,tag,docInlineTags);
    	
    	// collect return tag
    	final Tag[] returnTags = doc.tags("return");
    	if(returnTags != null && returnTags.length > 0) {
	    	for(Tag returnTag : returnTags) {
	    		final Tag[] inlineTags = returnTag.inlineTags();
	    		this.collectProtoVersions(range, versionMap, returnTag, inlineTags);
	    	}    		
    	}
    	
    	// collect param tags
    	final Tag[] paramTags = doc.tags("param");
    	if(paramTags != null && paramTags.length > 0) {
	    	for(Tag paramTag : paramTags) {
	    		final Tag[] inlineTags = paramTag.inlineTags();
	    		this.collectProtoVersions(range, versionMap, paramTag, inlineTags);
	    	}
    	}
    	return versionMap;
    }
    
    private void collectProtoVersions(ProtocolVersionRange range, Map<ProtocolVersion,List<Tag>> versionMap,Tag parentTag, Tag... inlineTags) {
		if(inlineTags == null || inlineTags.length == 0) return;
		
		for(Tag inlineTag : inlineTags) {
			final String tagName = inlineTag.name();
			if(tagName.equals("@" + MythProtoVersionTag.NAME)) {
				final ProtoVersionInfoHolder protoVersionInfoHolder = MythProtoVersionTag.getProtoVersionInfo(inlineTag);
				final ProtocolVersion paramVersion = protoVersionInfoHolder.getVersionEnum();
				if(paramVersion == null) continue;
				else if(paramVersion.equals(range.from())) continue;
				else if(paramVersion.equals(range.to())) continue;
				else if(!range.isInRange(paramVersion)) continue;
				
				List<Tag> changedTags = null;    					
				if(versionMap.containsKey(paramVersion)) {
					changedTags = versionMap.get(paramVersion);
				} else {
					changedTags = new ArrayList<Tag>();
					versionMap.put(paramVersion,changedTags);
				}
				changedTags.add(parentTag);
				
			}
		}    	
    }
    
    private void appendDetails(String label, String versionLinkPrefix, String cssClass, ProtocolVersion version, StringBuilder buff, boolean addMetadata, String comment) {
		buff.append("<tr " + (cssClass!=null?"class='" + cssClass + "'":"") + ">")
			.append("<th class='label'>").append(label).append("</th>")
			.append(String.format(
				"<td class='version'><a href='%s'>%02d</a></td>",
				versionLinkPrefix + version.name(),
				version.getVersion()
			));
		
		String date = "&nbsp;";
		StringBuffer additional = new StringBuffer();

		// extracting the version date
		
		
		if(comment != null && comment.length() > 0) {
			additional.append(comment).append(" ");
		}
		
		final Map<String,String> metadata = version.getMetaData();
		if(metadata != null && !metadata.isEmpty()) {
			if(metadata.containsKey(DATE)) {
				date = metadata.get(DATE);
			}
			
			if(addMetadata) {
				if(additional.length() > 0) additional.append("| ");
				
				for(Entry<String,String> entry : metadata.entrySet()) {
					final String key = entry.getKey();
					final String[] valueParts = entry.getValue().split(",");
					
					for(String valuePart : valueParts) {
						if(key.equals(DATE)) continue;
						
						if (key.equals(GIT_COMMIT)) {
							additional.append("<a href='" + JavadocUtils.GIT_COMMIT_URL + valuePart + "' target='_blank'>Changelog</a>, ");
						} else if (key.equals(MYTH_RELEASE)) {
							additional.append(key + ": <a href='" + JavadocUtils.MYTHTV_WIKI_URL + valuePart + "' target='_blank'>" + valuePart + "</a>, ");
						} else {
							additional.append(key).append(": ").append(valuePart).append(", ");
						}
					}
				}
			}
			if(additional.toString().endsWith(", ") || additional.toString().endsWith("| ")) {
				additional.setLength(additional.length()-2);
			}
		}
		
		buff.append("<td class='date'>").append(date).append("</td>")
			.append("<td class='others'>").append(additional).append("</td>")
			.append("</tr>\n");
    }
    
    public String toString(Tag[] tags) {
        if (tags.length == 0) {
            return null;
        }
        
        // getting just the first tag
    	final Tag tag = tags[0];
    	return toString(tag);
    }
    

    

} 