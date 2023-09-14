package pl.javastart.streamsexercise;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class PaymentService {

    private final PaymentRepository paymentRepository;
    private final DateTimeProvider dateTimeProvider;

    PaymentService(PaymentRepository paymentRepository, DateTimeProvider dateTimeProvider) {
        this.paymentRepository = paymentRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    private Stream<Payment> getStream() {
        return paymentRepository.findAll().stream();
    }

    /*
    Znajdź i zwróć płatności posortowane po dacie rosnąco
     */

    List<Payment> findPaymentsSortedByDateAsc() {
        return getStream()
                .sorted(Comparator.comparing(Payment::getPaymentDate))
                .toList();
    }

    /*
    Znajdź i zwróć płatności posortowane po dacie malejąco
     */

    List<Payment> findPaymentsSortedByDateDesc() {
        return getStream()
                .sorted(Comparator.comparing(Payment::getPaymentDate).reversed())
                .toList();
    }

    /*
    Znajdź i zwróć płatności posortowane po liczbie elementów rosnąco
     */

    List<Payment> findPaymentsSortedByItemCountAsc() {
        return getStream()
                .sorted(Comparator.comparingInt(PaymentService::getPaymentSize))
                .toList();
    }

    /*
    Znajdź i zwróć płatności posortowane po liczbie elementów malejąco
     */

    List<Payment> findPaymentsSortedByItemCountDesc() {
        return getStream()
                .sorted(Comparator.comparingInt(PaymentService::getPaymentSize).reversed())
                .toList();
    }

    private static int getPaymentSize(Payment payment) {
        return payment.getPaymentItems().size();
    }


    /*
    Znajdź i zwróć płatności dla wskazanego miesiąca
     */
    List<Payment> findPaymentsForGivenMonth(YearMonth yearMonth) {
        int year = yearMonth.getYear();
        Month month = yearMonth.getMonth();
        return getStream()
                .filter(isYearMonthEqual(year, month))
                .toList();
    }

    /*
    Znajdź i zwróć płatności dla aktualnego miesiąca
     */
    List<Payment> findPaymentsForCurrentMonth() {
        int year = dateTimeProvider.zonedDateTimeNow().getYear();
        Month month = dateTimeProvider.zonedDateTimeNow().getMonth();
        return getStream()
                .filter(isYearMonthEqual(year, month))
                .toList();
    }

    private static Predicate<Payment> isYearMonthEqual(int year, Month month) {
        return payment -> year == payment.getPaymentDate().getYear()
                && month.equals(payment.getPaymentDate().getMonth());
    }

    /*
    Znajdź i zwróć płatności dla ostatnich X dni
     */
    List<Payment> findPaymentsForGivenLastDays(int days) {
        ZonedDateTime now = dateTimeProvider.zonedDateTimeNow();
        ZonedDateTime past = now.minusDays(days);
        return getStream()
                .filter(isPaymentDateInRange(now, past))
                .toList();
    }

    private static Predicate<Payment> isPaymentDateInRange(ZonedDateTime now, ZonedDateTime past) {
        return payment -> payment.getPaymentDate().isBefore(now)
                && payment.getPaymentDate().isAfter(past);
    }


    /*
    Znajdź i zwróć płatności z jednym elementem
     */
    Set<Payment> findPaymentsWithOnePaymentItem() {
        return getStream()
                .filter(PaymentService::checkIfOnlyOnePayment)
                .collect(Collectors.toSet());
    }

    private static boolean checkIfOnlyOnePayment(Payment payment) {
        return getPaymentSize(payment) == 1;
    }

    /*
    Znajdź i zwróć nazwy produktów sprzedanych w aktualnym miesiącu
     */

    Set<String> findProductsSoldInCurrentMonth() {
        List<Payment> paymentsForCurrentMonth = findPaymentsForCurrentMonth();
        return paymentsForCurrentMonth.stream()
                .map(Payment::getPaymentItems)
                .flatMap(List::stream)
                .map(PaymentItem::getName)
                .collect(Collectors.toSet());
    }
    /*
    Policz i zwróć sumę sprzedaży dla wskazanego miesiąca
     */

    BigDecimal sumTotalForGivenMonth(YearMonth yearMonth) {
        return findPaymentsForGivenMonth(yearMonth).stream()
                .map(Payment::countTotalSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /*
    Policz i zwróć sumę przyznanych rabatów dla wskazanego miesiąca
     */

    BigDecimal sumDiscountForGivenMonth(YearMonth yearMonth) {
        return findPaymentsForGivenMonth(yearMonth).stream()
                .map(Payment::countDiscountSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /*
    Znajdź i zwróć płatności dla użytkownika z podanym mailem
     */
    List<PaymentItem> getPaymentsForUserWithEmail(String userEmail) {
        return getStream()
                .filter(isEmailEqual(userEmail))
                .map(Payment::getPaymentItems)
                .flatMap(List::stream)
                .toList();
    }

    private static Predicate<Payment> isEmailEqual(String userEmail) {
        return payment -> payment.getUser().getEmail().equals(userEmail);
    }

    /*
    Znajdź i zwróć płatności, których wartość przekracza wskazaną granicę
     */
    Set<Payment> findPaymentsWithValueOver(int value) {
        return getStream()
                .filter(isSumBiggerThanValue(value))
                .collect(Collectors.toSet());
    }

    private static Predicate<Payment> isSumBiggerThanValue(int value) {
        return payment -> {
            BigDecimal valueBig = new BigDecimal(value);
            BigDecimal sum = payment.countTotalSum();
            return sum.compareTo(valueBig) > 0;
        };
    }

}
