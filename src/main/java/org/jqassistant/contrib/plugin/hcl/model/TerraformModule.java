package org.jqassistant.contrib.plugin.hcl.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * A container to group all objects within one terraform module.
 *
 * @author Matthias Kay
 * @since 1.0
 */
@Label("Module")
public interface TerraformModule extends TerraformBlock {
  String getName();

  void setName(String name);
}
