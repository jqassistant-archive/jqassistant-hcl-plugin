package org.jqassistant.contrib.plugin.hcl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformInputVariable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class TerraformScannerPluginVariableIT extends AbstractPluginIT {
  private static final String FILE_ALL_TF = "/terraform/input variable/variables.tf";

  @Test
  public void shouldReadAllAttributes_whenScan_givenInputVariable() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginVariableIT.class), FILE_ALL_TF);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_ALL_TF,
        DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();
    assertThat(actualDescriptor.getInputVariables()).hasSize(1).first()
        .extracting(TerraformInputVariable::getName, TerraformInputVariable::getDefault,
            TerraformInputVariable::getType, TerraformInputVariable::getDescription,
            TerraformInputVariable::getValidationErrorMessage, TerraformInputVariable::getValidationRule)
        .containsExactly("all", "xyz", "string", "all description", "This is an error.", "length(var.all)");
  }

  @BeforeEach
  public void startTransaction() {
    this.store.beginTransaction();
  }
}
