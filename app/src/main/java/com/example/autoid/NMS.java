package com.example.autoid;

import com.example.autoid.Modelos.Detection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NMS {
    public static List<Detection> applyNMS(List<Detection> detections, float threshold) {
        List<Detection> filteredDetections = new ArrayList<>();

        Collections.sort(detections, (a, b) -> Float.compare(b.getConfidence(), a.getConfidence()));

        while (!detections.isEmpty()) {
            Detection maxConfidenceDetection = detections.remove(0);
            filteredDetections.add(maxConfidenceDetection);

            for (int i = 0; i < detections.size(); ) {
                float overlap = calculateOverlap(maxConfidenceDetection, detections.get(i));

                if (overlap > threshold) {
                    detections.remove(i);
                } else {
                    i++;
                }
            }
        }

        return filteredDetections;
    }
    private static float calculateOverlap(Detection a, Detection b) {
        float overlapX = Math.max(0, Math.min(a.getBoundingBox().getX() + a.getBoundingBox().getWidth(), b.getBoundingBox().getX() + b.getBoundingBox().getWidth()) - Math.max(a.getBoundingBox().getX(), b.getBoundingBox().getX()));
        float overlapY = Math.max(0, Math.min(a.getBoundingBox().getY() + a.getBoundingBox().getHeight(), b.getBoundingBox().getY() + b.getBoundingBox().getHeight()) - Math.max(a.getBoundingBox().getY(), b.getBoundingBox().getY()));
        float intersection = overlapX * overlapY;
        float areaA = a.getBoundingBox().getWidth() * a.getBoundingBox().getHeight();
        float areaB = b.getBoundingBox().getWidth() * b.getBoundingBox().getHeight();
        float union = areaA + areaB - intersection;
        return intersection / union;
    }
}
