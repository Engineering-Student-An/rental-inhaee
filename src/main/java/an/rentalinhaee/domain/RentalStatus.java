package an.rentalinhaee.domain;

public enum RentalStatus {
    // 연체, 진행중, 반납완료
    OVERDUE("연체 중"),
    ING("대여 중"),
    FINISH("반납 완료");

    private String displayName;

    RentalStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}
