package org.jqassistant.contrib.plugin.hcl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformLexer;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.ArgumentContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.BlockContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.FileContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.VariableContext;
import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformInputVariable;
import org.jqassistant.contrib.plugin.hcl.model.internal.InputVariable;
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
import com.google.common.collect.ImmutableMap;

@ScannerPlugin.Requires(FileDescriptor.class)
public class TerraformScannerPlugin extends AbstractScannerPlugin<FileResource, TerraformFileDescriptor> {
  private static final Consumer<String> DO_NOTHING = s -> {
  };

  private static final Logger logger = LoggerFactory.getLogger(TerraformScannerPlugin.class);

  @Override
  public boolean accepts(final FileResource item, final String path, final Scope scope) throws IOException {
    return path.toLowerCase().endsWith(".tf");
  }

  private InputVariable extractInputVariable(final ParseTree inputVariableNode) {
    final InputVariable variable = new InputVariable();
    variable.setName(inputVariableNode.getChild(1).getText());

    final Consumer<String> setDefault = s -> variable.setDefaultValue(s);
    final Consumer<String> setType = s -> variable.setType(s);
    final Consumer<String> setDescription = s -> variable.setDescription(s);

    final Map<String, Consumer<String>> setter = ImmutableMap.of("default", setDefault, "type", setType, "description",
        setDescription);

    // skip the terminals for "{" and "}"
    final ParseTree properties = inputVariableNode.getChild(2);

    for (int i = 1; i < properties.getChildCount() - 1; i++) {
      final ParseTree property = properties.getChild(i);

      if (property instanceof ArgumentContext) {
        setter.getOrDefault(property.getChild(0).getText(), DO_NOTHING).accept(property.getChild(2).getText());
      } else if (property instanceof BlockContext) {
        property.getChild(0).getText(); // validation
      }
    }

    return variable;
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
      final List<VariableContext> variables = ast.variable();

      variables.forEach(variableContext -> {
        final TerraformInputVariable variable = store.create(TerraformInputVariable.class);

        terraformFileDescriptor.getInputVariables().add(extractInputVariable(variableContext).toStore(variable));
      });

      terraformFileDescriptor.setValid(true);
    } catch (final IOException e) {
      terraformFileDescriptor.setValid(false);

      logger.error("Parsing failed", e);
    }

    return terraformFileDescriptor;
  }

}