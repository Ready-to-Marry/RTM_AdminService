package ready_to_marry.adminservice.event.enums;

public enum LinkType {
    SD, // Special Deal
    CE; // Coupon Event

    public static LinkType from(String value) {
        return LinkType.valueOf(value.toUpperCase());
    }
}