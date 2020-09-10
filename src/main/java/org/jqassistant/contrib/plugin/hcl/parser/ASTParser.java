package org.jqassistant.contrib.plugin.hcl.parser;

import java.util.Map;
import java.util.function.Consumer;

import org.antlr.v4.runtime.tree.ParseTree;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.ArgumentContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.BlockContext;
import org.jqassistant.contrib.plugin.hcl.model.internal.InputVariable;
import org.jqassistant.contrib.plugin.hcl.util.StringHelper;

import com.google.common.collect.ImmutableMap;

/**
 * A parser for the AST generated for the HCL grammar.
 *
 * @author Matthias Kay
 * @since 1.0
 *
 */
public class ASTParser {
  private static final Consumer<String> DO_NOTHING = s -> {
  };

  /**
   * Extracts the properties of an input variable.
   *
   * @param inputVariableNode Points to an input variable of the AST and is
   *                          extracted.
   * @return The {@link InputVariable} extracted from the AST.
   */
  public InputVariable extractInputVariable(final ParseTree inputVariableNode) {
    final InputVariable variable = new InputVariable();
    variable.setName(StringHelper.removeQuotes(inputVariableNode.getChild(1).getText()));

    final Consumer<String> setDefault = s -> variable.setDefaultValue(StringHelper.removeQuotes(s));
    final Consumer<String> setType = s -> variable.setType(StringHelper.removeQuotes(s));
    final Consumer<String> setDescription = s -> variable.setDescription(StringHelper.removeQuotes(s));
    final Consumer<String> setValidationRule = s -> variable.setValidationRule(StringHelper.removeQuotes(s));
    final Consumer<String> setValidationErrorMessage = s -> variable
        .setValidationErrorMessage(StringHelper.removeQuotes(s));

    final Map<String, Consumer<String>> setter = ImmutableMap.of("default", setDefault, "type", setType, "description",
        setDescription, "validation.condition", setValidationRule, "validation.error_message",
        setValidationErrorMessage);

    extractPropertiesRecursivlyFromBlock(setter, inputVariableNode.getChild(2));

    return variable;
  }

  /**
   * Extracts the properties from the <code>node</code> and stores them via a
   * <code>propertySetter</code> in an object.
   *
   * Nested blocks can be extracted by prepending the blockname and a '.' to the
   * property name, e.g. "validation.error_message" to extract the
   * <i>error_message</i> property from the nested block <i>validation</i>.
   *
   * @param propertySetter The key references the propery name and the value is
   *                       the {@link Consumer} which stores the value.
   * @param node           The BlockbodyContext from the AST.
   */
  private void extractPropertiesRecursivlyFromBlock(final Map<String, Consumer<String>> propertySetter,
      final ParseTree node) {
    extractPropertiesRecursivlyFromBlock(propertySetter, node, "");
  }

  /**
   * @see #extractPropertiesRecursivlyFromBlock(Map, ParseTree)
   */
  private void extractPropertiesRecursivlyFromBlock(final Map<String, Consumer<String>> propertySetter,
      final ParseTree node, final String blockName) {
    // skip the terminals for "{" and "}"
    for (int i = 1; i < node.getChildCount() - 1; i++) {
      final ParseTree property = node.getChild(i);

      if (property instanceof ArgumentContext) {
        // identifier = value setting
        propertySetter.getOrDefault(blockName + property.getChild(0).getText(), DO_NOTHING)
            .accept(property.getChild(2).getText());
      } else if (property instanceof BlockContext) {
        // a nested block
        extractPropertiesRecursivlyFromBlock(propertySetter, property.getChild(1),
            blockName + property.getChild(0).getChild(0).getText() + ".");
      }
    }
  }
}
