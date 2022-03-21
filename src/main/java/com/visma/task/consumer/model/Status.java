package com.visma.task.consumer.model;

public class Status {

    private String uuid;
    private StatusType statusType;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
    }

    @Override
    public String toString() {
        return "Status{" +
                "uuid='" + uuid + '\'' +
                ", statusType=" + statusType +
                '}';
    }
}
