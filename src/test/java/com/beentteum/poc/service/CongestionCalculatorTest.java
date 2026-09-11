package com.beentteum.poc.service;

import com.beentteum.poc.service.CongestionCalculator.CongestionStatus;
import com.beentteum.poc.service.CongestionCalculator.Report;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CongestionCalculatorTest {

    private final CongestionCalculator calculator = new CongestionCalculator();
    private final LocalDateTime now = LocalDateTime.of(2026, 9, 11, 14, 0);

    @Test
    @DisplayName("TC-01: 제보 0건일 때 정보부족 반환")
    void test_zeroReports() {
        List<Report> reports = List.of();
        CongestionStatus result = calculator.calculateCongestion(reports, now);
        assertThat(result).isEqualTo(CongestionStatus.정보부족);
    }

    @Test
    @DisplayName("TC-02: 동일 사용자 중복 제보는 최신 1건만 인정")
    void test_duplicateUserReports() {
        List<Report> reports = List.of(
                new Report("user1", CongestionStatus.혼잡, now.minusMinutes(30)),
                new Report("user1", CongestionStatus.여유, now.minusMinutes(10)),
                new Report("user2", CongestionStatus.여유, now.minusMinutes(20)),
                new Report("user3", CongestionStatus.여유, now.minusMinutes(15))
        );
        CongestionStatus result = calculator.calculateCongestion(reports, now);
        assertThat(result).isEqualTo(CongestionStatus.여유);
    }

    @Test
    @DisplayName("TC-03: 동점 발생 시 최신 제보 상태 채택")
    void test_tieBreakRules() {
        List<Report> reports = List.of(
                new Report("user1", CongestionStatus.여유, now.minusMinutes(25)),
                new Report("user2", CongestionStatus.여유, now.minusMinutes(20)),
                new Report("user3", CongestionStatus.혼잡, now.minusMinutes(15)),
                new Report("user4", CongestionStatus.혼잡, now.minusMinutes(5))
        );
        CongestionStatus result = calculator.calculateCongestion(reports, now);
        assertThat(result).isEqualTo(CongestionStatus.혼잡);
    }

    @Test
    @DisplayName("TC-04: 60분 윈도우 경계(61분 전) 밖 제보 제외")
    void test_windowBoundary() {
        List<Report> reports = List.of(
                new Report("user1", CongestionStatus.여유, now.minusMinutes(61)),
                new Report("user2", CongestionStatus.여유, now.minusMinutes(30)),
                new Report("user3", CongestionStatus.여유, now.minusMinutes(10))
        );
        CongestionStatus result = calculator.calculateCongestion(reports, now);
        assertThat(result).isEqualTo(CongestionStatus.정보부족);
    }
}
