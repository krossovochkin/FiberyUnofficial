// Generated by Dagger (https://dagger.dev).
package com.krossovochkin.fiberyunofficial.api;

import com.krossovochkin.serialization.Serializer;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class ApiModule_SerializerFactory implements Factory<Serializer> {
  @Override
  public Serializer get() {
    return serializer();
  }

  public static ApiModule_SerializerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static Serializer serializer() {
    return Preconditions.checkNotNullFromProvides(ApiModule.serializer());
  }

  private static final class InstanceHolder {
    private static final ApiModule_SerializerFactory INSTANCE = new ApiModule_SerializerFactory();
  }
}