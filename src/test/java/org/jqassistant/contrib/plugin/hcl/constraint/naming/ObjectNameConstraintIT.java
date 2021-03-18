package org.jqassistant.contrib.plugin.hcl.constraint.naming;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.jqassistant.contrib.plugin.hcl.TerraformScannerPluginIT;
import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Result.Status;
import com.buschmais.jqassistant.core.rule.api.model.Constraint;
import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.core.rule.api.model.Severity;
import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class ObjectNameConstraintIT extends AbstractPluginIT {
  private static final String FILE_FILENAME_TF = "/constraint/naming/file-name.tf";
  private static final String FILE_HYPHEN_TF = "/constraint/naming/object-name-hyphen.tf";
  private static final String FILE_UPPERCASE_TF = "/constraint/naming/object-name-uppercase.tf";

  @Test
  public void shouldFindObjectNamesContainingHyphen() throws RuleException {
    // given
    final File testFile = new File(this.getClassesDirectory(TerraformScannerPluginIT.class), FILE_HYPHEN_TF);
    this.getScanner().scan(testFile, FILE_HYPHEN_TF, DefaultScope.NONE);

    // when
    final Result<Constraint> actualConstraint = validateConstraint("hcl:ObjectNames");

    // then
    assertThat(actualConstraint.getSeverity()).isEqualTo(Severity.MAJOR);
    assertThat(actualConstraint.getStatus()).isEqualTo(Status.FAILURE);

    this.store.beginTransaction();

    assertThat(actualConstraint.getRows()).hasSize(1);

    final TerraformBlock actualInvalidObject = (TerraformBlock) actualConstraint.getRows().get(0).get("n");
    assertThat(actualInvalidObject.getInternalName()).isEqualTo("name-with-hyphen");

    this.store.rollbackTransaction();
  }

  @Test
  public void shouldFindObjectNamesContainingUppercaseLetters() throws RuleException {
    // given
    final File testFile = new File(this.getClassesDirectory(TerraformScannerPluginIT.class), FILE_UPPERCASE_TF);
    this.getScanner().scan(testFile, FILE_UPPERCASE_TF, DefaultScope.NONE);

    // when
    final Result<Constraint> actualConstraint = validateConstraint("hcl:ObjectNames");

    // then
    assertThat(actualConstraint.getSeverity()).isEqualTo(Severity.MAJOR);
    assertThat(actualConstraint.getStatus()).isEqualTo(Status.FAILURE);

    this.store.beginTransaction();

    assertThat(actualConstraint.getRows()).hasSize(1);

    final TerraformBlock actualInvalidObject = (TerraformBlock) actualConstraint.getRows().get(0).get("n");
    assertThat(actualInvalidObject.getInternalName()).isEqualTo("No_Uppercase_Letters");

    this.store.rollbackTransaction();
  }

  @Test
  public void shouldIgnoreFileNames() throws RuleException {
    // given
    final File testFile = new File(this.getClassesDirectory(TerraformScannerPluginIT.class), FILE_FILENAME_TF);
    this.getScanner().scan(testFile, FILE_FILENAME_TF, DefaultScope.NONE);

    // when
    final Result<Constraint> actualConstraint = validateConstraint("hcl:ObjectNames");

    // then
    assertThat(actualConstraint.getSeverity()).isEqualTo(Severity.MAJOR);
    assertThat(actualConstraint.getStatus()).isEqualTo(Status.SUCCESS);
  }
}
