package com.example.frontend.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Component
public class MemberModel {

    @Value("${backend.api.url}")
    private String BASE_URL;

    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ 2nd Page (List)
    public List<Map<String, Object>> getMemberData(String memberId) {
        String endpoint;

        switch (memberId) {
            case "santosh" -> endpoint = "orders";
            case "pranathi" -> endpoint = "customers";
            case "sunny" -> endpoint = "customers";
            case "sowmya" -> endpoint = "productlines";
            case "sarika" -> endpoint = "offices";
            case "reshmika" -> endpoint = "customers";
            case "gnanasri" -> endpoint = "employees"; // ✅ Employees list
            default -> throw new IllegalArgumentException("Invalid member: " + memberId);
        }

        String url = BASE_URL + endpoint;
        System.out.println("👉 Fetching list from: " + url);

        try {
            Map<String, Object>[] response = restTemplate.getForObject(url, Map[].class);
            return response == null ? Collections.emptyList() : Arrays.asList(response);
        } catch (RestClientException e) {
            System.err.println("❌ Error fetching list for " + memberId + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ✅ 3rd Page (Details)
    public Object getMemberRecord(String memberId, String key) {
        String endpoint;
        boolean expectList = true;

        switch (memberId) {
            case "santosh" -> endpoint = "orderdetails/order/" + key;
            case "pranathi" -> endpoint = "payments/customer/" + key;
            case "sunny" -> endpoint = "products/customer/" + key;
            case "sowmya" -> endpoint = "products/by-productline/" + key;
            case "sarika" -> endpoint = "employees/" + key;
            case "reshmika" -> endpoint = "orders/customer/" + key;

            // ✅ Gnanasri: customers handled by this employee
            case "gnanasri" -> endpoint = "customers/salesrep/" + key;

            default -> throw new IllegalArgumentException("Invalid member: " + memberId);
        }

        String url = BASE_URL + endpoint;
        System.out.println("🔍 Fetching details from: " + url);

        try {
            List<Map<String, Object>> listResponse = restTemplate.getForObject(url, List.class);
            return listResponse != null ? listResponse : Collections.emptyList();
        } catch (RestClientException e) {
            System.err.println("❌ Error fetching details for " + memberId + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ✅ 3rd API (Summary / Totals)
    public Object getMemberSummary(String memberId, String key, String extraKey) {
        String endpoint;
        boolean expectList = false;
        boolean expectNumber = false;

        switch (memberId) {
            case "santosh" -> endpoint = "orders/total/" + key;

            case "pranathi" -> {
                endpoint = "payments/customer/" + key + "/total";
                expectNumber = true;
            }

            case "sunny" -> {
                if (extraKey == null || extraKey.isEmpty()) {
                    System.out.println("⚠️ Sunny’s 3rd API waiting for productCode input...");
                    return null;
                }
                endpoint = "orders/filter/" + key + "/" + extraKey;
                expectList = true;
            }

            case "sowmya" -> endpoint = "productvendor/" + key;

            case "sarika" -> {
                endpoint = "employees/count/" + key;
                expectNumber = true;
            }

            case "reshmika" -> endpoint = "orders/status-count/" + key;

            // ✅ Gnanasri summary — employees with customer count
            case "gnanasri" -> {
                endpoint = "employees/with-customer-count";
                expectList = true;
            }

            default -> throw new IllegalArgumentException("Invalid member: " + memberId);
        }

        String url = BASE_URL + endpoint;
        System.out.println("📊 Fetching summary from: " + url);

        try {
            if (expectNumber)
                return restTemplate.getForObject(url, Number.class);
            if (expectList)
                return restTemplate.getForObject(url, List.class);
            return restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            System.err.println("❌ Error fetching summary for " + memberId + ": " + e.getMessage());
            return "No summary data";
        }
    }
}
