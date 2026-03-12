package com.project.inventory_management_system.enums;

import java.util.Arrays;

public enum ActionStatus
{

    // CLOUD TEAM
    CLOUD_UPDATED("CLOUD UPDATED"),

    // FINANCE
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),

    //RMA
    PASSED("PASSED"),
    FAILED("FAILED"),

    //SCM
    TICKET_FILLED("JIRA FILLED"),
    TICKET_VERIFIED("JIRA VERIFIED"),

    //SYRMA
    COMPLETED("COMPLETED"),
    RE_PROD_TEST_Completed("RE-PROD/TEST-Completed"),

    //PROJECT TEAM
    PRI_DELIVERY_PDI("Pri-Delivery PDI"),
    PDI_PASS("PDI PASS"),
    PDI_FAIL("PDI FAIL"),
    POST_DELIVERY_PDI("Post-Delivery PDI");

    private final String display;

    ActionStatus(String display)
    {
        this.display = display;
    }

    public String toDisplay()
    {
        return display;
    }

    public static ActionStatus fromDisplay(String display)
    {
        return Arrays.stream(values())
                .filter(s -> s.display.equalsIgnoreCase(display))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid OrderStatus: " + display)
                );
    }
}
