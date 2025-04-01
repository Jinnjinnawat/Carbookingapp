package com.example.carrental

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.carbookingapp.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

class MapQrFragment : Fragment() {

    private lateinit var mapImageView: ImageView
    private lateinit var qrCodeImage: ImageView
    private var carBookingId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map_qr, container, false)

        mapImageView = view.findViewById(R.id.mapImageView)
        qrCodeImage = view.findViewById(R.id.qrCodeImage)

        arguments?.let {
            carBookingId = it.getString("carBookingId", "CAR123456") // ค่า default
        }

        generateQRCode(carBookingId ?: "UNKNOWN")

        return view
    }

    private fun generateQRCode(text: String) {
        try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                text, BarcodeFormat.QR_CODE, 300, 300
            )
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            qrCodeImage.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        fun newInstance(carBookingId: String): MapQrFragment {
            val fragment = MapQrFragment()
            val args = Bundle()
            args.putString("carBookingId", carBookingId)
            fragment.arguments = args
            return fragment
        }
    }
}
