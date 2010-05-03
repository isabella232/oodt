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

package gov.nasa.jpl.oodt.cas.catalog.query.filter.time;

//OODT imports
import gov.nasa.jpl.oodt.cas.catalog.metadata.TransactionalMetadata;
import gov.nasa.jpl.oodt.cas.commons.filter.ObjectTimeEvent;

/**
 * @author bfoster
 * @version $Revision$
 *
 * <p>
 * A ObjectTimeEvent that holds TermBuckets
 * <p>
 */
public class MetadataTimeEvent extends ObjectTimeEvent<TransactionalMetadata> {

    public MetadataTimeEvent(long startTime, long endTime, TransactionalMetadata timeObj) {
		super(startTime, endTime, timeObj);
    }
	
	public MetadataTimeEvent(long startTime, long endTime, double priority, TransactionalMetadata timeObj) {
		super(startTime, endTime, priority, timeObj);
	}

}
