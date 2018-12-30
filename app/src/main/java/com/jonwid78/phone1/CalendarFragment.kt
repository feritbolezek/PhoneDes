package com.jonwid78.phone1


import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.util.*

class CalendarFragment : Fragment() {

    companion object {
        val TAG = "CalendarFragment"
    }

    private var recyclerView: RecyclerView? = null
    private var calendarRecyclerViewAdapter: RecyclerView.Adapter<CalendarItemViewHolder>? = null

    private var viewModel: UserViewModel? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity).get(UserViewModel::class.java)

        recyclerView = calendarRecyclerView
        calendarRecyclerViewAdapter = CalendarAdapter(context)

        recyclerView!!.adapter = calendarRecyclerViewAdapter
        val layoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager = layoutManager

        currentDateCalendarLabel.text = "${Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())}" +
                " ${Calendar.getInstance().get(Calendar.YEAR)}, Week " +
                "${Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)}"

        currentDateCalendarLabel.text = context.getString(R.string.calendar_date_label,Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()), Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)  )

        val startOfWeek = Calendar.getInstance()
        startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)
        val today = Calendar.getInstance()
        today.timeInMillis += 900_000

        loadWeekResults(startOfWeek.time, today.time)

        selectNewDateBtn.setOnClickListener {
            val datePickerDialog = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->

                val startDate = Calendar.getInstance()
                startDate.set(year,month,dayOfMonth)

                val endDate = Calendar.getInstance()
                endDate.set(year,month,dayOfMonth)
                endDate.add(Calendar.DAY_OF_WEEK, 6)

                currentDateCalendarLabel.text = "${startDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())}" +
                        " ${startDate.get(Calendar.YEAR)}, Week " +
                        "${startDate.get(Calendar.WEEK_OF_YEAR)}"

                loadWeekResults(startDate.time, endDate.time)

            },Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }



    }

    private fun loadWeekResults(startDate: Date, endDate: Date) {
         viewModel?.loadEndResultsBetween(startDate,endDate)?.observe(this, android.arch.lifecycle.Observer {
            if (it != null) {
                    (calendarRecyclerViewAdapter as CalendarAdapter).daysVisible = it.count()
                    (calendarRecyclerViewAdapter as CalendarAdapter).weeklyResults = it
                    calendarRecyclerViewAdapter?.notifyDataSetChanged()
            }

        })
    }

    class CalendarAdapter(val context: Context) : RecyclerView.Adapter<CalendarItemViewHolder>() {

        var daysVisible = 0
        var weeklyResults: List<EndResults> = emptyList()

        override fun onBindViewHolder(holder: CalendarItemViewHolder?, position: Int) {
            if (weeklyResults.isNotEmpty()) {
                val calendarDate = Calendar.getInstance()
                calendarDate.time = weeklyResults[position].date
                holder?.dateLabel!!.text = "${calendarDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())} " +
                        "${calendarDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} ${calendarDate.get(Calendar.DAY_OF_MONTH)}"

                val distance = weeklyResults[position].distance / 1000
                val speed = weeklyResults[position].speed
                Log.d(TAG, "${weeklyResults[position].time}")
                val time = (weeklyResults[position].time.toFloat() / 60) / 60

                holder.distanceLabel.text = context.getString(R.string.distance_calendar_label, distance)
                holder.speedLabel.text = context.getString(R.string.speed_calendar_label, speed)
                holder.TimeLabel.text = context.getString(R.string.time_calendar_label, time)


            }
        }

        override fun getItemCount(): Int = daysVisible

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CalendarItemViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.calendar_item,parent,false)
            return CalendarItemViewHolder(view)
        }
    }

}// Required empty public constructor

class CalendarItemViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView) {

    val dateLabel: TextView = itemView.findViewById(R.id.calendarItemDateLabel)
    val distanceLabel: TextView = itemView.findViewById(R.id.calendarItemDistanceLabel)
    val speedLabel: TextView = itemView.findViewById(R.id.calendarItemAvgSpeedLabel)
    val TimeLabel: TextView = itemView.findViewById(R.id.calendarItemTimeLabel)
}


