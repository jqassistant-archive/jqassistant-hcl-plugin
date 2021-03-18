package org.jqassistant.contrib.plugin.hcl.constraint.naming;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.jqassistant.contrib.plugin.hcl.TerraformScannerPluginIT;
import org.junit.jupiter.api.Test;

import com.buschmais.jqassistant.core.report.api.model.Result;
import com.buschmais.jqassistant.core.report.api.model.Result.Status;
import com.buschmais.jqassistant.core.rule.api.model.Constraint;
import com.buschmais.jqassistant.core.rule.api.model.RuleException;
import com.buschmais.jqassistant.core.rule.api.model.Severity;
import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;

public class FileNameConstraintsIT extends AbstractPluginIT {
    private static final String INPUT_VARIABLE_TF = "/constraint/naming/input-variable.tf";
    private static final String LOCAL_VARIABLE_TF = "/constraint/naming/local-variable.tf";
    private static final String OUTPUT_VARIABLE_TF = "/constraint/naming/output-variable.tf";

    @Test
    public void shouldFindInputVariablesInWrongFile_whenValidateConstraint() throws RuleException {
        // given
        final File testFile = new File(this.getClassesDirectory(TerraformScannerPluginIT.class), INPUT_VARIABLE_TF);
        this.getScanner().scan(testFile, INPUT_VARIABLE_TF, DefaultScope.NONE);

        // when
        final Result<Constraint> actualConstraint = validateConstraint("hcl:InputVariableFileName");

        // then
        assertThat(actualConstraint.getSeverity()).isEqualTo(Severity.MAJOR);
        assertThat(actualConstraint.getStatus()).isEqualTo(Status.FAILURE);

        this.store.beginTransaction();

        assertThat(actualConstraint.getRows()).hasSize(1);

        final String actualInvalidFilename = (String) actualConstraint.getRows().get(0).get("f.fileName");
        assertThat(actualInvalidFilename).isEqualTo("/constraint/naming/input-variable.tf");

        this.store.rollbackTransaction();
    }

    @Test
    public void shouldFindLocalVariablesInWrongFile_whenValidateConstraint() throws RuleException {
        // given
        final File testFile = new File(this.getClassesDirectory(TerraformScannerPluginIT.class), LOCAL_VARIABLE_TF);
        this.getScanner().scan(testFile, LOCAL_VARIABLE_TF, DefaultScope.NONE);

        // when
        final Result<Constraint> actualConstraint = validateConstraint("hcl:LocalVariableFileName");

        // then
        assertThat(actualConstraint.getSeverity()).isEqualTo(Severity.MAJOR);
        assertThat(actualConstraint.getStatus()).isEqualTo(Status.FAILURE);

        this.store.beginTransaction();

        assertThat(actualConstraint.getRows()).hasSize(1);

        final String actualInvalidFilename = (String) actualConstraint.getRows().get(0).get("f.fileName");
        assertThat(actualInvalidFilename).isEqualTo("/constraint/naming/local-variable.tf");

        this.store.rollbackTransaction();
    }

    @Test
    public void shouldFindOutputVariablesInWrongFile_whenValidateConstraint() throws RuleException {
        // given
        final File testFile = new File(this.getClassesDirectory(TerraformScannerPluginIT.class), OUTPUT_VARIABLE_TF);
        this.getScanner().scan(testFile, OUTPUT_VARIABLE_TF, DefaultScope.NONE);

        // when
        final Result<Constraint> actualConstraint = validateConstraint("hcl:OutputVariableFileName");

        // then
        assertThat(actualConstraint.getSeverity()).isEqualTo(Severity.MAJOR);
        assertThat(actualConstraint.getStatus()).isEqualTo(Status.FAILURE);

        this.store.beginTransaction();

        assertThat(actualConstraint.getRows()).hasSize(1);

        final String actualInvalidFilename = (String) actualConstraint.getRows().get(0).get("f.fileName");
        assertThat(actualInvalidFilename).isEqualTo("/constraint/naming/output-variable.tf");

        this.store.rollbackTransaction();
    }
}
