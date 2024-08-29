package com.xcvi.stepcounter

import android.app.Application
import androidx.room.Room
import com.xcvi.stepcounter.data.BioDatabase
import com.xcvi.stepcounter.data.StepsDao
import com.xcvi.stepcounter.data.StepsRepository
import com.xcvi.stepcounter.service.sensor.AccelerometerSensor
import com.xcvi.stepcounter.service.sensor.StepListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSensor(application: Application): AccelerometerSensor {
        return AccelerometerSensor(application)
    }

    @Provides
    @Singleton
    fun provideStepsDao(app: Application): StepsDao {
        return Room.databaseBuilder(
            app,
            BioDatabase::class.java,
            "bio_db"
        ).build().dao
    }

    @Provides
    @Singleton
    fun provideStepsRepository(stepsDao: StepsDao): StepsRepository {
        return StepsRepository(stepsDao)
    }
}











