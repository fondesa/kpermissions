/*
 * Copyright (c) 2021 Giorgio Antonioli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fondesa.test

import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.pluginapi.config.Configurer
import org.robolectric.pluginapi.config.GlobalConfigProvider
import org.robolectric.plugins.ConfigConfigurer
import org.robolectric.plugins.HierarchicalConfigurationStrategy
import org.robolectric.plugins.PackagePropertiesLoader
import java.lang.reflect.Method
import java.util.Properties
import kotlin.math.max
import kotlin.math.min

/**
 * A custom [HierarchicalConfigurationStrategy] which loads [PrioritizePropsFileConfigurer] instead of [ConfigConfigurer].
 */
class PrioritizePropsFileConfigurationStrategy(vararg configurers: Configurer<*>) :
    HierarchicalConfigurationStrategy(*configurers.removeRobolectricConfigConfigurer())

/**
 * A custom [ConfigConfigurer] which prioritizes the robolectric.properties file instead of the annotation @Config on the tests methods.
 */
class PrioritizePropsFileConfigurer(packagePropertiesLoader: PackagePropertiesLoader, defaultConfigProvider: GlobalConfigProvider) :
    ConfigConfigurer(packagePropertiesLoader, defaultConfigProvider) {

    private val robolectricPropertiesConfig: Config by lazy {
        val properties = loadRobolectricProperties()
        Config.Implementation.fromProperties(properties)
    }

    override fun getConfigFor(testClass: Class<*>): Config? = super.getConfigFor(testClass)?.mergeWithProperties()

    override fun getConfigFor(method: Method): Config? = super.getConfigFor(method)?.mergeWithProperties()

    private fun loadRobolectricProperties(): Properties {
        val properties = Properties()
        val loader = Thread.currentThread().contextClassLoader ?: return properties
        loader.getResourceAsStream(RobolectricTestRunner.CONFIG_PROPERTIES).use { resourceStream -> properties.load(resourceStream) }
        return properties
    }

    private fun Config.mergeWithProperties(): Config {
        var minSdk = max(robolectricPropertiesConfig.minSdk, minSdk)
        val maxSdk = min(robolectricPropertiesConfig.maxSdk, maxSdk)
        if (minSdk > maxSdk) {
            minSdk = maxSdk
        }
        return Config.Builder()
            .overlay(this)
            .setMinSdk(minSdk)
            .setMaxSdk(maxSdk)
            .build()
    }
}

private fun Array<out Configurer<*>>.removeRobolectricConfigConfigurer(): Array<out Configurer<*>> = toMutableList()
    .filterNot { configurer -> configurer::class == ConfigConfigurer::class }
    .toTypedArray()
