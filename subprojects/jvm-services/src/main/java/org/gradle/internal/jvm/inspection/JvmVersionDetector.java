/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.jvm.inspection;

import org.gradle.api.JavaVersion;
import org.gradle.internal.jvm.JavaInfo;
import org.gradle.internal.scan.UsedByScanPlugin;

/**
 * Probes a JVM installation to determine the Java version it provides.
 */
@UsedByScanPlugin("test-distribution")
// TODO: deprecate and/or delegate to JvmMetdataDetector
public interface JvmVersionDetector {
    /**
     * Probes the Java version for the given JVM installation.
     */
    JavaVersion getJavaVersion(JavaInfo jvm);

    /**
     * Probes the Java version for the given `java` command.
     */
    @UsedByScanPlugin("test-distribution")
    JavaVersion getJavaVersion(String javaCommand);
}
