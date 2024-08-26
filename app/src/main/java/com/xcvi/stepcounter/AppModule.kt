package com.xcvi.stepcounter

import android.app.Application
import androidx.room.Room
import com.xcvi.stepcounter.service.MeasurableSensor
import com.xcvi.stepcounter.service.*
import com.xcvi.stepcounter.data.BioDatabase
import com.xcvi.stepcounter.data.StepsDao
import com.xcvi.stepcounter.data.StepsRepository
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
    fun provideSensor(application: Application): MeasurableSensor {
        return StepDetectorSensor(application)
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











