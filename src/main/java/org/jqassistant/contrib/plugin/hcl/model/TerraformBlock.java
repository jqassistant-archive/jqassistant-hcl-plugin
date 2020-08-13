package org.jqassistant.contrib.plugin.hcl.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Identifies a block in the terraform file.
 * 
 * @author Matthias Kay
 * @since 1.0
 */
@Label("Block")
public interface TerraformBlock extends TerraformDescriptor {

}
