package com.surepay.library;

public class ReportMessage
{
    public static final int START_REPORT = 1;
    public static final int END_REPORT = 2;
    public static final int PART_REPORT = 3;
    public static final int ONE_SHOT_REPORT = 4;
    private String reportData;
    private int reportType;

    public ReportMessage(String reportData, int reportType) {
        this.reportData = reportData;
        this.reportType = reportType;
    }

    public String getReportData() {
        return this.reportData;
    }

    public void setReportData(String reportData) {
        this.reportData = reportData;
    }

    public int getReportType() {
        return this.reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }
}