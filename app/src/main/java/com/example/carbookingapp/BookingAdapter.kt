package com.example.carbookingapp

import Booking
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

class BookingAdapter(private val bookingList: ArrayList<Booking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carModel: TextView = itemView.findViewById(R.id.text_view_car_model)
        val licensePlate: TextView = itemView.findViewById(R.id.text_view_license_plate)
        val nameSurname: TextView = itemView.findViewById(R.id.text_view_name_surname)
        val totalCost: TextView = itemView.findViewById(R.id.text_view_total_cost)
        val startDate: TextView = itemView.findViewById(R.id.text_view_start_date)
        val startTime: TextView = itemView.findViewById(R.id.text_view_start_time)
        val endDate: TextView = itemView.findViewById(R.id.text_view_end_date)
        val endTime: TextView = itemView.findViewById(R.id.text_view_end_time)
        val status: TextView = itemView.findViewById(R.id.text_view_status)
        val payButton: Button = itemView.findViewById(R.id.button_pay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_booking_detail, parent, false)
        return BookingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookingList[position]
        holder.carModel.text = "รุ่นรถ: ${booking.carModel}"
        holder.licensePlate.text = "ทะเบียน: ${booking.licensePlate}"
        holder.nameSurname.text = "ชื่อ: ${booking.name} ${booking.surname}"
        holder.totalCost.text = "ค่าใช้จ่าย: ${booking.totalCost}"
        holder.startDate.text = "วันที่เริ่ม: ${booking.startDate}"
        holder.startTime.text = "เวลาเริ่ม: ${booking.startTime}"
        holder.endDate.text = "วันที่สิ้นสุด: ${booking.endDate}"
        holder.endTime.text = "เวลาสิ้นสุด: ${booking.endTime}"
        holder.status.text = "สถานะ: ${booking.status}"

        if (booking.status == "approve") {
            holder.payButton.visibility = View.VISIBLE
            holder.payButton.setOnClickListener {
                val bookingDetailFragment = BookingDetailFragment()
                val bundle = Bundle()
                bundle.putString("bookingId", booking.documentId) // Pass the documentId to the fragment
                bookingDetailFragment.arguments = bundle
                (holder.itemView.context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, bookingDetailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        } else {
            holder.payButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }
}