package org.apache.maven.it;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.it.util.ResourceExtractor;
import org.apache.maven.it.Verifier;

import java.io.File;

public class MavenITmng3536AppendedAbsolutePaths extends AbstractMavenIntegrationTestCase {
    
    public MavenITmng3536AppendedAbsolutePaths()
    {
        super( "(2.0.99,)"); // 2.1.0+ only
    }

    public void testitMNG3536() throws Exception {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(),
                                                                 "/mng-3536-appendedAbsolutePaths" );
        File pluginDir = new File( testDir, "plugin" );
        Verifier verifier = new Verifier( pluginDir.getAbsolutePath() );

        verifier.executeGoal( "install" );

        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        File projectDir = new File( testDir, "project" );
        verifier = new Verifier( projectDir.getAbsolutePath() );

        verifier.executeGoal( "verify" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
    }
}
