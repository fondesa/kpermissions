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
 * A custom [ConfigConfigurer] which merges the robolectric.properties with the annotation @Config on the tests methods/classes.
 * This is useful to avoid the automatic prioritization done by the annotation @Config on tests.
 * This fixes the following problem:
 * When a minSdk=22 is specified on a test, if the robolectric.properties contain minSdk=23, the minSdk=22 on test wins.
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
        val propertiesMinSdk = robolectricPropertiesConfig.minSdk
        val propertiesMaxSdk = robolectricPropertiesConfig.maxSdk
        var minSdk = if (minSdk == -1) propertiesMinSdk else max(propertiesMinSdk, minSdk)
        var maxSdk = if (maxSdk == -1) propertiesMaxSdk else min(propertiesMaxSdk, maxSdk)
        if (minSdk > maxSdk) {
            if (this.maxSdk == maxSdk) {
                // E.g. propertiesMinSdk is 19, propertiesMaxSdk is 21 and this.maxSdk is 17.
                // Since, maxSdk can't be 17 and minSdk 19, it makes minSdk 17.
                minSdk = maxSdk
            } else if (this.minSdk == minSdk) {
                // E.g. propertiesMinSdk is 16, propertiesMaxSdk is 19 and this.minSdk is 21.
                // Since, maxSdk can't be 19 and minSdk 21, it makes maxSdk 21.
                maxSdk = minSdk
            }
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
