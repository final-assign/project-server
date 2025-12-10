package org.example.menu;

import lombok.Getter;
import org.example.general.Utils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class MenuRegisterRequestDTO {

    final private Long restId;
    final private String menuName;
    final private Integer standardPrice;
    final private Integer studentPrice;
    final private Integer defaultAmount;
    final private LocalDateTime salesDate;
    final private MenuTypeName menuType;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public MenuRegisterRequestDTO(byte[] body) {

        int cursor = 0;

        this.restId = Utils.bytesToLong(body, cursor);
        cursor += 8;

        short menuNameLength = Utils.bytesToShort(body, cursor);
        cursor += 2;
        this.menuName = new String(body, cursor, menuNameLength, StandardCharsets.UTF_8);
        cursor += menuNameLength;

        this.standardPrice = Utils.bytesToInt(body, cursor);
        cursor += 4;

        this.studentPrice = Utils.bytesToInt(body, cursor);
        cursor += 4;

        this.defaultAmount = Utils.bytesToInt(body, cursor);
        cursor += 4;

        short dateLength = Utils.bytesToShort(body, cursor);
        cursor += 2;
        String dateString = new String(body, cursor, dateLength, StandardCharsets.UTF_8);
        this.salesDate = LocalDateTime.parse(dateString, DATE_FORMATTER);
        cursor += dateLength;

        short typeLength = Utils.bytesToShort(body, cursor);
        cursor += 2;
        String typeString = new String(body, cursor, typeLength, StandardCharsets.UTF_8);
        this.menuType = MenuTypeName.valueOf(typeString);
    }
}