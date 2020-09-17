package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.nio.file.Path;

import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformOutputVariable;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

import com.buschmais.jqassistant.core.store.api.Store;
import com.google.common.collect.ImmutableMap;

public class Module extends TerraformObject {
  private String name;
  private String source;

  public String getName() {
    return this.name;
  }

  public String getSource() {
    return this.source;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setSource(final String source) {
    this.source = source;
  }

  /**
   * onverts this object into a {@link TerraformOutputVariable} and puts it into
   * the store.
   *
   * @param storeHelper helper to access the {@link Store}
   * @param directory   path of the file this module is defined in
   */
  public TerraformModule toStore(final Path directory, final StoreHelper storeHelper) {
    final TerraformModule module = storeHelper
        .createOrRetrieveObject(ImmutableMap.of(TerraformLogicalModule.FieldName.FULL_QUALIFIED_NAME,
            directory.resolve(this.source).normalize().toString().replace('\\', '/')), TerraformModule.class);

    module.setName(this.name);
    module.setSource(this.source);

    final String fullQualifiedNameOfReferencedModule = directory.resolve(this.source).normalize().toString()
        .replace('\\', '/');

    final TerraformLogicalModule referencedModule = storeHelper.createOrRetrieveObject(
        ImmutableMap.of(TerraformLogicalModule.FieldName.FULL_QUALIFIED_NAME, fullQualifiedNameOfReferencedModule),
        TerraformLogicalModule.class);
    referencedModule.setFullQualifiedName(fullQualifiedNameOfReferencedModule);

    final TerraformLogicalModule referencedModule1 = storeHelper.createOrRetrieveObject(
        ImmutableMap.of(TerraformLogicalModule.FieldName.FULL_QUALIFIED_NAME, fullQualifiedNameOfReferencedModule),
        TerraformLogicalModule.class);

    module.setReference(referencedModule);

    return module;
  }
}
