package com.license.workguru_app.authentification.presentation.components

import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.license.workguru_app.AuthorizedActivity
import com.license.workguru_app.R
import com.license.workguru_app.authentification.data.repository.AuthRepository
import com.license.workguru_app.authentification.data.source.helper.GraphicOverlay
import com.license.workguru_app.authentification.data.source.helper.RectOverlay
import com.license.workguru_app.authentification.domain.use_case.log_in_with_face_recognition.FaceRecognitionViewModel
import com.license.workguru_app.authentification.domain.use_case.log_in_with_face_recognition.FaceRecognitionViewModelFactory
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.utils.ProfileUtil
import com.wonderkiln.camerakit.*
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class FaceRecFragment : Fragment() {
//    var _binding: FragmentFaceRecBinding? = null
//    val binding get() = _binding!!
    lateinit var waitingDialog: AlertDialog
    lateinit var detectBtn:Button
    lateinit var cameraView: CameraView
    lateinit var graphicOverlay: GraphicOverlay
    val sharedViewModel: SharedViewModel by activityViewModels()
    lateinit var faceRecognitionViewModel: FaceRecognitionViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val faceFactory = FaceRecognitionViewModelFactory(requireActivity(), AuthRepository())
        faceRecognitionViewModel = ViewModelProvider(this, faceFactory).get(FaceRecognitionViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_face_rec, container, false)
        detectBtn = view.findViewById(R.id.face_rec_accept_btn)
        cameraView = view.findViewById(R.id.face_rec_camera_view)
        graphicOverlay = view.findViewById(R.id.face_rec_graphic_overlay)

        handleThatBackPress()

        waitingDialog = SpotsDialog.Builder().setContext(context)
            .setMessage(getString(R.string.mPleaseWait))
            .setCancelable(false)
            .build()

        detectBtn.setOnClickListener {
            cameraView.start()
            cameraView.captureImage()
            graphicOverlay.clear()
        }

        cameraView.addCameraKitListener(object : CameraKitEventListener {
            override fun onEvent(p0: CameraKitEvent?) {
            }

            override fun onError(p0: CameraKitError?) {
            }

            override fun onImage(p0: CameraKitImage?) {
                waitingDialog.show()

                var bitmap = p0!!.bitmap
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.width, cameraView.height, false)
                cameraView.stop()
                runFaceDetector(bitmap)
            }

            override fun onVideo(p0: CameraKitVideo?) {

            }

        })

        return view
    }

    override fun onResume() {
        super.onResume()
        cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
        cameraView.start()
    }

    override fun onPause() {
        super.onPause()
        cameraView.stop()
    }

    private fun runFaceDetector(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val options = FirebaseVisionFaceDetectorOptions.Builder().build()
        val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)

        detector.detectInImage(image)
            .addOnSuccessListener { result -> processFaceResult(result,bitmap) }
            .addOnFailureListener{ e -> Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()}
    }

    private fun processFaceResult(result: List<FirebaseVisionFace>, image:Bitmap) {

        var count = 0
        for(face in result)
        {
            val bounds = face.boundingBox

            val rectOverlay = RectOverlay(graphicOverlay, bounds )
            graphicOverlay.add(rectOverlay)

            count++
        }
        waitingDialog.dismiss()
        Toast.makeText(context, String.format(getString(R.string.tDetectFaces), count), Toast.LENGTH_SHORT).show()
        if (count>=0){
            
            sharedViewModel.saveImage(image)
            lifecycleScope.launch {
                delay(1000)
                val imageURI = saveImageToInternalStorage(image)
                Toast.makeText(requireActivity(), "${imageURI?.path}", Toast.LENGTH_SHORT).show()
                if (imageURI != null){
                    if(faceRecognitionViewModel.loginWithFace(imageURI.path,sharedViewModel.faceEmail.value)){
                        Log.d("AUTH", "Login with email")
                        val intent = Intent(context, AuthorizedActivity::class.java).apply {
                            putExtra(AlarmClock.EXTRA_MESSAGE, "You are logged in!${faceRecognitionViewModel.access_token.value}")
                        }
                        startActivity(intent)
                    }
                }
                else {
                    Toast.makeText(requireActivity(), "Can't save your image!", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }
//    override fun onStart() {
//        super.onStart()
//        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
//        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
//        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
//    }
    private fun handleThatBackPress(){
        val callback: OnBackPressedCallback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.signInFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
    // Method to save an image to internal storage
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri? {

        // Get the context wrapper instance
        val wrapper = ContextWrapper(requireActivity())

        // Initializing a new file
        // The bellow line return a directory in internal storage
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)


        // Create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException){ // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image uri
        return Uri.parse(file.absolutePath)
    }

}