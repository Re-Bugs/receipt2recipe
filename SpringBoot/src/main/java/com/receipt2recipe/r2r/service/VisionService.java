package com.receipt2recipe.r2r.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class VisionService {

    public String extractTextFromImageUrl(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        ByteString imgBytes;
        try (InputStream in = url.openStream()) {
            imgBytes = ByteString.readFrom(in);
        }

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();
        List<AnnotateImageRequest> requests = new ArrayList<>();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            StringBuilder stringBuilder = new StringBuilder();
            for (AnnotateImageResponse res : response.getResponsesList()) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return "Error detected";
                }
                stringBuilder.append(res.getFullTextAnnotation().getText());
            }
            return stringBuilder.toString();
        }
    }
}
