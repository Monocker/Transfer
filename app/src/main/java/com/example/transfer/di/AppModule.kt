package com.example.transfer.di

import com.example.transfer.data.repository.UserRepository
import com.example.transfer.data.source.remote.AuthService
import com.google.android.datatransport.runtime.dagger.Module
import com.google.android.datatransport.runtime.dagger.Provides

@Module
object AppModule {
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }
}
//@InstallIn(AppModule.SingletonComponent::class)
/*object AppModule {
    class SingletonComponent {

    }

    @Provides
    fun provideUserRepository(authService: AuthService): UserRepository {
        return UserRepository(authService)
    }
}*/




//annotation class InstallIn(val value: Any)
