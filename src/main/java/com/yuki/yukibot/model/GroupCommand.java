package com.yuki.yukibot.model;

import com.yuki.yukibot.util.enums.CommandTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupCommand {

    private CommandTypeEnum commandTypeEnum;


}
