package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformModule;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

import com.google.common.collect.ImmutableMap;

public class Module extends TerraformObject<TerraformModule> {
  private String count;
  private final List<String> dependantResources = new ArrayList<>();
  private String forEach;
  private final Map<String, String> matchedInputVariables = new HashMap<>();
  private String name;
  private String providers;
  private String source;
  private String version;

  public void addDependantResource(final String dependantResource) {
    this.dependantResources.add(dependantResource);
  }

  public void addInputVariableMapping(final String inputVariableName, final String mappedValue) {
    this.matchedInputVariables.put(inputVariableName, mappedValue);
  }

  public String getName() {
    return this.name;
  }

  public String getSource() {
    return this.source;
  }

  @Override
  protected TerraformModule saveInternalState(final TerraformModule object, final TerraformLogicalModule partOfModule,
      final StoreHelper storeHelper) {
    final boolean isOnLocalFileSystem = this.source.startsWith("./") || this.source.startsWith("../");
    final String moduleSource = isOnLocalFileSystem ? Paths.get(this.source).normalize().toString().replace('\\', '/')
        : this.source;

    object.setCount(this.count);
    object.setForEach(this.forEach);
    object.setInternalName(this.name);
    object.setProviders(this.providers);
    object.setSource(moduleSource);
    object.setVersion(this.version);

    this.dependantResources.forEach(dependentObjectName -> {
      final TerraformBlock block = storeHelper.createOrRetrieveObject(
          ImmutableMap.of(TerraformBlock.FieldName.FULL_QUALIFIED_NAME, dependentObjectName), partOfModule,
          TerraformBlock.class);
      block.setFullQualifiedName(dependentObjectName);

      object.getDependantResources().add(block);
    });

    final String fullQualifiedNameOfReferencedModule = isOnLocalFileSystem
        ? Paths.get(moduleSource).normalize().toString().replace('\\', '/')
        : this.source;

    final TerraformLogicalModule referencedModule = storeHelper.createOrRetrieveObject(
        ImmutableMap.of(TerraformLogicalModule.FieldName.FULL_QUALIFIED_NAME, fullQualifiedNameOfReferencedModule),
        partOfModule, TerraformLogicalModule.class);
    referencedModule.setFullQualifiedName(fullQualifiedNameOfReferencedModule);

    object.setSourcedFrom(referencedModule);

    storeHelper.addPropertiesToObject(object, this.matchedInputVariables);

    return object;
  }

  public void setCount(final String count) {
    this.count = count;
  }

  public void setForEach(final String forEach) {
    this.forEach = forEach;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setProviders(final String providers) {
    this.providers = providers;
  }

  public void setSource(final String source) {
    this.source = source;
  }

  public void setVersion(final String version) {
    this.version = version;
  }
}
