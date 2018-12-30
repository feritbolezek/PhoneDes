package com.jonwid78.phone1


import android.Manifest
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

class HomeFragment : Fragment(), UserSetupGoalsListener, LocationUpdatedListener {

    private var todayValuesAdapter: RecyclerView.Adapter<TodayValueViewHolder>? = null
    private var todayValuesRecyclerView: RecyclerView? = null

    private var recordsAdapter: RecyclerView.Adapter<RecordViewHolder>? = null
    private var recordsRecyclerView: RecyclerView? = null

   // private var lastState = 0 Implemented later

    private var googleAPI: GoogleApiClient? = null

    private var viewModel: UserViewModel? = null

    companion object {
        val TAG = "HomeFragment"
        val PERMISSION_REQUEST_READ_LOCATION = 101
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        googleAPI = buildGoogleApiClient()
        return inflater!!.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity).get(UserViewModel::class.java)
        viewModel!!.locationUpdatedListener = this

        checkPermissions()

        checkForGoals()

        dateTextSwitcher.setInAnimation(context, android.R.anim.fade_in)
        dateTextSwitcher.setOutAnimation(context, android.R.anim.fade_out)

        val todayValuesLayoutManager = LinearLayoutManager(context)
        val recordsLayoutManager = LinearLayoutManager(context)

        todayValuesLayoutManager.orientation = OrientationHelper.HORIZONTAL
        todayValuesLayoutManager.reverseLayout = true

        todayValuesRecyclerView = todayValuesRV
        todayValuesRecyclerView?.layoutManager = todayValuesLayoutManager
        recordsRecyclerView = recordsRV
        recordsRecyclerView?.layoutManager = recordsLayoutManager



        todayValuesAdapter = TodayValuesAdapter(context)
        todayValuesRecyclerView?.adapter = todayValuesAdapter
        (todayValuesAdapter as TodayValuesAdapter).viewModel = viewModel


        recordsAdapter = RecordsAdapter(context)
        recordsRecyclerView?.adapter = recordsAdapter
        (recordsAdapter as RecordsAdapter).viewModel = viewModel

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(todayValuesRecyclerView)

        todayValuesRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

/*                val focusedView = snapHelper.findSnapView(todayValuesLayoutManager)
                val position = todayValuesRecyclerView!!.getChildLayoutPosition(focusedView)

                if (lastState != position) {
                    lastState = position
                    val focusedView = snapHelper.findSnapView(todayValuesLayoutManager)
                    val position = todayValuesRecyclerView!!.getChildLayoutPosition(focusedView)
                    setDateText(position)
                }*/ // Implemented Later.
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_READ_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel!!.requestLocationUpdates()
            }
        }
    }

    override fun locationUpdated(distance: Float, speed: Float, time: Long, highestSpeed: Float) { // remove params if not needed later

        val dateTime = Calendar.getInstance().time

        val id = ("${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)}" +
                "${Calendar.getInstance().get(Calendar.MONTH) + 1}" +
                "${Calendar.getInstance().get(Calendar.YEAR)}").toInt()

        viewModel?.saveEndResults(EndResults(id, distance, speed, time, dateTime))
        todayValuesRecyclerView!!.adapter.notifyDataSetChanged()
    }

    private fun checkForGoals() {
        viewModel!!.loadGoals().observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                viewModel!!.goals = it
                todayValuesRecyclerView!!.adapter.notifyDataSetChanged()

            } else {
                val dialog = GoalsFragment()
                dialog.goalsListener = this
                dialog.show(fragmentManager, null)
                dialog.isCancelable = false
            }
        })
    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_READ_LOCATION)
    }

    private fun checkPermissions() {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

        } else if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(context)
                        .setMessage("Access to location required for speed measurements and distance traveled data, try again?")
                        .setTitle("Permissions required")
                        .setPositiveButton("Try again", { _, _ ->
                            requestLocationPermission()
                        }).setNegativeButton("Cancel", { _, _ ->
                    activity.finish()
                }).create().show()
            } else {
                requestLocationPermission()
            }
        }
    }

    private fun buildGoogleApiClient(): GoogleApiClient {
        val client = GoogleApiClient.Builder(context).addApi(LocationServices.API).build()
        client.connect()
        return client
    }

    override fun goalsWereSetUp(distance: Goal, speed: Goal, time: Goal) {
        viewModel!!.saveGoals(distance)
        viewModel!!.saveGoals(speed)
        viewModel!!.saveGoals(time)
        checkPermissions()
    }

/*    fun setDateText(snappedViewPosition: Int) { Implementer later.
        when (snappedViewPosition) {
            0 -> dateTextSwitcher.setText("Today")
            1 -> dateTextSwitcher.setText("Yesterday")
            2 -> dateTextSwitcher.setText("2 days ago")
            else -> {
                dateTextSwitcher.setText("Your values")
            }
        }
    }*/


    class TodayValuesAdapter(val context: Context) : RecyclerView.Adapter<TodayValueViewHolder>() {
        var viewModel: UserViewModel? = null

        override fun onBindViewHolder(holder: TodayValueViewHolder?, position: Int) {
            setValues(holder)
            updateCharts(holder)
        }

        override fun getItemCount(): Int = 1

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TodayValueViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.today_values_card, parent, false)
            return TodayValueViewHolder(view)
        }

        fun setValues(holder: TodayValueViewHolder?) {
            val distanceGoal = viewModel?.retrieveGoal(GoalType.DISTANCE)?.value ?: 1.0f
            val speedGoal = viewModel?.retrieveGoal(GoalType.SPEED)?.value ?: 1.0f
            val timeGoal = viewModel?.retrieveGoal(GoalType.TIME)?.value ?: 1.0f

            holder?.distanceLabel!!.text = context.getString(R.string.distance_amount, (viewModel?.currentDistance!!) / 1000, distanceGoal)
            holder.speedLabel.text = context.getString(R.string.mph_amount, viewModel?.currentSpeed!!, speedGoal)
            holder.timeLabel.text = context.getString(R.string.min_amount, (viewModel?.currentTime!!.toFloat()) / 60, timeGoal)
        }

        fun updateCharts(holder: TodayValueViewHolder?) {
            val distanceGoal = viewModel?.retrieveGoal(GoalType.DISTANCE)?.value ?: 1.0f
            val timeGoal = viewModel?.retrieveGoal(GoalType.TIME)?.value ?: 1.0f

            holder?.distanceDonutChart!!.progressValue = (viewModel?.currentDistance!!) / (1000 * distanceGoal) // divided by currentDistance / 1000 * distanceGoal
            holder.speedDonutChart.progressValue = viewModel?.currentSpeed!!
            holder.timeDonutChart.progressValue = viewModel?.currentTime!!.toFloat() / (36 * timeGoal) // divided by currentTime / 36 * timeGoal

            holder.distanceDonutChart.invalidate()
            holder.speedDonutChart.invalidate()
            holder.timeDonutChart.invalidate()  // 100 -> 60 / 60 -> 1 %
        }
    }

    class RecordsAdapter(val context: Context) : RecyclerView.Adapter<RecordViewHolder>() {
        var viewModel: UserViewModel? = null

        override fun onBindViewHolder(holder: RecordViewHolder?, position: Int) {
            viewModel?.loadInRecords { highestDistance, highestSpeed, highestTime ->

                val recordDistance = highestDistance?.distance ?: 0.0f
                val recordSpeed = highestSpeed?.speed ?: 0.0f
                val recordTime = highestTime?.time ?: 0.0f


                holder?.distanceRecordLabel!!.text = context.getString(R.string.kilometers, recordDistance / 1000)
                holder.speedRecordLabel.text = context.getString(R.string.speed_record, recordSpeed)
                holder.timeRecordLabel.text = context.getString(R.string.hours, (recordTime.toString().toFloat() / 60) / 60)

                if (highestDistance != null) {
                    val highestDistanceDate = Calendar.getInstance()
                    highestDistanceDate.time = highestDistance.date
                    val dateBuildHighestDistance = "${highestDistanceDate.get(Calendar.DAY_OF_MONTH)}-" +
                            "${highestDistanceDate.get(Calendar.MONTH) + 1}-" +
                            "${highestDistanceDate.get(Calendar.YEAR)}"
                    holder.distanceRecordAchieved.text = context.getString(R.string.Achieved, dateBuildHighestDistance)
                } else {
                    holder.distanceRecordAchieved.text = ""
                }
                if (highestSpeed != null) {

                    val highestSpeedDate = Calendar.getInstance()
                    highestSpeedDate.time = highestSpeed.date
                    val dateBuildhighestSpeed = "${highestSpeedDate.get(Calendar.DAY_OF_MONTH)}-" +
                            "${highestSpeedDate.get(Calendar.MONTH) + 1}-" +
                            "${highestSpeedDate.get(Calendar.YEAR)}"
                    holder.speedRecordAchieved.text = context.getString(R.string.Achieved, dateBuildhighestSpeed)
                } else {
                    holder.speedRecordAchieved.text = ""
                }

                if (highestTime != null) {
                    val highestTimeDate = Calendar.getInstance()
                    highestTimeDate.time = highestTime.date
                    val dateBuildhighestTime = "${highestTimeDate.get(Calendar.DAY_OF_MONTH)}-" +
                            "${highestTimeDate.get(Calendar.MONTH) + 1}-" +
                            "${highestTimeDate.get(Calendar.YEAR)}"
                    holder.timeRecordAchieved.text = context.getString(R.string.Achieved, dateBuildhighestTime)
                } else {
                    holder.timeRecordAchieved.text = ""
                }

            }
        }

        override fun getItemCount(): Int = 1

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecordViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.record_item, parent, false)
            return RecordViewHolder(view)
        }
    }


}// Required empty public constructor

class TodayValueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var distanceLabel: TextView = itemView.findViewById(R.id.distanceLabelTextView)
    var speedLabel: TextView = itemView.findViewById(R.id.speedLabelTextView)
    var timeLabel: TextView = itemView.findViewById(R.id.timeLabelTextView)

    val distanceDonutChart: DonutChart = itemView.findViewById(R.id.distanceDonutChart)
    val speedDonutChart: DonutChart = itemView.findViewById(R.id.speedDonutChart)
    val timeDonutChart: DonutChart = itemView.findViewById(R.id.timeDonutChart)


}

class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var distanceRecordLabel: TextView = itemView.findViewById(R.id.distanceRecordLabel)
    var speedRecordLabel: TextView = itemView.findViewById(R.id.speedRecordLabel)
    var timeRecordLabel: TextView = itemView.findViewById(R.id.timeRecordLabel)

    var distanceRecordAchieved: TextView = itemView.findViewById(R.id.distanceRecordAchievedLabel)
    var speedRecordAchieved: TextView = itemView.findViewById(R.id.speedRecordAchievedLabel)
    var timeRecordAchieved: TextView = itemView.findViewById(R.id.timeRecordAchievedLabel)
}
