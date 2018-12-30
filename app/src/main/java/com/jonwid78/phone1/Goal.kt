package com.jonwid78.phone1

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

/**
 * Created by Ferit on 10-Oct-17.
 */

@Entity(tableName = "Goal")
data class Goal(@PrimaryKey(autoGenerate = true) val id: Int, @ColumnInfo(name = "goal_type") val goalType: String, val value: Float)


@Dao interface GoalDao {

    @Query("SELECT * FROM Goal")
    fun loadGoals() : LiveData<List<Goal>>

    @Query("DELETE FROM Goal")
    fun nukeUserGoals()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveGoal(goal: Goal)

}
