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
package org.jmythapi.protocol.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.ProtocolVersionInfo;
import org.jmythapi.protocol.request.IMythCommand;

/**
 * This annotation specifies a MythTV protocol version range.
 * <p>
 * Depending on the MythTV protocol version the backend is speaking, different protocol commands 
 * are supported and the required request- and received response-parameters may be different.
 * This annotation is used to specify for which MythTV-protocol versions a given 
 * protocol-request-command or protocol response-parameter is valid. It specifies a version range as an 
 * open interval.
 * <p>
 * 
 * <h3>Protocol Version Range examples:</h3>
 * <ul>
 * 
 * <li><b>{@code [3,10)}:</b><br> 
 * A command or property was introduced in protocol version {@code 03}, but was removed in protocol version {@code 10}. 
 * Therefore it can only be used till version {@code 09}.
 * </li>
 * 
 * <li><b>{@code [20,-1)}:</b><br> 
 * A command or property was introduced in protocol version {@code 20} and is still available in the last version
 * </li>
 * 
 * <li><b>{@code [0,20)}:</b><br>
 * A command or property was introduced in the first known MythTV version, but was removed in protocol version {@code 20}. 
 * Therefore the element can be used till version {@code 19}.
 * 
 * <li><b>{@code [0,-1)}:</b><br>
 * A command or property is supported starting with the first MythTV version and is still available 
 * in the last version.
 * </li>
 * </ul>
 * 
 * <h3>Protocol Versions:</h3>
 * See {@link ProtocolVersion here} for enumeration constants defining all known versions of the MythTV protocol.
 * <p>
 * The latest supported protocol version can be determined via {@link ProtocolVersion#getMaxVersion()}.<br>
 * The current version used by a MythTV backend connection or protocol element can be determined via 
 * {@link IVersionable#getVersionNr()}.<br> 
 * Different version can be compared using {@link ProtocolVersion#compareTo}.
 * 
 * 
 * <h3>Usage Examples</h3>
 * This section shows how the version annotation can be used on functions, function parameters or enumeration constants.
 * <h4>Annotation on functions:</h4>
 * <p>
 * In the following example the method getNextFreeRecorder was introduced in version {@code 03} and 
 * is still valid, because no upper bound was specified.
 * <p>
 * {@mythCodeExample <pre>
 * import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_03;
 * 
 * public interface IBackend extends Closeable &#123;
 * 	
 *     &#064;MythProtoVersionAnnotation(from=PROTO_VERSION_03)
 *     public abstract IRecorderInfo getNextFreeRecorder() throws IOException;
 *     
 * &#125;
 * </pre>} 
 * <p>
 * In the next example the backend method {@code reactivateRecording} was introduced in version {@code 05} 
 * but was removed in version {@code 19}.
 * </p>
 * {@mythCodeExample <pre>
 * import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_05;
 * import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_19;
 * ...
 * 
 * public interface IBackend &#123;
 *   ...
 *   &#47;**
 *    * &#064;since 05
 *    * &#064;deprecated 19
 *    *&#47;
 *   &#064;MythProtoVersionAnnotation(from = PROTO_VERSION_05, to = PROTO_VERSION_19)
 *   public abstract boolean reactivateRecording(IProgramInfo programInfo) throws IOException;
 *   ...
 * &#125;
 * </pre>}
 * 
 * Please note that many functions are usable (maybe with some restrictions) beyond the given protocol range, 
 * if an alternative set of functions can be used to achieve the same result. If such an "extended" version-range is 
 * available for a function, then the {@code toFallback} and {@code fromFallback} properties of the annotation are specified,
 * and the version numbers are mentioned as "fallback-from"- and "fallback-to"-version in the functions javadoc.  
 * 
 * <h4>Annotation on Enumeration constants:</h4>
 * <p>
 * In the next example the enum property {@code REPEAT} was removed in protocol version {@code 57}
 * and the property {@code REC_GROUP} was added in version {@code 03}.
 * </p>
 * 
 * {@mythCodeExample <pre>
 * import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_03;
 * import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_57;
 * 
 * public interface IProgramInfo &#123;
 *   public static enum Props &#123;
 *     ...
 *     &#064;MythProtoVersionAnnotation(to=PROTO_VERSION_57)
 *     REPEAT,	    
 *     ...
 *     &#064;MythProtoVersionAnnotation(from=PROTO_VERSION_03)
 *     REC_GROUP,
 *     ...
 *   &#125;
 * &#125;
 * </pre>}

 * <h4>Annotation on function parameters</h4>
 * <p>
 * Method parameters can also be marked with the version annotation. Parameters that are not supported
 * in the current protocol version are just ignored or are converted into other reasonable values if possible.
 * Read the javadoc of the method for the exact behavior.
 * </p>
 * {@mythCodeExample <pre>
 *   &#47;**
 *    * &#064;since 00
 *    *&#47;
 *   &#064;MythProtoVersionAnnotation(from = PROTO_VERSION_00)
 *   public IFileTransfer annotateFileTransfer(
 *      String fileName,
 *      &#064;MythProtoVersionAnnotation(from = PROTO_VERSION_29) Boolean useReadAhead,
 *      &#064;MythProtoVersionAnnotation(from = PROTO_VERSION_29) Integer retries,
 *      &#064;MythProtoVersionAnnotation(from = PROTO_VERSION_44) String storageGroup
 *    ) throws IOException;
 * </pre>}
 * <br>
 * 
 * <h3>Javadoc Taglet</h3>
 * 
 * There is a new javadoc-taglet {@code @mythProtoVersionRange} that generates an additional table
 * in javadoc, showing the protocol-range of an element including some metadata about the protocol-
 * versions, e.g. the commit date or a link to the Git revision when the element was added or
 * removed to the protocol. 
 * <br>
 * See {@link IMythCommand#ANN_FILE_TRANSFER here} for an example.
 * 
 * <h3>Additional Metadata:</h3>
 * Its also possible to add additional metadata to a {@link MythProtoVersionAnnotation} or to a {@link ProtocolVersion} constant.
 * See {@link ProtocolVersionInfo} for details.<br>
 * This additional metadata is only used for documentation purposes and will be displayed by the {@code @mythProtoVersionRange} taglet.
 * 
 * @see ProtocolVersion
 * @see ProtocolVersionInfo
 * @see MythProtoVersionMetadata
 * @see IVersionable
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD,PARAMETER,TYPE})
public @interface MythProtoVersionAnnotation {	
	
	/**
	 * Gets the MythTV-protocol version when a command or parameter was introduced.
	 * 
	 * @return 
	 * 		the protocol version when a command or parameter was introduced.
	 */
	ProtocolVersion from() default ProtocolVersion.PROTO_VERSION_00;
	
	/**
	 * Gets metadata about the lower protocol range.
	 * <p>
	 * This metadata could be, e.g. the SVN-Revision or Git commit
	 * when the command or property was introduced.  
	 * 
	 * @return
	 * 		metadata about the command- or property-introduction
	 */
	MythProtoVersionMetadata[] fromInfo() default {};
	
	/**
	 * Gets the version when a command or parameter was removed. 
	 * 
	 * @return 
	 * 		the MythTV-protocol version when the command was removed. 
	 * 		{@code -1} means that the command is still valid. 
	 */
	ProtocolVersion to() default ProtocolVersion.PROTO_VERSION_LATEST;
	
	/**
	 * Gets metadata about the higher protocol range.
	 * <p>
	 * This metadata could be, e.g. the SVN-Revision or Git commit
	 * when the command or property was removed. 
	 * 
	 * @return
	 * 		metadata about the command- or property-removal.
	 */	
	MythProtoVersionMetadata[] toInfo() default {};	
	
	/**
	 * Gets fallback versions. This versions are set, if the
	 * given protocol element can be used beyond the given protocol range,
	 * by using a fallback code.
	 *  
	 * @return
	 * 		the fallback versions
	 */
	ProtocolVersion[] fromFallback() default {};
	
	/**
	 * Gets fallback versions. This versions are set, if the
	 * given protocol element can be used beyond the given protocol range,
	 * by using a fallback code.
	 *  
	 * @return
	 * 		the fallback versions
	 */
	ProtocolVersion[] toFallback() default {};	
}
