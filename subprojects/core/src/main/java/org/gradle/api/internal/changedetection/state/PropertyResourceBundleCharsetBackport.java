/*
 * Copyright (c) 2015, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * Backport of PropertyResourceBundleCharset to retain Java 8 compatibility.
 *
 * Unchanged except the classname to avoid confusion and member variables that don't meed Gradle codestyle rules.
 */

//package sun.util;

package org.gradle.api.internal.changedetection.state;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A Charset implementation for reading PropertyResourceBundle, in order
 * for loading properties files. This first tries to load the properties
 * file with UTF-8 encoding). If it fails, then load the file with ISO-8859-1
 */
public class PropertyResourceBundleCharsetBackport extends Charset {

    private boolean strictUTF8 = false;

    public PropertyResourceBundleCharsetBackport(boolean strictUTF8) {
        this(PropertyResourceBundleCharsetBackport.class.getCanonicalName(), null);
        this.strictUTF8 = strictUTF8;
    }

    public PropertyResourceBundleCharsetBackport(String canonicalName, String[] aliases) {
        super(canonicalName, aliases);
    }

    @Override
    public boolean contains(Charset cs) {
        return false;
    }

    @Override
    public CharsetDecoder newDecoder() {
        return new PropertiesFileDecoder(this, 1.0f, 1.0f);
    }

    @Override
    public CharsetEncoder newEncoder() {
        throw new UnsupportedOperationException("Encoding is not supported");
    }

    private final class PropertiesFileDecoder extends CharsetDecoder {

        private CharsetDecoder utf8 = StandardCharsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT); // Backport: was cdUTF_8
        private CharsetDecoder iso88591 = null; // Backport: was cdISO_8859_1

        protected PropertiesFileDecoder(Charset cs,
                                        float averageCharsPerByte, float maxCharsPerByte) {
            super(cs, averageCharsPerByte, maxCharsPerByte);
        }

        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            if (Objects.nonNull(iso88591)) {
                return iso88591.decode(in, out, false);
            }
            in.mark();
            out.mark();

            CoderResult cr = utf8.decode(in, out, false);
            if (cr.isUnderflow() || cr.isOverflow() ||
                PropertyResourceBundleCharsetBackport.this.strictUTF8) {
                return cr;
            }

            // Invalid or unmappable UTF-8 sequence detected.
            // Switching to the ISO 8859-1 decorder.
            assert cr.isMalformed() || cr.isUnmappable();
            in.reset();
            out.reset();
            iso88591 = StandardCharsets.ISO_8859_1.newDecoder();
            return iso88591.decode(in, out, false);
        }
    }
}