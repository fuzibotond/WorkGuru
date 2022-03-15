package com.license.workguru_app.authentification.presentation.components

import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.license.workguru_app.authentification.data.source.helper.RectOverlay
import com.license.workguru_app.databinding.FragmentFaceRecBinding
import com.wonderkiln.camerakit.*
import dmax.dialog.SpotsDialog


class FaceRecFragment : Fragment() {
    var _binding: FragmentFaceRecBinding? = null
    val binding get() = _binding!!
    lateinit var waitingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentFaceRecBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        binding.faceRecCameraView.start()
    }

    override fun onPause() {
        super.onPause()
        binding.faceRecCameraView.stop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        detectBtn = rootview.findViewById(R.id.face_rec_accept_btn)
//        cameraView = rootview.findViewById(R.id.face_rec_camera_view)
//        graphicOverlay = rootview.findViewById(R.id.face_rec_graphic_overlay)

        waitingDialog = SpotsDialog.Builder().setContext(context)
            .setMessage("Please wait...")
            .setCancelable(false)
            .build()

        binding.faceRecAcceptBtn.setOnClickListener {
            binding.faceRecCameraView.start()
            binding.faceRecCameraView.captureImage()
            binding.faceRecGraphicOverlay.clear()
        }

        binding.faceRecCameraView.addCameraKitListener(object : CameraKitEventListener {
            override fun onEvent(p0: CameraKitEvent?) {
            }

            override fun onError(p0: CameraKitError?) {
            }

            override fun onImage(p0: CameraKitImage?) {
                waitingDialog.show()

                var bitmap = p0!!.bitmap
                bitmap = Bitmap.createScaledBitmap(bitmap, binding.faceRecCameraView.width, binding.faceRecCameraView.height, false)
                binding.faceRecCameraView.stop()
                runFaceDetector(bitmap)
            }

            override fun onVideo(p0: CameraKitVideo?) {

            }

        })
    }
    private fun runFaceDetector(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val options = FirebaseVisionFaceDetectorOptions.Builder().build()
        val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)

        detector.detectInImage(image)
            .addOnSuccessListener { result -> processFaceResult(result) }
            .addOnFailureListener{ e -> Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()}
    }

    private fun processFaceResult(result: List<FirebaseVisionFace>) {

        var count = 0
        for(face in result)
        {
            val bounds = face.boundingBox

            val rectOverlay = RectOverlay(binding.faceRecGraphicOverlay, bounds )
            binding.faceRecGraphicOverlay.add(rectOverlay)

            count++
        }
        waitingDialog.dismiss()
        Toast.makeText(context, String.format("Detect %d faces in pictures", count), Toast.LENGTH_SHORT).show()
    }
//    override fun onStart() {
//        super.onStart()
//        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
//        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
//        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
//    }
//    private fun handleThatBackPress(){
//        val callback: OnBackPressedCallback = object: OnBackPressedCallback(true){
//            override fun handleOnBackPressed() {
//                findNavController().navigate(R.id.signUpFragment)
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
//    }

}