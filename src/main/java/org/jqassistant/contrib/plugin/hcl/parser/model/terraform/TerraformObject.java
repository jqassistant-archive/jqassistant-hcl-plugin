package org.jqassistant.contrib.plugin.hcl.parser.model.terraform;

import java.io.File;
import java.nio.file.Path;

import org.jqassistant.contrib.plugin.hcl.model.TerraformBlock;
import org.jqassistant.contrib.plugin.hcl.model.TerraformDescriptor;
import org.jqassistant.contrib.plugin.hcl.model.TerraformLogicalModule;
import org.jqassistant.contrib.plugin.hcl.util.StoreHelper;

import com.buschmais.jqassistant.core.store.api.Store;
import com.google.common.collect.ImmutableMap;

/**
 * Superclass for all terraform objects to model dependencies between them.
 *
 * @author Matthias Kay
 * @since 1.0
 *
 */
public abstract class TerraformObject<T extends TerraformBlock> {
  /**
   * Calculates the prefix for the full qualified name.
   *
   * @param filePath the file path of the file the object is defined in
   * @return the prefix to use for the full qualified name
   */
  protected static String getFullQualifiedNamePrefix(final Path filePath) {
    return filePath.getParent().normalize().toString().replace(File.separatorChar, '.') + ".";
  }

  /**
   * Saves the internal state.
   *
   * @param object       receives the data to save
   * @param storeHelper  helper to access the {@link Store}
   * @param partOfModule The {@link TerraformLogicalModule} to which
   *                     <code>object</code> belongs to
   * @param filePath     the name of the directory in which this object is defined
   * @return the <code>object</code>
   */
  protected abstract T saveInternalState(T object, final TerraformLogicalModule partOfModule, Path filePath,
      StoreHelper storeHelper);

  /**
   * Converts this object into a terraform object of <code>T</code>. If the object
   * already exists, that instance is returned and updated.
   *
   * @param storeHelper       helper to access the {@link Store}
   * @param fullQualifiedName name used to find an existing object
   * @param partOfModule      <code>null</code> to search all objects, otherwise
   *                          searches objects belonging to that
   *                          {@link TerraformLogicalModule}
   * @param clazz             If the object does not exists a new instance of this
   *                          type is created.
   * @param filePath          the name of the directory in which this object is
   *                          defined
   * @return <code>variable</code>
   */
  public T toStore(final StoreHelper storeHelper, final String fullQualifiedName, final Path filePath,
      final TerraformLogicalModule partOfModule, final Class<T> clazz) {
    final T object = storeHelper.createOrRetrieveObject(
        ImmutableMap.of(TerraformDescriptor.FieldName.FULL_QUALIFIED_NAME, fullQualifiedName), partOfModule, clazz);

    return saveInternalState(object, partOfModule, filePath, storeHelper);
  }
}
