package com.yuki.yukibot.model;

import com.yuki.yukibot.util.enums.DurationUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Duration {

    private int duration;

    private DurationUnit unit;
}
