package com.example.travellelotralala.di

import com.example.travellelotralala.repository.AuthRepository
import com.example.travellelotralala.repository.FirebaseAuthRepository
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
    fun provideAuthRepository(): AuthRepository {
        return FirebaseAuthRepository()
    }
    
    // Thêm các providers khác khi cần
}