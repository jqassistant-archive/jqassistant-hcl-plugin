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
  String getDefault();

  String getDescription();

  String getName();

  String getType();

  String getValidationRule();

  String getValidationErrorMessage();

  void setDefault(String defaultValue);

  void setDescription(String description);

  void setName(String name);

  void setType(String type);

  void setValidationRule(String rule);

  void setValidationErrorMessage(String errorMessage);
}
