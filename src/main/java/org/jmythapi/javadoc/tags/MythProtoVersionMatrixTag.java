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

import java.util.EnumSet;
import java.util.Map;

import org.jmythapi.IVersionable;
import org.jmythapi.javadoc.utils.JavadocUtils;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.ProtocolVersionRange;
import org.jmythapi.protocol.response.IFlag;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.Taglet;

/**
 * The javadoc tag {@code @mythProtoVersionMatrix}.
 * <p>
 * This inline tag can be used for enums to display a table showing which
 * enum property is supported in which protocol version.
 */
public class MythProtoVersionMatrixTag  implements Taglet {    
    private static final String NAME = "mythProtoVersionMatrix";
    
    public String getName() {
        return NAME;
    }
    
    public boolean inField() {
        return false;
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
        return true;
    }
    
    /**
     * Registers this taglet as custom javadoc tag.
     * 
     * @param tagletMap
     * 		a map the taglet should be registered to.
     */    
    public static void register(Map<String,Object> tagletMap) {
		final MythProtoVersionMatrixTag tag = new MythProtoVersionMatrixTag();
		tagletMap.remove(tag.getName());
		tagletMap.put(tag.getName(), tag);
    }
    
	public String toString(Tag tag) {
    	try {
	    	if(tag == null) return null;
	
	    	// determine it shis is an enum class<E extends Enum<E>> 
	    	final Doc doc = tag.holder();
	    	if(!doc.isEnum()) return null;
	    	else if(!(doc instanceof ClassDoc)) return null;
	    	
	    	// we only accept enums
	    	final Type superClassType = ((ClassDoc)doc).superclassType();
	    	if(!superClassType.qualifiedTypeName().equals(Enum.class.getName())) return null;
	    	
	    	// getting the enum name
	    	// final ParameterizedType parameterizedType = superClassType.asParameterizedType();
	    	// final Type[] typeAargs = parameterizedType.typeArguments();
	    	// final String typeName = typeAargs[0].qualifiedTypeName();
	    	
	    	// getting the enum properties
	    	final FieldDoc[] enumProps = ((ClassDoc)doc).enumConstants();
	    	if(enumProps == null || enumProps.length == 0) return null;

	    	// check if the enum implements some of our interfaces
	    	boolean isFlag = false;
	    	final ClassDoc[] enumInterfaces = ((ClassDoc)doc).interfaces();
	    	if(enumInterfaces != null) {
				for(ClassDoc enumInterface : enumInterfaces) {
					if(enumInterface.qualifiedTypeName().equals(IFlag.class.getName())) {
						isFlag = true;						
					}
				}
	    	}
	    	
	    	// determine the version range of the whole enum
	    	final ProtocolVersionRange enumVersionRange = JavadocUtils.getProtocolVersionRange(doc,true);	    	
	    	
	    	// collecting all used protocol versions
	    	final EnumSet<ProtocolVersion> protocolVersions = EnumSet.noneOf(ProtocolVersion.class);
	    	for(FieldDoc prop : enumProps) {
	    		ProtocolVersionRange range = JavadocUtils.getProtocolVersionRange(prop,true);
	    		range = range.restrictRange(enumVersionRange);
	    		
	    		final ProtocolVersion from = range.from();
	    		final ProtocolVersion to = range.to();
	    		
	    		protocolVersions.add(from);
    			if(to.getPredecessor() != null && !to.equals(PROTO_VERSION_LATEST)){
    				protocolVersions.add(to.getPredecessor());
    			}
				protocolVersions.add(to);
	    	}
	    	
	    	// generating the table header
	    	final int additionalCols = 3;
	    	final StringBuilder buff = new StringBuilder();
	    	buff.append(String.format(
	    		"<table class='mythProtoMatrix' cellspacing='0' cellpadding='0'>\r\n" + 
	    			"<tr class='matrixTitle'>" +
	    				"<th colspan='" + Integer.toString(protocolVersions.size()+additionalCols) + "'>" +
		        			"MythTV Protocol Version Matrix" + 
		        		"</th>" +
		        	"</tr>\r\n"
		    ));
		        
	    	// the protocol version line
	    	buff.append(
	    		"<tr class='subHead'>" +
	    			"<th class='head' colspan='" + (additionalCols-1) + "'>&nbsp;</th>" +
	    			"<th class='head' colspan='" + (protocolVersions.size()+1) + "'>Protocol</th>" + 
	    		"</tr>" + 
	    		"<tr class='subHead'>" + 
	    			"<th class='head'>Name</th>" +
	    			"<th class='head'>Type</th>" +
	    			"<th class='head'>Range</th>"	    			
	    	);
			for (ProtocolVersion protoVersion : protocolVersions) {
				final String protoVersionLink = JavadocUtils.getProtocolVersionLink(tag,protoVersion);
				final String protoVersionTitle = JavadocUtils.getProtocolVersionInfoTitle(protoVersion);
				buff.append(
					"<th class='matrixVersion'>" + 
						String.format("<a href='%s' title='%s'>%02d</a>",protoVersionLink,protoVersionTitle,protoVersion.getVersion()) + 
					"</th>"
				);
			}
			buff.append(
				"</tr>\r\n"
			);	    	
	    	
			// one line for each constant
			for(FieldDoc prop : enumProps) {
				// the name of the property
				final String propName = prop.name();
				// final String propFullName = typeName + "." + propName;
				final StringBuilder propDescr = new StringBuilder();
				
				// extract the description
				final Tag[] firstSentence = prop.firstSentenceTags();
				if(firstSentence != null) {
					for(Tag firstSentencePart : firstSentence) {
						String textPart = firstSentencePart.text();
						if(textPart != null) {
							textPart = textPart.replaceAll("(\r)?\n","");
							textPart = textPart.replaceAll("\\s+"," ");
							propDescr.append(textPart);
						}
					}
				}
				
				// the link to the property documentation
				final String propLink = "#" + propName;
				
				// the property protocol version range
				ProtocolVersionRange propVersionRange = JavadocUtils.getProtocolVersionRange(prop,true);	
				propVersionRange = propVersionRange.restrictRange(enumVersionRange);
				
				// determine the data type of the class
				String propTypeFullName = "java.lang.String";
				String propTypeName = "String";
				final ClassDoc propType = JavadocUtils.getEnumDataType(prop);
				if(propType != null) {
					propTypeFullName = propType.qualifiedTypeName();
					propTypeName = propType.typeName();
				} else if (isFlag) {
					propTypeFullName = IFlag.class.getName();
					propTypeName = IFlag.class.getSimpleName();
				}
				
				if(!propTypeFullName.startsWith("java.")) {
					// is there any nice interface?				
					final ClassDoc[] propTypeInterfaces = propType==null?null:propType.interfaces();
					if(propTypeInterfaces != null) {
						for(ClassDoc propTypeInterface : propTypeInterfaces) {
							if(propTypeInterface.qualifiedTypeName().equals(IVersionable.class.getName())) continue;
							propTypeName = propTypeInterface.typeName();
						}
					}
				}
				
				// print result
				buff.append("<tr class='propInfo'><th class='propName'>" + String.format("<a class='propLink' href='%s' title='%s'>%s</a>",propLink,propDescr,propName) + "</th>")
				    .append("<td class='propType' title='" + propTypeFullName + "'>" + propTypeName + "</td>")
				    .append("<td class='propRange'>" + propVersionRange + "</td>");
				for (ProtocolVersion protoVersion : protocolVersions) {
					boolean inRange = propVersionRange.isInRange(protoVersion);
					buff.append(String.format("<td class='propInRange %s'>",inRange?"propAvailable":"propMissing"));
					buff.append((inRange)?"X":"-").append("</td>");
				}
				buff.append("</tr>\r\n");				
			}
			
			// the table footer
			buff.append("</table>\r\n");
				
	    	return buff.toString();
    	} catch (Exception e) {
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
