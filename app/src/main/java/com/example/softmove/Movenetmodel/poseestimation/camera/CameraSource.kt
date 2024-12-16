package org.tensorflow.lite.examples.poseestimation.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import kotlinx.coroutines.suspendCancellableCoroutine
import org.tensorflow.lite.examples.poseestimation.VisualizationUtils
import org.tensorflow.lite.examples.poseestimation.YuvToRgbConverter
import org.tensorflow.lite.examples.poseestimation.data.Person
import org.tensorflow.lite.examples.poseestimation.ml.PoseClassifier
import org.tensorflow.lite.examples.poseestimation.ml.PoseDetector
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import android.speech.tts.TextToSpeech
import org.tensorflow.lite.examples.poseestimation.data.PoseResult
import kotlin.math.roundToInt

class CameraSource(
    private val surfaceView: SurfaceView,
    private val listener: CameraSourceListener? = null
) {

    companion object {
        private const val PREVIEW_WIDTH = 640
        private const val PREVIEW_HEIGHT = 480

        /** Threshold for confidence score. */
        private const val MIN_CONFIDENCE = .2f
        private const val TAG = "Camera Source"
    }

    private val lock = Any()
    private var detector: PoseDetector? = null
    private var classifier: PoseClassifier? = null
    private var isTrackerEnabled = false
    private var yuvConverter: YuvToRgbConverter = YuvToRgbConverter(surfaceView.context)
    private lateinit var imageBitmap: Bitmap

    /** Frame count that have been processed so far in an one second interval to calculate FPS. */
    private var fpsTimer: Timer? = null
    private var frameProcessedInOneSecondInterval = 0
    private var framesPerSecond = 0

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val cameraManager: CameraManager by lazy {
        val context = surfaceView.context
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** Readers used as buffers for camera still shots */
    private var imageReader: ImageReader? = null

    /** The [CameraDevice] that will be opened in this fragment */
    private var camera: CameraDevice? = null

    /** Internal reference to the ongoing [CameraCaptureSession] configured with our parameters */
    private var session: CameraCaptureSession? = null

    /** [HandlerThread] where all buffer reading operations run */
    private var imageReaderThread: HandlerThread? = null

    /** [Handler] corresponding to [imageReaderThread] */
    private var imageReaderHandler: Handler? = null
    private var cameraId: String = ""

    /** Store the Pose Result */
    val result = PoseResult()

    /** Left and Right hand body points */
    private lateinit var right_shoulder_points : PointF
    private lateinit var right_elbow_points : PointF
    private lateinit var right_wrist_points : PointF
    private lateinit var left_shoulder_points : PointF
    private lateinit var left_elbow_points : PointF
    private lateinit var left_wrist_points : PointF

    /** Left and Right leg body points */
    private lateinit var right_hip_points : PointF
    private lateinit var right_knee_points : PointF
    private lateinit var right_ankle_points : PointF
    private lateinit var left_hip_points : PointF
    private lateinit var left_knee_points : PointF
    private lateinit var left_ankle_points : PointF

    suspend fun initCamera() {
        camera = openCamera(cameraManager, cameraId)
        imageReader =
            ImageReader.newInstance(PREVIEW_WIDTH, PREVIEW_HEIGHT, ImageFormat.YUV_420_888, 3)
        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()
            if (image != null) {
                if (!::imageBitmap.isInitialized) {
                    imageBitmap =
                        Bitmap.createBitmap(
                            PREVIEW_WIDTH,
                            PREVIEW_HEIGHT,
                            Bitmap.Config.ARGB_8888
                        )
                }
                yuvConverter.yuvToRgb(image, imageBitmap)
                // Create rotated version for portrait display
                val rotateMatrix = Matrix()
                rotateMatrix.postRotate(270.0f)

                val rotatedBitmap = Bitmap.createBitmap(
                    imageBitmap, 0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT,
                    rotateMatrix, false
                )
                processImage(rotatedBitmap)
                image.close()
            }
        }, imageReaderHandler)

        imageReader?.surface?.let { surface ->
            session = createSession(listOf(surface))
            val cameraRequest = camera?.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW
            )?.apply {
                addTarget(surface)
            }
            cameraRequest?.build()?.let {
                session?.setRepeatingRequest(it, null, null)
            }
        }
    }

    private suspend fun createSession(targets: List<Surface>): CameraCaptureSession =
        suspendCancellableCoroutine { cont ->
            camera?.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(captureSession: CameraCaptureSession) =
                    cont.resume(captureSession)

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    cont.resumeWithException(Exception("Session error"))
                }
            }, null)
        }

    @SuppressLint("MissingPermission")
    private suspend fun openCamera(manager: CameraManager, cameraId: String): CameraDevice =
        suspendCancellableCoroutine { cont ->
            manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) = cont.resume(camera)

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    if (cont.isActive) cont.resumeWithException(Exception("Camera error"))
                }
            }, imageReaderHandler)
        }

    fun prepareCamera() {
        for (cameraId in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)

            // We don't use a back facing camera in this sample.
            val cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (cameraDirection != null &&
                cameraDirection == CameraCharacteristics.LENS_FACING_BACK
            ) {
                continue
            }
            this.cameraId = cameraId
        }
    }

    fun setDetector(detector: PoseDetector) {
        synchronized(lock) {
            if (this.detector != null) {
                this.detector?.close()
                this.detector = null
            }
            this.detector = detector
        }
    }

    fun setClassifier(classifier: PoseClassifier?) {
        synchronized(lock) {
            if (this.classifier != null) {
                this.classifier?.close()
                this.classifier = null
            }
            this.classifier = classifier
        }
    }

    /**
     * Set Tracker for Movenet MuiltiPose model.
     */
//    fun setTracker(trackerType: TrackerType) {
//        isTrackerEnabled = trackerType != TrackerType.OFF
//        (this.detector as? MoveNetMultiPose)?.setTracker(trackerType)
//    }

    fun resume() {
        imageReaderThread = HandlerThread("imageReaderThread").apply { start() }
        imageReaderHandler = Handler(imageReaderThread!!.looper)
        fpsTimer = Timer()
        fpsTimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    framesPerSecond = frameProcessedInOneSecondInterval
                    frameProcessedInOneSecondInterval = 0
                }
            },
            0,
            1000
        )
    }

    fun close() {
        session?.close()
        session = null
        camera?.close()
        camera = null
        imageReader?.close()
        imageReader = null
        stopImageReaderThread()
        detector?.close()
        detector = null
        classifier?.close()
        classifier = null
        fpsTimer?.cancel()
        fpsTimer = null
        frameProcessedInOneSecondInterval = 0
        framesPerSecond = 0
    }

    /**
     * ANGLE DETECTION
     * */
    //Calculate Angle
    private fun calculate_angle(a : PointF, b: PointF, c: PointF): Double {
        //radians = Math.atan2(c[1]-b[1], c[0]-b[0]) - Math.atan2(a[1]-b[1], a[0]-b[0])
        var radians = Math.atan2((c.y - b.y).toDouble(), (c.x - b.x).toDouble()) - Math.atan2((a.y - b.y).toDouble(),
            (a.x - b.x).toDouble()
        )
        var angle = Math.abs(radians * 180.0 / Math.PI)
        if (angle > 180){
            angle = 360 - angle
        }
        return angle
    }

    /**
     * LEG STRETCHING EXERCISE
     * */
    private fun legStretching(person: Person){
        var angle1 : Int = 0
        var angle2 : Int = 0

        // Right Leg keypoints
        right_hip_points = person.keyPoints[12].coordinate
        right_knee_points = person.keyPoints[14].coordinate
        right_ankle_points = person.keyPoints[16].coordinate

        // Left leg Keypoints
        left_hip_points = person.keyPoints[11].coordinate
        left_knee_points = person.keyPoints[13].coordinate
        left_ankle_points = person.keyPoints[15].coordinate

        // Right Leg Keypoint's score
        if (person.keyPoints[12].score > 0.60 && person.keyPoints[14].score > 0.60 && person.keyPoints[16].score > 0.60){
            angle1 = calculate_angle(right_hip_points, right_knee_points, right_ankle_points).roundToInt()
        }

        // Left Arm Keypoint's score
        if (person.keyPoints[11].score > 0.60 && person.keyPoints[13].score > 0.60 && person.keyPoints[15].score > 0.60){
            angle2 = calculate_angle(left_hip_points, left_knee_points, left_ankle_points).roundToInt()
        }

//        angle1 = calculate_angle(right_hip_points, right_knee_points, right_ankle_points).roundToInt()
//        angle2 = calculate_angle(left_hip_points, left_knee_points, left_ankle_points).roundToInt()

        // RIGHT Leg EXERCISE
        if (angle1 >= 110){
            result.rightLegStretchingResult ="Right Leg Stretched."
        } else if (angle1 > 10){
            result.rightLegStretchingResult ="Right Leg Folded."
        }

        // LEFT Leg EXERCISE
        if (angle2 >= 110){
            result.leftLegStretchingResult ="Left Leg Stretched."
        } else if (angle2 > 10){
            result.leftLegStretchingResult ="Right Leg Folded."
        }

        // CALF Stretching Exercise
        if (angle1 <= 80 && angle2 >= 110){
            result.leftLegStretchingResult ="Legs Stretched."
        }

    }

    /**
     * HAND STRETCHING EXERCISE
     * */
    private fun handStretching(person : Person){
        var angle1 : Int = 0
        var angle2 : Int = 0

        // Right ARM keypoints
        right_shoulder_points = person.keyPoints[6].coordinate
        right_elbow_points = person.keyPoints[8].coordinate
        right_wrist_points = person.keyPoints[10].coordinate

        // Left ARM Keypoint's
        left_shoulder_points = person.keyPoints[5].coordinate
        left_elbow_points = person.keyPoints[7].coordinate
        left_wrist_points = person.keyPoints[9].coordinate

        // Right Arm Keypoint's score
        if (person.keyPoints[6].score > 0.60 && person.keyPoints[8].score > 0.60 && person.keyPoints[10].score > 0.60){
            angle1 = calculate_angle(right_shoulder_points, right_elbow_points, right_wrist_points).roundToInt()
        }

        // Left Arm Keypoint's score
        if (person.keyPoints[5].score > 0.60 && person.keyPoints[7].score > 0.60 && person.keyPoints[9].score > 0.60){
            angle2 = calculate_angle(left_shoulder_points, left_elbow_points, left_wrist_points).roundToInt()
        }

        // RIGHT ARM EXERCISE
        if (angle1 >= 110){
            result.rightHandStretchingResult ="Right Arm Stretched."
        } else if (angle1 > 10){
            result.rightHandStretchingResult ="Right Arm Folded."
        }

        // LEFT ARM EXERCISE
        if (angle2 >= 110){
            result.leftHandStretchingResult ="Left Arm Stretched."
       } else if (angle1 > 10){
            result.leftHandStretchingResult ="Left Arm Folded."
        }
    }

    // process image
    private fun processImage(bitmap: Bitmap) {
        val persons = mutableListOf<Person>()
        var classificationResult: List<Pair<String, Float>>? = null


        synchronized(lock) {
            detector?.estimatePoses(bitmap)?.let {
                persons.addAll(it)

                // if the model only returns one item, allow running the Pose classifier.
                if (persons.isNotEmpty()) {

                    if (result.exerciseType == "Yoga"){
                        classifier?.run {
                            classificationResult = classify(persons[0])
                        }
                    }else if (result.exerciseType == "Stretching"){
                        handStretching(persons[0])
                        legStretching(persons[0])
                    }
                    Log.i("Keypoints", persons[0].keyPoints.toString())
                }
            }
        }
        frameProcessedInOneSecondInterval++
        if (frameProcessedInOneSecondInterval == 1) {
            // send fps to view
            listener?.onFPSListener(framesPerSecond)
        }

        // if the model returns only one item, show that item's score.
        if (persons.isNotEmpty()) {
            listener?.onDetectedInfo(persons[0].score, classificationResult)
        }
        visualize(persons, bitmap)
    }

    private fun visualize(persons: List<Person>, bitmap: Bitmap) {

        val outputBitmap = VisualizationUtils.drawBodyKeypoints(
            bitmap,
            persons.filter { it.score > MIN_CONFIDENCE }, isTrackerEnabled
        )

        val holder = surfaceView.holder
        val surfaceCanvas = holder.lockCanvas()
        surfaceCanvas?.let { canvas ->
            val screenWidth: Int
            val screenHeight: Int
            val left: Int
            val top: Int

            if (canvas.height > canvas.width) {
                val ratio = outputBitmap.height.toFloat() / outputBitmap.width
                screenWidth = canvas.width
                left = 0
                screenHeight = (canvas.width * ratio).toInt()
                top = (canvas.height - screenHeight) / 2
            } else {
                val ratio = outputBitmap.width.toFloat() / outputBitmap.height
                screenHeight = canvas.height
                top = 0
                screenWidth = (canvas.height * ratio).toInt()
                left = (canvas.width - screenWidth) / 2
            }
            val right: Int = left + screenWidth
            val bottom: Int = top + screenHeight

            canvas.drawBitmap(
                outputBitmap, Rect(0, 0, outputBitmap.width, outputBitmap.height),
                Rect(left, top, right, bottom), null
            )
            surfaceView.holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun stopImageReaderThread() {
        imageReaderThread?.quitSafely()
        try {
            imageReaderThread?.join()
            imageReaderThread = null
            imageReaderHandler = null
        } catch (e: InterruptedException) {
            Log.d(TAG, e.message.toString())
        }
    }

    interface CameraSourceListener {
        fun onFPSListener(fps: Int)

        fun onDetectedInfo(personScore: Float?, poseLabels: List<Pair<String, Float>>?)
    }
}
