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

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-3885">MNG-3885</a>.
 * 
 * @author Benjamin Bentmann
 * @version $Id$
 */
public class MavenITmng3885UniqueVersionFromParentProfileTest
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng3885UniqueVersionFromParentProfileTest()
    {
        super( "(2.0.10,)" );
    }

    /**
     * Test that uniqueVersion=false defined by a parent profile is effective for child modules when building
     * from the parent.
     */
    public void testitNonUniqueVersionReactor()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-3885" );

        Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        verifier.setLogFileName( "log-f.txt" );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "repo-f" );
        verifier.filterFile( "pom-template.xml", "pom.xml", "UTF-8", verifier.newDefaultFilterProperties() );
        verifier.getCliOptions().add( "-Pnon-unique-version" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFilePresent( "repo-f/org/apache/maven/its/mng3885/sub/0.2-SNAPSHOT/sub-0.2-SNAPSHOT.jar" );
    }

    /**
     * Test that uniqueVersion=false defined by a parent profile is effective for child modules when building
     * the child in isolation.
     */
    public void testitNonUniqueVersionStandalone()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-3885" );

        Verifier verifier = new Verifier( new File( testDir, "sub" ).getAbsolutePath() );
        verifier.setLogFileName( "log-f.txt" );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "repo-f" );
        verifier.filterFile( "../pom-template.xml", "../pom.xml", "UTF-8", verifier.newDefaultFilterProperties() );
        verifier.getCliOptions().add( "-Pnon-unique-version" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFilePresent( "repo-f/org/apache/maven/its/mng3885/sub/0.2-SNAPSHOT/sub-0.2-SNAPSHOT.jar" );
    }

    /**
     * Test that uniqueVersion=true defined by a parent profile is effective for child modules when building
     * from the parent.
     */
    public void testitUniqueVersionReactor()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-3885" );

        Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        verifier.setLogFileName( "log-t.txt" );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "repo-t" );
        verifier.filterFile( "pom-template.xml", "pom.xml", "UTF-8", verifier.newDefaultFilterProperties() );
        verifier.getCliOptions().add( "-Punique-version" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFileNotPresent( "repo-t/org/apache/maven/its/mng3885/sub/0.2-SNAPSHOT/sub-0.2-SNAPSHOT.jar" );
        verifier.assertFilePresent( "repo-t/org/apache/maven/its/mng3885/sub/0.2-SNAPSHOT/sub-0.2-*-1.jar" );
    }

    /**
     * Test that uniqueVersion=true defined by a parent profile is effective for child modules when building
     * the child in isolation.
     */
    public void testitUniqueVersionStandalone()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-3885" );

        Verifier verifier = new Verifier( new File( testDir, "sub" ).getAbsolutePath() );
        verifier.setLogFileName( "log-t.txt" );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "repo-t" );
        verifier.filterFile( "../pom-template.xml", "../pom.xml", "UTF-8", verifier.newDefaultFilterProperties() );
        verifier.getCliOptions().add( "-Punique-version" );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFileNotPresent( "repo-t/org/apache/maven/its/mng3885/sub/0.2-SNAPSHOT/sub-0.2-SNAPSHOT.jar" );
        verifier.assertFilePresent( "repo-t/org/apache/maven/its/mng3885/sub/0.2-SNAPSHOT/sub-0.2-*-1.jar" );
    }

}
