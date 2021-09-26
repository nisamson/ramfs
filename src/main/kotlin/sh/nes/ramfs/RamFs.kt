package sh.nes.ramfs

import java.nio.file.*
import java.nio.file.attribute.UserPrincipalLookupService
import java.nio.file.spi.FileSystemProvider
import java.util.concurrent.atomic.AtomicBoolean

class RamFs(private val provider: RamFsProvider, val name: String) : FileSystem() {

    private val closed = AtomicBoolean(false)
    var isClosed get() = closed.get(); private set(value) = closed.set(value)

    private val backReadOnly = AtomicBoolean(false)
    var readOnly get() = backReadOnly.get(); set(value) = closed.set(value)

    /**
     * Closes this file system.
     *
     *
     *  After a file system is closed then all subsequent access to the file
     * system, either by methods defined by this class or on objects associated
     * with this file system, throw [ClosedFileSystemException]. If the
     * file system is already closed then invoking this method has no effect.
     *
     *
     *  Closing a file system will close all open [ ], [directory-streams][DirectoryStream],
     * [watch-service][WatchService], and other closeable objects associated
     * with this file system. The [default][FileSystems.getDefault] file
     * system cannot be closed.
     *
     * @throws  IOException
     * If an I/O error occurs
     * @throws  UnsupportedOperationException
     * Thrown in the case of the default file system
     */
    override fun close() {
        isClosed = true
    }

    /**
     * Returns the provider that created this file system.
     *
     * @return  The provider that created this file system.
     */
    override fun provider(): FileSystemProvider {
        return provider
    }

    /**
     * Tells whether or not this file system is open.
     *
     *
     *  File systems created by the default provider are always open.
     *
     * @return  `true` if, and only if, this file system is open
     */
    override fun isOpen(): Boolean {
        return !isClosed
    }

    /**
     * Tells whether or not this file system allows only read-only access to
     * its file stores.
     *
     * @return  `true` if, and only if, this file system provides
     * read-only access
     */
    override fun isReadOnly(): Boolean {
        return readOnly
    }

    /**
     * Returns the name separator, represented as a string.
     *
     *
     *  The name separator is used to separate names in a path string. An
     * implementation may support multiple name separators in which case this
     * method returns an implementation specific *default* name separator.
     * This separator is used when creating path strings by invoking the [ ][Path.toString] method.
     *
     *
     *  In the case of the default provider, this method returns the same
     * separator as [java.io.File.separator].
     *
     * @return  The name separator
     */
    override fun getSeparator(): String {
        return "/"
    }

    /**
     * Returns an object to iterate over the paths of the root directories.
     *
     *
     *  A file system provides access to a file store that may be composed
     * of a number of distinct file hierarchies, each with its own top-level
     * root directory. Unless denied by the security manager, each element in
     * the returned iterator corresponds to the root directory of a distinct
     * file hierarchy. The order of the elements is not defined. The file
     * hierarchies may change during the lifetime of the Java virtual machine.
     * For example, in some implementations, the insertion of removable media
     * may result in the creation of a new file hierarchy with its own
     * top-level directory.
     *
     *
     *  When a security manager is installed, it is invoked to check access
     * to the each root directory. If denied, the root directory is not returned
     * by the iterator. In the case of the default provider, the [ ][SecurityManager.checkRead] method is invoked to check read access
     * to each root directory. It is system dependent if the permission checks
     * are done when the iterator is obtained or during iteration.
     *
     * @return  An object to iterate over the root directories
     */
    override fun getRootDirectories(): MutableIterable<Path> {
        return mutableListOf(RamFsPath(this, "/"))
    }

    /**
     * Returns an object to iterate over the underlying file stores.
     *
     *
     *  The elements of the returned iterator are the [ ] for this file system. The order of the elements is
     * not defined and the file stores may change during the lifetime of the
     * Java virtual machine. When an I/O error occurs, perhaps because a file
     * store is not accessible, then it is not returned by the iterator.
     *
     *
     *  In the case of the default provider, and a security manager is
     * installed, the security manager is invoked to check [ ]`("getFileStoreAttributes")`. If denied, then
     * no file stores are returned by the iterator. In addition, the security
     * manager's [SecurityManager.checkRead] method is invoked to
     * check read access to the file store's *top-most* directory. If
     * denied, the file store is not returned by the iterator. It is system
     * dependent if the permission checks are done when the iterator is obtained
     * or during iteration.
     *
     *
     *  **Usage Example:**
     * Suppose we want to print the space usage for all file stores:
     * <pre>
     * for (FileStore store: FileSystems.getDefault().getFileStores()) {
     * long total = store.getTotalSpace() / 1024;
     * long used = (store.getTotalSpace() - store.getUnallocatedSpace()) / 1024;
     * long avail = store.getUsableSpace() / 1024;
     * System.out.format("%-20s %12d %12d %12d%n", store, total, used, avail);
     * }
    </pre> *
     *
     * @return  An object to iterate over the backing file stores
     */
    override fun getFileStores(): MutableIterable<FileStore> {
        TODO("Not yet implemented")
    }

    /**
     * Returns the set of the [names][FileAttributeView.name] of the file
     * attribute views supported by this `FileSystem`.
     *
     *
     *  The [BasicFileAttributeView] is required to be supported and
     * therefore the set contains at least one element, "basic".
     *
     *
     *  The [ supportsFileAttributeView(String)][FileStore.supportsFileAttributeView] method may be used to test if an
     * underlying [FileStore] supports the file attributes identified by a
     * file attribute view.
     *
     * @return  An unmodifiable set of the names of the supported file attribute
     * views
     */
    override fun supportedFileAttributeViews(): MutableSet<String> {
        return mutableSetOf("basic")
    }

    /**
     * Converts a path string, or a sequence of strings that when joined form
     * a path string, to a `Path`. If `more` does not specify any
     * elements then the value of the `first` parameter is the path string
     * to convert. If `more` specifies one or more elements then each
     * non-empty string, including `first`, is considered to be a sequence
     * of name elements (see [Path]) and is joined to form a path string.
     * The details as to how the Strings are joined is provider specific but
     * typically they will be joined using the [ name-separator][.getSeparator] as the separator. For example, if the name separator is
     * "`/`" and `getPath("/foo","bar","gus")` is invoked, then the
     * path string `"/foo/bar/gus"` is converted to a `Path`.
     * A `Path` representing an empty path is returned if `first`
     * is the empty string and `more` does not contain any non-empty
     * strings.
     *
     *
     *  The parsing and conversion to a path object is inherently
     * implementation dependent. In the simplest case, the path string is rejected,
     * and [InvalidPathException] thrown, if the path string contains
     * characters that cannot be converted to characters that are *legal*
     * to the file store. For example, on UNIX systems, the NUL (&#92;u0000)
     * character is not allowed to be present in a path. An implementation may
     * choose to reject path strings that contain names that are longer than those
     * allowed by any file store, and where an implementation supports a complex
     * path syntax, it may choose to reject path strings that are *badly
     * formed*.
     *
     *
     *  In the case of the default provider, path strings are parsed based
     * on the definition of paths at the platform or virtual file system level.
     * For example, an operating system may not allow specific characters to be
     * present in a file name, but a specific underlying file store may impose
     * different or additional restrictions on the set of legal
     * characters.
     *
     *
     *  This method throws [InvalidPathException] when the path string
     * cannot be converted to a path. Where possible, and where applicable,
     * the exception is created with an [ index][InvalidPathException.getIndex] value indicating the first position in the `path` parameter
     * that caused the path string to be rejected.
     *
     * @param   first
     * the path string or initial part of the path string
     * @param   more
     * additional strings to be joined to form the path string
     *
     * @return  the resulting `Path`
     *
     * @throws  InvalidPathException
     * If the path string cannot be converted
     */
    override fun getPath(first: String, vararg more: String): Path {
        return RamFsPath(this, first, *more)
    }

    /**
     * Returns a `PathMatcher` that performs match operations on the
     * `String` representation of [Path] objects by interpreting a
     * given pattern.
     *
     * The `syntaxAndPattern` parameter identifies the syntax and the
     * pattern and takes the form:
     * <blockquote><pre>
     * *syntax***:***pattern*
    </pre></blockquote> *
     * where `':'` stands for itself.
     *
     *
     *  A `FileSystem` implementation supports the "`glob`" and
     * "`regex`" syntaxes, and may support others. The value of the syntax
     * component is compared without regard to case.
     *
     *
     *  When the syntax is "`glob`" then the `String`
     * representation of the path is matched using a limited pattern language
     * that resembles regular expressions but with a simpler syntax. For example:
     *
     * <table class="striped" style="text-align:left; margin-left:2em">
     * <caption style="display:none">Pattern Language</caption>
     * <thead>
     * <tr>
     * <th scope="col">Example
    </th> * <th scope="col">Description
    </th></tr> *
    </thead> *
     * <tbody>
     * <tr>
     * <th scope="row">`*.java`</th>
     * <td>Matches a path that represents a file name ending in `.java`</td>
    </tr> *
     * <tr>
     * <th scope="row">`*.*`</th>
     * <td>Matches file names containing a dot</td>
    </tr> *
     * <tr>
     * <th scope="row">`*.{java,class}`</th>
     * <td>Matches file names ending with `.java` or `.class`</td>
    </tr> *
     * <tr>
     * <th scope="row">`foo.?`</th>
     * <td>Matches file names starting with `foo.` and a single
     * character extension</td>
    </tr> *
     * <tr>
     * <th scope="row">`&#47;home&#47;*&#47;*`
    </th> * <td>Matches `&#47;home&#47;gus&#47;data` on UNIX platforms</td>
    </tr> *
     * <tr>
     * <th scope="row">`&#47;home&#47;**`
    </th> * <td>Matches `&#47;home&#47;gus` and
     * `&#47;home&#47;gus&#47;data` on UNIX platforms</td>
    </tr> *
     * <tr>
     * <th scope="row">`C:&#92;&#92;*`
    </th> * <td>Matches `C:&#92;foo` and `C:&#92;bar` on the Windows
     * platform (note that the backslash is escaped; as a string literal in the
     * Java Language the pattern would be `"C:&#92;&#92;&#92;&#92;*"`) </td>
    </tr> *
    </tbody> *
    </table> *
     *
     *
     *  The following rules are used to interpret glob patterns:
     *
     *
     *  *
     *
     * The `*` character matches zero or more [   characters][Character] of a [name][Path.getName] component without
     * crossing directory boundaries.
     *
     *  *
     *
     * The `**` characters matches zero or more [   characters][Character] crossing directory boundaries.
     *
     *  *
     *
     * The `?` character matches exactly one character of a
     * name component.
     *
     *  *
     *
     * The backslash character (`\`) is used to escape characters
     * that would otherwise be interpreted as special characters. The expression
     * `\\` matches a single backslash and "\{" matches a left brace
     * for example.
     *
     *  *
     *
     * The `[ ]` characters are a *bracket expression* that
     * match a single character of a name component out of a set of characters.
     * For example, `[abc]` matches `"a"`, `"b"`, or `"c"`.
     * The hyphen (`-`) may be used to specify a range so `[a-z]`
     * specifies a range that matches from `"a"` to `"z"` (inclusive).
     * These forms can be mixed so [abce-g] matches `"a"`, `"b"`,
     * `"c"`, `"e"`, `"f"` or `"g"`. If the character
     * after the `[` is a `!` then it is used for negation so `[!a-c]` matches any character except `"a"`, `"b"`, or `"c"`.
     *
     *  Within a bracket expression the `*`, `?` and `\`
     * characters match themselves. The (`-`) character matches itself if
     * it is the first character within the brackets, or the first character
     * after the `!` if negating.
     *
     *  *
     *
     * The `{ }` characters are a group of subpatterns, where
     * the group matches if any subpattern in the group matches. The `","`
     * character is used to separate the subpatterns. Groups cannot be nested.
     *
     *
     *  *
     *
     * Leading period`&#47;`dot characters in file name are
     * treated as regular characters in match operations. For example,
     * the `"*"` glob pattern matches file name `".login"`.
     * The [Files.isHidden] method may be used to test whether a file
     * is considered hidden.
     *
     *
     *  *
     *
     * All other characters match themselves in an implementation
     * dependent manner. This includes characters representing any [   ][FileSystem.getSeparator].
     *
     *  *
     *
     * The matching of [root][Path.getRoot] components is highly
     * implementation-dependent and is not specified.
     *
     *
     *
     *
     *  When the syntax is "`regex`" then the pattern component is a
     * regular expression as defined by the [java.util.regex.Pattern]
     * class.
     *
     *
     *   For both the glob and regex syntaxes, the matching details, such as
     * whether the matching is case sensitive, are implementation-dependent
     * and therefore not specified.
     *
     * @param   syntaxAndPattern
     * The syntax and pattern
     *
     * @return  A path matcher that may be used to match paths against the pattern
     *
     * @throws  IllegalArgumentException
     * If the parameter does not take the form: `syntax:pattern`
     * @throws  java.util.regex.PatternSyntaxException
     * If the pattern is invalid
     * @throws  UnsupportedOperationException
     * If the pattern syntax is not known to the implementation
     *
     * @see Files.newDirectoryStream
     */
    override fun getPathMatcher(syntaxAndPattern: String?): PathMatcher {
        TODO("Not yet implemented")
    }

    /**
     * Returns the `UserPrincipalLookupService` for this file system
     * *(optional operation)*. The resulting lookup service may be used to
     * lookup user or group names.
     *
     *
     *  **Usage Example:**
     * Suppose we want to make "joe" the owner of a file:
     * <pre>
     * UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
     * Files.setOwner(path, lookupService.lookupPrincipalByName("joe"));
    </pre> *
     *
     * @throws  UnsupportedOperationException
     * If this `FileSystem` does not does have a lookup service
     *
     * @return  The `UserPrincipalLookupService` for this file system
     */
    override fun getUserPrincipalLookupService(): UserPrincipalLookupService {
        throw UnsupportedOperationException()
    }

    /**
     * Constructs a new [WatchService] *(optional operation)*.
     *
     *
     *  This method constructs a new watch service that may be used to watch
     * registered objects for changes and events.
     *
     * @return  a new watch service
     *
     * @throws  UnsupportedOperationException
     * If this `FileSystem` does not support watching file system
     * objects for changes and events. This exception is not thrown
     * by `FileSystems` created by the default provider.
     * @throws  IOException
     * If an I/O error occurs
     */
    override fun newWatchService(): WatchService {
        throw UnsupportedOperationException()
    }

}
