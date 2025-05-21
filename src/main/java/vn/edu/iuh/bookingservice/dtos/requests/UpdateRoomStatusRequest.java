package vn.edu.iuh.bookingservice.dtos.requests;

public record UpdateRoomStatusRequest(
        String hotelId,
        String roomId,
        String status
) {
}
