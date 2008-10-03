package org.apache.maven.plugin.coreit;

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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Creates a properties file with the effective values of some user-defined expressions. Unlike Maven's built-in
 * expression syntax for interpolation, these expressions use forward slashes to navigate down the object graph and
 * support access to individual collection/array elements. Furthermore, the result of an expression need not be a scalar
 * value but can also be a collection/array or a bean-like object (from the Maven model). For example, the expression
 * "project/dependencies/0" would extract the first project dependency. In more detail, this example expression could
 * output the following keys to the properties file:
 * 
 * <pre>
 * project.dependencies.0.groupId = org.apache.maven
 * project.dependencies.0.artifactId = maven-project
 * project.dependencies.0.type = jar
 * project.dependencies.0.version = 2.0
 * project.dependencies.0.optional = false
 * project.dependencies.0.exclusions = 2
 * project.dependencies.0.exclusions.0.groupId = plexus
 * project.dependencies.0.exclusions.0.artifactId = plexus-utils
 * project.dependencies.0.exclusions.1.groupId = plexus
 * project.dependencies.0.exclusions.1.artifactId = plexus-container-default
 * </pre>
 * 
 * Expressions that reference non-existing objects or use invalid collection/array indices silently resolve to
 * <code>null</code>.
 * 
 * @goal eval
 * @phase initialize
 * 
 * @author Benjamin Bentmann
 * @version $Id$
 */
public class EvalMojo
    extends AbstractMojo
{

    /**
     * The path to the output file for the properties with the expression values. For each expression given by the
     * parameter {@link #expressions} an similar named properties key will be used to save the expression value. If an
     * expression evaluated to <code>null</code>, there will be no corresponding key in the properties file.
     * 
     * @parameter expression="${expression.outputFile}" default-value="${project.build.directory}/expression.properties"
     */
    private File outputFile;

    /**
     * The set of expressions to evaluate.
     * 
     * @parameter
     */
    private String[] expressions;

    /**
     * The comma separated set of expressions to evaluate.
     * 
     * @parameter expression="${expression.expressions}"
     */
    private String expressionList;

    /**
     * The current Maven project against which expressions are evaluated.
     * 
     * @parameter default-value="${project}"
     * @readonly
     */
    private Object project;

    /**
     * The merged user/global settings of the current build against which expressions are evaluated.
     * 
     * @parameter default-value="${settings}"
     * @readonly
     */
    private Object settings;

    /**
     * The session context of the current build against which expressions are evaluated.
     * 
     * @parameter default-value="${session}"
     * @readonly
     */
    private Object session;

    /**
     * The local repository of the current build against which expressions are evaluated.
     * 
     * @parameter default-value="${localRepository}"
     * @readonly
     */
    private Object localRepository;

    /**
     * Runs this mojo.
     * 
     * @throws MojoExecutionException If the output file could not be created.
     * @throws MojoFailureException If the output file has not been set.
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( outputFile == null )
        {
            throw new MojoFailureException( "Path name for output file has not been specified" );
        }

        getLog().info( "[MAVEN-CORE-IT-LOG] Creating output file: " + outputFile );

        Properties expressionProperties = new Properties();

        if ( expressionList != null && expressionList.length() > 0 )
        {
            expressions = expressionList.split( "," );
        }
        if ( expressions != null && expressions.length > 0 )
        {
            Map contexts = new HashMap();
            contexts.put( "project", project );
            contexts.put( "pom", project );
            contexts.put( "settings", settings );
            contexts.put( "session", session );
            contexts.put( "localRepository", localRepository );

            for ( int i = 0; i < expressions.length; i++ )
            {
                Object value = ExpressionUtil.evaluate( expressions[i], contexts );
                PropertyUtil.store( expressionProperties, expressions[i].replace( '/', '.' ), value );
            }
        }

        try
        {
            PropertyUtil.write( expressionProperties, outputFile );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Output file could not be created: " + outputFile, e );
        }

        getLog().info( "[MAVEN-CORE-IT-LOG] Created output file: " + outputFile );
    }

}
