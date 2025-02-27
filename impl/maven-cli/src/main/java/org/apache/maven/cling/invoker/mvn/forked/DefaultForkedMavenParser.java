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
package org.apache.maven.cling.invoker.mvn.forked;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.ParseException;
import org.apache.maven.api.cli.ParserException;
import org.apache.maven.api.cli.mvn.MavenOptions;
import org.apache.maven.api.cli.mvn.forked.ForkedMavenInvokerRequest;
import org.apache.maven.api.cli.mvn.forked.ForkedMavenParser;
import org.apache.maven.cling.invoker.mvn.BaseMavenParser;
import org.apache.maven.cling.invoker.mvn.CommonsCliMavenOptions;
import org.apache.maven.cling.invoker.mvn.LayeredMavenOptions;

/**
 * Forked invoker that invokes Maven in a child process.
 */
public class DefaultForkedMavenParser extends BaseMavenParser<MavenOptions, ForkedMavenInvokerRequest>
        implements ForkedMavenParser {

    @SuppressWarnings("ParameterNumber")
    @Override
    protected ForkedMavenInvokerRequest getInvokerRequest(LocalContext context) {
        return new DefaultForkedMavenInvokerRequest(
                context.parserRequest,
                context.cwd,
                context.installationDirectory,
                context.userHomeDirectory,
                context.userProperties,
                context.systemProperties,
                context.topDirectory,
                context.rootDirectory,
                context.parserRequest.in(),
                context.parserRequest.out(),
                context.parserRequest.err(),
                context.extensions,
                getJvmArguments(context.rootDirectory),
                (MavenOptions) context.options);
    }

    protected List<String> getJvmArguments(Path rootDirectory) {
        if (rootDirectory != null) {
            Path jvmConfig = rootDirectory.resolve(".mvn/jvm.config");
            if (Files.exists(jvmConfig)) {
                try {
                    return Files.readAllLines(jvmConfig).stream()
                            .filter(l -> !l.isBlank() && !l.startsWith("#"))
                            .flatMap(l -> Arrays.stream(l.split(" ")))
                            .collect(Collectors.toList());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
        return null;
    }

    // TODO: same is in DefaultMavenParser!!! (duplication)
    @Override
    protected MavenOptions parseArgs(String source, List<String> args) throws ParserException {
        try {
            return CommonsCliMavenOptions.parse(source, args.toArray(new String[0]));
        } catch (ParseException e) {
            throw new ParserException("Failed to parse source " + source, e.getCause());
        }
    }

    // TODO: same is in DefaultMavenParser!!! (duplication)
    @Override
    protected MavenOptions assembleOptions(List<MavenOptions> parsedOptions) {
        return LayeredMavenOptions.layerMavenOptions(parsedOptions);
    }
}
