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
package org.jmythapi.protocol;

import static org.jmythapi.protocol.ProtocolVersionInfo.DATE;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.MYTHBUNTU_RELEASE;
import static org.jmythapi.protocol.ProtocolVersionInfo.MYTH_RELEASE;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import java.util.Map;
import java.util.TreeMap;

import org.jmythapi.IVersion;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.request.EPlaybackSockEventsMode;
import org.jmythapi.protocol.request.ERecordingsType;
import org.jmythapi.protocol.request.IMythCommand;
import org.jmythapi.protocol.response.IProgramRecordingSearchType;
import org.jmythapi.protocol.response.IProgramVideoProperties;


/**
 * This enumeration specifies all supported MythTV-protocol versions.
 * 
 * <h3>Protocol Versions:</h3>
 * Depending on the MythTV protocol version that a backend is speaking, different protocol commands are supported 
 * and the required request- and received response-parameters may be different.
 * <p>
 * On server side the protocol-version a specific MythTV-version is using is defined in the MythTV 
 * file <a href="https://github.com/MythTV/mythtv/blob/master/mythtv/libs/libmythbase/mythversion.h">mythversion.h</a>.
 * <br>
 * On jMythAPI side each object which is protocol-version-aware need to implement the interface {@link IVersionable}. 
 * With this interface the currently used protocol-version can be determined when sending or receiving request or 
 * response messages. Depending on the used version, the content of request-messages to be send is formatted differently, 
 * or a received response-message is interpreted differently.
 *
 * <h3>Protocol Version Ranges:</h3>
 * In which protocol versions an command or a request- or response-parameter is supported, is specified via 
 * the {@link MythProtoVersionAnnotation}. This annotation is used to define the range of protocol version 
 * a given request or parameter is valid. This lower and upper bound versions are additionally documented with 
 * the {@code @since} and the {@code @deprecated} javadoc tag.
 * <br>
 * Furthermore there is a special javadoc-taglet {@code @mythProtoVersionRange}, which that generates an additional 
 * table in javadoc, showing the protocol-range of an element including some metadata about the protocol-
 * versions, e.g. the commit date or a link to the Git revision when the element was added or removed to the protocol. 
 * See {@link IMythCommand#ANN_FILE_TRANSFER here} for an example.
 * <p>
 * There are two special protocol versions:
 * <ul>
 * 	<li>{@link #PROTO_VERSION_00}: represents all versions before the command
 *      {@link IMythCommand#MYTH_PROTO_VERSION MYTH_PROTO_VERSION} was introduced.</li>
 *  <li>{@link #PROTO_VERSION_LATEST}: represents the current trunk version.</li>
 * </ul>
 * If a protocol element (a request command or response property) is supported from version
 * {@mythProtoVersion 00} to version {@mythProtoVersion -1}, the given element is supported in all known
 * protocol version.
 *
 * <h3>Protocol Changelog:</h3>
 * See the javadoc for the various enumeration constants to see, which changes were done for a given protocol version.<br>
 * E.g. in {@link #PROTO_VERSION_37} multiple protocol-commands were added and removed, some response-message properties were added, and 
 * some new flags were introduced.  
 *
 * <h3>Usage Examples:</h3>
 * 
 * <h4>Comparing of protocol versions:</h4>
 * In the following example the backend method {@code queryUptime} was introduced in protocol version 15,
 * therefore the function can not be used with prior protocol versions.
 * 
 * {@mythCodeExample <pre>
 *    IBackend backend = ...; // an already connected backend
 *    
 *    // starting with protocol version 15 the backend uptime can be queried
 *    if(backend.getVersionNr().compareTo(ProtocolVersion.PROTO_VERSION_15)>=0) &#123;
 *       IUptime uptime = backend.queryUptime();
 *       System.out.println(String.format(
 *          "MythTV has an uptime of %s seconds.",
 *          uptime.getUptimeSeconds()
 *       ));
 *    &#125;
 * </pre>}
 * 
 * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmythbase/mythversion.h">libmythbase/mythversion.h</a>
 * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmythdb/mythversion.h">libmythdb/mythversion.h</a>
 * @see <a href="https://github.com/MythTV/mythtv/commits/master/mythtv/libs/libmyth/mythcontext.h">libmyth/mythcontext.h</a>
 * @see <a href="http://www.mythtv.org/wiki/Category:Myth_Protocol">Wiki</a>
 */
public enum ProtocolVersion implements IVersion {
	/* ============================================================================
	 * Protocol version CONSTANTS
	 * ============================================================================ */	
	/**
	 * All MythTV Version before 2004-01-29.
	 * <p>
	 * This version represents all versions before the command
	 * {@link IMythCommand#MYTH_PROTO_VERSION MYTH_PROTO_VERSION} was introduced.
	 */
	PROTO_VERSION_00(0,""),
	
	/**
	 * MythTV <span class='releaseVersion'>Release Version 0.14</span> - 2004-01-29.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#MYTH_PROTO_VERSION} added</li>
	 * </ul>
	 * 
	 * @since 2004-01-29
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_01(1,null,new String[][]{
		{DATE,"2004-01-29"},
		{SVN_COMMIT,"3021"},
		{GIT_COMMIT,"e6ffdd37481937e09ec7"}
	}),
	
	/**
	 * MythTV Development Version 2004-02-03.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingType.Type#FIND_ONE_RECORD} added</li>
	 * </ul>
	 * 
	 * @since 2004-02-03
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_02(2,null,new String[][]{
		{DATE,"2004-02-03"},
		{SVN_COMMIT,"3078"},
		{GIT_COMMIT,"bc9ecb5d63ca65b427a6"}
	}),
	
	/**
	 * MythTV Development Version 2004-02-05.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#GET_NEXT_FREE_RECORDER} added</li>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#REC_GROUP} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#CHAN_COMM_FREE} added</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#REC_DUPS} removed</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#DUP_IN} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#DUP_METHOD} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingDupInType} added</li>
	 * </ul>
	 * 
	 * @since 2004-02-05
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_03(3,null,new String[][]{
		{DATE,"2004-02-05"},
		{SVN_COMMIT,"3112"},
		{GIT_COMMIT,"13be2e34c22fa59d3874"}
	}),
	
	/**
	 * MythTV Development Version 2004-02-27.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#LOWER_REC_PRIORITY} removed</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#CONFLICT} added</li>
	 * 	<li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#MANUAL_CONFLICT} removed</li>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#LATER_SHOWING} added</li>
	 * 	<li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#AUTO_CONFLICT} removed</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingDupMethodType.Flags#DUP_ALLOW_EMPTY} removed</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingDupMethodType.Flags#DUP_EMPTY_SUB_DESC} removed</li>
	 * </ul>
	 * 
	 * @since 2004-02-27
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_04(4,null,new String[][]{
		{DATE,"2004-02-27"},
		{SVN_COMMIT,"3308"},
		{GIT_COMMIT,"e3f1508f4eb01052eded"}
	}),
	
	/**
	 * MythTV Development Version 2004-04-10.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#REACTIVATE_RECORDING} added</li>
	 * 	<li class='added'>{@link IMythCommand#REFRESH_BACKEND} added</li>
	 * </ul>
	 * 
	 * @since 2004-04-10
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_05(5,null,new String[][]{
		{DATE,"2004-04-10"},
		{SVN_COMMIT,"3503"},
		{GIT_COMMIT,"fbb6c46dd476b974e641"}
	}),
	
	/**
	 * MythTV Development Version 2004-05-01.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#CHANNEL_OUTPUT_FILTERS} added</li>
	 * </ul>
	 * 
	 * @since 2004-05-01
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_06(6,null,new String[][]{
		{DATE,"2004-05-01"},
		{SVN_COMMIT,"3589"},
		{GIT_COMMIT,"7038088d7ff6bff8d178"}
	}),
	
	/**
	 * MythTV Development Version 2004-05-08.
	 * 
	 * <ul class='protocolChanges'>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#MANUAL_OVERRIDE} removed</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#DONT_RECORD} added</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#OVERLAP} removed</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingType.Type#OVERRIDE_RECORD} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingType.Type#DONT_RECORD} added</li>
	 * </ul>
	 * 
	 * @since 2004-05-08
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_07(7,null,new String[][]{
		{DATE,"2004-05-08"},
		{SVN_COMMIT,"3617"},
		{GIT_COMMIT,"443b50aad3e36e672fbf"}
	}),
	
	/**
	 * MythTV <span class='releaseVersion'>Release Version 0.15</span> - 2004-05-09.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'> {@link org.jmythapi.protocol.response.IProgramInfo.Props#SERIES_ID} added</li>
	 *  <li class='added'> {@link org.jmythapi.protocol.response.IProgramInfo.Props#PROGRAM_ID} added</li>
	 *  <li class='added'> {@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#SERIES_ID} added</li>
	 *  <li class='added'> {@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#PROGRAM_ID} added</li>
	 *  <li class='added'> {@link org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props#SERIES_ID} added</li>
	 *  <li class='added'> {@link org.jmythapi.protocol.response.IRecorderNextProgramInfo.Props#PROGRAM_ID} added</li>
	 * </ul>
	 * 
	 * @since 2004-05-09
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_08(8,null,new String[][]{
		{DATE,"2004-05-09"},
		{MYTH_RELEASE,"0.15"},
		{SVN_COMMIT,"3621"},
		{GIT_COMMIT,"ddc3ba7379051f63b642"}
	}),
	
	/**
	 * MythTV Development Version 2004-06-04.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#GET_FREE_RECORDER_COUNT} added</li>
	 *  <li class='changed'>{@link org.jmythapi.protocol.response.IProgramRecordingDupMethodType.Flags#DUP_CHECK_ID_ONLY} added/removed</li>
	 *  <li class='added'>{@link IProgramRecordingSearchType} added</li>
	 * </ul>
	 * 
	 * @since 2004-06-04
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_09(9,null,new String[][]{
		{DATE,"2004-06-04"},
		{SVN_COMMIT,"3838"},
		{GIT_COMMIT,"2543a5c99b83e0d2bb07"}
	}),
	
	/**
	 *  MythTV Development Version 2004-07-01.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 *  <ul class='protocolChanges'>
	 *   <li class='added'>{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#CHANNEL_OUTPUT_FILTERS} added.</li>
	 *  </ul>
	 * 
	 * @since 2004-07-01
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_10(10,null,new String[][]{
		{DATE,"2004-07-01"},
		{SVN_COMMIT,"3966"},
		{GIT_COMMIT,"304c592c14ce7cc4b038"}
	}),
	
	/**
	 * MythTV Development Version 2004-07-06.
	 * 
	 * <ul class='protocolChanges'>
	 * 	<li class='added'> {@link org.jmythapi.protocol.response.IProgramInfo.Props#LAST_MODIFIED} added</li>
	 * </ul> 
	 * 
	 * @since 2004-07-06
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_11(11,null,new String[][]{
		{DATE,"2004-07-06"},
		{SVN_COMMIT,"3973"},
		{GIT_COMMIT,"f223614972b66a47682a"}
	}),
	
	/**
	 * MythTV Development Version 2004-07-10.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#STARS} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#ORIGINAL_AIRDATE} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#REPEAT} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingDupMethodType.Flags#DUP_CHECK_NEW_EPI} added</li>
	 * </ul> 
	 * 
	 * @since 2004-07-10
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_12(12,null,new String[][]{
		{DATE,"2004-07-10"},
		{SVN_COMMIT,"3988"},
		{GIT_COMMIT,"c5dbecde01ea70f8add7"}
	}),
	
	/**
	 * MythTV <span class='releaseVersion'>Release Version 0.16</span> - 2004-08-16.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#REPEAT} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#ORIGINAL_AIRDATE} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IRecorderProgramInfo.Props#STARS} added</li>
	 * </ul>
	 * 
	 * @since 2004-08-16
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_13(13,null,new String[][]{
			{DATE,"2004-08-16"},
			{SVN_COMMIT,"4122"},
			{GIT_COMMIT,"ad4597cdc8ef89a91ef7"}
	}),
	
	/**
	 * MythTV <span class='releaseVersion'>Release Version 0.17</span> - 2004-10-06.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link org.jmythapi.protocol.response.IProgramInfo} now encodes dates as UTC</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingDupInType.Flags#DUPS_IN_BOTH} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingDupInType.Flags#DUPS_NEW_EPI} added</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingDupMethodType.Flags#DUP_CHECK_NEW_EPI} removed</li>
	 * </ul>
	 * 
	 * @since 2004-10-06
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_14(14,null,new String[][]{
		{DATE,"2004-10-06"},
		{MYTH_RELEASE,"0.17"},
		{SVN_COMMIT,"4508"},
		{GIT_COMMIT,"2a743b9ded4095b78a90"}
	}),
	
	/**
	 * MythTV <span class='releaseVersion'>Release Version 0.18</span> - 2005-03-23.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#QUERY_LOAD} added</li>
	 * 	<li class='added'>{@link IMythCommand#QUERY_UPTIME} added</li>
	 * 	<li class='added'>{@link IMythCommand#QUERY_MEMSTATS} added</li>
	 * 	<li class='added'>{@link IMythCommand#QUERY_GUIDEDATATHROUGH} added</li>
	 * 	<li class='added'>{@link IMythCommand#RESCHEDULE_RECORDINGS} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#HAS_AIRDATE} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#INACTIVE} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingType.Type#FIND_DAILY_RECORD} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingType.Type#FIND_WEEKLY_RECORD} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingSearchType.Type#MANUAL_SEARCH} added</li>
	 * </ul>
	 * 
	 * @since 2005-03-23
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_15(15,null,new String[][]{
		{DATE,"2005-03-23"},
		{MYTH_RELEASE,"0.18"},
		{SVN_COMMIT,"5833"},
		{GIT_COMMIT,"f9bb13c9be24ddad4591"}
	}),
	
	/**
	 * MythTV Development Version 2005-05-03.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#FORCE_DELETE_RECORDING} added</li>
	 * 	<li class='added'>{@link IMythCommand#OK} added</li>
	 * 	<li class='added'>{@link IMythCommand#UNKNOWN_COMMAND} added</li>
	 * </ul>
	 * 
	 * @since 2005-05-03
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_16(16,null,new String[][]{
		{DATE,"2005-05-03"},
		{SVN_COMMIT,"6284"},
		{GIT_COMMIT,"e452f9a6fc03f261ab04"}
	}),
	
	/**
	 * MythTV Development Version  2005-05-24.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='removed'>{@link IMythCommand#QUERY_FREESPACE} removed.</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_FREE_SPACE} added.</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_FREE_SPACE_LIST} added.</li>
	 * 	<li class='added'>	{@link IMythCommand#GET_FREE_RECORDER_LIST} added.</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_PIXMAP_LASTMODIFIED} added.</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_COMMBREAK} added.</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_CUTLIST} added.</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_BOOKMARK} added.</li>
	 * 	<li class='added'>	{@link IMythCommand#SET_BOOKMARK} added.</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_SETTING} added.</li>
	 * 	<li class='added'>	{@link IMythCommand#SET_SETTING} added.</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_REMOTEENCODER_GET_MAX_BITRATE} added.</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_RECORDER_GET_MAX_BITRATE} added.</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_RECORDER_SHOULD_SWITCH_CARD} added.</li>
	 *  <li class='added'	>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#NOT_LISTED} added.</li>
	 * </ul>
	 * 
	 * @since 2005-05-24
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_17(17,null,new String[][]{
		{DATE,"2005-05-24"},
		{SVN_COMMIT,"6482"},
		{GIT_COMMIT,"a975132f72aab16ce5b2"}
	}),
	
	/**
	 * MythTV Development Version 2005-07-19.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_RECORDER_SET_SIGNAL_MONITORING_RATE} added.</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_RECORDER_PAUSE_RECORDER} added.</li>
	 *  <li class='removed'>{@link IMythCommand#QUERY_RECORDER_PAUSE_RECORDER} removed.</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_RECORDER_UNPAUSE} added.</li>
	 *  <li class='removed'>{@link IMythCommand#QUERY_RECORDER_UNPAUSE} removed.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramInfo.Props#TIMESTRETCH} added.</li>
	 * </ul>
	 * 
	 * @since 2005-07-19
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_18(18,null,new String[][]{
		{DATE,"2005-07-19"},
		{SVN_COMMIT,"6865"},
		{GIT_COMMIT,"240102c549d351d5b285"}
	}),
	
	/**
	 * MythTV Development Version  2005-10-09.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='removed'>{@link IMythCommand#REACTIVATE_RECORDING} removed.</li>
	 * 	<li class='added'>	{@link IMythCommand#ALLOW_SHUTDOWN} added</li>
	 * 	<li class='added'>	{@link IMythCommand#BLOCK_SHUTDOWN} added</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_RECORDER_GET_CURRENT_RECORDING} added.</li>
	 *  <li class='added'>	{@link IMythCommand#QUERY_REMOTEENCODER_GET_CURRENT_RECORDING} added.</li>
	 *  <li class='changed'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#TUNER_BUSY} changed.</li>
	 *  <li class='changed'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#LOW_DISKSPACE} changed.</li>
	 *  <li class='changed'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#CANCELLED} changed.</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#DELETED} removed.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#MISSED} added.</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#STOPPED} removed.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#ABORTED} added.</li>
	 *  <li class='changed'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#NOT_LISTED} changed.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#NEVER_RECORD} added.</li>
	 * </ul>
	 * 
	 * @since 2005-10-09
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_19(19,null,new String[][]{
		{DATE,"2005-10-09"},
		{SVN_COMMIT,"7427"},
		{GIT_COMMIT,"d71e95c15342f12ecbd4"}
	}),
	
	/**
	 * MythTV Development Version 2005-11-05.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='removed'>{@link IMythCommand#ANN_RING_BUFFER} removed.</li>
	 * 	<li class='removed'>{@link IMythCommand#QUERY_RECORDER_GET_FREE_SPACE} removed.</li>
	 * 	<li class='removed'>{@link IMythCommand#QUERY_RECORDER_SETUP_RING_BUFFER} removed.</li>
	 * 	<li class='removed'>{@link IMythCommand#QUERY_RECORDER_STOP_PLAYING} removed.</li>
	 * 	<li class='removed'>{@link IMythCommand#QUERY_RECORDER_REQUEST_BLOCK_RINGBUF} removed.</li>
	 * 	<li class='removed'>{@link IMythCommand#QUERY_RECORDER_SEEK_RINGBUF} removed.</li>
	 * 	<li class='removed'>{@link IMythCommand#QUERY_RECORDER_DONE_RINGBUF} removed.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_RECORDER_SPAWN_LIVETV} changed.</li>
	 *  <li class='added'>{@link IMythCommand#BACKEND_MESSAGE_LIVETV_CHAIN} added.</li>
	 *  <li class='added'>Mythwelcome added as backend feature.</li>
	 * </ul>
	 * 
	 * @since 2005-11-05
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_20(20,null,new String[][]{
		{DATE,"2005-11-05"},
		{SVN_COMMIT,"7739"},
		{GIT_COMMIT,"244771e83ab75f64f6ae"}
	}),
	
	/**
	 * MythTV Development Version 2005-11-10.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='removed'>{@link IMythCommand#QUERY_RECORDER_GET_PROGRAM_INFO} removed.</li>
	 * 	<li class='removed'>{@link IMythCommand#QUERY_RECORDER_GET_INPUT_NAME} removed.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_INUSERECORDING} added.</li>
	 * </ul>
	 * 
	 * @since 2005-11-10
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_21(21,null,new String[][]{
		{DATE,"2005-11-10"},
		{SVN_COMMIT,"7826"},
		{GIT_COMMIT,"ce1b4f5d7d54b34ca907"}
	}),
	
	/**
	 * MythTV Development Version 2005-11-15.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#ANN_MONITOR} added.</li>
	 * </ul>
	 * 
	 * @since 2005-11-15
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_22(22,null,new String[][]{
		{DATE,"2005-11-15"},
		{SVN_COMMIT,"7883"},
		{GIT_COMMIT,"a7af182e72ad9a81c52e"}
	}),
	
	/**
	 * MythTV Development Version 2006-01-10.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='removed'>{@link IMythCommand#QUEUE_TRANSCODE} removed.</li>
	 *  <li class='removed'>{@link IMythCommand#QUEUE_TRANSCODE_CUTLIST} removed.</li>
	 *  <li class='removed'>{@link IMythCommand#QUEUE_TRANSCODE_STOP} removed.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_RECORDER_CANCEL_NEXT_RECORDING} changed.</li>
	 *  <li class='added'>	{@link IMythCommand#QUERY_GETEXPIRING} added.</li>
	 *  <li class='added'>	{@link IMythCommand#BACKEND_MESSAGE_CLEAR_SETTINGS_CACHE} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.events.IAskRecording.Props#HAS_RECORDING} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_INUSEPLAYING} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramInfo.Props#PLAY_GROUP} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramInfo.Props#TIMESTRETCH} removed.</li>
	 * </ul>
	 * 
	 * @since 2006-01-10
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_23(23,null,new String[][]{
		{DATE,"2006-01-10"},
		{SVN_COMMIT,"8553"},
		{GIT_COMMIT,"546724c8c9a3b573946e"}
	}),
	
	/**
	 * MythTV Development Version 2006-01-15.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link IMythCommand#QUERY_RECORDER_CHECK_CHANNEL_PREFIX} response changed</li>
	 * </ul>
	 * 
	 * @since 2006-01-15
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_24(24,null,new String[][]{
		{DATE,"2006-01-15"},
		{SVN_COMMIT,"8617"},
		{GIT_COMMIT,"e271b33dce644f8c6dd2"}
	}),
	
	/**
	 * MythTV Development Version 2006-01-17.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#REC_PRIORITY2} added</li>
	 * </ul>
	 * 
	 * @since 2006-01-17
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_25(25,null,new String[][]{
		{DATE,"2006-01-17"},
		{SVN_COMMIT,"8628"},
		{GIT_COMMIT,"bb8a1b09e91b5b3c7d5a"}
	}),
	
	/**
	 * MythTV <span class='releaseVersion'>Release Version 0.19</span> - 2006-01-17.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#QUERY_RECORDER_SET_LIVE_RECORDING} added</li>
	 * </ul>
	 * 
	 * @since 2006-01-29
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_26(26,null,new String[][]{
		{DATE,"2006-01-17"},
		{MYTH_RELEASE,"0.19"},
		{SVN_COMMIT,"8754"},
		{GIT_COMMIT,"fcb87d603529e426a6cc"}
	}),
	
	/**
	 * MythTV Development Version 2006-02-15.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='removed'>{@link IMythCommand#QUERY_RECORDER_TOGGLE_INPUTS} removed.</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_RECORDER_GET_CONNECTED_INPUTS} added.</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_RECORDER_GET_INPUT} added.</li>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_RECORDER_SET_INPUT} added.</li>
	 * 	<li class='added'>	{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_CC} added.</li>
	 * 	<li class='added'>	{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_STEREO} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_HDTV} added.</li>
	 * </ul>
	 * 
	 * @since 2006-02-15
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_27(27,null,new String[][]{
		{DATE,"2006-02-15"},
		{SVN_COMMIT,"8973"},
		{GIT_COMMIT,"253a31568d27bf080f94"}
	}),
	
	/**
	 * MythTV Development Version 2006-03-28.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#QUERY_RECORDER_GET_CHANNEL_INFO} added.</li>
	 *  <li class='added'>{@link IMythCommand#SET_CHANNEL_INFO} added.</li>
	 *  <li class='added'>{@link IMythCommand#QUERY_FILETRANSFER_SET_TIMEOUT} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_TRANSCODED} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#OFFLINE} added.</li>
	 * </ul>
	 * 
	 * @since 2006-03-28
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_28(28,null,new String[][]{
		{DATE,"2006-03-28"},
		{SVN_COMMIT,"9524"},
		{GIT_COMMIT,"cc49c6ff3b674a9ad1e4"}
	}),
	
	/**
	 * MythTV Development Version 2006-04-01.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link IMythCommand#ANN_FILE_TRANSFER} changed: useReadAhead,retries params added.</li>
	 * </ul>
	 * 
	 * @since 2006-04-01
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_29(29,null,new String[][]{
		{DATE,"2006-04-01"},
		{SVN_COMMIT,"9592"},
		{GIT_COMMIT,"2116c72efb6633036608"}
	}),
	
	/**
	 * MythTV <span class='releaseVersion'>Release Version 0.20</span> - 2006-05-22.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>	{@link IMythCommand#QUERY_RECORDER_GET_COLOUR} added.</li>
	 *  <li class='added'>	{@link IMythCommand#QUERY_RECORDER_GET_CONTRAST} added.</li>
	 *  <li class='added'>	{@link IMythCommand#QUERY_RECORDER_GET_BRIGHTNESS} added.</li>
	 *  <li class='added'>	{@link IMythCommand#QUERY_RECORDER_GET_HUE} added.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_RECORDER_CHANGE_COLOUR} changed.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_RECORDER_CHANGE_CONTRAST} changed.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_RECORDER_CHANGE_BRIGHTNESS} changed.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_RECORDER_CHANGE_HUE} changed.</li>
	 * </ul>
	 * 
	 * @since 2006-05-22
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_30(30,null,new String[][]{
		{DATE,"2006-05-22"},
		{MYTH_RELEASE,"0.20"},
		{SVN_COMMIT,"9968"},
		{GIT_COMMIT,"b54b9e176ddec280a8ed"}
	}),
	
	/**
	 * MythTV <span class='releaseVersion'>Release Version 0.20-fixes</span> - 2006-09-24.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_WATCHED} added.</li>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#PARENT_ID} added.</li>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#FAILED} added.</li>
	 * </ul>
	 * 
	 * @since 2006-09-24
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_31(31,null,new String[][]{
		{DATE,"2006-09-24"},
		{MYTH_RELEASE,"0.20-fixes"},
		{SVN_COMMIT,"11278"},
		{GIT_COMMIT,"b64f4f654b9f88bd5860"}
	}),
	
	/**
	 * MythTV Development Version 2006-11-30.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#QUERY_RECORDING} added.</li>
	 *  <li class='added'>{@link IMythCommand#QUERY_RECORDING_BASENAME} added.</li>
	 *  <li class='added'>{@link IMythCommand#QUERY_RECORDING_TIMESLOT} added.</li>
	 * 	<li class='added'>{@link IMythCommand#QUERY_FREE_SPACE_SUMMARY} added.</li>
	 * 	<li class='added'>{@link IMythCommand#SET_NEXT_LIVETV_DIR} added.</li>		
	 *  <li class='changed'>{@link IMythCommand#QUERY_CHECKFILE} changed.</li>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps#DIRECTORIES} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps#IS_LOCAL} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps#FILESYSTEM_ID} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_PRESERVED} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#STORAGE_GROUP} added.</li>
	 * </ul>
	 * 
	 * @since 2006-11-30
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_32(32,null,new String[][]{
		{DATE,"2006-11-30"},
		{SVN_COMMIT,"12151"},
		{GIT_COMMIT,"a4796b5fc991f6739d71"}
	}),
	
	/**
	 * MythTV Development Version 2007-03-01.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#ORIGINAL_AIRDATE} format changed.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#OTHER_SHOWING} added.</li>
	 *  <li class='added'>	{@link ERecordingsType#Recording} added.</li>
	 *  <li class='changed'>{@link org.jmythapi.protocol.response.IProgramRecordingDupInType.Flags#DUPS_NEW_EPI} changed.</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingDupInType.Flags#DUPS_IN_BOTH} removed.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramRecordingDupInType.Flags#DUPS_EX_REPEATS} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramRecordingDupInType.Flags#DUPS_EX_GENERIC} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramRecordingDupMethodType.Flags#DUP_CHECK_SUB_THEN_DESC} added.</li>
	 * </ul>
	 * 
	 * @since 2007-03-01
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_33(33,null,new String[][]{
		{DATE,"2007-03-01"},
		{SVN_COMMIT,"12904"},
		{GIT_COMMIT,"d2f00684f17c6f6bd3cd"}
	}),
	
	/**
	 * MythTV Development Version 2007-04-13.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link IMythCommand#QUERY_RECORDER_SPAWN_LIVETV} changed.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingDupInType.Flags#DUPS_FIRST_NEW} added.</li>
	 * </ul>
	 * 
	 * @since 2007-04-13
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_34(34,null,new String[][]{
		{DATE,"2007-04-13"},
		{SVN_COMMIT,"13230"},
		{GIT_COMMIT,"69307f175c5bfd926bcb"}
	}),
	
	/**
	 * MythTV Development Version 2007-07-16.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>	{@link org.jmythapi.protocol.response.IProgramInfo.Props#AUDIO_PROPERTIES} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramInfo.Props#VIDEO_PROPERTIES} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramInfo.Props#SUBTITLE_TYPE} added.</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_STEREO} removed.</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_CC} removed.</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_HDTV} removed.</li>
	 * </ul>
	 * 
	 * @since 2007-07-16
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_35(35,null,new String[][]{
		{DATE,"2007-07-16"},
		{SVN_COMMIT,"13952"},
		{GIT_COMMIT,"2ec595b45d09fec565ee"}
	}),
	
	/**
	 * MythTV Development Version 2007-09-11.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#UNDELETE_RECORDING} added.</li>
	 * </ul>
	 * 
	 * @since 2007-09-11
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_36(36,null,new String[][]{
		{DATE,"2007-09-11"},
		{SVN_COMMIT,"14483"},
		{GIT_COMMIT,"b94ee87382b87a7126c5"}
	}),
	
	/**
	 * MythTV Development Version 2008-01-14.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='removed'>{@link IMythCommand#QUERY_RECORDER_GET_CONNECTED_INPUTS} removed.</li>
	 *  <li class='added'>	{@link IMythCommand#QUERY_RECORDER_GET_FREE_INPUTS} added.</li>
	 *  <li class='added'>	{@link IMythCommand#QUERY_REMOTEENCODER_GET_FREE_INPUTS} added.</li>
	 *  <li class='added'>	{@link IMythCommand#QUERY_REMOTEENCODER_GET_FLAGS} added.</li>
	 *  <li class='added'>	{@link IMythCommand#QUERY_REMOTEENCODER_CANCEL_NEXT_RECORDING} added.</li>
	 *  <li class='added'>	{@link IMythCommand#QUERY_REMOTEENCODER_STOP_RECORDING} added.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_REMOTEENCODER_IS_BUSY} changed.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_REMOTEENCODER_RECORD_PENDING} changed.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_GENPIXMAP} changed.</li>
	 *  <li class='added'>  {@link IMythCommand#BACKEND_MESSAGE_RECORDING_LIST_CHANGE} {@code DELETE} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramAudioProperties.Flags#AUD_HARDHEAR} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramAudioProperties.Flags#AUD_VISUALIMPAIR} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramVideoProperties.Flags#VID_AVC} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps#STORAGE_GROUP_ID} added</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramSubtitleType.Flags#SUB_SIGNED} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.events.IAskRecording.Props#HAS_LATER_SHOWING} added.</li>
	 * </ul>
	 * 
	 * @since 2008-01-14
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_37(37,null,new String[][]{
		{DATE,"2008-01-14"},
		{SVN_COMMIT,"15437"},
		{GIT_COMMIT,"b3c20d633a874f886f11"}
	}),
	
	/**
	 * MythTV Development Version 2008-01-23.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#DELETE_FAILED_RECORDING} added.</li>
	 * </ul>
	 * 
	 * @since 2008-01-23
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_38(38,null,new String[][]{
		{DATE,"2008-01-23"},
		{SVN_COMMIT,"15550"},
		{GIT_COMMIT,"975b5a71e4e55345da6e"}
	}),
	
	/**
	 * MythTV <span class='releaseVersion'>Release Version 0.21</span> - 2008-01-31.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='removed'>{@link IMythCommand#DELETE_FAILED_RECORDING} removed.</li>
	 * </ul>
	 * 
	 * @since 2008-01-31
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_39(39,null,new String[][]{
		{DATE,"2008-01-31"},
		{MYTH_RELEASE,"0.21"},
		{SVN_COMMIT,"15701"},
		{GIT_COMMIT,"b2d8e1fe354a796d54bf"}
	}),
	
	/**
	 * MythTV Development Version 2008-02-17.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link IMythCommand#QUERY_PIXMAP_LASTMODIFIED} changed. A unix-timestamp is returned instead of a localized date string now.</li>
	 *  <li class='added'>	{@link IMythCommand#BACKEND_MESSAGE_RESET_IDLETIME} added.</li>
	 * 	<li class='added'>	{@link org.jmythapi.protocol.response.IRecordingStatus.Props#LIVETV_IN_PROGRESS} added.</li>
	 * </ul>
	 * 
	 * @since 2008-02-17
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_40(40,null,new String[][]{
		{DATE,"2008-02-17"},
		{SVN_COMMIT,"16090"},
		{GIT_COMMIT,"34048055f51197778129"}
	}),
	
	/**
	 * MythTV Development Version 2008-09-25.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#YEAR} added.</li>
	 *  <li class='changed'>{@link IMythCommand#DELETE_RECORDING} changed. Deletes can be done using channel-id and rec-start-time now.</li>
	 * </ul>
	 * 
	 * @since 2008-09-25
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_41(41,null,new String[][]{
		{DATE,"2008-09-25"},
		{SVN_COMMIT,"18419"},
		{GIT_COMMIT,"ad871fd258218dd87c9d"}
	}),
	
	/**
	 * MythTV Development Version 2008-10-07.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#QUERY_TIME_ZONE} added.</li>
	 * </ul>
	 * 
	 * @since 2008-10-07
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_42(42,null,new String[][]{
		{DATE,"2008-10-07"},
		{SVN_COMMIT,"18574"},
		{GIT_COMMIT,"7a4c6703d76598943580"}
	}),
	
	/**
	 * MythTV Development Version 2008-12-22.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link IMythCommand#QUERY_RECORDER_FILL_POSITION_MAP} changed.</li>
	 * </ul>
	 * 
	 * @since 2008-12-22
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_43(43,null,new String[][]{
		{DATE,"2008-12-22"},
		{SVN_COMMIT,"19417"},
		{GIT_COMMIT,"52259b824062fb261989"}
	}),
	
	/**
	 * MythTV Development Version 2009-02-12.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#ANN_FILE_TRANSFER} changed: storagegroup parameter added.</li>
	 *  <li class='added'>{@link IMythCommand#QUERY_SG_GETFILELIST} added.</li>
	 *  <li class='added'>{@link IMythCommand#QUERY_SG_FILEQUERY} added.</li>
	 * </ul>

	 * @since 2009-02-12
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_44(44,null,new String[][]{
		{DATE,"2009-02-12"},
		{SVN_COMMIT,"19978"},
		{GIT_COMMIT,"47918f95763a0874acac"}
	}),
	
	/**
	 * MythTV Development Version 2009-05-09.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link IMythCommand#QUERY_RECORDER_TOGGLE_CHANNEL_FAVORITE} changed.</li>
	 *  <li class='changed'>{@link IMythCommand#BACKEND_MESSAGE_DONE_RECORDING} changed.</li>
	 *  <li class='added'>	{@link IMythCommand#GO_TO_SLEEP} added.</li>
	 *  <li class='added'>	{@link IMythCommand#QUERY_REMOTEENCODER_GET_SLEEPSTATUS} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.ISleepStatus} added.</li>
	 * 	<li class='added'>	{@link org.jmythapi.protocol.response.IRemoteEncoderState.State#WATCHING_VIDEO} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IRemoteEncoderState.State#WATCHING_DVD} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramVideoProperties.Flags#VID_720} added.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramVideoProperties.Flags#VID_1080} added.</li>
	 * </ul>
	 * 
	 * @since 2009-05-09
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_45(45,null,new String[][]{
		{DATE,"2009-05-09"},
		{SVN_COMMIT,"20523"},
		{GIT_COMMIT,"852b80ae3a2dc6cfcf67"}
	}),
	
	/**
	 * MythTV Development Version 2009-08-08.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>	{@link IMythCommand#DELETE_FILE} added.</li>
	 *  <li class='added'>	{@link IMythCommand#QUERY_FILETRANSFER_WRITE_BLOCK} added.</li>
	 *  <li class='changed'>{@link IMythCommand#ANN_FILE_TRANSFER} changed: writing of files.</li>
	 * </ul>
	 * 
	 * @since 2009-08-08
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_46(46,null,new String[][]{
		{DATE,"2009-08-08"},
		{SVN_COMMIT,"21158"},
		{GIT_COMMIT,"48b0dff7a796dbec38dc"}
	}),
	
	/**
	 * MythTV Development Version 2009-08-16.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>	{@link org.jmythapi.protocol.response.IFreeSpaceListEntry.EntryProps#BLOCK_SIZE} added.</li>
	 *  <li class='changed'>{@link IMythCommand#DELETE_FILE} changed.</li>
	 * </ul>
	 * 
	 * @since 2009-08-16
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_47(47,null,new String[][]{
		{DATE,"2009-08-16"},
		{SVN_COMMIT,"21298"},
		{GIT_COMMIT,"23313c004e217d3ccd0b"}
	}),
	
	/**
	 * MythTV Development Version 2009-08-23.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link IMythCommand#ANN} changed. Error descriptions are returned now.</li>
	 * </ul>
	 * 
	 * @since 2009-08-23
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_48(48,null,new String[][]{
		{DATE,"2009-08-23"},
		{SVN_COMMIT,"21445"},
		{GIT_COMMIT,"2fdeb3fc4acf89989a5d"}
	}),
	
	/**
	 * MythTV Development Version 2009-10-01.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#QUERY_FILE_EXISTS} added.</li>
	 *  <li class='added'>{@link IMythCommand#QUERY_PIXMAP_GET_IF_MODIFIED} added.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_SG_GETFILELIST} changed.</li>
	 * </ul>
	 * 
	 * @since 2009-10-01
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_49(49,null,new String[][]{
		{DATE,"2009-10-01"},
		{SVN_COMMIT,"22164"},
		{GIT_COMMIT,"3c131674bcd3aa853588"}
	}),
	
	/**
	 * MythTV <span class='releaseVersion'>Release Version 0.22</span> - 2009-10-02.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#QUERY_HOSTNAME} added.</li>
	 * </ul>
	 * 
	 * @since 2009-10-0
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_50(50,null,new String[][]{
		{DATE,"2009-10-02"},
		{MYTH_RELEASE,"0.22"},
		{SVN_COMMIT,"22170"},
		{GIT_COMMIT,"b515e6c5b2f9384135d2"}
	}),
	
	/**
	 * MythTV Development Version 2009-11-23.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link IMythCommand#QUERY_FILE_HASH} added.</li>
	 * </ul>
	 * 
	 * @since 2009-11-23
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_51(51,null,new String[][]{
		{DATE,"2009-11-23"},
		{SVN_COMMIT,"22892"},
		{GIT_COMMIT,"8c36504df2f3eb89502a"}
	}),
	
	/**
	 * MythTV Development Version 2009-11-30.
	 * 
	 * TODO:
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>	{@link IMythCommand#BACKEND_MESSAGE_UPDATE_PROG_INFO} added.</li>
	 * 	<li class='changed'>{@link IMythCommand#MESSAGE} changed.</li>
	 * </ul>
	 * 
	 * @since 2009-11-30
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_52(52,null,new String[][]{
		{DATE,"2009-11-30"},
		{SVN_COMMIT,"22932"},
		{GIT_COMMIT,"bddb06933b3932730990"}
	}),
	
	/**
	 * MythTV Development Version 2009-12-06.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_COMMPROCESSING} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_DELETEPENDING} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_REALLYEDITING} added.</li>
	 *  <li class='added'>{@link IMythCommand#BACKEND_MESSAGE_RECORDING_LIST_CHANGE} {@code ADD} added.</li>
	 * </ul>
	 * 
	 * @since 2009-12-06
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_53(53,null,new String[][]{
		{DATE,"2009-12-06"},
		{SVN_COMMIT,"22955"},
		{GIT_COMMIT,"5b6770d22545a21211d8"}
	}),
	
	/**
	 * MythTV Development Version 2009-12-18.
	 * TODO:
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#BACKEND_MESSAGE_MASTER_UPDATE_PROG_INFO} added.</li>
	 * 	<li class='added'>{@link IMythCommand#BACKEND_MESSAGE_UPDATE_FILE_SIZE} added.</li>
	 * </ul>
	 * 
	 * @since 2009-12-18
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_54(54,null,new String[][]{
		{DATE,"2009-12-18"},
		{SVN_COMMIT,"22976"},
		{GIT_COMMIT,"fead9968a5f28c33bb7d"}
	}),
	
	/**
	 * MythTV Development Version 2009-12-21.
	 * 
	 * TODO
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 *  <li class='added'>{@link IMythCommand#BACKEND_MESSAGE_RECORDING_LIST_CHANGE} {@code UPDATE} added.</li>
	 *  <li class='removed'>{@link IMythCommand#BACKEND_MESSAGE_UPDATE_PROG_INFO} removed.</li>
	 * 	<li class='changed'>MASTER_UPDATE_PROG_INFO changed. Dends only the channel-id and rec-start-time now</li>
	 * </ul>
	 * 
	 * @since 2009-12-21
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_55(55,null,new String[][]{
		{DATE,"2009-12-21"},
		{SVN_COMMIT,"22992"},
		{GIT_COMMIT,"b2247829b3050be1da88"}
	}),
	
	/**
	 * MythTV <span class='releaseVersion'>Release Version 0.23</span> - 2009-12-29.
	 * <p>
	 * TODO: what changes were done in this Version?
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 *  <li class='changed'>MythTV event handling system changed. Various events added? XXX</li>
	 *  <li class='changed'>{@link IMythCommand#DELETE_RECORDING} changed. Parameter {@code FORCE} added.</li>
	 *  <li class='changed'>{@link IMythCommand#UNDELETE_RECORDING} changed. Undelete can be done by channel-id and rec-start-time now. TODO</li>
	 * </ul>
	 * 
	 * @since 2009-12-29
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_56(56,null,new String[][]{
		{DATE,"2009-12-29"},
		{MYTH_RELEASE,"0.23"},
		{MYTHBUNTU_RELEASE,"10.04"},
		{SVN_COMMIT,"23028,23012"},
		{GIT_COMMIT,"c1a564c9b62e3150d7c3" + "," + "07948b49f4f7ecd79e6d28ff2faf1a7c4f516078"}
	}),

	/**
	 * MythTV <span class='releaseVersion'>Bugfix Version 0.23.1</span> - 2010-07-17.
	 * <p>
	 * Some bugfixes from trunk were backported into this version.
	 * 
	 * @since 2010-07-17
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_23056(23056,null,new String[][]{
		{DATE,"2010-07-17"},
		{MYTH_RELEASE,"0.23.1"},
		{SVN_COMMIT,"25366"},
		{GIT_COMMIT,"feafbbce465d0a573937"}
	}),
	
	/**
	 * MythTV Development Version 2010-05-16.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link IMythCommand#ANN} changed. More event-modes possible.</li>
	 *  <li class='added'>{@link EPlaybackSockEventsMode#NON_SYSTEM} added.</li>
	 *  <li class='added'>{@link EPlaybackSockEventsMode#SYSTEM_ONLY} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_NONE} added</li>
	 *  <li class='changed'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_INUSERECORDING} changed</li>
	 *  <li class='changed'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_INUSEPLAYING} changed</li>
	 *  <li class='changed'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_REALLYEDITING} changed</li>
	 *  <li class='changed'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_COMMPROCESSING} changed</li>
	 *  <li class='changed'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_DELETEPENDING} changed</li>
	 *  <li class='changed'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_TRANSCODED} changed</li>
	 *  <li class='changed'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_WATCHED} changed</li>
	 *  <li class='changed'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_PRESERVED} changed</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_CHANCOMMFREE} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_REPEAT} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_DUPLICATE} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_REACTIVATE} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_IGNOREBOOKMARK} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_TYPEMASK} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramFlags.Flags#FL_INUSEOTHER} added</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_GETCONFLICTING} changed. A programInfo must be specified as request parameter now.</li>
	 * </ul>
	 * 
	 * @since 2010-05-16
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_57(57,null,new String[][]{
		{DATE,"2010-05-16"},
		{SVN_COMMIT,"24694,23636"},
		{GIT_COMMIT,"6e17b4de938428becdee" +","+ "bfa20e9cd66ce89929f10bdeeecf75d2b7fd1166"}
	}),
	
	/**
	 * MythTV Development Version 2010-07-17.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 *  <li class='added'>{@link IMythCommand#DOWNLOAD_FILE} added.</li>
	 *  <li class='added'>{@link IMythCommand#DOWNLOAD_FILE_NOW} added.</li>
	 *  <li class='added'>{@link IMythCommand#BACKEND_MESSAGE_DOWNLOAD_FILE} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IRemoteEncoderState.State#WATCHING_BD} added.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_SG_FILEQUERY} changed. A relative path can be used as pathname now. The backend IP can be used as hostname now.</li>
	 * </ul>
	 * 
	 * @since 2010-07-17
	 */
	PROTO_VERSION_58(58,null,new String[][]{
		{DATE,"2010-07-17"},
		{SVN_COMMIT,"25362,25229"},
		{GIT_COMMIT,"0bba872055b07014f16b" + "," + "c1e00b45da0b65d23b0023a6e596061f847f75ea" + "," + "7fce1004ab6e3692a5cfd1b6166d31f611447a05"}
	}),	
	
	/**
	 * MythTV Development Version 2010-08-26.
	 * 
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IFileStatus.Props#DEV} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IFileStatus.Props#INO} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IFileStatus.Props#MODE} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IFileStatus.Props#NLINK} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IFileStatus.Props#UID} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IFileStatus.Props#GID} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IFileStatus.Props#RDEV} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IFileStatus.Props#SIZE} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IFileStatus.Props#BLKSIZE} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IFileStatus.Props#BLOCKS} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IFileStatus.Props#ATIME} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IFileStatus.Props#MTIME} added</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IFileStatus.Props#CTIME} added</li>
	 *  <li class='changed'>{@link IMythCommand#DELETE_RECORDING} changed. Parameter {@code FORGET} added.</li>
	 *  <li class='changed'>Adds support for ISOs over remote file access (SVN 25858) TODO:</li>
	 * </ul>
	 * 
	 * @since 2010-08-26
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_59(59,null,new String[][]{
		{DATE,"2010-08-26"},
		{SVN_COMMIT,"25858"},
		{GIT_COMMIT,"3af3489357da268916c0"}
	}),
	
	/**
	 * MythTV Development Version 2010-09-03.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link IMythCommand#ANN_FILE_TRANSFER} changed. A timeout value is used instead fo the retry counter now.</li>
	 * </ul>
	 * 
	 * @since 2010-09-03
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_60(60,null,new String[][]{
		{DATE,"2010-09-03"},
		{SVN_COMMIT,"26101"},
		{GIT_COMMIT,"961dc80a878226da3f39"}
	}),
	
	/**
	 * MythTV Development Version 2010-09-03.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='removed'>{@link IMythCommand#QUERY_GENPIXMAP} removed</li>
	 * 	<li class='added'>{@link IMythCommand#QUERY_GENPIXMAP2} added</li>
	 *  <li class='added'>{@link IMythCommand#BACKEND_MESSAGE_GENERATED_PIXMAP} added</li>
	 * </ul>
	 * 
	 * @since 2010-09-03
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_61(61,null,new String[][]{
		{DATE,"2010-09-03"},
		{SVN_COMMIT,"26106"},
		{GIT_COMMIT,"c65a45682845f16c3ce6"}
	}),
	
	/**
	 * MythTV Development Version 2010-09-13.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link IMythCommand#MYTH_PROTO_VERSION} changed. Protocol token added.</li>
	 * </ul>
	 * 
	 * @since 2010-09-13
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_62(62,"78B5631E",new String[][]{
		{DATE,"2010-09-13"},
		{SVN_COMMIT,"26280"},
		{GIT_COMMIT,"c590e97346959463ef05"}
	}),
	
	/**
	 * MythTV <span class='releaseVersion'>Release Version 0.24</span> - 2010-09-25.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#QUERY_REMOTEENCODER_GET_RECORDING_STATUS} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#TUNING} added.</li>
	 *  <li class='added'>{@link IMythCommand#BACKEND_MESSAGE_VIDEO_LIST_CHANGE} added.</li>
	 * </ul>
	 * 
	 * @since 2010-09-25
	 * @mythProtoVersionInfo
	 * 
	 * Database version 1263
	 */
	PROTO_VERSION_63(63,"3875641D",new String[][]{
		{DATE,"2010-09-25"},
		{MYTH_RELEASE,"0.24"},
		{MYTHBUNTU_RELEASE,"11.04"},
		{SVN_COMMIT,"26518"},
		{GIT_COMMIT,"67fd5cffa0c31d2eddac"}
	}),
	
	/**
	 * MythTV Development Version 2010-11-20.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#SCAN_VIDEOS} added.</li>
	 * </ul>
	 * 
	 * @since 2010-11-20
	 * @mythProtoVersionInfo
	 * 
	 * database version 1264
	 */
	PROTO_VERSION_64(64,"8675309J",new String[][]{
		{DATE,"2010-11-20"},
		{SVN_COMMIT,"27308"},
		{GIT_COMMIT,"69f594d3d3dbc573b90e"}
	}),
	
	/**
	 * MythTV Development Version 2011-03-08.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link IMythCommand#QUERY_RECORDINGS} changed.</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.request.ERecordingsType#Play} removed.</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.request.ERecordingsType#Delete} removed.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.request.ERecordingsType#Unsorted} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.request.ERecordingsType#Ascending} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.request.ERecordingsType#Descending} added.</li>
	 * 	<li class='changed'>{@link IMythCommand#QUERY_FILE_EXISTS} changed: Storage group argument made optional.</li>
	 *  <li class='added'>	{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#MISSED_FUTURE} added.</li>
	 * </ul>
	 * 
	 * @since 2011-03-08
	 * @mythProtoVersionInfo
	 * 
	 * database version 1269
	 */
	PROTO_VERSION_65(65,"D2BB94C2",new String[][]{
		{DATE,"2011-03-08"},
		{GIT_COMMIT,"e965e4bab1d37fd9fa94"}
	}),
	
	/**
	 * MythTV Development Version 2011-05-25.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 *  <li class='changed'>{@link IMythCommand#ANN_FILE_TRANSFER} changed.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_FILETRANSFER_SEEK} changed.</li>
	 * 	<li class='changed'>{@link IMythCommand#QUERY_FREE_SPACE_SUMMARY} changed.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_FREE_SPACE_LIST} changed.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_RECORDER_GET_FILE_POSITION} changed.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_RECORDER_GET_FRAMES_WRITTEN} changed.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_RECORDER_GET_MAX_BITRATE} changed.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_REMOTEENCODER_GET_MAX_BITRATE} changed.</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_RECORDER_FILL_POSITION_MAP} changed (TODO).</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_RECORDER_GET_KEYFRAME_POS} changed (TODO).</li>
	 *  <li class='changed'>{@link IMythCommand#SET_BOOKMARK} changed (TODO).</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_BOOKMARK} changed (TODO).</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_COMMBREAK} changed (TODO).</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_CUTLIST} changed (TODO).</li>
	 *  <li class='changed'>{@link IMythCommand#QUERY_REMOTEENCODER_START_RECORDING} changed (response value changes)</li>
	 * </ul>
	 * 
	 * @since 2011-05-25
	 * @mythProtoVersionInfo
	 * 
	 * database version 1275
	 */
	PROTO_VERSION_66(66,"0C0FFEE0",new String[][]{
		{DATE,"2011-05-25"},
		{GIT_COMMIT,"64d448acdd27b0384748,1508085eb3cf5f5b88af"}
	}),
	
	/**
	 * MythTV Development Version 2011-06-03.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#EPISODE} added</li>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#SEASON} added</li>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#INETREF} added</li>
	 * </ul>
	 * 
	 * @since 2011-06-03
	 * @mythProtoVersionInfo
	 * 
	 * database version 1278
	 */
	PROTO_VERSION_67(67,"0G0G0G0",new String[][]{
		{DATE,"2011-06-03"},
		{GIT_COMMIT,"a41e9657c0e9dfbfb412"}
	}),		
		
	/**
	 * MythTV Development Version 2011-07-09.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link IMythCommand#BACKEND_MESSAGE_VIDEO_LIST_CHANGE} changed.</li>
	 *  <li class='added'>TODO: ANN MediaServer added</li>
	 * </ul>
	 * 
	 * @since 2011-07-09
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_68(68,"90094EAD",new String[][]{
		{DATE,"2011-07-09"},
		{GIT_COMMIT,"a17e689bdc00df4f04ca"}
	}),	
	
	/**
	 * MythTV Development Version 2011-07-11.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>{@link IMythCommand#QUERY_FILE_HASH} changed</li>
	 *  <li class='added'>{@link IMythCommand#BACKEND_MESSAGE_VIDEO_LIST_NO_CHANGE} added.</li>
	 * </ul>
	 * 
	 * @since 2011-07-11
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_69(69,"63835135",new String[][]{
		{DATE,"2011-07-11"},
		{GIT_COMMIT,"fb34130d3a7d3c5991fa"}
	}),	
	
	/**
	 * MythTV Development Version 2011-11-30.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#QUERY_FILETRANSFER_REOPEN} added TODO</li>
	 *  <li class='added'>{@link IProgramVideoProperties.Flags#VID_DAMAGED} added.</li>
	 * </ul>
	 * 
	 * @since 2011-07-11
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_70(70,"53153836",new String[][]{
		{DATE,"2011-11-30"},
		{GIT_COMMIT,"1da9d23d2838bc2d2e0bc06e2e9e1d382897d21d"}
	}),		
	
	/**
	 * MythTV Development Version 2012-01-15.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IInputInfoTuned.Props#LIVETV_ORDER} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IInputInfoFree.Props#LIVETV_ORDER} added.</li>
	 * </ul>
	 * 
	 * @since 2012-01-15
	 * @mythProtoVersionInfo
	 */	
	PROTO_VERSION_71(71,"05e82186",new String[][]{
		{DATE,"2012-01-15"},
		{GIT_COMMIT,"3281cdd32cbc7fe27a6b693bbc1406aa98b288ca"}
	}),
	
	/**
	 * MythTV <span class='releaseVersion'>Release Version 0.25</span> 2012-01-29.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#QUERY_ACTIVE_BACKENDS} added.</li>
	 * </ul>
	 * 
	 * @since 2012-01-29
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_72(72,"D78EFD6F",new String[][]{
		{DATE,"2012-01-29"},
		{GIT_COMMIT,"7a94153c8cbd637e6b84ead48e3fedcfb94b0241"},
		{MYTH_RELEASE,"0.25"},
		{MYTHBUNTU_RELEASE,"12.04"},
	}),
	
	/**
	 * MythTV Development Version 2012-04-11.
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#OTHER_RECORDING} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status#OTHER_TUNING} added.</li>
	 *  <li class='changed'>{@link IMythCommand#RESCHEDULE_RECORDINGS} changed. TODO</li>
	 * </ul>
	 * 
	 * @since 2012-04-11
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_73(73,"D7FE8D6F",new String[][]{
		{DATE,"2012-04-11"},
		{GIT_COMMIT,"cbb8eb1ee32a658a519d2d5fb751ace114f63bf9"}
	}),
	
	/**
	 * MythTV Development Version 2012-05-09.
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingType.Type#TEMPLATE_RECORD} added.</li>
	 *  <li class='removed'>Some settings from the settings table were removed 
	 *  	(see <a href="https://github.com/MythTV/mythtv/commit/71c65ba67614c751ea149a1960e0db60437591af#diff-12">here</a>).<br>
	 *  	This settings are replaced by a default recording rule template.
	 *  </li>
	 * </ul>
	 */
	PROTO_VERSION_74(74,"SingingPotato",new String[][]{
		{DATE,"2012-05-09"},
		{GIT_COMMIT,"3fb9d6eb779e08d91df8a849a5be38e219c34bed"}
	}),
	
	/**
	 * MythTV <span class='releaseVersion'>Release Version 0.26</span> 2012-05-30.
	 * <p>
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='changed'>MythTV now return the localtime and not the UTC time for the timezone queries.</li>
	 *  <li class='changed'>Update DB times to UTC.</li>
	 * </ul>
	 * 
	 * @since 2012-05-30
	 * @mythProtoVersionInfo
	 */
	PROTO_VERSION_75(75,"SweetRock",new String[][]{
		{DATE,"2012-05-30"},
		{GIT_COMMIT,
			"1f8c59021075d4c46889d19b0082c6dcdf04a455," + 
			"a7cf09d9df392e8ce22d05d19bb10dc779fa9583," + 
			"4f028f388c38c1677b6c79c5efcc461f8b20cb4c"
		},
		{MYTH_RELEASE,"0.26"}
	}),	
	
	/**
	 * MythTV Development Version 2012-11-23.
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link org.jmythapi.protocol.response.IProgramInfo.Props#SYNDICATED_EPISODE} added.</li>
	 *  <li class='changed'>Some {@link IMythCommand command} response messages changed from {@code ok} to {@code OK}</li>
	 * </ul>
	 */
	PROTO_VERSION_76(76,"FireWilde",new String[][]{
		{DATE,"2012-11-23"},
		{GIT_COMMIT,
			"d4dcff374e," + 
			"9acc8531963129714201dc263bbf94e0d963fed8," + 
			"dfd37f38fc0c8874aeab09ac98a9cb488c8c3a89"
		  }
	}),
	
	/**
	 * MythTV <span class='releaseVersion'>Release Version 0.27</span> Version 2013-01-01.
	 * 
	 * <b>Protocol Changes:</b>
	 * <ul class='protocolChanges'>
	 * 	<li class='added'>{@link IMythCommand#QUERY_RECORDER_FILL_DURATION_MAP} added.</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingType.Type#FIND_DAILY_RECORD} removed.</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingType.Type#FIND_WEEKLY_RECORD} removed.</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingType.Type#CHANNEL_RECORD} removed.</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingType.Type#TIMESLOT_RECORD} removed.</li>
	 *  <li class='removed'>{@link org.jmythapi.protocol.response.IProgramRecordingType.Type#WEEKSLOT_RECORD} removed.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingType.Type#DAILY_RECORD} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingType.Type#WEEKLY_RECORD} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingFilters.Filters#THIS_TIME} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingFilters.Filters#THIS_DAY_AND_TIME} added.</li>
	 *  <li class='added'>{@link org.jmythapi.protocol.response.IProgramRecordingFilters.Filters#THIS_CHANNEL} added.</li>
	 *  <li class='added'>{@link IMythCommand#BACKEND_MESSAGE_FILE_WRITTEN} added.</li>
	 * </ul>
	 */
	PROTO_VERSION_77(77,"WindMark",new String[][]{
		{DATE,"2013-01-01"},
		{GIT_COMMIT,
			"49dbed5be0," +
			"a1f979393d4897d91b338581a14a2e245d76fa16," + 
			"9497ba1b63a5a6a91b06dbb85beea37444ef3ccc," + 
			"030ba69"
		},
		{MYTH_RELEASE,"0.26"}
	}),

	PROTO_VERSION_78(78,"IceBurns", new String [][]{
			{DATE,"2013-10-13"},
			{GIT_COMMIT,"e8bfd99e2"},
			{MYTH_RELEASE,"0.28"}
	}),

	PROTO_VERSION_79(79,"BasaltGiant", new String [][]{
			{DATE,"2013-10-23"},
			{GIT_COMMIT,"e8bfd99e2"},
			{MYTH_RELEASE,"0.28"}
	}),

	PROTO_VERSION_80(80,"TaDah!", new String [][]{
			{DATE,"2014-03-16"},
			{GIT_COMMIT,"8aefe1bcf0,"+"2f1a5350be,"+"b257f3c860,"+"d9217461ff,"+"d0185093e,"+"e6c8a78685,"
			+"e350c8fb0,"+"b9d40e407,"+"efafb148f,"},
			{MYTH_RELEASE,"0.28"}
	}),

	PROTO_VERSION_81(81,"MultiRecDos", new String [][]{
			{DATE,"2014-05-08"},
			{GIT_COMMIT,"cd8666848"},
			{MYTH_RELEASE,"0.28"}
	}),

	PROTO_VERSION_82(82,"IdIdO", new String [][]{
			{DATE,"2014-06-02"},
			{GIT_COMMIT,"e8a99d45c"},
			{MYTH_RELEASE,"0.28"}
	}),

	PROTO_VERSION_83(83,"BreakingGlass", new String [][]{
			{DATE,"2014-07-22"},
			{GIT_COMMIT,"1dab19079"},
			{MYTH_RELEASE,"0.28"}
	}),

	PROTO_VERSION_84(84,"CanaryCoalmine", new String [][]{
			{DATE,"2015-01-29"},
			{GIT_COMMIT,"b220116f77"},
			{MYTH_RELEASE,"0.28"}
	}),

	PROTO_VERSION_85(85,"BluePool", new String [][]{
			{DATE,"2015-02-04"},
			{GIT_COMMIT,"a4f65ce15b"},
			{MYTH_RELEASE,"0.28"}
	}),

	PROTO_VERSION_86(86,"(ノಠ益ಠ)ノ彡┻━┻ ", new String [][]{
			{DATE,"2015-04-27"},
			{GIT_COMMIT,"bcd7d65ef7"},
			{MYTH_RELEASE,"0.28"}
	}),

	PROTO_VERSION_87(87,"(ノಠ益ಠ)ノ彡┻━┻ (No entiendo!)", new String [][]{
			{DATE,"2015-05-15"},
			{GIT_COMMIT,"189a7be2a" + "d273696311"},
			{MYTH_RELEASE,"0.28"}
	}),

	PROTO_VERSION_88(88,"XmasGift", new String [][]{
			{DATE,"2015-08-19"},
			{GIT_COMMIT,"a2676c2dd," + "20603add4"},
			{MYTH_RELEASE,"0.28"}
	}),
	
	/**
	 * MythTV Current Development Version.
	 */
	PROTO_VERSION_LATEST(-1,"")
	;
	
	/* ============================================================================
	 * Protocol version METHODS
	 * ============================================================================ */
	private int protoVersion = -1;
	private String protoToken = null;
	private Map<String,String> metaData = new TreeMap<String, String>();
	
	private ProtocolVersion(int protoVersion,String protoToken) {
		this(protoVersion,protoToken,null);
	}
	
	private ProtocolVersion(int protoVersion, String protoToken, String[][] metaDataValues) {
		this.protoVersion = protoVersion;
		this.protoToken = protoToken;
		if(metaDataValues != null) {
			for(String[] metaDataValue : metaDataValues) {
				if(metaDataValue == null || metaDataValue.length == 0) continue;
				final String key = metaDataValue[0];
				final String value = metaDataValue.length>1?metaDataValue[1]:null;
				metaData.put(key,value);
			}
		}
	}
	
	/**
	 * Gets the protocol version number.
	 * 
	 * @return
	 * 		the version number, e.g. {@code 5}
	 */
	public int getVersion() {
		return this.protoVersion;
	}
	
	/**
	 * Gets the protocol version "handshake" token.
	 * <p>
	 * This token is used starting from version {@link #PROTO_VERSION_62 62} to
	 * establish a connection with the backend.
	 * 
	 * @return
	 * 		the handshake token.
	 */
	public String getToken() {
		return this.protoToken;
	}
	
	/**
	 * Gets some known metadata about the protocol version.
	 * <p>
	 * Metadata are e.g. the Date and SVN revision the version was 
	 * introduced, or the Git commit id. See {@link ProtocolVersionInfo}
	 * for the supported types of data.
	 * 
	 * @return
	 * 		metadata about the protocol version.
	 */
	public Map<String,String> getMetaData() {
		return this.metaData;
	}
	
	/**
	 * Gets the predecessor version of the current version.
	 * @return
	 * 		the predecessor version.
	 */
	public ProtocolVersion getPredecessor() {
		final int idx = this.ordinal();
		if(idx == 0) return null;
		return ProtocolVersion.values()[idx-1];
	}
	
	/**
	 * Gets the successor version of the current version.
	 * @return
	 * 		the successor version.
	 */	
	public ProtocolVersion getSuccessor() {
		final int idx = this.ordinal();
		if(idx == PROTO_VERSION_LATEST.ordinal()) return null;
		return ProtocolVersion.values()[idx+1];
	}
	
	/**
	 * Gets the protocol-version whose value matches the given protocol version.
	 * 
	 * @param protoVersion
	 * 		the requested protocol version
	 * @return
	 * 		the found protocol version property or {@code null}.
	 */
	public static ProtocolVersion valueOf(int protoVersion) {
		for (ProtocolVersion version : ProtocolVersion.values()) {
			if(version.getVersion() == protoVersion) return version;
		}
		return null;
	}
	
	/**
	 * Gets the maximum protocol version that is currently supported.
	 * <p>
	 * This returns the last enum constant excluding {@link #PROTO_VERSION_LATEST}.
	 * 
	 * @return
	 * 		the maximum supported protocol version
	 */
	public static ProtocolVersion getMaxVersion() {
		final ProtocolVersion[] versions = ProtocolVersion.values();
		return versions[versions.length-2];
	}
}

