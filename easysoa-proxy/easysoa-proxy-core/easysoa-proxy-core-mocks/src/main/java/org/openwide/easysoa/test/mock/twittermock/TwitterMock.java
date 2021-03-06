/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.openwide.easysoa.test.mock.twittermock;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.osoa.sca.annotations.Remotable;

/**
 * A Twitter REST API mock
 * 
 * @author jguillemotte
 *
 */
@Remotable
public interface TwitterMock {

	// Original methods
	@GET
	@Path("/1/users/show/{user}")
	public String returnUsersShow(@PathParam("user") String user);	
	
	@GET
	@Path("/1/statuses/friends/{user}")
	public String returnStatusesFriends(@PathParam("user") String user);
	
	@GET
	@Path("/1/statuses/followers/{user}")
	public String returnStatusesFollowers(@PathParam("user") String user);

	// New methods
	@GET
	@Path("/1/tweets/lastTweet/{user}")
	public String returnLastTweet(@PathParam("user") String user);

	@GET
	@Path("/1/tweets/severalTweets/{user}")
	public String returnSeveralRecentTweet(@PathParam("user") String user, @QueryParam("tweetNumber") int tweetNumbers);
	
	@POST
	@Path("/1/tweets/postNewTweet")
	public String postNewTweet(@FormParam("user") String user, @FormParam("tweet") String tweet);
	
}
