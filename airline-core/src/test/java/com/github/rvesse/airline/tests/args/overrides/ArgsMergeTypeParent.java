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
package com.github.rvesse.airline.tests.args.overrides;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;

@Command(name = "ArgsMergeTypeParent")
public class ArgsMergeTypeParent extends ArgsMergeParent {
    
    public static class A {
        public long value;
        
        public A(String value) {
            this.value = Long.parseLong(value);
        }
    }
    
    public static class B extends A {
        public B(String value) {
            super(value);
        }
    }
    
    public static class C extends B {
        public C(String value) {
            super(value);
        }
    }

    @Option(name = "--test", arity = 1)
    public B test;
    
    @Option(name = "--string", arity = 1)
    public String s;
}
