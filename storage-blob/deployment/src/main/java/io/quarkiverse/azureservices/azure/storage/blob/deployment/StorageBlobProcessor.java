package io.quarkiverse.azureservices.azure.storage.blob.deployment;

import java.util.Arrays;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

import io.quarkiverse.azureservices.azure.storage.blob.runtime.StorageBlobServiceClientProducer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.ExtensionSslNativeSupportBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

class StorageBlobProcessor {

    static final String FEATURE = "azure-storage-blob";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem producer() {
        return new AdditionalBeanBuildItem(StorageBlobServiceClientProducer.class);
    }

    @BuildStep
    ExtensionSslNativeSupportBuildItem activateSslNativeSupport() {
        return new ExtensionSslNativeSupportBuildItem(FEATURE);
    }

    @BuildStep
    IndexDependencyBuildItem indexDependency() {
        return new IndexDependencyBuildItem("com.azure", "azure-storage-blob");
    }

    @BuildStep
    void reflectiveClasses(CombinedIndexBuildItem combinedIndex, BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) {
        String[] modelClasses = combinedIndex
                .getIndex()
                .getKnownClasses()
                .stream()
                .map(ClassInfo::name)
                .map(DotName::toString)
                .filter(n -> n.startsWith("com.azure.storage.blob.implementation.models.")
                        || n.startsWith("com.azure.storage.blob.models."))
                .sorted()
                .toArray(String[]::new);
        modelClasses = Arrays.copyOf(modelClasses, modelClasses.length + 1);
        modelClasses[modelClasses.length - 1] = "com.azure.core.http.rest.ResponseBase";
        reflectiveClasses.produce(new ReflectiveClassBuildItem(false, true, modelClasses));
    }

    @BuildStep
    NativeImageProxyDefinitionBuildItem proxies() {
        return new NativeImageProxyDefinitionBuildItem(
                "com.azure.storage.blob.implementation.ServicesImpl$ServicesService",
                "com.azure.storage.blob.implementation.ContainersImpl$ContainersService",
                "com.azure.storage.blob.implementation.BlobsImpl$BlobsService",
                "com.azure.storage.blob.implementation.PageBlobsImpl$PageBlobsService",
                "com.azure.storage.blob.implementation.BlockBlobsImpl$BlockBlobsService",
                "com.azure.storage.blob.implementation.AppendBlobsImpl$AppendBlobsService");
    }
}
