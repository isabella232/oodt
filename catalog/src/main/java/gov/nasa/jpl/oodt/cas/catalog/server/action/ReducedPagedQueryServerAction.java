/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gov.nasa.jpl.oodt.cas.catalog.server.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import gov.nasa.jpl.oodt.cas.catalog.metadata.TransactionalMetadata;
import gov.nasa.jpl.oodt.cas.catalog.page.Page;
import gov.nasa.jpl.oodt.cas.catalog.page.PageInfo;
import gov.nasa.jpl.oodt.cas.catalog.query.QueryExpression;
import gov.nasa.jpl.oodt.cas.catalog.query.parser.QueryParser;
import gov.nasa.jpl.oodt.cas.catalog.system.impl.CatalogServiceClient;

public class ReducedPagedQueryServerAction extends CatalogServiceServerAction {

	protected int pageNum;
	protected int pageSize;
	protected String query;
	protected Set<String> catalogIds;
	protected List<String> termNames;
	
	public void performAction(CatalogServiceClient csClient) throws Exception {
		QueryExpression queryExpression = QueryParser.parseQueryExpression(query);
		System.out.println(queryExpression);
		Page page = null;
		if (catalogIds == null) 
			page = csClient.getPage(new PageInfo(pageSize, pageNum), queryExpression);
		else
			page = csClient.getPage(new PageInfo(pageSize, pageNum), queryExpression, catalogIds);
		List<TransactionalMetadata> transactionMetadatas = csClient.getMetadata(page);
		for (TransactionalMetadata tMet : transactionMetadatas) {
			StringBuffer sb = new StringBuffer("");
			for (String termName : this.termNames) {
				List<String> values = tMet.getMetadata().getAllMetadata((String) termName);
				sb.append(termName + " = '" + (values == null ? "null" : StringUtils.join(values.iterator(), ",")) + "', ");
			}
			System.out.println(sb.substring(0, sb.length() - 2));
		}
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	 
	public void setQuery(String query) {
		this.query = query;
	}
	
	public void setCatalogIds(List<String> catalogIds) {
		this.catalogIds = new HashSet<String>(catalogIds);
	}
	
	public void setReducedTerms(List<String> termNames) {
		this.termNames = termNames;
	}

}
