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
  enum FieldName implements TerraformModelField {
    FULL_QUALIFIED_NAME("fullQualifiedName");

    private final String modelName;

    private FieldName(final String modelName) {
      this.modelName = modelName;
    }

    @Override
    public String getModelName() {
      return this.modelName;
    }
  }

  @Relation("CALLS")
  List<TerraformModule> getCalledModules();

  @Relation("DECLARES_INPUT_VARIABLE")
  List<TerraformInputVariable> getInputVariables();

  @Relation("DECLARES_OUTPUT_VARIABLE")
  List<TerraformOutputVariable> getOutputVariables();

  @Relation("CREATES_RESOURCES")
  List<TerraformProviderResource> getProviderResources();

  @Relation("CREATES_RESOURCES_VIA")
  List<TerraformProvider> getProviders();
}
