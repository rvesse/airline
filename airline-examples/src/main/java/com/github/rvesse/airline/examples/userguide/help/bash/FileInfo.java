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
package com.github.rvesse.airline.examples.userguide.help.bash;

import java.io.File;
import java.util.List;

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.help.BashCompletion;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.github.rvesse.airline.examples.ExampleRunnable;
import com.github.rvesse.airline.help.cli.bash.CompletionBehaviour;

@Command(name = "file-info", description = "Returns information about the given files")
public class FileInfo implements ExampleRunnable {
    
    @Arguments(description = "Files to get info for")
    @Required
    @BashCompletion(behaviour = CompletionBehaviour.FILENAMES)
    private List<String> files;

    @Override
    public int run() {
        for (String file : files) {
            File f = new File(file);
            System.out.printf("File: %s\n", file);
            System.out.printf("Absolute Path: %s\n", f.getAbsolutePath());
            System.out.printf("Is Directory? %s\n", f.isDirectory() ? "Yes" : "No");
            System.out.printf("Size: %,d\n", f.length());
            System.out.println();
        }
        
        return 0;
    }

}
