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
package org.jmythapi.protocol.response;




/**
 * A program-filter can be used to filter {@link IProgramInfo programs} from a {@link IProgramInfoList program-list}.
 * 
 * <h4>Usage example:</h4>
 * This example shows how you can implement your own filter. The example filter only returns recordings
 * with a duration greater than 60 minutes.
 * 
 * {@mythCodeExample <pre>
 *    // get a list of all recordings
 *    IProgramInfoList allRecordings = backend.queryRecordings();
 *    
 *    // define a new duration filter
 *    IProgramInfoFilter durationFilter = new IProgramInfoFilter() &#123;			
 *       public boolean accept(IProgramInfo program) &#123;
 *          return (program.getDuration() > 60);
 *       &#125;
 *    &#125;;
 *    
 *    // filter recordings
 *    IProgramInfoList filteredRecordings = allRecordings.filter(durationFilter);
 *    
 *    // print out all matching recordings				
 *    for(IProgramInfo program : filteredRecordings) &#123;
 *       System.out.println(String.format(
 *          "%s (%d minutes)",
 *          program.getFullTitle(),
 *          program.getDuration()
 *       ));
 *    &#125;
 * </pre>}
 * 
 * See {@link ProgramInfoFilters} for a list of all predefined filters.
 * 
 * @see IRecordings#getProgramInfoList(IProgramInfoFilter)
 * @see IRecordings#iterator(IProgramInfoFilter)
 * @see IProgramInfoList#filter(IProgramInfoFilter)
 * @see IProgramInfoList#groupBy(org.jmythapi.protocol.response.IProgramInfo.Props, IProgramInfoFilter)
 */
public interface IProgramInfoFilter extends IFilter<IProgramInfo.Props, IProgramInfo> {
	/**
	 * Tests if the filter criteria matches onto the given program.
	 * 
	 * @param program
	 * 		the program that should be tested
	 * @return
	 * 		{@code true} if the given program is accepted by the filter
	 */
	public boolean accept(IProgramInfo program);
}
