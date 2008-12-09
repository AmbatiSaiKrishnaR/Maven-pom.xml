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
import java.util.Collections;
import java.util.Properties;

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-3853">MNG-3853</a>.
 * 
 * @author Benjamin Bentmann
 * @version $Id$
 */
public class MavenITmng3853ProfileInjectedDistReposTest
    extends AbstractMavenIntegrationTestCase
{

    public MavenITmng3853ProfileInjectedDistReposTest()
    {
    	 super( "(2.99,)" ); // only test in 3.0+
    }

    /**
     * Verify that distribution management repos injected by profiles are recognized by the MavenProject instance and
     * that the resulting artifact repositories are available to plugins via the corresponding expressions. Note that
     * this issue is not primarily about the effective model (which was correct for the original issue, i.e. reflected
     * the injected distributionManagement section) but whether the MavenProject wrapper around the model is in sync
     * with the model.
     */
    public void testitMNG3853()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/mng-3853" );

        Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        verifier.setAutoclean( false );
        verifier.deleteDirectory( "target" );
        verifier.setCliOptions( Collections.singletonList( "-Pcoreit" ) );
        verifier.executeGoal( "validate" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        Properties props = verifier.loadProperties( "target/dist.properties" );
        assertEquals( "1", props.getProperty( "listParam" ) );
        assertNotNull( props.getProperty( "listParam.0" ) );
        assertFalse( props.getProperty( "listParam.0" ).startsWith( "${" ) );
    }

}
