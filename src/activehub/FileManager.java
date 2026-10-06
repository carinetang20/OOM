package activehub;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles simple line-based text file persistence.
 */
public class FileManager {
    private final String dataFolder;

    public FileManager(String dataFolder) {
        this.dataFolder = dataFolder;
        ensureFolder();
    }

    private void ensureFolder() {
        File folder = new File(dataFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    private String path(String fileName) {
        return dataFolder + File.separator + fileName;
    }

    public void saveCatalogue(List<RentalItem> items) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path("catalogue.txt")))) {
            for (RentalItem item : items) {
                bw.write(item.toFileLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving catalogue: " + e.getMessage());
        }
    }

    public List<RentalItem> loadCatalogue() {
        List<RentalItem> items = new ArrayList<>();
        File file = new File(path("catalogue.txt"));
        if (!file.exists()) {
            return items;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    items.add(RentalItem.fromFileLine(line.trim()));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading catalogue: " + e.getMessage());
        }
        return items;
    }

    public void saveFacilities(List<Facility> facilities) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path("facilities.txt")))) {
            for (Facility f : facilities) {
                bw.write(f.getFacilityId() + "|" + f.getFacilityName() + "|" + f.getFacilityType());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving facilities: " + e.getMessage());
        }
    }

    public List<Facility> loadFacilities() {
        List<Facility> list = new ArrayList<>();
        File file = new File(path("facilities.txt"));
        if (!file.exists()) {
            return list;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split("\\|");
                list.add(new Facility(p[0], p[1], p[2]));
            }
        } catch (IOException e) {
            System.out.println("Error loading facilities: " + e.getMessage());
        }
        return list;
    }

    public void saveBookings(List<Booking> bookings) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path("bookings.txt")))) {
            for (Booking b : bookings) {
                bw.write(b.toFileLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }

    public List<Booking> loadBookings(List<Facility> facilities) {
        List<Booking> list = new ArrayList<>();
        File file = new File(path("bookings.txt"));
        if (!file.exists()) {
            return list;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split("\\|");
                Customer customer = new Customer(p[1], p[2]);
                Facility facility = findFacility(facilities, p[3]);
                if (facility == null) {
                    continue;
                }
                Booking booking = new Booking(
                        p[0],
                        customer,
                        facility,
                        LocalDate.parse(p[4], Booking.DATE_FMT),
                        LocalTime.parse(p[5], Booking.TIME_FMT),
                        Integer.parseInt(p[6])
                );
                if ("CANCELLED".equalsIgnoreCase(p[7])) {
                    booking.cancel();
                }
                list.add(booking);
            }
        } catch (Exception e) {
            System.out.println("Error loading bookings: " + e.getMessage());
        }
        return list;
    }

    public void saveTransactions(List<Transaction> transactions) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path("transactions.txt")))) {
            for (Transaction t : transactions) {
                bw.write(t.toFileLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    public List<Transaction> loadTransactions(List<RentalItem> catalogue,
                                              List<Booking> bookings,
                                              PromotionEngine engine) {
        List<Transaction> list = new ArrayList<>();
        File file = new File(path("transactions.txt"));
        if (!file.exists()) {
            return list;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Transaction tx = parseTransaction(line.trim(), catalogue, bookings, engine);
                if (tx != null) {
                    list.add(tx);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
        return list;
    }

    private Transaction parseTransaction(String line, List<RentalItem> catalogue,
                                         List<Booking> bookings, PromotionEngine engine) {
        String[] p = line.split("\\|", -1);
        if (p.length < 12) {
            return null;
        }
        Customer customer = new Customer(p[1], p[2]);
        Booking booking = "NONE".equalsIgnoreCase(p[3]) ? null : findBooking(bookings, p[3]);
        Transaction tx = new Transaction(p[0], customer, booking);

        if (!p[11].isEmpty()) {
            String[] parts = p[11].split(",");
            for (String part : parts) {
                String[] iq = part.split(":");
                if (iq.length != 2) {
                    continue;
                }
                RentalItem item = findItem(catalogue, iq[0]);
                if (item != null) {
                    tx.addItem(item, Integer.parseInt(iq[1]));
                }
            }
        }

        Promotion promo = engine.findByCode(p[5]);
        if (promo != null) {
            tx.setSelectedPromotion(promo, Double.parseDouble(p[6]));
        }

        Payment payment = reconstructPayment(p[9]);
        if (payment != null) {
            tx.setPayment(payment);
        }
        if ("PAID".equalsIgnoreCase(p[10])) {
            tx.markCompleted();
        }
        return tx;
    }

    private Payment reconstructPayment(String method) {
        if (method == null || method.isEmpty() || "NONE".equalsIgnoreCase(method)) {
            return null;
        }
        if (method.toLowerCase().contains("card")) {
            return new CardPayment();
        }
        if (method.toLowerCase().contains("wallet")) {
            return new EWalletPayment();
        }
        if (method.equalsIgnoreCase("Cash")) {
            return new CashPayment();
        }
        return null;
    }

    private Booking findBooking(List<Booking> bookings, String id) {
        for (Booking b : bookings) {
            if (b.getBookingId().equalsIgnoreCase(id)) {
                return b;
            }
        }
        return null;
    }

    private RentalItem findItem(List<RentalItem> catalogue, String code) {
        for (RentalItem item : catalogue) {
            if (item.getItemCode().equalsIgnoreCase(code)) {
                return item;
            }
        }
        return null;
    }

    private Facility findFacility(List<Facility> facilities, String id) {
        for (Facility f : facilities) {
            if (f.getFacilityId().equalsIgnoreCase(id)) {
                return f;
            }
        }
        return null;
    }
}
