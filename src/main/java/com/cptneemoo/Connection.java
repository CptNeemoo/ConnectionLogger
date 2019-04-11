package com.cptneemoo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class Connection {
    private long time;
    private int session;
    private String ip;
}
