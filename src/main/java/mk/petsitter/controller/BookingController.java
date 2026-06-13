package mk.petsitter.controller;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import mk.petsitter.model.Booking;
import mk.petsitter.model.PetOwner;
import mk.petsitter.model.PetSitter;
import mk.petsitter.model.User;
import mk.petsitter.service.BookingService;
import mk.petsitter.service.PetService;
import mk.petsitter.service.ReviewService;
import mk.petsitter.service.PaymentService;
import mk.petsitter.repository.PetSitterRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookingController {

    private final BookingService bookingService;
    private final PetSitterRepository petSitterRepository;
    private final PetService petService;
    private final ReviewService reviewService;
    private final PaymentService paymentService;

    public BookingController(BookingService bookingService, PetSitterRepository petSitterRepository, PetService petService, ReviewService reviewService, PaymentService paymentService) {
        this.bookingService = bookingService;
        this.petSitterRepository = petSitterRepository;
        this.petService = petService;
        this.reviewService = reviewService;
        this.paymentService = paymentService;
    }

    @GetMapping("/book")
    public String showBookingForm(@RequestParam String sitterId, @RequestParam(required = false) String serviceType, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetOwner)) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("pets", petService.getPetsByOwner(user.getUserId()));
        model.addAttribute("serviceType", serviceType);
        petSitterRepository.findById(sitterId).ifPresent(sitter -> model.addAttribute("sitter", sitter));
        return "book-sitter";
    }

    @PostMapping("/book")
    public String bookSitter(
            @RequestParam String sitterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam String address,
            @RequestParam(required = false) List<String> petIds,
            @RequestParam Integer amount,
            @RequestParam String paymentType,
            @RequestParam(required = false) String serviceType,
            HttpSession session) {
            
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetOwner)) {
            return "redirect:/dashboard";
        }
        if (petIds == null || petIds.isEmpty()) {
            return "redirect:/book?sitterId=" + sitterId + "&error=NoPets";
        }
        
        Booking booking = bookingService.createBooking(user.getUserId(), sitterId, dateFrom, dateTo, address, petIds, serviceType);
        paymentService.processPayment(booking.getBookingId(), amount, paymentType);
        return "redirect:/my-bookings";
    }

    @PostMapping("/my-bookings/cancel")
    public String cancelBooking(@RequestParam String bookingId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetOwner)) {
            return "redirect:/dashboard";
        }
        bookingService.updateBookingStatus(bookingId, "Canceled");
        return "redirect:/my-bookings";
    }

    @GetMapping("/requests")
    public String showSitterRequests(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetSitter)) {
            return "redirect:/dashboard";
        }
        
        List<Booking> requests = bookingService.getBookingsForSitter(user.getUserId());
        requests.sort(java.util.Comparator.comparing(Booking::getDateFrom));
        
        List<Booking> activeRequests = new java.util.ArrayList<>();
        List<Booking> inactiveRequests = new java.util.ArrayList<>();
        for (Booking b : requests) {
            if ("Pending".equals(b.getStatus()) || "Confirmed".equals(b.getStatus())) {
                activeRequests.add(b);
            } else {
                inactiveRequests.add(b);
            }
        }
        model.addAttribute("activeRequests", activeRequests);
        model.addAttribute("inactiveRequests", inactiveRequests);
        return "requests";
    }

    @PostMapping("/requests/update")
    public String updateRequestStatus(
            @RequestParam String bookingId,
            @RequestParam String status,
            HttpSession session) {
            
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetSitter)) {
            return "redirect:/dashboard";
        }
        
        bookingService.updateBookingStatus(bookingId, status);
        return "redirect:/requests";
    }

    @GetMapping("/my-bookings")
    public String showOwnerBookings(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetOwner)) {
            return "redirect:/dashboard";
        }
        
        // fix n+1 - fetch all bookings with details at once and filter them in mem
        List<Booking> bookings = bookingService.getAllBookings().stream()
                .filter(b -> b.getOwner().getUserId().equals(user.getUserId()))
                .sorted(java.util.Comparator.comparing(Booking::getDateFrom))
                .collect(java.util.stream.Collectors.toList());
        
        List<Booking> activeBookings = new java.util.ArrayList<>();
        List<Booking> inactiveBookings = new java.util.ArrayList<>();
        for (Booking b : bookings) {
            if ("Pending".equals(b.getStatus()) || "Confirmed".equals(b.getStatus())) {
                activeBookings.add(b);
            } else {
                inactiveBookings.add(b);
            }
        }
        model.addAttribute("activeBookings", activeBookings);
        model.addAttribute("inactiveBookings", inactiveBookings);
        return "my-bookings";
    }

    @GetMapping("/review")
    public String showReviewForm(@RequestParam String bookingId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetOwner)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("bookingId", bookingId);
        return "review-form";
    }

    @PostMapping("/review")
    public String submitReview(
            @RequestParam String bookingId,
            @RequestParam Integer rating,
            @RequestParam String comment,
            HttpSession session) {
            
        User user = (User) session.getAttribute("loggedInUser");
        if (!(user instanceof PetOwner)) {
            return "redirect:/dashboard";
        }
        reviewService.addReview(bookingId, rating, comment);
        return "redirect:/my-bookings";
    }
}