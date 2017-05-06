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
package org.jmythapi.javadoc.utils;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_00;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_LATEST;
import static org.jmythapi.protocol.ProtocolVersionInfo.DATE;

import java.util.HashMap;
import java.util.Map;

import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.ProtocolVersionRange;
import org.jmythapi.protocol.annotation.MythParameterType;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtocolCmd;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;

/**
 * Javadoc related utils.
 */
public class JavadocUtils {
	/**
	 * The link prefix to a GIT commit.
	 */
	public static final String GIT_COMMIT_URL = "https://github.com/MythTV/mythtv/commit/";
	
	/**
	 * The link prefix to the release notes of a MythTV version.
	 */
	public static final String MYTHTV_WIKI_URL = "http://www.mythtv.org/wiki/Release_Notes_-_";	
	
	/**
	 * Gets a nice info-text that can be used as titles in links to a specific protocol version.
	 * <p>
	 * E.g. {@code Protocol Version 18 (2005-07-19)}
	 * 
	 * @param version
	 * 		the protocol version
	 * @return
	 * 		the info text
	 */	
	public static String getProtocolVersionInfoTitle(int version) {
		final ProtocolVersion protoVersionElem = ProtocolVersion.valueOf(version);
		return getProtocolVersionInfoTitle(protoVersionElem);
	}
	
	/**
	 * Gets a nice info-text that can be used as titles in links to a specific protocol version.
	 * <p>
	 * E.g. {@code Protocol Version 18 (2005-07-19)}
	 * 
	 * @param version
	 * 		the protocol version
	 * @return
	 * 		the info text
	 */
	public static String getProtocolVersionInfoTitle(ProtocolVersion version) {
    	final String versionDate = JavadocUtils.getProtocolVersionMetadata(version, DATE);    	
    	final String versionTitle = String.format(
    		"Protocol Version %02d%s",
    		version.getVersion(),
    		versionDate==null?"":" (" + versionDate + ")"
    	);
    	return versionTitle;
	}
	
	/**
	 * Gets the metadata info about the given protocol version.
	 * 
	 * @param version
	 * 		the protocol version
	 * @param metaDataKey
	 * 		the name of the requested metadata
	 * @return
	 * 		the value of the metadata or {@code null}
	 */
	public static String getProtocolVersionMetadata(ProtocolVersion version, String metaDataKey) {
		if(version == null) return null;
		
		final Map<String,String> metadata = version.getMetaData();
		if(metadata == null || !metadata.containsKey(metaDataKey)) return null;
		
		final String metaDataValue = metadata.get(metaDataKey);
		if(metaDataValue == null || metaDataValue.trim().length() == 0) return null;
		return metaDataValue.trim();
	}
	
	/**
	 * Gets the relative link to a specific protocol-version.
	 * 
	 * @param tag
	 * 		the current tag. This is required to determine the path to the protocol version.
	 * @param protoVersion
	 * 		the given protocol version
	 * @return
	 * 		the relative link to the given protocol version
	 */	
	public static String getProtocolVersionLink(Tag tag, int protoVersion) {
		final ProtocolVersion protoVersionElem = ProtocolVersion.valueOf(protoVersion);
		return getProtocolVersionLink(tag, protoVersionElem);
	}
	
	/**
	 * Gets the relative link to a specific {@link ProtocolVersion}.
	 * 
	 * @param tag
	 * 		the current tag. This is required to determine the path to the protocol version.
	 * @param version
	 * 		the given protocol version
	 * @return
	 * 		the relative link to the given protocol version, e.g. {@code ../../../org/jmythapi/protocol/ProtocolVersion.html#PROTO_VERSION_09}
	 */
	public static String getProtocolVersionLink(Tag tag, ProtocolVersion version) {
		final String linkPrefix = getProtocolVersionLinkPrefix(tag);
		if(linkPrefix == null) return null;
		return linkPrefix + version.name();
	}
	
	/**
	 * Gets the relative link to the {@link ProtocolVersion} enumeration.
	 * <p>
	 * This is used to link to a given protocol version.
	 * 
	 * @param tag
	 * 		the current tag. This is required to determine the path to the protocol version.
	 * @return
	 * 		a relative link to the protocol-version enum.
	 */
    public static String getProtocolVersionLinkPrefix(Tag tag) {
    	return getLinkPrefix(tag, ProtocolVersion.class.getName());
    }
    
	/**
	 * Gets the relative link to the given class.
	 * <p>
	 * This is used to link to a class or constant.
	 * 
	 * @param tag
	 * 		the current tag. This is required to determine the path to the class.
	 * @param className
	 * 		the class to link to
	 * @return
	 * 		a relative link to the given class
	 */    
    public static String getLinkPrefix(Tag tag, String className) {
    	// getting the package
    	final PackageDoc packageDoc = getPackageDoc(tag);
    	if(packageDoc == null) return null;
    	
    	final String packageName = packageDoc.name();
    	final String[] packageParts = packageName.split("\\.");
    	
    	final StringBuilder buff = new StringBuilder();
    	for(int i=0; i<packageParts.length;i++) {
    		buff.append("../");
    	}
    	
    	final String clazzPath = className.replace('.','/');
    	buff.append(clazzPath).append(".html#");
    	
    	return buff.toString();    	
    }
    
    /**
     * Gets the doc object of the containing package of the given tag.
     * 
     * @param tag
     * 		the tag
     * @return
     * 		the containing package of the tag
     */
    public static PackageDoc getPackageDoc(Tag tag) {
        Doc holder = tag.holder();
        if (holder instanceof ProgramElementDoc) {
            return ((ProgramElementDoc) holder).containingPackage();
        } else if (holder instanceof PackageDoc) {
            return (PackageDoc) holder;
        } else {
            return null;
        }
    }
    
    /**
     * Gets the protocol range a method or enumeration is annotated with.
     * <p>
     * If no protocol-version-range annotation is found, {@code null} is returned.
     * 
     * @param tag
     * 		the current tag
     * @param returnDefaultIfNull TODO
     * @return
     * 		the protocol version range or {@code null}.
     */
    public static ProtocolVersionRange getProtocolVersionRange(Tag tag, boolean returnDefaultIfNull) {
    	if(tag == null) return null;
    	
    	// trying to find the annotation we are interested in
    	final AnnotationDesc annotation = JavadocUtils.findProtocolVersionAnnotation(tag);
    	if(annotation == null && returnDefaultIfNull) return ProtocolVersionRange.DEFAULT_RANGE;
    	return getProtocolVersionRange(annotation);
    }	
    
    /**
     * Gets the protocol range of the given javadoc element.
     * <p>
     * If no protocol-version-range annotation is found, {@code null} is returned.
     * 
     * @param doc
     * 		the javadoc documentation
     * @param returnDefaultIfNull
     * 		if {@code true} and no version-range is found, {@link ProtocolVersionRange#DEFAULT_RANGE} is returned.
     * @return
     * 		the protocol version range or {@code null}.
     */    
    public static ProtocolVersionRange getProtocolVersionRange(Doc doc, boolean returnDefaultIfNull) {
    	if(doc == null) return null;
    	
    	// trying to find the annotation we are interested in
    	final AnnotationDesc annotation = JavadocUtils.findProtocolVersionAnnotation(doc);
    	if(annotation == null && returnDefaultIfNull) return ProtocolVersionRange.DEFAULT_RANGE;
    	return getProtocolVersionRange(annotation);
    }	    
    
    /**
     * Gets the protocol range of a javadoc annotation description.
     * <p>
     * If no protocol-version-range annotation is found, {@code null} is returned.
     * 
     * @param annotation
     * 		the javadoc of the annotation
     * @return
     * 		the protocol version range or {@code null}.
     */    
    public static ProtocolVersionRange getProtocolVersionRange(AnnotationDesc annotation) {
    	if(annotation == null) return null;
    	
    	// getting the annotation values
    	final ElementValuePair[] elems = annotation.elementValues();
    	if(elems == null || elems.length == 0) return null;    	
    	
    	// determine the version range
    	ProtocolVersion from = PROTO_VERSION_00;
    	Map<String,String> fromMetaData = new HashMap<String, String>();
    	ProtocolVersion fromFallback = null;
    	
    	ProtocolVersion to = PROTO_VERSION_LATEST;
    	Map<String,String> toMetaData = new HashMap<String, String>();
    	ProtocolVersion toFallback = null;
    	
    	for(ElementValuePair elem : elems) {
    		final String name = elem.element().name();
    		if(name.equals("from")) {
    			final String fromString =  ((FieldDoc)elem.value().value()).name();
    			from = ProtocolVersion.valueOf(fromString);
    		} else if(name.equals("fromInfo")) {
    			fromMetaData = extractMetaData(elem.value());
    		} else if(name.equals("to")) {
    			final String toString =  ((FieldDoc)elem.value().value()).name();
    			to = ProtocolVersion.valueOf(toString);
    		} else if(name.equals("toInfo")) {
    			toMetaData = extractMetaData(elem.value());
    		} else if(name.equals("fromFallback")) {
    			final AnnotationValue[] fallbacks = (AnnotationValue[]) elem.value().value();
    			if(fallbacks != null && fallbacks.length > 0) {
    				final String fromFallbackString = ((FieldDoc)fallbacks[0].value()).name();
    				fromFallback = ProtocolVersion.valueOf(fromFallbackString);
    			}
    		} else if(name.equals("toFallback")) {
    			final AnnotationValue[] fallbacks = (AnnotationValue[]) elem.value().value();
    			if(fallbacks != null && fallbacks.length > 0) {
    				final String toFallbackString = ((FieldDoc)fallbacks[0].value()).name();
    				toFallback = ProtocolVersion.valueOf(toFallbackString);
    			}
    		}
    	}
    	
    	return new ProtocolVersionRange(
    		from,fromMetaData, fromFallback,
    		to,toMetaData, toFallback
    	);
    }
    
    private static Map<String,String> extractMetaData(AnnotationValue annotationValue) {
    	final Map<String,String> metaData = new HashMap<String, String>();
    	try {
	    	if(annotationValue != null) {
	    		AnnotationValue[] annotationValuesArray = (AnnotationValue[]) annotationValue.value();
	    		if(annotationValuesArray != null) {
	    			for(AnnotationValue subValue : annotationValuesArray) {
	    				String key = null;
	    				String value = null;
	    				
	    				for(ElementValuePair elem : ((AnnotationDesc)subValue.value()).elementValues()) {
	    					final String name = elem.element().name();
	    					if(name.equals("key")) {
	    						key = elem.value().value().toString();
	    					} else if(name.equals("value")) {
	    						value = elem.value().value().toString();
	    					}
	    				}
	    				
	    				if(key != null) {
	    					metaData.put(key,value);
	    				}
	    			}    			
	    		}
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return metaData;
    }
    
    /**
     * Returns the documentation of the {@link MythProtoVersionAnnotation} a method
     * or enumeration is annotated with.
     * 
     * @param tag
     * 		the current tag
     * @return
     * 		the found documentation or {@code null}
     */
    public static AnnotationDesc findProtocolVersionAnnotation(Tag tag) {
    	return findProtocolVersionAnnotation(tag.holder());
    }
    
    /**
     * Returns the documentation of the {@link MythProtoVersionAnnotation} a method
     * or enumeration is annotated with.
     * 
     * @param doc
     * 		the javadoc of the element
     * @return
     * 		the found documentation or {@code null}
     */    
    public static AnnotationDesc findProtocolVersionAnnotation(Doc doc) {
    	if(doc == null) return null;
    	
    	// getting the parent doc
    	AnnotationDesc[] annotations = null;
    	if(doc instanceof MethodDoc) {
    		annotations = ((MethodDoc) doc).annotations();
    	} else if(doc instanceof FieldDoc) {
    		annotations = ((FieldDoc) doc).annotations();
    	} else if(doc instanceof ClassDoc) {
    		annotations = ((ClassDoc) doc).annotations();
    	} else {
    		return null;
    	}
   		if(annotations == null || annotations.length == 0) return null;
   		
   		// loop through the annotations to find the proper one
   		for(AnnotationDesc annotation : annotations) {
   			final String annotationName = annotation.annotationType().qualifiedName();
   			if (annotationName.equals(MythProtoVersionAnnotation.class.getName())){
   				return annotation;
   			} else if(annotationName.equals(MythProtocolCmd.class.getName())) {
   				ElementValuePair[] values = annotation.elementValues();
   				if(values != null) {
   					for(ElementValuePair value : values) {
   						final String name = value.element().name();
   						if(name.equals("protoVersion")) {
   							return (AnnotationDesc) ((AnnotationValue)value.value()).value();
   						}
   					}
   				}
   				return null;
   			}
   		}
   		return null;    	
    }
    
    public static ClassDoc getEnumDataType(FieldDoc enumDoc) {
    	if(enumDoc == null) return null;
    	
    	final AnnotationDesc[] annotations = enumDoc.annotations();
    	if(annotations == null || annotations.length == 0) return null;
    	
   		for(AnnotationDesc annotation : annotations) {
   			final String annotationName = annotation.annotationType().qualifiedName();
   			if (annotationName.equals(MythParameterType.class.getName())){
   		    	final ElementValuePair[] elems = annotation.elementValues();
   		    	if(elems == null || elems.length == 0) return null;  
   		    	
   		    	for(ElementValuePair elem : elems) {
   		    		final String name = elem.element().name();
   		    		if(name.equals("value")) {
   		    			return (ClassDoc) elem.value().value();
   		    		}
   		    	}   		    	
   			}
   		}
   		return null;
    }
}
