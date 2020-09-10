package org.jqassistant.contrib.plugin.hcl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformOutputVariable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class TerraformScannerPluginOutputVariableIT extends AbstractPluginIT {
  private static final String FILE_ALL_TF = "/terraform/output variable/outputs.tf";

  @Test
  public void shouldReadAllAttributes_whenScan_givenOutputVariable() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginOutputVariableIT.class),
        FILE_ALL_TF);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_ALL_TF,
        DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();
    assertThat(actualDescriptor.getOutputVariables()).hasSize(1).first()
        .extracting(TerraformOutputVariable::getName, TerraformOutputVariable::getDescription,
            TerraformOutputVariable::getSensitive, TerraformOutputVariable::getValue)
        .containsExactly("db_password", "The password for logging in to the database.", "true",
            "aws_db_instance.db.password");
  }

  @BeforeEach
  public void startTransaction() {
    this.store.beginTransaction();
  }
}
