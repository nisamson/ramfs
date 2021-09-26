package sh.nes.ramfs

import java.lang.IllegalArgumentException
import java.net.URI
import java.nio.channels.SeekableByteChannel
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import java.nio.file.spi.FileSystemProvider
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.IllegalArgumentException

class RamFsProvider : FileSystemProvider() {

    private val rwLock = ReentrantReadWriteLock(false)
    private val fileSystems = ConcurrentSkipListMap<String, RamFs>(compareBy { s: String -> s.lowercase() })

    companion object {
        val URI_SCHEME = "ramfs"
    }

    override fun getScheme(): String {
        return URI_SCHEME
    }

    /**
     * Constructs a new `FileSystem` object identified by a URI. This
     * method is invoked by the [FileSystems.newFileSystem]
     * method to open a new file system identified by a URI.
     *
     *
     *  The `uri` parameter is an absolute, hierarchical URI, with a
     * scheme equal (without regard to case) to the scheme supported by this
     * provider. The exact form of the URI is highly provider dependent. The
     * `env` parameter is a map of provider specific properties to configure
     * the file system.
     *
     *
     *  This method throws [FileSystemAlreadyExistsException] if the
     * file system already exists because it was previously created by an
     * invocation of this method. Once a file system is [ ][java.nio.file.FileSystem.close] it is provider-dependent if the
     * provider allows a new file system to be created with the same URI as a
     * file system it previously created.
     *
     * @param   uri
     * URI reference
     * @param   env
     * A map of provider specific properties to configure the file system;
     * may be empty
     *
     * @return  A new file system
     *
     * @throws  IllegalArgumentException
     * If the pre-conditions for the `uri` parameter aren't met,
     * or the `env` parameter does not contain properties required
     * by the provider, or a property value is invalid
     * @throws  IOException
     * An I/O error occurs creating the file system
     * @throws  SecurityException
     * If a security manager is installed and it denies an unspecified
     * permission required by the file system provider implementation
     * @throws  FileSystemAlreadyExistsException
     * If the file system has already been created
     */
    override fun newFileSystem(uri: URI?, env: MutableMap<String, *>?): FileSystem {
       if (env?.isNotEmpty() == true) {
           throw IllegalArgumentException("Unexpected environment variables in env (should be none)")
       }

        uri!!
    }

    /**
     * Returns an existing `FileSystem` created by this provider.
     *
     *
     *  This method returns a reference to a `FileSystem` that was
     * created by invoking the [newFileSystem(URI,Map)][.newFileSystem]
     * method. File systems created the [ newFileSystem(Path,Map)][.newFileSystem] method are not returned by this method.
     * The file system is identified by its `URI`. Its exact form
     * is highly provider dependent. In the case of the default provider the URI's
     * path component is `"/"` and the authority, query and fragment components
     * are undefined (Undefined components are represented by `null`).
     *
     *
     *  Once a file system created by this provider is [ ][java.nio.file.FileSystem.close] it is provider-dependent if this
     * method returns a reference to the closed file system or throws [ ]. If the provider allows a new file system to
     * be created with the same URI as a file system it previously created then
     * this method throws the exception if invoked after the file system is
     * closed (and before a new instance is created by the [ newFileSystem][.newFileSystem] method).
     *
     *
     *  If a security manager is installed then a provider implementation
     * may require to check a permission before returning a reference to an
     * existing file system. In the case of the [ default][FileSystems.getDefault] file system, no permission check is required.
     *
     * @param   uri
     * URI reference
     *
     * @return  The file system
     *
     * @throws  IllegalArgumentException
     * If the pre-conditions for the `uri` parameter aren't met
     * @throws  FileSystemNotFoundException
     * If the file system does not exist
     * @throws  SecurityException
     * If a security manager is installed and it denies an unspecified
     * permission.
     */
    override fun getFileSystem(uri: URI?): FileSystem {
        TODO("Not yet implemented")
    }

    /**
     * Return a `Path` object by converting the given [URI]. The
     * resulting `Path` is associated with a [FileSystem] that
     * already exists or is constructed automatically.
     *
     *
     *  The exact form of the URI is file system provider dependent. In the
     * case of the default provider, the URI scheme is `"file"` and the
     * given URI has a non-empty path component, and undefined query, and
     * fragment components. The resulting `Path` is associated with the
     * default [default][FileSystems.getDefault] `FileSystem`.
     *
     *
     *  If a security manager is installed then a provider implementation
     * may require to check a permission. In the case of the [ ][FileSystems.getDefault] file system, no permission check is
     * required.
     *
     * @param   uri
     * The URI to convert
     *
     * @return  The resulting `Path`
     *
     * @throws  IllegalArgumentException
     * If the URI scheme does not identify this provider or other
     * preconditions on the uri parameter do not hold
     * @throws  FileSystemNotFoundException
     * The file system, identified by the URI, does not exist and
     * cannot be created automatically
     * @throws  SecurityException
     * If a security manager is installed and it denies an unspecified
     * permission.
     */
    override fun getPath(uri: URI): Path {
        TODO("Not yet implemented")
    }

    /**
     * Opens or creates a file, returning a seekable byte channel to access the
     * file. This method works in exactly the manner specified by the [ ][Files.newByteChannel] method.
     *
     * @param   path
     * the path to the file to open or create
     * @param   options
     * options specifying how the file is opened
     * @param   attrs
     * an optional list of file attributes to set atomically when
     * creating the file
     *
     * @return  a new seekable byte channel
     *
     * @throws  IllegalArgumentException
     * if the set contains an invalid combination of options
     * @throws  UnsupportedOperationException
     * if an unsupported open option is specified or the array contains
     * attributes that cannot be set atomically when creating the file
     * @throws  FileAlreadyExistsException
     * if a file of that name already exists and the [          ][StandardOpenOption.CREATE_NEW] option is specified
     * *(optional specific exception)*
     * @throws  IOException
     * if an I/O error occurs
     * @throws  SecurityException
     * In the case of the default provider, and a security manager is
     * installed, the [checkRead][SecurityManager.checkRead]
     * method is invoked to check read access to the path if the file is
     * opened for reading. The [          checkWrite][SecurityManager.checkWrite] method is invoked to check write access to the path
     * if the file is opened for writing. The [          ][SecurityManager.checkDelete] method is
     * invoked to check delete access if the file is opened with the
     * `DELETE_ON_CLOSE` option.
     */
    override fun newByteChannel(
        path: Path?,
        options: MutableSet<out OpenOption>?,
        vararg attrs: FileAttribute<*>?
    ): SeekableByteChannel {
        TODO("Not yet implemented")
    }

    /**
     * Opens a directory, returning a `DirectoryStream` to iterate over
     * the entries in the directory. This method works in exactly the manner
     * specified by the [ ][Files.newDirectoryStream]
     * method.
     *
     * @param   dir
     * the path to the directory
     * @param   filter
     * the directory stream filter
     *
     * @return  a new and open `DirectoryStream` object
     *
     * @throws  NotDirectoryException
     * if the file could not otherwise be opened because it is not
     * a directory *(optional specific exception)*
     * @throws  IOException
     * if an I/O error occurs
     * @throws  SecurityException
     * In the case of the default provider, and a security manager is
     * installed, the [checkRead][SecurityManager.checkRead]
     * method is invoked to check read access to the directory.
     */
    override fun newDirectoryStream(dir: Path?, filter: DirectoryStream.Filter<in Path>?): DirectoryStream<Path> {
        TODO("Not yet implemented")
    }

    /**
     * Creates a new directory. This method works in exactly the manner
     * specified by the [Files.createDirectory] method.
     *
     * @param   dir
     * the directory to create
     * @param   attrs
     * an optional list of file attributes to set atomically when
     * creating the directory
     *
     * @throws  UnsupportedOperationException
     * if the array contains an attribute that cannot be set atomically
     * when creating the directory
     * @throws  FileAlreadyExistsException
     * if a directory could not otherwise be created because a file of
     * that name already exists *(optional specific exception)*
     * @throws  IOException
     * if an I/O error occurs or the parent directory does not exist
     * @throws  SecurityException
     * In the case of the default provider, and a security manager is
     * installed, the [checkWrite][SecurityManager.checkWrite]
     * method is invoked to check write access to the new directory.
     */
    override fun createDirectory(dir: Path?, vararg attrs: FileAttribute<*>?) {
        TODO("Not yet implemented")
    }

    /**
     * Deletes a file. This method works in exactly the  manner specified by the
     * [Files.delete] method.
     *
     * @param   path
     * the path to the file to delete
     *
     * @throws  NoSuchFileException
     * if the file does not exist *(optional specific exception)*
     * @throws  DirectoryNotEmptyException
     * if the file is a directory and could not otherwise be deleted
     * because the directory is not empty *(optional specific
     * exception)*
     * @throws  IOException
     * if an I/O error occurs
     * @throws  SecurityException
     * In the case of the default provider, and a security manager is
     * installed, the [SecurityManager.checkDelete] method
     * is invoked to check delete access to the file
     */
    override fun delete(path: Path?) {
        TODO("Not yet implemented")
    }

    /**
     * Copy a file to a target file. This method works in exactly the manner
     * specified by the [Files.copy] method
     * except that both the source and target paths must be associated with
     * this provider.
     *
     * @param   source
     * the path to the file to copy
     * @param   target
     * the path to the target file
     * @param   options
     * options specifying how the copy should be done
     *
     * @throws  UnsupportedOperationException
     * if the array contains a copy option that is not supported
     * @throws  FileAlreadyExistsException
     * if the target file exists but cannot be replaced because the
     * `REPLACE_EXISTING` option is not specified *(optional
     * specific exception)*
     * @throws  DirectoryNotEmptyException
     * the `REPLACE_EXISTING` option is specified but the file
     * cannot be replaced because it is a non-empty directory
     * *(optional specific exception)*
     * @throws  IOException
     * if an I/O error occurs
     * @throws  SecurityException
     * In the case of the default provider, and a security manager is
     * installed, the [checkRead][SecurityManager.checkRead]
     * method is invoked to check read access to the source file, the
     * [checkWrite][SecurityManager.checkWrite] is invoked
     * to check write access to the target file. If a symbolic link is
     * copied the security manager is invoked to check [          ]`("symbolic")`.
     */
    override fun copy(source: Path?, target: Path?, vararg options: CopyOption?) {
        TODO("Not yet implemented")
    }

    /**
     * Move or rename a file to a target file. This method works in exactly the
     * manner specified by the [Files.move] method except that both the
     * source and target paths must be associated with this provider.
     *
     * @param   source
     * the path to the file to move
     * @param   target
     * the path to the target file
     * @param   options
     * options specifying how the move should be done
     *
     * @throws  UnsupportedOperationException
     * if the array contains a copy option that is not supported
     * @throws  FileAlreadyExistsException
     * if the target file exists but cannot be replaced because the
     * `REPLACE_EXISTING` option is not specified *(optional
     * specific exception)*
     * @throws  DirectoryNotEmptyException
     * the `REPLACE_EXISTING` option is specified but the file
     * cannot be replaced because it is a non-empty directory
     * *(optional specific exception)*
     * @throws  AtomicMoveNotSupportedException
     * if the options array contains the `ATOMIC_MOVE` option but
     * the file cannot be moved as an atomic file system operation.
     * @throws  IOException
     * if an I/O error occurs
     * @throws  SecurityException
     * In the case of the default provider, and a security manager is
     * installed, the [checkWrite][SecurityManager.checkWrite]
     * method is invoked to check write access to both the source and
     * target file.
     */
    override fun move(source: Path?, target: Path?, vararg options: CopyOption?) {
        TODO("Not yet implemented")
    }

    /**
     * Tests if two paths locate the same file. This method works in exactly the
     * manner specified by the [Files.isSameFile] method.
     *
     * @param   path
     * one path to the file
     * @param   path2
     * the other path
     *
     * @return  `true` if, and only if, the two paths locate the same file
     *
     * @throws  IOException
     * if an I/O error occurs
     * @throws  SecurityException
     * In the case of the default provider, and a security manager is
     * installed, the [checkRead][SecurityManager.checkRead]
     * method is invoked to check read access to both files.
     */
    override fun isSameFile(path: Path?, path2: Path?): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Tells whether or not a file is considered *hidden*. This method
     * works in exactly the manner specified by the [Files.isHidden]
     * method.
     *
     *
     *  This method is invoked by the [isHidden][Files.isHidden] method.
     *
     * @param   path
     * the path to the file to test
     *
     * @return  `true` if the file is considered hidden
     *
     * @throws  IOException
     * if an I/O error occurs
     * @throws  SecurityException
     * In the case of the default provider, and a security manager is
     * installed, the [checkRead][SecurityManager.checkRead]
     * method is invoked to check read access to the file.
     */
    override fun isHidden(path: Path?): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Returns the [FileStore] representing the file store where a file
     * is located. This method works in exactly the manner specified by the
     * [Files.getFileStore] method.
     *
     * @param   path
     * the path to the file
     *
     * @return  the file store where the file is stored
     *
     * @throws  IOException
     * if an I/O error occurs
     * @throws  SecurityException
     * In the case of the default provider, and a security manager is
     * installed, the [checkRead][SecurityManager.checkRead]
     * method is invoked to check read access to the file, and in
     * addition it checks
     * [RuntimePermission]`("getFileStoreAttributes")`
     */
    override fun getFileStore(path: Path?): FileStore {
        TODO("Not yet implemented")
    }

    /**
     * Checks the existence, and optionally the accessibility, of a file.
     *
     *
     *  This method may be used by the [isReadable][Files.isReadable],
     * [isWritable][Files.isWritable] and [ isExecutable][Files.isExecutable] methods to check the accessibility of a file.
     *
     *
     *  This method checks the existence of a file and that this Java virtual
     * machine has appropriate privileges that would allow it access the file
     * according to all of access modes specified in the `modes` parameter
     * as follows:
     *
     * <table class="striped">
     * <caption style="display:none">Access Modes</caption>
     * <thead>
     * <tr> <th scope="col">Value</th> <th scope="col">Description</th> </tr>
    </thead> *
     * <tbody>
     * <tr>
     * <th scope="row"> [READ][AccessMode.READ] </th>
     * <td> Checks that the file exists and that the Java virtual machine has
     * permission to read the file. </td>
    </tr> *
     * <tr>
     * <th scope="row"> [WRITE][AccessMode.WRITE] </th>
     * <td> Checks that the file exists and that the Java virtual machine has
     * permission to write to the file, </td>
    </tr> *
     * <tr>
     * <th scope="row"> [EXECUTE][AccessMode.EXECUTE] </th>
     * <td> Checks that the file exists and that the Java virtual machine has
     * permission to [execute][Runtime.exec] the file. The semantics
     * may differ when checking access to a directory. For example, on UNIX
     * systems, checking for `EXECUTE` access checks that the Java
     * virtual machine has permission to search the directory in order to
     * access file or subdirectories. </td>
    </tr> *
    </tbody> *
    </table> *
     *
     *
     *  If the `modes` parameter is of length zero, then the existence
     * of the file is checked.
     *
     *
     *  This method follows symbolic links if the file referenced by this
     * object is a symbolic link. Depending on the implementation, this method
     * may require to read file permissions, access control lists, or other
     * file attributes in order to check the effective access to the file. To
     * determine the effective access to a file may require access to several
     * attributes and so in some implementations this method may not be atomic
     * with respect to other file system operations.
     *
     * @param   path
     * the path to the file to check
     * @param   modes
     * The access modes to check; may have zero elements
     *
     * @throws  UnsupportedOperationException
     * an implementation is required to support checking for
     * `READ`, `WRITE`, and `EXECUTE` access. This
     * exception is specified to allow for the `Access` enum to
     * be extended in future releases.
     * @throws  NoSuchFileException
     * if a file does not exist *(optional specific exception)*
     * @throws  AccessDeniedException
     * the requested access would be denied or the access cannot be
     * determined because the Java virtual machine has insufficient
     * privileges or other reasons. *(optional specific exception)*
     * @throws  IOException
     * if an I/O error occurs
     * @throws  SecurityException
     * In the case of the default provider, and a security manager is
     * installed, the [checkRead][SecurityManager.checkRead]
     * is invoked when checking read access to the file or only the
     * existence of the file, the [          checkWrite][SecurityManager.checkWrite] is invoked when checking write access to the file,
     * and [checkExec][SecurityManager.checkExec] is invoked
     * when checking execute access.
     */
    override fun checkAccess(path: Path?, vararg modes: AccessMode?) {
        TODO("Not yet implemented")
    }

    /**
     * Returns a file attribute view of a given type. This method works in
     * exactly the manner specified by the [Files.getFileAttributeView]
     * method.
     *
     * @param   <V>
     * The `FileAttributeView` type
     * @param   path
     * the path to the file
     * @param   type
     * the `Class` object corresponding to the file attribute view
     * @param   options
     * options indicating how symbolic links are handled
     *
     * @return  a file attribute view of the specified type, or `null` if
     * the attribute view type is not available
    </V> */
    override fun <V : FileAttributeView?> getFileAttributeView(
        path: Path?,
        type: Class<V>?,
        vararg options: LinkOption?
    ): V {
        TODO("Not yet implemented")
    }

    /**
     * Reads a file's attributes as a bulk operation. This method works in
     * exactly the manner specified by the [ ][Files.readAttributes] method.
     *
     * @param   <A>
     * The `BasicFileAttributes` type
     * @param   path
     * the path to the file
     * @param   type
     * the `Class` of the file attributes required
     * to read
     * @param   options
     * options indicating how symbolic links are handled
     *
     * @return  the file attributes
     *
     * @throws  UnsupportedOperationException
     * if an attributes of the given type are not supported
     * @throws  IOException
     * if an I/O error occurs
     * @throws  SecurityException
     * In the case of the default provider, a security manager is
     * installed, its [checkRead][SecurityManager.checkRead]
     * method is invoked to check read access to the file
    </A> */
    override fun <A : BasicFileAttributes?> readAttributes(
        path: Path?,
        type: Class<A>?,
        vararg options: LinkOption?
    ): A {
        TODO("Not yet implemented")
    }

    /**
     * Reads a set of file attributes as a bulk operation. This method works in
     * exactly the manner specified by the [ ][Files.readAttributes] method.
     *
     * @param   path
     * the path to the file
     * @param   attributes
     * the attributes to read
     * @param   options
     * options indicating how symbolic links are handled
     *
     * @return  a map of the attributes returned; may be empty. The map's keys
     * are the attribute names, its values are the attribute values
     *
     * @throws  UnsupportedOperationException
     * if the attribute view is not available
     * @throws  IllegalArgumentException
     * if no attributes are specified or an unrecognized attributes is
     * specified
     * @throws  IOException
     * If an I/O error occurs
     * @throws  SecurityException
     * In the case of the default provider, and a security manager is
     * installed, its [checkRead][SecurityManager.checkRead]
     * method denies read access to the file. If this method is invoked
     * to read security sensitive attributes then the security manager
     * may be invoke to check for additional permissions.
     */
    override fun readAttributes(
        path: Path?,
        attributes: String?,
        vararg options: LinkOption?
    ): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }

    /**
     * Sets the value of a file attribute. This method works in exactly the
     * manner specified by the [Files.setAttribute] method.
     *
     * @param   path
     * the path to the file
     * @param   attribute
     * the attribute to set
     * @param   value
     * the attribute value
     * @param   options
     * options indicating how symbolic links are handled
     *
     * @throws  UnsupportedOperationException
     * if the attribute view is not available
     * @throws  IllegalArgumentException
     * if the attribute name is not specified, or is not recognized, or
     * the attribute value is of the correct type but has an
     * inappropriate value
     * @throws  ClassCastException
     * If the attribute value is not of the expected type or is a
     * collection containing elements that are not of the expected
     * type
     * @throws  IOException
     * If an I/O error occurs
     * @throws  SecurityException
     * In the case of the default provider, and a security manager is
     * installed, its [checkWrite][SecurityManager.checkWrite]
     * method denies write access to the file. If this method is invoked
     * to set security sensitive attributes then the security manager
     * may be invoked to check for additional permissions.
     */
    override fun setAttribute(path: Path?, attribute: String?, value: Any?, vararg options: LinkOption?) {
        TODO("Not yet implemented")
    }
}
