package org.jqassistant.contrib.plugin.hcl.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Marks an output variable in a terraform file.
 * 
 * @author Matthias Kay
 * @since 1.0
 */
@Label("OutputVariable")
public interface TerraformOutputVariable extends TerraformBlock {
  String getName();

  void setName(String name);

  String getType();

  String getDescription();

  String getValue();

  String getSensitive();

  void setType(String type);

  void setDescription(String description);

  void setValue(String value);

  void setSensitive(String sesitive);
}
