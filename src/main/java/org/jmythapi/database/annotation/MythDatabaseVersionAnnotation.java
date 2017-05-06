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
package org.jmythapi.database.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jmythapi.database.DatabaseVersion;

@Retention(RUNTIME)
@Target({FIELD,METHOD,TYPE})
public @interface MythDatabaseVersionAnnotation {
	/**
	 * Gets the MythTV-database version when a parameter was introduced.
	 * 
	 * @return 
	 * 		the database version when a parameter was introduced.
	 */
	DatabaseVersion from() default DatabaseVersion.DB_VERSION_1029;
	
	/**
	 * Gets the version when a parameter was removed. 
	 * 
	 * @return 
	 * 		the MythTV-database version when a parameter was removed. 
	 * 		{@code -1} means that the command is still valid. 
	 */	
	DatabaseVersion to() default DatabaseVersion.DB_VERSION_LATEST;
}
