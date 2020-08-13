package org.jqassistant.contrib.plugin.hcl.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Defines the label which is shared by all nodes representing the terraform
 * structure.
 * 
 * @author Matthias Kay
 * @since 1.0
 */
@Label("Terraform")
public interface TerraformDescriptor extends Descriptor {
}
