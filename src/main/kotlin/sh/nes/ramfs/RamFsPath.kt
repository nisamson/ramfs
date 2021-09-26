package sh.nes.ramfs

import java.net.URI
import java.nio.file.*
import java.util.*

class RamFsPath(private val fs: RamFs, val components: List<CIString>, private val absolute: Boolean) : Path {

    constructor(fs: RamFs, first: String, vararg components: String):
            this(fs, componentize(sequenceOf(first, *components)).toList(), first.startsWith("/") || first.isEmpty())

    val isRoot = this.components.isEmpty() && absolute

    companion object {
        fun componentize(components: Sequence<String>): Sequence<CIString> {
            return components.map(this::componentizeString).flatten().map(::CIString)
        }

        fun componentizeString(part: String): Sequence<String> {
            return part.split("/").asSequence().filter(String::isNotEmpty)
        }
    }

    private fun rootDir() = RamFsPath(fs, "/")

    /**
     * Compares two abstract paths lexicographically. The ordering defined by
     * this method is provider specific, and in the case of the default
     * provider, platform specific. This method does not access the file system
     * and neither file is required to exist.
     *
     *
     *  This method may not be used to compare paths that are associated
     * with different file system providers.
     *
     * @param   other  the path compared to this path.
     *
     * @return  zero if the argument is [equal][.equals] to this path, a
     * value less than zero if this path is lexicographically less than
     * the argument, or a value greater than zero if this path is
     * lexicographically greater than the argument
     *
     * @throws  ClassCastException
     * if the paths are associated with different providers
     */
    override fun compareTo(other: Path): Int {
        if (other !is RamFsPath) {
            throw ClassCastException("Can only compare an RamFsPath against another RamFsPath")
        }

        val
    }

    /**
     * Registers the file located by this path with a watch service.
     *
     *
     *  In this release, this path locates a directory that exists. The
     * directory is registered with the watch service so that entries in the
     * directory can be watched. The `events` parameter is the events to
     * register and may contain the following events:
     *
     *  * [ENTRY_CREATE][StandardWatchEventKinds.ENTRY_CREATE] -
     * entry created or moved into the directory
     *  * [ENTRY_DELETE][StandardWatchEventKinds.ENTRY_DELETE] -
     * entry deleted or moved out of the directory
     *  * [ENTRY_MODIFY][StandardWatchEventKinds.ENTRY_MODIFY] -
     * entry in directory was modified
     *
     *
     *
     *  The [context][WatchEvent.context] for these events is the
     * relative path between the directory located by this path, and the path
     * that locates the directory entry that is created, deleted, or modified.
     *
     *
     *  The set of events may include additional implementation specific
     * event that are not defined by the enum [StandardWatchEventKinds]
     *
     *
     *  The `modifiers` parameter specifies *modifiers* that
     * qualify how the directory is registered. This release does not define any
     * *standard* modifiers. It may contain implementation specific
     * modifiers.
     *
     *
     *  Where a file is registered with a watch service by means of a symbolic
     * link then it is implementation specific if the watch continues to depend
     * on the existence of the symbolic link after it is registered.
     *
     * @param   watcher
     * the watch service to which this object is to be registered
     * @param   events
     * the events for which this object should be registered
     * @param   modifiers
     * the modifiers, if any, that modify how the object is registered
     *
     * @return  a key representing the registration of this object with the
     * given watch service
     *
     * @throws  UnsupportedOperationException
     * if unsupported events or modifiers are specified
     * @throws  IllegalArgumentException
     * if an invalid combination of events or modifiers is specified
     * @throws  ClosedWatchServiceException
     * if the watch service is closed
     * @throws  NotDirectoryException
     * if the file is registered to watch the entries in a directory
     * and the file is not a directory  *(optional specific exception)*
     * @throws  IOException
     * if an I/O error occurs
     * @throws  SecurityException
     * In the case of the default provider, and a security manager is
     * installed, the [checkRead][SecurityManager.checkRead]
     * method is invoked to check read access to the file.
     */
    override fun register(
        watcher: WatchService,
        events: Array<out WatchEvent.Kind<*>>,
        vararg modifiers: WatchEvent.Modifier?
    ): WatchKey {
        TODO("Not yet implemented")
    }

    /**
     * Returns the file system that created this object.
     *
     * @return  the file system that created this object
     */
    override fun getFileSystem(): FileSystem {
        return this.fs
    }

    /**
     * Tells whether or not this path is absolute.
     *
     *
     *  An absolute path is complete in that it doesn't need to be combined
     * with other path information in order to locate a file.
     *
     * @return  `true` if, and only if, this path is absolute
     */
    override fun isAbsolute(): Boolean {
        return this.absolute
    }

    /**
     * Returns the root component of this path as a `Path` object,
     * or `null` if this path does not have a root component.
     *
     * @return  a path representing the root component of this path,
     * or `null`
     */
    override fun getRoot(): Path? {
        return if (this.absolute) { RamFsPath(fs, "/") } else { null }
    }

    /**
     * Returns the name of the file or directory denoted by this path as a
     * `Path` object. The file name is the *farthest* element from
     * the root in the directory hierarchy.
     *
     * @return  a path representing the name of the file or directory, or
     * `null` if this path has zero elements
     */
    override fun getFileName(): Path? {
        return this.components.lastOrNull()?.string?.let { s -> RamFsPath(this.fs, s) }
    }

    /**
     * Returns the *parent path*, or `null` if this path does not
     * have a parent.
     *
     *
     *  The parent of this path object consists of this path's root
     * component, if any, and each element in the path except for the
     * *farthest* from the root in the directory hierarchy. This method
     * does not access the file system; the path or its parent may not exist.
     * Furthermore, this method does not eliminate special names such as "."
     * and ".." that may be used in some implementations. On UNIX for example,
     * the parent of "`/a/b/c`" is "`/a/b`", and the parent of
     * `"x/y/.`" is "`x/y`". This method may be used with the [ ][.normalize] method, to eliminate redundant names, for cases where
     * *shell-like* navigation is required.
     *
     *
     *  If this path has more than one element, and no root component, then
     * this method is equivalent to evaluating the expression:
     * <blockquote><pre>
     * subpath(0,&nbsp;getNameCount()-1);
    </pre></blockquote> *
     *
     * @return  a path representing the path's parent
     */
    override fun getParent(): Path? {
        if (this.isRoot) {
            return null
        }

        if (components.size == 1) {
            return this.root
        }

        return this.subpath(0, this.nameCount - 1)
    }

    /**
     * Returns the number of name elements in the path.
     *
     * @return  the number of elements in the path, or `0` if this path
     * only represents a root component
     */
    override fun getNameCount(): Int {
        return this.components.size
    }

    /**
     * Returns a name element of this path as a `Path` object.
     *
     *
     *  The `index` parameter is the index of the name element to return.
     * The element that is *closest* to the root in the directory hierarchy
     * has index `0`. The element that is *farthest* from the root
     * has index [count][.getNameCount]`-1`.
     *
     * @param   index
     * the index of the element
     *
     * @return  the name element
     *
     * @throws  IllegalArgumentException
     * if `index` is negative, `index` is greater than or
     * equal to the number of elements, or this path has zero name
     * elements
     */
    override fun getName(index: Int): Path {
        val component = components.getOrNull(index) ?: throw IllegalArgumentException("Invalid path index: $index")
        return RamFsPath(fs, listOf(component), false)
    }

    /**
     * Returns a relative `Path` that is a subsequence of the name
     * elements of this path.
     *
     *
     *  The `beginIndex` and `endIndex` parameters specify the
     * subsequence of name elements. The name that is *closest* to the root
     * in the directory hierarchy has index `0`. The name that is
     * *farthest* from the root has index [ count][.getNameCount]`-1`. The returned `Path` object has the name elements
     * that begin at `beginIndex` and extend to the element at index `endIndex-1`.
     *
     * @param   beginIndex
     * the index of the first element, inclusive
     * @param   endIndex
     * the index of the last element, exclusive
     *
     * @return  a new `Path` object that is a subsequence of the name
     * elements in this `Path`
     *
     * @throws  IllegalArgumentException
     * if `beginIndex` is negative, or greater than or equal to
     * the number of elements. If `endIndex` is less than or
     * equal to `beginIndex`, or larger than the number of elements.
     */
    override fun subpath(beginIndex: Int, endIndex: Int): Path {
        if (beginIndex < 0 || endIndex <= beginIndex || endIndex > components.size) {
            throw IllegalArgumentException()
        }

        val components = components.asIterable().drop(beginIndex).take(endIndex - beginIndex).toList()
        return RamFsPath(fs, components, absolute)
    }

    /**
     * Tests if this path starts with the given path.
     *
     *
     *  This path *starts* with the given path if this path's root
     * component *starts* with the root component of the given path,
     * and this path starts with the same name elements as the given path.
     * If the given path has more name elements than this path then `false`
     * is returned.
     *
     *
     *  Whether or not the root component of this path starts with the root
     * component of the given path is file system specific. If this path does
     * not have a root component and the given path has a root component then
     * this path does not start with the given path.
     *
     *
     *  If the given path is associated with a different `FileSystem`
     * to this path then `false` is returned.
     *
     * @param   other
     * the given path
     *
     * @return  `true` if this path starts with the given path; otherwise
     * `false`
     */
    override fun startsWith(other: Path): Boolean {
        if (other !is RamFsPath || other.fs !== this.fs) {
            return false
        }

        if (this.isRoot && other.isRoot) {
            return true
        }

        if (this.isAbsolute && !other.isAbsolute) {
            return false
        }

        if (other.components.size > this.components.size) {
            return false
        }

        this.components.asSequence().zip(other.components.asSequence()).firstOrNull {
            (a, b) -> a != b
        } ?: return true

        return false
    }

    /**
     * Tests if this path ends with the given path.
     *
     *
     *  If the given path has *N* elements, and no root component,
     * and this path has *N* or more elements, then this path ends with
     * the given path if the last *N* elements of each path, starting at
     * the element farthest from the root, are equal.
     *
     *
     *  If the given path has a root component then this path ends with the
     * given path if the root component of this path *ends with* the root
     * component of the given path, and the corresponding elements of both paths
     * are equal. Whether or not the root component of this path ends with the
     * root component of the given path is file system specific. If this path
     * does not have a root component and the given path has a root component
     * then this path does not end with the given path.
     *
     *
     *  If the given path is associated with a different `FileSystem`
     * to this path then `false` is returned.
     *
     * @param   other
     * the given path
     *
     * @return  `true` if this path ends with the given path; otherwise
     * `false`
     */
    override fun endsWith(other: Path): Boolean {
        if (other !is RamFsPath || other.fs !== this.fs) {
            return false
        }

        if (this.isRoot && other.isRoot) {
            return true
        }

        if (!this.isAbsolute && other.isAbsolute) {
            return false
        }

        if (other.components.size > this.components.size) {
            return false
        }

        this.components.asReversed().asSequence().zip(other.components.asReversed().asSequence()).firstOrNull {
                (a, b) -> a != b
        } ?: return true

        return false
    }

    /**
     * Returns a path that is this path with redundant name elements eliminated.
     *
     *
     *  The precise definition of this method is implementation dependent but
     * in general it derives from this path, a path that does not contain
     * *redundant* name elements. In many file systems, the "`.`"
     * and "`..`" are special names used to indicate the current directory
     * and parent directory. In such file systems all occurrences of "`.`"
     * are considered redundant. If a "`..`" is preceded by a
     * non-"`..`" name then both names are considered redundant (the
     * process to identify such names is repeated until it is no longer
     * applicable).
     *
     *
     *  This method does not access the file system; the path may not locate
     * a file that exists. Eliminating "`..`" and a preceding name from a
     * path may result in the path that locates a different file than the original
     * path. This can arise when the preceding name is a symbolic link.
     *
     * @return  the resulting path or this path if it does not contain
     * redundant name elements; an empty path is returned if this path
     * does not have a root component and all name elements are redundant
     *
     * @see .getParent
     *
     * @see .toRealPath
     */
    override fun normalize(): Path {
        val outComponents = mutableListOf<CIString>()
        for (component in components) {
            when (component.string) {
                "." -> {}
                ".." -> if (outComponents.isNotEmpty()) { outComponents.removeLast() }
                else -> outComponents.add(component)
            }
        }

        if (outComponents.isEmpty() && !this.absolute) {
            return this.rootDir()
        }

        return RamFsPath(fs, outComponents, this.absolute)
    }

    /**
     * Resolve the given path against this path.
     *
     *
     *  If the `other` parameter is an [absolute][.isAbsolute]
     * path then this method trivially returns `other`. If `other`
     * is an *empty path* then this method trivially returns this path.
     * Otherwise this method considers this path to be a directory and resolves
     * the given path against this path. In the simplest case, the given path
     * does not have a [root][.getRoot] component, in which case this method
     * *joins* the given path to this path and returns a resulting path
     * that [ends][.endsWith] with the given path. Where the given path has
     * a root component then resolution is highly implementation dependent and
     * therefore unspecified.
     *
     * @param   other
     * the path to resolve against this path
     *
     * @return  the resulting path
     *
     * @see .relativize
     */
    override fun resolve(other: Path): Path {
        if (other !is RamFsPath) {

        }
    }

    /**
     * Constructs a relative path between this path and a given path.
     *
     *
     *  Relativization is the inverse of [resolution][.resolve].
     * This method attempts to construct a [relative][.isAbsolute] path
     * that when [resolved][.resolve] against this path, yields a
     * path that locates the same file as the given path. For example, on UNIX,
     * if this path is `"/a/b"` and the given path is `"/a/b/c/d"`
     * then the resulting relative path would be `"c/d"`. Where this
     * path and the given path do not have a [root][.getRoot] component,
     * then a relative path can be constructed. A relative path cannot be
     * constructed if only one of the paths have a root component. Where both
     * paths have a root component then it is implementation dependent if a
     * relative path can be constructed. If this path and the given path are
     * [equal][.equals] then an *empty path* is returned.
     *
     *
     *  For any two [normalized][.normalize] paths *p* and
     * *q*, where *q* does not have a root component,
     * <blockquote>
     * *p*`.relativize(`*p*
     * `.resolve(`*q*`)).equals(`*q*`)`
    </blockquote> *
     *
     *
     *  When symbolic links are supported, then whether the resulting path,
     * when resolved against this path, yields a path that can be used to locate
     * the [same][Files.isSameFile] file as `other` is implementation
     * dependent. For example, if this path is  `"/a/b"` and the given
     * path is `"/a/x"` then the resulting relative path may be `"../x"`. If `"b"` is a symbolic link then is implementation
     * dependent if `"a/b/../x"` would locate the same file as `"/a/x"`.
     *
     * @param   other
     * the path to relativize against this path
     *
     * @return  the resulting relative path, or an empty path if both paths are
     * equal
     *
     * @throws  IllegalArgumentException
     * if `other` is not a `Path` that can be relativized
     * against this path
     */
    override fun relativize(other: Path): Path {
        TODO("Not yet implemented")
    }

    /**
     * Returns a URI to represent this path.
     *
     *
     *  This method constructs an absolute [URI] with a [ ][URI.getScheme] equal to the URI scheme that identifies the
     * provider. The exact form of the scheme specific part is highly provider
     * dependent.
     *
     *
     *  In the case of the default provider, the URI is hierarchical with
     * a [path][URI.getPath] component that is absolute. The query and
     * fragment components are undefined. Whether the authority component is
     * defined or not is implementation dependent. There is no guarantee that
     * the `URI` may be used to construct a [java.io.File].
     * In particular, if this path represents a Universal Naming Convention (UNC)
     * path, then the UNC server name may be encoded in the authority component
     * of the resulting URI. In the case of the default provider, and the file
     * exists, and it can be determined that the file is a directory, then the
     * resulting `URI` will end with a slash.
     *
     *
     *  The default provider provides a similar *round-trip* guarantee
     * to the [java.io.File] class. For a given `Path` *p* it
     * is guaranteed that
     * <blockquote>
     * [Path.of]`(`*p*`.toUri()).equals(`*p*
     * `.`[toAbsolutePath][.toAbsolutePath]`())`
    </blockquote> *
     * so long as the original `Path`, the `URI`, and the new `Path` are all created in (possibly different invocations of) the same
     * Java virtual machine. Whether other providers make any guarantees is
     * provider specific and therefore unspecified.
     *
     *
     *  When a file system is constructed to access the contents of a file
     * as a file system then it is highly implementation specific if the returned
     * URI represents the given path in the file system or it represents a
     * *compound* URI that encodes the URI of the enclosing file system.
     * A format for compound URIs is not defined in this release; such a scheme
     * may be added in a future release.
     *
     * @return  the URI representing this path
     *
     * @throws  java.io.IOError
     * if an I/O error occurs obtaining the absolute path, or where a
     * file system is constructed to access the contents of a file as
     * a file system, and the URI of the enclosing file system cannot be
     * obtained
     *
     * @throws  SecurityException
     * In the case of the default provider, and a security manager
     * is installed, the [toAbsolutePath][.toAbsolutePath] method
     * throws a security exception.
     */
    override fun toUri(): URI {
        return URI(
            "${fs.provider().scheme}://${fs.name}/${this}"
        )
    }

    /**
     * Returns a `Path` object representing the absolute path of this
     * path.
     *
     *
     *  If this path is already [absolute][Path.isAbsolute] then this
     * method simply returns this path. Otherwise, this method resolves the path
     * in an implementation dependent manner, typically by resolving the path
     * against a file system default directory. Depending on the implementation,
     * this method may throw an I/O error if the file system is not accessible.
     *
     * @return  a `Path` object representing the absolute path
     *
     * @throws  java.io.IOError
     * if an I/O error occurs
     * @throws  SecurityException
     * In the case of the default provider, a security manager
     * is installed, and this path is not absolute, then the security
     * manager's [          checkPropertyAccess][SecurityManager.checkPropertyAccess] method is invoked to check access to the
     * system property `user.dir`
     */
    override fun toAbsolutePath(): Path {
        return if (this.isAbsolute) {
            this
        } else {
            rootDir().resolve(this)
        }
    }

    /**
     * Returns the *real* path of an existing file.
     *
     *
     *  The precise definition of this method is implementation dependent but
     * in general it derives from this path, an [absolute][.isAbsolute]
     * path that locates the [same][Files.isSameFile] file as this path, but
     * with name elements that represent the actual name of the directories
     * and the file. For example, where filename comparisons on a file system
     * are case insensitive then the name elements represent the names in their
     * actual case. Additionally, the resulting path has redundant name
     * elements removed.
     *
     *
     *  If this path is relative then its absolute path is first obtained,
     * as if by invoking the [toAbsolutePath][.toAbsolutePath] method.
     *
     *
     *  The `options` array may be used to indicate how symbolic links
     * are handled. By default, symbolic links are resolved to their final
     * target. If the option [NOFOLLOW_LINKS][LinkOption.NOFOLLOW_LINKS] is
     * present then this method does not resolve symbolic links.
     *
     * Some implementations allow special names such as "`..`" to refer to
     * the parent directory. When deriving the *real path*, and a
     * "`..`" (or equivalent) is preceded by a non-"`..`" name then
     * an implementation will typically cause both names to be removed. When
     * not resolving symbolic links and the preceding name is a symbolic link
     * then the names are only removed if it guaranteed that the resulting path
     * will locate the same file as this path.
     *
     * @param   options
     * options indicating how symbolic links are handled
     *
     * @return  an absolute path represent the *real* path of the file
     * located by this object
     *
     * @throws  IOException
     * if the file does not exist or an I/O error occurs
     * @throws  SecurityException
     * In the case of the default provider, and a security manager
     * is installed, its [checkRead][SecurityManager.checkRead]
     * method is invoked to check read access to the file, and where
     * this path is not absolute, its [          checkPropertyAccess][SecurityManager.checkPropertyAccess] method is invoked to check access to the
     * system property `user.dir`
     */
    override fun toRealPath(vararg options: LinkOption?): Path {
        return this.normalize().toAbsolutePath()
    }

}
