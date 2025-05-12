package com.example.travellelotralala.di

import com.example.travellelotralala.repository.AuthRepository
import com.example.travellelotralala.repository.BookingRepository
import com.example.travellelotralala.repository.FirebaseAuthRepository
import com.example.travellelotralala.repository.HotelRepository
import com.example.travellelotralala.repository.SavedTripsRepository
import com.example.travellelotralala.repository.TripRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository {
        return FirebaseAuthRepository(auth, firestore)
    }
    
    @Provides
    @Singleton
    fun provideTripRepository(): TripRepository {
        return TripRepository()
    }
    
    @Provides
    @Singleton
    fun provideSavedTripsRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        tripRepository: TripRepository
    ): SavedTripsRepository {
        return SavedTripsRepository(firestore, auth, tripRepository)
    }
    
    @Provides
    @Singleton
    fun provideBookingRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): BookingRepository {
        return BookingRepository(firestore, auth)
    }
    
    @Provides
    @Singleton
    fun provideHotelRepository(
        firestore: FirebaseFirestore
    ): HotelRepository {
        return HotelRepository(firestore)
    }
}







