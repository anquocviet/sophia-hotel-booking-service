package vn.edu.iuh.bookingservice.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private String id;
    private String name;
    private Double price;
    private Integer capacity;
    private String description;
    private List<String> amenities;
    private List<String> images;
    private String status;
}
