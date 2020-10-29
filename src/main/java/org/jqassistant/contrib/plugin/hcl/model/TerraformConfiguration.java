package org.jqassistant.contrib.plugin.hcl.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Encapsulate the terraform configuration.
 *
 * @author Matthias Kay
 * @since 1.0
 */
@Label("Configuration")
public interface TerraformConfiguration extends TerraformBlock {
  String getBackend();

  String getExperiments();

  String getProviderMeta();

  String getRequiredProviders();

  String getRequiredVersion();

  void setBackend(String backend);

  void setExperiments(String experiments);

  void setProviderMeta(String providerMeta);

  void setRequiredProviders(String requiredProviders);

  void setRequiredVersion(String requiredVersion);

}
