package io.quarkiverse.azure.app.configuration.deployment;

import java.util.List;

import com.azure.data.appconfiguration.implementation.ConfigurationSettingPage;
import com.azure.data.appconfiguration.models.ConfigurationSetting;

import io.quarkiverse.azure.app.configuration.AzureAppConfigurationConfigBuilder;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ExtensionSslNativeSupportBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigBuilderBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;

public class AzureAppConfigurationProcessor {
    @BuildStep
    public void feature(BuildProducer<FeatureBuildItem> feature) {
        feature.produce(new FeatureBuildItem("azure-app-configuration"));
    }

    @BuildStep
    public void enableSsl(BuildProducer<ExtensionSslNativeSupportBuildItem> extensionSslNativeSupport) {
        extensionSslNativeSupport.produce(new ExtensionSslNativeSupportBuildItem("azure-app-configuration"));
    }

    @BuildStep
    public void azureAppConfigurationConfigFactory(BuildProducer<RunTimeConfigBuilderBuildItem> runTimeConfigBuilder) {
        runTimeConfigBuilder.produce(new RunTimeConfigBuilderBuildItem(AzureAppConfigurationConfigBuilder.class.getName()));
    }

    @BuildStep
    void nativeImage(
            BuildProducer<RuntimeInitializedClassBuildItem> runtimeInitializedClasses,
            BuildProducer<NativeImageProxyDefinitionBuildItem> nativeImageProxyDefinitions,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) {

        nativeImageProxyDefinitions.produce(new NativeImageProxyDefinitionBuildItem(
                List.of("com.azure.data.appconfiguration.implementation.ConfigurationClientImpl$ConfigurationService")));

        reflectiveClasses.produce(new ReflectiveClassBuildItem(false, true,
                ConfigurationSettingPage.class,
                ConfigurationSetting.class));
    }
}
