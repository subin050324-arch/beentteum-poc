package com.beentteum.poc.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CongestionCalculator {

    public enum CongestionStatus {
        여유, 보통, 혼잡, 정보부족
    }

    public record Report(String userId, CongestionStatus status, LocalDateTime reportedAt) {}

    private static final int WINDOW_MINUTES = 60;
    private static final int MIN_REQUIRED_REPORTS = 3;

    public CongestionStatus calculateCongestion(List<Report> reports, LocalDateTime currentTime) {
        if (reports == null || reports.isEmpty()) {
            return CongestionStatus.정보부족;
        }

        LocalDateTime windowStart = currentTime.minusMinutes(WINDOW_MINUTES);

        // 1. 60분 윈도우 필터링 및 동일 사용자 최신 1건 유지
        Map<String, Report> latestReportsByUser = reports.stream()
                .filter(r -> !r.reportedAt().isBefore(windowStart) && !r.reportedAt().isAfter(currentTime))
                .collect(Collectors.toMap(
                        Report::userId,
                        r -> r,
                        (existing, newer) -> newer.reportedAt().isAfter(existing.reportedAt()) ? newer : existing
                ));

        List<Report> validReports = new ArrayList<>(latestReportsByUser.values());

        // 2. 최소 건수(3건) 미달 시 정보부족
        if (validReports.size() < MIN_REQUIRED_REPORTS) {
            return CongestionStatus.정보부족;
        }

        // 3. 다수결 개표
        Map<CongestionStatus, Long> frequencyMap = validReports.stream()
                .collect(Collectors.groupingBy(Report::status, Collectors.counting()));

        long maxCount = Collections.max(frequencyMap.values());

        List<CongestionStatus> topStatuses = frequencyMap.entrySet().stream()
                .filter(e -> e.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .toList();

        if (topStatuses.size() == 1) {
            return topStatuses.get(0);
        }

        // 4. 동점 처리: 동점 상태 중 가장 최신 제보의 상태 채택
        return validReports.stream()
                .filter(r -> topStatuses.contains(r.status()))
                .max(Comparator.comparing(Report::reportedAt))
                .map(Report::status)
                .orElse(CongestionStatus.정보부족);
    }
}
