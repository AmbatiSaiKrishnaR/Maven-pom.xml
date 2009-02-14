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

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.FileUtils;
import org.apache.maven.it.util.ResourceExtractor;

/**
 * Test that changes to a project's POM file reference (MavenProject.setFile(..))
 * doesn't affect the basedir of the project instance for using that project's classes directory
 * in the classpath of another project's build...this happens when both projects are
 * built in the same reactor, and one project depends on the other.
 * 
 * @author jdcasey
 */
public class MavenITmng3693PomFileBasedirChangeTest
    extends AbstractMavenIntegrationTestCase
{
    protected MavenITmng3693PomFileBasedirChangeTest()
    {
        super( ALL_MAVEN_VERSIONS );
    }

    public void testitMNG3693 ()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-3693" );
        
        File pluginDir = new File( testDir, "maven-mng3693-plugin" );
        File projectsDir = new File( testDir, "projects" );

        Verifier verifier = new Verifier( pluginDir.getAbsolutePath() );
        
        verifier.executeGoal( "install" );

        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
        
        String depPath = verifier.getArtifactPath( "org.apache.maven.its.mng3693", "dep", "1", "pom" );

        File dep = new File( depPath );
        dep = dep.getParentFile().getParentFile();

        // remove the dependency from the local repository.
        FileUtils.deleteDirectory( dep );

        verifier = new Verifier( projectsDir.getAbsolutePath() );
        
        verifier.executeGoal( "package" );

        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

    }
}
