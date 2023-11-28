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
package com.github.rvesse.airline.tests.restrictions;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.PathKind;
import com.github.rvesse.airline.annotations.restrictions.IsDirectory;
import com.github.rvesse.airline.annotations.restrictions.IsFile;
import com.github.rvesse.airline.annotations.restrictions.IsPath;

@Command(name = "paths")
public class Paths {

    @Option(name = "--path", arity = 1)
    @IsPath(mustExist = true)
    public String pathMustExist;
    
    @Option(name = "--file", arity = 1)
    @IsFile(mustExist = true)
    public String fileMustExit;
    
    @Option(name = "--directory", arity = 1)
    @IsDirectory(mustExist = true)
    public String dirMustExist;
    
    @Option(name = "--readable", arity = 1)
    @IsPath(mustExist = false, readable = true, writable = false, executable = false)
    public String readable;
    
    @Option(name = "--writable", arity = 1)
    @IsPath(mustExist = false, writable = true, readable = false, executable = false)
    public String writable;
    
    @Option(name = "--executable", arity = 1)
    @IsPath(mustExist = false, executable = true, readable = false, writable = false)
    public String executable;
    
    @Option(name = "--any", arity = 1)
    @IsPath(mustExist = false, readable = false, writable = false, executable = false, kind = PathKind.ANY)
    public String anyPath;
}
