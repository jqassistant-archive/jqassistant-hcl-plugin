package org.jqassistant.contrib.plugin.hcl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformInputVariable;
import org.jqassistant.contrib.plugin.hcl.model.TerraformInputVariableValidation;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class TerraformScannerPluginVariableIT extends AbstractPluginIT {
	private static final String FILE_ALL_TF = "/terraform/input variable/all.tf";

	@Test
	public void shouldReadAllAttributes_whenScan_givenInputVariable() {
		// given
		final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginVariableIT.class),
				FILE_ALL_TF);

		// when
		final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_ALL_TF,
				DefaultScope.NONE);

		// then
		assertThat(actualDescriptor.isValid()).isTrue();
		assertThat(actualDescriptor.getInputVariables()).hasSize(1).first()
				.extracting(TerraformInputVariable::getName, TerraformInputVariable::getDefault,
						TerraformInputVariable::getType, TerraformInputVariable::getDescription)
				.containsExactly("all", "xyz", "string", "all description");

		final TerraformInputVariableValidation actualValidationRule = actualDescriptor.getInputVariables().get(0)
				.getValidationConstraint();

		assertThat(actualValidationRule)
				.extracting(TerraformInputVariableValidation::getRule,
						TerraformInputVariableValidation::getErrorMessage)
				.containsExactly("length(var.all) = 7", "error");
	}
}
