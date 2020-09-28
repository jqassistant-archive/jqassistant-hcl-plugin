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
  enum Provider {
    AWS("aws");

    public static Provider fromString(final String s) {
      for (final Provider p : Provider.values()) {
        if (p.id.equals(s)) {
          return p;
        }
      }

      throw new IllegalArgumentException(String.format("Provider not found: %s", s));
    }

    private final String id;

    private Provider(final String id) {
      this.id = id;
    }
  }

  public Provider getProvider();

  public String getType();

  public void setProvider(Provider provider);

  public void setType(String type);
}
