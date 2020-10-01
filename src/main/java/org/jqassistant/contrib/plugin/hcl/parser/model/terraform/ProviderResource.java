package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformProviderResource;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

public class ProviderResource extends TerraformObject<TerraformProviderResource> {
  /**
   * Calculates the full qualified name for a module.
   *
   * @param parentFilePath the path name of the file this module is defined in
   * @param resourceName   the name of the resource
   * @param resourceType   the type of the resource
   * @return A name which can be used as ID
   */
  public static String calculateFullQualifiedName(final String resourceName, final String resourceType,
      final Path parentFilePath) {
    return getFullQualifiedNamePrefix(parentFilePath) + resourceType + "." + resourceName;
  }

  private String name;

  private final Map<String, String> properties = new HashMap<>();
  private String providerName;

  private String type;

  public String getName() {
    return this.name;
  }

  public String getType() {
    return this.type;
  }

  @Override
  protected TerraformProviderResource saveInternalState(final TerraformProviderResource object,
      final TerraformLogicalModule partOfModule, final Path filePath, final StoreHelper storeHelper) {
    object.setInternalName(this.name);
    object.setProvider(TerraformProviderResource.Provider.fromString(this.providerName));
    object.setType(this.type);

    storeHelper.addPropertiesToObject(object, this.properties);

    return object;
  }

  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Stores the value of a named property.
   *
   * @param name  name of the property
   * @param value value of the property
   */
  public void setProperty(final String name, final String value) {
    this.properties.put(name, value);
  }

  public void setProviderName(final String providerName) {
    this.providerName = providerName;
  }

  public void setType(final String type) {
    this.type = type;
  }
}
