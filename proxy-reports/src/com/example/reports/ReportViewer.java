package com.example.reports;

/**
 * Viewer now depends on the Report interface (not concrete ReportFile).
 */
public class ReportViewer {

    public void open(Report report, User user) {
        report.display(user);
    }
}
