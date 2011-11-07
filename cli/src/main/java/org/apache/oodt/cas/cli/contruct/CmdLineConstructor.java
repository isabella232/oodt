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
package org.apache.oodt.cas.cli.contruct;

//JDK imports
import java.util.Set;

//OODT imports
import org.apache.oodt.cas.cli.exception.CmdLineConstructionException;
import org.apache.oodt.cas.cli.option.CmdLineOption;
import org.apache.oodt.cas.cli.option.CmdLineOptionInstance;
import org.apache.oodt.cas.cli.util.CmdLineIterable;
import org.apache.oodt.cas.cli.util.ParsedArg;

/**
 * Responsible for constructing {@link CmdLineOptionInstance}s from parsed
 * command line arguments.
 *
 * @author bfoster (Brian Foster)
 */
public interface CmdLineConstructor {

   public Set<CmdLineOptionInstance> construct(CmdLineIterable<ParsedArg> parsedArgs,
         Set<CmdLineOption> validOptions) throws CmdLineConstructionException;

}
