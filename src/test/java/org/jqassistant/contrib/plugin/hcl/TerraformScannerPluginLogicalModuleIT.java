package org.jqassistant.contrib.plugin.hcl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class TerraformScannerPluginLogicalModuleIT extends AbstractPluginIT {
  private static final String FILE_NAME = "/terraform/logical module/main.tf";

  @Test
  public void shouldCreateALogicalModule_whenScan_givenTerraformFile() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginLogicalModuleIT.class),
        FILE_NAME);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_NAME,
        DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();
    assertThat(actualDescriptor.getModule()).isNotNull();

    final TerraformLogicalModule actualModule = actualDescriptor.getModule();
    assertThat(actualModule)
        .extracting(TerraformLogicalModule::getInternalName, TerraformLogicalModule::getFullQualifiedName)
        .containsExactly("logical module", ".terraform.logical module");
  }

  @Test
  public void shouldReturnInputVariables_whenScan_givenInputVariablesDefined() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginLogicalModuleIT.class),
        FILE_NAME);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_NAME,
        DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();

    assertThat(actualDescriptor.getModule().getInputVariables()).hasSize(1);
  }

  @Test
  public void shouldReturnOutputVariables_whenScan_givenOutputVariablesDefined() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginLogicalModuleIT.class),
        FILE_NAME);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_NAME,
        DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();

    assertThat(actualDescriptor.getModule().getOutputVariables()).hasSize(1);
  }

  @BeforeEach
  public void startTransaction() {
    this.store.beginTransaction();
  }
}
