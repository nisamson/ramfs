package sh.nes.ramfs

@JvmInline
value class CIString(val string: String): Comparable<CIString>, CharSequence {
    /**
     * Returns the length of this character sequence.
     */
    override val length: Int
        get() = string.length

    /**
     * Returns the character at the specified [index] in this character sequence.
     *
     * @throws [IndexOutOfBoundsException] if the [index] is out of bounds of this character sequence.
     *
     * Note that the [String] implementation of this interface in Kotlin/JS has unspecified behavior
     * if the [index] is out of its bounds.
     */
    override fun get(index: Int): Char {
        return string[index]
    }

    /**
     * Returns a new character sequence that is a subsequence of this character sequence,
     * starting at the specified [startIndex] and ending right before the specified [endIndex].
     *
     * @param startIndex the start index (inclusive).
     * @param endIndex the end index (exclusive).
     */
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return string.subSequence(startIndex, endIndex)
    }

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: CIString): Int {
        return this.compareTo(other.string)
    }

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    fun compareTo(other: String): Int {
        return string.compareTo(other, true)
    }

}
