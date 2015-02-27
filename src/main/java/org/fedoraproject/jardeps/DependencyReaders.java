/* 
 * Copyright (c) 2015 Michal Srb
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fedoraproject.jardeps;

import org.fedoraproject.jardeps.spi.DependencyReader;

public class DependencyReaders {

    private static final Class<DefaultDependencyReader> DEFAULT_PROVIDER_CLASS = DefaultDependencyReader.class;

    private DependencyReaders() {
    }

    public static DependencyReader newInstance() {
        String className = System.getProperty("dependency.reader", DEFAULT_PROVIDER_CLASS.getName());

        DependencyReader depReader = null;
        Exception error = null;

        try {
            Class<?> cls = Class.forName(className);

            if (DEFAULT_PROVIDER_CLASS.isAssignableFrom(cls)) {
                depReader = (DependencyReader) cls.newInstance();
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            error = e;
        }

        if (depReader == null) {
            if (className != DEFAULT_PROVIDER_CLASS.getName()) {
                // try default provider
                try {
                    depReader = DEFAULT_PROVIDER_CLASS.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    error = e;
                }
            }
        }

        if (depReader == null) {
            throw new IllegalStateException(error);
        }

        return depReader;
    }
}
