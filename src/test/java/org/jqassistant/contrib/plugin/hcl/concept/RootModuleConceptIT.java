package org.jqassistant.contrib.plugin.hcl.concept;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.jqassistant.contrib.plugin.hcl.TerraformScannerPluginIT;
import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Result.Status;
import com.buschmais.jqassistant.core.rule.api.model.Concept;
import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.core.rule.api.model.Severity;
import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import com.buschmais.xo.api.Query;

public class RootModuleConceptIT extends AbstractPluginIT {
  private static final String FILE_TEST_TF = "/concept/root-module/main.tf";

  protected <T> T retrieveSingleValue(final String query, final Class<T> t) {
    final Query.Result.CompositeRowObject singleResult = this.store.executeQuery(query).getSingleResult();
    return singleResult.get(singleResult.getColumns().get(0), t);
  }

  @Test
  public void shouldMarkTheTopLevelModuleAsRootModule() throws RuleException {
    // given
    final File testFile = new File(this.getClassesDirectory(TerraformScannerPluginIT.class), FILE_TEST_TF);

    final TerraformFileDescriptor descriptor = this.getScanner().scan(testFile, FILE_TEST_TF, DefaultScope.NONE);

    // when
    final Result<Concept> actualConcept = applyConcept("hcl:RootModule");

    // then
    assertThat(actualConcept.getSeverity()).isEqualTo(Severity.MINOR);
    assertThat(actualConcept.getStatus()).isEqualTo(Status.SUCCESS);

    this.store.beginTransaction();

    assertThat(actualConcept.getRows()).hasSize(1).first();

    final TerraformLogicalModule actualRootModule = (TerraformLogicalModule) actualConcept.getRows().get(0).get("n");
    assertThat(actualRootModule.getFullQualifiedName()).isEqualTo(".concept.root-module");

    this.store.rollbackTransaction();
  }
}
