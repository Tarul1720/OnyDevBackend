package com.devcom.OnlyDev.Modal;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentBlock {

    private Long id;
    private String type; // text, image, video, embed, grid, tags
    private String content;
    private Integer order;
    private Integer columns;
    private Integer rows;
    private List<Map<String, Object>> cells;
    private Map<String, Object> styles; // Direct map for styling
    private Map<String, Object> gridConfig; // Direct map for grid config
}