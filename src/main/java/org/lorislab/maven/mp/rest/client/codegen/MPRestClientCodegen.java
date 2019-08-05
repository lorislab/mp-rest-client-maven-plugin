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

import com.google.googlejavaformat.java.Formatter;
import io.swagger.codegen.v3.*;
import io.swagger.codegen.v3.generators.java.AbstractJavaJAXRSServerCodegen;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The rest client code generator.
 */
@Slf4j
public class MPRestClientCodegen extends AbstractJavaJAXRSServerCodegen {

    private static final String INTERFACE_ONLY = "interfaceOnly";

    private static final String JACKSON = "jackson";

    private static final String JSONB = "jsonb";

    private static final String LOMBOK_DATA = "lombokData";

    private static final String GENERATE_GETTER_SETTER = "generateGetterSetter";

    private static final String API_SUFFIX = "apiSuffix";

    private static final String API_NAME = "apiName";

    private static final String RETRUN_RESPONSE = "returnResponse";

    private static final String GENERATE_EQUALS = "generateEquals";

    private static final String GENERATE_TO_STRING = "generateToString";

    private static final String BEAN_PARAM_SUFFIX = "beanParamSuffix";

    private static final String BEAN_PARAM_COUNT = "beanParamCount";

    private static final String GENERATE_REST_CLIENT = "regenerateRestClient";

    private static final String REGISTER_PROVIDERS = "registerProviders";

    private static final String HAS_PROVIDERS = "hasProviders";

    private static final String REGISTER_ANNOTATIONS = "registerAnnotations";

    private static final String REGISTER_MODEL_ANNOTATIONS = "registerModelAnnotations";

    private static final String HAS_ANNOTATIONS = "hasAnnotations";

    private static final String HAS_MODEL_ANNOTATIONS = "hasModelAnnotations";

    private static final String FORMATTER_GOOGLE = "formatterGoogle";

    private static final String GROUP_PREFIX = "groupPrefix";

    @Getter
    private boolean interfaceOnly = true;

    @Getter
    private boolean jackson = false;

    @Getter
    private boolean jsonb = true;

    @Getter
    private boolean lombokData = true;

    @Getter
    private boolean generateGetterSetter = false;

    @Getter
    private boolean generateToString = false;

    @Getter
    private boolean generateEquals = false;

    @Getter
    private boolean returnResponse = true;

    @Getter
    private boolean generateRestClient = true;

    @Getter
    private String apiSuffix = "Api";

    @Getter
    private String beanParamSuffix = "BeanParam";

    @Getter
    private String apiName = null;

    @Getter
    private int beanParamCount = 2;

    @Getter
    private List<CodegenExtraAnnotation> registerProviders = new ArrayList<>();

    @Getter
    private List<CodegenExtraAnnotation> registerAnnotations = new ArrayList<>();

    @Getter
    private List<CodegenExtraAnnotation> registerModelAnnotations = new ArrayList<>();

    private List<String> outputFiles = new ArrayList<>();

    @Getter
    private boolean formatterGoogle = true;

    private String groupPrefix = null;

    public MPRestClientCodegen() {
        super();
        invokerPackage = "io.swagger.api";
        artifactId = "swagger-jaxrs-server";
        outputFolder = "generated-code/mp-restclient";
        apiPackage = "api";
        modelPackage = "model";

        additionalProperties.put("title", title);

        typeMapping.put("date", "LocalDate");

        importMapping.put("LocalDate", "org.joda.time.LocalDate");

        for (int i = 0; i < cliOptions.size(); i++) {
            if (CodegenConstants.LIBRARY.equals(cliOptions.get(i).getOpt())) {
                cliOptions.remove(i);
                break;
            }
        }

        additionalProperties.put(REGISTER_PROVIDERS, null);
        additionalProperties.put(GENERATE_REST_CLIENT, generateRestClient);
        additionalProperties.put(BEAN_PARAM_SUFFIX, beanParamSuffix);
        additionalProperties.put(LOMBOK_DATA, lombokData);
        additionalProperties.put(JSONB, jsonb);
        additionalProperties.put(JACKSON, jackson);
        additionalProperties.put(GENERATE_GETTER_SETTER, generateGetterSetter);
        additionalProperties.put(API_SUFFIX, apiSuffix);
        additionalProperties.put(RETRUN_RESPONSE, returnResponse);

        CliOption library = new CliOption(CodegenConstants.LIBRARY, "library template (sub-template) to use");
        library.setDefault(DEFAULT_LIBRARY);

        Map<String, String> supportedLibraries = new LinkedHashMap<>();
        supportedLibraries.put(DEFAULT_LIBRARY, "JAXRS");
        library.setEnum(supportedLibraries);
        cliOptions.add(library);


        cliOptions.add(CliOption.newString(GROUP_PREFIX, "Group path prefix to group operation to interface. For example groupPrefix=v1/ v1/process to process interface").defaultValue(groupPrefix));
        cliOptions.add(CliOption.newBoolean(FORMATTER_GOOGLE, "Google formatter source code.").defaultValue(String.valueOf(formatterGoogle)));
        cliOptions.add(CliOption.newString(REGISTER_PROVIDERS, "List of register providers @RegisterProvider('class_name') annotation on the interface. (Separator #)").defaultValue(null));
        cliOptions.add(CliOption.newBoolean(GENERATE_REST_CLIENT, "Generate the @RegisterRestClient annotation on the interface.").defaultValue(String.valueOf(generateRestClient)));
        cliOptions.add(CliOption.newBoolean(RETRUN_RESPONSE, "Return jaxrs response.").defaultValue(String.valueOf(returnResponse)));
        cliOptions.add(CliOption.newBoolean(INTERFACE_ONLY, "Interface only.").defaultValue(String.valueOf(interfaceOnly)));
        cliOptions.add(CliOption.newBoolean(JACKSON, "Use the jackson annotation.").defaultValue(String.valueOf(jackson)));
        cliOptions.add(CliOption.newBoolean(JSONB, "Use the jsonb property annotation for pojo.").defaultValue(String.valueOf(jsonb)));
        cliOptions.add(CliOption.newBoolean(LOMBOK_DATA, "Use the lombok @Data annotation for pojo.").defaultValue(String.valueOf(lombokData)));
        cliOptions.add(CliOption.newBoolean(GENERATE_GETTER_SETTER, "Generate getter and setter for pojo.").defaultValue(String.valueOf(generateGetterSetter)));
        cliOptions.add(CliOption.newBoolean(GENERATE_TO_STRING, "Generate toString method for pojo.").defaultValue(String.valueOf(generateToString)));
        cliOptions.add(CliOption.newBoolean(GENERATE_EQUALS, "Generate equals/hash method for pojo.").defaultValue(String.valueOf(generateEquals)));
        cliOptions.add(CliOption.newString(API_SUFFIX, "Name of the api client class suffic.").defaultValue(apiSuffix));
        cliOptions.add(CliOption.newString(API_NAME, "Name of the api client class.").defaultValue(apiName));
        cliOptions.add(CliOption.newString(BEAN_PARAM_SUFFIX, "The bean parameter suffix.").defaultValue(beanParamSuffix));
        cliOptions.add(CliOption.newString(BEAN_PARAM_COUNT, "Generate the bean for more than {beanParamCount} parameters. Disable generator -1").defaultValue(String.valueOf(beanParamCount)));
    }

    @Override
    public boolean shouldOverwrite(String filename) {
        outputFiles.add(filename);
        return super.shouldOverwrite(filename);
    }

    @Override
    public void processOpenAPI(OpenAPI openAPI) {
        if (formatterGoogle) {
            log.info("Google formatter source code");
            if (!outputFiles.isEmpty()) {
                Formatter formatter = new Formatter();
                for (String file : outputFiles) {
                    try {
                        log.info("Formatter source code: {}", file);
                        Path path = Paths.get(file);
                        String sourceString = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                        sourceString = formatter.formatSource(sourceString);
                        Files.write(path, sourceString.getBytes(StandardCharsets.UTF_8));
                    } catch (Exception ex) {
                        log.error("Skip format source code of the file {} ", file);
                        log.error("Error by formatting the source code", ex);
                    }
                }
            }
        }
        super.processOpenAPI(openAPI);
    }

    private List<CodegenExtraAnnotation> updatePropertyList(String name, List<CodegenExtraAnnotation> property) {
        if (additionalProperties.containsKey(name)) {
            List<String> items = (List<String>) additionalProperties.get(name);
            if (items != null) {
                for (String item : items) {
                    int index = item.lastIndexOf('.');
                    CodegenExtraAnnotation p = new CodegenExtraAnnotation();
                    String tmp = item;
                    int index2 = item.lastIndexOf('(');
                    if (index2 > -1) {
                        tmp = tmp.substring(0, index2);
                    }
                    p.setImports(tmp);
                    p.setName(item.substring(index + 1));
                    property.add(p);
                }
                additionalProperties.put(name, property);
            }
        }
        return property;
    }

    @Override
    public void processOpts() {
        super.processOpts();

        writePropertyBack(USE_BEANVALIDATION, useBeanValidation);

        registerProviders = updatePropertyList(REGISTER_PROVIDERS, registerProviders);
        additionalProperties.put(HAS_PROVIDERS, !registerProviders.isEmpty());

        registerAnnotations = updatePropertyList(REGISTER_ANNOTATIONS, registerAnnotations);
        additionalProperties.put(HAS_ANNOTATIONS, !registerAnnotations.isEmpty());

        registerModelAnnotations = updatePropertyList(REGISTER_MODEL_ANNOTATIONS, registerModelAnnotations);
        additionalProperties.put(HAS_MODEL_ANNOTATIONS, !registerModelAnnotations.isEmpty());

        if (additionalProperties.containsKey(API_SUFFIX)) {
            apiSuffix = (String) additionalProperties.get(API_SUFFIX);
        }

        if (additionalProperties.containsKey(GROUP_PREFIX)) {
            groupPrefix = (String) additionalProperties.get(GROUP_PREFIX);
        }

        if (additionalProperties.containsKey(API_NAME)) {
            apiName = (String) additionalProperties.get(API_NAME);
        }

        if (additionalProperties.containsKey(BEAN_PARAM_SUFFIX)) {
            beanParamSuffix = (String) additionalProperties.get(BEAN_PARAM_SUFFIX);
        }

        if (additionalProperties.containsKey(BEAN_PARAM_COUNT)) {
            beanParamCount = Integer.parseInt(additionalProperties.get(BEAN_PARAM_COUNT).toString());
            additionalProperties.put(BEAN_PARAM_COUNT, beanParamCount);
        }

        formatterGoogle = updateBoolean(FORMATTER_GOOGLE, formatterGoogle);
        generateRestClient = updateBoolean(GENERATE_REST_CLIENT, generateRestClient);
        generateEquals = updateBoolean(GENERATE_EQUALS, generateEquals);
        generateToString = updateBoolean(GENERATE_TO_STRING, generateToString);
        generateGetterSetter = updateBoolean(GENERATE_GETTER_SETTER, generateGetterSetter);
        lombokData = updateBoolean(LOMBOK_DATA, lombokData);
        jackson = updateBoolean(JACKSON, jackson);
        jsonb = updateBoolean(JSONB, jsonb);
        returnResponse = updateBoolean(RETRUN_RESPONSE, returnResponse);
        interfaceOnly = updateBoolean(INTERFACE_ONLY, interfaceOnly);

        if (StringUtils.isBlank(templateDir)) {
            embeddedTemplateDir = templateDir = getTemplateDir();
        }

        modelTemplateFiles.put("model.mustache", ".java");
        apiTemplateFiles.put("api.mustache", ".java");

        apiTestTemplateFiles.clear();
        modelTestTemplateFiles.clear();

        modelDocTemplateFiles.remove("model_doc.mustache");
        apiDocTemplateFiles.remove("api_doc.mustache");
        supportingFiles.clear();
    }

    private boolean updateBoolean(String name, boolean value) {
        boolean result = value;
        if (additionalProperties.containsKey(name)) {
            result = convertPropertyToBoolean(name);
            writePropertyBack(name, result);
        }
        return result;
    }

    @Override
    public String getArgumentsLocation() {
        return "";
    }

    @Override
    public String getDefaultTemplateDir() {
        return "mp-rest-client";
    }

    @Override
    public String getName() {
        return "mp-rest-client";
    }

    @Override
    public String toApiName(String name) {
        if (apiName != null) {
            return apiName;
        }
        if (groupPrefix != null) {
            name = name.replaceFirst(groupPrefix, "");
        }
        String computed = name;
        if (computed.length() == 0) {
            return "Default" + apiSuffix;
        }
        computed = sanitizeName(computed);
        return camelize(computed) + apiSuffix;
    }

    @Override
    public void addOperationToGroup(String tag, String resourcePath, Operation operation, CodegenOperation co, Map<String, List<CodegenOperation>> operations) {
        String basePath = resourcePath;
        if (groupPrefix != null) {
            basePath = basePath.replaceFirst(groupPrefix, "");
        }
        if (basePath.startsWith("/")) {
            basePath = basePath.substring(1);
        }
        int pos = basePath.indexOf('/');
        if (pos > 0) {
            basePath = basePath.substring(0, pos);
        }

        if (basePath.equals("")) {
            basePath = "default";
        } else {
            if (co.path.startsWith("/" + basePath)) {
                co.path = co.path.substring(("/" + basePath).length());
            }
            co.subresourceOperation = !co.path.isEmpty();
        }
        if (groupPrefix != null) {
            co.baseName = groupPrefix + basePath;
            if (co.path.startsWith("/")) {
                co.path = co.path.substring(1);
            }
            co.path = co.path.replaceFirst(co.baseName, "");
        } else {
            co.baseName = basePath;
        }
        List<CodegenOperation> opList = operations.computeIfAbsent(co.baseName, v -> new ArrayList<>());
        opList.add(co);
    }

    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        super.postProcessModelProperty(model, property);
        model.imports.remove("Schema");
        model.imports.remove("JsonSerialize");
        model.imports.remove("ToStringSerializer");
        model.imports.remove("JsonValue");
        model.imports.remove("JsonProperty");
        model.imports.remove("Data");
        model.imports.remove("ToString");
    }


    @Override
    public String getHelp() {
        return "Microprofile rest client generator according to JAXRS 2.0 specification.";
    }

    @Override
    public CodegenOperation fromOperation(String path, String httpMethod, Operation operation, Map<String, Schema> schemas, OpenAPI openAPI) {
        CodegenOperation op = super.fromOperation(path, httpMethod, operation, schemas, openAPI);
        ExtCodegenOperation e = new ExtCodegenOperation(op);
        e.setBeanParamName(camelize(op.operationId) + beanParamSuffix);
        if (e.beanParams.size() >= beanParamCount) {
            e.setBeanParam(true);
        }
        return e;
    }

}

