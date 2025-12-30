package com.thundertools.di

import android.content.Context
import com.thundertools.data.local.database.AppDatabase
import com.thundertools.data.local.database.dao.AdminUserDao
import com.thundertools.data.local.database.repositories.AdminRepository
import com.thundertools.domain.usecases.LoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.init(context)
    }
    
    @Provides
    fun provideAdminUserDao(database: AppDatabase): AdminUserDao {
        return database.adminUserDao()
    }
    
    @Provides
    @Singleton
    fun provideAdminRepository(adminUserDao: AdminUserDao): AdminRepository {
        return AdminRepository(adminUserDao)
    }
    
    @Provides
    @Singleton
    fun provideLoginUseCase(adminRepository: AdminRepository): LoginUseCase {
        return LoginUseCase(adminRepository)
    }
}