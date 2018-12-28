/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Original sources:
 * http://commons.apache.org/proper/commons-io/apidocs/src-html/org/apache/commons/io/FilenameUtils.html
 * http://commons.apache.org/proper/commons-io/apidocs/src-html/org/apache/commons/io/IOCase.html
 */

package itx.fileserver.services;

import java.util.ArrayList;
import java.util.Stack;

public final class FileUtils {

    private FileUtils() {
        throw new UnsupportedOperationException("please do not instantiate utility class");
    }

    private static final int NOT_FOUND = -1;

    /**
     * Checks a filename to see if it matches the specified wildcard matcher, always testing case-sensitive.
     * The wildcard matcher uses the characters '?' and '*' to represent a
     * single or multiple (zero or more) wildcard characters.
     * This is the same as often found on Dos/Unix command lines.
     * The check is case-sensitive always.
     *
     * <pre>
     *  wildcardMatch("c.txt", "*.txt")      --&gt; true
     *  wildcardMatch("c.txt", "*.jpg")      --&gt; false
     *  wildcardMatch("a/b/c.txt", "a/b/*")  --&gt; true
     *  wildcardMatch("c.txt", "*.???")      --&gt; true
     *  wildcardMatch("c.txt", "*.????")     --&gt; false
     * </pre>
     *
     * @param filename the filename to match on
     * @param wildcardMatcher the wildcard string to match against
     * @return true if the filename matches the wildcard string
     */
    public static boolean wildcardMatch(final String filename, final String wildcardMatcher) {
        return wildcardMatch(filename, wildcardMatcher, IOCase.SENSITIVE);
    }


    private static boolean wildcardMatch(final String filename, final String wildcardMatcher, IOCase caseSensitivity) {
        if (filename == null && wildcardMatcher == null) {
            return true;
        }
        if (filename == null || wildcardMatcher == null) {
            return false;
        }
        if (caseSensitivity == null) {
            caseSensitivity = IOCase.SENSITIVE;
        }
        final String[] wcs = splitOnTokens(wildcardMatcher);
        boolean anyChars = false;
        int textIdx = 0;
        int wcsIdx = 0;
        final Stack<int[]> backtrack = new Stack<>();

        // loop around a backtrack stack, to handle complex * matching
        do {
            if (backtrack.size() > 0) {
                final int[] array = backtrack.pop();
                wcsIdx = array[0];
                textIdx = array[1];
                anyChars = true;
            }

            // loop whilst tokens and text left to process
            while (wcsIdx < wcs.length) {

                if (wcs[wcsIdx].equals("?")) {
                    // ? so move to next text char
                    textIdx++;
                    if (textIdx > filename.length()) {
                        break;
                    }
                    anyChars = false;

                } else if (wcs[wcsIdx].equals("*")) {
                    // set any chars status
                    anyChars = true;
                    if (wcsIdx == wcs.length - 1) {
                        textIdx = filename.length();
                    }

                } else {
                    // matching text token
                    if (anyChars) {
                        // any chars then try to locate text token
                        textIdx = caseSensitivity.checkIndexOf(filename, textIdx, wcs[wcsIdx]);
                        if (textIdx == NOT_FOUND) {
                            // token not found
                            break;
                        }
                        final int repeat = caseSensitivity.checkIndexOf(filename, textIdx + 1, wcs[wcsIdx]);
                        if (repeat >= 0) {
                            backtrack.push(new int[] {wcsIdx, repeat});
                        }
                    } else {
                        // matching from current position
                        if (!caseSensitivity.checkRegionMatches(filename, textIdx, wcs[wcsIdx])) {
                            // couldnt match token
                            break;
                        }
                    }

                    // matched text token, move text index to end of matched token
                    textIdx += wcs[wcsIdx].length();
                    anyChars = false;
                }

                wcsIdx++;
            }

            // full match
            if (wcsIdx == wcs.length && textIdx == filename.length()) {
                return true;
            }

        } while (backtrack.size() > 0);

        return false;
    }

    private static String[] splitOnTokens(final String text) {
        if (text.indexOf('?') == NOT_FOUND && text.indexOf('*') == NOT_FOUND) {
            return new String[] { text };
        }

        final char[] array = text.toCharArray();
        final ArrayList<String> list = new ArrayList<>();
        final StringBuilder buffer = new StringBuilder();
        char prevChar = 0;
        for (final char ch : array) {
            if (ch == '?' || ch == '*') {
                if (buffer.length() != 0) {
                    list.add(buffer.toString());
                    buffer.setLength(0);
                }
                if (ch == '?') {
                    list.add("?");
                } else if (prevChar != '*') {// ch == '*' here; check if previous char was '*'
                    list.add("*");
                }
            } else {
                buffer.append(ch);
            }
            prevChar = ch;
        }
        if (buffer.length() != 0) {
            list.add(buffer.toString());
        }

        return list.toArray( new String[ list.size() ] );
    }

    private enum IOCase {

        SENSITIVE ("Sensitive", true),

        INSENSITIVE ("Insensitive", false),

        SYSTEM ("System", true);

        private final String name;

        private final transient boolean sensitive;

        public static IOCase forName(final String name) {
            for (final IOCase ioCase : IOCase.values()) {
                if (ioCase.getName().equals(name)) {
                    return ioCase;
                }
            }
            throw new IllegalArgumentException("Invalid IOCase name: " + name);
        }

        private IOCase(final String name, final boolean sensitive) {
            this.name = name;
            this.sensitive = sensitive;
        }

        private Object readResolve() {
            return forName(name);
        }

        public String getName() {
            return name;
        }

        public int checkIndexOf(final String str, final int strStartIndex, final String search) {
            final int endIndex = str.length() - search.length();
            if (endIndex >= strStartIndex) {
                for (int i = strStartIndex; i <= endIndex; i++) {
                    if (checkRegionMatches(str, i, search)) {
                        return i;
                    }
                }
            }
            return -1;
        }

        public boolean checkRegionMatches(final String str, final int strStartIndex, final String search) {
            return str.regionMatches(!sensitive, strStartIndex, search, 0, search.length());
        }

        @Override
        public String toString() {
            return name;
        }

    }

}
