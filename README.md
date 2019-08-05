# mp-rest-client-maven-plugin

Microprofile rest client maven plugin


[![pipeline status](https://gitlab.com/lorislab/maven/mp-rest-client-codegen/badges/master/pipeline.svg)](https://gitlab.com/lorislab/maven/mp-rest-client-codegen/commits/master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.lorislab.swagger/mp-rest-client-codegen/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.lorislab.swagger/mp-rest-client-codegen)

## Configuration parameters

| Parameter | Default value | Description |
|-----------|---------------|-------------|
| registerProviders | List of register providers @RegisterProvider('class_name') annotation on the interface. (Separator #) | null |
| registerAnnotations | List of extra annotations @Name annotation on the interface.| null |
| registerModelAnnotations | List of extra model annotations @Name annotation on the interface. | null |
| generateRestClient | Generate the @RegisterRestClient annotation on the interface. | true |
| returnResponse | Return jaxrs response | true |
| interfaceOnly | Interface only. | true |
| jackson | Use the jackson annotation. | false |
| jsonb | Use the jsonb property annotation for pojo. | true |
| lombokData | Use the lombok @Data annotation for pojo. | true |
| generateGetterSetter | Generate getter and setter for pojo. | false |
| generateToString | Generate toString method for pojo. | false |
| generateEquals | Generate equals/hash method for pojo. | false |
| apiSuffix | Name of the api client class suffic. | Api |
| apiName | Name of the api client class. | null |
| beanParamSuffix | The bean parameter suffix. | BeanParam |
| beanParamCount | Generate the bean for more than {beanParamCount} parameters. Disable generator -1") | 2 |
| formatterGoogle | Google formatter source code. | true |
| groupPrefix | Group path prefix to group operation to interface. For example groupPrefix=v1/ v1/process to process interface | null |

# Example

```xml
 <plugin>
    <groupId>org.lorislab.maven</groupId>
    <artifactId>mp-rest-client-maven-plugin</artifactId>
    <version>999-SNAPSHOT</version>
    <executions>
        <execution>
            <id>p6-gateway</id>
            <goals>
                <goal>codegen</goal>
            </goals>
            <configuration>
                <inputSpec>src/main/resources/clients/p6-gateway/openapi.yaml</inputSpec>
                <output>${project.build.directory}/generated-sources/mprestclient</output>
                <generateSupportingFiles>false</generateSupportingFiles>
                <apiPackage>org.lorislab.p6.example.client.gateway</apiPackage>
                <modelPackage>org.lorislab.p6.example.client.gateway.model</modelPackage>
                <groupPrefix>v1/</groupPrefix>
                <registerProviders>
                    <registerProvider>org.lorislab.quarkus.jel.log.interceptor.RestClientLogInterceptor</registerProvider>
                </registerProviders>
                <registerAnnotations>
                    <registerAnnotation>org.lorislab.quarkus.jel.log.interceptor.LoggerService</registerAnnotation>
                </registerAnnotations>
                <registerModelAnnotations>
                    <registerModelAnnotation>lombok.ToString(callSuper = true)</registerModelAnnotation>
                    <registerModelAnnotation>io.quarkus.runtime.annotations.RegisterForReflection</registerModelAnnotation>
                </registerModelAnnotations>
                <configOptions>
                    <sourceFolder>p6-gateway</sourceFolder>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```
Generate source code

```java

package api;

import model.Error;
import model.Pets;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.lorislab.quarkus.jel.log.interceptor.RestClientLogInterceptor;

import org.lorislab.quarkus.jel.log.interceptor.LoggerService;

import lombok.Data;

import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

@RegisterProvider(RestClientLogInterceptor.class)
@Path("/pets")
@RegisterRestClient
@LoggerService
@javax.annotation.Generated(
    value = "org.lorislab.swagger.mp.RestClientCodegen",
    date = "2019-06-13T08:26:44.867834+02:00[Europe/Berlin]")
public interface TestTestClient {

  @POST
  Response createPets(@BeanParam CreatePetsBeanParam beanParam, @Valid String body);

  @Data
  public class CreatePetsBeanParam {

    @QueryParam("limit")
    Integer limit;

    @QueryParam("limit2")
    Integer limit2;

    @HeaderParam("limit3")
    Integer limit3;

    @PathParam("petId2")
    String petId2;

    @PathParam("petId")
    String petId;
  }

  @GET
  Response listPets(@QueryParam("limit") Integer limit);

  @GET
  @Path("/{petId}")
  Response showPetById(@PathParam("petId") String petId);
}

```
