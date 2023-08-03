public enum SlotTime implements  Cloneable   {
    FROM_8_TO_9_15(Duration.LECTURE, 800, 915),
    FROM_8_30_TO_9_45(Duration.LECTURE, 830, 945),
    FROM_8_30_TO_11_10(Duration.LAB, 830, 1110),
    FROM_10_TO_11_15(Duration.LECTURE, 1000, 1115),
    FROM_11_25_TO_12_40(Duration.LECTURE, 1125, 1240),
    FROM_11_25_TO_14_05(Duration.LAB, 1125, 1405),
    FROM_12_50_TO_14_05(Duration.LECTURE, 1250, 1405),
    FROM_14_15_TO_15_30(Duration.LECTURE, 1415, 1530),
    FROM_14_15_TO_16_55(Duration.LAB, 1415, 1655);

    private Duration duration;
    private Integer from;
    private Integer to;

    SlotTime(Duration duration, Integer from, Integer to){
        this.duration = duration;
        this.from = from;
        this.to = to;
    }

    public Duration getDuration() {
        return duration;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getTo() {
        return to;
    }
}
