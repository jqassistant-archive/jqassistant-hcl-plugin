package org.jqassistant.contrib.plugin.hcl;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformLexer;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.FileContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.OutputContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.VariableContext;
import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformInputVariable;
import org.jqassistant.contrib.plugin.hcl.model.TerraformModule;
import org.jqassistant.contrib.plugin.hcl.model.TerraformOutputVariable;
import org.jqassistant.contrib.plugin.hcl.parser.ASTParser;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;

@ScannerPlugin.Requires(FileDescriptor.class)
public class TerraformScannerPlugin extends AbstractScannerPlugin<FileResource, TerraformFileDescriptor> {
  private static final Logger logger = LoggerFactory.getLogger(TerraformScannerPlugin.class);

  @Override
  public boolean accepts(final FileResource item, final String path, final Scope scope) throws IOException {
    return path.toLowerCase().endsWith(".tf");
  }

  private void addModule(final String path, final StoreHelper storeHelper) {
    final String moduleName = Paths.get(path).getParent().toString();

    final TerraformModule module = storeHelper.createOrRetrieveObject(moduleName, TerraformModule.class);
    module.setName(moduleName);
  }

  @Override
  public TerraformFileDescriptor scan(final FileResource item, final String path, final Scope scope,
      final Scanner scanner) {
    final ScannerContext context = scanner.getContext();
    final Store store = context.getStore();

    // add the file
    final FileDescriptor fileDescriptor = context.getCurrentDescriptor();
    final TerraformFileDescriptor terraformFileDescriptor = store.addDescriptorType(fileDescriptor,
        TerraformFileDescriptor.class);

    try {
      final terraformLexer lexer = new terraformLexer(CharStreams.fromStream(item.createStream()));
      final CommonTokenStream tokens = new CommonTokenStream(lexer);
      final terraformParser parser = new terraformParser(tokens);

      final FileContext ast = parser.file();
      final List<VariableContext> inputVariables = ast.variable();
      final List<OutputContext> outputVariables = ast.output();

      final ASTParser astParser = new ASTParser();
      final StoreHelper storeHelper = new StoreHelper(store);

      addModule(path, storeHelper);

      inputVariables.forEach(inputVariableContext -> {
        final TerraformInputVariable variable = store.create(TerraformInputVariable.class);

        terraformFileDescriptor.getInputVariables()
            .add(astParser.extractInputVariable(inputVariableContext).toStore(variable));
      });

      outputVariables.forEach(outputVariableContext -> {
        final TerraformOutputVariable outputVariable = store.create(TerraformOutputVariable.class);

        terraformFileDescriptor.getOutputVariables()
            .add(astParser.extractOutputVariable(outputVariableContext).toStore(outputVariable, storeHelper));
      });

      terraformFileDescriptor.setValid(true);
    } catch (final IOException e) {
      terraformFileDescriptor.setValid(false);

      logger.error(String.format("Error reading file {}", path), e);
    }

    return terraformFileDescriptor;
  }

}
