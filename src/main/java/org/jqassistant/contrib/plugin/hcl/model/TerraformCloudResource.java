package org.jqassistant.contrib.plugin.hcl.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Identifies a block in the terraform file.
 *
 * @author Matthias Kay
 * @since 1.0
 */
@Label("Block")
public interface TerraformCloudResource extends TerraformDescriptor {
  enum Provider {
    AWS("aws");

    private final String id;

    private Provider(final String id) {
      this.id = id;
    }
  }

  public Provider getProvider();

  public void setProvider(Provider provider);
}
