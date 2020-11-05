package org.jqassistant.contrib.plugin.hcl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;
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
    assertThat(actualDescriptor.getModule().getOutputVariables()).hasSize(1).first()
        .extracting(TerraformOutputVariable::getName, TerraformOutputVariable::getDescription,
            TerraformOutputVariable::getSensitive, TerraformOutputVariable::getValue,
            TerraformOutputVariable::getInternalName)
        .containsExactly("db_password", "The password for logging in to the database.", "true",
            "aws_db_instance.db.password", "output variable.db_password");

    final List<TerraformBlock> actualDependantObjects = actualDescriptor.getModule().getOutputVariables().get(0)
        .getDependantObjects();

    assertThat(actualDependantObjects).hasSize(2).extracting(TerraformBlock::getFullQualifiedName)
        .containsExactlyInAnyOrder("aws_db_instance.db", "aws_db_instance.db_new");
  }

  @BeforeEach
  public void startTransaction() {
    this.store.beginTransaction();
  }
}
