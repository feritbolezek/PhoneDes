package com.jonwid78.phone1


import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_goals.*


/**
 * A simple [Fragment] subclass.
 */
class GoalsFragment : DialogFragment() {

    private var distanceGoal = 0
    private var speedGoal = 0
    private var timeGoal = 0
    private var currentGoal = GoalType.DISTANCE
    var goalsListener: UserSetupGoalsListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_goals, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        goalCurrentSelectionTextView.text = context.getString(R.string.kilometers_per_day, 0.0f)

        goalChangeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

            override fun onProgressChanged(seekBar: SeekBar?, value: Int, p2: Boolean) {

                when (currentGoal) {
                    GoalType.DISTANCE -> {
                        goalCurrentSelectionTextView.text = context.getString(R.string.kilometers_per_day, value.toFloat())
                    }
                    GoalType.SPEED -> {
                        goalCurrentSelectionTextView.text = context.getString(R.string.average_speed_per_day, value.toFloat())
                    }
                    GoalType.TIME -> {
                        goalCurrentSelectionTextView.text = context.getString(R.string.hours_per_day_label, value.toFloat())
                    }
                }
            }
        })

        continueButton.setOnClickListener {
            updateUI()
        }



        super.onViewCreated(view, savedInstanceState)
    }


    private fun updateUI() {
        when (currentGoal) {
            GoalType.DISTANCE -> {
                currentGoal = GoalType.SPEED
                currentGoalProgress.progress = 2
                goalsSetupTitle.text = context.getString(R.string.how_fast_label)
                goalsSetupImage.setImageResource(R.drawable.speed)
                goalSetupDescription.text = context.getString(R.string.speed_label)
                goalCurrentSelectionTextView.text = context.getString(R.string.average_speed_per_day, 0.0f)
                distanceGoal = goalChangeSeekBar.progress
                goalChangeSeekBar.progress = 0
                goalChangeSeekBar.max = 45

            }
            GoalType.SPEED -> {
                currentGoal = GoalType.TIME
                currentGoalProgress.progress = 3
                goalsSetupTitle.text = context.getString(R.string.How_much_move_label)
                goalsSetupImage.setImageResource(R.drawable.timer)
                goalSetupDescription.text = context.getString(R.string.time_label)
                goalCurrentSelectionTextView.text = context.getString(R.string.hours_per_day_label, 0.0f)
                speedGoal = goalChangeSeekBar.progress
                goalChangeSeekBar.progress = 0
                goalChangeSeekBar.max = 24
            }
            GoalType.TIME -> {
                timeGoal = goalChangeSeekBar.progress
                val distance = Goal(0, "Distance", distanceGoal.toFloat())
                val speed = Goal(0,"Speed", speedGoal.toFloat())
                val time = Goal(0,"Time", timeGoal.toFloat())
                goalsListener?.goalsWereSetUp(distance,speed,time)
                dismiss()
            }
        }
    }

}// Required empty public constructor
