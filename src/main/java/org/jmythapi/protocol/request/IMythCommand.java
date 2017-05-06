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
package org.jmythapi.protocol.request;

import static org.jmythapi.protocol.ProtocolVersion.*;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import java.util.List;

import org.jmythapi.IGuideDataThrough;
import org.jmythapi.IRecorderChannelInfo;
import org.jmythapi.IRecorderInfo;
import org.jmythapi.IVersionable;
import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.IBackendConnection;
import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.IRecorder;
import org.jmythapi.protocol.IRemoteEncoder;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.annotation.MythProtocolCmd;
import org.jmythapi.protocol.events.IAskRecording;
import org.jmythapi.protocol.events.IClearSettingsCache;
import org.jmythapi.protocol.events.ICommflagStart;
import org.jmythapi.protocol.events.IDoneRecording;
import org.jmythapi.protocol.events.IDownloadFile;
import org.jmythapi.protocol.events.IDownloadFileFinished;
import org.jmythapi.protocol.events.IDownloadFileUpdate;
import org.jmythapi.protocol.events.IFileWritten;
import org.jmythapi.protocol.events.ILiveTvChainUpdate;
import org.jmythapi.protocol.events.IPixmapGenerated;
import org.jmythapi.protocol.events.IRecordingListChangeAdd;
import org.jmythapi.protocol.events.IRecordingListChangeDelete;
import org.jmythapi.protocol.events.IRecordingListChangeList;
import org.jmythapi.protocol.events.IRecordingListChangeSingle;
import org.jmythapi.protocol.events.IRecordingListChangeUpdate;
import org.jmythapi.protocol.events.IResetIdleTime;
import org.jmythapi.protocol.events.IScheduleChange;
import org.jmythapi.protocol.events.IShutdownCountdown;
import org.jmythapi.protocol.events.ISystemEvent;
import org.jmythapi.protocol.events.IUpdateFileSize;
import org.jmythapi.protocol.events.IUpdateProgInfo;
import org.jmythapi.protocol.events.IVideoListChange;
import org.jmythapi.protocol.events.IVideoListNoChange;
import org.jmythapi.protocol.events.impl.AskRecording;
import org.jmythapi.protocol.events.impl.ClearSettingsCache;
import org.jmythapi.protocol.events.impl.CommflagStart;
import org.jmythapi.protocol.events.impl.DoneRecording;
import org.jmythapi.protocol.events.impl.DownloadFile;
import org.jmythapi.protocol.events.impl.FileWritten;
import org.jmythapi.protocol.events.impl.LiveTvChain;
import org.jmythapi.protocol.events.impl.PixmapGenerated;
import org.jmythapi.protocol.events.impl.RecordingListChange;
import org.jmythapi.protocol.events.impl.ResetIdleTime;
import org.jmythapi.protocol.events.impl.ScheduleChange;
import org.jmythapi.protocol.events.impl.ShutdownCountdown;
import org.jmythapi.protocol.events.impl.SystemEvent;
import org.jmythapi.protocol.events.impl.UpdateFileSize;
import org.jmythapi.protocol.events.impl.UpdateProgInfo;
import org.jmythapi.protocol.events.impl.VideoListChange;
import org.jmythapi.protocol.events.impl.VideoListNoChange;
import org.jmythapi.protocol.response.*;
import org.jmythapi.protocol.utils.CommandUtils;

/**
 * This interface represents a MythTV-protocol command.
 * <p>
 * A MythTV-protocol command usually consists of a command-name and optionally a list of command arguments, e.g.
 * "{@code QUERY_REMOTEENCODER 1}".<br> 
 * The command is send as part of a {@link IMythRequest request-message} to the backend server.
 * The result of the command-processing is returned as a {@link IMythResponse response-message} back to the client.
 * <p>
 * Both, the request- and response-messages are transfered over network in the form of an {@link IMythPacket packet}.<br>
 * 
 * <h3>Request/Response example:</h3>
 * 
 * The MythTV-request consists of a command with the name {@code QUERY_REMOTEENCODER} and an additional command parameter {@code 1}, separated by space. 
 * The additional request parameter {@code IS_BUSY} is also part of the rquest-message and is separated by {@code []:[]}.<br>
 * The MythTV-response consists of multiple response arguments, also separated by {@code []:[]}.
 * 
 * {@mythProtoExample notitle
 * 33      QUERY_REMOTEENCODER 1[]:[]IS_BUSY
 * 46      1[]:[]Tuner 1[]:[]1[]:[]1[]:[]1[]:[]0[]:[]1002
 * }
 * 
 * <h3>Supported Commands:</h3>
 * 
 * The Field Summary table listed below contains a list of commands supported by a MythTV-backend.<br>
 * You can either send commands directly to a backend using a {@link IBackendConnection}, 
 * or you can use the more convenient functions provided by {@link IBackend} class.
 * Depending on the protocol version, different commands with different command- and request-arguments and different response-arguments are supported. 
 * 
 * <h3>Supported Protocol Versions:</h3>
 * 
 * All commands listed on this page are supported by at least one of the supported protocol-versions. If a given command
 * is not supported by all known protocol-versions, it is marked with the {@link MythProtoVersionAnnotation MythProtoVersion} 
 * annotation. Additionally the lower bound of the version range is documented with the {@code @since} javadoc
 * tag, whereas the upper bound is documented with the {@code @deprecated} tag.
 * <p>
 * Which protocol versions are supported can be seen in the enumeration {@link ProtocolVersion}.
 * 
 * <h3>Usage Examples:</h3>
 * See the javadoc of the listed commands for examples, how to use the different commands.
 * 
 * @see ProtocolVersion protocol version
 * @see MythProtoVersionAnnotation protocol version range
 * @see CommandUtils command utils
 * @see IMythRequest request
 * @see IMythResponse response
 * @see IMythPacket packet
 * @see IBackendConnection connection
 */
@SuppressWarnings("deprecation")
public interface IMythCommand extends IVersionable {
	
	/* =======================================================================================
	 * MYTHTV - INIT COMMANDS
	 * ======================================================================================= */
	/**
	 * Negotiates the protocol version to use.
	 * <p>
	 * This command must be sent afer a backend connection was established, to check if the
	 * client and backend are speeking the same protocol version.
	 * 
	 * {@mythProtoExample
	 * An accepted protocol version:
	 * 21      MYTH_PROTO_VERSION 15
	 * 13      ACCEPT[]:[]15
	 * 
	 * A rejected protocol version:
	 * 30      MYTH_PROTO_VERSION 70 53153836
	 * 13      REJECT[]:[]63
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li>protocol version of the client</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 * 	<dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 * 	<dt><b>Response arguments:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ACCEPT} | {@code REJECT}</li>
	 * 			<li><div class='paramType'>int</div> protocol version of the server</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 01}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackendConnection#open()
	 * @see <a href="http://www.mythtv.org/wiki/MYTH_PROTO_VERSION_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_01))
	public static final String MYTH_PROTO_VERSION = "MYTH_PROTO_VERSION";	
	
	/**
	 * Announces a client to the backend.
	 * <p>
	 * This type of command registers as client to the backend and announces a new connection of the given type.
	 * <p>
	 * The following connection types are available:
	 * <ul>
	 * 	<li>{@link #ANN_PLAYBACK}: for blocking playback connections</li>
	 * 	<li>{@link #ANN_MONITOR}: for non blocking monitor connections (Since {@mythProtoVersion 22})</li>
	 * 	<li>{@link #ANN_FILE_TRANSFER}: for file transfer connections</li>
	 * 	<li>{@link #ANN_RING_BUFFER}: for ring-buffer connections (Removed in {@mythProtoVersion 20})</li>
	 *  <li>{@link #ANN_SLAVEBACKEND}: used by slave backends</li>
	 *  <li>{@link #ANN_MEDIASERVER}: used by a mediaserver accessing the backend (Since {@mythProtoVersion 68})</li>
	 * </ul> 
	 * 
	 * {@mythProtoExample
	 * Announce a playback connection:
	 * 25      ANN Playback MythClient 0
	 * 2       OK
	 * 
	 * Announce a monitoring connection:
	 * 24      ANN Monitor MythClient 0
	 * 2       OK
	 * 
	 * Announce a file transfer:
	 * 64      ANN FileTransfer mythtv 0 -1[]:[]/channels/atv_at.jpg[]:[]Default
	 * 24      OK[]:[]31[]:[]0[]:[]5752
	 * }
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see #ANN_PLAYBACK
	 * @see #ANN_MONITOR
	 * @see #ANN_FILE_TRANSFER
	 * @see #ANN_SLAVEBACKEND
	 * @see #ANN_RING_BUFFER
	 * @see #ANN_MEDIASERVER
	 * @see IBackend#annotatePlayback IBackend.annotatePlayback(...)
	 * @see IBackend#annotateMonitor IBackend.annotateMonitor(...)
	 * @see IBackend#annotateFileTransfer IBackend.annotateFileTransfer(...)
	 * @see IRecorder#annotateRingBuffer IBackend.annotateRingBuffer(...)
	 * @see <a href="http://www.mythtv.org/wiki/ANN_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String ANN = "ANN";
	
	/**
	 * Closes a backend connection.
	 * <p>
	 * This commands signals the backend that the client will release the connection. 
	 * 
	 * {@mythProtoExample
	 * 4       DONE
	 * }
	 * 
	 * <dl>
	 *  <dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 * 	<dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 * 	<dt><b>Response parameters:</b></dt>
	 * 	<dd>none</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00} 
	 * @mythProtoVersionRange
	 * 
	 * @see IBackendConnection#close()
	 * @see <a href="http://www.mythtv.org/wiki/DONE_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String DONE = "DONE";	
	
	/* =======================================================================================
	 * MYTHTV - MAIN COMMANDS
	 * ======================================================================================= */	
	/**
	 * Queries all recorded programs.
	 * <p>
	 * This command is used to query a list of recordings in the system.
	 * 
	 * {@mythProtoExample
	 * 21      QUERY_RECORDINGS Play
	 * 847     2[]:[]Lara Croft: Tomb Raider - Die Wiege des Lebens[]:[][]:[][]:[]Actionthriller[]:[]17[]:[]1[]:[]ORF1[]:[]ORF 1[]:[]myth://127.0.0.1:6543/17_20051223201000_20051223221000.nuv[]:[]1[]:[]336893888[]:[]1135365000[]:[]1135372200[]:[][]:[]0[]:[]0[]:[]mythtv[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]1135365000[]:[]1135372200[]:[]0[]:[]21[]:[]Default[]:[]0[]:[][]:[]196631507[]:[][]:[]1135516681[]:[]0.000000[]:[]1135292400[]:[]1[]:[]Vanilla Sky[]:[][]:[][]:[]Psychothriller[]:[]17[]:[]1[]:[]ORF1[]:[]ORF 1[]:[]myth://127.0.0.1:6543/17_20051227012500_20051227034500.nuv[]:[]1[]:[]1108228032[]:[]1135643100[]:[]1135651500[]:[][]:[]0[]:[]0[]:[]mythtv[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]1135643100[]:[]1135651500[]:[]0[]:[]5[]:[]Default[]:[]0[]:[][]:[]58711337[]:[][]:[]1135660161[]:[]0.000000[]:[]1135638000[]:[]1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>String</div> RecoringsType - {@code Play}, {@code Delete}, {@code Recording} (see {@link org.jmythapi.protocol.request.ERecordingsType here} for all constants) <div class='paramVersion'>Since {@mythProtoVersion 33}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 * 	<dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 * 	<dt><b>Response arguments:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> number of recordings</li>
	 * 			<li><div class='paramType'>progInfo[]</div> a list of recordings (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryRecordings()
	 * @see IBackend#queryRecordings(org.jmythapi.protocol.request.ERecordingsType)
	 * @see IProgramInfoList
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/QUERY_RECORDINGS">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String QUERY_RECORDINGS = "QUERY_RECORDINGS";	
	
	/**
	 * Queries a single recording.
	 * <p>
	 * This command is used to query single recording based on its channel-id and recording-starting-time or its basename.<br>
	 * See <a href='#queryRecordingsVariants'>here</a> for all possible variants.
	 * 
	 * {@mythProtoExample
	 * With timeslot:
	 * 49      QUERY_RECORDING TIMESLOT 1057 2010-08-11T13:37:47
	 * 443     OK[]:[]Tom auf heißer Spur[]:[]Der Kinderzimmer-Kobold[]:[][]:[][]:[]1057[]:[]1[]:[]ORF 1[]:[]ORF 1[]:[]1057_20100811133747.mpg[]:[]0[]:[]88213944[]:[]1281525300[]:[]1281526800[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-3[]:[]0[]:[]0[]:[]15[]:[]6[]:[]1281526667[]:[]1281526800[]:[]0[]:[]4[]:[]LiveTV[]:[]0[]:[][]:[]257651874[]:[][]:[]1281526803[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]LiveTV[]:[]1[]:[]2[]:[]0[]:[]
	 * 
	 * With basename:
	 * 48      QUERY_RECORDING BASENAME 1057_20100811133747.mpg
	 * 443     OK[]:[]Tom auf heißer Spur[]:[]Der Kinderzimmer-Kobold[]:[][]:[][]:[]1057[]:[]1[]:[]ORF 1[]:[]ORF 1[]:[]1057_20100811133747.mpg[]:[]0[]:[]88213944[]:[]1281525300[]:[]1281526800[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-3[]:[]0[]:[]0[]:[]15[]:[]6[]:[]1281526667[]:[]1281526800[]:[]0[]:[]4[]:[]LiveTV[]:[]0[]:[][]:[]257651874[]:[][]:[]1281526803[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]LiveTV[]:[]1[]:[]2[]:[]0[]:[]
	 * 
	 * On error:
	 * 48      QUERY_RECORDING BASENAME 1057_20100811133747.XYZ
	 * 5       ERROR
	 * }
	 * 
	 * <h4><a name='queryRecordingsVariants'>Command variants:</a></h4>
	 * The following varians of this command are possible:
	 * <ul>
	 * 	<li>Query recording by basename: {@link #QUERY_RECORDING_BASENAME}</li>
	 * 	<li>Query recording by timeslot: {@link #QUERY_RECORDING_TIMESLOT}</li>
	 * </ul>
	 * 
	 * @since {@mythProtoVersion 32}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryRecording(String)
	 * @see IBackend#queryRecording(Integer, java.util.Date)
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/QUERY_RECORDING">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_32,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="11794"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="5be603e42bdc34697fb6")
	}))
	public static final String QUERY_RECORDING = "QUERY_RECORDING";
				
	/**
	 * Returns the free space on the connected backend.
	 * <p>
	 * This command is similar to {@link #QUERY_FREE_SPACE_LIST}, except that it only returns the free space for
	 * the connected host. Other slave backends are not taken into account.
	 * <p>
	 * This is a replacement for {@link #QUERY_FREESPACE}.
	 * 
	 * {@mythProtoExample
	 * 16      QUERY_FREE_SPACE
	 * 320     mythbox[]:[]/mnt/VHS/mythpinky[]:[]0[]:[]-1[]:[]12[]:[]524288[]:[]0[]:[]-449278464[]:[]0[]:[]-925365248[]:[]<br/>mythbox[]:[]/var/lib/mythtv/livetv[]:[]1[]:[]-1[]:[]11[]:[]4096[]:[]0[]:[]144629480[]:[]0[]:[]29703436[]:[]<br/>mythbox[]:[]/var/lib/mythtv/recordings[]:[]1[]:[]-1[]:[]1[]:[]4096[]:[]0[]:[]144629480[]:[]0[]:[]29703436
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 * 	<dd>A list of:
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> hostname</li>
	 * 			<li><div class='paramType'>string</div> directories</li>
	 * 			<li><div class='paramType'>boolean</div> is_local</li>
	 * 			<li><div class='paramType'>int</div> filesystem id</li>
	 * 			<li><div class='paramType'>int</div> storage group id</li>
	 * 			<li><div class='paramType'>int</div> block size id</li>
	 * 			<li  class='paramDeprecated'><div class='paramType'>int,int</div> total space in bytes<div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 * 			<li><div class='paramType'>long</div> total space in bytes<div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 * 			<li  class='paramDeprecated'><div class='paramType'>int,int</div> used space in bytes<div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 * 			<li><div class='paramType'>long</div> used space in bytes<div class='paramVersion'>since {@mythProtoVersion 66}</div></li> 
	 * 		</ol>
	 *  </dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 17}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryFreeSpaceList(boolean)
	 * @see IFreeSpaceList
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/QUERY_FREE_SPACE">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_17))
	public static final String QUERY_FREE_SPACE = "QUERY_FREE_SPACE";
	
	/**
	 * Returns the free space of all backends.
	 * <p>
	 * When this command is called on the master backend, it returns every backend's disk space stats, prepended with the hostname of each.
	 * <p>
	 * This is a replacement for {@link #QUERY_FREESPACE}.
	 * 
	 * {@mythProtoExample
	 * 21      QUERY_FREE_SPACE_LIST
	 * 236     mythbox[]:[]mythbox:/var/lib/mythtv/livetv,mythbox:/var/lib/mythtv/recordings[]:[]1[]:[]1[]:[]10[]:[]4096[]:[]0[]:[]18761596[]:[]0[]:[]2985540[]:[]<br/>mythbox[]:[]TotalDiskSpace[]:[]0[]:[]-2[]:[]-2[]:[]0[]:[]0[]:[]18761596[]:[]0[]:[]2985540
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 * 	<dd>A list of:
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> hostname</li>
	 * 			<li><div class='paramType'>string</div> directories</li>
	 * 			<li><div class='paramType'>boolean</div> is_local</li>
	 * 			<li><div class='paramType'>int</div> filesystem id</li>
	 * 			<li><div class='paramType'>int</div> storage group id</li>
	 * 			<li><div class='paramType'>int</div> block size id</li>
	 * 			<li  class='paramDeprecated'><div class='paramType'>int,int</div> total space in bytes<div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 * 			<li><div class='paramType'>long</div> total space in bytes<div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 * 			<li  class='paramDeprecated'><div class='paramType'>int,int</div> used space in bytes<div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 * 			<li><div class='paramType'>long</div> used space in bytes<div class='paramVersion'>since {@mythProtoVersion 66}</div></li> 
	 * 		</ol>
	 * 		The last entry of the list has the directory name {@code TotalDiskSpace} and is a summary of all other entries.
	 *  </dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 17}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryFreeSpaceList(boolean)
	 * @see IFreeSpaceList
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/QUERY_FREE_SPACE_LIST">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_17))
	public static final String QUERY_FREE_SPACE_LIST = "QUERY_FREE_SPACE_LIST";
			
	/**
	 * Returns the free space on the connected backend.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 15      QUERY_FREESPACE
	 * 14      30534[]:[]9630
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 *  
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> totalspace im MB</li>
	 * 			<li><div class='paramType'>int</div> usedspace in MB</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 17}, use {@link #QUERY_FREE_SPACE} instead
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryFreeSpace()
	 * @see IFreeSpace
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_17))
	public static final String QUERY_FREESPACE = "QUERY_FREESPACE";		
	
	/**
	 * Queries the free space on all backends.
	 * <p>
	 * This command summarizes the free space on all backends.
	 * 
	 * {@mythProtoExample
	 * 24      QUERY_FREE_SPACE_SUMMARY
	 * 32      0[]:[]18761596[]:[]0[]:[]2985552
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 * 	<dt><b>Response parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li  class='paramDeprecated'><div class='paramType'>int,int</div> total space in MB<div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 * 			<li><div class='paramType'>long</div> total space in MB<div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 *  		<li  class='paramDeprecated'><div class='paramType'>int,int</div> used space in MB<div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 *  		<li><div class='paramType'>long</div> used space in MB<div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 * 		</ol>
	 * </dd>
	 * </dl>
	 * 
	 * @see IBackend#queryFreeSpaceSummary()
	 * @see IFreeSpaceSummary
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/QUERY_FREE_SPACE_SUMMARY">Wiki</a>
	 * 
	 * @mythProtoVersionRange
	 * @since {@mythProtoVersion 32}
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_32))
	public static final String QUERY_FREE_SPACE_SUMMARY = "QUERY_FREE_SPACE_SUMMARY";
	
	/**
	 * Queries the system load.
	 * <p>
	 * This command queries the system load on the backend.
	 * 
	 * {@mythProtoExample
	 * 10      QUERY_LOAD
	 * 22      0.04[]:[]0.03[]:[]0.08
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>double</div> current load</li>
	 * 			<li><div class='paramType'>double</div> average load last 5 mins</li>
	 * 			<li><div class='paramType'>double</div> average load last 15 mins</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 15}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryLoad()
	 * @see ILoad
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/QUERY_LOAD">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_15,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="5019"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="420af8584c9d979e1311")
	}))
	public static final String QUERY_LOAD = "QUERY_LOAD";
	
	/**
	 * Queries the uptime.
	 * <p>
	 * This command queries the uptime of the backend and returns the number of seconds the backend's host has been running.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 12      QUERY_UPTIME
	 * 4       3251
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> uptime in seconds</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 15}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryUptime()
	 * @see IUptime
	 * @see <a href="http://www.mythtv.org/wiki/QUERY_UPTIME_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_15,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="5019"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="420af8584c9d979e1311")
	}))
	public static final String QUERY_UPTIME = "QUERY_UPTIME";
	
	/**
	 * Queries the memory usage.
	 * <p>
	 * This command queries the memory usage of the backend.
	 * 
	 * {@mythProtoExample
	 * 14      QUERY_MEMSTATS
	 * 26      121[]:[]11[]:[]917[]:[]917
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> total RAM MB</li>
	 * 			<li><div class='paramType'>int</div> free RAM MB</li>
	 * 			<li><div class='paramType'>int</div> total VM MB</li>
	 * 			<li><div class='paramType'>int</div> free VM MB</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 15}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryMemStats()
	 * @see IMemStats
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_15,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="5019"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="420af8584c9d979e1311")
	}))
	public static final String QUERY_MEMSTATS = "QUERY_MEMSTATS";			
		
	/**
	 * Checks if a file exists.
	 * <p>
	 * This command asks the backend to check the file information on the program.
	 * <p> 
	 * The checkslaves parameter should be 1 to request that the query should be passed 
	 * on to the slave backend (given by the hostname field in the ProgramInfo) and return 
	 * a URI or 0 to ignore this field and check only on the local machine returning a file path.
	 * <br>
	 * Apart from the hostname as described above, as of version 0.22 only the chanid and recstartts 
	 * fields of the specified ProgramInfo record can be used to determine the program to be checked. 
	 * The command can thus be used as a mapping between chanid + recstartts and the path to the 
	 * recording. In this case the path-name must be set to {@code null}. <br/>
	 * See <code>ProgramInfo::GetRecordBasename(bool fromDB)</code> for details.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * Protocol version 32:
	 * 545     QUERY_CHECKFILE[]:[]1[]:[]Germany's next Topmodel[]:[][]:[]Wer wird Deutschlands n&auml;chstes Supermodel? mit Thomas Rath, Thomas Hayo[]:[]show/gameshow[]:[]1063[]:[]1[]:[]PRO7[]:[]Pro7[]:[]/var/lib/mythtv/livetv/1063_20110410104434.mpg[]:[]0[]:[]0[]:[]1302419100[]:[]1302426600[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]1[]:[]0[]:[]0[]:[]-2[]:[]0[]:[]0[]:[]15[]:[]6[]:[]1302425074[]:[]1302426600[]:[]0[]:[]4[]:[]LiveTV[]:[]0[]:[][]:[]181080940[]:[][]:[]1302419100[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]LiveTV[]:[]0[]:[]0[]:[]0[]:[]2011
	 * 52      1[]:[]/var/lib/mythtv/livetv/1063_20110410104434.mpg
	 * 
	 * Protocol version 15:
	 * 463     QUERY_CHECKFILE[]:[]Die Simpsons[]:[][]:[]Das geheime Bekenntnis Zeichentrick[]:[]Kinderprogramm[]:[]1000[]:[]1[]:[]PRO7[]:[]Pro7[]:[]myth://knoppmyth:6543/1000_20110804183000_20110804184000.nuv[]:[]0[]:[]110700582[]:[]1312475400[]:[]1312476000[]:[][]:[]0[]:[]0[]:[]knoppmyth[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-3[]:[]0[]:[]0[]:[]0[]:[]0[]:[]1312475400[]:[]1312476000[]:[]0[]:[]4[]:[]Default[]:[]0[]:[][]:[]216592835[]:[][]:[]1312475596[]:[]0.000000[]:[]1312408800[]:[]1
	 * 1       1
	 * 
	 * Protocol version 32: If the file is not available:
	 * 374     QUERY_CHECKFILE[]:[]1[]:[]Tagesschau[]:[][]:[][]:[]nachrichten[]:[]1002[]:[]4[]:[]ARD[]:[]ARD[]:[][]:[]0[]:[]1326096001[]:[]1326096181[]:[]0[]:[]mythbuntu11[]:[]1[]:[]3[]:[]2[]:[]1[]:[]-1[]:[]15[]:[]1[]:[]15[]:[]6[]:[]1326096001[]:[]1326096181[]:[]0[]:[]Default[]:[][]:[]212613461[]:[][]:[]1326094992[]:[]0[]:[][]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]1[]:[]2[]:[]0[]:[]2012
	 * 6       0[]:[]
	 * }
	 * <p>
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>bool</div> checkslaves - {@code 1}=true, {@code 0}=false <div class='paramVersion'>since {@mythProtoVersion 32}</div></li>
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> {@code 1}=exists, {@code 0}=not found</li>
	 * 			<li><div class='paramType'>string</div> filename <div class='paramVersion'>since {@mythProtoVersion 32}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/QUERY_CHECKFILE">Wiki</a>
	 * @see IBackend#queryCheckFile(IProgramInfo, Boolean)
	 * @see IFileStatus
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String QUERY_CHECKFILE = "QUERY_CHECKFILE";	
		
	/**
	 * Queries tv-guide data status.
	 * <p>
	 * This command asks the backend for the date and time the guide data is filled through.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 22      QUERY_GUIDEDATATHROUGH
	 * 16      2007-01-26 18:30
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>date</div> program guide date</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 15}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryGuideDataThrough()
	 * @see IGuideDataThrough
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/QUERY_GUIDEDATATHROUGH">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_15,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="5218"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="6dc73c31312658679e2c")
	}))
	public static final String QUERY_GUIDEDATATHROUGH = "QUERY_GUIDEDATATHROUGH";	
	
	/**
	 * Enqueues a recording for transcoding.
	 * <p>
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 *		 <ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> {@code 0}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 23}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_23,toInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="8033"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="e65f8b61245722844c54")
	}))
	public static final String QUEUE_TRANSCODE = "QUEUE_TRANSCODE";
	
	/**
	 * Stops transcoding of a recording.
	 * <p>
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 *		 <ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> {@code 0}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 23}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_23,toInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="8033"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="e65f8b61245722844c54")
	}))
	public static final String QUEUE_TRANSCODE_STOP = "QUEUE_TRANSCODE_STOP";		
	
	/**
	 * Enqueues a recording for transcoding and cutlist processing.
	 * <p>
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 *		 <ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> {@code 0}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 23}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_23,toInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="8033"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="e65f8b61245722844c54")
	}))
	public static final String QUEUE_TRANSCODE_CUTLIST = "QUEUE_TRANSCODE_CUTLIST";
	
	/**
	 * Stop a currently active recording. 
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 959     STOP_RECORDING[]:[]Fringe - Grenzf&auml;lle des FBI[]:[]Kontrolle[]:[]Der Sohn eines hochrangigen Massive Dynamic-Mitarbeiters wird entf&uuml;hrt. Bei der Flucht sterben mehrere Polizisten auf merkw&uuml;rdige Weise, n&auml;mlich durch die Waffe einer Kollegin, die sich auch selbst erschießt. Es scheint, als habe es die Fringe-Abteilung mit einer Art Bewusstseinskontrolle zu tun. Bei der &uuml;bergabe der geforderten 2 Millionen Dollar kommt schließlich heraus, dass der eigentliche Entf&uuml;hrer der 15-j&auml;hrige Tyler selbst ist, der jetzt Peter in seiner Gewalt hat ...[]:[][]:[]11123[]:[]10123[]:[]PULS 4[]:[]PULS 4[]:[]/var/lib/mythtv/recordings/11123_20110401233600.mpg[]:[]0[]:[]0[]:[]1301691898[]:[]1301695104[]:[]0[]:[]0[]:[]0[]:[]brain[]:[]1[]:[]1[]:[]1[]:[]1[]:[]-2[]:[]330[]:[]1[]:[]15[]:[]6[]:[]1301693760[]:[]1301695104[]:[]0[]:[]0[]:[]Default[]:[]0[]:[][]:[][]:[][]:[]1301693739[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]1[]:[]0[]:[]0[]:[]0
	 * 1       1
	 * 
	 * If the recording could not be stopped (Protocol Version 15):
	 * 421     STOP_RECORDING[]:[]Die Nanny[]:[][]:[][]:[]Serie[]:[]1015[]:[]14[]:[]SUPRT[]:[]Super RTL[]:[]myth://127.0.0.1:6543/1015_20110414231000_20110414231000.nuv[]:[]0[]:[]25010112[]:[]1302822600[]:[]1302822600[]:[][]:[]0[]:[]0[]:[]knoppmyth[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]2[]:[]0[]:[]15[]:[]6[]:[]1302822600[]:[]1302822600[]:[]0[]:[]5[]:[]Default[]:[]0[]:[][]:[]122965929[]:[][]:[]1302822682[]:[]0.000000[]:[]1302739200[]:[]1
	 * 2       -1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 *		 <ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder-id ({@code -1} if no recorder is recording the program)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#stopRecording(IProgramInfo)
	 * @see <a href="http://www.mythtv.org/wiki/STOP_RECORDING_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String STOP_RECORDING = "STOP_RECORDING";
	
	/**
	 * Check if a prgram is currently being recorded.
	 * <p>
	 * This command checks if a program is currently being recorded and return the number of the recorder or {@code 0}, if it is not being recorded.
	 * 
	 * {@mythProtoExample
	 * 423     CHECK_RECORDING[]:[]Unknown[]:[][]:[][]:[][]:[]1001[]:[]1[]:[]VOX[]:[]VOX[]:[]/var/lib/mythtv/livetv/1001_20100622062602.mpg[]:[]0[]:[]0[]:[]1277180761[]:[]1277181000[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]1[]:[]0[]:[]0[]:[]-2[]:[]0[]:[]0[]:[]15[]:[]6[]:[]1277180762[]:[]1277181000[]:[]0[]:[]4[]:[]LiveTV[]:[]0[]:[][]:[][]:[][]:[]1277180761[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]LiveTV[]:[]0[]:[]0[]:[]0[]:[]
	 * 1       1
	 * 
	 * If recording is not in progress:
	 * 368     CHECK_RECORDING[]:[]Tagesschau[]:[][]:[][]:[]nachrichten[]:[]1002[]:[]4[]:[]ARD[]:[]ARD[]:[][]:[]0[]:[]1326096001[]:[]1326096181[]:[]0[]:[]mythbuntu11[]:[]1[]:[]3[]:[]2[]:[]1[]:[]-1[]:[]15[]:[]1[]:[]15[]:[]6[]:[]1326096001[]:[]1326096181[]:[]0[]:[]Default[]:[][]:[]212613461[]:[][]:[]1326095104[]:[]0[]:[][]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]1[]:[]2[]:[]0[]:[]2012
	 * 1       0
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> card-id ({@code 0} if no recorder was found)</li>
	 *		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/CHECK_RECORDING">Wiki</a>
	 * @see IBackend#checkRecording(IProgramInfo)
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String CHECK_RECORDING = "CHECK_RECORDING";
	
	/**
	 * Marks a recording for deletion.
	 * <p>
	 * This command marks a file on the backend for deletion, following the existing deletion rules.<br>
	 * If the property {@code AutoExpireInsteadOfDelete} is set to {@code false}, the recording will be deleted immediatelly.
	 * <p>
	 * There are two variants of this command:
	 * <ul>
	 * 	<li>Delete recording by programInfo (<a href='DELETE_RECORDING_1'>link</a>)</li>
	 * 	<li>Delete recording by channel-id and recording-start-time (<a href='DELETE_RECORDING_2'>link</a>, since {@mythProtoVersion 41})</li>
	 * </ul>
	 * <p>
	 * If the recording is deleted by programInfo, or if {@code FORCE} is not set, this command only deletes the recording but not its metadata.<br>
	 * See {@link #FORCE_DELETE_RECORDING} if the recording metadata should be deleted too, or use the additional parameter {@code FORCE}.
	 * <p>
	 * If the recording is deleted by programInfo, or if {@code FORGET} is not set, this command only deletes the recording but not the recording history.<br>
	 * See {@link #FORGET_RECORDING} if the history of a recording should be deleted, or use the additional parameter {@code FORGET}.
	 * 
	 * {@mythProtoExample
	 * Delete by programInfo - File deleted:
	 * 975     DELETE_RECORDING[]:[]Pink! &ouml;sterreichs Starmagazin[]:[]Thema u.a.: Germany's next Topmodel[]:[]"Pink! &ouml;sterreichs Starmagazin" zeigt die VIPs in &ouml;sterreich und dem Rest der Welt, so wie sie wirklich sind: manchmal gl&auml;nzend, manchmal ungeschminkt, aber immer hautnah! Von Montag bis Freitag wird die schillernde Welt der Stars, Promis und Adabeis genauer beleuchtet. Dazu kommen noch die aktuellsten Trends aus den Gebieten Fashion, Lifestyle und Styling! Dabei ist "Pink!" alles: informativ, witzig, aktuell und kritisch. Nur eines ist "Pink!" nicht: todernst![]:[][]:[]11123[]:[]10123[]:[]PULS 4[]:[]PULS 4[]:[]myth://192.168.10.201:6543/11123_20110402000000.mpg[]:[]0[]:[]5213428[]:[]1301695104[]:[]1301696824[]:[]0[]:[]0[]:[]0[]:[]brain[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-3[]:[]332[]:[]0[]:[]15[]:[]6[]:[]1301695200[]:[]1301695200[]:[]0[]:[]4[]:[]Default[]:[]0[]:[][]:[][]:[][]:[]1301695225[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]1[]:[]0[]:[]0[]:[]0
	 * 2       -1
	 * 
	 * Delete by programInfo - File not found:
	 * 369     DELETE_RECORDING[]:[]Tagesschau[]:[][]:[][]:[]nachrichten[]:[]1002[]:[]4[]:[]ARD[]:[]ARD[]:[][]:[]0[]:[]1326096001[]:[]1326096181[]:[]0[]:[]mythbuntu11[]:[]1[]:[]3[]:[]2[]:[]1[]:[]-1[]:[]15[]:[]1[]:[]15[]:[]6[]:[]1326096001[]:[]1326096181[]:[]0[]:[]Default[]:[][]:[]212613461[]:[][]:[]1326095104[]:[]0[]:[][]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]1[]:[]2[]:[]0[]:[]2012
	 * 2       -2
	 * 
	 * Delete by channel-id and recording-start-time - File deleted:
	 * 54      DELETE_RECORDING 1010 2012-01-10T15:43:00 FORCE FORGET
	 * 2       -1
	 * }
	 *  
	 * <h4 style="background-color: #CCCCCC; border-bottom: 1px solid black; border-top: 1px solid black;"><a name='DELETE_RECORDING_1'>Delete by ProgramInfo</a></h4>
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * </dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> {@code -1}=file deleted, {@code -2}=file not found, if the recording is in progress the recorder ID is returned.</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 *  
	 * <h4 style="background-color: #CCCCCC; border-bottom: 1px solid black; border-top: 1px solid black;">
	 * 		<a name='DELETE_RECORDING_2'>Delete by channel-id and recording-start-time (since {@mythProtoVersion 41}):</a>
	 * </h4>
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 *   		<li><div class='paramType'>string</div> {@code FORCE}  | {@code NO_FORCE} (optional) <div class='paramVersion'>since {@mythProtoVersion 56}</div></li> 
	 *   		<li><div class='paramType'>string</div> {@code FORGET} | {@code NO_FORGET} (optional) <div class='paramVersion'>since {@mythProtoVersion 59}</div></li>
	 *   	</ol> 
	 * 	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> channel id</li>
	 * 			<li><div class='paramType'>date</div> recording start time</li>
	 * 		</ol>
	 * </dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> {@code -1}=file deleted, {@code -2}=file not found, if the recording is in progress the recorder ID is returned.</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see #FORGET_RECORDING
	 * @see #FORCE_DELETE_RECORDING
	 * @see IBackend#deleteRecording(IProgramInfo)
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String DELETE_RECORDING = "DELETE_RECORDING";
	
	/**
	 * Deletes a failed recording.
	 * <p>
	 * This command was only available in {@mythProtoVersion 38}, therefore we will not implement it.
	 * 
	 * @since {@mythProtoVersion 38}
	 * @deprecated {@mythProtoVersion 39}, only available for a very short time.
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_38,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="15549"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="89c5f7ae25b7be547801")
	},to=PROTO_VERSION_39))
	public static final String DELETE_FAILED_RECORDING = "DELETE_FAILED_RECORDING";
	
	/**
	 * Forces deleting of a recording.
	 * <p>
	 * This command forces the backend to delete a recording and its metadata.<br>
	 * See {@link #DELETE_RECORDING} if only the recording but not its metadata should be deleted.
	 * 
	 * {@mythProtoExample
	 * 445     FORCE_DELETE_RECORDING[]:[]Eine Frage der Ehe[]:[]Til Death[]:[][]:[]comedy[]:[]1010[]:[]6[]:[]ORF1[]:[]ORF1[]:[]myth://192.168.10.207:6543/1010_20120109115200.mpg[]:[]458432000[]:[]1326105301[]:[]1326106801[]:[]0[]:[]mythbuntu11[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-3[]:[]24[]:[]0[]:[]0[]:[]0[]:[]1326106320[]:[]1326106800[]:[]5[]:[]Default[]:[][]:[]58973125[]:[][]:[]1326107293[]:[]0[]:[][]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]2[]:[]2[]:[]0[]:[]2006
	 * 2       -1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * </dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> {@code -1}=file deleted, {@code -2}=file not found, or {@code recorder-id} if the recording is in progress the recorder ID is returned.</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @see IBackend#forceDeleteRecording(IProgramInfo)
	 * 
	 * @since {@mythProtoVersion 16}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_16,fromInfo={
		@MythProtoVersionMetadata(key=GIT_COMMIT, value="834fedd51d451da70f40"),
		@MythProtoVersionMetadata(key=SVN_COMMIT, value="6235")
	}))
	public static final String FORCE_DELETE_RECORDING = "FORCE_DELETE_RECORDING";
	
	/**
	 * Reactivates a inactive recording.
	 * <p>
	 * This command allows inactive recordings to be reactivated.
	 * <p>
	 * This can be used fro things like restarting a program that was stopped or 
	 * deleted in progress or starting a program after correcting a low disk space
	 * or tuner busy condition.
	 * <p> 
	 * Starting with protocol version {@mythProtoVersion 19}, a recording can be only reactivated by
	 * setting the {@code reactivate} property in database table {@code oldrecorded}.
	 * 
	 * 	<dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>boolean</div> {@code 1}=success, {@code 0}=failed</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IBackend#reactivateRecording
	 * 
	 * @since {@mythProtoVersion 05}
	 * @deprecated {@mythProtoVersion 19}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_05,to=PROTO_VERSION_19,
		fromInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT, value="451ddbb1a38ed00ba527"),
			@MythProtoVersionMetadata(key=SVN_COMMIT, value="3411")
		},toInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT, value="bbc9f2ed33ca6340c82b"),
			@MythProtoVersionMetadata(key=SVN_COMMIT, value="7300")
		}
	))
	public static final String REACTIVATE_RECORDING = "REACTIVATE_RECORDING";	
	
	/**
	 * Undelete a recording.
	 * <p>
	 * This command allows to undelete a previously deleted recording.<br>
	 * This command only works if {@code AutoExpireInsteadOfDelete} is set to {@code 1} in MythTV-settings.
	 * <p>
	 * There are two variants of this command:
	 * <ul>
	 * 	<li>Undelete recording by programInfo (<a href='UNDELETE_RECORDING_1'>link</a>)</li>
	 * 	<li>Undelete recording by channel-id and recording-start-time (<a href='UNDELETE_RECORDING_2'>link</a>, since {@mythProtoVersion 56})</li>
	 * </ul>
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 426     UNDELETE_RECORDING[]:[]Test[]:[]Mo. Dez 26 15:24:00 2011[]:[][]:[][]:[]1001[]:[]1[]:[]PRO7[]:[]Pro7[]:[]myth://192.168.10.207:6543/1001_20111226152500.mpg[]:[]177472000[]:[]1324909440[]:[]1324909740[]:[]0[]:[]mythbuntu11[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-3[]:[]11[]:[]0[]:[]15[]:[]6[]:[]1324909500[]:[]1324909680[]:[]0[]:[]Default[]:[][]:[][]:[][]:[]1324909692[]:[]0[]:[][]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]0[]:[]0[]:[]0[]:[]0
	 * 1       0
	 * }
	 * 
	 * <h4 style="background-color: #CCCCCC; border-bottom: 1px solid black; border-top: 1px solid black;">
	 * 		<a name='UNDELETE_RECORDING_1'>Undelete by ProgramInfo</a>
	 * </h4>
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> {@code -1}=error, {@code 0}=ok</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * 
	 * <h4 style="background-color: #CCCCCC; border-bottom: 1px solid black; border-top: 1px solid black;">
	 * 		<a name='UNDELETE_RECORDING_2'>Undelete by channel-id and recording-start-time (since {@mythProtoVersion 56})</a>
	 * </h4>
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> channel id</li>
	 * 			<li><div class='paramType'>date</div> recording start time</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> {@code -1}=error, {@code 0}=ok</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 36}
	 * @mythProtoVersionRange
	 * 
	 * @see #DELETE_RECORDING
	 * @see IBackend#undeleteRecording(IProgramInfo)
	 * @see <a href="http://www.mythtv.org/wiki/UNDELETE_RECORDING_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_36))
	public static final String UNDELETE_RECORDING = "UNDELETE_RECORDING";
	
	/**
	 * Reschedules recordings.
	 * <p>
	 * This command forces the backend to reschedule all recordings with the given recording-id.
	 * 
	 * {@mythProtoExample
	 * Reschedule a given recording (protocol version &lt; 73):
	 * 25      RESCHEDULE_RECORDINGS 163
	 * 1       1
	 * 
	 * Rescheduling all recordings  (protocol version &lt; 73):
	 * 24      RESCHEDULE_RECORDINGS -1
	 * 1       1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recording-ID (Use <code>-1</code> ro reschedule all recordings)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> {@code 0} | {@code 1}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 15}
	 * @mythProtoVersionRange
	 *        
	 * @see IBackend#rescheduleRecordings
	 * @see <a href="http://www.mythtv.org/wiki/RESCHEDULE_RECORDINGS_%28Myth_Protocol%29">Wiki</a>
	 */	
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_15,
		fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT, value="5156"),
			@MythProtoVersionMetadata(key=GIT_COMMIT, value="e6c87e28e6e54e046882")
		}
	))
	public static final String RESCHEDULE_RECORDINGS = "RESCHEDULE_RECORDINGS";
	
	/**
	 * Forgets a recording.
	 * <p>
	 * This command deletes the history of a recording but not the recording itself or its metadata.<br>
	 * The recording is marked as never duplicates, so it can be re-recorded.
	 * <p>
	 * This is similar to the option "Forget Old" on MythWeb.
	 * 
	 * {@mythProtoExample
	 * 439     FORGET_RECORDING[]:[]Eine Frage der Ehe[]:[]Til Death[]:[][]:[]comedy[]:[]1010[]:[]6[]:[]ORF1[]:[]ORF1[]:[]myth://192.168.10.207:6543/1010_20120109115200.mpg[]:[]458432000[]:[]1326105301[]:[]1326106801[]:[]0[]:[]mythbuntu11[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-3[]:[]24[]:[]0[]:[]0[]:[]0[]:[]1326106320[]:[]1326106800[]:[]5[]:[]Default[]:[][]:[]58973125[]:[][]:[]1326107293[]:[]0[]:[][]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]2[]:[]2[]:[]0[]:[]2006
	 * 1       0
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> the recording to forget (if any, see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> {@code 0} always returns this value.</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#forgetRecording(IProgramInfo)
	 * @see <a href="http://www.mythtv.org/wiki/FORGET_RECORDING_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String FORGET_RECORDING = "FORGET_RECORDING";
	
	/**
	 * Queries all pending recordings.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * Example with one pending recording:
	 * 19      QUERY_GETALLPENDING
	 * 364     0[]:[]1[]:[]Charmed - Zauberhafte Hexen[]:[]Besessen[]:[][]:[]Mysteryserie[]:[]19[]:[]3[]:[]PRO7[]:[]PRO 7[]:[][]:[]0[]:[]0[]:[]1202382000[]:[]1202385600[]:[][]:[]0[]:[]0[]:[][]:[]1[]:[]1[]:[]1[]:[]0[]:[]-1[]:[]4[]:[]1[]:[]15[]:[]6[]:[]1202382000[]:[]1202385600[]:[]0[]:[]0[]:[]Default[]:[]0[]:[][]:[]202622814[]:[][]:[]1202371124[]:[]0.000000[]:[]1202338800[]:[]0
	 * 
	 * Example with no pending recordings:
	 * 19      QUERY_GETALLPENDING
	 * 7       0[]:[]0
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> temptable (optional; unknown purpose)</li>
	 * 			<li><div class='paramType'>int</div> recordid (optional; unknown purpose)</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> indicates whether there are any conflicts (0: No; 1: Yes)</li>
	 * 			<li><div class='paramType'>int</div> the number of programs pending</li>
	 * 			<li><div class='paramType'>progInfo[]</div> a list of recordings (if any)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryAllPending()
	 * @see IRecordingsPending
	 * @see <a href="http://www.mythtv.org/wiki/QUERY_GETALLPENDING_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String QUERY_GETALLPENDING = "QUERY_GETALLPENDING";
	
	/**
	 * Queries all scheduled recordings.
	 * <p>
	 * A command to query a list of Program objects which are scheduled to be recorded.
	 * 
	 * {@mythProtoExample
	 * Example with one scheduled recording:
	 * 21      QUERY_GETALLSCHEDULED
	 * 352     1[]:[]King of Queens[]:[]Ein Zeichen Gottes[]:[][]:[]Comedyserie[]:[]9[]:[]9[]:[]K1[]:[]KABEL 1[]:[][]:[]0[]:[]0[]:[]1202382600[]:[]1202384400[]:[][]:[]0[]:[]0[]:[][]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]13[]:[]1[]:[]15[]:[]6[]:[]1202382600[]:[]1202384400[]:[]0[]:[]0[]:[]Default[]:[]0[]:[][]:[]96652531[]:[][]:[]1202372524[]:[]0.000000[]:[]1202338800[]:[]0
	 * 
	 * Example with no scheduled recordings:
	 * 21      QUERY_GETALLSCHEDULED
	 * 1       0
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> the number of scheduled recordings</li>
	 * 			<li><div class='paramType'>progInfo[]</div> a list of programs (if any, see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryAllScheduled()
	 * @see IRecordingsScheduled
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/QUERY_GETALLSCHEDULED">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String QUERY_GETALLSCHEDULED = "QUERY_GETALLSCHEDULED";
	
	/**
	 * Queries all conflicting recordings.
	 * <p>
	 * A command to query a list of conflicting recordings.
	 * <p>
	 * <b>Note:</b><br>
	 * Starting with protocol version {@mythProtoVersion 57}, the backend seens not to return a conflicting recording anymore, without specifying a recording as request-parameter.
	 * 
	 * {@mythProtoExample
	 * Example with one conflict (<font color='red'>this seems not to work anymore starting with version 57</font>):
	 * 20      QUERY_GETCONFLICTING
	 * 872     1[]:[]Servus Kasperl[]:[]Kasperl &amp; Buffi[]:[]Heute: Buffi findet auf dem Fensterbrett einen Hut.[]:[][]:[]1000[]:[]2[]:[]ORF1[]:[]ORF 1[]:[]1000_20100514070528.mpg[]:[]0[]:[]76234564[]:[]1273813200[]:[]1273814700[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]15[]:[]6[]:[]1273813528[]:[]1273813680[]:[]0[]:[]0[]:[]LiveTV[]:[]0[]:[][]:[]219655276[]:[][]:[]1273813926[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]LiveTV[]:[]0[]:[]0[]:[]0
	 * 
	 * Example with no conflicts (<font color='red'>this seems not to work anymore starting with version 57</font>):
	 * 20      QUERY_GETCONFLICTING
	 * 1       0
	 * 
	 * Example with a given program and two conflicts:
	 * 380     QUERY_GETCONFLICTING[]:[]Sturm der Liebe[]:[][]:[][]:[]familienserie[]:[]1006[]:[]15[]:[]BR3[]:[]BR3[]:[][]:[]0[]:[]1326096301[]:[]1326099301[]:[]0[]:[]mythbuntu11[]:[]1[]:[]0[]:[]0[]:[]1[]:[]7[]:[]17[]:[]1[]:[]15[]:[]6[]:[]1326096001[]:[]1326099301[]:[]0[]:[]Default[]:[][]:[]148753397[]:[][]:[]1326095636[]:[]0[]:[][]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]2[]:[]0[]:[]2[]:[]2008
	 * 758     2[]:[]Len&szlig;en &amp; Partner[]:[]Das Killer-Video[]:[][]:[]krimiserie[]:[]1005[]:[]3[]:[]SAT.1[]:[]SAT.1[]:[]/var/lib/mythtv/recordings[]:[]0[]:[]1326096001[]:[]1326097801[]:[]0[]:[]mythbuntu11[]:[]1[]:[]3[]:[]2[]:[]1[]:[]-1[]:[]16[]:[]1[]:[]15[]:[]6[]:[]1326095701[]:[]1326097801[]:[]0[]:[]Default[]:[][]:[]58414658[]:[][]:[]1326095636[]:[]0[]:[][]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]2[]:[]0[]:[]0[]:[]2005[]:[]Tagesschau[]:[][]:[][]:[]nachrichten[]:[]1002[]:[]4[]:[]ARD[]:[]ARD[]:[][]:[]0[]:[]1326096001[]:[]1326096181[]:[]0[]:[]mythbuntu11[]:[]1[]:[]4[]:[]3[]:[]1[]:[]-1[]:[]15[]:[]1[]:[]15[]:[]6[]:[]1326096001[]:[]1326096181[]:[]0[]:[]Default[]:[][]:[]212613461[]:[][]:[]1326095636[]:[]0[]:[][]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]1[]:[]2[]:[]0[]:[]2012
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *   <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> the recording to test for conflicts (mandatory since {@mythProtoVersion 57}, 
	 * 				see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 *  </dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> the number of conflicting recordings</li>
	 * 			<li><div class='paramType'>progInfo[]</div> a list of recordings (if any, see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00} 
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryConflicting()
	 * @see IRecordingsConflicting
	 * @see <a href="http://www.mythtv.org/wiki/QUERY_GETCONFLICTING_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String QUERY_GETCONFLICTING = "QUERY_GETCONFLICTING";		
		
	/**
	 * Queries all expiring recordings.
	 * <p>
	 * A command to receive a list of expiring programs.
	 * 
	 * {@mythProtoExample
	 * 17      QUERY_GETEXPIRING
	 * 872     1[]:[]Servus Kasperl[]:[]Kasperl & Buffi[]:[]Heute: Buffi findet auf dem Fensterbrett einen Hut.[]:[][]:[]1000[]:[]2[]:[]ORF1[]:[]ORF 1[]:[]1000_20100514070528.mpg[]:[]0[]:[]76234564[]:[]1273813200[]:[]1273814700[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]15[]:[]6[]:[]1273813528[]:[]1273813680[]:[]0[]:[]0[]:[]LiveTV[]:[]0[]:[][]:[]219655276[]:[][]:[]1273813926[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]LiveTV[]:[]0[]:[]0[]:[]0
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> the number of expiring recordings</li>
	 * 			<li><div class='paramType'>progInfo[]</div> a list of recordings (if any, see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 23}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryExpiring()
	 * @see IRecordingsExpiring
	 * @see <a href="http://www.mythtv.org/wiki/QUERY_GETEXPIRING_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_23,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="8280"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="71beb18d90c7f2b9b7d9")
	}))
	public static final String QUERY_GETEXPIRING = "QUERY_GETEXPIRING";	
	
	/**
	 * Gets the next free recorder.
	 * <p>
	 * This command returns the next free recorder.<br>
	 * This command similar to {@link #GET_NEXT_FREE_RECORDER}, except that it tries to get a recorder on the 
	 * local machine if possible.
	 * 
	 * {@mythProtoExample
	 * 17      GET_FREE_RECORDER
	 * 24      1[]:[]127.0.0.1[]:[]6543
	 * 
	 * If no free recorder is available
	 * 17      GET_FREE_RECORDER
	 * 20      -1[]:[]nohost[]:[]-1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id ({@code -1} if no recorder is free)</li>
	 * 			<li><div class='paramType'>string</div> hostname of the recorder ({@code nohost} if no recorder is free)</li>
	 * 			<li><div class='paramType'>int</div> port of the recorder ({@code -1} if no recorder is free)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#getFreeRecorder()
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/GET_FREE_RECORDER">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String GET_FREE_RECORDER = "GET_FREE_RECORDER";
	
	/**
	 * Gets the amount of free recorders.
	 * <p>
	 * This command asks the backend for the number free recorders.<br>
	 * A free recorder is identifed as one that is connected, but not busy or locked. 
	 * 
	 * {@mythProtoExample
	 * 23      GET_FREE_RECORDER_COUNT
	 * 1       1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> amount of free recorders</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 09}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#getFreeRecorderCount()
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/GET_FREE_RECORDER_COUNT">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_09))
	public static final String GET_FREE_RECORDER_COUNT = "GET_FREE_RECORDER_COUNT";	
	
	/**
	 * Gets all free recorders.
	 * <p>
	 * This command asks the backend for a list of free recorders.<br>
	 * A free recorder is one that is connected but not busy or locked. 
	 * 
	 * {@mythProtoExample
	 * 22      GET_FREE_RECORDER_LIST
	 * 1       1
	 * 
	 * With multiple recorders:
	 * 22      GET_FREE_RECORDER_LIST
	 * 7       1[]:[]2
	 * 
	 * With no free recorders:
	 * 22      GET_FREE_RECORDER_LIST
	 * 1       0
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int[]</div> ids of free recorders</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 17}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#getFreeRecorderIDs()
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/GET_FREE_RECORDER_LIST">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_17,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="6392"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="a4bd91f4699ec453bac1")
	}))
	public static final String GET_FREE_RECORDER_LIST = "GET_FREE_RECORDER_LIST";
	
	/**
	 * Gets the next free recorder.
	 * <p>
	 * This command queries the next free recorder, starting with given recorder id.<br>
	 * This command similar to {@link #GET_FREE_RECORDER}, except that it returns the next free recorder, even if it is located on a remote backend.
	 * 
	 * {@mythProtoExample
	 * 29      GET_NEXT_FREE_RECORDER[]:[]-1
	 * 24      1[]:[]127.0.0.1[]:[]6543
	 * 
	 * If no free recorder is available
	 * 29      GET_NEXT_FREE_RECORDER[]:[]-1
	 * 20      -1[]:[]nohost[]:[]-1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> current recorder id ({@code -1} if not specified)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id ({@code -1} if no recorder is free)</li>
	 * 			<li><div class='paramType'>string</div> hostname of the recorder ({@code nohost} if no recorder is free)</li>
	 * 			<li><div class='paramType'>int</div> port of the recorder ({@code -1} if no recorder is free)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 03}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#getNextFreeRecorder()
	 * @see IRecorderInfo
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_03))
	public static final String GET_NEXT_FREE_RECORDER = "GET_NEXT_FREE_RECORDER";	
		
	/**
	 * Queries a recorder.
	 * <p>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String QUERY_RECORDER = "QUERY_RECORDER";		
		
	/**
	 * Changes LiveTV recording directory.
	 * <p>
	 * This command thells the backend where to put the next LiveTV recording.
	 * <p> 
	 * If a new LiveTV recording was started, the  MythTV encoder seems to wait max 500 ms to get the next LiveTV recording directory.<br>
	 * See <a href=https://raw.github.com/MythTV/mythtv/master/mythtv/libs/libmythtv/tv_rec.cpp>tv_rec.cpp</a> function {@code WaitForNextLiveTVDir()}.
	 * 
	 * {@mythProtoExample
	 * 33      SET_NEXT_LIVETV_DIR 3 /tmp/mythtv
	 * 2       OK
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 			<li><div class='paramType'>string</div> dirname</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code OK} | {@code bad}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 32}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#setNextLiveTvDirectory(String)
	 * @see <a href="http://www.mythtv.org/wiki/SET_NEXT_LIVETV_DIR_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_32))
	public static final String SET_NEXT_LIVETV_DIR = "SET_NEXT_LIVETV_DIR";
	
	/**
	 * Changes channel infos.
	 * <p>
	 * This command allows a frontend to change some channel information, and updates
	 * that data in all the currently running recorders.
	 * <p>
	 * To fetch the current channel information, {@link #QUERY_RECORDER_GET_CHANNEL_INFO} 
	 * can be used.
	 * <p> 
	 * {@mythProtoExample
	 * On success:
	 * 78      SET_CHANNEL_INFO[]:[]1001[]:[]1[]:[]1[]:[]PRO7[]:[]2[]:[]Pro7[]:[]prosieben.de
	 * 1       1
	 * 
	 * On error:
	 * 78      SET_CHANNEL_INFO[]:[]1001[]:[]0[]:[]1[]:[]PRO7[]:[]1[]:[]Pro7[]:[]prosieben.de
	 * 1       1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> channel id (<span class='valueImportant'>can not be changed</span>)</li>
	 * 			<li><div class='paramType'>int</div> source id (<span class='valueImportant'>can not be changed</span>)</li>
	 * 			<li><div class='paramType'>string</div> old channel number (required for channel renumbering)</li>
	 * 			<li><div class='paramType'>string</div> channel sign</li>
	 *			<li><div class='paramType'>string</div> new channel number</li>
	 * 			<li><div class='paramType'>string</div> channel name</li>
	 * 			<li><div class='paramType'>string</div> xmltv id</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>bool</div> {@code 0}=error, {@code 1}=success</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 28}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#setChannelInfo
	 * @see <a href="http://www.mythtv.org/wiki/SET_CHANNEL_INFO_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_28))
	public static final String SET_CHANNEL_INFO = "SET_CHANNEL_INFO";
	
	/**
	 * Queries a remote encoder.
	 * <p>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRemoteEncoder
	 * @see <a href="http://www.mythtv.org/wiki/QUERY_REMOTEENCODER_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String QUERY_REMOTEENCODER = "QUERY_REMOTEENCODER";		
	
	/**
	 * Gets a recorder by ID.
	 * <p>
	 * This command asks a backend for a specific recorder.<br>
	 * The host address and port returned may not be on the originally queried host, 
	 * if slave backends are used. If the recorder is not valid, {@code -1} will be returned 
	 * for the port and the host will be {@code nohost}.<br>
	 * Note that this does not check if a recorder is busy or locked. 
	 * 
	 * {@mythProtoExample
	 * 27      GET_RECORDER_FROM_NUM[]:[]1
	 * 18      127.0.0.1[]:[]6543
	 * 
	 * If no recorder is found
	 * 28      GET_RECORDER_FROM_NUM[]:[]20
	 * 13      nohost[]:[]-1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> hostname of the recorder ({@code nohost} if no recorder was found)</li>
	 * 			<li><div class='paramType'>int</div> port of the recorder ({@code -1} if no recorder was found)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#getRecorderForNum(int)
	 * @see IRecorderInfo
	 * <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/GET_RECORDER_FROM_NUM">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String GET_RECORDER_FROM_NUM = "GET_RECORDER_FROM_NUM";	
	
	/**
	 * Queries the recorder that is recording a given program.
	 * <p>
	 * This command asks a backend for a recorder that matches the program info passed in.
	 * <p> 
	 * The host address and port returned may not be on the originally queried host, 
	 * if slave backends are used. If the recorder is not valid, {@code -1} will be returned for 
	 * the port and the host will be {@code nohost}.<br> 
	 * 
	 * {@mythProtoExample
	 * 466     GET_RECORDER_NUM[]:[]ATV LIFE Hi Society[]:[][]:[][]:[]soziales[]:[]1002[]:[]1[]:[]ATV+[]:[]ATV+[]:[]myth://127.0.0.1:6543/1002_20100523194500.mpg[]:[]0[]:[]37590628[]:[]1274636100[]:[]1274638500[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-2[]:[]3[]:[]0[]:[]15[]:[]6[]:[]1274636700[]:[]1274638500[]:[]0[]:[]36[]:[]Default[]:[]0[]:[][]:[]112543609[]:[][]:[]1274636765[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]2[]:[]0[]:[]0[]:[]2010
	 * 24      1[]:[]127.0.0.1[]:[]6543
	 * 
	 * On error:
	 * 490     GET_RECORDER_NUM[]:[]Die Simpsons[]:[][]:[]Hailhouse Blues []:[]kinderprogramm[]:[]1063[]:[]1[]:[]PRO7[]:[]Pro7[]:[]myth://192.168.10.202:6543/1063_20110329201000.mpg[]:[]0[]:[]-2113007680[]:[]1301422500[]:[]1301424000[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-3[]:[]262[]:[]0[]:[]15[]:[]6[]:[]1301422200[]:[]1301424900[]:[]0[]:[]0[]:[]Default[]:[]0[]:[][]:[]216592835[]:[][]:[]1301424901[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]4[]:[]0[]:[]0[]:[]2011
	 * 20      -1[]:[]nohost[]:[]-1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id ({@code -1} if no recorder was found)</li>
	 * 			<li><div class='paramType'>string</div> hostname of the recorder ({@code nohost} if no recorder was found)</li>
	 * 			<li><div class='paramType'>int</div> port of the recorder ({@code -1} if no recorder was found)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#getRecorderNum
	 * @see IRecorderInfo
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/GET_RECORDER_NUM">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String GET_RECORDER_NUM = "GET_RECORDER_NUM";	
	
	/**
	 * Filetransfer queries.
	 * <p>
	 * As pre-requirement for this command, a {@link #ANN_FILE_TRANSFER file-transfer connection} needs to be established.<br>
	 * All subsequent commands reference the previously announced connection by its socket-id.
	 * <p>
	 * The following sub-commands for this command are possible:
	 * <ul>
	 * 		<li>Configure timeout: {@link #QUERY_FILETRANSFER_SET_TIMEOUT SET_TIMEOUT}</li>
	 * 		<li>Check if a file is open: {@link #QUERY_FILETRANSFER_IS_OPEN IS_OPEN}</li>
	 * 		<li>Reopen a file: {@link #QUERY_FILETRANSFER_REOPEN REOPEN}</li>		
	 * 		<li>Seek within a file: {@link #QUERY_FILETRANSFER_SEEK SEEK}</li>
	 *      <li>Read a file block: {@link #QUERY_FILETRANSFER_REQUEST_BLOCK REQUEST_BLOCK}</li>
	 * 		<li>Write a file block: {@link #QUERY_FILETRANSFER_WRITE_BLOCK WRITE_BLOCK}</li>
	 * 		<li>Finish file-transfer: {@link #QUERY_FILETRANSFER_DONE DONE}</li>
	 * </ul>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IFileTransfer
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String QUERY_FILETRANSFER = "QUERY_FILETRANSFER";	
	
	/**
	 * Generates a preview image of an recording.
	 * <p>
	 * This command asks the backend to generate a preview image of the requested show.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 467     QUERY_GENPIXMAP[]:[]ATV LIFE Hi Society[]:[][]:[][]:[]soziales[]:[]1002[]:[]1[]:[]ATV+[]:[]ATV+[]:[]myth://127.0.0.1:6543/1002_20100523194500.mpg[]:[]0[]:[]37590628[]:[]1274636100[]:[]1274638500[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-2[]:[]3[]:[]0[]:[]15[]:[]6[]:[]1274636700[]:[]1274638500[]:[]0[]:[]36[]:[]Default[]:[]0[]:[][]:[]112543609[]:[][]:[]1274636765[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]2[]:[]0[]:[]0[]:[]2010
	 * 2       OK
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 *  		<li><div class='paramType'>string</div> time_is_seconds (optional) <div class='paramVersion'>since {@mythProtoVersion 37}</div></li>
	 *  		<li><div class='paramType'>long,long</div> time (optional) <div class='paramVersion'>since {@mythProtoVersion 37}</div></li>
	 *  		<li><div class='paramType'>string</div> fileName (optional) <div class='paramVersion'>since {@mythProtoVersion 37}</div></li>
	 *  		<li><div class='paramType'>int</div> width (optional) <div class='paramVersion'>since {@mythProtoVersion 37}</div></li>
	 *  		<li><div class='paramType'>int</div> height (optional) <div class='paramVersion'>since {@mythProtoVersion 37}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code OK}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>

	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 61}, replaced by {@link #QUERY_GENPIXMAP2}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryGenPixmap(IProgramInfo)
	 * @see <a href="http://www.mythtv.org/wiki/QUERY_GENPIXMAP_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00, to=PROTO_VERSION_61))
	public static final String QUERY_GENPIXMAP = "QUERY_GENPIXMAP";			
	
	/**
	 * Generates a preview image of an recording.
	 * <p>
	 * This command asks the backend to generate a preview image of the requested show.
	 * <p>
	 * This command is a replacement for {@link #QUERY_GENPIXMAP}.
	 * 
	 * {@mythProtoExample
	 * 442     QUERY_GENPIXMAP2[]:[]do_not_care[]:[]Test5[]:[]Fr. Jul 1 12:29:00 2011[]:[][]:[][]:[]1000[]:[]01[]:[]TEST[]:[]Test[]:[]myth://192.168.10.144:6543/1000_20110701123000.mpg[]:[]65792000[]:[]1309516140[]:[]1309516260[]:[]0[]:[]mythbuntu11[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-3[]:[]5[]:[]0[]:[]15[]:[]6[]:[]1309516200[]:[]1309516260[]:[]2052[]:[]Default[]:[][]:[][]:[][]:[]1309516260[]:[]0[]:[][]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]0[]:[]0[]:[]0[]:[]0
	 * 2       OK
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> token</li>
	 *  		<li><div class='paramType'>string</div> time_is_seconds (optional)</li>
	 *  		<li><div class='paramType'>long,long</div> time (optional)</li>
	 *  		<li><div class='paramType'>string</div> fileName (optional)</li>
	 *  		<li><div class='paramType'>int</div> width (optional)</li>
	 *  		<li><div class='paramType'>int</div> height (optional)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code OK}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 61}, replaces {@link #QUERY_GENPIXMAP}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryGenPixmap2(String, IProgramInfo)
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_61))
	public static final String QUERY_GENPIXMAP2 = "QUERY_GENPIXMAP2";	
	
	/**
	 * Queries the last-modified date of a preview image.
	 * <p>
	 * This command asks the backend for the last-modified date of preview image of a given recording.
	 * <p>
	 * Starting with protocol {@mythProtoVersion 40} this command returns a unix-timestamp instead of a localized date string.
	 * 
	 * {@mythProtoExample
	 * 989     QUERY_PIXMAP_LASTMODIFIED[]:[]Jim Carreys Die Maske[]:[]Kom&ouml;die / USA / 1994[]:[]Stanley arbeitet in der Kreditabteilung einer großen Bank. Er ist ein gro&szlig;er Fan von Cartoons und Comics. Doch eines Tages &auml;ndert sich sein eint&ouml;niges Spie&szlig;erleben drastisch: Als er eine alte gr&uuml;ne Holzmaske findet und diese anprobiert, verschmilzt diese f&ouml;rmlich mit seinem Gesicht und verwandelt ihn in eine Art Superman, der &uuml;ber alle au&szlig;ergew&ouml;hnlichen F&auml;higkeiten von Cartoon-Stars verf&uuml;gt. Stanley treibt damit allerhand Schabernack und erobert das Herz der sch&ouml;nen Tina.[]:[][]:[]11123[]:[]10123[]:[]PULS 4[]:[]PULS 4[]:[]myth://192.168.10.201:6543/11123_20110313221000.mpg[]:[]0[]:[]-761164196[]:[]1300050920[]:[]1300057620[]:[]0[]:[]0[]:[]734575[]:[]brain[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-3[]:[]313[]:[]0[]:[]0[]:[]0[]:[]1300050600[]:[]1300058520[]:[]0[]:[]1[]:[]Default[]:[]0[]:[][]:[][]:[][]:[]1300819682[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]1[]:[]0[]:[]0[]:[]0
	 * 10      1302196901
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>proginfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li class='paramChanged'><div class='paramType'>string</div> {@code BAD} | date: last-modified-date (since {@mythProtoVersion 40} a timestamp is returned) <div class='paramVersion'>changed in {@mythProtoVersion 40}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 17}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryPixmapLastModified(IProgramInfo)
	 * @see <a href="http://www.mythtv.org/wiki/QUERY_PIXMAP_LASTMODIFIED_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_17,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="6308"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="e841c802ce4a12c9fc28")
	}))
	public static final String QUERY_PIXMAP_LASTMODIFIED = "QUERY_PIXMAP_LASTMODIFIED";	
	
	/**
	 * Queries a preview image.
	 * <p>
	 * This command can be used to download a preview image.<br>
	 * Optionally it can be specified that the preview image should only be returned if it was recently modified and 
	 * its file size is lower than the specified maximum.
	 * <p>
	 * Use {@link #QUERY_PIXMAP_LASTMODIFIED} if you only want to check the last modified date of the pixmap.
	 * 
	 * {@mythProtoExample
	 * If the pixmap was modified:
	 * 482     QUERY_PIXMAP_GET_IF_MODIFIED[]:[]-1[]:[]1048576[]:[]Lenßen &amp; Partner[]:[]Das Killer-Video[]:[][]:[]krimiserie[]:[]1005[]:[]3[]:[]SAT.1[]:[]SAT.1[]:[]myth://192.168.10.207:6543/1005_20120109085500.mpg[]:[]887680000[]:[]1326096001[]:[]1326097801[]:[]0[]:[]mythbuntu11[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-3[]:[]16[]:[]0[]:[]0[]:[]0[]:[]1326095700[]:[]1326096600[]:[]4[]:[]Deleted[]:[][]:[]58414658[]:[][]:[]1326096616[]:[]0[]:[][]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]2[]:[]0[]:[]0[]:[]2005
	 * 129115  1326106707[]:[]96808[]:[]52821[]:[]iVBORw0KGgoAAAANSUhEUgAAAUAA ..... ==
	 * 
	 * If the pixmap as not modified:
	 * 465     QUERY_PIXMAP_GET_IF_MODIFIED[]:[]1326279614[]:[]1048576[]:[]Test1[]:[]Di. Jän 10 09:00:00 2012[]:[][]:[][]:[]1001[]:[]1[]:[]PRO7[]:[]Pro7[]:[]myth://192.168.10.207:6543/1001_20120110090000.mpg[]:[]581440000[]:[]1326182400[]:[]1326183000[]:[]0[]:[]mythbuntu11[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-3[]:[]28[]:[]0[]:[]15[]:[]6[]:[]1326182400[]:[]1326183000[]:[]5[]:[]Default[]:[][]:[][]:[][]:[]1326187877[]:[]0[]:[][]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]0[]:[]0[]:[]0[]:[]0
	 * 10      1326279614
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> unix-timestamp - the last modified timestamp, use {@code -1} if not required</li>
	 * 			<li><div class='paramType'>int</div> max file size - if {@code 0} or the limit is exeeded only the timestamp will be returned.</li>
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 *  	On error:
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ERROR}</li>
	 * 			<li><div class='paramType'>string</div> error message<br>
	 * 				{@code 1: Parameter list too short} | {@code 2: Invalid ProgramInfo} | {@code 3: Failed to read preview file} | 
	 * 				{@code 5: Could not locate mythbackend that made this recording}</li>
	 * 		</ol>
	 * 
	 *  	On warning: 
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code WARNING} </li>
	 * 			<li><div class='paramType'>string</div> waring message <br>
	 * 				{@code 1: Preview file too big ...} | {@code 2: Could not locate requested file} | {@code 4: Preview file is invalid}</li>
	 * 		</ol>
	 * 
	 *  	On success: 
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> last-modified timestamp</li>
	 * 			<li><div class='paramType'>int</div> file size </li>
	 * 			<li><div class='paramType'>int</div> checksum </li>
	 *  		<li><div class='paramType'>bytes</div> data </li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IBackend#queryPixmapIfModified(IProgramInfo, java.util.Date, Long)
	 * @see IPixmap
	 * 
	 * @since {@mythProtoVersion 49}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_49))
	public static final String QUERY_PIXMAP_GET_IF_MODIFIED = "QUERY_PIXMAP_GET_IF_MODIFIED";
	
	/**
	 * Queries if a backend is recording.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * One LiveTV recording:
	 * 17      QUERY_ISRECORDING
	 * 7       1[]:[]1
	 * 
	 * One scheduled recording:
	 * 17      QUERY_ISRECORDING
	 * 7       1[]:[]0
	 * 
	 * No recordings:
	 * 17      QUERY_ISRECORDING
	 * 7       0[]:[]0
	 * 
	 * No recordings (protocol version 15):
	 * 17      QUERY_ISRECORDING
	 * 1       0
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recordings in progress</li>
	 * 			<li><div class='paramType'>int</div> live tv recordings <div class='paramVersion'>since {@mythProtoVersion 40}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecordingStatus
	 * @see IBackend#queryIsRecording()
	 * @see <a href="http://www.mythtv.org/wiki/QUERY_ISRECORDING_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String QUERY_ISRECORDING = "QUERY_ISRECORDING";
	
	/**
	 * Message.
	 * <p>
	 * This command is sent from a client to the backend.
	 * <p>
	 * If the server needs to send a message to the client, a {@link #BACKEND_MESSAGE} is used instead. 
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see <a href="http://www.mythtv.org/wiki/MESSAGE_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String MESSAGE = "MESSAGE";
	
	/**
	 * Updates the pathname and file-size of a recording.
	 * <p>
	 * This command asks the backend to fill in the pathname and filesize fields of a given ProgramInfo record and return it.<br>
	 * If the path is local to the play back host (as specified to the backend), then the returned pathname will be a local filename. 
	 * Otherwise, it will be a myth URI.
	 * 
	 * {@mythProtoExample
	 * 440     FILL_PROGRAM_INFO[]:[]MythTV-API[]:[]Unknown[]:[][]:[][]:[][]:[]1001[]:[]1[]:[]VOX[]:[]VOX[]:[]/var/lib/mythtv/livetv/1001_20100620200048.mpg[]:[]0[]:[]0[]:[]1277056847[]:[]1277058600[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]1[]:[]0[]:[]0[]:[]-2[]:[]0[]:[]0[]:[]15[]:[]6[]:[]1277056848[]:[]1277058600[]:[]0[]:[]4[]:[]LiveTV[]:[]0[]:[][]:[][]:[][]:[]1277056847[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]LiveTV[]:[]0[]:[]0[]:[]0[]:[]
	 * 407     Unknown[]:[][]:[][]:[][]:[]1001[]:[]1[]:[]VOX[]:[]VOX[]:[]<span class='valueHighlighted'>myth://mythbox:6543/1001_20100620200048.mpg</span>[]:[]0[]:[]<span class='valueHighlighted'>18325548</span>[]:[]1277056847[]:[]1277058600[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]1[]:[]0[]:[]0[]:[]-2[]:[]0[]:[]0[]:[]15[]:[]6[]:[]1277056848[]:[]1277058600[]:[]0[]:[]4[]:[]LiveTV[]:[]0[]:[][]:[][]:[][]:[]1277056847[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]LiveTV[]:[]0[]:[]0[]:[]0[]:[]
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#fillProgramInfo(IProgramInfo, String)
	 * @see IFileTransfer#updateFileSize()
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/FILL_PROGRAM_INFO">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String FILL_PROGRAM_INFO = "FILL_PROGRAM_INFO";		
	
	/**
	 * Locks a tuner.
	 * <p>
	 * This command asks the backend to lock a tuner.
	 * <p> 
	 * Locked tuners will not be used for scheduled recordings and are not available for use by other frontends. 
	 * The backend will attempt to re-schedule recordings to take this into account. The tuner should be 
	 * released when no longer required using {@link #FREE_TUNER}.<br/>
	 * 
	 * If no card ID is specified, the backend will choose an unlocked, unused tuner.
	 * 
	 * <b>ATTENTION:</b> This only works if your frontend is on the same machine as the tuner you are trying to lock.
	 * 
	 * {@mythProtoExample
	 * 12      LOCK_TUNER 1
	 * 27      1[]:[]/dev/video0[]:[][]:[]
	 * 
	 * On error:
	 * 12      LOCK_TUNER 1
	 * 17      -1[]:[][]:[][]:[]
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> tuner-id (optional)</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> tuner-id ({@code -1} if no card can be found with the specified ID; {@code -2} if tuner is already locked)</li>
	 * 			<li><div class='paramType'>string</div> video dev node</li>
	 * 			<li><div class='paramType'>string</div> audio dev node</li>
	 * 			<li><div class='paramType'>string</div> vbi dev node</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#lockTuner(Integer)
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/LOCK_TUNER">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String LOCK_TUNER = "LOCK_TUNER";
	
	/**
	 * Releases a locked tuner.
	 * <p>
	 * This command asks the backend to free a previously locked tuner.
	 * 
	 * {@mythProtoExample
	 * 12      FREE_TUNER 1
	 * 2       OK
	 * 
	 * On error:
	 * 13      FREE_TUNER 20
	 * 6       FAILED
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> tuner-id</li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code OK} | {@code FAILED}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#freeTuner(Integer)
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/FREE_TUNER">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String FREE_TUNER = "FREE_TUNER";		
	
	/**
	 * Checks if a backend is active.
	 * <p>
	 * This command checks if the given backend is active.
	 * 
	 * {@mythProtoExample
	 * 35      QUERY_IS_ACTIVE_BACKEND[]:[]mythbox
	 * 4       TRUE
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> hostname</li>
	 * 		</ol>
	 *  </dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code TRUE} | {@code FALSE}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#isActiveBackend(String)
	 * @see <a href="http://www.mythtv.org/wiki/QUERY_IS_ACTIVE_BACKEND_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String QUERY_IS_ACTIVE_BACKEND = "QUERY_IS_ACTIVE_BACKEND";	
	
	/**
	 * Queries the names of all active backends.
	 * <p>
	 * This command returns the names of all currently active backends.
	 * 
	 * {@mythProtoExample
	 * 21      QUERY_ACTIVE_BACKENDS
	 * 27      2[]:[]mythbox1[]:[]mythbox2
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string[]</div> the names of all active backends.</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#isActiveBackend(String)
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_72))
	public static final String QUERY_ACTIVE_BACKENDS = "QUERY_ACTIVE_BACKENDS";
	
	
	/**
	 * Queries commerical breaks.
	 * <p>
	 * <div class="notImplemented"><b>TODO:</b>  needs to be implemented.</div>
	 * 
	 * ATTENTION: The backend crashes if the recording does not exist.
	 * Parameters changed in {@mythProtoVersion 66}
	 * 
	 * @since {@mythProtoVersion 17}
	 * 
	 * @see <a href="http://www.mythtv.org/wiki/QUERY_COMMBREAK_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_17,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="6295"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="a18499516a398f8b170a")
	}))
	public static final String QUERY_COMMBREAK = "QUERY_COMMBREAK";
	
	/**
	 * Queries cutlists.
	 * <p>
	 * <div class="notImplemented"><b>TODO:</b>  needs to be implemented.</div>
	 * 
	 * ATTENTION: The backend crashes if the recording does not exist.<br>
	 * Parameters changed in {@mythProtoVersion 66}
	 * 
	 * @since {@mythProtoVersion 17}
	 * @mythProtoVersionRange
	 * 
	 * @see <a href="http://www.mythtv.org/wiki/QUERY_CUTLIST_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_17,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="6295"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="a18499516a398f8b170a")
	}))
	public static final String QUERY_CUTLIST = "QUERY_CUTLIST";
	
	/**
	 * Queries bookmarks.
	 * <p>
	 * This command is used to query the bookmark position for a given recording.
	 * <p>
	 * ATTENTION: The backend crashes if the recording does not exist.
	 * 
	 * {@mythProtoExample
	 * No bookmark available:
	 * 30      QUERY_BOOKMARK 1002 1312551660
	 * 7       0[]:[]0
	 * 
	 * With a bookmark:
	 * 30      QUERY_BOOKMARK 1002 1312551660
	 * 9       0[]:[]644
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> channel-id</li>
	 * 			<li><div class='paramType'>int</div> recording-start-time in seconds</li>
	 * 		</ol>
	 *  </dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li  class='paramDeprecated'><div class='paramType'>long, long</div> bookmark position in frames <div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 * 			<li><div class='paramType'>long</div> bookmark position in frames <div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 17}
	 * 
	 * @see IBackend#queryBookmark(IProgramInfo)
	 * @see <a href="http://www.mythtv.org/wiki/QUERY_BOOKMARK_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_17,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="6295"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="a18499516a398f8b170a")
	}))
	public static final String QUERY_BOOKMARK = "QUERY_BOOKMARK";
	
	/**
	 * Setting bookmarks.
	 * <p>
	 * This command is used to set the bookmark for a given recording to the given number of frames.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 34      SET_BOOKMARK 1002 1312551660 0 644
	 * 2       OK
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> channel-id</li>
	 * 			<li><div class='paramType'>int</div> recording-start-time in seconds</li>
	 * 			<li  class='paramDeprecated'><div class='paramType'>long, long</div> bookmark position in frames <div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 * 			<li><div class='paramType'>long64</div> bookmark position in frames <div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 * 		</ol>
	 *  </dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code OK} | {@code FAILED}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 17}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#setBookmark
	 * @see <a href="http://www.mythtv.org/wiki/SET_BOOKMARK_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_17,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="6295"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="a18499516a398f8b170a")
	}))
	public static final String SET_BOOKMARK = "SET_BOOKMARK";		
	
	/**
	 * Queries backend settings.
	 * <p>
	 * This command is used to query the remote host for a specific setting.<br>
	 * The backend will look in the MySQL database table 'settings', and 
	 * attempt to return the value for the given setting. It seems only settings 
	 * with the hostname set can be retrieved by this call.
	 * 
	 * {@mythProtoExample
	 * 26      QUERY_SETTING mythtv Theme
	 * 7       G.A.N.T
	 * }
	 * 
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> hostname</li>
	 * 			<li><div class='paramType'>string</div> settings-name</li>
	 * 		</ol>
	 *  </dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>String</div> setting-value ({@code -1} for unknown settings</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * 
	 * @since {@mythProtoVersion 17}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#querySetting(String, String)
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/QUERY_SETTING">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_17,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="6295"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="a18499516a398f8b170a")
	}))
	public static final String QUERY_SETTING = "QUERY_SETTING";
	
	/**
	 * Configure backend settings.
	 * <p>
	 * This command is used to set given setting on the remote host.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 20      SET_SETTING mythtv Language DE
	 * 2       OK
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> hostname</li>
	 * 			<li><div class='paramType'>string</div> settings-name</li>
	 * 			<li><div class='paramType'>string</div> settings-value</li>
	 * 		</ol>
	 *  </dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>String</div> {@code OK}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 17}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#setSetting(String, String, String)
	 * @see <a href="http://www.mythtv.org/wiki/SET_SETTING_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_17,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="6295"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="a18499516a398f8b170a")
	}))
	public static final String SET_SETTING = "SET_SETTING";
	
	/**
	 * Allows a backend to shut down.
	 * <p>
	 * This command signals a backend that a shutdown is allowed again, after a previous call to {@link #BLOCK_SHUTDOWN}.
	 * 
	 * {@mythProtoExample
	 * 14      ALLOW_SHUTDOWN
	 * 2       OK
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>String</div> {@code OK}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 19}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#allowShutdown()
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/ALLOW_SHUTDOWN">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_19))
	public static final String ALLOW_SHUTDOWN = "ALLOW_SHUTDOWN";
	
	/**
	 * Prevents a backend shutdown.
	 * <p>
	 * This command prevents a backend from shutting down until a the next call to {@link #ALLOW_SHUTDOWN}.
	 * 
	 * {@mythProtoExample
	 * 14      BLOCK_SHUTDOWN
	 * 2       OK
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>String</div> {@code OK}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 19}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#blockShutdown()
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/BLOCK_SHUTDOWN">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_19))
	public static final String BLOCK_SHUTDOWN = "BLOCK_SHUTDOWN";
	
	/**
	 * Shutdown a slave backend.
	 * <p>
	 * This command can be used to shutdown a slave backend.
	 * 
	 * {@mythProtoExample
	 * 12      SHUTDOWN_NOW
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>none</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#shutdownNow()
	 * @see <a href="http://www.mythtv.org/wiki/SHUTDOWN_NOW_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String SHUTDOWN_NOW = "SHUTDOWN_NOW";

	/**
	 * Backend Message.
	 * <p>
	 * This type of command represents a MythTV event message.
	 * 
	 * params:
	 * <ol class="cmdParams">
	 * <li><div class='paramType'>string</div> message</li>
	 * <li><div class='paramType'>string[]</div> extra <div class='paramVersion'>since {@mythProtoVersion 52}</div></li>
	 * </ol>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00))
	public static final String BACKEND_MESSAGE = "BACKEND_MESSAGE";	
	
	/**
	 * Reloading backend settings.
	 * <p>
	 * This command forces the backend to reload the backend settings.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 15      REFRESH_BACKEND
	 * 2       OK
	 * }
	 * 
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>String</div> {@code OK}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * 
	 * @since {@mythProtoVersion 05}
	 * @mythProtoVersionRange
	 *        
	 * @see IBackend#refreshBackend()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_05,
		fromInfo={
			@MythProtoVersionMetadata(key=GIT_COMMIT, value="381f5afb2b7fe120a165"),
			@MythProtoVersionMetadata(key=SVN_COMMIT, value="3412")
		}
	))
	public static final String REFRESH_BACKEND = "REFRESH_BACKEND";
	
	/**
	 * OK.
	 * 
	 * @since {@mythProtoVersion 16}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_16,fromInfo={
		@MythProtoVersionMetadata(key=GIT_COMMIT, value="77ec835feeb33505ad79"),
		@MythProtoVersionMetadata(key=SVN_COMMIT, value="5937")
	}))
	public static final String OK = "OK";
	
	/**
	 * Unknown Command.
	 * 
	 * @since {@mythProtoVersion 16}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_16,fromInfo={
		@MythProtoVersionMetadata(key=GIT_COMMIT, value="b1cbe8392fbc3f871ed5"),
		@MythProtoVersionMetadata(key=SVN_COMMIT, value="6025")
	}))
	public static final String UNKNOWN_COMMAND = "UNKNOWN_COMMAND";	

	/**
	 * Queries the timezone of a backend.
	 * <p> 
	 * {@mythProtoExample
	 * 15      QUERY_TIME_ZONE
	 * 46      Europe/Vienna[]:[]7200[]:[]2010-05-24T11:55:14
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>String</div> timezone</li>
	 * 			<li><div class='paramType'>int</div> UTC offset</li>
	 * 			<li><div class='paramType'>date</div> current date/time</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 42}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryTimeZone()
	 * @see ITimezone
	 * @see <a href="http://svn.mythtv.org/trac/changeset/18574">SVN Rev. 18574</a>
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/QUERY_TIME_ZONE">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_42))
	public static final String QUERY_TIME_ZONE = "QUERY_TIME_ZONE";

	/**
	 * Check the existence of a remote file.
	 * <p>
	 * This command asks the backend to query for the existence of a remote file.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * Example Live-TV:
	 * 57      QUERY_FILE_EXISTS[]:[]/1002_20100524113828.mpg[]:[]LiveTV
	 * 53      1[]:[]/var/lib/mythtv/livetv//1002_20100524113828.mpg
	 * 
	 * Example Recording:
	 * 59      QUERY_FILE_EXISTS[]:[]/11123_20110313221000.mpg[]:[]Default
	 * 58      1[]:[]/var/lib/mythtv/recordings//11123_20110313221000.mpg
	 * 
	 * Example Preview Image:
	 * 62      QUERY_FILE_EXISTS[]:[]/1063_20110329201000.mpg.png[]:[]Default
	 * 61      1[]:[]/var/lib/mythtv/recordings//1063_20110329201000.mpg.png
	 * 
	 * If the file could not be found:
	 * 33      QUERY_FILE_EXISTS[]:[]XYZ[]:[]ABC
	 * 1       0
	 * 
	 * Protocol version 63 example:
	 * 58      QUERY_FILE_EXISTS[]:[]/1000_20110701123000.mpg[]:[]Default
	 * 193     1[]:[]/var/lib/mythtv/recordings//1000_20110701123000.mpg[]:[]2049[]:[]136043[]:[]33188[]:[]1[]:[]109[]:[]117[]:[]0[]:[]65792000[]:[]4096[]:[]128504[]:[]1309516209[]:[]1309516260[]:[]1309516260
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> the remote file-name</li>
	 * 			<li><div class='paramType'>string</div> the storage group name (optional) <div class='paramVersion'>since {@mythProtoVersion 65}</div></li>
	 * 		</ol>
	 *  </dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> 0=not found, 1=exists</li>
	 * 			<li><div class='paramType'>string</div> Filepath</li>
	 * 			<li><div class='paramType'>int</div> device-id <div class='paramVersion'>since {@mythProtoVersion 59}</div></li>
	 * 			<li><div class='paramType'>int</div> inode number <div class='paramVersion'>since {@mythProtoVersion 59}</div></li>
	 * 			<li><div class='paramType'>int</div> protection mode <div class='paramVersion'>since {@mythProtoVersion 59}</div></li>
	 * 			<li><div class='paramType'>int</div> number of hard links <div class='paramVersion'>since {@mythProtoVersion 59}</div></li>
	 * 			<li><div class='paramType'>int</div> user id <div class='paramVersion'>since {@mythProtoVersion 59}</div></li>
	 * 			<li><div class='paramType'>int</div> group id <div class='paramVersion'>since {@mythProtoVersion 59}</div></li>
	 * 			<li><div class='paramType'>int</div> special device-id <div class='paramVersion'>since {@mythProtoVersion 59}</div></li>
	 * 			<li><div class='paramType'>int</div> file size <div class='paramVersion'>since {@mythProtoVersion 59}</div></li>
	 * 			<li><div class='paramType'>int</div> blocksize <div class='paramVersion'>since {@mythProtoVersion 59}</div></li>
	 * 			<li><div class='paramType'>int</div> blocks <div class='paramVersion'>since {@mythProtoVersion 59}</div></li>
	 * 			<li><div class='paramType'>date</div> last access time <div class='paramVersion'>since {@mythProtoVersion 59}</div></li>
	 * 			<li><div class='paramType'>date</div> last modified time <div class='paramVersion'>since {@mythProtoVersion 59}</div></li>
	 * 			<li><div class='paramType'>date</div> last status change <div class='paramVersion'>since {@mythProtoVersion 59}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 49}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryFileExists(IProgramInfo)
	 * @see IBackend#queryFileExists(String, String)
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_49,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="21770"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="d7a8d1a8f3555321868a")
	}))
	public static final String QUERY_FILE_EXISTS = "QUERY_FILE_EXISTS";
	
	/**
	 * Query a file hash value.
	 * <p>
	 * This command asks the backend to generate a has value for a backend file.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 60      QUERY_FILE_HASH[]:[]/1002_20100523194500.mpg.png[]:[]Default
	 * 16      75aa72141cd08662
	 * 
	 * Protocol Version 69 example:
	 * 77      QUERY_FILE_HASH[]:[]1011_20120216191600.mpg.png[]:[]Default[]:[]mythbuntu1125
	 * 16      64f9b17040b23f5f
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> the remote file-name</li>
	 * 			<li><div class='paramType'>string</div> the storage group name</li>
	 * 			<li><div class='paramType'>string</div> host name <div class='paramVersion'>since {@mythProtoVersion 69}</div></li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>String</div> hash-value</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 51}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryFileHash(String, String)
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_51,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="22886"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="d02b0d7c0ae9ffcb916d")
	}))
	public static final String QUERY_FILE_HASH = "QUERY_FILE_HASH";
	
	/**
	 * Put to sleep a backend.
	 * <p>
	 * This command allows to put a slave-backend to sleep on demand by the master-backend.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * On success:
	 * 11      GO_TO_SLEEP
	 * 2       OK
	 * 
	 * On error:
	 * 11      GO_TO_SLEEP
	 * 28      ERROR: SleepCommand is empty
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>String</div> {@code OK} | {@code ERROR: xxx}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 45}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#goToSleep()
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/GO_TO_SLEEP">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_45,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="20084"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="663cd89811cf7fc22f59")
	}))
	public static final String GO_TO_SLEEP = "GO_TO_SLEEP";
	
	/**
	 * Query hostname.
	 * <p>
	 * A command to determine the hostname of a backend.
	 * 
	 * {@mythProtoExample
	 * 14      QUERY_HOSTNAME
	 * 7       mythbox
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>String</div> hostname</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IBackend#queryHostname()
	 * 
	 * @since {@link ProtocolVersion#PROTO_VERSION_50 50}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_50))
	public static final String QUERY_HOSTNAME = "QUERY_HOSTNAME";
	
	/**
	 * Query storage group.
	 * <p>
	 * A command to query the a list of files from the remote storage group.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 80      QUERY_SG_GETFILELIST[]:[]brain[]:[]Default[]:[]/var/lib/mythtv/recordings/[]:[]0
	 * 1318    file::11101_20101211173500.mpg.idx::1099180[]:[]file::11101_20101211180000.mpg.idx::1152150[]:[]file::11101_20110102022000.mpg.idx::6257696[]:[]file::11120_20101211155800.mpg.idx::6491684[]:[]file::11123_20110109020100.mpg.png.new::0[]:[]file::11123_20110313221000.mpg::3533803100[]:[]file::11123_20110313221000.mpg.64.100x56.png::9512[]:[]file::11123_20110313221000.mpg.png::78698[]:[]file::11123_20110321200900.mpg::3815729968[]:[]file::11123_20110321200900.mpg.64.100x75.png::11611[]:[]file::11123_20110321200900.mpg.png::75487[]:[]file::11123_20110326225800.mpg::4360045804[]:[]file::11123_20110326225800.mpg.64.100x75.png::9877[]:[]file::11123_20110326225800.mpg.png::82990[]:[]file::11123_20110327201000.mpg::5503386604[]:[]file::11123_20110327201000.mpg.64.100x75.png::8396[]:[]file::11123_20110327201000.mpg.png::63560[]:[]file::11123_20110328200900.mpg::3980990052[]:[]file::11123_20110328200900.mpg.64.100x56.png::9086[]:[]file::11123_20110328200900.mpg.png::71258[]:[]file::11123_20110329200800.mpg::4834346868[]:[]file::11123_20110329200800.mpg.64.100x75.png::12481[]:[]file::11123_20110329200800.mpg.png::75233[]:[]file::11123_20110331200900.mpg::4408794016[]:[]file::11123_20110331200900.mpg.64.100x75.png::15014[]:[]file::11123_20110331200900.mpg.png::96947[]:[]file::11123_20110401233600.mpg.png::50061
	 * 
	 * Example 2 (with fileNamesOnly and all paths):
	 * 54      QUERY_SG_GETFILELIST[]:[]brain[]:[]Default[]:[]/[]:[]1
	 * 932     11101_20101211173500.mpg.idx[]:[]11101_20101211180000.mpg.idx[]:[]11101_20110102022000.mpg.idx[]:[]11120_20101211155800.mpg.idx[]:[]11123_20110109020100.mpg.png.new[]:[]11123_20110313221000.mpg[]:[]11123_20110313221000.mpg.64.100x56.png[]:[]11123_20110313221000.mpg.png[]:[]11123_20110321200900.mpg[]:[]11123_20110321200900.mpg.64.100x75.png[]:[]11123_20110321200900.mpg.png[]:[]11123_20110326225800.mpg[]:[]11123_20110326225800.mpg.64.100x75.png[]:[]11123_20110326225800.mpg.png[]:[]11123_20110327201000.mpg[]:[]11123_20110327201000.mpg.64.100x75.png[]:[]11123_20110327201000.mpg.png[]:[]11123_20110328200900.mpg[]:[]11123_20110328200900.mpg.64.100x56.png[]:[]11123_20110328200900.mpg.png[]:[]11123_20110329200800.mpg[]:[]11123_20110329200800.mpg.64.100x75.png[]:[]11123_20110329200800.mpg.png[]:[]11123_20110331200900.mpg[]:[]11123_20110331200900.mpg.64.100x75.png[]:[]11123_20110331200900.mpg.png[]:[]11123_20110401233600.mpg.png
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> hostname</li>
	 * 			<li><div class='paramType'>string</div> storage group</li>
	 * 			<li><div class='paramType'>string</div> path name</li>
	 * 			<li><div class='paramType'>boolean</div> fileNamesOnly <div class='paramVersion'>since {@mythProtoVersion 49}</div></li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>list</div> sequence of file-info objects</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 44}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryStorageGroupFileList(String, String, String, boolean)
	 * @see <a href="http://www.mythtv.org/wiki/QUERY_SG_GETFILELIST_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_44))
	public static final String QUERY_SG_GETFILELIST = "QUERY_SG_GETFILELIST";
	
	/**
	 * Query storage group file.
	 * <p>
	 * A command to query the a single file from the remote storage group.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 97      QUERY_SG_FILEQUERY[]:[]brain[]:[]Default[]:[]/var/lib/mythtv/recordings//11123_20110313221000.mpg
	 * 82      /var/lib/mythtv/recordings//11123_20110313221000.mpg[]:[]1300058521[]:[]3533803100
	 * 
	 * If the slave is not reachable:
	 * 46      QUERY_SG_FILEQUERY[]:[]unknown[]:[]ABC[]:[]DEF
	 * 29      SLAVE UNREACHABLE: []:[]brain
	 * 
	 * If the file could not be found:
	 * 44      QUERY_SG_FILEQUERY[]:[]brain[]:[]ABC[]:[]DEF
	 * 10      EMPTY LIST
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> hostname (since {@mythProtoVersion 58} the backend IP can be used as well).
	 *  			<div class='paramVersion'>changed in {@mythProtoVersion 58}</div>
	 *  		<li><div class='paramType'>string</div> storage group</li>
	 *  		<li class='paramChanged'><div class='paramType'>string</div> path to file (since {@mythProtoVersion 58} a relative pathname can be used as well).
	 *  			<div class='paramVersion'>changed in {@mythProtoVersion 58}</div>
	 *  		</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> file name</li>
	 *  		<li><div class='paramType'>int</div> last modified time</li>
	 *  		<li><div class='paramType'>long</div> file size</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 44}
	 * @mythProtoVersionRange
	 * 
	 * @see IStorageGroupFile
	 * @see IBackend#queryStorageGroupFile(String, String, String)
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/QUERY_SG_FILEQUERY">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_44))	
	public static final String QUERY_SG_FILEQUERY = "QUERY_SG_FILEQUERY";
	
	/**
	 * Downloads a file on the backend.
	 * <p>
	 * A command to instruct the backend to download a remote file into a local storage group asynchronously.
	 * <p>
	 * The command takes a URL, Storage Group name, and destination filename as arguments. 
	 * The reply contains the  myth:// URI of the file downloaded.
	 * <p>
	 * See {@link #BACKEND_MESSAGE_DOWNLOAD_FILE} how to receive notifications about an in progress or finished
	 * download job.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 101     DOWNLOAD_FILE[]:[]http://upload.wikimedia.org/wikipedia/de/7/7c/ORF-Logo.svg[]:[]Default[]:[]logo.svg
	 * 50      OK[]:[]myth://Default@192.168.10.207:6543/logo.svg
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> url to download</li>
	 *  		<li><div class='paramType'>string</div> storage-group name</li>
	 *  		<li><div class='paramType'>string</div> destination file-name</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code OK} | {@code ERROR}</li>
	 * 			<li><div class='paramType'>string</div> myth URI</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 *  
	 * @since {@mythProtoVersion 58}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#downloadFile
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_58,fromInfo={
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="7fce1004ab6e3692a5cfd1b6166d31f611447a05")			
	}))	
	public static final String DOWNLOAD_FILE = "DOWNLOAD_FILE";
	
	/**
	 * Downloads a file on the backend immediately.
	 * <p>
	 * A command to instruct the backend to download a remote file into a local storage group synchronously.
	 * <p>
	 * The command takes a URL, Storage Group name, and destination filename as arguments. 
	 * The reply contains the  myth:// URI of the file downloaded.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 105     DOWNLOAD_FILE_NOW[]:[]http://upload.wikimedia.org/wikipedia/de/7/7c/ORF-Logo.svg[]:[]Default[]:[]logo.svg
	 * 50      OK[]:[]myth://Default@192.168.10.207:6543/logo.svg
	 * }
	 * 
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> url to download</li>
	 *  		<li><div class='paramType'>string</div> storage-group name</li>
	 *  		<li><div class='paramType'>string</div> destination file-name</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code OK} | {@code ERROR}</li>
	 * 			<li><div class='paramType'>string</div> myth URI</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 58}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#downloadFileNow
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_58,fromInfo={
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="7fce1004ab6e3692a5cfd1b6166d31f611447a05")			
	}))	
	public static final String DOWNLOAD_FILE_NOW = "DOWNLOAD_FILE_NOW";
	
	/**
	 * Scan video folder.
	 * <p>
	 * This command triggers the scanning of all video folders.
	 * <p>
	 * 
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code OK} | {@code ERROR}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IBackend#scanVideos()
	 * 
	 * @since {@mythProtoVersion 64}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_64))
	public static final String SCAN_VIDEOS = "SCAN_VIDEOS";
	
	/**
	 * Delete a remote file.
	 * <p>
	 * This command allows to delete a remote file.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 37      DELETE_FILE[]:[]/logo.svg[]:[]Default
	 * 1       1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> filename</li>
	 *  		<li><div class='paramType'>string</div> storage-group <div class='paramVersion'>since {@mythProtoVersion 47}</div></li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code 0} | {@code 1}</li>
	 * 			<li><div class='paramType'>string</div> myth URI</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * 
	 * @since {@mythProtoVersion 46}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#deleteFile
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_46))
	public static final String DELETE_FILE = "DELETE_FILE";
	
	/* ========================================================================================
	 * ANN - COMMANDS
	 * ======================================================================================== */	
	
	/**
	 * Announces a client as playback client.
	 * <p>
	 * This command registers a client to the backend and announces a blocking playback connection.
	 * <p>
	 * <i>Note:</i> This connection type blocks a shutdown of the backend.
	 * 
	 * {@mythProtoExample
	 * 23      ANN Playback sycamore 0
	 * 2       OK
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>
	 *  	<ol class="cmdParams">
	 *  		<li><div class='paramType'>string</div> {@code Playback}</li>
	 * 			<li><div class='paramType'>string</div> host</li>
	 * 			<li class='paramChanged'><div class='paramType'>int</div> events-mode (see {@link org.jmythapi.protocol.request.EPlaybackSockEventsMode here} for all pssible events) <div class='paramVersion'>changed {@mythProtoVersion 57}</div></li>
	 * 		</ol>
	 *	</dd>
	 *
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code OK}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see <a href="http://www.mythtv.org/wiki/ANN_%28Myth_Protocol%29">Wiki</a>
	 * @see IBackend#annotatePlayback
	 * @see #ANN
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=ANN)
	public static final String ANN_PLAYBACK = "Playback";

	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=ANN)
	public static final String ANN_FRONTEND = "Frontend";

	/**
	 * Announces a client as slave backend.
	 * <p>
	 * This command registers a client to the backend and announces a slave backend connection.
	 * <p>
	 * <div class="notImplemented"><b>TODO:</b>  needs to be implemented.</div>
	 * 
	 * params:
	 * <ol class="cmdParams">
	 * <li>IPaddress</li>
	 * </ol>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see <a href="http://www.mythtv.org/wiki/ANN_%28Myth_Protocol%29">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=ANN)
	public static final String ANN_SLAVEBACKEND = "SlaveBackend";
		
	/**
	 * Announces a client as mediaserver.
	 * <p>
	 * This command registers a client to the backend and announces a media-server connection.
	 * <p>
	 * <div class="notImplemented"><b>TODO:</b>  needs to be implemented</div>
	 * 
	 * @since {@mythProtoVersion 68}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_68,fromInfo={
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="fb63a2e785d4fc7771539cd0ab5602246b1de1bd")
	}),parentCommand=ANN)
	public static final String ANN_MEDIASERVER = "MediaServer";
	
	/**
	 * Announces ringbuffer access.
	 * <p>
	 * This command registers a client to the backend and announces a ringbuffer connection.
	 * 
	 * {@mythProtoExample
	 * 25      ANN RingBuffer sycamore 1
	 * 2       OK
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>
	 *  	<ol class="cmdParams">
	 *  		<li><div class='paramType'>string</div> {@code RingBuffer}</li>
	 * 			<li><div class='paramType'>string</div> host</li>
	 * 			<li><div class='paramType'>int</div> recorder nr</li>
	 * 		</ol>
	 *	</dd>
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 20}
	 * @mythProtoVersionRange
	 * 
	 * @see #ANN
	 * @see IRecorder#annotateRingBuffer
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_20),parentCommand=ANN)
	public static final String ANN_RING_BUFFER = "RingBuffer";
	
	/**
	 * Announces a file transfer.
	 * <p>
	 * This command registers a client to the backend and announces a file-transfer connection.
	 * 
	 * {@mythProtoExample
	 * Version 15:
	 * 66      ANN FileTransfer mythtv[]:[]/home/mythtv/.mythtv/channels/pro7.gif
	 * 23      OK[]:[]17[]:[]0[]:[]704
	 * Version 44:
	 * 64      ANN FileTransfer mythtv 0 -1[]:[]/channels/atv_at.jpg[]:[]Default
	 * 24      OK[]:[]31[]:[]0[]:[]5752
	 * Version 66:
	 * 64      ANN FileTransfer mythtv 0 -1[]:[]/channels/atv_at.jpg[]:[]Default
	 * 18      OK[]:[]31[]:[]5752
	 * Version 66 (with timeout parameter)
	 * 103     ANN FileTransfer mythClient 0 1 2000[]:[]myth://192.168.10.207:6543/1018_20120109091500.mpg[]:[]Default
	 * 29      OK[]:[]34[]:[]0[]:[]121477152
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>
	 *  	<ol class="cmdParams">
	 *  		<li><div class='paramType'>string</div> {@code FileTransfer}</li>
	 *  		<li><div class='paramType'>string</div> hostname</li>
	 *  		<li><div class='paramType'>int</div> writeMode <div class='paramVersion'>since {@mythProtoVersion 46}</div></li>
	 *  		<li><div class='paramType'>int</div> usereadahead <div class='paramVersion'>since {@mythProtoVersion 29}</div></li>
	 *  		<li  class='paramDeprecated'><div class='paramType'>int</div> retries <div class='paramVersion'>since {@mythProtoVersion 29}, deprecated {@mythProtoVersion 60}</div></li>
	 *  		<li><div class='paramType'>int</div> timeout_ms <div class='paramVersion'>since {@mythProtoVersion 60}</div></li>
	 * 		</ol>
	 *	</dd>
	 *
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> name of the file to transfer</li>
	 *  		<li><div class='paramType'>string</div> storage-group <div class='paramVersion'>since {@mythProtoVersion 44}</div></li>
	 *  		<li><div class='paramType'>string[]</div> checkfiles (optional)</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code OK}</li>
	 * 			<li><div class='paramType'>int</div> socket id</li>
	 * 			<li  class='paramDeprecated'><div class='paramType'>long,long</div> filesize <div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 * 			<li><div class='paramType'>long</div> filesize <div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see <a href="http://www.mythtv.org/wiki/ANN_%28Myth_Protocol%29">Wiki</a>
	 * @see IBackend#annotateFileTransfer
	 * @see IFileTransfer
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=ANN)
	public static final String ANN_FILE_TRANSFER = "FileTransfer";
	
	/**
	 * Announces a client as monitoring client.
	 * <p>
	 * This command registers a client to the backend and announces a monitoring connection.
	 * <p>
	 * <i>Note:</i> This connection type does not block a shutdown of the backend.
	 * 
	 * {@mythProtoExample
	 * 24      ANN Monitor MythClient 0
	 * 2       OK
	 * }
	 *
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 * 	<dd>
	 *  	<ol class="cmdParams">
	 *  		<li><div class='paramType'>string</div> {@code Monitor}</li>
	 *  		<li><div class='paramType'>string</div> hostname</li>
	 *  		<li class='paramChanged'><div class='paramType'>int</div> events-mode (see {@link org.jmythapi.protocol.request.EPlaybackSockEventsMode here} for all pssible events) <div class='paramVersion'>changed {@mythProtoVersion 57}</div></li>
	 * 		</ol>
	 *	</dd>
	 *
	 *  <dt><b>Request parameters:</b></dt>
	 * 	<dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code OK}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 *
	 * @see <a href="http://www.mythtv.org/wiki/ANN_%28Myth_Protocol%29">Wiki</a>
	 * @see IBackend#annotateMonitor
	 * 
	 * @since {@mythProtoVersion 22}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_22),parentCommand=ANN)
	public static final String ANN_MONITOR = "Monitor";
	
	/* ========================================================================================
	 * FILETRANSFER - COMMANDS
	 * ======================================================================================== */
	/**
	 * Filetransfer - Checks if a file is open.
	 * <p>
	 * This command queries, whether a file socket is currently open.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 33      QUERY_FILETRANSFER 17[]:[]IS_OPEN
	 * 1       1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> socket id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code IS_OPEN}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> 1=open, 0=closed</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IFileTransfer#isOpen()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_FILETRANSFER)
	public static final String QUERY_FILETRANSFER_IS_OPEN = "IS_OPEN";
	
	/**
	 * Filetransfer - Finish file-transfer.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 30      QUERY_FILETRANSFER 17[]:[]DONE
	 * 2       ok
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> socket id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code DONE}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code OK}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IFileTransfer#done()
	 * @see IFileTransfer#close()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_FILETRANSFER)
	public static final String QUERY_FILETRANSFER_DONE = "DONE";
	
	/**
	 * Filetransfer - Request a file block.
	 * 
	 * {@mythProtoExample
	 * 49      QUERY_FILETRANSFER 17[]:[]REQUEST_BLOCK[]:[]65536
	 * 5       65536
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> socket id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code REQUEST_BLOCK}</li>
	 * 			<li><div class='paramType'>int</div> the size of the block to request</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> read byte count</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IFileTransfer#requestBlock
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_FILETRANSFER)
	public static final String QUERY_FILETRANSFER_REQUEST_BLOCK = "REQUEST_BLOCK";
	
	/**
	 * Filetransfer - Seek within a file.
	 * 
	 * <p>
	 * This command wraps the file "seek" function.<br>
	 * The pos and curpos fields are combined into a larger "long long" data type. 
	 * This returns two values, a split "long long" value for the result. 
	 * <p>
	 * {@mythProtoExample
	 * 64      QUERY_FILETRANSFER 43[]:[]SEEK[]:[]0[]:[]0[]:[]1[]:[]0[]:[]10000
	 * 11      0[]:[]10000
	 * }
	 * <p>
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> socket id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code SEEK}</li>
	 * 			<li  class='paramDeprecated'><div class='paramType'>int,int</div> pos <div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 *  		<li><div class='paramType'>long</div> pos <div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 * 			<li><div class='paramType'>int</div> whence</li>
	 * 			<li  class='paramDeprecated'><div class='paramType'>int,int</div> curpos <div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 *  		<li><div class='paramType'>long</div> curpos <div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li  class='paramDeprecated'><div class='paramType'>int,int</div> curpos <div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 *  		<li><div class='paramType'>long</div> curpos <div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IFileTransfer#seek(long, long, org.jmythapi.protocol.response.IFileTransfer.Seek)
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_FILETRANSFER)
	public static final String QUERY_FILETRANSFER_SEEK = "SEEK";
	
	/**
	 * Filetransfer - Configure timeout.
	 * <p>
	 * This command sets whether reading from a file should have a fast or slow timeout.<br>
	 * Slow timeouts are used for live TV ring buffers, and is three seconds. Fast timeous are 
	 * used for static files, and are 0.12 seconds.
	 * 
	 * {@mythProtoExample
	 * 43      QUERY_FILETRANSFER 36[]:[]SET_TIMEOUT[]:[]1
	 * 2       ok
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> socket id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code SET_TIMEOUT}</li>
	 * 			<li><div class='paramType'>int</div> {@code 1}=fast, {@code 0}=slow timeout</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 28}
	 * @mythProtoVersionRange
	 * 
	 * @see IFileTransfer#setTimeout(boolean)
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_28,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="9106"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="2a1285e76da4ac535c8f")
	}),parentCommand=QUERY_FILETRANSFER)
	public static final String QUERY_FILETRANSFER_SET_TIMEOUT = "SET_TIMEOUT";
	
	/**
	 * Filetransfer - Write a file block.
	 * <div class="notImplemented"><b>TODO:</b>  needs to be implemented.</div>
	 * 
	 * @since {@mythProtoVersion 46}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_46,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="21134"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="9d57165c68abb5cfbf29")
	}),parentCommand=QUERY_FILETRANSFER)
	public static final String QUERY_FILETRANSFER_WRITE_BLOCK = "WRITE_BLOCK";
	
	/**
	 * Filetransfer - Reopen a file.
	 * <p>
	 * <div class="notImplemented"><b>TODO:</b>  needs to be implemented.</div>
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code REOPEN}</li>
	 * 			<li><div class='paramType'>string</div> filename (optional, if another file should be opened)</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>boolean</div> {@code 1}=ok, {@code 0}=error</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 70}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_70),parentCommand=QUERY_FILETRANSFER)
	public static final String QUERY_FILETRANSFER_REOPEN = "REOPEN";
	
	/* ========================================================================================
	 * QUERY_RECORDING - COMMANDS
	 * ======================================================================================== */
	
	/**
	 * Queries a single recording by basename.
	 * 
	 * {@mythProtoExample
	 * 48      QUERY_RECORDING BASENAME 1057_20100811133747.mpg
	 * 443     OK[]:[]Tom auf heißer Spur[]:[]Der Kinderzimmer-Kobold[]:[][]:[][]:[]1057[]:[]1[]:[]ORF 1[]:[]ORF 1[]:[]1057_20100811133747.mpg[]:[]0[]:[]88213944[]:[]1281525300[]:[]1281526800[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-3[]:[]0[]:[]0[]:[]15[]:[]6[]:[]1281526667[]:[]1281526800[]:[]0[]:[]4[]:[]LiveTV[]:[]0[]:[][]:[]257651874[]:[][]:[]1281526803[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]LiveTV[]:[]1[]:[]2[]:[]0[]:[]
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code BASENAME}</li>
	 * 			<li><div class='paramType'>string</div> basename (format: <code>&lt;channelId&gt;_&lt;recordStartingTime&gt;.mpg</code>, e.g. <code>1057_20100811133747.mpg</code>)</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code OK} | {@code ERROR}</li>
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 32}
	 * 
	 * @see IBackend#queryRecording(String)
	 * @see IMythCommand#QUERY_RECORDING QUERY_RECORDING
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/QUERY_RECORDING">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_32,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="11794"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="5be603e42bdc34697fb6")
	}),parentCommand=QUERY_RECORDING)
	public static final String QUERY_RECORDING_BASENAME = "BASENAME";
	
	/**
	 * Queries a single recording by channel-id and recording-start-time.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 49      QUERY_RECORDING TIMESLOT 1057 2010-08-11T13:37:47
	 * 443     OK[]:[]Tom auf heißer Spur[]:[]Der Kinderzimmer-Kobold[]:[][]:[][]:[]1057[]:[]1[]:[]ORF 1[]:[]ORF 1[]:[]1057_20100811133747.mpg[]:[]0[]:[]88213944[]:[]1281525300[]:[]1281526800[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-3[]:[]0[]:[]0[]:[]15[]:[]6[]:[]1281526667[]:[]1281526800[]:[]0[]:[]4[]:[]LiveTV[]:[]0[]:[][]:[]257651874[]:[][]:[]1281526803[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]LiveTV[]:[]1[]:[]2[]:[]0[]:[]
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code TIMESLOT}</li>
	 * 			<li><div class='paramType'>int</div> channel-ID (e.g. <code>1058</code>)</li>
	 * 			<li><div class='paramType'>date</div> recording-start-time (e.g. <code>2010-08-11T13:37:47</code>)</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>none</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code OK} | {@code ERROR}</li>
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 32}
	 * @mythProtoVersionRange
	 * 
	 * @see IBackend#queryRecording(Integer, java.util.Date)
	 * @see IMythCommand#QUERY_RECORDING QUERY_RECORDING
	 * @see <a href="http://www.mythtv.org/wiki/Myth_Protocol/Commands/QUERY_RECORDING">Wiki</a>
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_32,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="11794"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="5be603e42bdc34697fb6")
	}),parentCommand=QUERY_RECORDING)
	public static final String QUERY_RECORDING_TIMESLOT = "TIMESLOT";	
	
	/* ========================================================================================
	 * QUERY_REMOTEENCODER - COMMANDS
	 * ======================================================================================== */
	
	/**
	 * Queries the state of the remote encoder.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 35      QUERY_REMOTEENCODER 1[]:[]GET_STATE
	 * 1       0
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> encoder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_STATE}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> state (See {@link org.jmythapi.protocol.response.IRemoteEncoderState.Props here} for all states)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRemoteEncoder#getState()
	 * @see IRemoteEncoderState
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_REMOTEENCODER)
	public static final String QUERY_REMOTEENCODER_GET_STATE = "GET_STATE";		
	
	/**
	 * Queries the remote encoder flags.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 35      QUERY_REMOTEENCODER 1[]:[]GET_FLAGS
	 * 10      1073741826
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> encoder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_FLAGS}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> flags  - See {@link org.jmythapi.protocol.response.IRemoteEncoderFlags.Flags here} for all flags</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 37}
	 * @mythProtoVersionRange
	 * 
	 * @see IRemoteEncoder#getFlags()
	 * @see IRemoteEncoderFlags
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_37),parentCommand=QUERY_REMOTEENCODER)
	public static final String QUERY_REMOTEENCODER_GET_FLAGS = "GET_FLAGS";
	
	/**
	 * Query the remote-encoder busy status.
	 * <p>true if the recorder is busy, or will be within the next time_buffer seconds.</p>
	 * 
	 * {@mythProtoExample
	 * Example for a busy encoder (Protocol Version 56)
	 * 33      QUERY_REMOTEENCODER 1[]:[]IS_BUSY
	 * 46      1[]:[]Tuner 1[]:[]1[]:[]1[]:[]1[]:[]0[]:[]1002
	 * 
	 * Example for an idle encoder (Protocol Version 56):
	 * 33      QUERY_REMOTEENCODER 1[]:[]IS_BUSY
	 * 43      0[]:[]&lt;EMPTY&gt;[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0
	 * 
	 * Example for an idle encoder (Protocol Version 15):
	 * 33      QUERY_REMOTEENCODER 1[]:[]IS_BUSY
	 * 1       0
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> encoder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code IS_BUSY}</li>
	 * 			<li><div class='paramType'>int</div> time_buffer - in seconds (optional) <div class='paramVersion'>since {@mythProtoVersion 37}</div></li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>bool</div> busy status - {@code 1}=busy, {@code 0}=idle</li>
	 * 			<li><div class='paramType'>int</div> source id <div class='paramVersion'>since {@mythProtoVersion 37}</div></li>
	 * 			<li><div class='paramType'>int</div> input id <div class='paramVersion'>since {@mythProtoVersion 37}</div></li>
	 * 			<li><div class='paramType'>int</div> card id <div class='paramVersion'>since {@mythProtoVersion 37}</div></li>
	 * 			<li><div class='paramType'>int</div> multiplex id <div class='paramVersion'>since {@mythProtoVersion 37}</div></li>
	 * 			<li><div class='paramType'>int</div> live-tv order <div class='paramVersion'>since {@mythProtoVersion 71}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRemoteEncoder#isBusy()
	 * @see IRemoteEncoder#getBusyStatus()
	 * @see IRemoteEncoderBusyStatus
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_REMOTEENCODER)
	public static final String QUERY_REMOTEENCODER_IS_BUSY = "IS_BUSY";	
	
	/**
	 * Checks if a remote encoder is recording the given program.
	 * <p>
	 * This command is used to check if the given encododer is currently recording the given program.
	 * 
	 * {@mythProtoExample
	 * 451     QUERY_REMOTEENCODER 1[]:[]MATCHES_RECORDING[]:[]Unknown[]:[][]:[][]:[][]:[]1001[]:[]1[]:[]VOX[]:[]VOX[]:[]/var/lib/mythtv/livetv/1001_20100622073520.mpg[]:[]0[]:[]0[]:[]1277184920[]:[]1277186400[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]1[]:[]0[]:[]0[]:[]-2[]:[]0[]:[]0[]:[]15[]:[]6[]:[]1277184920[]:[]1277186400[]:[]0[]:[]4[]:[]LiveTV[]:[]0[]:[][]:[][]:[][]:[]1277184920[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]LiveTV[]:[]0[]:[]0[]:[]0[]:[]
	 * 1       1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> encoder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code MATCHES_RECORDING}</li>
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>bool</div> {@code 0}=no, {@code 1}=yes</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRemoteEncoder#matchesRecording
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_REMOTEENCODER)
	public static final String QUERY_REMOTEENCODER_MATCHES_RECORDING = "MATCHES_RECORDING";	

	/**
	 * Start recording of a program.
	 * <p>
	 * This command tells the encoder to start recording the program as soon as possible.
	 * <p>
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> encoder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li>string</div> {@code START_RECORDING}</li>
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li class='paramDeprecated'><div class='paramType'>int</div> {@code +1}=recording started, {@code -1}=encoder is busy, {@code 0}=unknown <div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 * 			<li><div class='paramType'>int</div> recording-status <div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRemoteEncoder#startRecording(IProgramInfo)
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_REMOTEENCODER)
	public static final String QUERY_REMOTEENCODER_START_RECORDING = "START_RECORDING";	
	
	/**
	 * Start recording of a pending program.
	 * <p>
	 * Tells the encoder that there is a pending recording in "secsleft" seconds.
	 * 
	 * <div class="notImplemented"><b>TODO:</b>  needs to be implemented.</div>
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> encoder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code RECORD_PENDING}</li>
	 * 			<li><div class='paramType'>int</div> secsleft - seconds to wait before starting recording</li>
	 * 			<li>boolean</div> haslate - if true, a later non-conflicting showing is available <div class='paramVersion'>since {@mythProtoVersion 37}</div></li>
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_REMOTEENCODER)
	public static final String QUERY_REMOTEENCODER_RECORD_PENDING = "RECORD_PENDING";	
	
	/**
	 * Cancel the next recording.
	 * <p>
	 * This command tells the encoder to cancel the next recording.<br>
	 * This is used when the user is watching "Live TV" and does not 
	 * want to allow the recorder to be taken for a pending recording.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * Cancel next recording:
	 * 53      QUERY_REMOTEENCODER 1[]:[]CANCEL_NEXT_RECORDING[]:[]1
	 * 2       ok
	 * 
	 * Allow next recording:
	 * 53      QUERY_REMOTEENCODER 1[]:[]CANCEL_NEXT_RECORDING[]:[]0
	 * 2       ok
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> encoder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code CANCEL_NEXT_RECORDING}</li>
	 * 			<li><div class='paramType'>int</div> {@code 0}=cancel, {@code 1}=allow</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRemoteEncoder#cancelNextRecording(Boolean)
	 * @see #BACKEND_MESSAGE_ASK_RECORDING
	 * 
	 * @since {@mythProtoVersion 37}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_37),parentCommand=QUERY_REMOTEENCODER)
	public static final String QUERY_REMOTEENCODER_CANCEL_NEXT_RECORDING = "CANCEL_NEXT_RECORDING";
	
	/**
	 * Stop recording.
	 * <p>
	 * This command stops a currently active recording on the given recoder.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 40      QUERY_REMOTEENCODER 1[]:[]STOP_RECORDING
	 * 2       OK
	 * }
	 * <p>
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> encoder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code STOP_RECORDING}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRemoteEncoder#stopRecording()
	 * 
	 * @since {@mythProtoVersion 37}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_37),parentCommand=QUERY_REMOTEENCODER)
	public static final String QUERY_REMOTEENCODER_STOP_RECORDING = "STOP_RECORDING";
	
	/**
	 * Get maximum bitrate.
	 * <p>
	 * This command queries the maximum bitrate the encoder can output.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * Protocol version 65 example:
	 * 41      QUERY_REMOTEENCODER 2[]:[]GET_MAX_BITRATE
	 * 14      0[]:[]10080000
	 * 
	 * Protocol version 66 example:
	 * 41      QUERY_REMOTEENCODER 2[]:[]GET_MAX_BITRATE
	 * 8       10080000
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> encoder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_MAX_BITRATE}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li  class='paramDeprecated'><div class='paramType'>int,int</div> maxBitRate <div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 * 			<li><div class='paramType'>long</div> maxBitRate <div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 17}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_17),parentCommand=QUERY_REMOTEENCODER)
	public static final String QUERY_REMOTEENCODER_GET_MAX_BITRATE = "GET_MAX_BITRATE";	
	
	/**
	 * Queries current recording.
	 * <p>
	 * This command queries the encoders current recording.<br>
	 * This command was introduced to "find out if the slave backend is recording something the master backend thinks
	 * should be done by now."
	 * <p>
	 * {@mythProtoExample
	 * 47      QUERY_REMOTEENCODER 2[]:[]GET_CURRENT_RECORDING
	 * 639     Schlimmer geht's immer[]:[][]:[]Als Ganove Kevin das Haus von Mr. Fairbanks ausr&auml;umen will, wird er von ihm ertappt und der Polizei &uuml;bergeben. Zuvor nimmt Fairbanks ihm noch dessen Ring ab. Der bestohlene Dieb sinnt auf Rache...[]:[]spielfilm[]:[]1002[]:[]1[]:[]ATV+[]:[]ATV+[]:[]/var/lib/mythtv/livetv/1002_20100604074022.mpg[]:[]0[]:[]0[]:[]1275623700[]:[]1275630300[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]1[]:[]0[]:[]0[]:[]-2[]:[]0[]:[]0[]:[]15[]:[]6[]:[]1275630022[]:[]1275630300[]:[]0[]:[]4[]:[]LiveTV[]:[]0[]:[][]:[]11825298[]:[][]:[]1275623700[]:[]0.200000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]LiveTV[]:[]0[]:[]0[]:[]0[]:[]2001
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> encoder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li>string</div> {@code GET_CURRENT_RECORDING}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRemoteEncoder#getCurrentRecording()
	 * 
	 * @since {@mythProtoVersion 19}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_19),parentCommand=QUERY_REMOTEENCODER)
	public static final String QUERY_REMOTEENCODER_GET_CURRENT_RECORDING = "GET_CURRENT_RECORDING";
	
	/**
	 * Query connected inputs.
	 * <p>
	 * This command returns the recorders connected inputs. 
	 * 
	 * {@mythProtoExample
	 * Example for an encoder with a free input: 
	 * 41      QUERY_REMOTEENCODER 1[]:[]GET_FREE_INPUTS
	 * 31      MPEG2TS[]:[]1[]:[]1[]:[]1[]:[]0
	 * 
	 * Protocol version 74 example:
	 * 41      QUERY_REMOTEENCODER 4[]:[]GET_FREE_INPUTS
	 * 38      DVBInput[]:[]2[]:[]4[]:[]4[]:[]0[]:[]4
	 * 
	 * Example for an encoder with no free input:
	 * 41      QUERY_REMOTEENCODER 1[]:[]GET_FREE_INPUTS
	 * 10      EMPTY_LIST
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> encoder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_FREE_INPUTS}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>On error:
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code EMPTY_LIST}</li>
	 * 		</ol>
	 * 		On success - A List of:
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> input name</li>
	 * 			<li><div class='paramType'>int</div> source id</li>
	 * 			<li><div class='paramType'>int</div> input id</li>
	 * 			<li><div class='paramType'>int</div> card id</li>
	 * 			<li><div class='paramType'>int</div> multiplex id</li>
	 * 			<li><div class='paramType'>int</div> live-tv order <div class='paramVersion'>since {@mythProtoVersion 71}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 37}
	 * @mythProtoVersionRange
	 * 
	 * @see IRemoteEncoder#getFreeInputs()
	 * @see IFreeInputList
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_37),parentCommand=QUERY_REMOTEENCODER)
	public static final String QUERY_REMOTEENCODER_GET_FREE_INPUTS = "GET_FREE_INPUTS";
	
	/**
	 * Queries sleep status.
	 * <p>
	 * This command returns the current Sleep Status of the encoder.
	 * 
	 * {@mythProtoExample
	 * 41      QUERY_REMOTEENCODER 1[]:[]GET_SLEEPSTATUS
	 * 1       8
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> encoder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_SLEEPSTATUS}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> sleep status</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 45}
	 * @mythProtoVersionRange
	 * 
	 * @see <a href="http://svn.mythtv.org/trac/changeset/20084/">SVN Rev. 20084</a>
	 * @see IRemoteEncoder#getSleepStatus()
	 * @see ISleepStatus
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_45,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="20084"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="663cd89811cf7fc22f59")
	}),parentCommand=QUERY_REMOTEENCODER)
	public static final String QUERY_REMOTEENCODER_GET_SLEEPSTATUS = "GET_SLEEPSTATUS";
	
	/**
	 * Query encoder status.
	 * <p>
	 * This command gets the current recording status of the tuner.
	 * 
	 * {@mythProtoExample
	 * For a recording encoder:
	 * 46      QUERY_REMOTEENCODER 1[]:[]GET_RECORDING_STATUS
	 * 2       -2
	 * 
	 * For a tuning encoder:
	 * 46      QUERY_REMOTEENCODER 3[]:[]GET_RECORDING_STATUS
	 * 3       -10
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> encoder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_RECORDING_STATUS}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recording status (see {@link org.jmythapi.protocol.response.IProgramRecordingStatus.Status} for all possible values)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRemoteEncoder#getRecordingStatus()
	 * @see IProgramRecordingStatus
	 * 
	 * @since {@mythProtoVersion 63}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_63),parentCommand=QUERY_REMOTEENCODER)
	public static final String QUERY_REMOTEENCODER_GET_RECORDING_STATUS = "GET_RECORDING_STATUS";
	
	/* ========================================================================================
	 * QUERY_RECORDER - COMMANDS
	 * ======================================================================================== */
	
	/**
	 * Query busy status.
	 * <p>
	 * This command queries if the recorder is actually recording.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * On a busy recorder:
	 * 33      QUERY_RECORDER 1[]:[]IS_RECORDING
	 * 1       1
	 * 
	 * On an idle recorder:
	 * 33      QUERY_RECORDER 3[]:[]IS_RECORDING
	 * 1       0
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code IS_RECORDING}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> {@code 1}=busy, {@code 0}=idle</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#isRecording()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_IS_RECORDING = "IS_RECORDING";	
		
	/**
	 * Queries frame rate.
	 * <p>
	 * This command queries the recording frame rate.
	 * 
	 * {@mythProtoExample
	 * If the recorder is recording:
	 * 34      QUERY_RECORDER 1[]:[]GET_FRAMERATE
	 * 2       25
	 * If the recorder is NOT recording:
	 * 34      QUERY_RECORDER 1[]:[]GET_FRAMERATE
	 * 2       -1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_FRAMERATE}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>float</div> framerate ({@code -1} if not recording)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getFrameRate()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_FRAMERATE = "GET_FRAMERATE";	
	
	/**
	 * Queries written frames.
	 * <p>
	 * This command queries the number of frames written to disk.
	 * 
	 * {@mythProtoExample
	 * Protocol version 65 example:
	 * 39      QUERY_RECORDER 1[]:[]GET_FRAMES_WRITTEN
	 * 9       0[]:[]153
	 * 
	 * Protocol version 66 example:
	 * 39      QUERY_RECORDER 1[]:[]GET_FRAMES_WRITTEN
	 * 3       153
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_FRAMES_WRITTEN}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 *			<li  class='paramDeprecated'><div class='paramType'>int,int</div> freespace <div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 * 		 	<li><div class='paramType'>long</div> freespace <div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getFramesWritten()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_FRAMES_WRITTEN = "GET_FRAMES_WRITTEN";	
	
	/**
	 * Queries written bytes.
	 * <p>
	 * This command queries the total number of bytes written by the recorder.
	 * 
	 * {@mythProtoExample
	 * Protocol version 66 example:
	 * 38      QUERY_RECORDER 1[]:[]GET_FILE_POSITION
	 * 7       8217352
	 * 
	 * Protocol version 65 example:
	 * 38      QUERY_RECORDER 1[]:[]GET_FILE_POSITION
	 * 13      0[]:[]8217352
	 * 
	 * Protocol version 65 example - If the recorder is NOT recording:
	 * 38      QUERY_RECORDER 1[]:[]GET_FILE_POSITION
	 * 9       -1[]:[]-1
	 * }
	 * 
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_FILE_POSITION}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li  class='paramDeprecated'><div class='paramType'>int,int</div> filePosition ({@code -1, -1} if not recording <div class='paramVersion'>deprecated {@mythProtoVersion 66})</div></li>
	 * 			<li><div class='paramType'>long</div> filePosition ({@code -1} if not recording <div class='paramVersion'>since {@mythProtoVersion 66})</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getFilePosition()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_FILE_POSITION = "GET_FILE_POSITION";	
	
	/**
	 * Queries bytes available to read.
	 * <p>
	 * This command ask the recorder for the number of bytes beyond "totalreadpos" it is safe to read.<br>
	 * This command may return a negative number, including {@code -1} if the call succeeds. 
	 * This means totalreadpos is past the "safe read" portion of the file.
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_FREE_SPACE}</li>
	 * 			<li><div class='paramType'>long,long</div> totalReadPos</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>long,long</div> bytes ready to read</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 20}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_20),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_FREE_SPACE = "GET_FREE_SPACE";	
	
	/**
	 * Queries maximum bitrage.
	 * <p>
	 * This command gets the maximum bitrate a recorder/encoder can output.
	 * 
	 * {@mythProtoExample
	 * Protocol version 65 example:
	 * 36      QUERY_RECORDER 1[]:[]GET_MAX_BITRATE
	 * 14      0[]:[]10080000
	 * 
	 * Protocol version 66 example:
	 * 36      QUERY_RECORDER 1[]:[]GET_MAX_BITRATE
	 * 8       10080000
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_MAX_BITRATE}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li  class='paramDeprecated'><div class='paramType'>int,int</div> maxBitRate <div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 * 			<li><div class='paramType'>long</div> maxBitRate <div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 17}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getMaxBitrate()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_17),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_MAX_BITRATE = "GET_MAX_BITRATE";			

	/**
	 * Queries current recording.
	 * <p>
	 * This command returns the recorders current recording.<br>
	 * This command was introduced to "find out if the slave backend is recording something the master backend thinks
	 * should be done by now.".
	 * 
	 * {@mythProtoExample
	 * 42      QUERY_RECORDER 1[]:[]GET_CURRENT_RECORDING
	 * 639     Schlimmer geht's immer[]:[][]:[]Als Ganove Kevin das Haus von Mr. Fairbanks ausr&auml;umen will, wird er von ihm ertappt und der Polizei &uuml;bergeben. Zuvor nimmt Fairbanks ihm noch dessen Ring ab. Der bestohlene Dieb sinnt auf Rache...[]:[]spielfilm[]:[]1002[]:[]1[]:[]ATV+[]:[]ATV+[]:[]/var/lib/mythtv/livetv/1002_20100604074022.mpg[]:[]0[]:[]0[]:[]1275623700[]:[]1275630300[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]1[]:[]0[]:[]0[]:[]-2[]:[]0[]:[]0[]:[]15[]:[]6[]:[]1275630022[]:[]1275630300[]:[]0[]:[]4[]:[]LiveTV[]:[]0[]:[][]:[]11825298[]:[][]:[]1275623700[]:[]0.200000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]LiveTV[]:[]0[]:[]0[]:[]0[]:[]2001
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_CURRENT_RECORDING}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 19}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getCurrentRecording()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_19),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_CURRENT_RECORDING = "GET_CURRENT_RECORDING";
	
	/**
	 * Queries keyframe position.
	 * <p>
	 * This command ask a recorder to return byte position in RingBuffer of a keyframe.
	 * <p>
	 * If you need to query a continuous range of frame numbers, use {@link #QUERY_RECORDER_FILL_POSITION_MAP FILL_POSITION_MAP} instead.
	 * 
	 * {@mythProtoExample
	 * On error:
	 * 50      QUERY_RECORDER 3[]:[]GET_KEYFRAME_POS[]:[]0[]:[]11
	 * 9       -1[]:[]-1
	 * 
	 * On success:
	 * 50      QUERY_RECORDER 3[]:[]GET_KEYFRAME_POS[]:[]0[]:[]13
	 * 12      0[]:[]380778
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_KEYFRAME_POS}</li>
	 * 			<li class='paramDeprecated'><div class='paramType'>int,int</div> desired frame number <div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 * 			<li><div class='paramType'>long</div> desired frame number <div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li  class='paramDeprecated'><div class='paramType'>int,int</div> keyframe byte postion <div class='paramVersion'>deprecated {@mythProtoVersion 66}</div></li>
	 * 			<li><div class='paramType'>long</div> keyframe byte position <div class='paramVersion'>since {@mythProtoVersion 66}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRecorder#getKeyframePosition(long)
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_KEYFRAME_POS = "GET_KEYFRAME_POS";	
	
	/**
	 * Queries keyframe position map.
	 * <p>
	 * Returns byte position in RingBuffer of a keyframes according to recorder.
	 * <p>
	 * <b>Note:</b><br>
	 * For protocol versions prior to {@mythProtoVersion 43}, only 32 bit integer values are supported as frame numbers. <br>
	 * If you need to work with larger numbers, use {@link #QUERY_RECORDER_GET_KEYFRAME_POS GET_KEYFRAME_POS} instead.
	 * 
	 * {@mythProtoExample
	 * Protocol Version 63 example:
	 * 52      QUERY_RECORDER 3[]:[]FILL_POSITION_MAP[]:[]0[]:[]201
	 * 305     12[]:[]380778[]:[]24[]:[]745322[]:[]36[]:[]1134442[]:[]48[]:[]1525674[]:[]60[]:[]1964906[]:[]72[]:[]2346922[]:[]84[]:[]2744234[]:[]96[]:[]3168106[]:[]108[]:[]3579818[]:[]120[]:[]4004714[]:[]132[]:[]4403114[]:[]144[]:[]4855658[]:[]156[]:[]5216170[]:[]168[]:[]5686122[]:[]180[]:[]6014890[]:[]192[]:[]6389610
	 * 
	 * Protocol Version &lt; 63 example:
	 * 52      QUERY_RECORDER 1[]:[]FILL_POSITION_MAP[]:[]0[]:[]225
	 * 815     0[]:[]0[]:[]0[]:[]38[]:[]0[]:[]1[]:[]0[]:[]485414[]:[]0[]:[]2[]:[]0[]:[]780326[]:[]0[]:[]3[]:[]0[]:[]1071142[]:[]0[]:[]4[]:[]0[]:[]1345574[]:[]0[]:[]5[]:[]0[]:[]1630246[]:[]0[]:[]6[]:[]0[]:[]1910822[]:[]0[]:[]7[]:[]0[]:[]2189350[]:[]0[]:[]8[]:[]0[]:[]2465830[]:[]0[]:[]9[]:[]0[]:[]2758694[]:[]0[]:[]10[]:[]0[]:[]3067942[]:[]0[]:[]11[]:[]0[]:[]3366950[]:[]0[]:[]12[]:[]0[]:[]3674150[]:[]0[]:[]13[]:[]0[]:[]3981350[]:[]0[]:[]14[]:[]0[]:[]4290598[]:[]0[]:[]15[]:[]0[]:[]4589606[]:[]0[]:[]16[]:[]0[]:[]4876326[]:[]0[]:[]17[]:[]0[]:[]5165094[]:[]0[]:[]18[]:[]0[]:[]5453862[]:[]0[]:[]19[]:[]0[]:[]5744678[]:[]0[]:[]20[]:[]0[]:[]6041638[]:[]0[]:[]21[]:[]0[]:[]6332454[]:[]0[]:[]22[]:[]0[]:[]6623270[]:[]0[]:[]23[]:[]0[]:[]6912038[]:[]0[]:[]24[]:[]0[]:[]7198758[]:[]0[]:[]25[]:[]0[]:[]7487526[]:[]0[]:[]26[]:[]0[]:[]7772198
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code FILL_POSITION_MAP}</li>
	 * 			<li class='paramDeprecated'><div class='paramType'>int</div> starting frame number <div class='paramVersion'>deprecated {@mythProtoVersion 43}</div></li>
	 * 			<li><div class='paramType'>long</div> starting frame number <div class='paramVersion'>since {@mythProtoVersion 43}</div></li>
	 * 			<li class='paramDeprecated'><div class='paramType'>int</div> ending frame number <div class='paramVersion'>deprecated {@mythProtoVersion 43}</div></li>
	 * 			<li><div class='paramType'>long</div> ending frame number <div class='paramVersion'>since {@mythProtoVersion 43}</div></li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 *     		<li  class='paramDeprecated'><div class='paramType'>int,int</div> keynum <div class='paramVersion'>deprecated {@mythProtoVersion 43}</div></li>
	 *     		<li><div class='paramType'>long</div> keynum <div class='paramVersion'>since {@mythProtoVersion 43}</div></li>
	 *      	<li  class='paramDeprecated'><div class='paramType'>int,int</div> value <div class='paramVersion'>deprecated {@mythProtoVersion 43}</div></li>
	 *     		<li><div class='paramType'>long</div> value <div class='paramVersion'>since {@mythProtoVersion 43}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * 
	 * @see IRecorder#fillPositionMap(long, long)
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_FILL_POSITION_MAP = "FILL_POSITION_MAP";
	
	/**
	 * Queries the duration map of a recording.
	 * <p>
	 * Returns time position in RingBuffer of a keyframes according to recorder.
	 * <p>
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code FILL_DURATION_MAP}</li>
	 * 			<li><div class='paramType'>long</div> starting frame number</li>
	 * 			<li><div class='paramType'>long</div> ending frame number</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 *     		<li><div class='paramType'>long</div> keynum</li>
	 *     		<li><div class='paramType'>long</div> time in seconds</li>
	 * 		</ol>
	 * 	</dd>
	 * @see IRecorder#fillDurationMap(long, long)
	 * 
	 * @since {@mythProtoVersion 77}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_77),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_FILL_DURATION_MAP = "FILL_DURATION_MAP";
	
	/**
	 * Setup a ring buffer.
	 * <p>
	 * This command sets up RingBuffer for "Live TV" playback.
	 * 
	 * {@mythProtoExample
	 * 44      QUERY_RECORDER 1[]:[]SETUP_RING_BUFFER[]:[]0
	 * 77      rbuf://127.0.0.1:6543/cache/cache/ringbuf1.nuv[]:[]1[]:[]0[]:[]0[]:[]52428800
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code SETUP_RING_BUFFER}</li>
	 * 			<li><div class='paramType'>boolean</div> {@code 1}=picture in picture</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok} | {@code not_ok}</li>
	 * 			<li><div class='paramType'>string</div> url to ringbuffer file</li>
	 * 			<li><div class='paramType'>long,long</div> filesize</li>
	 * 			<li><div class='paramType'>long,long</div> fillamount</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 20}
	 * @mythProtoVersionRange
	 * 
	 * @see IRingBuffer
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_20),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_SETUP_RING_BUFFER = "SETUP_RING_BUFFER";			
	
	/**
	 * Query current recording.
	 * <p<
	 * This command returns the recorders current recording.
	 * </p>
	 * 
	 * {@mythProtoExample
	 * 34      QUERY_RECORDER 1[]:[]GET_RECORDING
	 * 639     Schlimmer geht's immer[]:[][]:[]Als Ganove Kevin das Haus von Mr. Fairbanks ausr&auml;umen will, wird er von ihm ertappt und der Polizei &uuml;bergeben. Zuvor nimmt Fairbanks ihm noch dessen Ring ab. Der bestohlene Dieb sinnt auf Rache...[]:[]spielfilm[]:[]1002[]:[]1[]:[]ATV+[]:[]ATV+[]:[]/var/lib/mythtv/livetv/1002_20100604074022.mpg[]:[]0[]:[]0[]:[]1275623700[]:[]1275630300[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]1[]:[]0[]:[]0[]:[]-2[]:[]0[]:[]0[]:[]15[]:[]6[]:[]1275630022[]:[]1275630300[]:[]0[]:[]4[]:[]LiveTV[]:[]0[]:[][]:[]11825298[]:[][]:[]1275623700[]:[]0.200000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]LiveTV[]:[]0[]:[]0[]:[]0[]:[]2001
	 * 
	 * If the recorder is not recording:
	 * 34      QUERY_RECORDER 1[]:[]GET_RECORDING
	 * 333     []:[][]:[][]:[][]:[][]:[][]:[][]:[][]:[][]:[]0[]:[]0[]:[]1275631440[]:[]1275631440[]:[]0[]:[]0[]:[]0[]:[][]:[]0[]:[]1[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0[]:[]15[]:[]6[]:[]1275631440[]:[]1275631440[]:[]0[]:[]0[]:[]Default[]:[]0[]:[][]:[][]:[][]:[]1275631440[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]0[]:[]0[]:[]0[]:[]
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_RECORDING}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getRecording()
	 * @see IProgramInfo
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_RECORDING = "GET_RECORDING";	
	
	/**
	 * Stop playing.
	 * <p>
	 * This command tells the recorder to stop streaming a recording to the frontend.<br>
	 * This command is used prior to {@mythProtoVersion 20} if watching a recording should be stopped. 
	 * 
	 * <p>
	 * <b>Note</b><br>
	 * This command seems to be still available in <code>remoteencoder.cpp</code> but is accessible anymore in <code>mainserver.cpp</code>
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code STOP_PLAYING}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 20}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#stopPlaying()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_20),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_STOP_PLAYING = "STOP_PLAYING";	
	
	/**
	 * Frontend ready.
	 * <p>
	 * This command tells the recorder that the frontend is up and ready.
	 * <p>
	 * This is required e.g. if the backend needs to transmit the {@code ASK_RECORDING} message to the frontend.
	 * 
	 * {@mythProtoExample
	 * 35      QUERY_RECORDER 1[]:[]FRONTEND_READY
	 * 2       ok
	 * }
	 * <p>
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code FRONTEND_READY}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#signalFrontendReady()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_FRONTEND_READY = "FRONTEND_READY";	
	
	/**
	 * Cancel next recording.
	 * <p>
	 * this command tells the recorder to cancel the next recording.<br>
	 * This is used when the user is watching "Live TV" and does not 
	 * want to allow the recorder to be taken for a pending recording.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * Cancel next recording:
	 * 48      QUERY_RECORDER 1[]:[]CANCEL_NEXT_RECORDING[]:[]1
	 * 2       ok
	 * 
	 * Allow next recording:
	 * 48      QUERY_RECORDER 1[]:[]CANCEL_NEXT_RECORDING[]:[]0
	 * 2       ok
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code CANCEL_NEXT_RECORDING}</li>
	 * 			<li><div class='paramType'>boolean</div> {@code 1}=cancel, {@code 0}=continue <div class='paramVersion'>since {@mythProtoVersion 23}</div></li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRecorder#cancelNextRecording
	 * @see #BACKEND_MESSAGE_ASK_RECORDING
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_CANCEL_NEXT_RECORDING = "CANCEL_NEXT_RECORDING";	
	
	/**
	 * Spawn LiveTV.
	 * <p>
	 * This command tells the backend to spawn a "Live TV" recorder.
	 * <p>
	 * See {@link #SET_NEXT_LIVETV_DIR} on how to change the recording directory to use.
	 * 
	 * {@mythProtoExample
	 * Version 15:
	 * 33      QUERY_RECORDER 1[]:[]SPAWN_LIVETV
	 * 2       ok
	 * 
	 * Version 20:
	 * 79      QUERY_RECORDER 1[]:[]SPAWN_LIVETV[]:[]live-seans-laptop-07-03-23T12:30:32[]:[]0
	 * 2       ok
	 * 
	 * Version 56:
	 * 81      QUERY_RECORDER 1[]:[]SPAWN_LIVETV[]:[]live-seans-laptop-1302425076102[]:[]0[]:[]1
	 * 2       ok
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code SPAWN_LIVETV}</li>
	 * 			<li><div class='paramType'>string</div> chain-id - the LiveTV chain id to use <div class='paramVersion'>since {@mythProtoVersion 20}</div></li>
	 * 			<li><div class='paramType'>int pip</div> Tells TVRec's RingBuffer that this is for a Picture in Picture display <div class='paramVersion'>since {@mythProtoVersion 20}</div></li>
	 * 			<li><div class='paramType'>string</div> startchan - the channel the LiveTV should start with <div class='paramVersion'>since {@mythProtoVersion 34}</div></li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see #SET_NEXT_LIVETV_DIR
	 * @see IRecorder#spawnLiveTV()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_SPAWN_LIVETV = "SPAWN_LIVETV";	
	
	/**
	 * Stop LiveTV.
	 * <p>
	 * This command tells the backend to stop a "Live TV" recorder.
	 * 
	 * {@mythProtoExample
	 * 32      QUERY_RECORDER 1[]:[]STOP_LIVETV
	 * 2       ok
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code STOP_LIVETV}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#stopLiveTv()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_STOP_LIVETV = "STOP_LIVETV";	
		
	/**
	 * Pause recorder.
	 * <p>
	 * This command tells the backend to pause a recorder.
	 * <p>
	 * This is used for channel and input changes.
	 * <p>
	 * This command was only available in {@mythProtoVersion 18}, therefore we will not implement it.
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code PAUSE_RECORDER}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 *  
	 * @since {@mythProtoVersion 18}
	 * @deprecated {@mythProtoVersion 18}, was supported just a very short time period, use {@link #QUERY_RECORDER_PAUSE} instead.
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_18,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="6739"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="8eb94238aa3e69473628")
	},toInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="6740"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="73c16d1a3cc383cfe11a")		
	},to=PROTO_VERSION_18),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_PAUSE_RECORDER = "PAUSE_RECORDER";		
		
	/**
	 * Pause recorder.
	 * <p>
	 * This command tells the backend to pause a recorder.
	 * <p>
	 * This is used for channel and input changes.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 26      QUERY_RECORDER 1[]:[]PAUSE
	 * 2       ok
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code PAUSE}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#pause()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_PAUSE = "PAUSE";			
	
	/**
	 * Unpause recorder.
	 * <p>
	 * This command tells the backend to unpause a recorder.
	 * <p>
	 * This command was only available in {@mythProtoVersion 18}, therefore we will not implement it.
	 * <p>
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code UNPAUSE}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 18}
	 * @deprecated {@mythProtoVersion 18}, was supported just a very short time period
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_18,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="6739"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="8eb94238aa3e69473628")
	},to=PROTO_VERSION_18,toInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="6740"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="73c16d1a3cc383cfe11a")			
	}),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_UNPAUSE = "UNPAUSE";		
	
	/**
	 * Finish recording.
	 * <p>
	 * This command tells a backend to stop recording, but only after "overrecord" seconds.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 37      QUERY_RECORDER 1[]:[]FINISH_RECORDING
	 * 2       ok
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code FINISH_RECORDING}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRecorder#finishRecording()
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_FINISH_RECORDING = "FINISH_RECORDING";
		
	/**
	 * Keep LiveTV recording.
	 * <p>
	 * This command tells the backend to keep a LiveTV recording, if "recording" is 1.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 46      QUERY_RECORDER 1[]:[]SET_LIVE_RECORDING[]:[]-1
	 * 2       ok
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code SET_LIVE_RECORDING}</li>
	 * 			<li><div class='paramType'>int</div> {@code -1} (currently only -1 is supported)</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRecorder#setLiveTvRecording()
	 * 
	 * @since {@mythProtoVersion 26}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_26),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_SET_LIVE_RECORDING = "SET_LIVE_RECORDING";
	
	/**
	 * Change to next input.
	 * <p>
	 * This command tells are recorder to change to the next input.
	 * <p>
	 * {@mythProtoExample
	 * 34      QUERY_RECORDER 1[]:[]TOGGLE_INPUTS
	 * 2       ok
	 * }
	 * <p>
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code TOGGLE_INPUTS}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 27}, 
	 * 			replaced by {@link #QUERY_RECORDER_GET_CONNECTED_INPUTS},
	 *             {@link #QUERY_RECORDER_GET_INPUT} and {@link #QUERY_RECORDER_SET_INPUT}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#toggleInputs()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_27),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_TOGGLE_INPUTS = "TOGGLE_INPUTS";	
	
	/**
	 * Get connected inputs.
	 * <p>
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_CONNECTED_INPUTS}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 *  	On success:
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string[]</div> input name</li>
	 * 		</ol>
	 * 
	 * 		On error:
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code EMPTY_LIST}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRecorder#getConnectedInputs
	 * 
	 * @since {@mythProtoVersion 27}
	 * @deprecated {@mythProtoVersion 37}, 
	 * 		replaced by {@link #QUERY_RECORDER_GET_FREE_INPUTS}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_27,to=PROTO_VERSION_37),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_CONNECTED_INPUTS = "GET_CONNECTED_INPUTS";
	
	/**
	 * Get free inputs.
	 * <p>
	 * This command tells a recorder to return all free inputs.
	 * 
	 * {@mythProtoExample
	 * 36      QUERY_RECORDER 1[]:[]GET_FREE_INPUTS
	 * 31      MPEG2TS[]:[]1[]:[]1[]:[]1[]:[]0
	 * }
	 *  
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_FREE_INPUTS}</li>
	 * 			<li><div class='paramType'>int[]</div> list of card-IDs to exclude (optional)</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> input name</li>
	 * 			<li><div class='paramType'>int</div> source id</li>
	 * 			<li><div class='paramType'>int</div> input id</li>
	 * 			<li><div class='paramType'>int</div> card id</li>
	 * 			<li><div class='paramType'>int</div> multiplex id</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 37}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getFreeInputs()
	 * @see IFreeInputList
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_37),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_FREE_INPUTS = "GET_FREE_INPUTS";
	
	/**
	 * Query used input.
	 * <p>
	 * This command returns the recorders current input.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * Example for an Hauppauge PVR-350 card:
	 * 30      QUERY_RECORDER 1[]:[]GET_INPUT
	 * 7       MPEG2TS
	 * 
	 * Example for an Haupppauge DVB-T card:
	 * 30      QUERY_RECORDER 1[]:[]GET_INPUT
	 * 8       DVBInput
	 * 
	 * Example for an inactive Recorder:
	 * 30      QUERY_RECORDER 1[]:[]GET_INPUT
	 * 7       UNKNOWN
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_INPUT}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> input name ({@code UNKNOWN} if unknown)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 27}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getInput()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_27),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_INPUT = "GET_INPUT";
	
	/**
	 * Change input.
	 * <p>
	 * This command tells the recorder to change to the specified input.
	 * <p>
	 * <b>ATTENTION:</b> You must pause the recorder before doing this.
	 * 
	 * <p>
	 * A list of available inputs can be queried using {@link #QUERY_RECORDER_GET_CONNECTED_INPUTS} or 
	 * {@link #QUERY_RECORDER_GET_FREE_INPUTS}.
	 * 
	 * {@mythProtoExample
	 * Switch to a named input:
	 * 43      QUERY_RECORDER 1[]:[]SET_INPUT[]:[]DVBInput
	 * 8       DVBInput
	 * 
	 * Switch to the next input:
	 * 52      QUERY_RECORDER 1[]:[]SET_INPUT[]:[]SwitchToNextInput
	 * 8       DVBInput
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code SET_INPUT}</li>
	 * 			<li><div class='paramType'>string</div> input-name | {@code SwitchToNextInput}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> input name ({@code UNKNOWN} if unknown)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRecorder#setInput(String)
	 * @see IRecorder#switchToNextInput()
	 * 
	 * @since {@mythProtoVersion 27}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_27),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_SET_INPUT = "SET_INPUT";
	
	/**
	 * Toggle channel favorite.
	 * <p>
	 * This command toggles whether the current channel should be on our favorites list.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 58      QUERY_RECORDER 1[]:[]TOGGLE_CHANNEL_FAVORITE[]:[]Favorites
	 * 2       ok
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code TOGGLE_CHANNEL_FAVORITE}</li>
	 * 			<li><div class='paramType'>String</div> changroup <div class='paramVersion'>since {@mythProtoVersion 45}</div></li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRecorder#toggleChannelFavorite
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_TOGGLE_CHANNEL_FAVORITE = "TOGGLE_CHANNEL_FAVORITE";	
	
	/**
	 * Change channel.
	 * <p>
	 * This command forces the recorder to change to the next or previous channel.<br> 
	 * <b>ATTENTION:</b> You must pause the recorder before doing this.
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code CHANGE_CHANNEL}</li>
	 * 			<li><div class='paramType'>int</div> direction</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#changeChannel
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_CHANGE_CHANNEL = "CHANGE_CHANNEL";	
	
	/**
	 * Set Channel.
	 * <p>
	 * This command forces the recorder to change to the named channel on the current tuner.<br>
	 * <b>ATTENTION:</b> You must pause the recorder before doing this.
	 * 
	 * 
	 * {@mythProtoExample
	 * 38      QUERY_RECORDER 1[]:[]SET_CHANNEL[]:[]3
	 * 2       ok
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code SET_CHANNEL}</li>
	 * 			<li><div class='paramType'>string</div> channel number</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ok}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#setChannel(String)
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_SET_CHANNEL = "SET_CHANNEL";	
	
	/**
	 * Sets the signal monitoring rate.
	 * <p>
	 * This is only be used to toggle signal monitoring once you are 
	 * already watching a recording.
	 * <p>
	 * {@mythProtoExample
	 * 60      QUERY_RECORDER 1[]:[]SET_SIGNAL_MONITORING_RATE[]:[]-1[]:[]0
	 * 1       0
	 * }
	 * 
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code SET_SIGNAL_MONITORING_RATE}</li>
	 * 			<li><div class='paramType'>int</div> rate (Milliseconds between each signal check, {@code 0} to disable, {@code -1} to preserve old value.)</li>
	 * 			<li><div class='paramType'>int</div> notifyFrontend (If {@code 1} SIGNAL messages are sent to the frontend, if {@code 0} SIGNAL messages will not be sent, and if {@code -1} the old value is preserved.)</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> old rate</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 18}
	 * @mythProtoVersionRange
	 *        
	 * @see IRecorder#setSignalMonitoringRate(int, boolean)
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_18,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="6847"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="91b4b42d7390d03af7a6")
	}),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_SET_SIGNAL_MONITORING_RATE = "SET_SIGNAL_MONITORING_RATE";	
	
	/**
	 * Get colour.
	 * <p>
	 * Gets the colour of a recording.
	 * 
	 * {@mythProtoExample
	 * 31      QUERY_RECORDER 1[]:[]GET_COLOUR
	 * 2       50
	 * 
	 * On Error:
	 * 31      QUERY_RECORDER 1[]:[]GET_COLOUR
	 * 2       -1
	 * }
	 * 
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_COLOUR}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> color ({@code -1} on error)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 30}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getColour()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_30),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_COLOUR = "GET_COLOUR";
	
	/**
	 * Get contrast.
	 * <p>
	 * Gets the contrast of a recording.
	 * 
	 * {@mythProtoExample
	 * 33      QUERY_RECORDER 1[]:[]GET_CONTRAST
	 * 2       50
	 * 
	 * On Error:
	 * 33      QUERY_RECORDER 1[]:[]GET_CONTRAST
	 * 2       -1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_CONTRAST}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> contrast ({@code -1} on error)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 30}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getContrast()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_30),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_CONTRAST = "GET_CONTRAST";
	
	/**
	 * Get Brightness.
	 * <p>
	 * Gets the brightness of a recording.
	 * 
	 * {@mythProtoExample
	 * 35      QUERY_RECORDER 1[]:[]GET_BRIGHTNESS
	 * 2       50
	 * 
	 * On Error:
	 * 35      QUERY_RECORDER 1[]:[]GET_BRIGHTNESS
	 * 2       -1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_BRIGHTNESS}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> brightness ({@code -1} on error)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * 
	 * @since {@mythProtoVersion 30}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getBrightness()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_30),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_BRIGHTNESS = "GET_BRIGHTNESS";
	
	/**
	 * Get hue.
	 * <p>
	 * Gets the hue of a recording.
	 * 
	 * {@mythProtoExample
	 * 28      QUERY_RECORDER 1[]:[]GET_HUE
	 * 2       50
	 * 
	 * On Error:
	 * 28      QUERY_RECORDER 1[]:[]GET_HUE
	 * 2       -1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_HUE}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> hue ({@code -1} on error)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 30}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getHue()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_30),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_HUE = "GET_HUE";
	
	/**
	 * Change colour.
	 * <p>
	 * Changes the colour of a recording.<br>
	 * <b>Note:</b> In practice this only works with frame grabbing recorders.
	 * 
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code CHANGE_COLOUR}</li>
	 * 			<li><div class='paramType'>int</div> type <div class='paramVersion'>since {@mythProtoVersion 30}</div></li>
	 * 			<li><div class='paramType'>bool</div> {@code 1}=up, {@code 0}=down</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> color ({@code -1} on error)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * 
	 * @see IRecorder#changeColour(EPictureAdjustmentType, boolean)
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_CHANGE_COLOUR = "CHANGE_COLOUR";
	
	/**
	 * Changes contrast.
	 * <p>
	 * Changes the contrast of a recording.<br>
	 * <b>Note:</b> In practice this only works with frame grabbing recorders.
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code CHANGE_CONTRAST}</li>
	 * 			<li><div class='paramType'>int</div> type <div class='paramVersion'>since {@mythProtoVersion 30}</div></li>
	 * 			<li><div class='paramType'>bool</div> {@code 1}=up, {@code 0}=down</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div>: contrast ({@code -1} on error)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRecorder#changeContrast(EPictureAdjustmentType, boolean)
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_CHANGE_CONTRAST = "CHANGE_CONTRAST";
	
	/**
	 * Changes brightness.
	 * <p>
	 * Changes the brightness of a recording.<br>
	 * <b>Note:</b> In practice this only works with frame grabbing recorders.
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code CHANGE_BRIGHTNESS}</li>
	 * 			<li><div class='paramType'>int</div> type <div class='paramVersion'>since {@mythProtoVersion 30}</div></li>
	 * 			<li><div class='paramType'>bool</div> {@code 1}=up, {@code 0}=down</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> brightness ({@code -1} on error)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRecorder#changeBrightness(EPictureAdjustmentType, boolean)
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_CHANGE_BRIGHTNESS = "CHANGE_BRIGHTNESS";
	
	/**
	 * Changes hue.
	 * <p>
	 * Changes the hue of a recording.<br>
	 * <b>Note:</b> In practice this only works with frame grabbing recorders.
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code CHANGE_HUE}</li>
	 * 			<li><div class='paramType'>int</div> type <div class='paramVersion'>since {@mythProtoVersion 30}</div></li>
	 * 			<li><div class='paramType'>bool</div> {@code 1}=up, {@code 0}=down</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> hue ({@code -1} on error)</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRecorder#changeHue(EPictureAdjustmentType, boolean)
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_CHANGE_HUE = "CHANGE_HUE";
		
	/**
	 * Check if channel exists on current.
	 * <p>
	 * This command checks if named channel exists on current tuner.<br>
	 * <b>ATTENTION:</b> You must pause the recorder before doing this.
	 * 
	 * {@mythProtoExample
	 * 44      QUERY_RECORDER 1[]:[]CHECK_CHANNEL[]:[]10123
	 * 1       1
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code CHECK_CHANNEL}</li>
	 * 			<li><div class='paramType'>string</div> channel number</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>boolean</div> {@code 1}=channel available</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#checkChannel(String)
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_CHECK_CHANNEL = "CHECK_CHANNEL";	
	
	/**
	 * Check if channel exists on other recorder.
	 * <p>
	 * This command checks if named channel exists on current tuner, or another tuner.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 49      QUERY_RECORDER 1[]:[]SHOULD_SWITCH_CARD[]:[]11123
	 * 1       0
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code SHOULD_SWITCH_CARD}</li>
	 * 			<li><div class='paramType'>string</div> channel number</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>boolean</div> {@code 0}=switch not required</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 17}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#shouldSwitchCard(Integer)
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_17,fromInfo={
			@MythProtoVersionMetadata(key=SVN_COMMIT,value="6392"),
			@MythProtoVersionMetadata(key=GIT_COMMIT,value="a4bd91f4699ec453bac1")
	}),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_SHOULD_SWITCH_CARD = "SHOULD_SWITCH_CARD";	
	
	/**
	 * Check channel prefix.
	 * <p>
	 * Checks a channel-number prefix against the channels in the MythTV-DB.
	 * 
	 * {@mythProtoExample
	 * Example for unknown prefix:
	 * 47      QUERY_RECORDER 1[]:[]CHECK_CHANNEL_PREFIX[]:[]3
	 * 19      0[]:[]0[]:[]0[]:[]X
	 * 
	 * Example for known prefix:
	 * 50      QUERY_RECORDER 1[]:[]CHECK_CHANNEL_PREFIX[]:[]1012
	 * 19      1[]:[]0[]:[]1[]:[]X
	 * 
	 * Example for full matching channel number:
	 * 51      QUERY_RECORDER 1[]:[]CHECK_CHANNEL_PREFIX[]:[]10123
	 * 19      1[]:[]1[]:[]0[]:[]X
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code CHECK_CHANNEL_PREFIX}</li>
	 * 			<li><div class='paramType'>string</div> channel number prefix</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> match</li>
	 * 			<li><div class='paramType'>int</div> unique</li>
	 * 			<li><div class='paramType'>int</div> extra_char_useful <div class='paramVersion'>since {@mythProtoVersion 24}</div></li>
	 * 			<li><div class='paramType'>string</div> needed_spacer <div class='paramVersion'>since {@mythProtoVersion 24}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @see IRecorder#checkChannelPrefix(String)
	 * @see IRecorderChannelPrefixStatus
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_CHECK_CHANNEL_PREFIX = "CHECK_CHANNEL_PREFIX";
	
	/**
	 * Query program info.
	 * <p>
	 * Command to query informations about a channel program.<br>
	 * This command returns information about the program that would be seen if we changed the channel using 
	 * ChangeChannel(int) with "direction".
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 85      QUERY_RECORDER 1[]:[]GET_NEXT_PROGRAM_INFO[]:[]2[]:[]18[]:[]1[]:[]2008-02-07T07:20:47
	 * 203     Sabrina - Total verhext![]:[]Die Verj&uuml;ngungskur[]:[] []:[]Comedyserie[]:[]2008-02-07T07:00:00[]:[]2008-02-07T07:25:00[]:[]PRO7[]:[]/home/mythtv/.mythtv/channels/pro7.gif[]:[]3[]:[]19[]:[]193741841[]:[] 
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_NEXT_PROGRAM_INFO}</li>
	 * 			<li><div class='paramType'>string</div> channel number</li>
	 * 			<li><div class='paramType'>string</div> chanid</li>
	 * 			<li><div class='paramType'>int</div> direction</li>
	 * 			<li><div class='paramType'>date</div> starttime</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> title</li>
	 * 			<li><div class='paramType'>string</div> subtitle</li>
	 * 			<li><div class='paramType'>string</div> desc</li>
	 * 			<li><div class='paramType'>string</div> category</li>
	 * 			<li><div class='paramType'>date</div> starttime</li>
	 * 			<li><div class='paramType'>date</div> endtime</li>
	 * 			<li><div class='paramType'>string</div> channel sign</li>
	 * 			<li><div class='paramType'>string</div> channel icon</li>
	 * 			<li><div class='paramType'>string</div> channel number</li>
	 * 			<li><div class='paramType'>int</div> channel id</li>
	 * 			<li><div class='paramType'>int</div> series id <div class='paramVersion'>since {@mythProtoVersion 8}</div></li>
	 * 			<li><div class='paramType'>int</div> program id <div class='paramVersion'>since {@mythProtoVersion 8}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getNextProgramInfo
	 * @see IRecorderNextProgramInfo
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_NEXT_PROGRAM_INFO = "GET_NEXT_PROGRAM_INFO";		
	
	/**
	 * Query current program.
	 * <p>
	 * Gets the current program on the current recorder channel.<br>
	 * <b>ATTENTION:</b> This does not return a full programInfo, instead only a subset of the programInfo fields are returned.
	 * 
	 * {@mythProtoExample
	 * Example on a busy recorder:
	 * 37      QUERY_RECORDER 1[]:[]GET_PROGRAM_INFO
	 * 221     Unter uns[]:[]Folge 3274[]:[] []:[]Familienserie[]:[]2008-02-07T08:00:00[]:[]2008-02-07T08:30:00[]:[]RTL[]:[]/home/mythtv/.mythtv/channels/rtl.gif[]:[]4[]:[]20[]:[]180627459[]:[] []:[] []:[]0[]:[]2008-02-07T08:00:00[]:[]0
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_PROGRAM_INFO}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> title</li>
	 * 			<li><div class='paramType'>string</div> subtitle</li>
	 * 			<li><div class='paramType'>string</div> desc</li>
	 * 			<li><div class='paramType'>string</div> category</li>
	 * 			<li><div class='paramType'>date</div> starttime</li>
	 * 			<li><div class='paramType'>date</div> endtime</li>
	 * 			<li><div class='paramType'>string</div> channel sign</li>
	 * 			<li><div class='paramType'>string</div> channel icon path</li>
	 * 			<li><div class='paramType'>string</div> channel number</li>
	 * 			<li><div class='paramType'>int</div> channel id</li>
	 * 			<li><div class='paramType'>int</div> series id <div class='paramVersion'>since {@mythProtoVersion 08}</div></li>
	 * 			<li><div class='paramType'>int</div> program id <div class='paramVersion'>since {@mythProtoVersion 08}</div></li>
	 * 			<li><div class='paramType'>string</div> chanOutputFilters <div class='paramVersion'>since {@mythProtoVersion 10}</div></li>
	 * 			<li><div class='paramType'>boolean</div> repeat <div class='paramVersion'>since {@mythProtoVersion 13}</div></li>
	 * 			<li><div class='paramType'>date</div> airdate <div class='paramVersion'>since {@mythProtoVersion 13}</div></li>
	 * 			<li><div class='paramType'>float</div> stars <div class='paramVersion'>since {@mythProtoVersion 13}</div></li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 21}, 
	 * 		use {@link #QUERY_RECORDER_GET_RECORDING} or {@link #QUERY_RECORDER_GET_NEXT_PROGRAM_INFO} instead.
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getProgramInfo()
	 * @see IRecorderProgramInfo
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_21),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_PROGRAM_INFO = "GET_PROGRAM_INFO";
	
	/**
	 * Query used input.
	 * <p>
	 * Command to query the name of the current input of a recorder.
	 * 
	 * {@mythProtoExample
	 * 35      QUERY_RECORDER 1[]:[]GET_INPUT_NAME
	 * 7       Tuner 0
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_INPUT_NAME}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> input name</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 21}, 
	 * 		       use {@link #QUERY_RECORDER_GET_INPUT} instead.
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getInputName()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_21),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_INPUT_NAME = "GET_INPUT_NAME";		

	/**
	 * Query channel info.
	 * <p>
	 * Command to query informations about a channel.<br>
	 * This command allows the frontend to query a backend recorder for some additional 
	 * information about the current channel (or another channel).
	 * <p>
	 * {@mythProtoExample
	 * 46      QUERY_RECORDER 1[]:[]GET_CHANNEL_INFO[]:[]1000
	 * 48      1000[]:[]1[]:[]ORF1[]:[]2[]:[]ORF 1[]:[]1.orf.at
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GET_CHANNEL_INFO}</li>
	 * 			<li><div class='paramType'>int</div> channel id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> chan id</li>
	 * 			<li><div class='paramType'>int</div> source id</li>
	 * 			<li><div class='paramType'>string</div> channel sign</li>
	 * 			<li><div class='paramType'>string</div> channel number</li>
	 * 			<li><div class='paramType'>string</div> channel name</li>
	 * 			<li><div class='paramType'>string</div> xml tv id</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 28}
	 * @mythProtoVersionRange
	 * 
	 * @see IRecorder#getChannelInfo
	 * @see IRecorderChannelInfo
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_28),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_GET_CHANNEL_INFO = "GET_CHANNEL_INFO";
	
	/**
	 * Read ringbuffer block.
	 * <p>
	 * Command to read a data block from a ringbuffer.
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code REQUEST_BLOCK_RINGBUF}</li>
	 * 			<li><div class='paramType'>int</div> block size</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> returned block size</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 20}
	 * @mythProtoVersionRange
	 * 
	 * @see IRingBuffer#readBlock(byte[], int)
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_20),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_REQUEST_BLOCK_RINGBUF = "REQUEST_BLOCK_RINGBUF";	
		
	/**
	 * Seek in ringbuffer.
	 * <p>
	 * Command to seek within a ringbuffer.
	 * 
	 * {@mythProtoExample
	 * 63      QUERY_RECORDER 1[]:[]SEEK_RINGBUF[]:[]0[]:[]0[]:[]0[]:[]0[]:[]0
	 * 7       0[]:[]0
	 * }
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code SEEK_RINGBUF}</li>
	 * 			<li><div class='paramType'>long,long</div> pos</li>
	 * 			<li><div class='paramType'>int</div> whence</li>
	 * 			<li><div class='paramType'>long,long</div> curpos</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>long,long</div> ret</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 20}
	 * @mythProtoVersionRange
	 * 
	 * @see IRingBuffer#seek(long, long, org.jmythapi.protocol.response.ITransferable.Seek)
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_20),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_SEEK_RINGBUF = "SEEK_RINGBUF";	
	
	/**
	 * Destroy ringbuffer.
	 * <p>
	 * Informs a backend that a ringbuffer is not required anymore.
	 * 
	 * {@mythProtoExample
	 * 33      QUERY_RECORDER 1[]:[]DONE_RINGBUF
	 * 2       OK
	 * }
	 * 
	 * 
	 * <dl>
	 * 	<dt><b>Command parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>int</div> recorder id</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Request parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code DONE_RINGBUF}</li>
	 * 		</ol>
	 *	</dd>
	 * 
	 *  <dt><b>Response parameters:</b></dt>
	 *  <dd>
	 * 		<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code OK}</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 00}
	 * @deprecated {@mythProtoVersion 20}
	 * @mythProtoVersionRange
	 * 
	 * @see IRingBuffer#done()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00,to=PROTO_VERSION_20),parentCommand=QUERY_RECORDER)
	public static final String QUERY_RECORDER_DONE_RINGBUF = "DONE_RINGBUF";	

	/* ==============================================================================
	 * EVENTS
	 * ============================================================================== */
	
	/**
	 * Backend Message - Done Recording.
	 * <p>
	 * This message is sent by the backend to inform the client about a finished recording.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 49      BACKEND_MESSAGE[]:[]DONE_RECORDING 1 84[]:[]empty
	 * }
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code DONE_RECORDING}</li>
	 * 			<li><div class='paramType'>int</div> recorder-id</li>
	 * 
	 * 			<!-- protoVersion &lt; 45 -->
	 * 			<li class='paramDeprecated'><div class='paramType'>int</div> file length in seconds (frames-written /  framerate) <div class='paramVersion'>deprecated {@mythProtoVersion 45}</div></li>
	 * 
	 * 			<!-- protoVersion &gt;= 45 -->
	 * 			<li><div class='paramType'>int</div> seconds since start <div class='paramVersion'>since {@mythProtoVersion 45}</div></li>
	 * 			<li><div class='paramType'>int</div> frames written <div class='paramVersion'>since {@mythProtoVersion 45}</div></li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @see IDoneRecording
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),responseClass=DoneRecording.class,parentCommand=BACKEND_MESSAGE)
	public static final String BACKEND_MESSAGE_DONE_RECORDING = "DONE_RECORDING";
	
	/**
	 * Backend Message - Recording File Size changed.
	 * <p>
	 * This message is sent by the backend on changes to the recording-file size.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 62      UPDATE_FILE_SIZE 1075 2012-01-19T22:20:00 3386394808[]:[]empty
	 * }
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code UPDATE_FILE_SIZE}</li>
	 * 			<li><div class='paramType'>int</div> channel-id</li>
	 * 			<li><div class='paramType'>date</div> recording-start-time</li>
	 * 			<li><div class='paramType'>long</div> file size</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @see IUpdateFileSize
	 * 
	 * @since {@mythProtoVersion 54}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_54),responseClass=UpdateFileSize.class,parentCommand=BACKEND_MESSAGE)	
	public static final String BACKEND_MESSAGE_UPDATE_FILE_SIZE = "UPDATE_FILE_SIZE";
	
	/**
	 * Backend Message - Recording List Changed.
	 * <p>
	 * This message is sent by the backend to inform the client about changes to a recording.
	 * <br>
	 * The following sub-types of this message are available:
	 * <ul>
	 *    <li><a href='#RECORDING_LIST_CHANGE_LIST'>RECORDING_LIST_CHANGE</a> for changes to the recording list.</li>
	 *    <li><a href='#RECORDING_LIST_CHANGE_DELETE'>RECORDING_LIST_CHANGE DELETE</a> for deleted recordings.</li>
	 *    <li><a href='#RECORDING_LIST_CHANGE_ADD'>RECORDING_LIST_CHANGE ADD</a> for added recordings.</li>
	 *    <li><a href='#RECORDING_LIST_CHANGE_UPDATE'>RECORDING_LIST_CHANGE UPDATE</a> for updated recordings.</li>
	 * </ul>
	 * <p>
	 * <h3>Protocol Version Hint:</h3>
	 * Starting with {@mythProtoVersion 52} this event was partially replaced by {@link #BACKEND_MESSAGE_UPDATE_PROG_INFO}.<br>
	 * Starting with {@mythProtoVersion 55} {@link #BACKEND_MESSAGE_UPDATE_PROG_INFO} was replaced by <code>{@link #BACKEND_MESSAGE_RECORDING_LIST_CHANGE} UPDATE</code>.
	 * <p>
	 * {@mythProtoExample
	 * 51      BACKEND_MESSAGE[]:[]RECORDING_LIST_CHANGE[]:[]empty
	 * 
	 * On new recordings (since protocol version 53)
	 * 80      BACKEND_MESSAGE[]:[]RECORDING_LIST_CHANGE ADD 1073 2012-01-20T00:58:00[]:[]empty
	 * 
	 * On changed recordings (since protocol version 55; replaces UPDATE_PROG_INFO)
	 * 557     BACKEND_MESSAGE[]:[]RECORDING_LIST_CHANGE UPDATE[]:[]Scrubs - Die Anfänger[]:[]Mein Weg nach Hause[]:[]Manchmal ist es alles andere als einfach, nach Hause zu kommen...[]:[][]:[]1063[]:[]1[]:[]PRO7[]:[]Pro7[]:[]1063_20120116125500.mpg[]:[]0[]:[]2011492416[]:[]1326715201[]:[]1326716701[]:[]0[]:[]0[]:[]0[]:[]mythbox[]:[]0[]:[]0[]:[]0[]:[]0[]:[]-3[]:[]1925[]:[]0[]:[]15[]:[]6[]:[]1326714900[]:[]1326717300[]:[]0[]:[]516[]:[]Default[]:[]0[]:[][]:[]60697138[]:[][]:[]1327001290[]:[]0.000000[]:[][]:[]0[]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]2[]:[]0[]:[]0[]:[]
	 * 
	 * On deleted recordings (since protocol version 37, GIT 544ceb5584057581c699)
	 * 83      BACKEND_MESSAGE[]:[]RECORDING_LIST_CHANGE DELETE 1063 2012-01-16T12:55:00[]:[]empty
	 * }
	 * 
	 * <h4 style="background-color: #CCCCCC; border-bottom: 1px solid black; border-top: 1px solid black;">
	 *    <a name='RECORDING_LIST_CHANGE_LIST'>RECORDING_LIST_CHANGE Message (since {@mythProtoVersion 00})</a>
	 * </h4>
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code RECORDING_LIST_CHANGE}</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * <h4 style="background-color: #CCCCCC; border-bottom: 1px solid black; border-top: 1px solid black;">
	 *    <a name='RECORDING_LIST_CHANGE_DELETE'>RECORDING_LIST_CHANGE DELETE Message (since {@mythProtoVersion 37})</a>
	 * </h4>
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code RECORDING_LIST_CHANGE}</li>
	 * 			<li><div class='paramType'>string</div> {@code DELETE}</li>
	 * 			<li><div class='paramType'>int</div> the channel id of the deleted recording.</li>
	 * 			<li><div class='paramType'>date</div> the recording-start date of the deleted recording.</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * <h4 style="background-color: #CCCCCC; border-bottom: 1px solid black; border-top: 1px solid black;">
	 *    <a name='RECORDING_LIST_CHANGE_ADD'>RECORDING_LIST_CHANGE ADD Message (since {@mythProtoVersion 53})</a>
	 * </h4>
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code RECORDING_LIST_CHANGE}</li>
	 * 			<li><div class='paramType'>string</div> {@code ADD}</li>
	 * 			<li><div class='paramType'>int</div> the channel id of the deleted recording.</li>
	 * 			<li><div class='paramType'>date</div> the recording-start date of the deleted recording.</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * <h4 style="background-color: #CCCCCC; border-bottom: 1px solid black; border-top: 1px solid black;">
	 *    <a name='RECORDING_LIST_CHANGE_UPDATE'>RECORDING_LIST_CHANGE UPDATE Message (since {@mythProtoVersion 55})</a>
	 * </h4>
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code RECORDING_LIST_CHANGE}</li>
	 * 			<li><div class='paramType'>string</div> {@code UPDATE}</li>
	 * 			<li><div class='paramType'>progInfo</div> recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @see IRecordingListChangeSingle
	 * @see IRecordingListChangeList
	 * @see IRecordingListChangeAdd
	 * @see IRecordingListChangeUpdate
	 * @see IRecordingListChangeDelete
	 * 
	 * @since {@mythProtoVersion 00}
	 * @mythProtoVersionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),responseClass=RecordingListChange.class,parentCommand=BACKEND_MESSAGE)
	public static final String BACKEND_MESSAGE_RECORDING_LIST_CHANGE = "RECORDING_LIST_CHANGE";		
	
	/**
	 * Backend Message - Update Program Info.
	 * <p>
	 * This message tells the frontends to update a particular recording.<br>
	 * This is e.g. used for bookmark updates.
	 * <p>
	 * <h3>Protocol Version Hint:</h3>
	 * This message partially replaces the message {@link #BACKEND_MESSAGE_RECORDING_LIST_CHANGE}.
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code UPDATE_PROG_INFO}</li>
	 * 			<li><div class='paramType'>progInfo</div> a recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 52}
	 * @deprecated {@mythProtoVersion 55}, replaced by <code>{@link #BACKEND_MESSAGE_RECORDING_LIST_CHANGE} UPDATE</code>.
	 * @mythProtoVersionRange
	 * 
	 * @see IUpdateProgInfo
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_52),responseClass=UpdateProgInfo.class,parentCommand=BACKEND_MESSAGE)
	public static final String BACKEND_MESSAGE_UPDATE_PROG_INFO = "UPDATE_PROG_INFO";	
	
	/**
	 * Backend Message - Master Update Program Info.
	 * <p>
	 * This message is used to send program updates to the master backend first, so it can pull the updated recstatus before sending out 
	 * {@link #BACKEND_MESSAGE_UPDATE_PROG_INFO UPDATE_PROG_INFO} to the frontends.
	 * <p>
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code MASTER_UPDATE_PROG_INFO}</li>
	 * 			<li><div class='paramType'>progInfo</div> a recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties)</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion PROTO_VERSION_54}
	 * @mythProtoversionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_54,to=PROTO_VERSION_87),parentCommand=BACKEND_MESSAGE)
	public static final String BACKEND_MESSAGE_MASTER_UPDATE_PROG_INFO = "MASTER_UPDATE_PROG_INFO";

	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_87,to=PROTO_VERSION_87),parentCommand=BACKEND_MESSAGE)
	public static final String BACKEND_MESSAGE_MASTER_UPDATE_REC_INFO = "MASTER_UPDATE_REC_INFO";

	/**
	 * Backend Message - LiveTV Watch.
	 * <p>
	 * <div class="notImplemented"><b>TODO:</b>  needs to be implemented.</div>
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code LIVETV_WATCH}</li>
	 * 			<li><div class='paramType'>int</div> card-id</li>
	 * 			<li><div class='paramType'>int</div> {@code 0} (fixed value; unknown purpose)</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 */
	public static final String BACKEND_MESSAGE_LIVETV_WATCH = "LIVETV_WATCH";
	
	/**
	 * Backend Message - Quit LiveTV.
	 * <p>
	 * <div class="notImplemented"><b>TODO:</b>  needs to be implemented.</div>
	 */
	public static final String BACKEND_MESSAGE_QUIT_LIVETV = "QUIT_LIVETV";
	
	/**
	 * Backend Message - LiveTV Chain Update.
	 * <p>
	 * This message is sent by the backend to inform the client abount live-tv chain updates.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 85      BACKEND_MESSAGE[]:[]LIVETV_CHAIN UPDATE live-seans-laptop-07-03-23T12:30:32[]:[]empty
	 * }
	 * 
	 * @see ILiveTvChainUpdate
	 * 
	 * @since {@mythProtoVersion 20}
	 * @mythProtoversionRange
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_20),responseClass=LiveTvChain.class, parentCommand=BACKEND_MESSAGE)	
	public static final String BACKEND_MESSAGE_LIVETV_CHAIN = "LIVETV_CHAIN";	
	
	/**
	 * Backend Message - Ask Recording.
	 * <p>
	 * This message is sent by the backend to ask for the permission to record a scheduled program.<br>
	 * The client can cancel the recording unsing {@link #QUERY_REMOTEENCODER_CANCEL_NEXT_RECORDING CANCEL_NEXT_RECORDING}.
	 * 
	 * {@mythProtoExample
	 * 439     BACKEND_MESSAGE[]:[]ASK_RECORDING 1 0 0 0[]:[]1 (TEST)[]:[]Thu Jun 7 11:33:00 2012[]:[][]:[]0[]:[]0[]:[][]:[]1000[]:[]1[]:[]TEST[]:[]Test[]:[]/var/lib/mythtv/recordings[]:[]0[]:[]1339061580[]:[]1339061880[]:[]0[]:[]mythbuntu1204[]:[]1[]:[]1[]:[]1[]:[]1[]:[]-1[]:[]19[]:[]1[]:[]15[]:[]6[]:[]1339061580[]:[]1339061880[]:[]0[]:[]Default[]:[][]:[][]:[][]:[][]:[]1339061593[]:[]0[]:[][]:[]Default[]:[]0[]:[]0[]:[]Default[]:[]0[]:[]0[]:[]0[]:[]0
	 * }
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code ASK_RECORDING}</li>
	 * 			<li><div class='paramType'>int</div> card-id</li>
	 * 			<li><div class='paramType'>int</div> seconds till recording</li>
	 * 			<li><div class='paramType'>boolean</div> has recording <div class='paramVersion'>since {@mythProtoVersion 23}</div></li>
	 * 			<li><div class='paramType'>boolean</div> has later showing <div class='paramVersion'>since {@mythProtoVersion 37}</div></li>
	 * 			<li><div class='paramType'>progInfo</div> a recording (see {@link org.jmythapi.protocol.response.IProgramInfo.Props here} for all properties) <div class='paramVersion'>since {@mythProtoVersion 37}</div></li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 0}
	 * @mythProtoVerson
	 * 
	 * @see IAskRecording
	 * @see #QUERY_REMOTEENCODER_CANCEL_NEXT_RECORDING
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=BACKEND_MESSAGE,responseClass=AskRecording.class)
	public static final String BACKEND_MESSAGE_ASK_RECORDING = "ASK_RECORDING";
	
	/**
	 * Backend Message - Scheduler Changed.
	 * <p>
	 * This message is sent by the backend to inform the client about changes to the scheduled recordings list.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * 45      BACKEND_MESSAGE[]:[]SCHEDULE_CHANGE[]:[]empty
	 * }
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code SCHEDULE_CHANGE}</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @see IScheduleChange
	 * 
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_00),parentCommand=BACKEND_MESSAGE,responseClass=ScheduleChange.class)	
	public static final String BACKEND_MESSAGE_SCHEDULE_CHANGE = "SCHEDULE_CHANGE";	
	
	/**
	 * Backend Message - Clear Settings Cache.
	 * <p>
	 * A event forcing the backend to clear its settings cache.
	 * <p>
	 * This command should be used after changing any backend settings and forces the backend 
	 * to reload settings from the database (for example, using MythWeb or Mythtv-setup).
	 * 
	 * {@mythProtoExample
	 * 50      BACKEND_MESSAGE[]:[]CLEAR_SETTINGS_CACHE[]:[]empty
	 * }
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code CLEAR_SETTINGS_CACHE}</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 23}
	 * @mythProtoVersionRange
	 * 
	 * @see IClearSettingsCache
	 * @see IBackend#clearSettingsCache()
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_23,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="8136"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="2875552667c6ffbfb244")
	}),responseClass=ClearSettingsCache.class,parentCommand=BACKEND_MESSAGE)
	public static final String BACKEND_MESSAGE_CLEAR_SETTINGS_CACHE = "CLEAR_SETTINGS_CACHE";	
		
	/**
	 * Backend Message - Reset Idletime.
	 * <p>
	 * Whenever mythshutdown --unlock is called send a message to the master BE telling it to restart the idle count down.
	 * <p>
	 * 
	 * {@mythProtoExample
	 *  44      BACKEND_MESSAGE[]:[]RESET_IDLETIME[]:[]empty
	 * }
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code RESET_IDLETIME}</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 40}
	 * @mythProtoVersionRange
	 * 
	 * @see IResetIdleTime
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_40,fromInfo={
		@MythProtoVersionMetadata(key=SVN_COMMIT,value="15708"),
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="a046006757ba89ed1721")
	}),responseClass=ResetIdleTime.class,parentCommand=BACKEND_MESSAGE)
	public static final String BACKEND_MESSAGE_RESET_IDLETIME = "RESET_IDLETIME";
	
	/**
	 * Backend Message - System Event.
	 * <p>
	 * 
	 * {@mythProtoExample
	 *  A newly connected client:
	 *  99      BACKEND_MESSAGE[]:[]SYSTEM_EVENT CLIENT_CONNECTED HOSTNAME mythbuntu11 SENDER mythbuntu11[]:[]empty
	 *  
	 *  A recently disconnected client:
	 *  102     BACKEND_MESSAGE[]:[]SYSTEM_EVENT CLIENT_DISCONNECTED HOSTNAME mythbuntu11 SENDER mythbuntu11[]:[]empty
	 *  
	 *  A scheduler event:
	 *  75      BACKEND_MESSAGE[]:[]SYSTEM_EVENT SCHEDULER_RAN SENDER mythbuntu11[]:[]empty
	 * }
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code SYSTEM_EVENT}</li>
	 *			<li><div class='paramType'>string</div> name of the system event</li>
	 *			<li><div class='paramType'>string[]</div> a list of key/value pairs.</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @see ISystemEvent
	 */
	@MythProtocolCmd(parentCommand=BACKEND_MESSAGE,responseClass=SystemEvent.class)	
	public static final String BACKEND_MESSAGE_SYSTEM_EVENT = "SYSTEM_EVENT";
	
	/**
	 * Backend Message - Commflag Start.
	 * <p>
	 * TODO:
	 * 
	 * @see ICommflagStart
	 */
	@MythProtocolCmd(parentCommand=BACKEND_MESSAGE,responseClass=CommflagStart.class)	
	public static final String BACKEND_MESSAGE_COMMFLAG_START = "COMMFLAG_START";
	
	/**
	 * Backend Message - Backend is idle and waiting for shutdown.
	 * <p>
	 * The backend informs that only xx secs are left to system shutdown.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * On Countdown reset:
	 * 51      BACKEND_MESSAGE[]:[]SHUTDOWN_COUNTDOWN -1[]:[]empty
	 * }
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code SHUTDOWN_COUNTDOWN}</li>
	 * 			<li><div class='paramType'>int</div> idle timeout seconts ({@code -1} if shutdown countdown is aborted)</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @see IShutdownCountdown
	 */
	@MythProtocolCmd(parentCommand=BACKEND_MESSAGE,responseClass=ShutdownCountdown.class)	
	public static final String BACKEND_MESSAGE_SHUTDOWN_COUNTDOWN = "SHUTDOWN_COUNTDOWN";
	
	/**
	 * Backend Message - Backend will shutdown now.
	 * <p>
	 * The backend informs connected clients that the master server is going down now.
	 * <p>
	 * 
	 * {@mythProtoExample
	 * On Countdown reset:
	 * 42      BACKEND_MESSAGE[]:[]SHUTDOWN_NOW[]:[]empty
	 * }
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code SHUTDOWN_NOW}</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 */
	@MythProtocolCmd(parentCommand=BACKEND_MESSAGE)
	public static final String BACKEND_MESSAGE_SHUTDOWN_NOW = "SHUTDOWN_NOW";
	
	/**
	 * Backend Message - Signal.
	 * <p>
	 * 
	 * TODO:
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code SIGNAL}</li>
	 * 			<li><div class='paramType'>int</div> card-id</li>
	 * 			<li><div class='paramType'>string</div> message text</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 */
	@MythProtocolCmd(parentCommand=BACKEND_MESSAGE)
	public static final String BACKEND_MESSAGE_SIGNAL = "SIGNAL";
	
	/**
	 * Backend Message - Download File.
	 * <p>
	 * This message informs the frontend about a currently running backend download.<br>
	 * See {@link #DOWNLOAD_FILE} on how to start a now download job.
	 * <p>
	 * The following sub-types of this message are available:
	 * <ul>
	 *    <li><a href='#DOWNLOAD_FILE_UPDATE'>DOWNLOAD_FILE UPDATE</a> on status updates</li>
	 *    <li><a href='#DOWNLOAD_FILE_FINISHED'>DOWNLOAD_FILE FINISHED</a> on finished downloads</li>
	 * </ul>
	 * 
	 * {@mythProtoExample
	 * Download in progress
	 * 173     BACKEND_MESSAGE[]:[]DOWNLOAD_FILE UPDATE[]:[]http://upload.wikimedia.org/wikipedia/de/7/7c/ORF-Logo.svg[]:[]myth://Default@192.168.10.208:6543/ORF-Logo.svg[]:[]1024[]:[]6663
	 * 
	 * Download finished
	 * 90     BACKEND_MESSAGE[]:[]DOWNLOAD_FILE FINISHED[]:[]http://upload.wikimedia.org/wikipedia/de/7/7c/ORF-Logo.svg[]:[]myth://Default@192.168.10.208:6543/ORF-Logo.svg[]:[]6663[]:[]Unknown error[]:[]0
	 * }
	 * 
	 * <h4 style="background-color: #CCCCCC; border-bottom: 1px solid black; border-top: 1px solid black;"><a name='DOWNLOAD_FILE_UPDATE'>UPDATE Message</a></h4>
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code DOWNLOAD_FILE}</li>
	 * 			<li><div class='paramType'>string</div> info type ({@code UPDATE})</li>
	 * 			<li><div class='paramType'>string</div> the remote file URI</li>
	 * 			<li><div class='paramType'>string</div> the local file URI</li>
	 * 			<li><div class='paramType'>int</div> amount of downloaded bytes</li>
	 * 			<li><div class='paramType'>int</div> total file size in bytes</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * <h4 style="background-color: #CCCCCC; border-bottom: 1px solid black; border-top: 1px solid black;"><a name='DOWNLOAD_FILE_FINISHED'>FINISHED Message</a></h4>
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code DOWNLOAD_FILE}</li>
	 * 			<li><div class='paramType'>string</div> info type ({@code FINISHED})</li>
	 * 			<li><div class='paramType'>string</div> the remote file URI</li>
	 * 			<li><div class='paramType'>string</div> the local file URI</li>
	 * 			<li><div class='paramType'>int</div> total file size in bytes</li>
	 * 			<li><div class='paramType'>string</div> error text ({@code Unknown error} on no error})</li>
	 * 			<li><div class='paramType'>int</div> error code ({@code 0} on no error})</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @since {@mythProtoVersion 58}
	 * 
	 * @see IDownloadFile
	 * @see IDownloadFileUpdate
	 * @see IDownloadFileFinished
	 * @see IBackend#downloadFile(java.net.URI, String, String)
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_58,fromInfo={
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="7fce1004ab6e3692a5cfd1b6166d31f611447a05")			
	}), parentCommand=BACKEND_MESSAGE,responseClass=DownloadFile.class)
	public static final String BACKEND_MESSAGE_DOWNLOAD_FILE = "DOWNLOAD_FILE";
	
	/**
	 * Backend Message - Video List - Change.
	 * <p>
	 * This event is sent by the backend at the end of a rescan of the video-storagegroup, if changes were found.<br>
	 * Since protocol {@mythProtoVersion 63} a list of changes is contained in the event.<br>
	 * If no changes were found, the {@link #BACKEND_MESSAGE_VIDEO_LIST_NO_CHANGE} event is sent.
	 * <p>
	 * See {@link #SCAN_VIDEOS} on how to trigger a rescan of the video storagegroup.
	 * 
	 * {@mythProtoExample
	 * 82      BACKEND_MESSAGE[]:[]VIDEO_LIST_CHANGE[]:[]deleted::4[]:[]deleted::5[]:[]deleted::6
	 * }
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code VIDEO_LIST_CHANGE}</li>
	 * 			<li><div class='paramType'>list</div> list of detected changes (possible values: {@code deleted}, {@code added} or {@code moved}) <div class='paramVersion'>since {@mythProtoVersion 68}</div></li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @see IBackend#scanVideos()
	 * @see IVideoListChange
	 * 
	 * @since {@mythProtoVersion 63}
	 */
	@MythProtocolCmd(protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_63,fromInfo={
		@MythProtoVersionMetadata(key=GIT_COMMIT,value="20097268b71d947908636e09f38ead4f875fc12e")			
	}), parentCommand=BACKEND_MESSAGE,responseClass=VideoListChange.class)
	public static final String BACKEND_MESSAGE_VIDEO_LIST_CHANGE = "VIDEO_LIST_CHANGE";	
	
	/**
	 * Backend Message - Video List - No Change.
	 * <p>
	 * This event is sent by the backend at the end of a rescan of the video-storagegroup, if no changes were found.<br>
	 * If changes were found to the storage group, the {@link #BACKEND_MESSAGE_VIDEO_LIST_CHANGE} is sent instead.<br>
	 * See {@link #SCAN_VIDEOS} on how to trigger a rescan of the video storagegroup.
	 * 
	 * {@mythProtoExample
	 * 50      BACKEND_MESSAGE[]:[]VIDEO_LIST_NO_CHANGE[]:[]empty
	 * }
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code VIDEO_LIST_NO_CHANGE}</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @see IBackend#scanVideos()
	 * @see IVideoListNoChange
	 * 
	 * @since {@mythProtoVersion 69}
	 */	
	@MythProtocolCmd(
		protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_69,fromInfo={
				@MythProtoVersionMetadata(key=GIT_COMMIT,value="82c763ac10994866c74ffb0f0ba7b924bac0df93")			
		}), 
		parentCommand=BACKEND_MESSAGE,responseClass=VideoListNoChange.class
	)
	public static final String BACKEND_MESSAGE_VIDEO_LIST_NO_CHANGE = "VIDEO_LIST_NO_CHANGE";
	
	/**
	 * Backend Message - Pixmap generated.
	 * <p>
	 * This event is sent by the backend as a result of the {@link #QUERY_GENPIXMAP2} command.<br>
	 * See {@link #QUERY_GENPIXMAP2} on how to query pixmap generation.
	 * 
	 * <p>
	 * There following sub-types of this event are available:
	 * <ul>
	 *   <li><a href='#GENERATED_PIXMAP_OK'>GENERATED_PIXMAP[]:[]OK</a> on success.</a></li>
	 *   <li><a href='#GENERATED_PIXMAP_ERROR'>GENERATED_PIXMAP[]:[]ERROR</a> on errors.</a></li>
	 * </ul>
	 * <p>
	 * 
	 * {@mythProtoExample
	 * On Success:
	 * 129638  BACKEND_MESSAGE[]:[]GENERATED_PIXMAP[]:[]OK[]:[]1000_2012-05-20T11:28:00[]:[]On Disk[]:[]2012-05-29T06:57:26[]:[]97120[]:[]42534[]:[]iVBOR...ZQAAAABJRU5ErkJggg==[]:[]TEST
	 * 
	 * On Error:
	 * 113     BACKEND_MESSAGE[]:[]GENERATED_PIXMAP[]:[]ERROR[]:[]1000_2012-05-20T11:28:00[]:[]Pixmap generation failed[]:[]TEST
	 * }
	 * 
	 * <h4 style="background-color: #CCCCCC; border-bottom: 1px solid black; border-top: 1px solid black;"><a name='GENERATED_PIXMAP_OK'>OK Message</a></h4>
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GENERATED_PIXMAP}</li>
	 * 			<li><div class='paramType'>string</div> info type ({@code OK})</li>
	 * 			<li><div class='paramType'>string</div> unique recording id (containing the channel id and recording start date)</li>
	 * 			<li><div class='paramType'>string</div> status message</li>
	 *          <li><div class='paramType'>pixmap</div> the pixmap data a recording (see {@link org.jmythapi.protocol.response.IPixmap.Props here} for all properties)</li>
	 *          <li><div class='paramType'>string</div> token (the token that was specified by the client during the request).</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * <h4 style="background-color: #CCCCCC; border-bottom: 1px solid black; border-top: 1px solid black;"><a name='GENERATED_PIXMAP_ERROR'>ERROR Message</a></h4>
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code GENERATED_PIXMAP}</li>
	 * 			<li><div class='paramType'>string</div> info type ({@code ERROR})</li>
	 * 			<li><div class='paramType'>string</div> unique recording id (containing the channel id and recording start date)</li>
	 * 			<li><div class='paramType'>string</div> status message</li>
	 *          <li><div class='paramType'>string</div> token (the token that was specified by the client during the request).</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @see IBackend#queryGenPixmap2(String, IProgramInfo)
	 * @see IPixmapGenerated
	 * 
	 * @since {@mythProtoVersion 61}
	 */
	@MythProtocolCmd(
		protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_61),
		parentCommand=BACKEND_MESSAGE,responseClass=PixmapGenerated.class
	)
	public static final String BACKEND_MESSAGE_GENERATED_PIXMAP = "GENERATED_PIXMAP";
	
	/**
	 * Backend Message - File written.
	 * <p>
	 * {@mythProtoExample
	 * 104     BACKEND_MESSAGE[]:[]FILE_WRITTEN /var/lib/mythtv/recordings/4008_20141021052500.mpg 1273393936[]:[]empty
	 * }
	 * 
	 * <dl> 
	 * 	<dt><b>Message parameters:</b></dt>
	 *  <dd>
	 *  	<ol class="cmdParams">
	 * 			<li><div class='paramType'>string</div> {@code FILE_WRITTEN}</li>
	 * 			<li><div class='paramType'>string</div> full-file-path</li>
	 * 			<li><div class='paramType'>long</div> file size</li>
	 * 		</ol>
	 *	</dd>
	 * </dl>
	 * 
	 * @see IFileWritten
	 * 
	 * @since {@mythProtoVersion 77}
	 */
	@MythProtocolCmd(
		protoVersion=@MythProtoVersionAnnotation(from=PROTO_VERSION_77),
		parentCommand=BACKEND_MESSAGE,responseClass=FileWritten.class
	)
	public static final String BACKEND_MESSAGE_FILE_WRITTEN = "FILE_WRITTEN";

	@MythProtocolCmd(
			protoVersion = @MythProtoVersionAnnotation(from=PROTO_VERSION_87),
			responseClass = IFreeInputList.class
	)
	public static final String GET_FREE_INPUT_INFO = "GET_FREE_INPUT_INFO";
	
	/* ==============================================================================
	 * IMythCommand METHODS
	 * ============================================================================== */
	/**
	 * Gets the name of this command.
	 * 
	 * @return 
	 * 		the name of the command, e.g. <code>MYTH_PROTO_VERSION</code>
	 */
	public String getName();
	
	/**
	 * Gets the arguments of this command.
	 * @return 
	 * 		the command arguments, e.g. <code>Playback sycamore 0</code>
	 */
	public List<String> getCommandArguments();
	
	/**
	 * Returns the command argument at the given position. 
	 * If e.g. the whole command is {@code ANN Playback sycamore 0}, 
	 * {@code getCommandArgument(0)} returns {@code Playback}.
	 * 
	 * @param 
	 * 		idx the position of the requested argument.
	 * @return 
	 * 		the command argument at the given position.
	 */
	public String getCommandArgument(int idx);
	
	/**
	 * Gets the amount of available command arguments
	 * @return 
	 * 		the amount of command arguments
	 */
	public int getCommandArgumentsLength();
}
