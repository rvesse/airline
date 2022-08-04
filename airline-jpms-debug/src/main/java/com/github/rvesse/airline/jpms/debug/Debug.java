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
package com.github.rvesse.airline.jpms.debug;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.AirlineModule;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.help.ExitCodes;
import com.github.rvesse.airline.examples.ExampleExecutor;
import com.github.rvesse.airline.examples.ExampleRunnable;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Command(name = "debug", description = "Debugs the visibility of resources within modules")
@ExitCodes(codes = { 0, 127, 255 }, descriptions = {
        "Successfully found some resources",
        "No resources found",
        "Help shown"
})
public class Debug implements ExampleRunnable {

    @AirlineModule
    private HelpOption<Debug> help = new HelpOption<>();

    @Option(name = {
            "-m",
            "--modules"
    }, title = "ModuleName", description = "Specifies one/more module(s) to scan.  If not specified scans all modules.")
    private List<String> modules = new ArrayList<>();

    @Option(name = {
            "-p",
            "--pattern"
    }, title = "ResourcePattern", description = "Specifies a pattern to filter the returned resources")
    private String pattern = null;

    @Option(name = "--show-module-name", description = "When set displays the Module name for each found resource")
    private boolean showModuleName = false;

    @Option(name = "--test-open", description = "When set tests whether each found resource can be successfully opened")
    private boolean testOpen = false;

    @Option(name = "--test-print",
            description = "When set implies --test-open and prints out the contents of the resources")
    private boolean testPrint = false;

    public static void main(String[] args) {
        ExampleExecutor.executeSingleCommand(Debug.class, args);
    }

    @Override
    public int run() {
        if (this.help.showHelpIfRequested()) {
            return 255;
        }

        long found = 0;

        System.out.println();
        System.out.println("Scanning...");
        ClassGraph graph = new ClassGraph();
        if (this.modules.size() > 0) {
            graph = graph.acceptModules(this.modules.toArray(new String[modules.size()]));
            System.out.println("Scanning specified modules: ");
            for (String module : modules) {
                System.out.println(" " + module);
            }
        }

        String lastModuleNameShown = null;
        try (ScanResult scanResult = graph.scan()) {
            try (ResourceList resources = filterResources(scanResult)) {
                for (Resource resource : resources.nonClassFilesOnly()) {
                    if (this.showModuleName) {
                        if (!StringUtils.equals(lastModuleNameShown, resource.getModuleRef().getName())) {
                            System.out.println("Module " + resource.getModuleRef().getName());
                            lastModuleNameShown = resource.getModuleRef().getName();
                        }
                        System.out.print(' ');
                    }
                    System.out.println(resource.getPath());

                    if (this.testOpen || this.testPrint) {
                        try (InputStream input = resource.open()) {
                            System.out.println("  Opens OK");
                            if (this.testPrint) {
                                System.out.println("  Displaying content:");
                                System.out.println("  -------------------");
                                try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                                    String line = null;
                                    while ((line = reader.readLine()) != null) {
                                        System.out.println(line);
                                    }
                                }
                                System.out.println();
                            }
                        } catch (IOException e) {
                            System.out.println("  Unable to Open: " + e.getMessage());
                        }
                    }

                    found++;
                }
            }
        }
        System.out.println("Found " + found + " resources");

        return found > 0 ? 0 : 127;
    }

    private ResourceList filterResources(ScanResult scanResult) {
        if (StringUtils.isNotBlank(this.pattern)) {
            return scanResult.getResourcesMatchingWildcard(this.pattern);
        } else {
            return scanResult.getAllResources();
        }
    }
}
