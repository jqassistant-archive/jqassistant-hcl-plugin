package org.jqassistant.contrib.plugin.hcl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;
import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformModule;
import org.jqassistant.contrib.plugin.hcl.test.AbstractTerraformPluginIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;

public class TerraformScannerPluginModuleIT extends AbstractTerraformPluginIT {
  private static final String FILE_NAME_MAIN = "/terraform/module/main.tf";
  private static final String FILE_NAME_TEST_MODULE = "/terraform/module/test_module/main.tf";

  @Test
  public void shouldLinkTheCalledModule_whenScan_givenModuleCall() {
    // given
    final File givenTestFileMain = new File(this.getClassesDirectory(TerraformScannerPluginOutputVariableIT.class),
        FILE_NAME_MAIN);
    final File givenTestFileTestModule = new File(
        this.getClassesDirectory(TerraformScannerPluginOutputVariableIT.class), FILE_NAME_TEST_MODULE);

    // when
    final TerraformFileDescriptor actualDescriptorMain = this.getScanner().scan(givenTestFileMain, FILE_NAME_MAIN,
        DefaultScope.NONE);
    final TerraformFileDescriptor actualDescriptorTest = this.getScanner().scan(givenTestFileTestModule,
        FILE_NAME_TEST_MODULE, DefaultScope.NONE);

    // then
    assertThat(actualDescriptorMain.isValid()).isTrue();
    assertThat(actualDescriptorTest.isValid()).isTrue();

    assertThat(actualDescriptorMain.getModule().getCalledModules().get(0).getSourcedFrom().getFullQualifiedName())
        .isEqualTo(".terraform.module.test_module");
  }

  @Test
  public void shouldReadAllAttributes_whenScan_givenLocalModuleDefinitionCount() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginOutputVariableIT.class),
        FILE_NAME_MAIN);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_NAME_MAIN,
        DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();
    assertThat(actualDescriptor.getModule().getCalledModules()).hasSize(3);

    final TerraformModule actualModule = actualDescriptor.getModule().getCalledModules().stream()
        .filter(m -> "local_count".equals(m.getInternalName())).findFirst().get();

    assertThat(actualModule)
        .extracting(TerraformModule::getCount, TerraformModule::getInternalName, TerraformModule::getSource)
        .containsExactly("2", "local_count", "/terraform/module/test_module");

    final List<TerraformBlock> actualDependantObjects = actualModule.getDependantResources();

    assertThat(actualDependantObjects).hasSize(1).extracting(TerraformBlock::getFullQualifiedName)
        .containsExactlyInAnyOrder("aws_db_instance.main");

    // read all properties as some are not part of the model (input parameters)
    final Map<String, String> actualProperties = this.readAllProperties(actualModule);

    assertThat(actualProperties).contains(entry("in", "4711"));
  }

  @Test
  public void shouldReadAllAttributes_whenScan_givenLocalModuleDefinitionForEach() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginOutputVariableIT.class),
        FILE_NAME_MAIN);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_NAME_MAIN,
        DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();
    assertThat(actualDescriptor.getModule().getCalledModules()).hasSize(3);

    final TerraformModule actualModule = actualDescriptor.getModule().getCalledModules().stream()
        .filter(m -> "local_foreach".equals(m.getInternalName())).findFirst().get();

    assertThat(actualModule)
        .extracting(TerraformModule::getForEach, TerraformModule::getInternalName, TerraformModule::getSource)
        .containsExactly("toset([\"assets\",\"media\"])", "local_foreach", "/terraform/module/test_module");
  }

  @Test
  public void shouldReadVersionAttribute_whenScan_givenRemoteModuleDefinition() {
    // given
    final File givenTestFile = new File(this.getClassesDirectory(TerraformScannerPluginOutputVariableIT.class),
        FILE_NAME_MAIN);

    // when
    final TerraformFileDescriptor actualDescriptor = this.getScanner().scan(givenTestFile, FILE_NAME_MAIN,
        DefaultScope.NONE);

    // then
    assertThat(actualDescriptor.isValid()).isTrue();
    assertThat(actualDescriptor.getModule().getCalledModules()).hasSize(3);

    final TerraformModule actualModule = actualDescriptor.getModule().getCalledModules().stream()
        .filter(m -> "remote".equals(m.getInternalName())).findFirst().get();

    assertThat(actualModule)
        .extracting(TerraformModule::getVersion, TerraformModule::getInternalName, TerraformModule::getSource)
        .containsExactly("0.6.7", "remote", "hashicorp/nomad/aws");
  }

  @BeforeEach
  public void startTransaction() {
    this.store.beginTransaction();
  }
}
