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
package com.github.rvesse.airline.tests.types;

import java.math.BigInteger;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.types.numerics.abbreviated.KiloAs1000;
import com.github.rvesse.airline.types.numerics.bases.Binary;
import com.github.rvesse.airline.types.numerics.bases.Hexadecimal;
import com.github.rvesse.airline.types.numerics.bases.Octal;

@Command(name = "ArgsRadix")
public class ArgsRadix {

    @Option(name = "--normal")
    public long normal;
    
    @Option(name = "--octal", typeConverterProvider = Octal.class)
    public long octal;
    
    @Option(name = "--hex", typeConverterProvider = Hexadecimal.class)
    public long hex;
    
    @Option(name = "--binary", typeConverterProvider = Binary.class)
    public long binary;
    
    @Option(name = "--kilo", typeConverterProvider = KiloAs1000.class)
    public long abbrev;
    
    @Option(name = "--big-octal", typeConverterProvider = Octal.class)
    public BigInteger bigOctal;
    
    @Option(name = "--big-hex", typeConverterProvider = Hexadecimal.class)
    public BigInteger bigHex;
    
    @Option(name = "--big-binary", typeConverterProvider = Binary.class)
    public BigInteger bigBinary;
}
