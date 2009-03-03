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
import java.util.Properties;

public class MavenITmng2136ActiveByDefaultProfileTest
    extends AbstractMavenIntegrationTestCase
{
    public MavenITmng2136ActiveByDefaultProfileTest()
    {
        super( ALL_MAVEN_VERSIONS );
    }

    /**
     * Test that &lt;activeByDefault/&gt; calculations for profile activation only
     * use profiles defined in the POM. [MNG-2136]
     */
    public void testitMNG2136()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-2136" );

        Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "target" );

        Properties systemProperties = new Properties();
        systemProperties.put( "expression.outputFile", new File( testDir, "target/expression.properties" ).getPath() );
        systemProperties.put( "expression.expressions", "project/properties" );
        verifier.setSystemProperties( systemProperties );
        verifier.getCliOptions().add( "--settings" );
        verifier.getCliOptions().add( "settings.xml" );
        verifier.executeGoal( "org.apache.maven.its.plugins:maven-it-plugin-expression:2.1-SNAPSHOT:eval" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        verifier.assertFilePresent( "target/expression.properties" );
        Properties props = verifier.loadProperties( "target/expression.properties" );
        assertNull( props.getProperty( "project.properties.it0102.testOutput" ) );
        assertEquals( "Success", props.getProperty( "project.properties.testOutput" ) );
        assertEquals( "PASSED", props.getProperty( "project.properties.settingsValue" ) );
        if ( matchesVersionRange( "[2.0,3.0-alpha-1)" ) )
        {
            // support for profiles.xml removed from 3.x (see MNG-4060)
            assertEquals( "Present", props.getProperty( "project.properties.profilesXmlValue" ) );
        }
    }

}
