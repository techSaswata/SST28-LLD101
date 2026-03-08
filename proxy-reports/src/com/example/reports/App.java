package com.example.reports;

/**
 * CampusVault demo using ReportProxy.
 *
 * - Unauthorized access is blocked by the proxy.
 * - Real report loading is lazy (only on first authorized display).
 * - Repeated views reuse the cached real report.
 */
public class App {

    public static void main(String[] args) {
        User student = new User("Jasleen", "STUDENT");
        User faculty = new User("Prof. Noor", "FACULTY");
        User admin   = new User("Kshitij", "ADMIN");

        Report publicReport  = new ReportProxy("R-101", "Orientation Plan", "PUBLIC");
        Report facultyReport = new ReportProxy("R-202", "Midterm Review", "FACULTY");
        Report adminReport   = new ReportProxy("R-303", "Budget Audit", "ADMIN");

        ReportViewer viewer = new ReportViewer();

        System.out.println("=== CampusVault Demo ===");

        // Student can view PUBLIC
        viewer.open(publicReport, student);
        System.out.println();

        // Student CANNOT view FACULTY report
        viewer.open(facultyReport, student);
        System.out.println();

        // Faculty CAN view FACULTY report (lazy load happens here)
        viewer.open(facultyReport, faculty);
        System.out.println();

        // Admin CAN view ADMIN report (lazy load happens here)
        viewer.open(adminReport, admin);
        System.out.println();

        // Admin views ADMIN report again (cached — no disk load)
        viewer.open(adminReport, admin);
    }
}
