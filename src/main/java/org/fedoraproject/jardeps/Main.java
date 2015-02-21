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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jenkinsci.constant_pool_scanner.ConstantPoolScanner;

public class Main {

    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.exit(0);
        }

        JarFile jarFile = new JarFile(args[0]);

        Map<String, Set<String>> result = new HashMap<String, Set<String>>();

        try {
            for (Enumeration<? extends JarEntry> e = jarFile.entries(); e.hasMoreElements();) {

                JarEntry entry = e.nextElement();

                if (!entry.getName().endsWith(".class")) {
                    continue;
                }

                InputStream in = jarFile.getInputStream(entry);

                int size = (int) entry.getSize();
                int have = 0;
                byte[] bytes = new byte[size];
                while (have != size) {
                    have += in.read(bytes, have, size - have);
                }

                Set<String> deps = ConstantPoolScanner.dependencies(bytes);
                result.put(entry.getName(), deps);
            }
        } finally {
            jarFile.close();
        }

        Set<String> dependencies = new HashSet<String>();

        for (Set<String> classDeps : result.values()) {
            dependencies.addAll(classDeps);
        }

        for (String dep : dependencies) {
            System.out.println(dep);
        }
    }
}