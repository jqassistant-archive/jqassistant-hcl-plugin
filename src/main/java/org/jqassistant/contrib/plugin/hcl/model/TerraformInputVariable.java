package org.jqassistant.contrib.plugin.hcl.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Marks an input variable in a terraform file.
 *
 * @author Matthias Kay
 * @since 1.0
 */
@Label("InputVariable")
public interface TerraformInputVariable extends TerraformBlock {
  enum FieldName implements TerraformModelField {
    NAME("name");

    private final String modelName;

    private FieldName(final String modelName) {
      this.modelName = modelName;
    }

    @Override
    public String getModelName() {
      return this.modelName;
    }
  }

  String getDefault();

  String getDescription();

  String getName();

  String getType();

  String getValidationErrorMessage();

  String getValidationRule();

  void setDefault(String defaultValue);

  void setDescription(String description);

  void setName(String name);

  void setType(String type);

  void setValidationErrorMessage(String errorMessage);

  void setValidationRule(String rule);
}
