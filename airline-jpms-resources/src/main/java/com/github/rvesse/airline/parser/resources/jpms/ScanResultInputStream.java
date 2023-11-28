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
package com.github.rvesse.airline.parser.resources.jpms;

import io.github.classgraph.ScanResult;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A wrapper around an InputStream originating from a {@link ScanResult} to ensure that it is closed when the stream is
 * closed
 */
class ScanResultInputStream extends FilterInputStream {

    private final ScanResult result;

    /**
     * Creates a wrapper around an {@link InputStream} that was creating from a Class Graph {@link ScanResult}.  This
     * ensures that when the stream is closed we also close the {@link ScanResult} from which it originated thus
     * avoiding any resource leaks per {@link ScanResult#close()}.
     *
     * @param result Scan Result that originated the input stream
     * @param in     Input Stream
     */
    ScanResultInputStream(ScanResult result, InputStream in) {
        super(in);
        this.result = result;
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            this.result.close();
        }
    }
}
