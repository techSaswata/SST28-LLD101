package com.example.reports;

/**
 * Proxy that adds access control, lazy loading, and caching
 * in front of RealReport.
 */
public class ReportProxy implements Report {

    private final String reportId;
    private final String title;
    private final String classification;
    private final AccessControl accessControl = new AccessControl();

    private RealReport cachedReport; // lazy — null until first authorized access

    public ReportProxy(String reportId, String title, String classification) {
        this.reportId = reportId;
        this.title = title;
        this.classification = classification;
    }

    @Override
    public void display(User user) {
        // 1. Access check
        if (!accessControl.canAccess(user, classification)) {
            System.out.println("[ACCESS DENIED] " + user.getName()
                    + " (" + user.getRole() + ") cannot view "
                    + classification + " report " + reportId);
            return;
        }

        // 2. Lazy-load + cache the real report
        if (cachedReport == null) {
            cachedReport = new RealReport(reportId, title, classification);
        }

        // 3. Delegate to real subject
        cachedReport.display(user);
    }
}
