package com.jonwid78.phone1

import android.Manifest
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.persistence.room.*
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by Ferit on 05-Oct-17.
 */

@Entity(tableName = "User")
data class User(@PrimaryKey(autoGenerate = true)var id: Int, var name: String, @Ignore var goals: List<Goal>) {
    constructor() : this(UNKNOWN, "", emptyList())
    companion object {
        val UNKNOWN = -1
    }
}

@Dao
interface UserDao {

    @Query("SELECT * FROM User")
    fun loadUser() : LiveData<User>

    @Query("DELETE FROM User")
    fun nukeUserData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(user: User)

}

@Database(entities = arrayOf(User::class, Goal::class, EndResults::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun goalDao(): GoalDao
    abstract fun endResultsDao(): EndResultsDao
}


class UserViewModel(application: Application) : AndroidViewModel(application), GoogleApiClient.ConnectionCallbacks {

    var user: User? = null // Immutable later

    var goals: List<Goal>? = null

    var currentDistance: Float = 0.0f
    var currentSpeed = 0.0f
    var currentTime = 0L

    private var database: AppDatabase
    private var userDao: UserDao
    private var goalDao: GoalDao
    private var endResultsDao: EndResultsDao

    private var tracking: Tracking

    var locationUpdatedListener: LocationUpdatedListener? = null

    private val executor : ExecutorService

    private val handler = Handler(Looper.getMainLooper())



    init {
        database = Room.databaseBuilder(application.applicationContext, AppDatabase::class.java, "phoneDesDatabase.db").build()
        userDao = database.userDao()
        goalDao = database.goalDao()
        endResultsDao = database.endResultsDao()
        executor = Executors.newSingleThreadExecutor()
        val client = GoogleApiClient.Builder(application).addConnectionCallbacks(this).addApi(LocationServices.API).build()
        client.connect()
        tracking = Tracking(application, client)
        Log.d("Client", "${client.isConnected}")
    }

    override fun onConnected(p0: Bundle?) {
        Log.d("Client", "Now connected.")
            loadTodayEndResults {
                tracking.startTracking (it,{ distance, speed, time ->
                    locationUpdatedListener?.locationUpdated(distance, speed, time, tracking.highestSpeed)
                    currentDistance = distance
                    currentTime = time
                    currentSpeed = speed
                })
            }


    }

    fun loadUser() : LiveData<User> = userDao.loadUser()

    fun loadGoals() : LiveData<List<Goal>> {
        return goalDao.loadGoals()
    }

    fun saveUser(user: User) {
        executor.execute {
            userDao.saveUser(user)
        }
    }

    fun saveGoals(goal: Goal) {
        executor.execute {
            goalDao.saveGoal(goal)
        }
    }

    fun nukeEverything() {
        executor.execute {
            userDao.nukeUserData()
            goalDao.nukeUserGoals()
            endResultsDao.nukeEndResults()
        }
    }

    fun nukeGoals() {
        executor.execute {
            goalDao.nukeUserGoals()
        }
    }

    fun nukeEndResults() {
        executor.execute {
            endResultsDao.nukeEndResults()
        }
    }


    fun saveEndResults(endResults: EndResults) {
        executor.execute {
            endResultsDao.saveEndResults(endResults)
        }
    }

    fun loadAllEndResults() : LiveData<List<EndResults>> {
        return  endResultsDao.loadAllEndResults()
    }

    fun loadEndResultsBetween(from: Date, to: Date) : LiveData<List<EndResults>> {
        return  endResultsDao.loadEndResultsBetweenDates(from, to)
    }

    fun loadInRecords(recordsCallback: (highestDistance: EndResults?, highestSpeed: EndResults?, highestTime: EndResults?) -> Unit) {
        executor.execute {
            val distance = endResultsDao.loadInDistanceRecord()
            val speed = endResultsDao.loadInSpeedRecord()
            val time = endResultsDao.loadInTimeRecord()

            recordsCallback(distance,speed,time)
        }
    }

    private fun loadTodayEndResults(result: (endResult: EndResults?) -> Unit) {
        val id = ("${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)}" +
                "${Calendar.getInstance().get(Calendar.MONTH) + 1}" +
                "${Calendar.getInstance().get(Calendar.YEAR)}").toInt()
            executor.execute {
                val endRes = endResultsDao.loadTodayEndResults(id)
                handler.post {
                    result(endRes)
                    Log.d("LOADED", "$endRes")
                }
            }
    }

    fun retrieveGoal(goalType: GoalType) : Goal? {
        var goal: Goal? = null
        Log.d("CALLED", "CALLEDlooking")
        goals?.forEach {
            Log.d("LOOKING", "FOUND FOR EACH ${it}")
            if (it.goalType == goalType.raw) {
                Log.d("LOOKING", "FOUND MATCH ${it}")
                goal = it
            }
        }
        return goal
    }

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }



    fun requestLocationUpdates() {
        Log.d("Client", "Manually requested updates.")
        loadTodayEndResults {
            tracking.startTracking (it,{ distance, speed, time ->
                locationUpdatedListener?.locationUpdated(distance, speed, time, tracking.highestSpeed)
                currentDistance = distance
                currentSpeed = speed
                currentTime = time
            })
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d("Client", "Connection was suspended.")
    }
}