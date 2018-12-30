package com.jonwid78.phone1

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import java.util.*

/**
 * Created by Ferit on 16-Oct-17.
 */

@Entity(tableName = "EndResults")
@TypeConverters(DateTypeConverter::class)
data class EndResults(@PrimaryKey(autoGenerate = false) val id: Int, val distance: Float, val speed: Float, val time: Long, val date: Date)


@Dao
@TypeConverters(DateTypeConverter::class)
interface EndResultsDao {

    @Query("SELECT * FROM EndResults")
    fun loadAllEndResults() : LiveData<List<EndResults>>

    @Query("SELECT * FROM EndResults WHERE date BETWEEN :from AND :to")
    fun loadEndResultsBetweenDates(from: Date, to: Date) : LiveData<List<EndResults>>

    @Query("DELETE FROM EndResults")
    fun nukeEndResults()

    @Query("SELECT * FROM EndResults WHERE ID = :id")
    fun loadTodayEndResults(id: Int) : EndResults?

    @Query("SELECT * FROM EndResults ORDER BY distance LIMIT 1")
    fun loadInDistanceRecord() : EndResults

    @Query("SELECT * FROM EndResults ORDER BY speed LIMIT 1")
    fun loadInSpeedRecord() : EndResults

    @Query("SELECT * FROM EndResults ORDER BY time LIMIT 1")
    fun loadInTimeRecord() : EndResults

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveEndResults(endResults: EndResults)

}