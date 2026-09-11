package com.beentteum.poc;

import com.beentteum.poc.service.CongestionCalculator;
import com.beentteum.poc.service.CongestionCalculator.CongestionStatus;
import com.beentteum.poc.service.CongestionCalculator.Report;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        CongestionCalculator calculator = new CongestionCalculator();
        LocalDateTime now = LocalDateTime.now();
        List<Report> reports = new ArrayList<>();

        System.out.println("==========================================");
        System.out.println("   [빈틈] 실시간 매장 혼잡도 PoC 시뮬레이션   ");
        System.out.println("   타깃 매장: 성수 아늑 카페              ");
        System.out.println("==========================================\n");

        // 시나리오 1: 제보가 전혀 없는 초기 상태
        System.out.println("[Step 1] 매장 오픈 직후 (제보 0건)");
        printStatus(calculator.calculateCongestion(reports, now));

        // 시나리오 2: 제보 2건 인입 (최소 기준치 3건 미달)
        System.out.println("\n[Step 2] 회원 제보 2건 등록 (유저1: 혼잡, 유저2: 혼잡)");
        reports.add(new Report("user1", CongestionStatus.혼잡, now.minusMinutes(20)));
        reports.add(new Report("user2", CongestionStatus.혼잡, now.minusMinutes(10)));
        printStatus(calculator.calculateCongestion(reports, now));

        // 시나리오 3: 제보 1건 추가되어 최소 건수(3건) 충족 -> 혼잡 확정
        System.out.println("\n[Step 3] 회원 제보 1건 추가 등록 (유저3: 혼잡) -> 총 3건 도달");
        reports.add(new Report("user3", CongestionStatus.혼잡, now.minusMinutes(5)));
        printStatus(calculator.calculateCongestion(reports, now));

        // 시나리오 4: 기존 유저(user1, user2)의 상태 변경 제보 인입 (중복 제보 최신화)
        System.out.println("\n[Step 4] 손님 퇴장으로 기존 유저들의 상태 갱신 제보 (유저1: 여유, 유저2: 여유)");
        reports.add(new Report("user1", CongestionStatus.여유, now.minusMinutes(2)));
        reports.add(new Report("user2", CongestionStatus.여유, now.minusMinutes(1)));
        printStatus(calculator.calculateCongestion(reports, now));

        System.out.println("\n==========================================");
        System.out.println("   PoC 시뮬레이션 정상 완료                 ");
        System.out.println("==========================================");
    }

    private static void printStatus(CongestionStatus status) {
        System.out.println(">> 현재 매장 개표 혼잡도: [" + status + "]");
    }
}
