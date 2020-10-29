package org.jqassistant.contrib.plugin.hcl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLocalVariable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class TerraformScannerPluginLocalVariableIT extends AbstractPluginIT {
  private static final String FILE_ALL_TF = "/terraform/local variable/main.tf";

  @Test
  public void shouldReadAllAttributes_whenScan_givenOutputVariable() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginLocalVariableIT.class),
        FILE_ALL_TF);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_ALL_TF,
        DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();
    assertThat(actualDescriptor.getModule().getLocalVariables()).hasSize(1).first()
        .extracting(TerraformLocalVariable::getName, TerraformLocalVariable::getValue).containsExactly("a", "b");
  }

  @BeforeEach
  public void startTransaction() {
    this.store.beginTransaction();
  }
}
