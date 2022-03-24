package com.inetum.app;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.influx.InfluxDbOkHttpClientBuilderProvider;
import org.springframework.context.annotation.Configuration;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;

@Configuration
public class InfluxHttpConfig implements InfluxDbOkHttpClientBuilderProvider {

	@Override
	public Builder get() {
		return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS);
	}
}
