package gov.epa.ccte.api.chemical.service;

import com.nextmovesoftware.CaffeineFix.CaffeineFix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class CaffeineFixConversionService {

    @Value(value = "classpath:CFDictPubChem.cfx")
    private Resource dictionaryResource;

    CaffeineFix caffeineFix;

    @PostConstruct
    private void init() throws IOException {
        File tempFile = File.createTempFile("CFDictPubChem", ".cfx");
        tempFile.deleteOnExit(); 
        try (InputStream inputStream = dictionaryResource.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        caffeineFix = new CaffeineFix(tempFile.getAbsolutePath());
    }
    
    public List<String> CaffeineFixName(String nameString){
        System.out.println("---------------------------------------------------- NAME STRING: " + nameString + "----------------------------------------------------");
        List<String> suggestions = caffeineFix.suggestions(nameString, 1);
        if(!suggestions.isEmpty()) return suggestions;
        suggestions = caffeineFix.suggestions(nameString, 2);
        if(!suggestions.isEmpty()) return suggestions;
        suggestions = caffeineFix.suggestions(nameString, 3);
        if(!suggestions.isEmpty()) return suggestions;
        return null;
    }
}
