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

package com.github.rvesse.airline.prompts.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DelayedInputStream extends FilterInputStream {
    
    private final long delay;

    public DelayedInputStream(InputStream in, long delay) {
        super(in);
        this.delay = delay;
    }
    
    private void delay() {
        long start = System.currentTimeMillis();
        long waited = 0;
        while (waited < delay) {
            try {
                Thread.sleep(delay - waited);
            } catch (InterruptedException e) {
                // Ignore
            }
            waited = System.currentTimeMillis() - start;
        }
    }

    @Override
    public int read() throws IOException {
        delay();
        return super.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        delay();
        return super.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        delay();
        return super.read(b, off, len);
    }

}
