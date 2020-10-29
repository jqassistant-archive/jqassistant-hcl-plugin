package org.jqassistant.contrib.plugin.hcl.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Identifies a data resource. This type of resource is read only.
 *
 * @author Matthias Kay
 * @since 1.0
 */
@Label("ProviderDataResource")
public interface TerraformProviderDataResource extends TerraformProviderResource {
}
