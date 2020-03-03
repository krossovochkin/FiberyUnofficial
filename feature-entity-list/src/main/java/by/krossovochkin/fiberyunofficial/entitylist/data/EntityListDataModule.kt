package by.krossovochkin.fiberyunofficial.entitylist.data

import android.content.Context
import by.krossovochkin.fiberyunofficial.core.data.api.FiberyServiceApi
import by.krossovochkin.fiberyunofficial.entitylist.domain.EntityListRepository
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides

@Module
object EntityListDataModule {

    @JvmStatic
    @Provides
    fun entityListRepository(
        fiberyServiceApi: FiberyServiceApi,
        entityListFiltersSortStorage: EntityListFiltersSortStorage
    ): EntityListRepository {
        return EntityListRepositoryImpl(fiberyServiceApi, entityListFiltersSortStorage)
    }

    @JvmStatic
    @Provides
    fun entityListFiltersStorage(
        context: Context,
        moshi: Moshi
    ): EntityListFiltersSortStorage {
        return EntityListFiltersSortStorageImpl(context, moshi)
    }

    @JvmStatic
    @Provides
    fun moshi(): Moshi {
        return Moshi.Builder().build()
    }
}
