package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.nio.file.Path;

import org.jqassistant.contrib.plugin.hcl.model.TerraformConfiguration;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

public class Configuration extends TerraformObject<TerraformConfiguration> {
  /**
   * Calculates the full qualified name for the terraform block.
   *
   * @param parentFilePath the path name of the file this module is defined in
   * @return A name which can be used as ID
   */
  public static String calculateFullQualifiedName(final Path parentFilePath) {
    return getFullQualifiedNamePrefix(parentFilePath) + "terraform";

  }

  private String backend;

  private String experiments;

  private String providerMeta;

  private String requiredProviders;

  private String requiredVersion;

  @Override
  protected TerraformConfiguration saveInternalState(final TerraformConfiguration object,
      final TerraformLogicalModule partOfModule, final Path filePath, final StoreHelper storeHelper) {
    object.setBackend(this.backend);
    object.setExperiments(this.experiments);
    object.setInternalName("terraform");
    object.setFullQualifiedName(partOfModule.getFullQualifiedName() + "." + "terraform");
    object.setProviderMeta(this.providerMeta);
    object.setRequiredProviders(this.requiredProviders);
    object.setRequiredVersion(this.requiredVersion);

    return object;
  }

  public void setBackend(final String backend) {
    this.backend = backend;
  }

  public void setExperiments(final String experiments) {
    this.experiments = experiments;
  }

  public void setProviderMeta(final String providerMeta) {
    this.providerMeta = providerMeta;
  }

  public void setRequiredProviders(final String requiredProviders) {
    this.requiredProviders = requiredProviders;
  }

  public void setRequiredVersion(final String requiredVersion) {
    this.requiredVersion = requiredVersion;
  }

}
