package org.jqassistant.contrib.plugin.hcl.visitor;

import java.util.Map;
import java.util.function.Consumer;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.ArgumentContext;
import org.jqassistant.contrib.plugin.hcl.grammar.terraformParser.BlockContext;
import org.jqassistant.contrib.plugin.hcl.model.TerraformInputVariable;
import org.jqassistant.contrib.plugin.hcl.model.TerraformInputVariableValidation;

import com.google.common.collect.ImmutableMap;

public class InputVariableParseTreeVisitor implements ParseTreeVisitor<TerraformInputVariable> {
	private final TerraformInputVariable variable;

	public InputVariableParseTreeVisitor(final TerraformInputVariable variable) {
		this.variable = variable;
	}

	private void extractProperties(final ParseTree node) {
		final Consumer<String> setDefault = s -> this.variable.setDefault(s);
		final Consumer<String> setType = s -> this.variable.setType(s);
		final Consumer<String> setDescription = s -> this.variable.setDescription(s);
		final Consumer<String> doNothing = s -> {
		};

		final Map<String, Consumer<String>> setter = ImmutableMap.of("default", setDefault, "type", setType,
				"description", setDescription);

		// cut the terminals for "{" and "}"
		for (int i = 1; i < node.getChildCount() - 1; i++) {
			final ParseTree property = node.getChild(i);

			if (property instanceof ArgumentContext) {
				setter.getOrDefault(property.getChild(0).getText(), doNothing).accept(property.getChild(2).getText());
			} else if (property instanceof BlockContext) {
				property.getChild(0).getText(); // validation
			}
		}
	}

	private TerraformInputVariableValidation extractValidation(final ParseTree validationNode) {
		final Consumer<String> doNothing = s -> {
		};

		final Map<String, Consumer<String>> setter = ImmutableMap.of("errormessage", setErrorMessage, "rule", setRule,
				"description", setDescription);

		// cut the terminals for "{" and "}"
		for (int i = 1; i < validationNode.getChildCount() - 1; i++) {
			final ParseTree property = validationNode.getChild(i);

			if (property instanceof ArgumentContext) {
				setter.getOrDefault(property.getChild(0).getText(), doNothing).accept(property.getChild(2).getText());
			}
		}
	}

	@Override
	public TerraformInputVariable visit(final ParseTree tree) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TerraformInputVariable visitChildren(final RuleNode node) {
		this.variable.setName(node.getChild(1).getText().replace("\"", ""));

		if (node.getChildCount() >= 3) {
			this.extractProperties(node.getChild(2));
		}

		return this.variable;
	}

	@Override
	public TerraformInputVariable visitErrorNode(final ErrorNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TerraformInputVariable visitTerminal(final TerminalNode node) {
		return null;
	}

}
