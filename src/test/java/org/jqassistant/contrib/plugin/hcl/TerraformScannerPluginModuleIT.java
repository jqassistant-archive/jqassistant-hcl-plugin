package org.jqassistant.contrib.plugin.hcl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;
import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class TerraformScannerPluginModuleIT extends AbstractPluginIT {
  private static final String FILE_NAME = "/terraform/module/main.tf";

  @Test
  public void shouldReadAllAttributes_whenScan_givenLocalModuleDefinitionCount() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginOutputVariableIT.class),
        FILE_NAME);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_NAME,
        DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();
    assertThat(actualDescriptor.getModule().getCalledModules()).hasSize(3);

    final TerraformModule actualModule = actualDescriptor.getModule().getCalledModules().stream()
        .filter(m -> "local_count".equals(m.getName())).findFirst().get();

    assertThat(actualModule).extracting(TerraformModule::getCount, TerraformModule::getName, TerraformModule::getSource)
        .containsExactly("2", "local_count", "/terraform/module/test_module");

    final List<TerraformBlock> actualDependantObjects = actualModule.getDependantResources();

    assertThat(actualDependantObjects).hasSize(1).extracting(TerraformBlock::getFullQualifiedName)
        .containsExactlyInAnyOrder("aws_db_instance.main");
  }

  @Test
  public void shouldReadAllAttributes_whenScan_givenLocalModuleDefinitionForEach() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginOutputVariableIT.class),
        FILE_NAME);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_NAME,
        DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();
    assertThat(actualDescriptor.getModule().getCalledModules()).hasSize(3);

    final TerraformModule actualModule = actualDescriptor.getModule().getCalledModules().stream()
        .filter(m -> "local_foreach".equals(m.getName())).findFirst().get();

    assertThat(actualModule)
        .extracting(TerraformModule::getForEach, TerraformModule::getName, TerraformModule::getSource)
        .containsExactly("toset([\"assets\",\"media\"])", "local_foreach", "/terraform/module/test_module");
  }

  @Test
  public void shouldReadVersionAttribute_whenScan_givenRemoteModuleDefinition() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginOutputVariableIT.class),
        FILE_NAME);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_NAME,
        DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();
    assertThat(actualDescriptor.getModule().getCalledModules()).hasSize(3);

    final TerraformModule actualModule = actualDescriptor.getModule().getCalledModules().stream()
        .filter(m -> "remote".equals(m.getName())).findFirst().get();

    assertThat(actualModule)
        .extracting(TerraformModule::getVersion, TerraformModule::getName, TerraformModule::getSource)
        .containsExactly("0.6.7", "remote", "hashicorp/nomad/aws");
  }

  @BeforeEach
  public void startTransaction() {
    this.store.beginTransaction();
  }
}
