package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;
import org.jqassistant.contrib.plugin.hcl.model.TerraformDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformModule;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

import com.google.common.collect.ImmutableMap;

public class Module extends TerraformObject<TerraformModule> {
  /**
   * Calculates the full qualified name for a module.
   *
   * @param parentFilePath the path name of the file this module is defined in
   * @param sourcePath     path where the source code of the module is located
   * @param moduleName     the name of the module
   * @return A name which can be used as ID
   */
  public static String calculateFullQualifiedName(final String sourcePath, final String moduleName,
      final Path parentFilePath) {
    return getFullQualifiedNamePrefix(parentFilePath) + moduleName;
  }

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

  /**
   * @return <code>true</code> if the <code>source</code> references a file on the
   *         local file system. <code>false</code> otherwise.
   */
  private boolean isSourceOnLocalFileSystem() {
    return this.source != null && (this.source.startsWith("./") || this.source.startsWith("../"));
  }

  @Override
  protected TerraformModule saveInternalState(final TerraformModule object, final TerraformLogicalModule partOfModule,
      final Path filePath, final StoreHelper storeHelper) {
    final String moduleSource = isOnLocalFileSystem
        ? filePath.resolve(this.source).normalize().toString().replace('\\', '/')
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

    // create the logical module for modules present in the local file system only
    if (isSourceOnLocalFileSystem()) {
      final String fullQualifiedNameOfReferencedModule = LogicalModule
          .calculateFullQualifiedName(Paths.get(moduleSource));

      final TerraformLogicalModule referencedModule = storeHelper.createOrRetrieveObject(
          ImmutableMap.of(TerraformDescriptor.FieldName.FULL_QUALIFIED_NAME, fullQualifiedNameOfReferencedModule), null,
          TerraformLogicalModule.class);
      referencedModule.setFullQualifiedName(fullQualifiedNameOfReferencedModule);

      object.setSourcedFrom(referencedModule);
    }

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
