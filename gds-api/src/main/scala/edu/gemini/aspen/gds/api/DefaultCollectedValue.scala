package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.FitsKeyword

/**
 * Message indicating the value collected by GDS was the default */
class DefaultCollectedValue[T](keyword: FitsKeyword, value: T, comment: String, index: Int)(override implicit val _type: FitsType[T]) extends CollectedValue[T](keyword, value, comment, index) {
    // Can only equal the same type
    override protected def canEqual(other: Any): Boolean = other.isInstanceOf[DefaultCollectedValue[_]]

    override def toString = "DefaultCollectedValue(" + keyword + ", " + value + ", " + comment + ", " + index + ")"
}

/**
 * Companion object used to place apply and unapply
 */
object DefaultCollectedValue {
    def apply[T](keyword: FitsKeyword, value: T, comment: String, index: Int)(implicit _type: FitsType[T]) = new DefaultCollectedValue[T](keyword, value, comment, index)

    def unapply(cv: DefaultCollectedValue[_]) = Option(cv.keyword, cv.value, cv.comment, cv.index)
}
