package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformOutputVariable;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

import com.buschmais.jqassistant.core.store.api.Store;
import com.google.common.collect.ImmutableMap;

public class Module extends TerraformObject {
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

  /**
   * onverts this object into a {@link TerraformOutputVariable} and puts it into
   * the store.
   *
   * @param storeHelper helper to access the {@link Store}
   * @param directory   path of the file this module is defined in
   *
   * @return the created {@link TerraformModule}
   */
  public TerraformModule toStore(final Path directory, final StoreHelper storeHelper) {
    final String fullQualifiedName = directory.toString() + "." + this.name;

    final TerraformModule module = storeHelper.createOrRetrieveObject(
        ImmutableMap.of(TerraformBlock.FieldName.FULL_QUALIFIED_NAME, fullQualifiedName), TerraformModule.class);

    final boolean isOnLocalFileSystem = this.source.startsWith("./") || this.source.startsWith("../");
    final String moduleSource = isOnLocalFileSystem
        ? directory.resolve(this.source).normalize().toString().replace('\\', '/')
        : this.source;

    module.setCount(this.count);
    module.setForEach(this.forEach);
    module.setFullQualifiedName(fullQualifiedName);
    module.setName(this.name);
    module.setProviders(this.providers);
    module.setSource(moduleSource);
    module.setVersion(this.version);

    this.dependantResources.forEach(dependentObjectName -> {
      final TerraformBlock block = storeHelper.createOrRetrieveObject(ImmutableMap
          .of(TerraformBlock.FieldName.FULL_QUALIFIED_NAME, directory.toString() + "." + dependentObjectName),
          TerraformBlock.class);
      block.setFullQualifiedName(dependentObjectName);

      module.getDependantResources().add(block);
    });

    final String fullQualifiedNameOfReferencedModule = isOnLocalFileSystem
        ? directory.resolve(moduleSource).normalize().toString().replace('\\', '/')
        : this.source;

    final TerraformLogicalModule referencedModule = storeHelper.createOrRetrieveObject(
        ImmutableMap.of(TerraformLogicalModule.FieldName.FULL_QUALIFIED_NAME, fullQualifiedNameOfReferencedModule),
        TerraformLogicalModule.class);
    referencedModule.setFullQualifiedName(fullQualifiedNameOfReferencedModule);

    module.setSourcedFrom(referencedModule);

    storeHelper.addPropertiesToObject(module, this.matchedInputVariables);

    return module;
  }
}
