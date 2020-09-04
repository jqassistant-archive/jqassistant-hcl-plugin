package org.jqassistant.contrib.plugin.hcl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TerraformScannerPluginUnitTest {
	private TerraformScannerPlugin plugin;

	@BeforeEach
	public void initClassUnderTest() {
		this.plugin = new TerraformScannerPlugin();
	}

	@Test
	public void shouldAcceptTFFile_whenAccept_givenTFFile() throws IOException {
		final boolean actualFileAccepted = this.plugin.accepts(null, "/abc/def.tf", null);

		assertThat(actualFileAccepted).isTrue();
	}

	@Test
	public void shouldIgnoreCase_whenAccept_givenFilenameIsMixedCase() throws IOException {
		final boolean actualFileAccepted = this.plugin.accepts(null, "/abc/def.TF", null);

		assertThat(actualFileAccepted).isTrue();
	}

	@Test
	public void shouldIgnoreNonTFFiles_whenAccept_givenFilenameIsDOC() throws IOException {
		final boolean actualFileAccepted = this.plugin.accepts(null, "/abc/def.doc", null);

		assertThat(actualFileAccepted).isFalse();
	}
}
