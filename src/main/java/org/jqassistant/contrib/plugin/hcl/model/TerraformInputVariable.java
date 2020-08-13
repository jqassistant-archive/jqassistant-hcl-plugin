package org.jqassistant.contrib.plugin.hcl.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/**
 * Marks an input variable in a terraform file.
 * 
 * @author Matthias Kay
 * @since 1.0
 */
@Label("InputVariable")
public interface TerraformInputVariable extends TerraformBlock {
  String getName();

  void setName(String name);

  String getDefault();

  void setDefault(String defaultValue);

  void setType(String type);

  String getType();

  String getDescription();

  void setDescription(String description);

  @Relation("DEPENDS_ON")
  List<TerraformBlock> getDependencies();
}
