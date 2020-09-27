package org.jqassistant.contrib.plugin.hcl.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Marks a provider which understands the API of the provided platform and
 * exposes resources.
 *
 * @author Matthias Kay
 * @since 1.0
 */
@Label("Provider")
public interface TerraformProvider extends TerraformBlock {
}
