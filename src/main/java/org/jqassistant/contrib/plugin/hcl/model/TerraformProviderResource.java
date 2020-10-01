package org.jqassistant.contrib.plugin.hcl.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Identifies a cloud resource.
 *
 * @author Matthias Kay
 * @since 1.0
 */
@Label("ProviderResource")
public interface TerraformProviderResource extends TerraformBlock {
  public String getProvider();

  public String getType();

  public void setProvider(String provider);

  public void setType(String type);
}
