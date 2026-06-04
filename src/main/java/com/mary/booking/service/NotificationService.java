package com.mary.booking.service;

import com.mary.booking.dto.auth.NotificationResponse;
import com.mary.booking.entity.Booking;
import com.mary.booking.entity.Notification;
import com.mary.booking.entity.User;
import com.mary.booking.enums.NotificationType;
import com.mary.booking.repository.BookingRepository;
import com.mary.booking.repository.NotificationRepository;
import com.mary.booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public void createNotification(String userEmail, String message, NotificationType type, Long bookingId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        Booking booking = null;
        if (bookingId != null) {
            booking = bookingRepository.findById(bookingId).orElse(null);
        }

        Notification notification = Notification.builder()
                .user(user)
                .booking(booking)
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void createBookingNotification(Booking booking) {
        String message = String.format("Ваше бронирование \"%s\" в комнате %s подтверждено на %s в %s",
                booking.getTitle(),
                booking.getRoom().getName(),
                booking.getStartTime().toLocalDate().toString(),
                booking.getStartTime().toLocalTime().toString());

        createNotification(booking.getUser().getEmail(), message, NotificationType.BOOKING_CONFIRMED, booking.getId());
    }

    @Transactional
    public void createCancellationNotification(Booking booking) {
        String message = String.format("Бронирование \"%s\" в комнате %s отменено",
                booking.getTitle(),
                booking.getRoom().getName());

        createNotification(booking.getUser().getEmail(), message, NotificationType.BOOKING_CANCELLED, booking.getId());
    }

    @Transactional
    public void createWelcomeNotification(String userEmail, String username) {
        String message = String.format("Добро пожаловать в систему бронирования комнат, %s!", username);
        createNotification(userEmail, message, NotificationType.BOOKING_CONFIRMED, null);
    }

    public List<NotificationResponse> getUserNotifications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);

        return notifications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Transactional
    public void markAsRead(Long notificationId, String email) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));

        if (!notification.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Access denied to notification");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        notificationRepository.markAllAsReadByUser(user.getId());
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .bookingId(notification.getBooking() != null ? notification.getBooking().getId() : null)
                .build();
    }
}
