
package com.xcvi.stepcounter.service.sensor

import kotlin.math.sqrt

/**
 * A collection of matrix and vector operations used specifically for sensor
 * fusion.  These are purposefully specific so as to be fast.
 */
object SensorFusionMath {
    @JvmStatic
    fun sum(array: FloatArray): Float {
        var retval = 0f
        for (i in array.indices) {
            retval += array[i]
        }
        return retval
    }

    @JvmStatic
    fun cross(arrayA: FloatArray, arrayB: FloatArray): FloatArray {
        val retArray = FloatArray(3)
        retArray[0] = arrayA[1] * arrayB[2] - arrayA[2] * arrayB[1]
        retArray[1] = arrayA[2] * arrayB[0] - arrayA[0] * arrayB[2]
        retArray[2] = arrayA[0] * arrayB[1] - arrayA[1] * arrayB[0]
        return retArray
    }

    @JvmStatic
    fun norm(array: FloatArray): Float {
        var retval = 0f
        for (i in array.indices) {
            retval += array[i] * array[i]
        }
        return sqrt(retval.toDouble()).toFloat()
    }

    // Note: only works with 3D vectors.
    @JvmStatic
    fun dot(a: FloatArray, b: FloatArray): Float {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2]
    }

    @JvmStatic
    fun normalize(a: FloatArray): FloatArray {
        val retval = FloatArray(a.size)
        val norm = norm(a)
        for (i in a.indices) {
            retval[i] = a[i] / norm
        }
        return retval
    }
}