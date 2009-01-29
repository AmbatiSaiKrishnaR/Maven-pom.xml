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

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MavenITmng3485OverrideWagonExtensionTest
    extends AbstractMavenIntegrationTestCase
{
    public MavenITmng3485OverrideWagonExtensionTest()
    {
        super( "(2.0.8,3.0-alpha-1)" );  // extensions not supported in 3.0+
    }

    public void testitMNG3485 ()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-3485" );

        Verifier verifier;

        verifier = new Verifier( testDir.getAbsolutePath() );

        List cliOptions = new ArrayList();
        verifier.setCliOptions( cliOptions );
        verifier.executeGoal( "deploy" );

        verifier.assertFilePresent( "target/wagon-data" );
        verifier.verifyErrorFreeLog();

        verifier.resetStreams();
    }
}
