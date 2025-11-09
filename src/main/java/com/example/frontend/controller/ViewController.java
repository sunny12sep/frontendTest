package com.example.frontend.controller;

import com.example.frontend.model.MemberModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class ViewController {

    @Autowired
    private MemberModel memberModel;

    // ✅ 1️⃣ Home Page — shows all members
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        List<String> members = Arrays.asList(
                "santosh",
                "pranathi",
                "sunny",
                "sowmya",
                "sarika",
                "reshmika",
                "gnanasri"
        );
        model.addAttribute("members", members);
        return "index";
    }

    // ✅ 2️⃣ Member Page — lists data for each member
    @GetMapping("/member/{memberId}")
    public String member(
            @PathVariable String memberId,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        List<Map<String, Object>> data = memberModel.getMemberData(memberId);
        int pageSize = 15;
        int from = Math.max(0, (page - 1) * pageSize);
        int to = Math.min(from + pageSize, data.size());
        List<Map<String, Object>> pagedData = data.subList(from, to);

        List<String> columns = data.isEmpty()
                ? Collections.emptyList()
                : new ArrayList<>(data.get(0).keySet());

        model.addAttribute("memberId", memberId);
        model.addAttribute("memberDisplay", capitalize(memberId));
        model.addAttribute("data", pagedData);
        model.addAttribute("columns", columns);
        model.addAttribute("totalPages",
                (int) Math.ceil((double) data.size() / pageSize));
        model.addAttribute("currentPage", page);

        return "member";
    }

    // ✅ 3️⃣ Member Details Page — handles single key and Sunny’s dual-key logic
    @GetMapping("/member/details")
    public String memberDetails(
            @RequestParam String key,
            @RequestParam String member,
            @RequestParam(required = false) String extraKey, // used for Sunny’s productCode
            Model model) {

        System.out.println("➡️ Loading details for member: " + member +
                " | key=" + key +
                (extraKey != null ? " | extraKey=" + extraKey : ""));

        // 1️⃣ Main details (e.g., orderdetails/order/{key})
        Object record = memberModel.getMemberRecord(member, key);

        // 2️⃣ Summary (totals, counts, or filtered results)
        Object summary = memberModel.getMemberSummary(member, key, extraKey);

        model.addAttribute("record", record);
        model.addAttribute("summary", summary);
        model.addAttribute("memberId", member);
        model.addAttribute("memberDisplay", capitalize(member));
        model.addAttribute("key", key);
        model.addAttribute("extraKey", extraKey);

        return "member_details";
    }

    // ✅ Helper: Capitalize names for display
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
