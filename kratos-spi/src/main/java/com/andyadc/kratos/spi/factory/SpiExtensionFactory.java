package com.andyadc.kratos.spi.factory;

import com.andyadc.kratos.spi.annotation.SPI;
import com.andyadc.kratos.spi.annotation.SPIClass;
import com.andyadc.kratos.spi.loader.ExtensionLoader;

import java.util.Optional;

@SPIClass
public class SpiExtensionFactory implements ExtensionFactory {

    @Override
    public <T> T getExtension(String key, Class<T> clazz) {
        return Optional.ofNullable(clazz)
                .filter(Class::isInterface)
                .filter(cls -> cls.isAnnotationPresent(SPI.class))
                .map(ExtensionLoader::getExtensionLoader)
                .map(ExtensionLoader::getDefaultSpiClassInstance)
                .orElse(null)
                ;
    }

}
