package org.example.menu;

import lombok.RequiredArgsConstructor;
import org.example.general.Pair;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@RequiredArgsConstructor
public class MenuController {

    final private MenuDAO menuDAO;

    public MenuBatchResponseDTO registerMenuByCSV(MenuBatchRequestDTO req){

        String[] lines = new String(req.getCsvBytes(), StandardCharsets.UTF_8).split("\n");

        ArrayList<Pair<Long, String>> successList;

        for(int i = 1; i < lines.length; ++i){

            lines[i] = lines[i].strip();
            if(lines[i].isEmpty()) continue;

            String[] parsedLine = lines[i].split(",");
            Long generatedId = parsedLine
        }
    }
}
