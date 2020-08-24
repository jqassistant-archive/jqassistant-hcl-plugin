package org.jqassistant.contrib.plugin.hcl;

import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformLexer;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.BlockContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.FileContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.VariableContext;
import org.jqassistant.contrib.plugin.hcl.model.TerraformFileDescriptor;
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
  public boolean accepts(FileResource item, String path, Scope scope) throws IOException {
    return path.toLowerCase().endsWith(".tf");
  }

  @Override
  public TerraformFileDescriptor scan(FileResource item, String path, Scope scope, Scanner scanner) {
    ScannerContext context = scanner.getContext();
    final Store store = context.getStore();

    // add the file
    final FileDescriptor fileDescriptor = context.getCurrentDescriptor();
    final TerraformFileDescriptor terraformFileDescriptor = store.addDescriptorType(fileDescriptor,
        TerraformFileDescriptor.class);

    try {
      terraformLexer lexer = new terraformLexer(CharStreams.fromStream(item.createStream()));
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      terraformParser parser = new terraformParser(tokens);

      FileContext ast = parser.file();
      List<VariableContext> variables = ast.variable();
      List<BlockContext> blocks = ast.block();

      terraformFileDescriptor.setValid(true);
    } catch (IOException e) {
      terraformFileDescriptor.setValid(false);

      logger.error("Parsing failed", e);
    }

    return terraformFileDescriptor;
  }
}
