package com.appback.appbacksdk.exceptions

import com.appback.appbacksdk.AppbackConstants.ROUTE_NOT_DEFINED

/**
 * Class that extends from [Exception]. This exception should be thrown when the user is performing
 * an operation on the library that requires a router to be previously defined.
 * @author - sapardo10
 * @since 0.0.1
 */
class RouterNotDefinedException : Exception() {
    //Message that explains the user what is the possible cause for this exception
    override val message: String = ROUTE_NOT_DEFINED
}