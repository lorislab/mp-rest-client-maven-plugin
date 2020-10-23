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

import io.swagger.codegen.v3.*;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import io.swagger.codegen.v3.generators.java.AbstractJavaCodegen;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static io.swagger.codegen.v3.config.CodegenConfiguratorUtils.*;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The micro-profile rest client codegen mojo.
 */
@Mojo(name = "codegen", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class MicroProfileRestClientCodegenMojo extends AbstractMojo {

    @Parameter(name = "verbose", required = false, defaultValue = "false")
    private boolean verbose;

    /**
     * Location of the output directory.
     */
    @Parameter(name = "output", property = "mp.rest-client.output",
            defaultValue = "${project.build.directory}/generated-sources/mp-rest-client")
    private File output;

    /**
     * Location of the openAPI spec, as URL or file.
     */
    @Parameter(name = "inputSpec", required = true)
    private String inputSpec;

    /**
     * Git user ID, e.g. swagger-api.
     */
    @Parameter(name = "gitUserId", required = false)
    private String gitUserId;

    /**
     * Git repo ID, e.g. swagger-codegen.
     */
    @Parameter(name = "gitRepoId", required = false)
    private String gitRepoId;

    /**
     * Folder containing the template files.
     */
    @Parameter(name = "templateDirectory")
    private File templateDirectory;

    /**
     * Adds authorization headers when fetching the swagger definitions remotely. " Pass in a
     * URL-encoded string of name:header with a comma separating multiple values
     */
    @Parameter(name = "auth")
    private String auth;

    /**
     * Path to separate json configuration file.
     */
    @Parameter(name = "configurationFile", required = false)
    private String configurationFile;

    /**
     * Specifies if the existing files should be overwritten during the generation.
     */
    @Parameter(name = "skipOverwrite", required = false)
    private Boolean skipOverwrite;

    /**
     * Specifies if the existing files should be overwritten during the generation.
     */
    @Parameter(name = "removeOperationIdPrefix", required = false)
    private Boolean removeOperationIdPrefix;

    /**
     * The package to use for generated api objects/classes
     */
    @Parameter(name = "apiPackage")
    private String apiPackage;

    /**
     * The package to use for generated model objects/classes
     */
    @Parameter(name = "modelPackage")
    private String modelPackage;

    /**
     * The package to use for the generated invoker objects
     */
    @Parameter(name = "invokerPackage")
    private String invokerPackage;

    /**
     * groupId in generated pom.xml
     */
    @Parameter(name = "groupId")
    private String groupId;

    /**
     * artifactId in generated pom.xml
     */
    @Parameter(name = "artifactId")
    private String artifactId;

    /**
     * artifact version in generated pom.xml
     */
    @Parameter(name = "artifactVersion")
    private String artifactVersion;

    /**
     * Sets the library
     */
    @Parameter(name = "library", required = false)
    private String library;

    /**
     * Sets the prefix for model enums and classes
     */
    @Parameter(name = "modelNamePrefix", required = false)
    private String modelNamePrefix;

    /**
     * Sets the suffix for model enums and classes
     */
    @Parameter(name = "modelNameSuffix", required = false)
    private String modelNameSuffix;

    /**
     * Sets an optional ignoreFileOverride path
     */
    @Parameter(name = "ignoreFileOverride", required = false)
    private String ignoreFileOverride;

    /**
     * A map of language-specific parameters as passed with the -c option to the command line
     */
    @Parameter(name = "configOptions")
    private Map<?, ?> configOptions = new HashMap<>();

    /**
     * A map of types and the types they should be instantiated as
     */
    @Parameter(name = "instantiationTypes")
    private List<String> instantiationTypes;

    /**
     * A map of classes and the import that should be used for that class
     */
    @Parameter(name = "importMappings")
    private List<String> importMappings;

    /**
     * A map of swagger spec types and the generated code types to use for them
     */
    @Parameter(name = "typeMappings")
    private List<String> typeMappings;

    /**
     * A map of additional language specific primitive types
     */
    @Parameter(name = "languageSpecificPrimitives")
    private List<String> languageSpecificPrimitives;

    /**
     * A map of additional properties that can be referenced by the mustache templates
     */
    @Parameter(name = "additionalProperties")
    private List<String> additionalProperties;

    /**
     * A map of reserved names and how they should be escaped
     */
    @Parameter(name = "reservedWordsMappings")
    private List<String> reservedWordsMappings;

    /**
     * Generate the apis
     */
    @Parameter(name = "generateApis", required = false)
    private Boolean generateApis = true;

    /**
     * Generate the models
     */
    @Parameter(name = "generateModels", required = false)
    private Boolean generateModels = true;

    /**
     * A comma separated list of models to generate. All models is the default.
     */
    @Parameter(name = "modelsToGenerate", required = false)
    private String modelsToGenerate = "";

    /**
     * Generate the supporting files
     */
    @Parameter(name = "generateSupportingFiles", required = false)
    private Boolean generateSupportingFiles = false;

    /**
     * A comma separated list of models to generate. All models is the default.
     */
    @Parameter(name = "supportingFilesToGenerate", required = false)
    private String supportingFilesToGenerate = "";

    /**
     * Generate the model tests
     */
    @Parameter(name = "generateModelTests", required = false)
    private Boolean generateModelTests = true;

    /**
     * Generate the model documentation
     */
    @Parameter(name = "generateModelDocumentation", required = false)
    private Boolean generateModelDocumentation = true;

    /**
     * Generate the api tests
     */
    @Parameter(name = "generateApiTests", required = false)
    private Boolean generateApiTests = true;

    /**
     * Generate the api documentation
     */
    @Parameter(name = "generateApiDocumentation", required = false)
    private Boolean generateApiDocumentation = true;

    /**
     * Generate the api documentation
     */
    @Parameter(name = "withXml", required = false)
    private Boolean withXml = false;

    /**
     * Skip the execution.
     */
    @Parameter(name = "skip", property = "codegen.skip", required = false, defaultValue = "false")
    private Boolean skip;

    /**
     * Add the output directory to the project as a source root, so that the generated java types
     * are compiled and included in the project artifact.
     */
    @Parameter(defaultValue = "true")
    private boolean addCompileSourceRoot = true;

    @Parameter
    protected Map<String, String> environmentVariables = new HashMap<String, String>();

    @Parameter
    protected Map<String, String> originalEnvironmentVariables = new HashMap<String, String>();

    @Parameter
    private boolean configHelp = false;

    /**
     * The project being built.
     */
    @Parameter(readonly = true, required = true, defaultValue = "${project}")
    private MavenProject project;

    /// EXTENDS parameters

    /**
     * Formatter google.
     */
    @Parameter(name = "formatter", required = false, defaultValue = "true")
    private Boolean formatter;

    /**
     * Group REST method by tags.
     */
    @Parameter(name = "groupByTags", required = false, defaultValue = "false")
    private Boolean groupByTags;

    /**
     * Api name.
     */
    @Parameter(name = "apiName", required = false)
    private String apiName;

    /**
     * Interface only.
     */
    @Parameter(name = "interfaceOnly", required = false, defaultValue = "true")
    private Boolean interfaceOnly;

    /**
     * Group prefix.
     */
    @Parameter(name = "pathPrefix", required = false)
    private String pathPrefix;

    /**
     * Api suffix.
     */
    @Parameter(name = "apiSuffix", required = false, defaultValue = "RestClient")
    private String apiSuffix;

    /**
     * The list of annotations.
     */
    @Parameter(name = "annotations")
    private List<String> annotations;

    /**
     * The list of model annotations.
     */
    @Parameter(name = "modelAnnotations")
    private List<String> modelAnnotations;

    /**
     * Return response.
     */
    @Parameter(name = "returnResponse", required = false, defaultValue = "true")
    private Boolean returnResponse;

    /**
     * Bean param suffix.
     */
    @Parameter(name = "beanParamSuffix", required = false, defaultValue = "BeanParam")
    private String beanParamSuffix;

    /**
     * When to group parameters to the @BeanParam.
     */
    @Parameter(name = "beanParamCount", required = false, defaultValue = "9")
    private String beanParamCount;

    /**
     * The JSON library annotation. Default: JSONB
     */
    @Parameter(name = "jsonLib", required = false, defaultValue = "JSONB")
    private JsonLib jsonLib;

    /**
     * The pojo field generator.
     */
    @Parameter(name = "fieldGen", required = false, defaultValue = "PUBLIC")
    private FieldGenerator fieldGen;

    /**
     * The implementation type.
     */
    @Parameter(name = "implType", required = false, defaultValue = "CLASS")
    private ImplType implType;

    /**
     * Date library.
     */
    @Parameter(name = "dateLibrary", required = false, defaultValue = "java8")
    private String dateLibrary;

    /**
     * Use bean validation.
     */
    @Parameter(name = "useBeanValidation", required = false, defaultValue = "true")
    private Boolean useBeanValidation;

    /**
     * Use bean validation.
     */
    @Parameter(name = "apiInterfaceDoc", required = false, defaultValue = "true")
    private Boolean apiInterfaceDoc;

    /**
     * Implementation proxy.
     */
    @Parameter(name = "proxyClientClass", required = false, defaultValue = "org.tkit.changeme.proxyClientClass")
    private String proxyClientClass;

    @Override
    public void execute() throws MojoExecutionException {

        if (skip) {
            getLog().info("Code generation is skipped.");
            // Even when no new sources are generated, the existing ones should
            // still be compiled if needed.
            addCompileSourceRootIfConfigured();
            return;
        }

        // attempt to read from config file
        CodegenConfigurator configurator = CodegenConfigurator.fromFile(configurationFile);

        // if a config file wasn't specified or we were unable to read it
        if (configurator == null) {
            configurator = new CodegenConfigurator();
        }

        configurator.setVerbose(verbose);

        if (skipOverwrite != null) {
            configurator.setSkipOverwrite(skipOverwrite);
        }

        if (removeOperationIdPrefix != null) {
            configurator.setRemoveOperationIdPrefix(removeOperationIdPrefix);
        }

        if (isNotEmpty(inputSpec)) {
            configurator.setInputSpecURL(inputSpec);
        }

        if (isNotEmpty(gitUserId)) {
            configurator.setGitUserId(gitUserId);
        }

        if (isNotEmpty(gitRepoId)) {
            configurator.setGitRepoId(gitRepoId);
        }

        if (isNotEmpty(ignoreFileOverride)) {
            configurator.setIgnoreFileOverride(ignoreFileOverride);
        }

        configurator.setLang(MicroProfileRestClientCodegen.class.getName());

        configurator.setOutputDir(output.getAbsolutePath());

        if (isNotEmpty(auth)) {
            configurator.setAuth(auth);
        }

        if (isNotEmpty(apiPackage)) {
            configurator.setApiPackage(apiPackage);
        }

        if (isNotEmpty(modelPackage)) {
            configurator.setModelPackage(modelPackage);
        }

        if (isNotEmpty(invokerPackage)) {
            configurator.setInvokerPackage(invokerPackage);
        }

        if (isNotEmpty(groupId)) {
            configurator.setGroupId(groupId);
        }

        if (isNotEmpty(artifactId)) {
            configurator.setArtifactId(artifactId);
        }

        if (isNotEmpty(artifactVersion)) {
            configurator.setArtifactVersion(artifactVersion);
        }

        if (isNotEmpty(library)) {
            configurator.setLibrary(library);
        }

        if (isNotEmpty(modelNamePrefix)) {
            configurator.setModelNamePrefix(modelNamePrefix);
        }

        if (isNotEmpty(modelNameSuffix)) {
            configurator.setModelNameSuffix(modelNameSuffix);
        }

        if (null != templateDirectory) {
            configurator.setTemplateDir(templateDirectory.getAbsolutePath());
        }

        // Set generation options
        if (null != generateApis && generateApis) {
            System.setProperty(CodegenConstants.APIS, "");
        } else {
            System.clearProperty(CodegenConstants.APIS);
        }

        if (null != generateModels && generateModels) {
            System.setProperty(CodegenConstants.MODELS, modelsToGenerate);
        } else {
            System.clearProperty(CodegenConstants.MODELS);
        }

        if (null != generateSupportingFiles && generateSupportingFiles) {
            System.setProperty(CodegenConstants.SUPPORTING_FILES, supportingFilesToGenerate);
        } else {
            System.clearProperty(CodegenConstants.SUPPORTING_FILES);
        }

        System.setProperty(CodegenConstants.MODEL_TESTS, generateModelTests.toString());
        System.setProperty(CodegenConstants.MODEL_DOCS, generateModelDocumentation.toString());
        System.setProperty(CodegenConstants.API_TESTS, generateApiTests.toString());
        System.setProperty(CodegenConstants.API_DOCS, generateApiDocumentation.toString());
        System.setProperty(CodegenConstants.WITH_XML, withXml.toString());

        if (configOptions != null) {
            // Retained for backwards-compataibility with configOptions -> instantiation-types
            if (instantiationTypes == null && configOptions.containsKey("instantiation-types")) {
                applyInstantiationTypesKvp(configOptions.get("instantiation-types").toString(),
                        configurator);
            }

            // Retained for backwards-compataibility with configOptions -> import-mappings
            if (importMappings == null && configOptions.containsKey("import-mappings")) {
                applyImportMappingsKvp(configOptions.get("import-mappings").toString(),
                        configurator);
            }

            // Retained for backwards-compataibility with configOptions -> type-mappings
            if (typeMappings == null && configOptions.containsKey("type-mappings")) {
                applyTypeMappingsKvp(configOptions.get("type-mappings").toString(), configurator);
            }

            // Retained for backwards-compataibility with configOptions -> language-specific-primitives
            if (languageSpecificPrimitives == null && configOptions.containsKey("language-specific-primitives")) {
                applyLanguageSpecificPrimitivesCsv(configOptions
                        .get("language-specific-primitives").toString(), configurator);
            }

            // Retained for backwards-compataibility with configOptions -> additional-properties
            if (additionalProperties == null && configOptions.containsKey("additional-properties")) {
                applyAdditionalPropertiesKvp(configOptions.get("additional-properties").toString(),
                        configurator);
            }

            // Retained for backwards-compataibility with configOptions -> reserved-words-mappings
            if (reservedWordsMappings == null && configOptions.containsKey("reserved-words-mappings")) {
                applyReservedWordsMappingsKvp(configOptions.get("reserved-words-mappings")
                        .toString(), configurator);
            }
        }

        //Apply Instantiation Types
        if (instantiationTypes != null && (configOptions == null || !configOptions.containsKey("instantiation-types"))) {
            applyInstantiationTypesKvpList(instantiationTypes, configurator);
        }

        //Apply Import Mappings
        if (importMappings != null && (configOptions == null || !configOptions.containsKey("import-mappings"))) {
            applyImportMappingsKvpList(importMappings, configurator);
        }

        //Apply Type Mappings
        if (typeMappings != null && (configOptions == null || !configOptions.containsKey("type-mappings"))) {
            applyTypeMappingsKvpList(typeMappings, configurator);
        }

        //Apply Language Specific Primitives
        if (languageSpecificPrimitives != null && (configOptions == null || !configOptions.containsKey("language-specific-primitives"))) {
            applyLanguageSpecificPrimitivesCsvList(languageSpecificPrimitives, configurator);
        }

        //Apply Additional Properties
        if (additionalProperties != null && (configOptions == null || !configOptions.containsKey("additional-properties"))) {
            applyAdditionalPropertiesKvpList(additionalProperties, configurator);
        }

        //Apply Reserved Words Mappings
        if (reservedWordsMappings != null && (configOptions == null || !configOptions.containsKey("reserved-words-mappings"))) {
            applyReservedWordsMappingsKvpList(reservedWordsMappings, configurator);
        }

        if (environmentVariables != null) {

            for (String key : environmentVariables.keySet()) {
                originalEnvironmentVariables.put(key, System.getProperty(key));
                String value = environmentVariables.get(key);
                if (value == null) {
                    // don't put null values
                    value = "";
                }
                System.setProperty(key, value);
                configurator.addSystemProperty(key, value);
            }
        }

        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.FORMATTER, formatter);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.GROUP_BY_TAGS, groupByTags);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.API_NAME, apiName);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.INTERFACE_ONLY, interfaceOnly);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.BEAN_PARAM_SUFFIX, beanParamSuffix);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.BEAN_PARAM_COUNT, beanParamCount);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.PATH_PREFIX, pathPrefix);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.API_SUFFIX, apiSuffix);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.ANNOTATIONS, annotations);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.MODEL_ANNOTATIONS, modelAnnotations);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.RETURN_RESPONSE, returnResponse);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.JSON_LIB, jsonLib);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.FIELD_GEN, fieldGen);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.IMPL_TYPE, implType);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.PROXY_CLIENT_CLASS, proxyClientClass);
        configurator.addAdditionalProperty(AbstractJavaCodegen.DATE_LIBRARY, dateLibrary);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.USE_BEAN_VALIDATION, useBeanValidation);
        configurator.addAdditionalProperty(MicroProfileRestClientCodegen.API_INTERFACE_DOC, apiInterfaceDoc);


        final ClientOptInput input = configurator.toClientOptInput();
        final CodegenConfig config = input.getConfig();

        if (configOptions != null) {
            for (CliOption langCliOption : config.cliOptions()) {
                if (configOptions.containsKey(langCliOption.getOpt())) {
                    input.getConfig().additionalProperties().put(langCliOption.getOpt(), configOptions.get(langCliOption.getOpt()));
                }
            }
        }

        if (configHelp) {
            for (CliOption langCliOption : config.cliOptions()) {
                System.out.println("\t" + langCliOption.getOpt());
                System.out.println("\t    "
                        + langCliOption.getOptionHelp().replaceAll("\n", "\n\t    "));
                System.out.println();
            }
            return;
        }
        try {
            new DefaultGenerator().opts(input).generate();
        } catch (Exception e) {
            // Maven logs exceptions thrown by plugins only if invoked with -e
            // I find it annoying to jump through hoops to get basic diagnostic information,
            // so let's log it in any case:
            getLog().error(e);
            throw new MojoExecutionException(
                    "Code generation failed. See above for the full exception.");
        }

        addCompileSourceRootIfConfigured();
    }

    private void addCompileSourceRootIfConfigured() {
        if (addCompileSourceRoot) {
            final Object sourceFolderObject =
                    configOptions == null ? null : configOptions
                            .get(CodegenConstants.SOURCE_FOLDER);
            final String sourceFolder = sourceFolderObject == null ? "src/main/java" : sourceFolderObject.toString();
            String sourceJavaFolder = output.toString() + "/" + sourceFolder;
            project.addCompileSourceRoot(sourceJavaFolder);
        }

        // Reset all environment variables to their original value. This prevents unexpected
        // behaviour
        // when running the plugin multiple consecutive times with different configurations.
        for (Map.Entry<String, String> entry : originalEnvironmentVariables.entrySet()) {
            if (entry.getValue() == null) {
                System.clearProperty(entry.getKey());
            } else {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        }
    }

}
