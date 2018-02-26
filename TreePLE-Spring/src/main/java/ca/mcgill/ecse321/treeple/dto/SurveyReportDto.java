package ca.mcgill.ecse321.treeple.dto;

import java.sql.Date;

public class SurveyReportDto {

    private int reportId;
    private Date reportDate;
    private String reportingUser;

    public SurveyReportDto() {
    }

    public SurveyReportDto(int reportId, Date reportDate, String reportingUser) {
        this.reportId = reportId;
        this.reportDate = reportDate;
        this.reportingUser = reportingUser;
    }

    public int getReportId() {
        return reportId;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public String getReportingUser() {
        return reportingUser;
    }
}
