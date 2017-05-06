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
package org.jmythapi.database.impl;

import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1170;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jmythapi.IBasicChannelInfo;
import org.jmythapi.IGuideDataThrough;
import org.jmythapi.IRecorderChannelInfo;
import org.jmythapi.IRecorderInfo;
import org.jmythapi.ISetting;
import org.jmythapi.database.DatabaseVersion;
import org.jmythapi.database.IDatabase;
import org.jmythapi.database.IJobCommand;
import org.jmythapi.database.IJobQueue;
import org.jmythapi.database.IMythFillDatabaseSettings;
import org.jmythapi.database.IMythShutdownSettings;
import org.jmythapi.database.ISchedule;
import org.jmythapi.database.IStorageGroup;
import org.jmythapi.database.IStorageGroupDirectory;
import org.jmythapi.database.annotation.MythDatabaseColumn;
import org.jmythapi.database.annotation.MythDatabaseVersionAnnotation;
import org.jmythapi.database.utils.DatabaseUtils;
import org.jmythapi.database.utils.EnumUtils;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IRecorderNextProgramInfo;
import org.jmythapi.protocol.utils.PropertyAwareUtils;
import org.jmythapi.utils.EncodingUtils;

import com.mysql.jdbc.Statement;

/**
 * TODO: database versions are stored in: mythtv/libs/libmythtv/dbcheck.cpp
 */
public class DataBase implements IDatabase {
	public static final String DRIVER_NAME = "com.mysql.jdbc.Driver";

	private static final String DB_VERSION_PROPERTY = "DBSchemaVer";

	/**
	 * For logging
	 */
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * The database connection
	 */
	private Connection theDBConnection = null;  
	
	/**
	 * TODO: we need to change this
	 */
	private ProtocolVersion protoVersion = ProtocolVersion.PROTO_VERSION_LATEST;
	
	private int dbVersion = -1;
	
	private String dbHhostName;
	
	private int dbPort;

	private String dbName;
	
	private String dbUserName;
	
	private String dbUserPwd;
	
	public DataBase(String dbHostName, int dbPort, String dbName, String dbUserName, String dbUserPwd) throws ClassNotFoundException {
		this.dbHhostName = dbHostName;
		this.dbPort = dbPort;
		this.dbName  = dbName;
		this.dbUserName = dbUserName;
		this.dbUserPwd = dbUserPwd;
		
		// trying to load the DB driver
		Class.forName(DRIVER_NAME);
	}
	
	public int getDbVersion() {
		if(dbVersion == -1) { 		
			try {
				this.openDatabaseConnection();
			} catch(Exception e) {
				logger.log(Level.SEVERE,"Unable to determine the db schema version.",e);
			} finally {
				try { 
					this.closeDataseConnection(); 
				} catch(Exception e) {
					// ignore this
				}
			}
		}
			
		return dbVersion;
	}
	
    /**
     * 
     * @throws Exception
     */
    private void openDatabaseConnection() throws Exception {
        try {
        	if(this.theDBConnection != null && !this.theDBConnection.isClosed()) return;
        	
            // loading the driver class
            Class.forName(DRIVER_NAME).newInstance();
            
            // generating the connection string
            String mythDbConnectionURL = String.format(
            	"jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull",
            	this.dbHhostName, Integer.valueOf(this.dbPort), this.dbName
            );
            if(dbVersion >= DatabaseVersion.DB_VERSION_1302.getVersion()) {
//            	 mythDbConnectionURL += "&useTimezone=true&serverTimezone=UTC";
            }
            
            // connecting to the db
            this.theDBConnection = DriverManager.getConnection(mythDbConnectionURL,this.dbUserName,this.dbUserPwd);

            // fetch the dbVersion
            PreparedStatement pStatement = null;
            ResultSet resultSet = null;
            if(dbVersion == -1) {
            	try {
            		pStatement = this.theDBConnection.prepareStatement(String.format(
            			"SELECT data FROM settings " +
            			"WHERE value = '%s'",
            			DB_VERSION_PROPERTY
            		));
            		
            		resultSet = pStatement.executeQuery();
            		if(resultSet.next()) {
            			this.dbVersion = resultSet.getInt("data");
            		}
            		
            	} catch (Throwable e) {
            		final IOException ioe = new IOException("Unable to determine the db schema version.");
            		ioe.initCause(e);
            		throw ioe;
        		} finally {
        			try {
        				if(resultSet != null) resultSet.close();
        				if(pStatement != null) pStatement.close();
        			} catch (Exception e) {
        				logger.log(Level.SEVERE,e.getMessage(),e);
        			}
        		}	
            }            
        } catch (Exception e) {
        	final IOException ioe = new IOException("Unable to establish a database connection.");
        	ioe.initCause(e);
        	throw ioe;
        }              
    }
    
    private void closeDataseConnection() throws Exception {
    	if(this.theDBConnection != null) {
    		this.theDBConnection.close();
    	}
    }
	
	public IRecorderChannelInfo getChannelInfo(Integer chanID) throws IOException {		
		final String sql = "SELECT * FROM channel WHERE chanid = ?";
		return this.queryDataItem(ChannelInfo.class,IRecorderChannelInfo.Props.class, sql, chanID);
	}
	
	@SuppressWarnings("unchecked")
	public List<IRecorderChannelInfo> getChannelInfosByCallSign(String callSign) throws IOException {
		final String sql = "SELECT * FROM channel WHERE callsign = ?";
		final List<? extends IRecorderChannelInfo> result = this.queryDataList(ChannelInfo.class,IRecorderChannelInfo.Props.class, sql, callSign);
		return (List<IRecorderChannelInfo>) result;
	}
	
	public List<IRecorderChannelInfo> getChannelInfos() throws IOException {
		return this.getChannelInfos((Integer)null);
	}
	
	public List<IRecorderChannelInfo> getChannelInfos(IRecorderInfo recorder) throws IOException {
		return this.getChannelInfos(recorder==null?null:recorder.getRecorderID());
	}
	
	@SuppressWarnings("unchecked")
	public List<IRecorderChannelInfo> getChannelInfos(Integer recorderID) throws IOException {
		String sql = "SELECT * FROM channel";
		if(recorderID != null) {
			sql += " INNER JOIN cardinput ON channel.sourceid = cardinput.sourceid WHERE cardid = ? AND visible=1";
		} else {
			sql += " WHERE visible=1";
		}
		List<? extends IRecorderChannelInfo> result = null;
		if(recorderID == null) {
			result = this.queryDataList(ChannelInfo.class,IRecorderChannelInfo.Props.class, sql);
		} else {
			result = this.queryDataList(ChannelInfo.class,IRecorderChannelInfo.Props.class, sql, recorderID);
		}
		if(result == null || result.isEmpty()) return null;
		return (List<IRecorderChannelInfo>) result;
	}
	
	public IGuideDataThrough queryGuideDataThrough() throws IOException {
		final String sql  = "SELECT MAX(endtime) as maxDate FROM program WHERE manualid = 0";
		return this.queryDataItem(GuideDataThrough.class,IGuideDataThrough.Props.class, sql);
	}
	
	public ISetting querySetting(String hostName,String settingName) throws IOException {
		final List<ISetting> settings = this.querySettings(hostName, settingName);
		if(settings == null || settings.isEmpty()) return null;
		return settings.get(0);
	}
	
	public Map<String,ISetting> querySettingsMap(String hostName,String... settingsNames) throws IOException {
		final LinkedHashMap<String,ISetting> settingsMap = new LinkedHashMap<String, ISetting>();
		
		final List<ISetting> settingsList = this.querySettings(hostName, settingsNames);
		for(ISetting setting : settingsList) {
			settingsMap.put(setting.getName(),setting);
		}
		
		return settingsMap;
	}
	
	@SuppressWarnings("unchecked")
	public List<ISetting> querySettings(String hostName,String... settingsNames) throws IOException {
		if(settingsNames == null || settingsNames.length == 0) return null;
		
		final ArrayList<String> args = new ArrayList<String>();
		final StringBuilder sql  = new StringBuilder("SELECT * FROM settings WHERE (");
		for(String settingsName : settingsNames) {
			if(settingsName == null || settingsName.length() == 0) continue;			
			sql.append(" value = ? OR");
			args.add(settingsName);
			
		}
		sql.setLength(sql.length() - "OR".length());
		sql.append(")");
		
		if(hostName == null || hostName.equals("") || hostName.equals("*")) {
			if(hostName == null) {
				sql.append(" AND hostname is null");
			}
						
		} else {
			sql.append(" AND hostname = ?");
			args.add(hostName);
		}
		
		final List<? extends ISetting> result = this.queryDataList(Setting.class,ISetting.Props.class, sql, args.toArray());
		if(result == null || result.isEmpty()) return Collections.emptyList();
		return (List<ISetting>) result;
	}
	
	@SuppressWarnings("unchecked")
	public List<ISetting> querySettings(String hostName) throws IOException {
		List<? extends ISetting> result = null;
		String sql  = "SELECT * FROM settings";
		if(hostName == null) {
			sql += " WHERE hostname is null";
			result = this.queryDataList(Setting.class,ISetting.Props.class, sql);
		} else if(hostName.length()==0) {
			result = this.queryDataList(Setting.class,ISetting.Props.class, sql);
		} else {
			sql += " WHERE hostname = ?";
			result = this.queryDataList(Setting.class,ISetting.Props.class, sql,hostName);
		}
		if(result == null || result.isEmpty()) return Collections.emptyList();
		return (List<ISetting>) result;
	}
	
	public IMythFillDatabaseSettings queryMythFillStatus() throws IOException {
		// determine the settings names to load
		final String[] settingsNames = ASettingsGroup.getSettingsNames(IMythFillDatabaseSettings.Props.class,this.getDbVersion());
		
		// load the settings map
		final Map<String,ISetting> settings = this.querySettingsMap(SETTING_HOST_ANY, settingsNames);
		
		// create the settings group object
		return new MythFillDatabaseStatus(this.protoVersion,this.dbVersion, settings);
	}
	
	public IMythShutdownSettings queryMythShutdownStatus() throws IOException {
		// determine the settings names to load
		final String[] settingsNames = ASettingsGroup.getSettingsNames(IMythShutdownSettings.Props.class,this.getDbVersion());
		
		// load the settings map
		final Map<String,ISetting> settings = this.querySettingsMap(SETTING_HOST_ANY, settingsNames);
		
		// create the settings group object
		return new MythShutdownStatus(this.protoVersion,this.dbVersion, settings);
	}	
	
	public IRecorderInfo getRecorderForNum(Integer recorderId) throws IOException {
		final String sql = 
			"SELECT capturecard.cardid, backendnames.data as backendIp, backendports.data as backendPort FROM capturecard " + 
			"INNER JOIN (SELECT DISTINCT hostname, data FROM settings where value = 'BackendServerIP') as backendnames " + 
				"ON capturecard.hostname = backendnames.hostname " + 
			"INNER JOIN (SELECT DISTINCT hostname, data FROM settings where value = 'BackendServerPort') as backendports " + 
				"ON capturecard.hostname = backendports.hostname " + 
			"WHERE capturecard.cardid = ?";
		return this.queryDataItem(RecorderInfo.class,IRecorderInfo.Props.class, sql, recorderId);
	}
	
	@SuppressWarnings("unchecked")
	public List<IRecorderInfo> getRecorders() throws IOException {
		final String sql = 
			"SELECT capturecard.cardid, backendnames.data as backendIp, backendports.data as backendPort FROM capturecard " + 
			"INNER JOIN (SELECT DISTINCT hostname, data FROM settings where value = 'BackendServerIP') as backendnames " + 
				"ON capturecard.hostname = backendnames.hostname " + 
			"INNER JOIN (SELECT DISTINCT hostname, data FROM settings where value = 'BackendServerPort') as backendports " + 
				"ON capturecard.hostname = backendports.hostname ";
		List<? extends IRecorderInfo> result = this.queryDataList(RecorderInfo.class,IRecorderInfo.Props.class, sql);
		return (List<IRecorderInfo>) result;
	}
	
	public IRecorderNextProgramInfo queryNextProgramInfo(IBasicChannelInfo channelInfo, Date startDateTime) throws IOException {
		return this.queryNextProgramInfo(channelInfo.getChannelID(), startDateTime);
	}
	
	public IRecorderNextProgramInfo queryNextProgramInfo(Integer channelId, Date startDateTime) throws IOException {
		final String sql = 
			"SELECT " +
				"program.*, " + 
				"channel.* " + 
		    "FROM program " + 
	        "LEFT JOIN channel " +
	        	"ON program.chanid = channel.chanid " +
		    "WHERE " + 
		    	"channel.chanid = ? AND " +
		    	// we do this to truncate minutes
		    	"YEAR(program.starttime) = ? AND " + 
		    	"MONTH(program.starttime) = ? AND " + 
		    	"DAYOFMONTH(program.starttime) = ? AND " + 
		    	"HOUR(program.starttime) = ? AND " +
		    	"MINUTE(program.starttime) = ? AND " +
		    	// skipping manual-id entries
		    	"program.manualid = 0 " + 
		    	// TODO: should we use a range query for the minutes here?
		    "LIMIT 5";
		
		Calendar startDateCal = null;	
		if(this.getDbVersion() >= DatabaseVersion.DB_VERSION_1302.getVersion()) {
			// conversion to UTC required
			startDateCal = Calendar.getInstance(TimeZone.getTimeZone(EncodingUtils.TIMEZONE_UTC));
		} else {
			startDateCal = Calendar.getInstance();
		}
		startDateCal.setTimeInMillis(startDateTime.getTime());
				
		final int year = startDateCal.get(Calendar.YEAR);
		final int month = startDateCal.get(Calendar.MONTH)+1;
		final int day = startDateCal.get(Calendar.DAY_OF_MONTH);
		final int hour = startDateCal.get(Calendar.HOUR_OF_DAY);
		final int min = startDateCal.get(Calendar.MINUTE);
		
		final IRecorderNextProgramInfo result = this.queryDataItem(
			RecorderNextProgramInfo.class,IRecorderNextProgramInfo.Props.class, 
			sql,
			channelId, 
			year,month,day,hour,min
		);
		return result;		
	}
	
	/* ============================================================================
	 * STORAGE GROUPS related
	 * ============================================================================ */	
	@SuppressWarnings("unchecked")
	@MythDatabaseVersionAnnotation(from=DB_VERSION_1170)
	public List<IStorageGroupDirectory> getStorageGroupDirectories() throws IOException {
		final String sql = "SELECT * FROM storagegroup";
		List<? extends IStorageGroupDirectory> result = this.queryDataList(StorageGroupDirectory.class,IStorageGroupDirectory.Props.class, sql);
		return (List<IStorageGroupDirectory>) result;	
	}
	
	public List<IStorageGroup> getStorageGroups() throws IOException {
		final List<IStorageGroupDirectory> storageDirs = this.getStorageGroupDirectories();
		if(storageDirs == null || storageDirs.isEmpty()) return Collections.emptyList();
		
		final List<IStorageGroup> groups = new ArrayList<IStorageGroup>();
		
		final Map<Object,List<IStorageGroupDirectory>> groupedMap = PropertyAwareUtils.groupListByProperty(storageDirs,IStorageGroupDirectory.Props.GROUPNAME);
		for(Entry<Object,List<IStorageGroupDirectory>> entry : groupedMap.entrySet()) {
			final String groupName = (String) entry.getKey();
			final List<IStorageGroupDirectory> dirs = entry.getValue();
			groups.add(new StorageGroup(groupName,dirs));
		}
		
		return groups;
	}
	
	/* ============================================================================
	 * JOBS related
	 * ============================================================================ */	
	
	@SuppressWarnings("unchecked")
	public List<IJobQueue> getJobs() throws IOException {
		final String sql = "SELECT * from jobqueue order by id";
		List<? extends IJobQueue> result = this.queryDataList(JobQueue.class,IJobQueue.Props.class, sql);
		return (List<IJobQueue>) result;		
	}
	
	public JobQueue getJob(Integer jobId) throws IOException {
		if(jobId == null) return null;;
		
		final String sql = "SELECT * from jobqueue where id = ?";
		return this.queryDataItem(JobQueue.class,IJobQueue.Props.class, sql, jobId);		
	}	
	
	public Integer addJob(JobQueue job) throws IOException {
		final boolean success = this.saveDataItem("jobqueue", job, IJobQueue.Props.class, IJobQueue.Props.ID,null);
		return success?job.getId():null;
	}
	
	public boolean deleteJob(Integer jobId) throws IOException {
		return this.deleteDataItem("jobqueue",jobId,IJobQueue.Props.ID);
	}	
	
	public boolean controlJob(Integer jobId, IJobCommand command) throws IOException {
		if(jobId == null) throw new NullPointerException("The job id is null");
		else if(command == null) throw new NullPointerException("The job command is null");
		
		final JobQueue job = this.getJob(jobId);
		if(job == null) return false;		
		job.setPropertyValueObject(IJobQueue.Props.COMMANDS, command);
		
		return this.saveDataItem("jobqueue", job, IJobQueue.Props.class, IJobQueue.Props.ID,EnumSet.of(IJobQueue.Props.COMMANDS));
	}
	
	/* ============================================================================
	 * SCHEDULE related
	 * ============================================================================ */	
	
	@SuppressWarnings("unchecked")
	public List<ISchedule> getSchedules() throws IOException {
		final String sql = "SELECT * from record";
		List<? extends ISchedule> result = this.queryDataList(Schedule.class,ISchedule.Props.class, sql);
		return (List<ISchedule>) result;		
	}
	

	public ISchedule getSchedule(Integer scheduleId) throws IOException {
		final String sql = "SELECT * from record WHERE recordid = ?";
		final ISchedule result = this.queryDataItem(Schedule.class,ISchedule.Props.class, sql, scheduleId);
		return result;		
	}	
	
	public Integer addSchedule(Schedule schedule) throws IOException {
		final boolean success = this.saveDataItem("record", schedule, ISchedule.Props.class, ISchedule.Props.REC_ID,null);
		return success?schedule.getRecordingId():null;
	}	
	
	public boolean deleteSchedule(Integer scheduleId) throws IOException {
		return this.deleteDataItem("record",scheduleId,ISchedule.Props.REC_ID);
	}
	
//	public List<IRecorderNextProgramInfo>
	
	/* ============================================================================
	 * PRIVATE METHODS
	 * ============================================================================ */
	private <E extends Enum<E>, R extends ADatabaseRow<E>> R queryDataItem(Class<R> rowClass, Class<E> propsClass, CharSequence sql, Object... args) throws IOException {
		final List<R> dataItems = this.queryDataList(rowClass, propsClass, sql, args);
		if(dataItems == null || dataItems.isEmpty()) return null;
		if(dataItems.size() > 1) {
			logger.info(String.format(
				"%d records found but only one record was expected.",
				Integer.valueOf(dataItems.size())
			));
		}
		return dataItems.get(0);
	}
	
	/**
	 * @param <E> 
	 * @param <R>
	 * @param rowClass an instance of this class should be returned for each result row
	 * @param propsClass an {@link Enum} class specifying all properties we are interested in
	 * @param sql the sql query template
	 * @param args the arguments used for the SQL query
	 * @return a list of result objects
	 * @throws IOException
	 */
	private <E extends Enum<E>, R extends ADatabaseRow<E>> List<R> queryDataList(Class<R> rowClass, Class<E> propsClass, CharSequence sql, Object... args) throws IOException {
		PreparedStatement pStatement = null;
		ResultSet resultSet = null;
		try {
			this.openDatabaseConnection();
			
			// formulate SQL
			pStatement = this.theDBConnection.prepareStatement(sql.toString());
			
			// set query parameters
			if(args != null) {				
				for(int i=1; i<=args.length; i++) {
					pStatement.setObject(i,args[i-1]);
				}
			}
			
			// fetch result
            resultSet = pStatement.executeQuery ();
            final List<R> data = DatabaseUtils.getDataRows(this.protoVersion, this.dbVersion, resultSet,rowClass,propsClass);
            return data;
		} catch (Throwable e) {
			final IOException ioe = new IOException();
			ioe.initCause(e);
			throw ioe;
		} finally {
			try {
				if(resultSet != null) resultSet.close();
				if(pStatement != null) pStatement.close();
				this.closeDataseConnection();
			} catch (Exception e) {
				logger.log(Level.SEVERE,"Unable to load data from DB",e);
			}
		}		
	}
	
	@SuppressWarnings("unused")
	private <E extends Enum<E>, R extends ADatabaseRow<E>> Boolean deleteDataItem(String tableName, R entity, E idColumn) throws IOException {
		// getting the identifier column value
		final Object identifier = entity.getPropertyValueObject(idColumn);
		return this.deleteDataItem(tableName, identifier, idColumn);
	}
	
	private <E extends Enum<E>> boolean deleteDataItem(String tableName, Object id, E idColumn) throws IOException {
		PreparedStatement pStatement = null;
		try {
			this.openDatabaseConnection();
			
			// get the column definition
			final MythDatabaseColumn colDef = EnumUtils.getEnumColumn(this.dbVersion,idColumn);
			if(colDef == null) throw new IllegalArgumentException("No column definition found for property " + idColumn);
			final String colName = colDef.column();
			
			// create the sql string
			final String sql = String.format(
				"DELETE FROM %s WHERE %s = ?",
				tableName, colName
			);
			
			// create the prepared statement
			pStatement = this.theDBConnection.prepareStatement(sql);
			pStatement.setObject(1,id);
			
			// execute the delete command
			final int rowCount = pStatement.executeUpdate();
			if(rowCount != 1) {
				logger.warning(String.format(
					"%d data items deleted from table '%s' with '%s=%s'.",
					Integer.valueOf(rowCount),
					tableName, 
					colName,
					id
				));
			}
			
			return rowCount > 0; 			
		} catch (Throwable e) {
			final IOException ioe = new IOException();
			ioe.initCause(e);
			throw ioe;
		} finally {
			try {
				if(pStatement != null) pStatement.close();
				this.closeDataseConnection();
			} catch (Exception e) {
				logger.log(Level.SEVERE,"Unable to delete data from DB",e);
			}
		}		
	}
	
	private <E extends Enum<E>, R extends ADatabaseRow<E>> boolean saveDataItem(String tableName, R entity, Class<E> entityPropertyClass, E idColumn,EnumSet<E> colsToUpdate) throws IOException {
		PreparedStatement pStatement = null;
		ResultSet resultSet = null;
		try {
			this.openDatabaseConnection();
			
			// check the id column
			String id = entity.getPropertyValue(idColumn);
			String idColName = null;
			final boolean isUpdate = id != null && id.length() > 0; 
			
			// build the query string
			final StringBuilder colString = new StringBuilder();
			final StringBuilder valString = new StringBuilder();
			final List<String> values = new ArrayList<String>();
			
			final EnumMap<E,MythDatabaseColumn> cols = EnumUtils.getEnumColumnMap(this.dbVersion,entityPropertyClass);
			for(Entry<E,MythDatabaseColumn> col : cols.entrySet()) {
				// getting the column property
				final E colProp = col.getKey();
				
				// getting the column definition
				final MythDatabaseColumn colDef = col.getValue();
				final int colLength = colDef.length();
				
				// skipping the id column for updates
				if (isUpdate && colProp.equals(idColumn)) {
					idColName = colDef.column();
					continue;
				} else if(colsToUpdate != null && !colsToUpdate.contains(colProp)) {
					continue;	
				}
				
				// getting the column value and trim it (if required)
				String value = entity.getPropertyValue(colProp);
				if(colLength > 0 && value != null && value.length() > colLength) {
					if(value.length() > 3) {
						value = value.substring(0,colLength-3);
						value += "...";
					} else {
						value = value.substring(0,colLength);
					}
				}
				
				// append the column name
				if(isUpdate) {
					colString.append(colDef.column()).append("=?,");
					values.add(value);
				} else {
					colString.append(colDef.column()).append(",");
					
					// append the values clause
					valString.append("?,");
					values.add(value);
				}
			}
			if(colString.length()>0)colString.setLength(colString.length()-1);
			if(valString.length()>0)valString.setLength(valString.length()-1);
			if(values.isEmpty()) return false;
			
			// formulate SQL
			String sql = null;
			if(isUpdate) {
				sql = String.format(
					"UPDATE %s SET %s WHERE %s=?",
					tableName,
					colString,
					idColName
				);
			} else {
				sql = String.format(
					"REPLACE INTO %s (%s) VALUES (%s)",
					tableName,
					colString,
					valString
				);
			}
			pStatement = isUpdate
				? this.theDBConnection.prepareStatement(sql)
				: this.theDBConnection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			
			// set parameter values
			int colIdx = 0;
			for(String value : values) {
				pStatement.setString(++colIdx,value);
			}
			
			// set where condition for update
			if(isUpdate) {
				pStatement.setString(++colIdx,id);
			}
			
			// fetch autogenerated key
			final int rowCount = pStatement.executeUpdate();
			if(rowCount > 0 && !isUpdate) {
				// fetch the id
				resultSet = pStatement.getGeneratedKeys();
				resultSet.next();
				id = resultSet.getString(1);
				
				// set the id of the entity
				if(id != null && idColumn != null) {
					entity.setPropertyValue(idColumn,id);
				}				
			}
			
			return rowCount > 0;
		} catch (Throwable e) {
			final IOException ioe = new IOException();
			ioe.initCause(e);
			throw ioe;
		} finally {
			try {
				if(resultSet != null) resultSet.close();
				if(pStatement != null) pStatement.close();
				this.closeDataseConnection();
			} catch (Exception e) {
				logger.log(Level.SEVERE,"Unable to save data to the DB",e);
			}
		}			
	}
}
