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
package com.github.rvesse.airline;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Channels provides methods for the cli output, error and input channels. The implementation of the channels can be
 * changed with an implementation of {@link ChannelFactory} which must be registered via {@link ServiceLoader}. The
 * default implementation is {@link SystemChannelFactory}.
 */
public final class Channels {

    private static ChannelFactory FACTORY;

    static {
        ServiceLoader<ChannelFactory> serviceLoader = ServiceLoader.load(ChannelFactory.class);
        Iterator<ChannelFactory> iterator = serviceLoader.iterator();
        if (iterator.hasNext()) {
            FACTORY = iterator.next();
        } else {
            FACTORY = new SystemChannelFactory();
        }
    }

    private Channels() {
    }

    /**
     * Returns output channel.
     *
     * @return output channel
     */
    public static PrintStream output() {
        return FACTORY.createOutput();
    }

    /**
     * Returns error channel.
     *
     * @return error channel
     */
    public static PrintStream error() {
        return FACTORY.createError();
    }

    /**
     * Returns input channel.
     *
     * @return input channel
     */
    public static InputStream input() {
        return FACTORY.createInput();
    }


}
