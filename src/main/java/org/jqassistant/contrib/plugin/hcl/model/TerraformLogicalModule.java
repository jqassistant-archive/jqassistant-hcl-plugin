package org.jqassistant.contrib.plugin.hcl.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * A container to group all objects within one terraform module.
 *
 * @author Matthias Kay
 * @since 1.0
 */
@Label("LogicalModule")
public interface TerraformLogicalModule extends TerraformBlock {
  @Relation("CALLS")
  List<TerraformModule> getCalledModules();

  @Relation("CONFIGURES_TERRAFORM_WITH")
  List<TerraformConfiguration> getConfiguration();

  @Relation("DECLARES_INPUT_VARIABLE")
  List<TerraformInputVariable> getInputVariables();

  @Relation("DECLARES_OUTPUT_VARIABLE")
  List<TerraformOutputVariable> getOutputVariables();

  @Relation("CREATES_RESOURCES")
  List<TerraformProviderResource> getProviderResources();

  @Relation("CREATES_RESOURCES_VIA")
  List<TerraformProvider> getProviders();
}
