package com.cptneemoo.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Connection {
    private long time;
    private int session;
    private String ip;
}
