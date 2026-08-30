// get stalls for UI
// admin add/remove stalls

package com.reservex.backend.services;

import com.reservex.backend.dto.CreateStallRequest;
import com.reservex.backend.dto.StallDto;
import com.reservex.backend.dto.UpdateStallRequest;
import com.reservex.backend.entity.Reservation;
import com.reservex.backend.entity.ReservationGenre;
import com.reservex.backend.entity.Stall;
import com.reservex.backend.entity.User;
import com.reservex.backend.repositories.ReservationGenreRepository;
import com.reservex.backend.repositories.ReservationRepository;
import com.reservex.backend.repositories.StallRepository;
import com.reservex.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class StallService {

    private final StallRepository stallRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ReservationGenreRepository reservationGenreRepository;

    @Transactional(readOnly = true)
    public List<StallDto> getAllStallsWithAvailability() {
        return stallRepository.findAllByOrderByNameAsc()
                .stream()
                .map(StallDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isNameTaken(String name) {
        return stallRepository.existsByNameIgnoreCase(name != null ? name.trim() : "");
    }

    @Transactional(readOnly = true)
    public boolean isNameTakenExcluding(String name, Integer excludeId) {
        if (name == null || name.isBlank()) return false;
        return stallRepository.existsByNameIgnoreCaseAndIdNot(name.trim(), excludeId);
    }

    private void validatePriceForSize(Double price, Stall.StallSize size) {
        if (price == null) {
            throw new IllegalArgumentException("Price is required");
        }
        
        switch (size) {
            case small:
                if (price < 5000 || price > 19999) {
                    throw new IllegalArgumentException("Small stalls must be priced between 5,000 - 19,999 LKR");
                }
                break;
            case medium:
                if (price < 20000 || price > 29999) {
                    throw new IllegalArgumentException("Medium stalls must be priced between 20,000 - 29,999 LKR");
                }
                break;
            case large:
                if (price < 30000 || price > 49999) {
                    throw new IllegalArgumentException("Large stalls must be priced between 30,000 - 49,999 LKR");
                }
                break;
        }
    }

    @Transactional
    public StallDto createStall(CreateStallRequest request) {
        String name = request.getName().trim();
        if (stallRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Stall name already exists: " + name);
        }
        
        // Validate grid position values
        if (request.getGridRow() < 1 || request.getGridCol() < 1) {
            throw new IllegalArgumentException("Grid row and column must be at least 1");
        }
        
        // Validate price range based on size
        validatePriceForSize(request.getPrice(), request.getSize());
        
        // Validate grid position uniqueness
        if (stallRepository.existsByGridRowAndGridCol(request.getGridRow(), request.getGridCol())) {
            throw new IllegalArgumentException("Grid position (Row: " + request.getGridRow() + 
                ", Col: " + request.getGridCol() + ") is already occupied");
        }
        
        Stall stall = Stall.builder()
                .name(name)
                .size(request.getSize())
                .price(request.getPrice() != null ? request.getPrice() : 0.0)
                .gridCol(request.getGridCol())
                .gridRow(request.getGridRow())
                .isConfirmed(false)
                .build();
        stall = stallRepository.save(stall);
        return StallDto.fromEntity(stall);
    }

    @Transactional
    public StallDto updateStall(Integer id, UpdateStallRequest request) {
        Stall stall = stallRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stall not found: " + id));
        String name = request.getName().trim();
        if (stallRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new IllegalArgumentException("Stall name already exists: " + name);
        }
        
        // Validate grid position values
        if (request.getGridRow() < 1 || request.getGridCol() < 1) {
            throw new IllegalArgumentException("Grid row and column must be at least 1");
        }
        
        // Validate price range based on size
        validatePriceForSize(request.getPrice(), request.getSize());
        
        // Validate grid position uniqueness (excluding current stall)
        if (stallRepository.existsByGridRowAndGridColAndIdNot(request.getGridRow(), request.getGridCol(), id)) {
            throw new IllegalArgumentException("Grid position (Row: " + request.getGridRow() + 
                ", Col: " + request.getGridCol() + ") is already occupied");
        }
        
        stall.setName(name);
        stall.setSize(request.getSize());
        stall.setPrice(request.getPrice() != null ? request.getPrice() : 0.0);
        stall.setGridCol(request.getGridCol());
        stall.setGridRow(request.getGridRow());
        stall = stallRepository.save(stall);
        return StallDto.fromEntity(stall);
    }

    @Transactional
    public void deleteStall(Integer stallId) {
        Stall stall = stallRepository.findById(stallId)
                .orElseThrow(() -> new IllegalArgumentException("Stall not found: " + stallId));
        String stallName = stall.getName();
        
        // Case 1: Stall is not reserved (is_Confirmed = false or null)
        if (stall.getIsConfirmed() == null || !stall.getIsConfirmed()) {
            // Simply delete the stall - no reservations to handle
            stallRepository.delete(stall);
            return;
        }
        
        // Cases 2 & 3: Stall is reserved (is_Confirmed = true)
        // Find all reservations that include this stall
        List<Reservation> reservationsWithStall = reservationRepository.findByStalls_Id(stallId);
        
        if (reservationsWithStall.isEmpty()) {
            // No reservations found, just delete the stall
            stallRepository.delete(stall);
            return;
        }
        
        for (Reservation reservation : reservationsWithStall) {
            User user = reservation.getUser();
            Integer reservationId = reservation.getId();
            
            // Get the current stall count before removal
            int stallCountBeforeDeletion = reservation.getStalls().size();
            
            // Delete reservation_genres entries for this stall in this reservation
            reservationGenreRepository.deleteByReservationIdAndStallId(reservationId, stallId);
            
            // Remove the stall from the reservation's stall set
            reservation.getStalls().removeIf(s -> s.getId().equals(stallId));
            
            // Decrease user's booking count
            int newBookingCount = Math.max(0, user.getNoOfCurrentBookings() - 1);
            user.setNoOfCurrentBookings(newBookingCount);
            userRepository.save(user);
            
            // Case 3: This was the only stall in the reservation
            if (stallCountBeforeDeletion == 1) {
                // Delete the reservation (cascade will handle reservation_stalls)
                reservationRepository.delete(reservation);
                
                // Send email: reservation cancelled
                try {
                    emailService.sendStallDeletionNotification(
                        user, 
                        stallName, 
                        newBookingCount, 
                        true  // reservationCancelled = true
                    );
                } catch (Exception e) {
                    // Log but don't fail the transaction
                    System.err.println("Failed to send email: " + e.getMessage());
                }
            } 
            // Case 2: User still has other stalls in the reservation
            else {
                // Save the updated reservation
                reservationRepository.save(reservation);
                
                // Send email: stall removed but reservation still active
                try {
                    emailService.sendStallDeletionNotification(
                        user, 
                        stallName, 
                        newBookingCount, 
                        false  // reservationCancelled = false
                    );
                } catch (Exception e) {
                    // Log but don't fail the transaction
                    System.err.println("Failed to send email: " + e.getMessage());
                }
            }
        }
        
        // Finally, delete the stall itself
        stallRepository.delete(stall);
    }

    @Transactional
    public void unreserveStall(Integer stallId) {
        log.info("Starting unreserveStall for stall ID: " + stallId);
        
        Stall stall = stallRepository.findById(stallId)
                .orElseThrow(() -> new IllegalArgumentException("Stall not found: " + stallId));
        String stallName = stall.getName();
        
        log.info("Found stall: " + stallName + ", isConfirmed: " + stall.getIsConfirmed());

        // Check if stall is actually reserved
        if (stall.getIsConfirmed() == null || !stall.getIsConfirmed()) {
            throw new IllegalArgumentException("Stall is not reserved");
        }

        // Find all reservations that include this stall
        List<Reservation> reservationsWithStall = reservationRepository.findByStalls_Id(stallId);
        log.info("Found " + reservationsWithStall.size() + " reservations with this stall");

        if (reservationsWithStall.isEmpty()) {
            // No reservations found, just mark as unreserved
            stall.setIsConfirmed(false);
            stallRepository.save(stall);
            log.info("No reservations found, marked stall as unreserved");
            return;
        }

        // Process each reservation that has this stall
        for (Reservation reservation : reservationsWithStall) {
            User user = reservation.getUser();
            Integer reservationId = reservation.getId();
            log.info("Processing reservation ID: " + reservationId + " for user: " + user.getEmail());

            // Get the current stall count before removal
            int stallCountBeforeDeletion = reservation.getStalls().size();
            log.info("Stall count before deletion: " + stallCountBeforeDeletion);

            // Delete reservation_genres entries for this stall in this reservation
            reservationGenreRepository.deleteByReservationIdAndStallId(reservationId, stallId);
            log.info("Deleted reservation_genres entries");

            // Remove the stall from the reservation's stall set
            reservation.getStalls().removeIf(s -> s.getId().equals(stallId));
            log.info("Removed stall from reservation's stall set");

            // Decrease user's booking count
            int newBookingCount = Math.max(0, user.getNoOfCurrentBookings() - 1);
            user.setNoOfCurrentBookings(newBookingCount);
            userRepository.save(user);
            log.info("Updated user booking count to: " + newBookingCount);

            // Case 1: This was the only stall in the reservation
            if (stallCountBeforeDeletion == 1) {
                log.info("Case 1: Deleting reservation (only stall)");
                // Delete the reservation (cascade will handle reservation_stalls)
                reservationRepository.delete(reservation);

                // Send email: reservation cancelled
                try {
                    emailService.sendStallUnreserveNotification(
                        user,
                        stallName,
                        newBookingCount,
                        true  // reservationCancelled = true
                    );
                    log.info("Email sent successfully");
                } catch (Exception e) {
                    // Log but don't fail the transaction
                    System.err.println("Failed to send email: " + e.getMessage());
                }
            }
            // Case 2: User still has other stalls in the reservation
            else {
                log.info("Case 2: Saving updated reservation (multiple stalls)");
                // Save the updated reservation
                reservationRepository.save(reservation);

                // Send email: stall removed but reservation still active
                try {
                    emailService.sendStallUnreserveNotification(
                        user,
                        stallName,
                        newBookingCount,
                        false  // reservationCancelled = false
                    );
                    log.info("Email sent successfully");
                } catch (Exception e) {
                    // Log but don't fail the transaction
                    System.err.println("Failed to send email: " + e.getMessage());
                }
            }
        }

        // Mark the stall as unreserved (available)
        stall.setIsConfirmed(false);
        stallRepository.save(stall);
        log.info("Stall marked as unreserved and saved");
    }
}
