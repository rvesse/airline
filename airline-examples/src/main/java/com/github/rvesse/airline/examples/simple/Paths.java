/**
 * Copyright (C) 2010-16 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rvesse.airline.examples.simple;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;

@Command(name = "paths", description = "Displays various paths")
public class Paths {

    @Option(name = "--env", description = "Specifies an environment variable whose value should be read and tested as a path")
    private List<String> envVars = new ArrayList<>();
    
    @Option(name = "--sys", description = "Specifies a JVM system property whose variable should be read and tested as a path")
    private List<String> sysProps = new ArrayList<>();
    
    @Option(name = "--empty", description = "Specifies that the empty path should be tested")
    private boolean empty = false;
    
    @Option(name = "--dot", description = "Specifies that the '.' path should be tested")
    private boolean dot = false;
    
    @Arguments(title = "Path", description = "Specifies arbitrary paths to test")
    private List<String> paths = new ArrayList<>();
    
    public static void main(String[] args) {
        SingleCommand<Paths> parser = SingleCommand.singleCommand(Paths.class);
        Paths paths = parser.parse(args);
        paths.test();
    }
    
    public void test() {
        List<PathSource> sources = new ArrayList<>();
        if (this.empty) {
            sources.add(new PathSource("--empty", ""));
        }
        if (this.dot) {
            sources.add(new PathSource("--dot", "."));
        }
        for (String envVar : this.envVars) {
            sources.add(new PathSource(String.format("Environment: %s", envVar), System.getenv(envVar)));
        }
        for (String prop : this.sysProps) {
            sources.add(new PathSource(String.format("JVM SysProp: %s", prop), System.getProperty(prop)));
        }
        for (String path : this.paths) {
            sources.add(new PathSource(String.format("Path: %s", path), path));
        }
        
        for (PathSource source : sources) {
            System.out.println(source);
            Path p = java.nio.file.Paths.get(source.getPath());
            System.out.println(String.format("Raw Path: %s", p));
            System.out.println(String.format("Absolute Path: %s", p.toAbsolutePath()));
            File f = p.toAbsolutePath().toFile();
            System.out.println(String.format("File Path: %s", f));
            System.out.println(String.format("Exists? %s", f.exists() ? "Yes" : "No"));
            System.out.println(String.format("Kind: %s", f.isDirectory() ? "Directory" : "File"));
            System.out.println(String.format("Readable? %s", f.canRead() ? "Yes" : "No"));
            System.out.println(String.format("Writeable? %s", f.canWrite() ? "Yes" : "No"));
            System.out.println(String.format("Executable? %s", f.canExecute() ? "Yes" : "No"));
            System.out.println();
        }
        
    }
    
    private static final class PathSource {
        private final String source, path;
        
        public PathSource(String source, String path) {
            this.source = source;
            this.path = path;
        }
        
        @SuppressWarnings("unused")
        public String getSource() {
            return this.source;
        }
        
        public String getPath() {
            return this.path;
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s", this.source, this.path);
        }
    }
}
