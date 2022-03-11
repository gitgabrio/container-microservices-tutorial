@file:JvmName("Either")
package net.microservices.tutorial.interfaces

/**
 * Created by Gabriele Cardosi - gcardosi@cardosi.net on 22/09/16.
 */
class Either<L,R> private constructor(l: L?, r: R?) {

    private var left: L? = l
    private var right: R? = r

    companion object Factory {
        fun <L, R> left(l: L): Either<L, R> = Either(l, null)
        fun <L, R> right(r: R): Either<L, R> = Either(null, r)
    }

    fun left(): L?  {
        return left
    }

    fun right(): R?  {
        return right
    }

    fun isLeft(): Boolean = left != null

    fun isRight(): Boolean = right != null

//    fun fold(leftOption : F<L>, rightOption: F<R>) {
//
//    }
}