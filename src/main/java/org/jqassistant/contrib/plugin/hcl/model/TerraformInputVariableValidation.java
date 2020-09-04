package org.jqassistant.contrib.plugin.hcl.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Marks an input variable validation block in a terraform file.
 *
 * @author Matthias Kay
 * @since 1.0
 */
@Label("InputVariableValidation")
public interface TerraformInputVariableValidation extends TerraformBlock {
	String getErrorMessage();

	String getRule();

	void setErrorMessage(String errorMessage);

	void setRule(String rule);
}
