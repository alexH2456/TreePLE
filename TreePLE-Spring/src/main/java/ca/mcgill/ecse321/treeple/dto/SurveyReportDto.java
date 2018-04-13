package ca.mcgill.ecse321.treeple.dto;

import java.sql.Date;

public class SurveyReportDto {

    private int reportId;
    private Date reportDate;
    private String reportUser;

    public SurveyReportDto() {
    }

    public SurveyReportDto(int reportId, Date reportDate, String reportUser) {
        this.reportId = reportId;
        this.reportDate = reportDate;
        this.reportUser = reportUser;
    }

    public int getReportId() {
        return reportId;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public String getReportUser() {
        return reportUser;
    }
}
