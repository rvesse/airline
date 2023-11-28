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
package com.github.rvesse.airline.tests.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.rvesse.airline.utils.comparators.ByteComparator;
import com.github.rvesse.airline.utils.comparators.DoubleComparator;
import com.github.rvesse.airline.utils.comparators.FloatComparator;
import com.github.rvesse.airline.utils.comparators.IntegerComparator;
import com.github.rvesse.airline.utils.comparators.LongComparator;

public class ComparatorTests {

    @Test
    public void float_comparator_negatives_01() {
        FloatComparator comparator = new FloatComparator();

        Assert.assertTrue(-1f < 0f);
        Assert.assertTrue(Float.valueOf(-1f) < Float.valueOf(0f));
        Assert.assertEquals(comparator.compare(-1f, 0f), -1);
    }
    
    @Test
    public void float_comparator_positives_01() {
        FloatComparator comparator = new FloatComparator();

        Assert.assertTrue(1f > 0f);
        Assert.assertTrue(Float.valueOf(1f) > Float.valueOf(0f));
        Assert.assertEquals(comparator.compare(1f, 0f), 1);
    }
    
    @Test
    public void float_comparator_limits_01() {
        FloatComparator comparator = new FloatComparator();

        Assert.assertEquals(comparator.compare(Float.MIN_VALUE, 0f), -1);
        Assert.assertEquals(comparator.compare(0f, Float.MIN_VALUE), 1);
        Assert.assertEquals(comparator.compare(Float.MIN_VALUE, Float.MIN_VALUE), 0);
        
        Assert.assertEquals(comparator.compare(Float.MAX_VALUE, 0f), 1);
        Assert.assertEquals(comparator.compare(0f, Float.MAX_VALUE), -1);
        Assert.assertEquals(comparator.compare(Float.MAX_VALUE, Float.MAX_VALUE), 0);
    }
    
    @Test
    public void double_comparator_negatives_01() {
        DoubleComparator comparator = new DoubleComparator();

        Assert.assertTrue(-1d < 0d);
        Assert.assertTrue(Double.valueOf(-1d) < Double.valueOf(0d));
        Assert.assertEquals(comparator.compare(-1d, 0d), -1);
    }
    
    @Test
    public void double_comparator_positives_01() {
        DoubleComparator comparator = new DoubleComparator();

        Assert.assertTrue(1d > 0d);
        Assert.assertTrue(Double.valueOf(1d) > Double.valueOf(0d));
        Assert.assertEquals(comparator.compare(1d, 0d), 1);
    }
    
    @Test
    public void double_comparator_limits_01() {
        DoubleComparator comparator = new DoubleComparator();

        Assert.assertEquals(comparator.compare(Double.MIN_VALUE, 0d), -1);
        Assert.assertEquals(comparator.compare(0d, Double.MIN_VALUE), 1);
        Assert.assertEquals(comparator.compare(Double.MIN_VALUE, Double.MIN_VALUE), 0);
        
        Assert.assertEquals(comparator.compare(Double.MAX_VALUE, 0d), 1);
        Assert.assertEquals(comparator.compare(0d, Double.MAX_VALUE), -1);
        Assert.assertEquals(comparator.compare(Double.MAX_VALUE, Double.MAX_VALUE), 0);
    }
    
    @Test
    public void byte_comparator_limits_01() {
        ByteComparator comparator = new ByteComparator();

        Assert.assertTrue(comparator.compare(Byte.MIN_VALUE, (byte)0) < 0);
        Assert.assertTrue(comparator.compare((byte)0, Byte.MIN_VALUE) > 0);
        Assert.assertEquals(comparator.compare(Byte.MIN_VALUE, Byte.MIN_VALUE), 0);
        
        Assert.assertTrue(comparator.compare(Byte.MAX_VALUE, (byte)0) > 0);
        Assert.assertTrue(comparator.compare((byte)0, Byte.MAX_VALUE) < 0);
        Assert.assertEquals(comparator.compare(Byte.MAX_VALUE, Byte.MAX_VALUE), 0);
    }
    
    @Test
    public void integer_comparator_limits_01() {
        IntegerComparator comparator = new IntegerComparator();

        Assert.assertEquals(comparator.compare(Integer.MIN_VALUE, 0), -1);
        Assert.assertEquals(comparator.compare(0, Integer.MIN_VALUE), 1);
        Assert.assertEquals(comparator.compare(Integer.MIN_VALUE, Integer.MIN_VALUE), 0);
        
        Assert.assertEquals(comparator.compare(Integer.MAX_VALUE, 0), 1);
        Assert.assertEquals(comparator.compare(0, Integer.MAX_VALUE), -1);
        Assert.assertEquals(comparator.compare(Integer.MAX_VALUE, Integer.MAX_VALUE), 0);
    }
    
    @Test
    public void long_comparator_limits_01() {
        LongComparator comparator = new LongComparator();

        Assert.assertEquals(comparator.compare(Long.MIN_VALUE, 0l), -1);
        Assert.assertEquals(comparator.compare(0l, Long.MIN_VALUE), 1);
        Assert.assertEquals(comparator.compare(Long.MIN_VALUE, Long.MIN_VALUE), 0);
        
        Assert.assertEquals(comparator.compare(Long.MAX_VALUE, 0l), 1);
        Assert.assertEquals(comparator.compare(0l, Long.MAX_VALUE), -1);
        Assert.assertEquals(comparator.compare(Long.MAX_VALUE, Long.MAX_VALUE), 0);
    }
}
