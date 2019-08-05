/*
 * Copyright 2019 lorislab.org.
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
package org.lorislab.maven.mp.rest.client.codegen;


import io.swagger.codegen.v3.CodegenOperation;
import io.swagger.codegen.v3.CodegenParameter;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The extends code generation for the operation.
 */
public class ExtCodegenOperation extends CodegenOperation {

    /**
     * The bean parameters list.
     */
    @Getter @Setter
    public List<CodegenParameter> beanParams;

    /**
     * The check of the beanParam.
     */
    @Getter @Setter
    public boolean beanParam;

    /**
     * The beanParam attribute name.
     */
    @Getter @Setter
    public String beanParamName;

    /**
     * The default constructor.
     * @param op the original code gen operation.
     */
    public ExtCodegenOperation(CodegenOperation op) {
        responseHeaders.addAll(op.responseHeaders);
        returnTypeIsPrimitive = op.returnTypeIsPrimitive;
        returnSimpleType = op.returnSimpleType;
        subresourceOperation = op.subresourceOperation;
        path = op.path;
        operationId = op.operationId;
        returnType = op.returnType;
        httpMethod = op.httpMethod;
        returnBaseType = op.returnBaseType;
        returnContainer = op.returnContainer;
        summary = op.summary;
        unescapedNotes = op.unescapedNotes;
        notes = op.notes;
        baseName = op.baseName;
        defaultResponse = op.defaultResponse;
        testPath = op.testPath;
        discriminator = op.discriminator;
        consumes = op.consumes;
        produces = op.produces;
        prioritizedContentTypes = op.prioritizedContentTypes;
        bodyParam = op.bodyParam;
        contents = op.contents;
        allParams = op.allParams;
        bodyParams = op.bodyParams;
        pathParams = op.pathParams;
        queryParams = op.queryParams;
        headerParams = op.headerParams;
        formParams = op.formParams;
        requiredParams = op.requiredParams;
        authMethods = op.authMethods;
        tags = op.tags;
        responses = op.responses;
        imports = op.imports;
        examples = op.examples;
        requestBodyExamples = op.requestBodyExamples;
        externalDocs = op.externalDocs;
        nickname = op.nickname;
        operationIdLowerCase = op.operationIdLowerCase;
        operationIdCamelCase = op.operationIdCamelCase;
        operationIdSnakeCase = op.operationIdSnakeCase;
        vendorExtensions = op.vendorExtensions;

        beanParams = new ArrayList<>();
        if (pathParams != null) {
            beanParams.addAll(pathParams);
        }
        if (queryParams != null) {
            beanParams.addAll(queryParams);
        }
        if (headerParams != null) {
            beanParams.addAll(headerParams);
        }
        if (formParams != null) {
            beanParams.addAll(formParams);
        }

    }

}
