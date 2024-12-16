package org.tensorflow.lite.examples.poseestimation.ml

import android.graphics.Bitmap
import org.tensorflow.lite.examples.poseestimation.data.Person

interface PoseDetector : AutoCloseable {

    fun estimatePoses(bitmap: Bitmap): List<Person>

    fun lastInferenceTimeNanos(): Long
}
