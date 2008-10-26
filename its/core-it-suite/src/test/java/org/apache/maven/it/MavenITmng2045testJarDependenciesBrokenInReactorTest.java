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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;

/**
 * Simple IT test invoking maven in a reactor with 2 projects.
 * First project produced a test-jar, which is required to 
 * compile second project. 
 *
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * @author mikko.koponen@ri.fi
 */
public class MavenITmng2045testJarDependenciesBrokenInReactorTest
    extends AbstractMavenIntegrationTestCase 
{

    public MavenITmng2045testJarDependenciesBrokenInReactorTest()
    {
        super( "(2.0.7,)" ); // 2.0.8+
    }

    public void testitMNG2045()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-2045" );
        Verifier verifier;

        verifier = new Verifier( testDir.getAbsolutePath() );
        verifier.deleteArtifact( "org.apache.maven.its.mng2045", "mng-2045-test", "1.0-SNAPSHOT", "pom" );
        verifier.deleteArtifact( "org.apache.maven.its.mng2045", "first-project", "1.0-SNAPSHOT", "jar" );
        verifier.deleteArtifact( "org.apache.maven.its.mng2045", "second-project", "1.0-SNAPSHOT", "jar" );

        verifier.executeGoal( "install" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

    }
}
