/*
 * Copyright 2020 lorislab.org.
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
package org.lorislab.maven.mp.restclient;

/**
 * Rest endpoints implementation type.
 */
public enum ImplType {

    /**
     * The class implementation. Each method response HTTP 501.
     */
    CLASS,

    /**
     * The interface implementation with {@code default} method implementation.
     * Each method response HTTP 501.
     */
    INTERFACE,

    /**
     * The proxy class implementation. Each method call rest client method.
     */
    PROXY;
}
