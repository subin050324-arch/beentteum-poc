package com.beentteum.poc.codingtest;

import java.util.*;

public class Solution {
    public int[] solution(String[] id_list, String[] report, int k) {
        Set<String> distinctReports = new HashSet<>(Arrays.asList(report));

        Map<String, List<String>> reportMap = new HashMap<>();
        for (String r : distinctReports) {
            String[] parts = r.split(" ");
            String reporter = parts[0];
            String reported = parts[1];
            reportMap.computeIfAbsent(reported, key -> new ArrayList<>()).add(reporter);
        }

        Map<String, Integer> mailCounts = new HashMap<>();
        for (List<String> reporters : reportMap.values()) {
            if (reporters.size() >= k) {
                for (String reporter : reporters) {
                    mailCounts.put(reporter, mailCounts.getOrDefault(reporter, 0) + 1);
                }
            }
        }

        int[] answer = new int[id_list.length];
        for (int i = 0; i < id_list.length; i++) {
            answer[i] = mailCounts.getOrDefault(id_list[i], 0);
        }
        return answer;
    }
}
