package com.license.workguru_app.authentification.presentation.components

import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.camerakit.type.CameraFacing
import com.google.android.material.button.MaterialButton
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.license.workguru_app.R
import com.license.workguru_app.authentification.data.source.helper.GraphicOverlay
import com.license.workguru_app.authentification.data.source.helper.RectOverlay
import com.license.workguru_app.authentification.presentation.SharedViewModel
import com.license.workguru_app.databinding.FragmentFaceRecBinding
import com.wonderkiln.camerakit.*
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FaceRecFragment : Fragment() {
//    var _binding: FragmentFaceRecBinding? = null
//    val binding get() = _binding!!
    lateinit var waitingDialog: AlertDialog
    lateinit var detectBtn:Button
    lateinit var cameraView: CameraView
    lateinit var graphicOverlay: GraphicOverlay
    val sharedViewModel: SharedViewModel by activityViewModels()

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
            .setMessage("Please wait...")
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
        Toast.makeText(context, String.format("Detect %d faces in pictures", count), Toast.LENGTH_SHORT).show()
        if (count>0){
            sharedViewModel.saveImage(image)
            lifecycleScope.launch {
                delay(1000)
                findNavController().navigate(R.id.signInFragment)
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

}